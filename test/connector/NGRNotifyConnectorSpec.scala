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
import uk.gov.hmrc.ngrdashboardfrontend.connector.NGRNotifyConnector
import uk.gov.hmrc.ngrdashboardfrontend.models.notify.RatepayerStatusResponse
import uk.gov.hmrc.ngrdashboardfrontend.models.registration._

import scala.concurrent.Future

class NGRNotifyConnectorSpec extends MockHttpV2 with TestData{
  val ngrConnector: NGRNotifyConnector = new NGRNotifyConnector(mockHttpClientV2, mockConfig)
  val id = CredId("12345")


override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.getBridgeStatusFromStub(false)
  }
  "getRatepayer" when {
    "Successfully return a Ratepayer when feature switch is off" in {
      val response: RatepayerStatusResponse = RatepayerStatusResponse(false, false, 0)
      setupMockHttpV2Get(s"${mockConfig.notifyNGRUrl}/ngr-notify/ratepayer-status/${id.value}")(Some(response))
      val result: Future[Option[RatepayerStatusResponse]] = ngrConnector.getRatepayerStatus(id)
      result.futureValue.get.activePropertyLinkCount mustBe 0
      result.futureValue.get.activeRatepayerPersonExists mustBe false
      result.futureValue.get.activeRatepayerPersonaExists mustBe false
    }
    "ratepayer not found when feature switch is off" in {
      setupMockHttpV2Get(s"${mockConfig.notifyNGRUrl}/ngr-notify/ratepayer-status/${id.value}")(None)
      val result: Future[Option[RatepayerStatusResponse]] = ngrConnector.getRatepayerStatus(id)
      result.futureValue mustBe None
    }
  }

  "getRatepayer" when {
    "Successfully return a Ratepayer when feature switch is on" in {
      mockConfig.features.getBridgeStatusFromStub(true)
      val response: RatepayerStatusResponse = RatepayerStatusResponse(false, false, 0)
      setupMockHttpV2Get(s"${mockConfig.ngrStubUrl}/ngr-stub/hip-ratepayer-status/testCred123")(Some(response))
      val result: Future[Option[RatepayerStatusResponse]] = ngrConnector.getRatepayerStatus(id)
      result.futureValue.get.activePropertyLinkCount mustBe 0
      result.futureValue.get.activeRatepayerPersonExists mustBe false
      result.futureValue.get.activeRatepayerPersonaExists mustBe false
    }
    "ratepayer not found when feature switch is on" in {
      mockConfig.features.getBridgeStatusFromStub(true)
      setupMockHttpV2Get(s"${mockConfig.ngrStubUrl}/ngr-stub/hip-ratepayer-status/testCred123")(None)
      val result: Future[Option[RatepayerStatusResponse]] = ngrConnector.getRatepayerStatus(id)
      result.futureValue mustBe None
    }
  }
}


