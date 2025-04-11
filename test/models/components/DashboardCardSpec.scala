package models.components

import helpers.TestSupport
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.{CardTitle, Tag, Text}
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{Card, CardCaption, DashboardCard, Link}

class DashboardCardSpec extends TestSupport {

  "DashboardCard.card" should {
    "build a full Card with all optional values" in {

      val input = DashboardCard(
        titleKey = "home.yourAccountCard.title",
        captionKey =  Some("home.yourAccountCard.caption"),
        captionKey2 =  Some("home.yourAccountCard.caption2"),
        captionKey3 =  Some("home.yourAccountCard.caption3"),
        voaReference = Some("VOA176292C"),
        tag = Some("home.yourAccountCard.tag"),
        links = Some(
          Seq(
            Link(
              href       = Call(method = "GET",url = "some-href"),
              linkId     = "LinkId-Card",
              messageKey = "home.yourAccountCard.link1",
            )
          )
        )
      )

      val result: Card = DashboardCard.card(input)

      result.titleKey mustBe Some(CardTitle(Text("Your account")))
      result.captionKey mustBe Some(CardCaption(Text("Add and manage rating agents.")))
      result.captionKey2 mustBe Some(CardCaption(Text("Rating agents can deal with the VOA and your business rates on your behalf.")))
      result.captionKey3 mustBe Some(CardCaption(Text("Your client code is:")))
      result.voaReference mustBe Some(CardCaption(Text("VOA176292C")))
      result.tag mustBe Some(Tag(content = Text("Action needed")))
      result.links.map(_.links.head.linkId) mustBe Some("LinkId-Card")
      result.links.map(_.links.head.href) mustBe Some(Call(method = "GET",url = "some-href"))
      result.links.map(_.links.head.messageKey) mustBe Some("home.yourAccountCard.link1")
    }

    "build a Card with only mandatory values" in {

      val input = DashboardCard(titleKey = "home.yourAccountCard.title")

      val result: Card = DashboardCard.card(input)

      result.titleKey mustBe Some(CardTitle(Text("Your account")))
      result.captionKey mustBe None
      result.captionKey2 mustBe None
      result.captionKey3 mustBe None
      result.voaReference mustBe None
      result.tag mustBe None
      result.links mustBe None
    }
  }

}
