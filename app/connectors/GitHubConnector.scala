package connectors
import config.AppConfig
import javax.inject.Inject
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class GitHubConnector @Inject()(ws: WSClient, appConfig: AppConfig)(implicit ec: ExecutionContext) {

  def getConfigString(serviceName: String): Future[Option[String]] =
    ws.url(s"https://raw.githubusercontent.com/${appConfig.repoOrgOrUser}/$serviceName/${appConfig.serviceConfigFile}")
      .addHttpHeaders("Authorization" -> s"token ${appConfig.gitHubAccessToken}")
      .get()
      .map(response =>
        response.status match {
          case 200 =>
            Some(response.body)
          case _ => None
      })

}
