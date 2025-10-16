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
import play.api.http.Status.OK
import play.api.test.Helpers.{await, contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.views.html.SelectYourPropertyView

import scala.concurrent.Future

class SelectYourPropertyControllerSpec extends ControllerSpecSupport with TestData {

  val selectYourPropertyView: SelectYourPropertyView = inject[SelectYourPropertyView]

  def controller() = new SelectYourPropertyController(
    selectYourPropertyView,
    mockAuthJourney,
    mockHasLinkedProperties,
    mockNGRConnector,
    mcc
  )(ec, mockConfig)

  "select your property controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        mockLinkedPropertiesRequest()
        when(mockNGRConnector.getLinkedProperty(any[CredId])(any())).thenReturn(Future.successful(Some(property)))
        val result = controller().showReportChange(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content must include("what-do-you-want-to-tell-us/2191322564521")
        content must include("Golf")
      }

      "Throw exception when no property linking is found" in {
        mockLinkedPropertiesRequest()
        when(mockNGRConnector.getLinkedProperty(any[CredId])(any())).thenReturn(Future.successful(None))
        val exception = intercept[NotFoundException] {
          await(controller().showReportChange(authenticatedFakeRequest))
        }
        exception.getMessage contains "Unable to find match Linked Properties" mustBe true
      }
    }
  }
}
