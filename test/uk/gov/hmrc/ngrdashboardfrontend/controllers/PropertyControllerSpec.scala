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
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrdashboardfrontend.models.Status.{Approved, Pending, Rejected}
import uk.gov.hmrc.ngrdashboardfrontend.models.propertyLinking.VMVPropertyStatus
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.views.html.SelectYourPropertyView

import scala.concurrent.Future

class PropertyControllerSpec extends ControllerSpecSupport with TestData {

  val selectYourPropertyView: SelectYourPropertyView = inject[SelectYourPropertyView]

  def controller() = new PropertyController(
    selectYourPropertyView,
    mockAuthJourney,
    mockHasLinkedProperties,
    mockNGRService,
    mcc
  )(ec, mockConfig)

  "select your property controller" must {
    "method show" must {
      "Return OK and the correct view when status is approved" in {
        mockLinkedPropertiesRequest()
        when(mockNGRService.linkedPropertyStatus(any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Approved, property))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content must include("Active")
      }
      "Return OK and the correct view when status is pending" in {
        mockLinkedPropertiesRequest()
        when(mockNGRService.linkedPropertyStatus(any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Pending, property))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content must include("Pending approval")
      }
      "Return OK and the correct view when status is rejected" in {
        mockLinkedPropertiesRequest()
        when(mockNGRService.linkedPropertyStatus(any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Rejected, property))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content must include("Rejected")
      }
      "Throw exception when no property linking is found" in {
        mockLinkedPropertiesRequest()
        when(mockNGRService.linkedPropertyStatus(any[Nino])(any())).thenReturn(Future.successful(None))
        val exception = intercept[NotFoundException] {
          await(controller().show()(authenticatedFakeRequest))
        }
        exception.getMessage contains "Unable to find match Linked Properties" mustBe true
      }
    }
  }
}
