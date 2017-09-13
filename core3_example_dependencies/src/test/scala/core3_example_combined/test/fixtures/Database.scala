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
package core3_example_combined.test.fixtures

import core3.database.ContainerType
import core3.database.containers._
import core3.database.dals.DatabaseAbstractionLayer
import core3_example_dependencies.database.containers.Organization

object Database {
  val groupDefinitions =            new core.Group.BasicDefinition          with core.Group.JsonDefinition
  val transactionLogDefinitions =   new core.TransactionLog.BasicDefinition with core.TransactionLog.JsonDefinition
  val localUserDefinitions =        new core.LocalUser.BasicDefinition      with core.LocalUser.JsonDefinition
  val organizationDefinitions =     new Organization.BasicDefinition        with Organization.JsonDefinition

  val defaultDefinitions: Map[ContainerType, BasicContainerDefinition with JsonContainerDefinition] =
    Map(
      "Group"             -> groupDefinitions,
      "TransactionLog"    -> transactionLogDefinitions,
      "LocalUser"         -> localUserDefinitions,
      "Organization"      -> organizationDefinitions
    )

  def createCoreInstance(wipeData: Boolean = true): DatabaseAbstractionLayer = {
    val redisDAL: DatabaseAbstractionLayer = core3.test.fixtures.Database.createRedisInstance(defaultDefinitions)

    val masterMap = Map(
      "Group"             -> Vector(redisDAL.getRef),
      "TransactionLog"    -> Vector(redisDAL.getRef),
      "LocalUser"         -> Vector(redisDAL.getRef),
      "Organization"      -> Vector(redisDAL.getRef)
    )

    core3.test.fixtures.Database.createCoreInstance(Some(masterMap), wipeData)
  }
}
