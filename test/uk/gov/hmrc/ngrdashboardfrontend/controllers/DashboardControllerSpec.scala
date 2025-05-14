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
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.ngrdashboardfrontend.views.html.DashboardView

class DashboardControllerSpec extends ControllerSpecSupport {

  lazy val dashboardRoute: Call = routes.DashboardController.show
  lazy val dashboardView: DashboardView = inject[DashboardView]

  def controller() = new DashboardController(
    dashboardView,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc
  )(appConfig = mockConfig)

  val pageTitle = "Manage your business rates valuation"

  "Dashboard Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
