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
import uk.gov.hmrc.ngrdashboardfrontend.controllers.routes

import javax.inject.{Inject, Singleton}


@Singleton
class AppConfig @Inject()(config: Configuration) {
  lazy val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  private lazy val feedbackFrontendHost = config.getOptional[String]("microservice.services.feedback-survey-frontend.host")
    .getOrElse(throw new Exception(s"Missing key: microservice.services.feedback-survey-frontend.host"))

  private lazy val dashboardHost = config.getOptional[String]("dashboard.host")
    .getOrElse(throw new Exception(s"Missing key: dashboard.host"))

  private lazy val basGatewayHost = config.getOptional[String]("microservice.services.bas-gateway-frontend.host")
    .getOrElse(throw new Exception(s"Missing key: microservice.services.bas-gateway-frontend.host"))

  private lazy val dashboardBeforeYouGoUrl = s"$dashboardHost${routes.BeforeYouGoController.show.url}"
  lazy val feedbackFrontendUrl = s"$feedbackFrontendHost/feedback/NGR-Dashboard"
  lazy val logoutUrl: String = s"$basGatewayHost/bas-gateway/sign-out-without-state?continue=$dashboardBeforeYouGoUrl"
}
