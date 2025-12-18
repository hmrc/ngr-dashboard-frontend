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

package uk.gov.hmrc.ngrdashboardfrontend.services

import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.connector.{NGRConnector, NGRNotifyConnector}
import uk.gov.hmrc.ngrdashboardfrontend.models.Status.{Approved, Pending, Rejected}
import uk.gov.hmrc.ngrdashboardfrontend.models.propertyLinking.VMVPropertyStatus

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PropertyLinkingStatusService @Inject()(notifyNGRConnector: NGRNotifyConnector,
                                             appConfig: AppConfig,
                                             nGRConnector: NGRConnector,
                                             http: HttpClientV2)(implicit ec: ExecutionContext) {
  def linkedPropertyStatus(nino: Nino)(implicit hc: HeaderCarrier): Future[Option[VMVPropertyStatus]] = {
    if (appConfig.features.vmvPropertyStatusTestEnabled()) {
      nGRConnector.getPropertyLinkingUserAnswers().flatMap {
        case Some(propertyLinkingUserAnswers) =>
          http.get(url"${appConfig.ngrStubUrl}/ngr-stub/ngrPropertyStatus/${nino.nino.getOrElse("AA000003D")}") //TODO remove this code once the ngr-notify PropertyStatus endpoint is complete
            .execute[HttpResponse].flatMap {
              response =>
                response.body match {
                  case value if value.contains(Rejected.toString) =>
                    Future.successful(Some(VMVPropertyStatus(Rejected, propertyLinkingUserAnswers.vmvProperty)))
                  case value if value.contains(Pending.toString) =>
                    Future.successful(Some(VMVPropertyStatus(Pending, propertyLinkingUserAnswers.vmvProperty)))
                  case value if value.contains(Approved.toString) =>
                    Future.successful(Some(VMVPropertyStatus(Approved, propertyLinkingUserAnswers.vmvProperty)))
                  case _ =>
                    notifyNGRConnector.getRatepayerStatus.flatMap {
                      case Some(response) if response.activePropertyLinkCount > 0  =>
                        Future.successful(Some(VMVPropertyStatus(Approved, propertyLinkingUserAnswers.vmvProperty)))
                      case Some(_) =>
                        Future.successful(Some(VMVPropertyStatus(Pending, propertyLinkingUserAnswers.vmvProperty)))
                      case None =>
                        Future.successful(None)
                    }
                }
            }
        case None => Future.successful(None)
      }
    } else nGRConnector.getPropertyLinkingUserAnswers()
      .flatMap {
        case Some(propertyLinkingUserAnswers) =>
          notifyNGRConnector.getRatepayerStatus.map {
            case Some(status) if status.activePropertyLinkCount > 0 =>
              Some(VMVPropertyStatus(Approved, propertyLinkingUserAnswers.vmvProperty))
            case Some(_) =>
              Some(VMVPropertyStatus(Pending, propertyLinkingUserAnswers.vmvProperty))
            case None =>
              None
          }
        case None => Future.successful(None)
      }
  }

}
