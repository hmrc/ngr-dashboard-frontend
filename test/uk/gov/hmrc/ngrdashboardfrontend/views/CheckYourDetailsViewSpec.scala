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
import uk.gov.hmrc.ngrdashboardfrontend.controllers.routes
import uk.gov.hmrc.ngrdashboardfrontend.views.html.CheckYourDetailsView

class CheckYourDetailsViewSpec extends ViewBaseSpec {
  lazy val view: CheckYourDetailsView = inject[CheckYourDetailsView]
  val title = "We are checking your details - GOV.UK"
  val heading = "We are checking your details"
  val returnHomeHref = "Return to account home"
  val body1 = "Your information has been submitted."

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div > h1"
    val returnHomeHref = "#main-content > div > div > p:nth-child(3) > a"
    val body1 = "#main-content > div > div > p:nth-child(2)"
  }

  "CheckYourDetailsView" must {
    val checkYourDetailsView = view(routes.DashboardController.show.url)
    lazy implicit val document: Document = Jsoup.parse(checkYourDetailsView.body)
    val htmlApply = view.apply(routes.DashboardController.show.url).body
    val htmlRender = view.render(routes.DashboardController.show.url, request, messages, mockConfig).body
    lazy val htmlF = view.f(routes.DashboardController.show.url)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "apply must be the same as render" in {
      htmlApply mustBe htmlRender
    }

    "render is not empty" in {
      htmlRender must not be empty
    }

    "show correct title" in {
      elementText(Selectors.navTitle) mustBe title
    }

    "show correct heading" in {
      elementText(Selectors.heading) mustBe heading
    }

    "show correct return to account home href" in {
      elementText(Selectors.returnHomeHref) mustBe returnHomeHref
    }

    "show correct body1" in {
      elementText(Selectors.body1) mustBe body1
    }
  }

}
