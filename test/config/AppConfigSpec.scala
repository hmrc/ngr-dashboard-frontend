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
import play.api.Configuration
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig

class AppConfigSpec extends TestSupport {
  "AppConfig" must {
    "retrieve logout url correct from config" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("dashboard.host")).thenReturn(Some("http://localhost:1503"))
      when(mockConfig.getOptional[String]("microservice.services.bas-gateway-frontend.host")).thenReturn(Some("http://localhost:9553"))

      appConfig.logoutUrl mustBe ("http://localhost:9553/bas-gateway/sign-out-without-state?continue=http://localhost:1503/ngr-dashboard-frontend/beforeYouGo")
    }

    "missing dashboard host from config throws exception" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("dashboard.host")).thenReturn(None)
      when(mockConfig.getOptional[String]("microservice.services.bas-gateway-frontend.host")).thenReturn(Some("http://localhost:9553"))

      val exception = intercept[Exception] {
        appConfig.logoutUrl
      }
      exception.getMessage mustBe "Missing key: dashboard.host"
    }

    "empty dashboard host String from config throws exception" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("dashboard.host")).thenReturn(Some(""))
      when(mockConfig.getOptional[String]("microservice.services.bas-gateway-frontend.host")).thenReturn(Some("http://localhost:9553"))

      val exception = intercept[Exception] {
        appConfig.logoutUrl
      }
      exception.getMessage mustBe "Missing key: dashboard.host"
    }

    "missing bas gateway host from config throws exception" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("dashboard.host")).thenReturn(Some("http://localhost:9514"))
      when(mockConfig.getOptional[String]("microservice.services.bas-gateway-frontend.host")).thenReturn(None)

      val exception = intercept[Exception] {
        appConfig.logoutUrl
      }
      exception.getMessage mustBe "Missing key: microservice.services.bas-gateway-frontend.host"
    }

    "retrieve feedback url correct from config" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("microservice.services.feedback-survey-frontend.host")).thenReturn(Some("http://localhost:9514"))

      appConfig.feedbackFrontendUrl mustBe ("http://localhost:9514/feedback/NGR-Dashboard")
    }

    "missing feedback host from config throws exception" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("microservice.services.feedback-survey-frontend.host")).thenReturn(None)

      val exception = intercept[Exception] {
        appConfig.feedbackFrontendUrl
      }
      exception.getMessage mustBe "Missing key: microservice.services.feedback-survey-frontend.host"
    }
  }
}
