package uk.gov.hmrc.ngrdashboardfrontend.utils

import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.ngrdashboardfrontend.controllers.routes
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{Card, DashboardCard, Link}

object DashboardHelper {
  // for demonstration:
  private val dashboardYourProperty: DashboardCard = DashboardCard(
    titleKey = "home.propertiesCard.title",
    captionKey = Some("home.propertiesCard.caption"),
    captionKey2 = Some("home.propertiesCard.caption2"),
    captionKey3 = None,
    voaReference = None,
    tag = None,
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = routes.AddPropertyToYourAccountController.show.url.replace("/", "")),
          linkId = "LinkId-Card",
          messageKey = "home.propertiesCard.addProperty",
        )
      )
    )
  )

  private val dashboardCardChangeToProperty: DashboardCard = DashboardCard(
    titleKey = "home.reportChangeCard.title",
    captionKey = Some("home.reportChangeCard.caption"),
    captionKey2 = Some("home.reportChangeCard.caption2"),
    captionKey3 = None,
    voaReference = None,
    tag = None,
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = routes.SelectYourPropertyController.show.url.replace("/", "")),
          linkId = "LinkId-Card",
          messageKey = "home.reportChangeCard.link1",
        )
      )
    )
  )

  def getDashboardCards(isPropertyLinked: Boolean)(implicit messages: Messages): Seq[Card] = {
    val base = Seq(DashboardCard.card(dashboardYourProperty))
    if (isPropertyLinked) base :+ DashboardCard.card(dashboardCardChangeToProperty) else base

  }

}
