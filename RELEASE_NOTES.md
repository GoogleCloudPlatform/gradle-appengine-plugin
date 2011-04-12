### Version 0.3 (April 12, 2011)

* Support for running local development server as daemon - [Issue 1](https://github.com/bmuschko/gradle-gae-plugin/issues#issue/1).
* Allow providing password via gradle.properties or convention property - [Issue 2](https://github.com/bmuschko/gradle-gae-plugin/issues#issue/2).

### Version 0.2 (March 18, 2011)

* Added new task `gaeExplodeWar`.
* Cut dependency to exploded WAR directory for `gaeVersion` task.
* Task `gaeRun` will use exploded WAR directory by default. This behavior can be overriden by the convention property
`warDir`.

### Version 0.1 (March 17, 2011)

* Initial release.