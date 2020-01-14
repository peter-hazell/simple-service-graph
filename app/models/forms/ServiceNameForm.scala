package models.forms

import play.api.data.Form
import play.api.data.Forms._

object ServiceNameForm {

  val serviceNameForm = Form(single("serviceName" -> text))

}
