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

package connector

import helpers.TestData
import mocks.MockHttpV2
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrdashboardfrontend.connector.NGRConnector
import uk.gov.hmrc.ngrdashboardfrontend.models.propertyLinking.{PropertyLinkingUserAnswers, VMVProperty}
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.ReferenceType.TRN
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.{CredId, Email, RatepayerRegistration, RatepayerRegistrationValuation, TRNReferenceNumber}

import scala.concurrent.Future

class NGRConnectorSpec extends MockHttpV2 with TestData{
  val ngrConnector: NGRConnector = new NGRConnector(mockHttpClientV2, mockConfig)
  val email: Email = Email("hello@me.com")
  val trn: TRNReferenceNumber = TRNReferenceNumber(TRN, "1234")
  val credId: CredId = CredId("1234")


  "getRatepayer" when {
    "Successfully return a Ratepayer" in {
      val ratepayer: RatepayerRegistration = RatepayerRegistration()
      val response: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/get-ratepayer")(Some(response))
      val result: Future[Option[RatepayerRegistrationValuation]] = ngrConnector.getRatepayer(credId)
      result.futureValue.get.credId mustBe credId
      result.futureValue.get.ratepayerRegistration mustBe Some(ratepayer)
    }
    "ratepayer not found" in {
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/get-ratepayer")(None)
      val result: Future[Option[RatepayerRegistrationValuation]] = ngrConnector.getRatepayer(credId)
      result.futureValue mustBe None
    }
  }

  "getPropertyLinkingUserAnswers" when {
    "Successfully return a PropertyLinkingUserAnswers" in {
      val propertyLinkingUserAnswers = PropertyLinkingUserAnswers(CredId("1234"), property)
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/get-property-linking-user-answers")(Some(propertyLinkingUserAnswers))
      val result: Future[Option[PropertyLinkingUserAnswers]] = ngrConnector.getPropertyLinkingUserAnswers(credId)
      result.futureValue.get.credId mustBe credId
      result.futureValue.get.vmvProperty mustBe property
    }
    "PropertyLinkingUserAnswers not found" in {
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/get-property-linking-user-answers")(None)
      val result: Future[Option[PropertyLinkingUserAnswers]] = ngrConnector.getPropertyLinkingUserAnswers(credId)
      result.futureValue mustBe None
    }
  }

  "getLinkedProperty" when {
    "Successfully return a Property" in {
      val propertyLinkingUserAnswers = PropertyLinkingUserAnswers(CredId("1234"), property)
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/get-property-linking-user-answers")(Some(propertyLinkingUserAnswers))
      val result: Future[Option[VMVProperty]] = ngrConnector.getLinkedProperty(credId)
      result.futureValue.get mustBe property
    }
    "Property not found" in {
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/get-property-linking-user-answers")(None)
      val exception = intercept[NotFoundException] {
        await(ngrConnector.getLinkedProperty(credId))
      }
      exception.getMessage contains "failed to find propertyLinkingUserAnswers from backend mongo" mustBe true
    }
  }
}


