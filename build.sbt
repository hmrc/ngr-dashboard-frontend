import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.16"

val strictBuilding: SettingKey[Boolean] = StrictBuilding.strictBuilding
StrictBuilding.strictBuildingSetting

lazy val microservice = Project("ngr-dashboard-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= {
      if (StrictBuilding.strictBuilding.value) ScalaCompilerFlags.strictScalaCompilerOptions else Nil
    },
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    pipelineStages := Seq(gzip),
    Compile / scalacOptions -= "utf8",
    PlayKeys.playDefaultPort := 1503,
  )
  .settings(CodeCoverageSettings.settings *)
  .settings(WartRemoverSettings.wartRemoverSettings *)
  .disablePlugins(JUnitXmlReportPlugin)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.it)

onLoadMessage := {
    val port = PlayKeys.playDefaultPort.value
    s"🚀 Service is running! Click here: http://localhost:$port/ngr-dashboard-frontend/dashboard"
}