package controllers

import javax.inject._
import models._
import models.forms.ServiceNameForm
import play.api.libs.json.Json
import play.api.mvc._
import services.SsgService
import models.forms.ServiceNameForm._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SsgController @Inject()(ssgService: SsgService, cc: ControllerComponents)(
  implicit ec:                            ExecutionContext
) extends AbstractController(cc) {

//  val nodeA = GraphNode(data = GraphNodeData("service-a"))
//  val nodeB = GraphNode(data = GraphNodeData("service-b"))
//  val nodeC = GraphNode(data = GraphNodeData("service-c"))
//
//  val edgeA = GraphEdge(data = GraphEdgeData("edge-a", "service-a", "service-b"))
//  val edgeB = GraphEdge(data = GraphEdgeData("edge-b", "service-a", "service-c"))
//  val edgeC = GraphEdge(data = GraphEdgeData("edge-c", "service-b", "service-c"))
//
//  val graphElements = GraphElements(
//    nodes = List(nodeA, nodeB, nodeC),
//    edges = List(edgeA, edgeB, edgeC)
//  )

  def show(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(
      views.html.simple_service_graph(serviceNameForm, None)
    )
  }

  def submit(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    serviceNameForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest("Bad request")), { serviceName =>
        ssgService.generateServiceGraph(serviceName).map { graph =>
          Ok(
            views.html.simple_service_graph(
              ServiceNameForm.serviceNameForm,
              Some(Json.toJson(graph).toString())
            ))
        }
      }
    )
  }

}
