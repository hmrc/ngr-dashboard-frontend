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

import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.ngrdashboardfrontend.controllers.routes
import uk.gov.hmrc.ngrdashboardfrontend.models.Status
import uk.gov.hmrc.ngrdashboardfrontend.models.Status.{Approved, Pending, Rejected}
import uk.gov.hmrc.ngrdashboardfrontend.models.components.{Card, DashboardCard, Link}

object DashboardHelper {
  // for demonstration:
  private val dashboardAddProperty: DashboardCard = DashboardCard(
    titleKey = "home.addPropertiesCard.title",
    captionKey = Some("home.addPropertiesCard.caption"),
    captionKey2 = Some("home.addPropertiesCard.caption2"),
    captionKey3 = None,
    voaReference = None,
    tag = Some("nav.actionNeeded"),
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = routes.AddPropertyToYourAccountController.show.url),
          linkId = "LinkId-Card",
          messageKey = "home.addPropertiesCard.addProperty",
        )
      )
    )
  )

  private val dashboardYourProperty: DashboardCard = DashboardCard(
    titleKey = "home.yourPropertiesCard.title",
    captionKey = Some("home.yourPropertiesCard.caption"),
    captionKey2 = None,
    captionKey3 = None,
    voaReference = None,
    tag = None,
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = routes.PropertyController.show.url),//TODO
          linkId = "LinkId1-Card",
          messageKey = "home.yourPropertiesCard.link.1",
        ),
        Link(
          href = Call(method = "GET", url = routes.SelectPropertyController.show.url),
          linkId = "LinkId2-Card",
          messageKey = "home.yourPropertiesCard.link.2",
        )
      )
    )
  )

  private val dashboardPendingPropertyLink: DashboardCard = DashboardCard(
    titleKey = "home.yourPropertiesCard.title",
    captionKey = Some("home.yourPropertiesCard.caption"),
    captionKey2 = None,
    captionKey3 = None,
    voaReference = None,
    tag = None,
    links = Some(
      Seq(
        Link(
          href = Call(method = "GET", url = routes.PropertyController.show.url),//TODO
          linkId = "LinkId1-Card",
          messageKey = "home.yourPropertiesCard.link.1",
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
          href = Call(method = "GET", url = routes.SelectYourPropertyController.show.url),
          linkId = "LinkId-Card",
          messageKey = "home.reportChangeCard.link1",
        )
      )
    )
  )

  def getDashboardCards(isPropertyLinked: Boolean, status: Status)(implicit messages: Messages): Seq[Card] = {
    val addProperty = Seq(DashboardCard.card(dashboardAddProperty))
    println("isPropertyLinked=========== " + isPropertyLinked)
    if (isPropertyLinked) {
      status match {
        case Approved => Seq(
          DashboardCard.card(dashboardYourProperty),
          DashboardCard.card(dashboardCardChangeToProperty)
        )
        case Pending => Seq(DashboardCard.card(dashboardPendingPropertyLink))
        case Rejected => Seq(DashboardCard.card(dashboardPendingPropertyLink))
      }
    } else addProperty
  }
}
