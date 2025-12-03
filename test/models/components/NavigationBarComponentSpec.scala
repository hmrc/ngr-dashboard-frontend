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

package models.components

import helpers.ViewBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrdashboardfrontend.views.html.components.navigationBarComponent

class NavigationBarComponentSpec extends ViewBaseSpec {
  val injectedView: navigationBarComponent = injector.instanceOf[navigationBarComponent]

  val content: NavigationBarContent = NavBarPageContents.CreateNavBar(
    contents = NavBarContents(
      homePage = Some(true),
      messagesPage = Some(false),
      profileAndSettingsPage = Some(false),
      signOutPage = Some(true)
    ),
    currentPage = NavBarCurrentPage(homePage = true),
    notifications = Some(1)
  )

  val backLine = "Back"

  object Selectors {
    val backLine = " div > a"
  }

  "The Nav Bar template" when {
    "navigation bar should render correctly" in {
      injectedView.f(content, false, None)(request, messages).toString() must not be empty
      injectedView.render(content, false, None, request, messages).toString() must not be empty
    }

    "back link should be created when showBackLine sets to true" in {
      val view = injectedView(content, true, None)(request, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      elementText(Selectors.backLine) mustBe backLine
    }

    "back link should be missing when showBackLine sets to false" in {
      val htmlReader = injectedView.render(content, false, None, request, messages).toString()

      htmlReader contains "<a href=\"#\" class=\"govuk-back-link\" data-module=\"hmrc-back-link\">Back</a>" shouldBe false
    }
  }
}
