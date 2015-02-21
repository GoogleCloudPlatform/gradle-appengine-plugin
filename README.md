![Google Cloud Platform Logo](https://cloud.google.com/_static/images/gcp-logo.png)
# Gradle App Engine plugin [![Build Status](https://travis-ci.org/GoogleCloudPlatform/gradle-appengine-plugin.svg?branch=master)](https://travis-ci.org/GoogleCloudPlatform/gradle-appengine-plugin)


The plugin provides tasks for uploading, downloading, running and managing [Google App Engine](https://cloud.google.com/appengine/)
(App Engine) projects in any given Gradle build.

## Usage

To use the App Engine plugin, include in your build script:

    apply plugin: 'appengine'

The plugin JAR needs to be defined in the classpath of your build script. It is directly available on
[Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.google.appengine%22%20AND%20a%3A%22gradle-appengine-plugin%22).
Alternatively, you can download it from GitHub and deploy it to your local repository. The following code snippet shows an
example on how to retrieve it from Maven Central:

    buildscript {
        repositories {
            mavenCentral()
        }

        dependencies {
            classpath 'com.google.appengine:gradle-appengine-plugin:1.9.18'
        }
    }

*Note:* The plugin requires you to set the environment variable APPENGINE_HOME or the system property _appengine.sdk.root_
pointing to your current Google App Engine SDK installation. In case you have both variables set the system property takes
precedence over the environment variable. Alternatively, you can choose to automatically download the SDK by setting the
convention property `downloadSdk` to `true`. This option requires you to specify the SDK version you want to use by setting
the configuration `appengineSdk`.

    dependencies {
        appengineSdk 'com.google.appengine:appengine-java-sdk:1.9.18'
    }

## Tasks

The App Engine plugin defines the following tasks:

* `appengineConfigureBackends`: Dynamically updates settings in `backends.xml` without having to stop the backend. The setting is defined by the project property `setting`.
* `appengineCronInfo`: Verifies and prints the scheduled task (cron) configuration.
* `appengineDeleteBackend`: Deletes the indicated backend. The backend is defined by the project property `backend`.
* `appengineDownloadApp`: Retrieves the most current version of your application.
* `appengineDownloadSdk`: Downloads and sets Google App Engine SDK.
* `appengineEndpointsGetClientLibs`: Download Endpoints client libraries. (this makes network calls)
* `appengineEndpointsGetDiscoveryDocs`: Download Endpoints discovery docs, you should run `appengineExplodeApp` with this to ensure the discovery docs are copied into the project after download. (this makes network calls)
* `appengineEndpointsInstallClientLibraries`: Install client libraries to the local maven repo.
* `appengineEndpointsExportClientLibraries`: Export client libraries to user-defined destination.
* `appengineEnhance`: Enhances DataNucleus classes by using byte-code manipulation to make your normal Java classes "persistable". This requires you to add the necessary enhancer jars as dependencies to your project.
* `appengineExplodeApp`: Extends the `war`/`ear` task to generate WAR/EAR file and explodes the artifact into `build/exploded-app`.
* `appengineFunctionalTest`: Runs the tests from `functionalTest` source set against a local development server started in daemon mode.
* `appengineListBackends`: Lists all the backends configured for the app specified in `appengine-web.xml`.
* `appengineLogs`: Retrieves log data for the application running on App Engine.
* `appengineRollback`: Undoes a partially completed update for the given application.
* `appengineRollbackBackend`: Rolls back a backend update that was interrupted by the user or stopped due to a configuration error. The backend is defined by the project property `backend`.
* `appengineRun`: Starts a local development server running your project code. By default the WAR file is created, exploded and used as
web application directory each time you run this task. This behavior can be changed by setting the convention property
`warDir`.
* `appengineStartBackend`: Sets the backend state to `START`, allowing it to receive HTTP requests. The backend is defined by the project property `backend`.
* `appengineStop`: Stops the local development server.
* `appengineStopBackend`: Sets the backend state to `STOP` and shuts down any running instances. The backend is defined by the project property `backend`.
* `appengineUpdateAllBackends`: Creates or updates all backends configured in `backends.xml`.
* `appengineUpdateBackend`: Creates or updates backend configured in `backends.xml`. The backend is defined by the project property `backend`.
* `appengineUpdateCron`: Updates the schedule task (cron) configuration for the app, based on the cron.xml file.
* `appengineUpdateDispatch`: Updates the dispatch configuration for the app, based on the dispatch.xml file.
* `appengineUpdateDos`: Updates the DoS protection configuration for the app, based on the dos.xml file.
* `appengineUpdateIndexes`: Updates datastore indexes in App Engine to include newly added indexes.
* `appengineUpdateQueues`: Updates the task queue configuration (queue.xml) in App Engine.
* `appengineUpdate`: Uploads files for an application given the application's root directory. The application ID and version are taken from the appengine-web.xml file.
* `appengineUpdateAll`: Uploads your application to App Engine and updates all backends by running the task `appengineUpdate` and `appengineUpdateAllBackends`.
* `appengineVacuumIndexes`: Deletes unused indexes in App Engine server.
* `appengineVersion`: Prints detailed version information about the SDK, Java and the operating system.

## Project layout

The App Engine plugin uses the same layout as the [Gradle War plugin](http://gradle.org/docs/current/userguide/war_plugin.html). The only difference is the addition of the `functionalTest` source set
(located in `src/functionalTest` by default) which is used by the `appengineFunctionalTest` task.

## Convention properties

The App Engine plugin defines the following convention properties in the `appengine` closure:

* `httpAddress`: The IP address for the local development server (if server is to be accessed from network). Default is localhost.
* `httpPort`: The TCP port which local development server should listen for HTTP requests on (defaults to 8080).
* `daemon`: Specifies whether the local development server should run in the background. When true, this task completes as
soon as the server has started. When false, this task blocks until the local development server is stopped (defaults to false).
 * Running the App Engine local development server in daemon mode requires that [Gradle is also run as a daemon](http://www.gradle.org/docs/current/userguide/gradle_daemon.html). The local development server will always be stopped when Gradle exits.
* `warDir`: Web application directory used for local development server (defaults to `build/exploded-war`).
* `disableUpdateCheck`: Disables the Google App Engine update check if set to true.
* `jvmFlags`: The JVM flags to pass on to the local development server. The data type is a `List`.
* `downloadSdk`: Downloads the Google App Engine SDK defined by the configuration name `appengineSdk` and explodes the artifact into
`~/.gradle/appengine-sdk` (defaults to false). If set to `true` the given SDK is used for running all plugin tasks which
takes precedence over APPENGINE_HOME and the system property _appengine.sdk.root_.
* ~~`enhancerVersion`: The version (v1/v2) parameter for App Engine datanucleus enhancer task~~ Deprecated
* ~~`enhancerApi`: The api (jdo/jpa) parameter for the App Engine datanucleas enhancer task~~ Deprecated

Within `appengine` you can define optional properties in a closure named `appcfg`:

* `email`: The email address of the Google account of an administrator for the application, for actions that require signing in.
If omitted and no cookie is stored from a previous use of the command, the command will prompt for this value.
* `server`: The App Engine server hostname (defaults to appengine.google.com).
* `host`: The hostname of the local machine for use with remote procedure calls.
* `noCookies`: Do not store the administrator sign-in credentials. Prompt for a password every time. (or go through the OAuth2 flow when the `oauth2` option is used).
* `passIn`: Do not store the administrator sign-in credentials as a cookie; prompt for a password every time. If the property
`password` was provided then this value will always be true.
* `password`: The password in plain text to be used whenever a task requires one. The password is only applied if the `email`
convention property was provided also. Alternatively, you can set the password in your `gradle.properties` via the property
`appenginePassword`. The password in `gradle.properties` takes precedence over the one set in this convention property.
* `httpProxy`: Use the given HTTP proxy to contact App Engine.
* `httpsProxy`: Use the given HTTPS proxy to contact App Engine, when using HTTPS. If `httpProxy` is given but `httpsProxy`
is not, both HTTP and HTTPS requests will use the given proxy.
* `oauth2`: Use OAuth2 authentication instead of password-based authentication.
* `extraOptions`: A list of extra command line options for the AppCfg tool (defaults to [])

Within `appengine` you can also define a closure named `enhancer`:
* `version`: The version (v1/v2) parameter for App Engine datanucleus enhancer task
* `api`: The api (jdo/jpa) parameter for the App Engine datanucleas enhancer task
* `enhanceOnBuild`: Automatically run the enhancer (defaults to false)

Within `appengine` you can also define a closure named `endpoints`:

* `discoveryDocFormat`: A list of discovery doc formats. (defaults to ['rpc', 'rest'])
* `serviceClasses`: A list of endpoints classes to generate discovery docs or client libs for (overrides values read from web.xml)
* `getDiscoveryDocsOnBuild`: Automatically download discovery docs before the `war` task is called. (defaults to false)
* `getClientLibsOnBuild`: Automatically download client libraries before the `war` task is called. (defaults to false)
* `installClientLibsOnBuild`: Automatically install client libs, will get client libs if necessary. (defaults to false) [this task will never be UP-TO-DATE, careful when making part of your build]
* `exportClientLibsOnBuild`: Automatically export client libs, will get client libs if necessary. (defaults to false)
* `clientLibsJarOut`: Output directory of client library jars when exporting. (Must be of type: File, will be created if doesn't exist)
* `clientLibsSrcJarOut`: Output directory of client library source jars when exporting, if not specified no source jars will be copied. (Must be of type: File, will be created if doesn't exist)
* `googleClientVersion`: Override the version of the Google Api Client Library that builds with endpoints artifacts. (Only works with endpoints and android-endpoints configurations)

The task `appengineDownloadApp` requires you to at least define the application ID and directory to write the files to. Define the tasks' properties in the
closure `app`:

* `id`: The application ID.
* `version`: The current application version (defaults to current default version).
* `outputDirectory`: The directory where you wish to save the files (defaults to `build/downloaded-app`).

The task `appengineLogs` requires you to at least define the file to write the logs to. Define the tasks' properties in the
closure `logs`:

* `numDays`: The number of days of log data to retrieve, ending on the current date at midnight UTC. A value of 0 retrieves
all available logs. If `append` is given, then the default is 0, otherwise the default is 1.
* `severity`: The minimum log level for the log messages to retrieve. The value is a number corresponding to the log
level: 4 for CRITICAL, 3 for ERROR, 2 for WARNING, 1 for INFO, 0 for DEBUG. All messages at the given log level and above
will be retrieved (defaults to 1 (INFO)).
* `append`: Tells the plugin to append logs to the log output file instead of overwriting the file. This simply appends the
requested data, it does not guarantee the file won't contain duplicate error messages. If this argument is not specified,
the plugin will overwrite the log output file.
* `includeAll`: Include everything in log messages.
* `outputFile`: The file the logs get written to.

The task `appengineUpdate` allows you to specify upload specific settings. Define the tasks' properties in the closure `update`:

* `useJava7`: Java 7 compatibility flag (default to `false` if not set). This feature will require a App Engine SDK of >= 1.7.3.

### Example

    appengine {
        httpPort = 8085

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

**Can I use the plugin with a [Gaelyk](http://appenginelyk.appspot.com/) project?**

Gaelyk's [template project](http://appenginelyk.appspot.com/tutorial/template-project) uses this plugin out-of-the-box so no
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

    appengine {
        warDir = file('war')
    }

*Gaelyk >= 1.2*

Starting with version 1.2 Gaelyk adopted Gradle's default directory structure. The following changes are required to
leverage Gaelyk's hot-reloading feature.

    appengine {
        warDir = file('src/main/webapp')
    }

    sourceSets.main.output.classesDir = 'src/main/webapp/WEB-INF/classes'

<br>
**How do I remote debug the local development server?**

You can use the convention property `jvmFlags` to set the JVM debug parameters. Make sure to set the TCP port you want
your JVM to listen on. The following example show how to set the JVM flags to listen on port `8000`.

    appengine {
        jvmFlags = ['-Xdebug', '-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000']
    }

<br>
**How do I run functional tests on the local development server?**

If you want to execute your tests in the container alongside the rest of your application you have to configure the `appengineRun` task
to run in `daemon` mode. Starting with version 0.7 of the plugin this has become even easier. It provides the task `appengineFunctionalTest`
which lets you define your functional test dependencies via the configuration `functionalTestCompile`. On top of that the task
has been fully integrated into the build lifecycle.

One of the most prominent functional testing libraries in the Groovy ecosystem is [Geb](http://www.gebish.org/), an expressive
and powerful browser automation solution. Please refer to this short and sweet [tutorial](http://blog.proxerd.pl/article/funcational-testing-of-appengine-lyk-applications-with-geb)
for a quickstart.

<br>
**Why is my exploded-war still using old discovery docs even though I ran appengineEndpointsGetDisoveryDocs?**

The task `appengineEndpointsGetDiscoveryDocs` only downloads the Discovery Docs for an endpoint to a cache in build/discovery-docs,
the `war` task copies this into the WEB-INF folder when packaging. If you want them to be copied in, just run `build` or `appengineExplodeApp` again.
Why do we do this? The endpoints calls are network calls to a service and some users may not want to do this on every build. The user can update their
endpoints discovery docs when they know they need to and it will be packaged in on every subsequent build without needing to be regenerated.

<br>
**How do I use a compile dependency on my endpoints client libraries from another project?**

Using the new configurations in the appengine plugin (endpoints/android-endpoints) you can do

     dependencies {
         compile project(path: '<appengine-module>', configuration: 'endpoints')
     }

or in an android project

    dependencies {
        compile project(path: '<appengine-module>', configuration: 'android-endpoints')
    }

<br>
**Enhancer failed with "Java returned: 1", how do I know what's wrong?**

Run using the --info flag when building to find the location of the error log file, that log file might have details on what went wrong. On linux machines
it may appear as "/tmp/enhance(some number).log"
