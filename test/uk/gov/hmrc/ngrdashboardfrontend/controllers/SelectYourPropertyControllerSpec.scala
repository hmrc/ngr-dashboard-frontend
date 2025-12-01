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
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.Helpers.{await, contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrdashboardfrontend.actions.CredIdValidator
import uk.gov.hmrc.ngrdashboardfrontend.models.Status._
import uk.gov.hmrc.ngrdashboardfrontend.models.propertyLinking.{VMVProperty, VMVPropertyStatus}
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.views.html.SelectYourPropertyView

import java.time.LocalDate
import scala.concurrent.Future

class SelectYourPropertyControllerSpec extends ControllerSpecSupport with TestData {

  val selectYourPropertyView: SelectYourPropertyView = inject[SelectYourPropertyView]
  val credIdValidator: CredIdValidator = inject[CredIdValidator]

  def controller() = new SelectYourPropertyController(
    selectYourPropertyView,
    mockAuthJourney,
    mockHasLinkedProperties,
    mockNGRService,
    credIdValidator,
    mcc
  )(ec, mockConfig)

  "select your property controller" must {
    "method show" must {
      "Return OK and the correct view when status is approved" in {
        mockLinkedPropertiesRequest(hasCredId = true)
        when(mockNGRService.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Approved, property))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content must include("what-do-you-want-to-tell-us/85141561000")
        content must include("Active")
      }

      "Return OK and the correct view when status is pending" in {
        mockLinkedPropertiesRequest(hasCredId = true)
        when(mockNGRService.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Pending, property))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content must include("what-do-you-want-to-tell-us/85141561000")
        content must include("Pending approval")
      }

      "Return OK and the correct view when status is rejected" in {
        mockLinkedPropertiesRequest(hasCredId = true)
        when(mockNGRService.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Rejected, property))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content must include("what-do-you-want-to-tell-us/85141561000")
        content must include("Rejected")
      }

      "Throw exception when no property linking is found" in {
        mockLinkedPropertiesRequest(hasCredId = true)
        when(mockNGRService.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(None))
        val exception = intercept[NotFoundException] {
          await(controller().show()(authenticatedFakeRequest))
        }
        exception.getMessage contains "Unable to find match Linked Properties" mustBe true
      }

      "assessment id is picked from the correct valuation" in {
        val propertyWithMultipleValuations: VMVProperty = property.copy(valuations = List(
          property.valuations.head,
          property.valuations.head.copy(effectiveDate = LocalDate.parse("2022-01-01"), assessmentRef = 9999999999L),
          property.valuations.head.copy(effectiveDate = LocalDate.parse("2023-01-01"), assessmentRef = 99999988899L),
          property.valuations.head.copy(effectiveDate = LocalDate.parse("2025-01-01"), assessmentRef = 9999777999L)
        ))
        mockLinkedPropertiesRequest(hasCredId = true)
        when(mockNGRService.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Approved, propertyWithMultipleValuations))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("what-do-you-want-to-tell-us/9999777999")
      }

      "assessment id is picked from the correct valuation when only one valuation exists" in {
        val propertyWithSingleValuation: VMVProperty = property.copy(valuations = List(
          property.valuations.head.copy(effectiveDate = LocalDate.parse("2022-01-01"), assessmentRef = 8888888888L)
        ))
        mockLinkedPropertiesRequest(hasCredId = true)
        when(mockNGRService.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Approved, propertyWithSingleValuation))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content: Document = Jsoup.parse(contentAsString(result))
        content.getElementsByTag("td").last().html() must include("what-do-you-want-to-tell-us/8888888888")
      }

      "assessment id is picked from the correct valuation when no CURRENT valuation exists" in {
        val propertyWithNoCurrentValuation: VMVProperty = property.copy(valuations = List(
          property.valuations.head.copy(assessmentStatus = "PREVIOUS", effectiveDate = LocalDate.parse("2021-04-01"), assessmentRef = 6666666666L),
          property.valuations.head.copy(assessmentStatus = "PREVIOUS", effectiveDate = LocalDate.parse("2020-04-01"), assessmentRef = 5555555555L)
        ))
        mockLinkedPropertiesRequest(hasCredId = true)
        when(mockNGRService.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Approved, propertyWithNoCurrentValuation))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content: Document = Jsoup.parse(contentAsString(result))
        content.getElementsByTag("td").last().`val`() mustBe ""
      }

      "Return a bad request when credId is missing" in {
        mockLinkedPropertiesRequest()
        when(mockNGRService.linkedPropertyStatus(any[CredId], any[Nino])(any())).thenReturn(Future.successful(Some(VMVPropertyStatus(Approved, property))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content mustNot include("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content mustNot include("what-do-you-want-to-tell-us/85141561000")
        content mustNot include("Active")
        content must include("Missing credId in request")
      }
    }
  }
}
