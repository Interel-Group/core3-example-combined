/**
  * Copyright 2017 Interel
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package core3_example_dependencies.workflows.definitions

import core3.security.UserTokenBase
import core3.workflows._
import core3_example_dependencies.database.containers.Organization
import core3_example_dependencies.database.containers.Organization.OrganizationType
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

object CreateOrganization extends WorkflowBase {

  case class CreateOrganizationParameters(
    name: String,
    description: String,
    organizationType: OrganizationType,
  ) extends WorkflowParameters {
    override def asJson: JsValue = Json.obj(
      "name" -> name,
      "description" -> description,
      "organizationType" -> organizationType.toString
    )
  }

  override def name: String = "CreateOrganization"

  override def readOnly: Boolean = false

  override def parseParameters(rawParams: JsValue)(implicit ec: ExecutionContext): Future[WorkflowParameters] = {
    Future {
      CreateOrganizationParameters(
        (rawParams \ "name").as[String],
        (rawParams \ "description").as[String],
        (rawParams \ "organizationType").as[OrganizationType]
      )
    }
  }

  override def loadData(params: WorkflowParameters, queryHandlers: DataQueryHandlers)(implicit ec: ExecutionContext): Future[InputData] = {
    Future.successful(NoInputData())
  }

  override def executeAction(requestID: RequestID, user: UserTokenBase, params: WorkflowParameters, data: InputData)(implicit ec: ExecutionContext): Future[(WorkflowResult, OutputData)] = {
    params match {
      case actualParams: CreateOrganizationParameters =>
        val org = Organization(actualParams.name, actualParams.description, actualParams.organizationType, user.userID)

        Future.successful((WorkflowResult(wasSuccessful = true, requestID), OutputData(add = Vector(org))))
    }
  }
}

