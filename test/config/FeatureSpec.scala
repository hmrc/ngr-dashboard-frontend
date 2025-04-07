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

package config

import helpers.TestSupport
import play.api.Configuration
import uk.gov.hmrc.ngrdashboardfrontend.config.features.{Feature, Features}

class FeatureSpec extends TestSupport{

  private val features = new Features()(app.injector.instanceOf[Configuration])

  override def beforeEach(): Unit = {
    super.beforeEach()
    features.welshLanguageSupportEnabled(true)
  }

  "The welsh language support feature" must {

    "return its current state" in {
      features.welshLanguageSupportEnabled() mustBe true
    }

    "switch to a new state" in {
      features.welshLanguageSupportEnabled(false)
      features.welshLanguageSupportEnabled() mustBe false
    }

  }


  "Feature" must {

    "set system property when apply(value) is called" in {
      val feature = new Feature("test.feature")(Configuration.empty)

      feature.apply(true)
      sys.props.get("test.feature") mustBe Some("true")

      feature.apply(false)
      sys.props.get("test.feature") mustBe Some("false")
    }

    "return system property value if set" in {
      sys.props += "test.feature" -> "true"
      val feature = new Feature("test.feature")(Configuration.empty)
      feature.apply() mustBe true

      sys.props += "test.feature" -> "false"
      feature.apply() mustBe false
    }

    "return configuration value if system property is not set" in {
      sys.props -= "test.feature" // Ensure system property is not set
      val config = Configuration("test.feature" -> true)
      val feature = new Feature("test.feature")(config)
      feature.apply() mustBe true
    }

    "return false if neither system property nor configuration value is set" in {
      sys.props -= "test.feature" // Ensure system property is not set
      val feature = new Feature("test.feature")(Configuration.empty)
      feature.apply() mustBe false
    }

    "handle invalid system property values gracefully" in {
      sys.props += "test.feature" -> "invalid"
      val feature = new Feature("test.feature")(Configuration.empty)

      an[Exception] shouldBe thrownBy {
        feature.apply()
      }
    }
  }


}
