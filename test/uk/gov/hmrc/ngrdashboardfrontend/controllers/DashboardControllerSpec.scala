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

import helpers.{ControllerSpecSupport, TestData}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.ngrdashboardfrontend.connector.{NGRNotifyConnector}
import uk.gov.hmrc.ngrdashboardfrontend.models.Status.{Approved, Pending, Rejected}
import uk.gov.hmrc.ngrdashboardfrontend.models.notify.RatepayerStatusResponse
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.views.html.DashboardView

import scala.concurrent.Future

class DashboardControllerSpec extends ControllerSpecSupport with TestData {

  lazy val dashboardRoute: Call = routes.DashboardController.show
  lazy val dashboardView: DashboardView = inject[DashboardView]
  lazy val mockNotifyConnector: NGRNotifyConnector = mock[NGRNotifyConnector]

  def controller() = new DashboardController(
    dashboardView,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockNGRConnector,
    mcc
  )(ec, appConfig = mockConfig)

  val pageTitle = "Manage your business rates valuation"
  val tellUsAboutChangeCardHeading = "Tell us about changes to your property, rent or agreement"

  "Dashboard Controller" must {
    "method show" must {
      "Return OK and the correct view with the card 'Tell us about a change' when the property is linked and approved" in {
        when(mockNGRConnector.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(vmvPropertyStatus(Approved))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include(tellUsAboutChangeCardHeading)
      }

      "Return OK and the correct view with the card 'Tell us about a change' when the property is linked and pending" in {
        when(mockNGRConnector.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(vmvPropertyStatus(Pending))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
        content mustNot include(tellUsAboutChangeCardHeading)
        content must include("Your property")
      }

      "Return OK and the correct view with the card 'Tell us about a change' when the property is linked and rejected" in {
        when(mockNGRConnector.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(vmvPropertyStatus(Rejected))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
        content mustNot include(tellUsAboutChangeCardHeading)
        content must include("Your property")
      }

      "Return OK and the correct view without the card 'Tell us about a change' when the property is not linked" in {
        when(mockNGRConnector.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(None))
        when(mockNotifyConnector.getRatepayerStatus(any[CredId])(any())).thenReturn(Future.successful(None))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
        content must not include tellUsAboutChangeCardHeading
      }
    }
  }
}
