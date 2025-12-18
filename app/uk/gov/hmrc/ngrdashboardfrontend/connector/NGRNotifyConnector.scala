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

package uk.gov.hmrc.ngrdashboardfrontend.connector

import play.api.Logging
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.models.notify.RatepayerStatusResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class NGRNotifyConnector @Inject()(http: HttpClientV2,
                                   appConfig: AppConfig,
                            )(implicit ec: ExecutionContext) extends Logging {


  def getRatepayerStatus(implicit hc: HeaderCarrier): Future[Option[RatepayerStatusResponse]] = {
    if (!appConfig.features.getBridgeStatusFromStub()) {
      http.get(url"${appConfig.notifyNGRUrl}/ngr-notify/ratepayer-status")
        .execute[Option[RatepayerStatusResponse]]
    } else {//TODO remove stub call when ngr-notify implementation is complete
      http.get(url"${appConfig.ngrStubUrl}/ngr-stub/hip-ratepayer-status/testCred123")
        .execute[Option[RatepayerStatusResponse]]
    }
  }
}
