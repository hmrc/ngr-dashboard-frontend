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
import uk.gov.hmrc.ngrdashboardfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.models.components.NavBarPageContents.CreateNavBar
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{NavBarContents, NavBarCurrentPage}
import uk.gov.hmrc.ngrdashboardfrontend.views.html.WhatYouNeedView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class WhatYouNeedController @Inject()(authenticate: AuthRetrievals,
                                      isRegisteredCheck: RegistrationAction,
                                      view: WhatYouNeedView, mcc: MessagesControllerComponents)(implicit appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  private lazy val navBarContents = CreateNavBar(
    contents = NavBarContents(
      homePage = Some(true),
      messagesPage = Some(false),
      profileAndSettingsPage = Some(false),
      signOutPage = Some(true)
    ),
    currentPage = NavBarCurrentPage(),
    notifications = None
  )

  def show: Action[AnyContent] = (authenticate andThen isRegisteredCheck).async { implicit request =>
   Future.successful(Ok(view(navBarContents, createLink)))
  }

  def next: Action[AnyContent] = {
    Action.async {
      Future.successful(Redirect(routes.DashboardController.show))
    }
  }

  private def createLink(implicit messages: Messages): String = {
    val text = messages("whatYouNeed.p2")
    val linkText = messages("whatYouNeed.link")
    val link = s"""<a href="https://www.gov.uk/contact-your-local-council-about-business-rates" target="_blank">$linkText</a>"""
    val content = text.replace(linkText, link)
    s"""<p class="govuk-body">$content</p>"""
  }

}
