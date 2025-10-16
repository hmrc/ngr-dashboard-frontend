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
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{NavBarPageContents, NavigationBarContent, TableData, TableHeader}
import uk.gov.hmrc.ngrdashboardfrontend.views.html.SelectYourPropertyView

class SelectYourPropertyViewSpec extends ViewBaseSpec {
  lazy val view: SelectYourPropertyView = inject[SelectYourPropertyView]
  val title = "Which property do you want to tell us about? - GOV.UK"
  val heading = "Which property do you want to tell us about?"
  val caption = "Your properties"
  val tableHeader1 = "Address"
  val tableHeader2 = "Property reference"
  val tableHeader3 = "Status"

  val content: NavigationBarContent = NavBarPageContents.createDefaultNavBar

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div > h1"
    val caption = "#main-content > div > div > table > caption"
    val tableHeader1 = "#main-content > div > div > table > thead > tr > th:nth-child(1)"
    val tableHeader2 = "#main-content > div > div > table > thead > tr > th:nth-child(2)"
    val tableHeader3 = "#main-content > div > div > table > thead > tr > th:nth-child(3)"
  }

  val table: Table =
    TableData(
      headers = Seq(
        TableHeader("Address", "govuk-table__caption--s govuk-!-width-one-quarter"),
        TableHeader("Property reference", "govuk-table__caption--s govuk-!-width-one-quarter"),
        TableHeader("Status", "govuk-table__caption--s govuk-!-width-one-quarter"),
        TableHeader("", "")),
      rows = Seq.empty,
      caption = Some(messages("selectYourProperty.table.caption"))
    ).toTable

  "SelectYourPropertyView" must {
    val selectYourPropertyView = view(content, table)
    implicit val document: Document = Jsoup.parse(selectYourPropertyView.body)
    val htmlApply = view.apply(content, table).body
    val htmlRender = view.render(content, table, request, messages, mockConfig).body

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

    "show correct caption" in {
      elementText(Selectors.caption) mustBe caption
    }

    "show correct table header1" in {
      elementText(Selectors.tableHeader1) mustBe tableHeader1
    }

    "show correct table header2" in {
      elementText(Selectors.tableHeader2) mustBe tableHeader2
    }

    "show correct table header3" in {
      elementText(Selectors.tableHeader3) mustBe tableHeader3
    }
  }
}
