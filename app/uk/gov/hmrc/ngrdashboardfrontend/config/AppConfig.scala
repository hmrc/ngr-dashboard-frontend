/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrdashboardfrontend.config

import play.api.Configuration
import uk.gov.hmrc.ngrdashboardfrontend.config.features.Features
import uk.gov.hmrc.ngrdashboardfrontend.controllers.routes
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

trait AppConfig {
  val features: Features
  val logoutUrl:String
  val registrationUrl: String
  val feedbackFrontendUrl:String
  val nextGenerationRatesUrl: String
  def getString(key: String): String
}

@Singleton
class FrontendAppConfig @Inject()(config: Configuration, sc: ServicesConfig) extends AppConfig{
  override val registrationUrl: String = sc.baseUrl("ngr-login-register-frontend")
  override val features = new Features()(config)
  override val nextGenerationRatesUrl: String = sc.baseUrl("next-generation-rates")
  def getString(key: String): String =
    config.getOptional[String](key)
      .getOrElse(throwConfigNotFoundError(key))
  private def throwConfigNotFoundError(key: String): String =
    throw new RuntimeException(s"Could not find config key '$key'")

  private def getOptionString(key: String) = config.getOptional[String](key).filter(!_.isBlank).getOrElse(throw new Exception(s"Missing key: $key"))

  lazy val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(true)

  private lazy val feedbackFrontendHost = getOptionString("microservice.services.feedback-survey-frontend.host")
  private lazy val dashboardHost = getOptionString("dashboard.host")
  private lazy val basGatewayHost = getOptionString("microservice.services.bas-gateway-frontend.host")

  private lazy val dashboardBeforeYouGoUrl = s"$dashboardHost${routes.BeforeYouGoController.show.url}"
  lazy val feedbackFrontendUrl = s"$feedbackFrontendHost/feedback/NGR-Dashboard"
  lazy val logoutUrl: String = s"$basGatewayHost/bas-gateway/sign-out-without-state?continue=$dashboardBeforeYouGoUrl"
}
