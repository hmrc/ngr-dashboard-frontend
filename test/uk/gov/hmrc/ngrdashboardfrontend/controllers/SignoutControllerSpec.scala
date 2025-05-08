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
import org.mockito.Mockito.when
import play.api.http.Status.SEE_OTHER
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{redirectLocation, status}

class SignoutControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  val logoutUrl = "http://localhost:9553/bas-gateway/sign-out-without-state?continue=http://localhost:9514/feedback/NGR-Dashboard"

  def controller() = new SignoutController(mcc)(appConfig = mockAppConfig)

  "Auth Journey" must {
    "redirect user to exit survey" when {
      "logout() is called it" should {
        "return status code 303" in {
          when(mockAppConfig.logoutUrl).thenReturn(logoutUrl)
          val result = controller().signout()(authenticatedFakeRequest)
          status(result) mustBe SEE_OTHER
        }

        "return the sign out url with feedback url" in {
          when(mockAppConfig.logoutUrl).thenReturn(logoutUrl)
          val result = controller().signout()(authenticatedFakeRequest)
          redirectLocation(result) mustBe Some(logoutUrl)
        }

        "new session contains feedbackId" in {
          when(mockAppConfig.logoutUrl).thenReturn(logoutUrl)
          val result = controller().signout()(authenticatedFakeRequest)
          result.map(result =>
            result.session.get("feedbackId").isDefined mustBe true
          )
        }

      }
    }
  }
}
