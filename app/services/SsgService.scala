package services
import com.typesafe.config.ConfigFactory
import connectors.GitHubConnector
import javax.inject.Inject
import models._

import collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class SsgService @Inject()(gitHubConnector: GitHubConnector)(implicit ec: ExecutionContext) {

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
        val c: List[String] = getServicesNamesFromConf(s)

        val realServices = Future
          .traverse(c)(maybeService =>
            getConfigString(maybeService) map {
              case Some(_) => Some(maybeService)
              case None    => None
          })
          .map(_.collect {
            case Some(realService) => realService
          })
          .map { l =>
            println(s"SERVICES USED BY $serviceName: $l")
            l
          }

        realServices
      case None => Future.successful(List.empty[String])
    }

  def generateServiceGraph(serviceName: String): Future[GraphElements] = {

    def updateGraph(services: List[String], graphElements: Future[GraphElements]): Future[GraphElements] =
      services.foldLeft(graphElements) { (g, s) =>
        graphElements.flatMap { g =>
          if (!g.nodes.exists(_.data.id == s)) {
            getValidServices(s).flatMap { validServices =>
              val graph = validServices.foldLeft(graphElements) { (g, s) =>
                g.map { g =>
                  if (!g.nodes.exists(_.data.id == s)) {
                    g.addNode(GraphNode(data = GraphNodeData(id = s)))
                      .addEdge(GraphEdge(data = GraphEdgeData(id = s"$serviceName-$s", source = serviceName, target = s)))
                  } else {
                    g.addEdge(GraphEdge(data = GraphEdgeData(id = s"$serviceName-$s", source = serviceName, target = s)))
                  }
                }
              }

              updateGraph(validServices, graph)
            }
          } else {
            Future.successful(g)
          }
        }

      }

    updateGraph(List(serviceName), Future.successful(GraphElements.empty))

  }

}
