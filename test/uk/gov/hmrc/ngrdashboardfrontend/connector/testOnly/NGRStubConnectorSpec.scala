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

package uk.gov.hmrc.ngrdashboardfrontend.connector.testOnly

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, delete, post, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.test.WireMockSupport
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

class NGRStubConnectorSpec extends AnyFreeSpec with WireMockSupport with GuiceOneAppPerSuite with ScalaFutures
with IntegrationPatience {

  implicit val hc: HeaderCarrier         = HeaderCarrier()

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.ngr-stub.host" -> wireMockHost,
        "microservice.services.ngr-stub.port" -> wireMockPort
      ).build()

  private val connector = app.injector.instanceOf[NGRStubConnector]
  private def url(path: String) = s"/ngr-stub$path"

  "NGRStubConnectorSpec" -  {
    "remove all stub data" in {
      stubDeleteResponse(OK, url("/all-data"))
      val result: Future[HttpResponse] = connector.removeAllStubData()
      result.futureValue.status mustBe OK
    }

    "remove all stub data when the downstream fail" in {
      stubDeleteResponse(INTERNAL_SERVER_ERROR, url("/all-data"))
      val result: Future[HttpResponse] = connector.removeAllStubData()
      result.futureValue.status mustBe INTERNAL_SERVER_ERROR
    }

    "populate all stub data" in {
      stubResponse(OK, url("/add-all-data"))
      val result: Future[HttpResponse] = connector.populateAllStubData()
      result.futureValue.status mustBe OK
    }

    "populate all stub data the downstream fails" in {
      stubResponse(INTERNAL_SERVER_ERROR, url("/add-all-data"))
      val result: Future[HttpResponse] = connector.populateAllStubData()
      result.futureValue.status mustBe INTERNAL_SERVER_ERROR
    }
  }

  private def stubResponse(expectedStatus: Int, url: String): StubMapping =
    wireMockServer.stubFor(
      post(urlEqualTo(url.toString))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
        )
    )

  private def stubDeleteResponse(expectedStatus: Int, url: String): StubMapping =
    wireMockServer.stubFor(
      delete(urlEqualTo(url.toString))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
        )
    )

}
