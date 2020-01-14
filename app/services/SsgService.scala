package services
import com.typesafe.config.ConfigFactory
import connectors.GitHubConnector
import javax.inject.Inject
import models._

import scala.collection.JavaConverters._
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

  def generateServiceGraph(serviceName: String): Future[GraphElements] = {
    def updateGraph(services: List[String], graphElements: Future[GraphElements]): Future[GraphElements] =
      services.foldLeft(graphElements) { (g, service) =>
        g.flatMap { g =>
          if (g.nodes.exists(_.data.id == service)) {
            Future.successful(g)
          } else {
            getValidServices(service).flatMap { validServices =>
              val graph = validServices.foldLeft(Future.successful(g.addNode(GraphNode(data = GraphNodeData(service))))) { (g, s) =>
                g.map { g =>
                  if (!g.nodes.exists(_.data.id == service)) {
                    g.addNode(GraphNode(data = GraphNodeData(id = service)))
                      .addEdge(GraphEdge(data = GraphEdgeData(id = s"$service->$s", source = service, target = s)))
                  } else {
                    g.addEdge(GraphEdge(data = GraphEdgeData(id = s"$service->$s", source = service, target = s)))
                  }
                }
              }

              updateGraph(validServices, graph)
            }
          }
        }
      }

    updateGraph(List(serviceName), Future.successful(GraphElements.empty))
  }

}
