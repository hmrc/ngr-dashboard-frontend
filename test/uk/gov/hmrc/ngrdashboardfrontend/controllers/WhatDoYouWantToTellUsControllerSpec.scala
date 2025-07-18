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
import play.api.http.Status.OK
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.views.html.WhatDoYouWantToTellUsView

class WhatDoYouWantToTellUsControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  val pageTitle = "What do you want to tell us?"
  lazy val frontendAppConfig: AppConfig = inject[AppConfig]
  lazy val whatDoYouWantToTellUsView: WhatDoYouWantToTellUsView = inject[WhatDoYouWantToTellUsView]

  def controller() = new WhatDoYouWantToTellUsController(
    mockAuthJourney,
    mockIsRegisteredCheck,
    whatDoYouWantToTellUsView,
    mcc
  )(appConfig = mockConfig)

  "WhatDoYouWantToTellUsController" must {
    "render 'what do you want to tell us' page" in {
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
      contentAsString(result) must include(pageTitle)
    }
  }
}
