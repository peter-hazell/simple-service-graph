package controllers

import javax.inject._
import models.forms.ServiceNameForm
import models.forms.ServiceNameForm._
import play.api.libs.json.Json
import play.api.mvc._
import services.SsgService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SsgController @Inject()(ssgService: SsgService, mcc: MessagesControllerComponents)(
  implicit ec:                            ExecutionContext
) extends MessagesAbstractController(mcc) {

  def show(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(
      views.html.simple_service_graph(serviceNameForm, None, None)
    )
  }

  def submit(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    serviceNameForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.simple_service_graph(formWithErrors, None, None))), { serviceName =>
        ssgService.generateServiceGraph(serviceName).map { graph =>
          Ok(
            views.html.simple_service_graph(
              ServiceNameForm.serviceNameForm,
              Some(Json.toJson(graph).toString()),
              Some(serviceName)
            ))
        }
      }
    )
  }

}
