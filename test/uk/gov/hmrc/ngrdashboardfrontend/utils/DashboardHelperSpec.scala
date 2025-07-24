package uk.gov.hmrc.ngrdashboardfrontend.utils

import helpers.TestSupport
import uk.gov.hmrc.govukfrontend.views.Aliases.{CardTitle, Text}

class DashboardHelperSpec extends TestSupport {

  "DashboardHelper" should {
    "return the correct dashboard cards when property is linked" in {
      // Setup test data and expectations

      val cards = DashboardHelper.getDashboardCards(isPropertyLinked = true)

      assert(cards.size == 2)
      assert(cards.head.titleKey.contains(CardTitle(Text("Add a property"), None, "")))
      assert(cards(1).titleKey.contains(CardTitle(Text("Tell us about changes to your property, rent or agreement"), None, "")))
    }

    "return the correct dashboard cards when property is not linked" in {

      val cards = DashboardHelper.getDashboardCards(isPropertyLinked = false)

      assert(cards.size == 1)
      assert(cards.head.titleKey.contains(CardTitle(Text("Add a property"), None, "")))
    }
  }

}
