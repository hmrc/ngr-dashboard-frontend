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
import uk.gov.hmrc.ngrdashboardfrontend.views.html.HelloWorldPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.ngrdashboardfrontend.models.{Card, DashboardCard, Link}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class HelloWorldController @Inject()(
  mcc: MessagesControllerComponents,
  helloWorldPage: HelloWorldPage)
    extends FrontendController(mcc) with I18nSupport {

  val dashboardCard: DashboardCard = DashboardCard(
    titleKey = "home.yourAccountCard.title",
    captionKey =  Some("home.yourAccountCard.caption"),
    captionKey2 =  Some("home.yourAccountCard.caption2"),
    captionKey3 =  Some("home.yourAccountCard.caption3"),
    voaReference = Some("VOA176292C"),
    tag = None,
    links = Some(
      Seq(
        Link(
          href       = Call(method = "GET",url = "some-href"),
          linkId     = "LinkId-Card",
          messageKey = "home.yourAccountCard.link1",
        )
      )
    )
  )

  def helloWorld(): Action[AnyContent] = {
    Action.async { implicit request =>
      val singleCard: Card = DashboardCard.card(dashboardCard)
      Future.successful(Ok(helloWorldPage(cards = Seq(singleCard, singleCard, singleCard, singleCard, singleCard, singleCard))))
    }
  }

}
