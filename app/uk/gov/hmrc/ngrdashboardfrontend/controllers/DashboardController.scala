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
import uk.gov.hmrc.ngrdashboardfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.connector.{NGRConnector, NGRNotifyConnector}
import uk.gov.hmrc.ngrdashboardfrontend.models.Status.{Approved, Rejected}
import uk.gov.hmrc.ngrdashboardfrontend.models.components.NavBarPageContents.createHomeNavBar
import uk.gov.hmrc.ngrdashboardfrontend.models.components._
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.utils.DashboardHelper
import uk.gov.hmrc.ngrdashboardfrontend.views.html.DashboardView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DashboardController @Inject()(
                                     dashboardView: DashboardView,
                                     authenticate: AuthRetrievals,
                                     isRegisteredCheck: RegistrationAction,
                                     ngrConnector: NGRConnector,
                                     ngrNotifyConnector: NGRNotifyConnector,
                                     mcc: MessagesControllerComponents
                                   )(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def show(): Action[AnyContent] = (authenticate andThen isRegisteredCheck).async { implicit request =>
    ngrConnector.linkedPropertyStatus(CredId(request.credId.getOrElse("")), request.nino).flatMap {
      case Some(vmvPropertyStatus) =>
        val cards: Seq[Card] = DashboardHelper.getDashboardCards(true, vmvPropertyStatus.status)
        val name = request.name.flatMap(_.name).getOrElse(throw new RuntimeException("Name not found"))
        Future.successful(Ok(dashboardView(
          cards = cards,
          name = name,
          navigationBarContent = createHomeNavBar)))
      case None => ngrNotifyConnector.getRatepayerStatus(CredId(request.credId.getOrElse(""))).map { vmvPropertyStatus =>
        val status = vmvPropertyStatus match {
          case Some(status) if status.activePropertyLinkCount > 0 => Approved
          case None => Rejected
          case _ => Rejected
        }

        val cards: Seq[Card] = DashboardHelper.getDashboardCards(vmvPropertyStatus.isDefined, status)
        val name = request.name.flatMap(_.name).getOrElse(throw new RuntimeException("Name not found"))
        Ok(dashboardView(
          cards = cards,
          name = name,
          navigationBarContent = createHomeNavBar))

      }
    }

  }
}

