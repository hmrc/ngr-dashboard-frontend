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
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.Configuration
import uk.gov.hmrc.ngrdashboardfrontend.config.features.Features
import uk.gov.hmrc.ngrdashboardfrontend.config.FrontendAppConfig
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends TestSupport {
  "FrontendAppConfig" must {

    "initialize features correctly" in {
      val mockConfig = mock[Configuration]
      val mockServicesConfig = mock[ServicesConfig]

      val appConfig = new FrontendAppConfig(mockConfig, mockServicesConfig)
      appConfig.features shouldBe a[Features] // Ensures Features is initialized
    }

    "retrieve nextGenerationRatesUrl from ServicesConfig" in {
      val mockConfig = mock[Configuration]
      val mockServicesConfig = mock[ServicesConfig]
      when(mockServicesConfig.baseUrl("next-generation-rates")).thenReturn("http://localhost/next-generation-rates")

      val appConfig = new FrontendAppConfig(mockConfig, mockServicesConfig)

      appConfig.nextGenerationRatesUrl shouldBe "http://localhost/next-generation-rates"
    }

    "retrieve existing config value using getString" in {
      val mockConfig = mock[Configuration]
      val mockServicesConfig = mock[ServicesConfig]
      when(mockConfig.getOptional[String]("some.key")).thenReturn(Some("someValue"))

      val appConfig = new FrontendAppConfig(mockConfig, mockServicesConfig)

      appConfig.getString("some.key") shouldBe "someValue"
    }

    "throw an exception when config key is missing" in {
      val mockConfig = mock[Configuration]
      val mockServicesConfig = mock[ServicesConfig]
      when(mockConfig.getOptional[String]("missing.key")).thenReturn(None)

      val appConfig = new FrontendAppConfig(mockConfig, mockServicesConfig)

      val exception = intercept[RuntimeException] {
        appConfig.getString("missing.key")
      }

      exception.getMessage shouldBe "Could not find config key 'missing.key'"
    }
  }
}
