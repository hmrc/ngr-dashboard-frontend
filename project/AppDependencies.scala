import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.5.0"
  private val enumeratumVersion = "1.9.1"
  

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"                        % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"                        % "12.25.0",
    "uk.gov.hmrc"             %% "centralised-authorisation-resource-client-play-30" % "1.15.0",
    "uk.gov.hmrc"             %% "domain-play-30"                                    % "11.0.0",
    "com.beachape"            %% "enumeratum-play"                                   %  enumeratumVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion    % Test,
    "org.jsoup"               %  "jsoup"                      % "1.22.1"            % Test,
  )

  val it: Seq[Nothing] = Seq.empty
}
