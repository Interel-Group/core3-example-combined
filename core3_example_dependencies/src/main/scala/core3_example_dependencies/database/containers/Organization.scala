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
package core3_example_dependencies.database.containers

import core3.database
import core3.database._
import core3.database.containers._
import core3.utils._
import core3.meta.containers._
import core3.meta.enums._

@WithBasicContainerDefinition
@WithJsonContainerDefinition
@WithSlickContainerDefinition
case class Organization(
  var name: String,
  var description: String,
  organizationType: Organization.OrganizationType,
  created: Timestamp,
  var updated: Timestamp,
  var updatedBy: String,
  id: ObjectID,
  var revision: RevisionID,
  var revisionNumber: RevisionSequenceNumber
) extends MutableContainer {
  override val objectType: ContainerType = "Organization"
}

object Organization {
  @DatabaseEnum
  sealed trait OrganizationType
  object OrganizationType {
    case object Client extends OrganizationType
    case object InternalTeam extends OrganizationType
  }

  trait BasicDefinition extends BasicContainerDefinition {
    override def matchCustomQuery(queryName: String, queryParams: Map[String, String], container: Container): Boolean = {
      queryName match {
        case "getByID" => queryParams("id") == container.asInstanceOf[Organization].id.toString
        case _ => throw new IllegalArgumentException(s"core3_example_dependencies.database.containers.Organization::matchCustomQuery > Query [$queryName] is not supported.")
      }
    }
  }

  def apply(
    name: String,
    description: String,
    organizationType: OrganizationType,
    createdBy: String
  ) = new Organization(
    name,
    description,
    organizationType,
    Time.getCurrentTimestamp,
    Time.getCurrentTimestamp,
    createdBy,
    database.getNewObjectID,
    database.getNewRevisionID,
    database.getInitialRevisionSequenceNumber
  )
}