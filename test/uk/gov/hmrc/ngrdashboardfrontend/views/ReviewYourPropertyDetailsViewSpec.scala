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
import uk.gov.hmrc.ngrdashboardfrontend.views.html.{BeforeYouGoView, ReviewYourPropertyDetailsView}

class ReviewYourPropertyDetailsViewSpec extends ViewBaseSpec {
  lazy val view: ReviewYourPropertyDetailsView = inject[ReviewYourPropertyDetailsView]
  val title = "Review your property details - GOV.UK"
  val heading = "Review your property details"

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div > h1"
  }

  "ReviewYourPropertyDetailsView" must {
    val reviewYourPropertyDetailsView = view("/ngr-property-linking-frontend/review-your-property-details")
    lazy implicit val document: Document = Jsoup.parse(reviewYourPropertyDetailsView.body)
    val htmlApply = view.apply("/ngr-property-linking-frontend/review-your-property-details").body
    val htmlRender = view.render("/ngr-property-linking-frontend/review-your-property-details", request, messages, mockConfig).body
    lazy val htmlF = view.f("/ngr-property-linking-frontend/review-your-property-details")

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
  }

}
