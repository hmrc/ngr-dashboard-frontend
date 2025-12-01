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

package uk.gov.hmrc.ngrdashboardfrontend.controllers

import helpers.{ControllerSpecSupport, TestData}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{await, contentAsString, status}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrdashboardfrontend.actions.CredIdValidationFilter
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.models.registration.CredId
import uk.gov.hmrc.ngrdashboardfrontend.views.html.WhatDoYouWantToTellUsView

import scala.concurrent.Future

class WhatDoYouWantToTellUsControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout with TestData {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  val pageTitle = "What do you want to tell us?"
  lazy val frontendAppConfig: AppConfig = inject[AppConfig]
  lazy val whatDoYouWantToTellUsView: WhatDoYouWantToTellUsView = inject[WhatDoYouWantToTellUsView]
  lazy val credIdValidationFilter: CredIdValidationFilter = inject[CredIdValidationFilter]

  def controller() = new WhatDoYouWantToTellUsController(
    mockAuthJourney,
    mockHasLinkedProperties,
    mockNGRConnector,
    whatDoYouWantToTellUsView,
    credIdValidationFilter,
    mcc
  )(ec, mockConfig)

  "WhatDoYouWantToTellUsController" must {
    "render 'what do you want to tell us' page" in {
      mockLinkedPropertiesRequest(hasCredId = true)
      when(mockNGRConnector.getLinkedProperty(any[CredId])(any())).thenReturn(Future.successful(Some(property)))
      val result = controller().show("2191322564521")(authenticatedFakeRequest)
      status(result) mustBe OK
      contentAsString(result) must include(pageTitle)
    }
    "Throw exception when no property linking is found" in {
      mockLinkedPropertiesRequest(hasCredId = true)
      when(mockNGRConnector.getLinkedProperty(any[CredId])(any())).thenReturn(Future.successful(None))
      val exception = intercept[NotFoundException] {
        await(controller().show("2191322564521")(authenticatedFakeRequest))
      }
      exception.getMessage contains "Unable to find match Linked Properties" mustBe true
    }
    "return a bad request when credId is missing" in {
      mockLinkedPropertiesRequest()
      when(mockNGRConnector.getLinkedProperty(any[CredId])(any())).thenReturn(Future.successful(Some(property)))
      val result = controller().show("2191322564521")(authenticatedFakeRequest)
      status(result) mustBe BAD_REQUEST
      val content = contentAsString(result)
      content mustNot include(pageTitle)
      content must include("Missing credId in request")
    }
  }
}
