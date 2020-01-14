package connectors
import javax.inject.Inject
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class GitHubConnector @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {

  def getConfigString(serviceName: String): Future[Option[String]] =
    ws.url(s"https://raw.githubusercontent.com/hmrc/$serviceName/master/conf/application.conf")
      .addHttpHeaders("Authorization" -> "token dd747155630cf7a14d4358b52a715ac99db10abb")
      .get()
      .map(response =>
        response.status match {
          case 200 =>
            Some(response.body)
          case _ => None
      })

}
