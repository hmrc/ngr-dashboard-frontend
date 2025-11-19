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

package mocks

import play.api.Configuration
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.config.features.Features

class MockAppConfig(val runModeConfiguration: Configuration) extends AppConfig{
  override val features: Features = new Features()(runModeConfiguration)
  override val nextGenerationRatesUrl: String = "https://localhost:1500"
  override def getString(key: String): String = "???"
  override val ngrRaldUrl: String = "https://localhost:1505"
  override val ngrPhysicalUrl: String = "https://localhost:1506"
  override val addAPropertyUrl: String = "http://localhost:1504/ngr-property-linking-frontend/add-a-property"
  override val reviewPropertyUrl: String = "http://localhost:1504/ngr-property-linking-frontend/review-your-property-details"
  override val logoutUrl: String = "http://localhost:9553/bas-gateway/sign-out-without-state?continue=http://localhost:1503/ngr-dashboard-frontend/beforeYouGo"
  override val registrationUrl: String = "https://localhost:1502"
  override val ngrStubHost: String = "http://localhost:1501"
  override val feedbackFrontendUrl: String = "http://localhost:9514/feedback/NGR-Dashboard"

  override val notifyNGRUrl:String = "http://localhost:1515"
}