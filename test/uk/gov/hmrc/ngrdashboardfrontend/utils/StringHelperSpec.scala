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

package uk.gov.hmrc.ngrdashboardfrontend.utils

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers

class StringHelperSpec extends AnyWordSpec with Matchers with StringHelper {

  "StringHelper" should {

    "mask NINO - only the last 4 characters stays visible" in {
      maskNino("AB123456C") mustBe "*****456C"
    }

    "return unchanged if shorter than 4 chars" in {
      maskNino("123") mustBe "123"
    }

    "maskString should mask all but the last'n' characters" in {
      maskString("1234567890", 4) mustBe "******7890"
    }
  }
}
