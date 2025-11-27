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

import play.api.libs.json.Json
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, NotFoundException, StringContextOps}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.models.Status.{Approved, Pending, Rejected}
import uk.gov.hmrc.ngrdashboardfrontend.models.notify.RatepayerStatusResponse
import uk.gov.hmrc.ngrdashboardfrontend.models.propertyLinking.{PropertyLinkingUserAnswers, VMVProperty, VMVPropertyStatus}
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.{CredId, RatepayerRegistrationValuation}

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NGRConnector @Inject()(http: HttpClientV2,
                             appConfig: AppConfig,
                             notifyNGRConnector: NGRNotifyConnector)
                            (implicit ec: ExecutionContext) {

  private def url(path: String): URL = url"${appConfig.nextGenerationRatesUrl}/next-generation-rates/$path"

  def getRatepayer(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[RatepayerRegistrationValuation]] = {
    implicit val rds: HttpReads[RatepayerRegistrationValuation] = readFromJson
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, None)
    http.get(url("get-ratepayer"))
      .withBody(Json.toJson(model))
      .execute[Option[RatepayerRegistrationValuation]]
  }

  def getPropertyLinkingUserAnswers(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[PropertyLinkingUserAnswers]] = {
    implicit val rds: HttpReads[PropertyLinkingUserAnswers] = readFromJson
    val dummyVMVProperty: VMVProperty = VMVProperty(0L, "", "", "", List.empty) // TODO: Replace with a proper VMVProperty instance if needed
    val model: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId, dummyVMVProperty)
    http.get(url("get-property-linking-user-answers"))
      .withBody(Json.toJson(model))
      .execute[Option[PropertyLinkingUserAnswers]]
  }

  def linkedPropertyStatus(credId: CredId, nino: Nino)(implicit hc: HeaderCarrier): Future[Option[VMVPropertyStatus]] = {
    if (appConfig.features.vmvPropertyStatusTestEnabled()) {
      getPropertyLinkingUserAnswers(credId).flatMap {
        case Some(propertyLinkingUserAnswers) =>
          http.get(url"${appConfig.ngrStubHost}/ngr-stub/ngrPropertyStatus/${nino.nino.getOrElse("AA000003D")}")
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
                    http.get(url"${appConfig.ngrStubHost}/ngr-stub/hip-ratepayer-status/testCred123").execute[RatepayerStatusResponse].flatMap {
                        case response if response.activePropertyLinkCount > 0  =>
                          Future.successful(Some(VMVPropertyStatus(Approved, propertyLinkingUserAnswers.vmvProperty)))
                        case response: RatepayerStatusResponse =>
                          Future.successful(Some(VMVPropertyStatus(Pending, propertyLinkingUserAnswers.vmvProperty)))
                    }
                }
            }
        case None => Future.successful(None)
      }
    } else getPropertyLinkingUserAnswers(credId)
      .flatMap {
        case Some(propertyLinkingUserAnswers) =>
          notifyNGRConnector.getRatepayerStatus(credId).map {
            case Some(status) if status.activePropertyLinkCount > 0 =>
              Some(VMVPropertyStatus(Approved, propertyLinkingUserAnswers.vmvProperty))
            case None =>
              Some(VMVPropertyStatus(Pending, propertyLinkingUserAnswers.vmvProperty))
          }
        case None => Future.successful(None)
      }
  }

  def getLinkedProperty(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[VMVProperty]] = {
    getPropertyLinkingUserAnswers(credId)
      .map {
        case Some(propertyLinkingUserAnswers) => Some(propertyLinkingUserAnswers.vmvProperty)
        case None => throw new NotFoundException("failed to find propertyLinkingUserAnswers from backend mongo")
      }
  }
}
