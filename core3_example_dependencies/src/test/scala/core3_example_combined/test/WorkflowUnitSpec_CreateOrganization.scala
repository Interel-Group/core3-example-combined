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
package core3_example_combined.test

import akka.actor.ActorRef
import akka.pattern.ask
import core3.database.dals.DatabaseAbstractionLayer
import core3.database.dals.Core.{BuildAllDatabases, ClearAllDatabases, VerifyAllDatabases}
import core3.security.Auth0UserToken
import core3.test.fixtures.TestSystem
import core3.workflows.WorkflowEngineComponent.ExecuteWorkflow
import core3.workflows.WorkflowResult
import core3_example_dependencies.database.containers.Organization
import core3_example_dependencies.database.containers.Organization.OrganizationType
import core3_example_dependencies.workflows.definitions.CreateOrganization
import play.api.libs.json.Json

import scala.concurrent.Await
import scala.concurrent.duration._

class WorkflowUnitSpec_CreateOrganization extends AsyncUnitSpec {
  implicit private val ec = TestSystem.ec
  implicit private val system = TestSystem.system
  implicit private val timeout = TestSystem.timeout

  private val workflows = Vector(CreateOrganization)
  private val authorizedUser = core3.test.fixtures.Workflows.createAuthorizedUser(workflows)
  private val db = fixtures.Database.createCoreInstance()
  private val engine = core3.test.fixtures.Workflows.createWorkflowEngine(db, workflows, fixtures.Database.defaultDefinitions, readOnlyTransactionLogsEnabled = false)

  case class FixtureParam(engine: ActorRef, db: DatabaseAbstractionLayer, authorizedUser: Auth0UserToken)
  def withFixture(test: OneArgAsyncTest) = {
    Await.result(
      for {
        _ <- db.getRef ? ClearAllDatabases(ignoreErrors = true)
        _ <- db.getRef ? BuildAllDatabases()
        _ <- db.getRef ? VerifyAllDatabases()
      } yield {
        true
      },
      atMost = 15.seconds
    )

    val fixture = FixtureParam(engine, db, authorizedUser)
    withFixture(test.toNoArgAsyncTest(fixture))
  }

  "A CreateOrganization workflow" should "successfully create organizations" in {
    fixture =>
      val testName = "test name"
      val testDescription = "test description"
      val testOrgType = OrganizationType.InternalTeam

      for {
        result1 <- (fixture.engine ? ExecuteWorkflow(
          CreateOrganization.name,
          rawParams = Json.obj(
            "name" -> testName,
            "description" -> testDescription,
            "organizationType" -> testOrgType.toString
          ),
          fixture.authorizedUser
        )).mapTo[WorkflowResult]
        result2 <- (fixture.engine ? ExecuteWorkflow(
          CreateOrganization.name,
          rawParams = Json.obj(
            "name" -> "other name",
            "description" -> testDescription,
            "organizationType" -> testOrgType.toString
          ),
          fixture.authorizedUser
        )).mapTo[WorkflowResult]
        organizations <- fixture.db.queryDatabase("Organization").map(_.map(_.asInstanceOf[Organization]))
      } yield {
        result1.wasSuccessful should be(true)
        result2.wasSuccessful should be(true)
        organizations should have size 2
      }
  }
}
