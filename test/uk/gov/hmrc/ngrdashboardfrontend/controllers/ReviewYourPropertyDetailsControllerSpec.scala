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

package uk.gov.hmrc.ngrdashboardfrontend.controllers

import helpers.ControllerSpecSupport
import play.api.http.Status.SEE_OTHER
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{redirectLocation, status}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig

class ReviewYourPropertyDetailsControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  val pageTitle = "Add a property to your account"
 lazy val frontendAppConfig: AppConfig = inject[AppConfig]

  def controller() = new ReviewYourPropertyDetailsController(
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc
  )(appConfig = mockConfig)

  "ReviewYourPropertyDetailsController" must {
    "method show" must {
      "Return SEE OTHER and redirect to property linking add a property page" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("http://localhost:1504/ngr-property-linking-frontend/review-your-property-details")
      }
    }
  }
}
