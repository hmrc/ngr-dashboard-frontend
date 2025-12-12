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

import helpers.TestSupport
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.ngrdashboardfrontend.models.audit.ExtendedAuditModel
import uk.gov.hmrc.ngrdashboardfrontend.services.DashboardAuditingService
import scala.concurrent.Future
import org.scalatestplus.mockito.MockitoSugar

class DashboardAuditingServiceSpec extends TestSupport with MockitoSugar {

  "The DashboardAuditingService" should {

    val auditConnector: AuditConnector = mock[AuditConnector]
    val obj = new DashboardAuditingService(mockConfig, auditConnector)

    val auditModel = new ExtendedAuditModel {
      override val auditType: String = "auditType"
      override val detail: Map[String,String] = Map("detail" -> "detail")
    }

    s"return ExtendedDataEvent" in {
      val result = obj.toExtendedDataEvent("appName", auditModel, "path")

      result.auditSource mustBe "appName"
      result.auditType mustBe "auditType"
      result.detail.toString must include("detail")
    }

    "call the connector when extendedAudit is invoked" in {
      when(auditConnector.sendExtendedEvent(any())(any(), any()))
        .thenReturn(Future.successful(AuditResult.Success))

      obj.extendedAudit(auditModel, "path")
      verify(auditConnector).sendExtendedEvent(any())(any(), any())
    }
  }
}




