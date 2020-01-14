lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "simple-service-graph",
    organization := "com.petehazell",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.12.6",
    PlayKeys.playDefaultPort := 9003,
    libraryDependencies ++= Seq(
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
      "org.mockito"            % "mockito-all"         % "1.10.19",
      "org.webjars"            % "bootstrap"           % "4.4.1",
      "org.webjars"            % "jquery"              % "3.4.1",
      "org.scala-graph"        % "graph-core_2.12"     % "1.13.1",
      guice,
      ws
    )
  )
