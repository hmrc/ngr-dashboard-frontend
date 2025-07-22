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
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{TableData, TableHeader, TableRowLink, TableRowText}
import uk.gov.hmrc.ngrdashboardfrontend.models.propertyLinking.VMVProperty
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.views.html.SelectYourPropertyView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectYourPropertyController @Inject()(selectYouPropertyView: SelectYourPropertyView,
                                             authenticate: AuthRetrievals,
                                             hasLinkedProperties: PropertyLinkingAction,
                                             ngrConnector: NGRConnector,
                                             mcc: MessagesControllerComponents)(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  private def generateTable(propertyList: List[VMVProperty])(implicit messages: Messages): Table = {
    TableData(
      headers = Seq(
        TableHeader("Address", "govuk-table__caption--s govuk-!-width-one-quarter"),
        TableHeader("Property reference", "govuk-table__caption--s govuk-!-width-one-quarter"),
        TableHeader("Description", "govuk-table__caption--s govuk-!-width-one-quarter"),
        TableHeader("", "")),
      rows = propertyList.map(property => Seq(
        TableRowText(property.addressFull),
        TableRowText(property.localAuthorityReference),
        TableRowText(property.valuations.last.descriptionText),
        TableRowLink(routes.WhatDoYouWantToTellUsController.show(property.localAuthorityReference).url, "Select property")
      )),
      caption = Some(messages("selectYourProperty.table.caption"))
    ).toTable
  }

  def show(): Action[AnyContent] =
    (authenticate andThen hasLinkedProperties).async { implicit request: AuthenticatedUserRequest[AnyContent] =>
      ngrConnector.getLinkedProperty(CredId(request.credId.getOrElse(""))).flatMap {
        case Some(vmvProperty) => Future.successful(Ok(selectYouPropertyView(createDefaultNavBar, generateTable(List(vmvProperty)))))
        case None => Future.failed(throw new NotFoundException("Unable to find match Linked Properties"))
      }
    }
}
