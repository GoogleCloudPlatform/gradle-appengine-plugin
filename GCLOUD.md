![Google Cloud Platform Logo](https://cloud.google.com/_static/images/gcp-logo.png)
# Gradle GCloud [preview] Plugin

This plugin provides tasks for running and deploying App Engine applications using the gcloud command line tool.
*Caution:* This plugin is experimental and may change without notice

## Usage

The gcloud-preview plugin is distributed with the Gradle App Engine plugin.
To use the gcloud-preview plugin, include in your build script:
    
    buildscript {
      repositories {
        mavenCentral()
      }

      dependencies {
        classpath 'com.google.appengine:gradle-appengine-plugin:<version>'
      }
    }
    
    apply plugin: 'com.google.gcloud-preview'

*Note:* The default behavior of this plugin is to have gcloud on the system path. If gcloud is not on the path, you can
provide that information via the environment variable _GCLOUD\_HOME_ or the system property _gcloud.home_
pointing to your current Google App Engine SDK installation. In case you have both variables set the system property takes
precedence over the environment variable._


## Tasks

The gcloud-preview plugin defines the following tasks

* `gcloudAppDeploy<configName>`: Deploy *configName* to Google Cloud using [`gcloud app preview deploy`](https://cloud.google.com/sdk/gcloud/reference/preview/app/deploy)
* `gcloudAppRun<configName>`: Starts the local dev server configured in *configName* using [`gcloud app preview run`](https://cloud.google.com/sdk/gcloud/reference/preview/app/run)
* `gcloudAppStop<configName>`: Stops the local dev server configured in *configName*

## Convention properties

The gcloud-preview plugin allows configuration in the `gcloud` closure

* `configurations`: This is the base named domain object container for all your gcloud configurations

The `configurations` closure allows you to name and specify the following for a configuration

* `args`: A map of args to pass to gcloud
* `deployArgs`: A map of deploy specific args
* `runArgs`: A map of run specific args
* `runAsync`: Configure the development server to run in async mode (true/false)
* `modules`: A set of gradle project paths to include in the configuration. The default
   value for this is "this" module. Any module specified here must be an appengine
   project with the gradle appengine plugin.
* `descriptors`: A list of deployment descriptors, defaults to `['app.yaml']` (app.yaml, index.yaml, etc)

### Example

```gradle
apply plugin: 'com.google.gcloud-preview'

gcloud {
  configurations {
    prod {
      args project: "myProd", verbosity: "info"
      deployArgs "http-port": "8888"
      runArgs "admin-host": ":1995"
      modules ":a", ":b”
    }
    test {
      modules ":a", ":b"
      runAsync true
      args project: "myTest"
      modules ":" // same as default behavior
      descriptors "app.yaml" // same as default behavior
    }
    indexes {
      modules ":a"
      descriptors "index.yaml"
    }
  }
}
```

The plugin will dynamically create deploy tasks for each configuration <br/>
`gcloudAppDeployProd`<br/>
`gcloudAppDeployTest`<br/>
`cloudAppDeployIndexes` <br/>
and create run/stop tasks for each configuration that defined an app.yaml descriptor <br/>
`gcloudAppRunProd`<br/>
`gcloudAppRunTest`<br/>
`gcloudAppStopProd`<br/>
`gcloudAppStopTest`<br/>


## FAQ

**How do I auth with gcloud?**

At the moment, you must call `gcloud auth` at least once to set up your authorization context

<br/>
**Does this work with EAR formatted projects**

No, there is no explicit support for EAR formatted projects
