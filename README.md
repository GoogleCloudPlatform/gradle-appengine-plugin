# Gradle GAE plugin

![Google App Engine Logo](https://developers.google.com/appengine/images/appengine_lowres.png)

The plugin provides tasks for uploading, downloading, running and managing [Google App Engine](http://code.google.com/appengine/)
(GAE) projects in any given Gradle build. It extends the War plugin.

## Usage

To use the GAE plugin, include in your build script:

    apply plugin: 'gae'

The plugin JAR needs to be defined in the classpath of your build script. It is directly available on
[Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.gradle.api.plugins%22%20AND%20a%3A%22gradle-gae-plugin%22).
Alternatively, you can download it from GitHub and deploy it to your local repository. The following code snippet shows an
example on how to retrieve it from Maven Central:

    buildscript {
        repositories {
            mavenCentral()
        }

        dependencies {
            classpath 'org.gradle.api.plugins:gradle-gae-plugin:0.7.6'
        }
    }

*Note:* The plugin requires you to set the environment variable _APPENGINE_HOME_ or the system property _google.appengine.sdk_
pointing to your current Google App Engine SDK installation. In case you have both variables set the system property takes
precedence over the environment variable. Alternatively, you can choose to automatically download the SDK by setting the
convention property `downloadSdk` to `true`. This option requires you to specify the SDK version you want to use by setting
the configuration `gaeSdk`.

    dependencies {
        gaeSdk 'com.google.appengine:appengine-java-sdk:1.6.6'
    }

## Tasks

The GAE plugin defines the following tasks:

* `gaeConfigureBackends`: Dynamically updates settings in `backends.xml` without having to stop the backend. The setting is defined by the project property `setting`.
* `gaeCronInfo`: Verifies and prints the scheduled task (cron) configuration.
* `gaeDeleteBackend`: Deletes the indicated backend. The backend is defined by the project property `backend`.
* `gaeDownloadApp`: Retrieves the most current version of your application.
* `gaeDownloadSdk`: Downloads and sets Google App Engine SDK.
* `gaeEnhance`: Enhances DataNucleus classes by using byte-code manipulation to make your normal Java classes "persistable".
* `gaeExplodeWar`: Extends the `war` task to generate WAR file and explodes the artifact into `build/exploded-war`.
* `gaeFunctionalTest`: Runs the tests from `functionalTest` source set against a local development server started in daemon mode.
* `gaeListBackends`: Lists all the backends configured for the app specified in `appengine-web.xml`.
* `gaeLogs`: Retrieves log data for the application running on App Engine.
* `gaeRollback`: Undoes a partially completed update for the given application.
* `gaeRollbackBackend`: Rolls back a backend update that was interrupted by the user or stopped due to a configuration error. The backend is defined by the project property `backend`.
* `gaeRun`: Starts a local development server running your project code. By default the WAR file is created, exploded and used as
web application directory each time you run this task. This behavior can be changed by setting the convention property
`warDir`.
* `gaeStartBackend`: Sets the backend state to `START`, allowing it to receive HTTP requests. The backend is defined by the project property `backend`.
* `gaeStop`: Stops the local development server.
* `gaeStopBackend`: Sets the backend state to `STOP` and shuts down any running instances. The backend is defined by the project property `backend`.
* `gaeUpdateAllBackends`: Creates or updates all backends configured in `backends.xml`.
* `gaeUpdateBackend`: Creates or updates backend configured in `backends.xml`. The backend is defined by the project property `backend`.
* `gaeUpdateCron`: Updates the schedule task (cron) configuration for the app, based on the cron.xml file.
* `gaeUpdateDos`: Updates the DoS protection configuration for the app, based on the dos.xml file.
* `gaeUpdateIndexes`: Updates datastore indexes in App Engine to include newly added indexes.
* `gaeUpdateQueues`: Updates the task queue configuration (queue.xml) in App Engine.
* `gaeUpload`: Uploads files for an application given the application's root directory. The application ID and version are taken from the appengine-web.xml file.
* `gaeUploadAll`: Uploads your application to App Engine and updates all backends by running the task `gaeUpload` and `gaeUpdateAllBackends`.
* `gaeVacuumIndexes`: Deletes unused indexes in App Engine server.
* `gaeVersion`: Prints detailed version information about the SDK, Java and the operating system.

## Project layout

The GAE plugin uses the same layout as the [Gradle War plugin](http://gradle.org/docs/current/userguide/war_plugin.html). The only difference is the addition of the `functionalTest` source set
(located in `src/functionalTest` by default) which is used by the `gaeFunctionalTest` task.

## Convention properties

The GAE plugin defines the following convention properties in the `gae` closure:

* `httpPort`: The TCP port which local development server should listen for HTTP requests on (defaults to 8080).
* `stopPort`: The TCP port which local development server should listen for admin requests on (defaults to 8081).
* `stopKey`: The key to pass to local development server when requesting it to stop (defaults to null).
* `daemon`: Specifies whether the local development server should run in the background. When true, this task completes as
soon as the server has started. When false, this task blocks until the local development server is stopped (defaults to false).
* `warDir`: Web application directory used for local development server (defaults to `build/exploded-war`).
* `disableUpdateCheck`: Disables the Google App Engine update check if set to true.
* `jvmFlags`: The JVM flags to pass on to the local development server. The data type is a `List`.
* `downloadSdk`: Downloads the Google App Engine SDK defined by the configuration name `gaeSdk` and explodes the artifact into
`~/.gradle/gae-sdk` (defaults to false). If set to `true` the given SDK is used for running all plugin tasks which
takes precedence over _APPENGINE_HOME_ and the system property _google.appengine.sdk_.

Within `gae` you can define optional properties in a closure named `appcfg`:

* `email`: The email address of the Google account of an administrator for the application, for actions that require signing in.
If omitted and no cookie is stored from a previous use of the command, the command will prompt for this value.
* `server`: The App Engine server hostname (defaults to appengine.google.com).
* `host`: The hostname of the local machine for use with remote procedure calls.
* `noCookies`: Do not store the administrator sign-in credentials. Prompt for a password every time. (or go through the OAuth2 flow when the `oauth2` option is used).
* `passIn`: Do not store the administrator sign-in credentials as a cookie; prompt for a password every time. If the property
`password` was provided then this value will always be true.
* `password`: The password in plain text to be used whenever a task requires one. The password is only applied if the `email`
convention property was provided also. Alternatively, you can set the password in your `gradle.properties` via the property
`gaePassword`. The password in `gradle.properties` takes precedence over the one set in this convention property.
* `httpProxy`: Use the given HTTP proxy to contact App Engine.
* `httpsProxy`: Use the given HTTPS proxy to contact App Engine, when using HTTPS. If `httpProxy` is given but `httpsProxy`
is not, both HTTP and HTTPS requests will use the given proxy.
* `oauth2`: Use OAuth2 authentication instead of password-based authentication.

The task `gaeDownloadApp` requires you to at least define the application ID and directory to write the files to. Define the tasks' properties in the
closure `app`:

* `id`: The application ID.
* `version`: The current application version (defaults to current default version).
* `outputDirectory`: The directory where you wish to save the files (defaults to `build/downloaded-app`).

The task `gaeLogs` requires you to at least define the file to write the logs to. Define the tasks' properties in the
closure `logs`:

* `numDays`: The number of days of log data to retrieve, ending on the current date at midnight UTC. A value of 0 retrieves
all available logs. If `append` is given, then the default is 0, otherwise the default is 1.
* `severity`: The minimum log level for the log messages to retrieve. The value is a number corresponding to the log
level: 4 for CRITICAL, 3 for ERROR, 2 for WARNING, 1 for INFO, 0 for DEBUG. All messages at the given log level and above
will be retrieved (defaults to 1 (INFO)).
* `append`: Tells the plugin to append logs to the log output file instead of overwriting the file. This simply appends the
requested data, it does not guarantee the file won't contain duplicate error messages. If this argument is not specified,
the plugin will overwrite the log output file.
* `outputFile`: The file the logs get written to.
* `optimizeWar`: Set it to `true` if you want to budle generated classes and dependencies into one single JAR file. [Gradle FatJar Plugin](https://github.com/musketyr/gradle-fatjar-plugin/) must be installed otherwise the flag has no effect.

### Example

    gae {
        httpPort = 8085
        optimizeWar = true
        appcfg {
            email = 'benjamin.muschko@gmail.com'
            passIn = true

            logs {
                severity = 1
                outputFile = file('mylogs.txt')
            }

            app {
                id = 'sample-app'
            }
        }
    }

## FAQ

**Can I use the plugin with a [Gaelyk](http://gaelyk.appspot.com/) project?**

Gaelyk's [template project](http://gaelyk.appspot.com/tutorial/template-project) uses this plugin out-of-the-box so no
additional configuration needs to be done. If you start your project from scratch and decide to use the plugin please refer
to the following sections to configure it properly.

*Gaelyk <= 1.1*

Yes, you just have to configure the WAR plugin to point to the correct web application (by default `war`) and source code
(by default `src`) directory. If you want to stick to the default source directory simply create the subdirectory `src/main/groovy`.

    apply plugin: 'groovy'

    sourceSets {
        main {
            groovy {
                srcDirs = ['src']
            }
        }
    }

    webAppDirName = file('war')

When editing a Groovlets/Groovy templates in Gaelyk the server automatically deploys the change and you see it take effect almost instantly.
The plugin provides support for that. Simply set the `warDir` convention property and leave the server running.

    gae {
        warDir = file('war')
    }

*Gaelyk >= 1.2*

Starting with version 1.2 Gaelyk adopted Gradle's default directory structure. The following changes are required to
leverage Gaelyk's hot-reloading feature.

    gae {
        warDir = file('src/main/webapp')
    }

    sourceSets.main.output.classesDir = 'src/main/webapp/WEB-INF/classes'

<br>
**How do I remote debug the local development server?**

You can use the convention property `jvmFlags` to set the JVM debug parameters. Make sure to set the TCP port you want
your JVM to listen on. The following example show how to set the JVM flags to listen on port `8000`.

    gae {
        jvmFlags = ['-Xdebug', '-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000']
    }

<br>
**How do I run functional tests on the local development server?**

If you want to execute your tests in the container alongside the rest of your application you have to configure the `gaeRun` task
to run in `daemon` mode. Starting with version 0.7 of the plugin this has become even easier. It provides the task `gaeFunctionalTest`
which lets you define your functional test dependencies via the configuration `functionalTestCompile`. On top of that the task
has been fully integrated into the build lifecycle.

One of the most prominent functional testing libraries in the Groovy ecosystem is [Geb](http://www.gebish.org/), an expressive
and powerful browser automation solution. Please refer to this short and sweet [tutorial](http://blog.proxerd.pl/article/funcational-testing-of-gae-lyk-applications-with-geb)
for a quickstart.
