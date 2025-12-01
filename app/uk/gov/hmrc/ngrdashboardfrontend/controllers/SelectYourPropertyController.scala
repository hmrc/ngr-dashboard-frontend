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

import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrdashboardfrontend.actions.{AuthRetrievals, PropertyLinkingAction}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.connector.NGRConnector
import uk.gov.hmrc.ngrdashboardfrontend.models.auth.AuthenticatedUserRequest
import uk.gov.hmrc.ngrdashboardfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrdashboardfrontend.models.components._
import uk.gov.hmrc.ngrdashboardfrontend.models.propertyLinking.VMVPropertyStatus
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.services.PropertyLinkingStatusService
import uk.gov.hmrc.ngrdashboardfrontend.views.html.SelectYourPropertyView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectYourPropertyController @Inject()(selectYouPropertyView: SelectYourPropertyView,
                                             authenticate: AuthRetrievals,
                                             hasLinkedProperties: PropertyLinkingAction,
                                             ngrService: PropertyLinkingStatusService,
                                             mcc: MessagesControllerComponents)(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {


  private def generateTable(propertyList: List[VMVPropertyStatus])(implicit messages: Messages): Table = {
    TableData(
      headers = Seq(
        TableHeader(messages("property.address"), "govuk-table__caption--s govuk-!-width-half"),
        TableHeader(messages("property.reference"), "govuk-table__caption--s, govuk-!-width-one-quarter"),
        TableHeader(messages("property.status"), "govuk-table__caption--s"),
        TableHeader(messages("property.action"), "govuk-!-width-one-quarter")),
      rows = propertyList.map(property => Seq(
        TableRowText(property.vmvProperty.addressFull),
        TableRowText(property.vmvProperty.localAuthorityReference),
        TableRowIsActive(status = property.status),
        getAssessmentId(property).map(assessmentRef => TableRowLink(routes.WhatDoYouWantToTellUsController.show(assessmentRef).url, messages("property.select"))).getOrElse(TableRowText(""))
      )),
      caption = Some(messages(""))
    ).toTable
  }

  private def getAssessmentId(property: VMVPropertyStatus): Option[String] = {
    property.vmvProperty.valuations
      .filter(_.assessmentStatus == "CURRENT")
      .sortBy(_.effectiveDate)
      .lastOption
      .map(_.assessmentRef.toString)
  }

  def show(): Action[AnyContent] =
    (authenticate andThen hasLinkedProperties).async { implicit request: AuthenticatedUserRequest[AnyContent] =>
      ngrService.linkedPropertyStatus(CredId(request.credId.getOrElse("")), request.nino).flatMap {
        case Some(vmvProperty) => Future.successful(Ok(selectYouPropertyView(createDefaultNavBar, generateTable(List(vmvProperty)), routes.DashboardController.show.url)))
        case None => Future.failed(throw new NotFoundException("Unable to find match Linked Properties"))
      }
    }
}
