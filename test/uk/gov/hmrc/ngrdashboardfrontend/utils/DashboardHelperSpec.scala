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

package uk.gov.hmrc.ngrdashboardfrontend.utils

import helpers.TestSupport
import uk.gov.hmrc.ngrdashboardfrontend.controllers.routes
import uk.gov.hmrc.govukfrontend.views.Aliases.{CardTitle, Text}
import uk.gov.hmrc.ngrdashboardfrontend.models.components.Link

class DashboardHelperSpec extends TestSupport {

  "DashboardHelper" should {
    "return the correct dashboard cards when property is linked" in {
      // Setup test data and expectations

      val cards = DashboardHelper.getDashboardCards(isPropertyLinked = true)

      assert(cards.size == 2)
      assert(cards(0).titleKey.contains(CardTitle(Text("Your property"), None, "")))
      assert(cards(1).titleKey.contains(CardTitle(Text("Tell us about changes to your property, rent or agreement"), None, "")))
    }

    "return the correct dashboard cards when property is not linked" in {

      val cards = DashboardHelper.getDashboardCards(isPropertyLinked = false)

      assert(cards.size == 1)
      assert(cards.head.titleKey.contains(CardTitle(Text("Add a property"), None, "")))
    }
    "return the correct links on the 'Your property' card when property is linked" in {
      val cards = DashboardHelper.getDashboardCards(isPropertyLinked = true)
      val yourProperty = cards.head
      val links: Seq[Link] = yourProperty.links.map(_.links).getOrElse(Seq.empty)
      val link1 = links.head
      val link2 = links(1)

      assert(links.size == 2)
      assert(link1.linkId == "LinkId1-Card")
      assert(link1.href.url == routes.PropertyController.show.url)
      assert(link2.linkId == "LinkId2-Card")
      assert(link2.href.url == routes.SelectYourPropertyController.show.url)
    }

  }

}
