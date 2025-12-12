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

package uk.gov.hmrc.ngrdashboardfrontend.controllers.testOnly

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.ngrdashboardfrontend.connector.testOnly.NGRStubConnector

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TestOnlyStubControllerSpec extends AnyFreeSpec with BeforeAndAfterEach {

  val mockNGRStubConnector: NGRStubConnector = mock[NGRStubConnector]

  val controller = new TestOnlyStubController(
    stubConnector = mockNGRStubConnector,
    mcc = stubMessagesControllerComponents()
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockNGRStubConnector)
  }

  "TestOnlyStubControllerSpec" - {
    "populateAllStubData" - {
      "should remove all stub data and repopulate it" in {

        when(mockNGRStubConnector.removeAllStubData()(any[HeaderCarrier]))
          .thenReturn(Future.successful(HttpResponse(OK)))

        when(mockNGRStubConnector.populateAllStubData()(any[HeaderCarrier]))
          .thenReturn(Future.successful(HttpResponse(OK)))

        val result = controller.populateAllStubData()(FakeRequest())

        status(result) mustBe OK
      }

    }

    "fail to populateAllStubData" - {
      "should return internal server error when remove all stub data fails" in {

        when(mockNGRStubConnector.removeAllStubData()(any[HeaderCarrier]))
          .thenReturn(Future.failed(new Exception("Failed to remove all stub data")))

        val result = controller.populateAllStubData()(FakeRequest())

        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "should return internal server error when repopulate stub data fails" in {

        when(mockNGRStubConnector.removeAllStubData()(any[HeaderCarrier]))
          .thenReturn(Future.successful(HttpResponse(OK)))

        when(mockNGRStubConnector.populateAllStubData()(any[HeaderCarrier]))
          .thenReturn(Future.failed(new Exception("Failed to repopulate stub data")))

        val result: Future[Result] = controller.populateAllStubData()(FakeRequest())

        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "should return internal server error when repopulate stub data fails with failure status" in {

        when(mockNGRStubConnector.removeAllStubData()(any[HeaderCarrier]))
          .thenReturn(Future.successful(HttpResponse(OK)))

        when(mockNGRStubConnector.populateAllStubData()(any[HeaderCarrier]))
          .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR)))

        val result: Future[Result] = controller.populateAllStubData()(FakeRequest())

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
