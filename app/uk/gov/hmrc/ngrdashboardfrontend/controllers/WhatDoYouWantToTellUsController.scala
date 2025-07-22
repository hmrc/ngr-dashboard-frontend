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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrdashboardfrontend.actions.{AuthRetrievals, PropertyLinkingAction}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.connector.NGRConnector
import uk.gov.hmrc.ngrdashboardfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.views.html.WhatDoYouWantToTellUsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhatDoYouWantToTellUsController @Inject()(authenticate: AuthRetrievals,
                                                hasLinkedProperties: PropertyLinkingAction,
                                                ngrConnector: NGRConnector,
                                                whatDoYouWantToTellUsView: WhatDoYouWantToTellUsView,
                                                mcc: MessagesControllerComponents)(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {
  def show(propertyReference: String): Action[AnyContent] =
    (authenticate andThen hasLinkedProperties).async { implicit request =>
      ngrConnector.getLinkedProperty(CredId(request.credId.getOrElse(""))).flatMap {
        case Some(vmvProperty) => Future.successful(Ok(whatDoYouWantToTellUsView(createDefaultNavBar, vmvProperty.addressFull, propertyReference)))
        case None => Future.failed(throw new NotFoundException("Unable to find match Linked Properties"))
      }
    }
}
