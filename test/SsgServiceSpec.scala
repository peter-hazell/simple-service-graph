import config.AppConfig
import connectors.GitHubConnector
import models._
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.ws.WSClient
import services.SsgService

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._
import scala.io.Source

class SsgServiceSpec extends WordSpec with Matchers with MockitoSugar {

  implicit val defaultTimeout: FiniteDuration = 5 seconds

  def await[A](future: Future[A])(implicit timeout: Duration): A = Await.result(future, timeout)

  val mockAppConfig: AppConfig = mock[AppConfig]
  val mockWsClient:  WSClient  = mock[WSClient]

  val fakeGitHubConnector: GitHubConnector = {
    new GitHubConnector(mockWsClient, mockAppConfig) {
      override def getConfigString(serviceName: String): Future[Option[String]] =
        Future.successful(Some(Source.fromResource(s"resources/$serviceName.conf").getLines.mkString("\n")))
    }
  }

  val service = new SsgService(fakeGitHubConnector)

  "generateServiceGraph" should {
    "generate the correct graph based on a given service" in {
      val expectedGraph = GraphElements(
        nodes = List(
          GraphNode(GraphNodeData(id = "service-a", href = "https://github.com/hmrc/service-a")),
          GraphNode(GraphNodeData(id = "service-d", href = "https://github.com/hmrc/service-d")),
          GraphNode(GraphNodeData(id = "service-b", href = "https://github.com/hmrc/service-b")),
          GraphNode(GraphNodeData(id = "service-c", href = "https://github.com/hmrc/service-c"))
        ),
        edges = List(
          GraphEdge(GraphEdgeData(id = "service-a~service-d", source = "service-a", target = "service-d")),
          GraphEdge(GraphEdgeData(id = "service-a~service-b", source = "service-a", target = "service-b")),
          GraphEdge(GraphEdgeData(id = "service-a~service-c", source = "service-a", target = "service-c")),
          GraphEdge(GraphEdgeData(id = "service-d~service-a", source = "service-d", target = "service-a")),
          GraphEdge(GraphEdgeData(id = "service-d~service-b", source = "service-d", target = "service-b")),
          GraphEdge(GraphEdgeData(id = "service-b~service-c", source = "service-b", target = "service-c")),
          GraphEdge(GraphEdgeData(id = "service-c~service-a", source = "service-c", target = "service-a")),
          GraphEdge(GraphEdgeData(id = "service-c~service-b", source = "service-c", target = "service-b"))
        )
      )

      val result = service.generateServiceGraph("service-a")

      await(result) shouldBe expectedGraph
    }
  }

}
