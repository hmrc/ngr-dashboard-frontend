package uk.gov.hmrc.ngrdashboardfrontend.controllers

import helpers.ControllerSpecSupport
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.ngrdashboardfrontend.views.html.WhatYouNeedView

class WhatYouNeedControllerSpec extends ControllerSpecSupport {
  val pageTitle = "What you need"
  val view: WhatYouNeedView = inject[WhatYouNeedView]
  val controller: WhatYouNeedController = new WhatYouNeedController(authenticate = mockAuthJourney, isRegisteredCheck = mockIsRegisteredCheck, view = view, mcc = mcc)

  "What you need controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller.show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Return OK and the correct view" in {
        val result = controller.next()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.DashboardController.show.url)
      }
    }
  }
}
