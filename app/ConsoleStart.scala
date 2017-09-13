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
import javax.inject.{Inject, Singleton}

import core3.core.cli.LocalConsole
import play.api.Environment

import scala.concurrent.{ExecutionContext, Future}

trait ConsoleStart

@Singleton
class ConsoleStartImpl @Inject()(console: Option[LocalConsole])(implicit environment: Environment, ec: ExecutionContext) extends ConsoleStart {
  Future {
    console.foreach(_.start())
  }
}