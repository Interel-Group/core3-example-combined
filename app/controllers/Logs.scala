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
package controllers

import javax.inject.Inject

import containers.UserData
import core3.config.StaticConfig
import core3.database.containers.core
import core3.database.dals.DatabaseAbstractionLayer
import core3.http.controllers.local.ClientController
import core3.http.requests.WorkflowEngineConnection
import core3.workflows.definitions._
import core3.workflows.{NoWorkflowParameters, WorkflowEngine}
import play.api.Environment
import play.api.cache.SyncCacheApi

import scala.concurrent.ExecutionContext

class Logs @Inject()(engine: WorkflowEngine, cache: SyncCacheApi, db: DatabaseAbstractionLayer)
  (implicit ec: ExecutionContext, environment: Environment)
  extends ClientController(cache, StaticConfig.get.getConfig("security.authentication.clients.LocalCombinedExample"), db) {

  def page() = AuthorizedAction(
    "c3eu:view",
    okHandler = { (request, user) =>
      implicit val r = request
      val userData = UserData(user)

      for {
        result <- engine.executeWorkflow(
          SystemQueryTransactionLogs.name,
          NoWorkflowParameters().asJson,
          user
        )
      } yield {
        if (result.wasSuccessful) {
          val logs = result.data.map {
            output =>
              (output \ "logs").as[Vector[core.TransactionLog]]
          }.getOrElse(Seq.empty).sortWith(
            (a, b) =>
              a.timestamp.isAfter(b.timestamp)
          )

          Ok(views.html.logs.page("Transaction Logs", userData, logs))
        } else {
          Ok(views.html.system.error("Transaction Logs", result.message.getOrElse("No message provided"), Some(userData)))
        }
      }
    }
  )
}
