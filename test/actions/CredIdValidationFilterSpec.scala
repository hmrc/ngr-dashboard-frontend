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

package actions

import helpers.TestSupport
import play.api.mvc._
import play.api.test.Helpers._
import uk.gov.hmrc.ngrdashboardfrontend.actions.CredIdValidationFilter
import uk.gov.hmrc.ngrdashboardfrontend.models.auth.AuthenticatedUserRequest

class CredIdValidationFilterSpec extends TestSupport {

  val filter = new CredIdValidationFilter()

  def buildRequest(testCredId: Option[String]): AuthenticatedUserRequest[AnyContentAsEmpty.type] =
    authenticatedFakeRequest.copy(credId = testCredId)

  "CredIdValidationFilter" should {
    "return None when credId is present and non-empty" in {
      val request = buildRequest(Some("validCredId"))
      val futureResult = filter.filter(request)
      await(futureResult) mustBe None
    }

    "return Some(BadRequest) when credId is missing" in {
      val request = buildRequest(None)
      val futureResult = filter.filter(request)
      await(futureResult) mustBe Some(Results.BadRequest("Missing credId in request"))
    }

    "return Some(BadRequest) when credId is empty string" in {
      val request = buildRequest(Some(""))
      val futureResult = filter.filter(request)
      await(futureResult) mustBe Some(Results.BadRequest("Missing credId in request"))
    }

    "return Some(BadRequest) when credId is whitespace only" in {
      val request = buildRequest(Some("   "))
      val futureResult = filter.filter(request)
      await(futureResult) mustBe Some(Results.BadRequest("Missing credId in request"))
    }
  }
}
