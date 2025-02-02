package services
import com.typesafe.config.ConfigFactory
import config.AppConfig
import connectors.GitHubConnector
import javax.inject.Inject
import models._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class SsgService @Inject()(gitHubConnector: GitHubConnector, appConfig: AppConfig)(implicit ec: ExecutionContext) {

  private def getConfigString(serviceName: String): Future[Option[String]] = gitHubConnector.getConfigString(serviceName)

  private def getServicesNamesFromConf(serviceConf: String): List[String] =
    ConfigFactory
      .parseString(serviceConf)
      .entrySet()
      .asScala
      .toList
      .filter(_.getKey.contains("microservice.services."))
      .map(_.getKey.split('.')(2))
      .distinct

  private def getValidServices(serviceName: String): Future[List[String]] =
    getConfigString(serviceName).flatMap {
      case Some(s) =>
        Future
          .traverse(getServicesNamesFromConf(s))(maybeService =>
            getConfigString(maybeService) map {
              case Some(_) => Some(maybeService)
              case None    => None
          })
          .map(_.collect {
            case Some(realService) => realService
          })

      case None => Future.successful(List.empty[String])
    }

  def repoUrl(serviceName: String): String =
    s"https://github.com/${appConfig.repoOrgOrUser}/$serviceName"

  def generateServiceGraph(serviceName: String): Future[GraphElements] = {
    def updateGraph(services: List[String], graphElements: Future[GraphElements]): Future[GraphElements] =
      services.foldLeft(graphElements) { (g, sourceService) =>
        g.flatMap { g =>
          if (g.nodes.exists(_.data.id == sourceService)) {
            Future.successful(g)
          } else {
            getValidServices(sourceService).flatMap { targetServices =>
              val graph = targetServices.foldLeft(Future.successful(g.addNode(sourceService, repoUrl(sourceService)))) { (g, targetService) =>
                g.map { g =>
                  if (!g.nodes.exists(_.data.id == sourceService)) {
                    g.addNode(sourceService, repoUrl(sourceService))
                      .addEdge(sourceService, targetService)
                  } else {
                    g.addEdge(sourceService, targetService)
                  }
                }
              }

              updateGraph(targetServices, graph)
            }
          }
        }
      }

    updateGraph(List(serviceName), Future.successful(GraphElements.empty))
  }

}
