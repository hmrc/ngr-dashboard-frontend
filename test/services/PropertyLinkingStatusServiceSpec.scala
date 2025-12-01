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

package services

import helpers.{ControllerSpecSupport, TestData}
import mocks.MockHttpV2
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.ngrdashboardfrontend.models.Status.{Approved, Pending}
import uk.gov.hmrc.ngrdashboardfrontend.models.notify.RatepayerStatusResponse
import uk.gov.hmrc.ngrdashboardfrontend.models.propertyLinking.PropertyLinkingUserAnswers
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.services.PropertyLinkingStatusService

import scala.concurrent.Future

class PropertyLinkingStatusServiceSpec extends ControllerSpecSupport with DefaultAwaitTimeout with MockHttpV2 with TestData {
  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.getBridgeStatusFromStub(false)
    mockConfig.features.vmvPropertyStatusTestEnabled(false)
  }

  implicit val requestHeader: RequestHeader = mock[RequestHeader]

  val credId = CredId("123456")
  val nino: Nino = Nino(true, Some("AA000003D"))
  val linkedProperty = PropertyLinkingUserAnswers(credId, property)

  val service = new PropertyLinkingStatusService(
    mockNotifyNGRConnector,
    mockConfig,
    mockNGRConnector,
    mockHttpClientV2
  )


  "PropertyLinkingStatusService" must {
    "produce correct results for linkedPropertyStatus when a bridge responds with rate payer status with one property links" in {
      when(mockNotifyNGRConnector.getRatepayerStatus(any[CredId])(any())).thenReturn(Future.successful(Some(RatepayerStatusResponse(true, true, 1))))
      when(mockNGRConnector.getPropertyLinkingUserAnswers(any[CredId])(any())).thenReturn(Future.successful(Some(linkedProperty)))

      val result = service.linkedPropertyStatus(credId, nino)

      result.futureValue.get.status mustBe Approved
    }

    "produce correct results for linkedPropertyStatus when a bridge responds with rate payer status with zero property links" in {
      when(mockNotifyNGRConnector.getRatepayerStatus(any[CredId])(any())).thenReturn(Future.successful(Some(RatepayerStatusResponse(true, true, 0))))
      when(mockNGRConnector.getPropertyLinkingUserAnswers(any[CredId])(any())).thenReturn(Future.successful(Some(linkedProperty)))

      val result = service.linkedPropertyStatus(credId, nino)

      result.futureValue.get.status mustBe Pending
    }

    "produce correct results for linkedPropertyStatus when a bridge responds with rate payer status when no response is received" in {
      when(mockNotifyNGRConnector.getRatepayerStatus(any[CredId])(any())).thenReturn(Future.successful(None))
      when(mockNGRConnector.getPropertyLinkingUserAnswers(any[CredId])(any())).thenReturn(Future.successful(Some(linkedProperty)))

      val result = service.linkedPropertyStatus(credId, nino)

      result.futureValue mustBe None
    }

    "produce correct results for linkedPropertyStatus when a bridge responds with rate payer status when no response is received from property link answer" in {
      when(mockNotifyNGRConnector.getRatepayerStatus(any[CredId])(any())).thenReturn(Future.successful(None))
      when(mockNGRConnector.getPropertyLinkingUserAnswers(any[CredId])(any())).thenReturn(Future.successful(None))

      val result = service.linkedPropertyStatus(credId, nino)

      result.futureValue mustBe None
    }
  }

}
