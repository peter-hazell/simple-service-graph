package config

import com.typesafe.config.ConfigFactory
import javax.inject.Inject
import play.api.Configuration

class AppConfig @Inject()(config: Configuration) {

  val gitHubAccessToken: String = ConfigFactory.load("local.conf").getString("gitHubAccessToken")
  val repoOrgOrUser:     String = ConfigFactory.load("local.conf").getString("repoOrgOrUser")
  val serviceConfigFile: String = ConfigFactory.load("local.conf").getString("serviceConfigFile")

}
