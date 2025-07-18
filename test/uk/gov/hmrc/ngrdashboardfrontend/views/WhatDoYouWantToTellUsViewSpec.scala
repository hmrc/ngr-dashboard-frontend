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
import uk.gov.hmrc.ngrdashboardfrontend.models.components.NavBarPageContents.CreateNavBar
import uk.gov.hmrc.ngrdashboardfrontend.models.components._
import uk.gov.hmrc.ngrdashboardfrontend.views.html.WhatDoYouWantToTellUsView

class WhatDoYouWantToTellUsViewSpec extends ViewBaseSpec {

  val view: WhatDoYouWantToTellUsView = app.injector.instanceOf[WhatDoYouWantToTellUsView]

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

    val backLink = "#main-content > div > div > div > a"
    val mainContent = "#main-content > div > div"
    val mainContentText = "What do you want to tell us?"
    val mainContentSubheading1 = "#main-content > div > div > h2:nth-child(4)"
    val mainContentSubheading2 = "#main-content > div > div > h2:nth-child(7)"
    val mainContentLink1 = "#main-content > div > div > p:nth-child(5) > a"
    val mainContentP1 = "#main-content > div > div > p:nth-child(8)"
    val mainContentP1Text = "#main-content > div > div > p:nth-child(8)"
    val mainContentLink2 = "#main-content > div > div > p:nth-child(9) > a"
    val mainContentP2 = "#main-content > div > div > p:nth-child(11)"
    val mainContentLink3 = "#main-content > div > div > p:nth-child(12) > a"
    val mainContentP3 = "#main-content > div > div > p:nth-child(14)"
    val mainContentLink4 = "#main-content > div > div > p:nth-child(15) > a"
  }

  "WhatDoYouWantToTellUsView" must {
    val whatDoYouWantToTellUsView = view(navigationBarContent = navBarContent)
    lazy implicit val document: Document = Jsoup.parse(whatDoYouWantToTellUsView.body)
    lazy val htmlF = view.f(navBarContent)
    lazy val htmlFOneLink = view.f(navBarContent)

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

    "show correct title" in {
      document.title() mustBe "What do you want to tell us? - Manage your business rates valuation - GOV.UK"
    }

    "show correct heading" in {
      elementText("h1") mustBe "What do you want to tell us?"
    }

    "show correct subheadings" in {
      elementText(Selectors.mainContentSubheading1) mustBe "Property"
      elementText(Selectors.mainContentSubheading2) mustBe "Rent agreement"
    }

    "show correct body text" in {
      elementText(Selectors.mainContentLink1) mustBe "You changed property features or use of space"
      elementText(Selectors.mainContentP1) mustBe "You have moved into a property and have a new agreement to rent it. You may have signed a lease or licence, or agreed it verbally. You will still have an agreement even if you do not pay rent."
      elementText(Selectors.mainContentLink2) mustBe "You have a new agreement"
      elementText(Selectors.mainContentP2) mustBe "Your agreement to rent a property has ended and you have a new agreement to keep renting it. You may have signed a lease or licence, or agreed it verbally."
      elementText(Selectors.mainContentLink3) mustBe "You renewed your agreement"
      elementText(Selectors.mainContentP3) mustBe "You have agreed a new rent on the date set out in your agreement."
      elementText(Selectors.mainContentLink4) mustBe "You reviewed your rent"
    }

    "show correct back button" in {
      elementText(Selectors.backLink) mustBe "Back"
    }

  }

}
