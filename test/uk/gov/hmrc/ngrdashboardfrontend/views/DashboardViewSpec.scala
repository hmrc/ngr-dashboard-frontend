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

package uk.gov.hmrc.ngrdashboardfrontend.views

import helpers.ViewBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import uk.gov.hmrc.ngrdashboardfrontend.models.components.NavBarPageContents.CreateNavBar
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{DashboardCard, Link, NavBarContents, NavBarCurrentPage, NavigationBarContent}
import uk.gov.hmrc.ngrdashboardfrontend.views.html.DashboardView

class DashboardViewSpec extends ViewBaseSpec {

  val view: DashboardView = app.injector.instanceOf[DashboardView]

  val dashboardCard: DashboardCard = DashboardCard(
    titleKey = "home.addPropertiesCard.title",
    captionKey = Some("home.addPropertiesCard.caption"),
    captionKey2 = Some("home.addPropertiesCard.caption2"),
    captionKey3 = Some("home.addPropertiesCard.caption2"),
    voaReference = Some("ref"),
    tag = Some("home.addPropertiesCard.tag"),
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = "some-href"),
          linkId = "LinkId-Card",
          messageKey = "home.addPropertiesCard.addProperty",
        ),
        Link(
          href = Call(method = "GET", url = "some-href"),
          linkId = "LinkId2-Card",
          messageKey = "home.addPropertiesCard.manageProperties",
        )
      )
    )
  )

  val dashboardCardOneLink: DashboardCard = DashboardCard(
    titleKey = "home.reportChangeCard.title",
    captionKey = Some("home.reportChangeCard.caption"),
    captionKey2 = Some("home.reportChangeCard.caption2"),
    captionKey3 = None,
    voaReference = Some("ref"),
    tag = Some("home.addPropertiesCard.tag"),
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = "second-href"),
          linkId = "LinkId-Card",
          messageKey = "home.reportChangeCard.link1",
        )
      )
    )
  )

  val navBarContent: NavigationBarContent = CreateNavBar(
    contents = NavBarContents(
      homePage = Some(true),
      messagesPage = Some(true),
      profileAndSettingsPage = Some(true),
      signOutPage = Some(true)
    ),
    currentPage = NavBarCurrentPage(homePage = true),
    notifications = Some(1)
  )

  object Selectors {
    val homeButton = "#secondary-nav > a > span"
    val messages = "#secondary-nav > ul > li:nth-child(1) > a"
    val signOut = "#secondary-nav > ul > li:nth-child(3) > a"
    val dashboardFirstCard = "#main-content > div > div > div.flex-container.govuk-grid-row > div:nth-child(1) > div.card-body.active > a"
    val dashboardFirstCardHeading = s"$dashboardFirstCard > h2.govuk-heading-s.card-heading"
    val dashboardFirstCardData = s"$dashboardFirstCard > h2:nth-child(2)"
    val dashboardFirstCardData2 = s"$dashboardFirstCard > h2:nth-child(3)"
    val dashboardSecondCard = "#main-content > div > div > div.flex-container.govuk-grid-row > div:nth-child(2) > div.card-body.active > a"
    val dashboardSecondCardHeading = s"$dashboardSecondCard > h2.govuk-heading-s.card-heading"
    val dashboardSecondCardData = s"$dashboardSecondCard > h2:nth-child(2)"
    val dashboardSecondCardData2 = s"$dashboardSecondCard > h2:nth-child(3)"

  }

  "Dashboard view" must {
    val dashboardView = view(cards = Seq(DashboardCard.card(dashboardCard), DashboardCard.card(dashboardCardOneLink)), name = "Greg", navigationBarContent = navBarContent)
    lazy implicit val document: Document = Jsoup.parse(dashboardView.body)
    lazy val htmlF = view.f(Seq(DashboardCard.card(dashboardCard)), "Greg", navBarContent)
    lazy val htmlFOneLink = view.f(Seq(DashboardCard.card(dashboardCardOneLink)), "Greg", navBarContent)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
      htmlFOneLink.toString() must not be empty
    }

    "show account home button" in {
      elementText(Selectors.homeButton) mustBe "Account home"
    }

    "show messages button" in {
      elementText(Selectors.messages) contains "Messages"
    }

    "show sign out button" in {
      elementText(Selectors.signOut) mustBe "Sign out"
    }

    "show the correct title" in {
      document.title() mustBe "Account home - Manage your business rates valuation - GOV.UK"
    }

    "show the correct heading" in {
      elementText("h1") mustBe "Greg"
    }

    "show the correct first dashboard card" in {
      elementText(Selectors.dashboardFirstCardHeading) mustBe "Add a property"
      elementText(Selectors.dashboardFirstCardData) mustBe "Add a property you have a connection with."
      elementText(Selectors.dashboardFirstCardData2) mustBe "You must tell us within 60 days of becoming the ratepayer. Do this by adding the property to your account."
      element(Selectors.dashboardFirstCard).attr("href") mustBe "some-href"
    }

    "show the correct seconds dashboard card" in {
      elementText(Selectors.dashboardSecondCardHeading) mustBe "Tell us about changes to your property, rent or agreement"
      elementText(Selectors.dashboardSecondCardData) mustBe "You must tell us within 60 days of a change."
      elementText(Selectors.dashboardSecondCardData2) mustBe "Telling us will help us rate your property."
      element(Selectors.dashboardSecondCard).attr("href") mustBe "second-href"
    }

  }

}
