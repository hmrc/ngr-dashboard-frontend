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
import play.twirl.api.Html
import uk.gov.hmrc.ngrdashboardfrontend.views.html.Layout

class LayoutSpec extends ViewBaseSpec {

  val injectedView: Layout = injector.instanceOf[Layout]
  val navTitle = "ngr-dashboard-frontend"
  val backLink = "Back"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
    val languageSelector = "#main-content > div > div > nav > ul > li:nth-child(1) > span"
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.welshLanguageSupportEnabled(false)
  }


  "The Layout template" when {

    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply(pageTitle = Some("Title of page"), showBackLink = false)(Html("Test")).body
      val htmlRender = injectedView.render(pageTitle = Some("Title of page"), showBackLink = false, contentBlock = Html("Test"), request = request, messages = messages, appConfig = mockConfig, fullWidth = false).body
      val htmlF = injectedView.f(Some("Title of page"), false, false)(Html("Test"))(request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      htmlF must not be empty
    }

    "injected into the view" should {

      "show the nav title" in {
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.navTitle) mustBe navTitle
      }

      "should not display the language selector" in {
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementExtinct(Selectors.languageSelector)
      }

      "the language selector feature switch is turned on" in {
        mockConfig.features.welshLanguageSupportEnabled(true)
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,  mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.languageSelector) mustBe "English"
        mockConfig.features.welshLanguageSupportEnabled(false)
      }
    }
  }

}