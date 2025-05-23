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
import play.api.i18n.Lang.logger
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.views.html.BeforeYouGoView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}

@Singleton
class BeforeYouGoController @Inject()(
                                     beforeYouGoView: BeforeYouGoView,
                                     mcc: MessagesControllerComponents)(implicit appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def signout: Action[AnyContent] = Action { _ =>
    Redirect(appConfig.logoutUrl).withNewSession
  }

  def show(): Action[AnyContent] = Action { implicit request =>
      Ok(beforeYouGoView(routes.DashboardController.show.url, routes.BeforeYouGoController.feedback.url))
    }

  def feedback: Action[AnyContent] = Action { _ =>
    val uuid = randomUUID().toString
    val auditData = Map("feedbackId" -> uuid, "customMetric" -> "Next-Generation-Rates")
    logger.info(s"Redirecting to feedback frontend $auditData")
    Redirect(appConfig.feedbackFrontendUrl).withSession(("feedbackId", uuid))
  }
}
