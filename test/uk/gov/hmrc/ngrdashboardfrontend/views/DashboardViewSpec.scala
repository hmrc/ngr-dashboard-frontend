package uk.gov.hmrc.ngrdashboardfrontend.views

import helpers.ViewBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import uk.gov.hmrc.ngrdashboardfrontend.models.components.NavBarPageContents.{CreateNavBar, Selected}
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{DashboardCard, Link, NavBarContents, NavBarCurrentPage, NavigationBarContent}
import uk.gov.hmrc.ngrdashboardfrontend.views.html.DashboardView

class DashboardViewSpec extends ViewBaseSpec {

  val view: DashboardView = app.injector.instanceOf[DashboardView]

  val dashboardCard: DashboardCard = DashboardCard(
    titleKey = "home.propertiesCard.title",
    captionKey = Some("home.propertiesCard.caption"),
    captionKey2 = Some("home.propertiesCard.caption2"),
    captionKey3 = None,
    voaReference = None,
    tag = Some("home.propertiesCard.tag"),
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = "some-href"),
          linkId = "LinkId-Card",
          messageKey = "home.propertiesCard.addProperty",
        ),
        Link(
          href = Call(method = "GET", url = "some-href"),
          linkId = "LinkId2-Card",
          messageKey = "home.propertiesCard.manageProperties",
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
  }

  "Dashboard view" must {
    val dashboardView = view(cards = Seq(DashboardCard.card(dashboardCard)), name = "Greg", navigationBarContent = navBarContent)
    lazy implicit val document: Document = Jsoup.parse(dashboardView.body)
    lazy val htmlF = view.f(Seq(DashboardCard.card(dashboardCard)), "Greg", navBarContent)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
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

  }

}
