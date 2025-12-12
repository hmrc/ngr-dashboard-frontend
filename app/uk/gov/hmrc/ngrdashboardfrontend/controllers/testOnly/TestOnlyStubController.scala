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

package uk.gov.hmrc.ngrdashboardfrontend.controllers.testOnly

import play.api.mvc._
import uk.gov.hmrc.ngrdashboardfrontend.connector.testOnly.NGRStubConnector
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class TestOnlyStubController @Inject()(
                                        mcc: MessagesControllerComponents,
                                        stubConnector: NGRStubConnector
                                      )(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def populateAllStubData(): Action[AnyContent] = Action.async { implicit request =>
    stubConnector.removeAllStubData()
      .flatMap(_ => stubConnector.populateAllStubData())
      .map { response =>
        if (response.status == OK) Ok("All stub data added successfully.\n")
        else InternalServerError(s"Unexpected response: ${response.status}")
      }
      .recover { case ex => InternalServerError(s"Failed to populate all stub data: ${ex.getMessage}") }
  }
}
