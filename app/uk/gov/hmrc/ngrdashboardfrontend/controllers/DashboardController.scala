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
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrdashboardfrontend.models.components.NavBarPageContents.CreateNavBar
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{Card, DashboardCard, Link, NavBarContents, NavBarCurrentPage}
import uk.gov.hmrc.ngrdashboardfrontend.views.html.DashboardView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class DashboardController @Inject()(
  dashboardView: DashboardView,
  authenticate: AuthJourney,
  mcc: MessagesControllerComponents
  )(implicit appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  // for demonstration:
  val dashboardCard: DashboardCard = DashboardCard(
    titleKey = "home.propertiesCard.title",
    captionKey = Some("home.propertiesCard.caption"),
    captionKey2 = Some("home.propertiesCard.caption2"),
    captionKey3 = None,
    voaReference = None,
    tag = Some("home.propertiesCard.tag"),
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = "some-href"),
          linkId = "LinkId-Card",
          messageKey = "home.propertiesCard.addProperty",
        ),
        Link(
          href = Call(method = "GET", url = "some-href"),
          linkId = "LinkId2-Card",
          messageKey = "home.propertiesCard.manageProperties",
        )
      )
    )
  )


  def show(): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      val singleCard: Card = DashboardCard.card(dashboardCard)
      val name = request.name.flatMap(_.name).getOrElse("John Smith")
      Future.successful(Ok(dashboardView(
        cards = Seq(singleCard),
        name = name,
        navigationBarContent = CreateNavBar(
          contents = NavBarContents(
            homePage = Some(true),
            messagesPage = Some(true),
            profileAndSettingsPage = Some(true),
            signOutPage = Some(true)
          ),
          currentPage = NavBarCurrentPage(homePage = true),
          notifications = Some(1)
        ))))
    }
  }

}
