import play.sbt.PlayImport.*
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.11.0"
  private val enumeratumVersion = "1.8.2"
  

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"                        % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"                        % "12.0.0",
    "uk.gov.hmrc"             %% "centralised-authorisation-resource-client-play-30" % "1.5.0",
    "uk.gov.hmrc"             %% "domain-play-30"                                    % "11.0.0",
    "com.beachape"            %% "enumeratum-play"                                   %  enumeratumVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion    % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"            % Test,
  )

  val it = Seq.empty
}
