# core3 - Example Workflow Engine + UI

Example Scala Play 2.6 app showcasing a way to build a combined backend and frontend service with [core3](https://github.com/Interel-Group/core3).

## Getting Started
* Clone repo
* Get Redis ([example setup](https://github.com/Interel-Group/core3/wiki/Example-Redis-Setup-(Ubuntu)))
* Add ```static.conf``` (see the [reference config](conf/static_ref.conf) for more info)
* ```sbt run -Dhttps.port=<some local port> -Dhttp.port=disabled -Dc3ec.console=enabled```

> For a split engine/UI implementation, see [core3-example-engine](https://github.com/Interel-Group/core3-example-engine) and [core3-example-ui](https://github.com/Interel-Group/core3-example-ui).

## Supported data sources
* [Redis](https://redis.io/) (tested on 3.2.5, 3.2.8)

## Supported auth providers
* [Local](https://github.com/Interel-Group/core3/wiki) - local credentials DB

## Deployment

[Deploying a Play 2.6 application](https://www.playframework.com/documentation/2.6.x/Production)

## Testing
Only one test is provided and it is used to create a few test users. See the [spec](test/core3_example_combined/test/ExampleInitSpec.scala) for details.

```
sbt test
```
Required options:
```
-Dserver.static.database.redis.secret=<some password>
```

## Interesting files
* [app/Module](app/Module.scala) - component setup
* [conf/routes](conf/routes) - routing config
* [app/ConsoleStart](app/ConsoleStart.scala) - enables the system management console
* [app/controllers/System](app/controllers/System.scala) - Example actions for handling login, logout, public and authorized pages.
* [views](app/views) - Twirl HTML templates
* [public/js/main.js](public/js/main.js) - RequireJS init
* [public/js/core3_example_ui](public/js/core3_example_ui) - client-side code

## Built With
* Scala 2.12.3
* sbt 0.13.16
* [core3](https://github.com/Interel-Group/core3) - Core framework
* [rediscala](https://github.com/etaty/rediscala) - Redis data layer support
* [jQuery](https://jquery.com/)
* [RequireJS](http://requirejs.org/)
* [UIkit](https://getuikit.com/)

## Versioning
We use [SemVer](http://semver.org/) for versioning.

## License
This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file for details

> Copyright 2017 Interel
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
> http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
