# Gradle GAE plugin

The plugin provides tasks for uploading, downloading, running and managing Google App Engine projects in any given
Gradle build. It extends the War plugin.

## Usage

To use the GAE plugin, include in your build script:

    apply plugin: 'gae'

The plugin JAR and the App Engine tools SDK library need to be defined in the classpath of your build script. You can
either get the plugin from the GitHub download section or upload it to your local repository. The following code snippet
shows an example:

    buildscript {
	    repositories {
		    add(new org.apache.ivy.plugins.resolver.URLResolver()) {
    		    name = "GitHub"
    		    addArtifactPattern 'http://cloud.github.com/downloads/bmuschko/gradle-gae-plugin/[module]-[revision].[ext]'
  		    }
            mavenCentral()
        }

	    dependencies {
		    classpath 'com.google.appengine:appengine-tools-sdk:1.4.2'
            classpath ':gradle-gae-plugin:0.1'
        }
    }

*Note:* The plugin requires you to set the environment variable _APPENGINE_HOME_ or the system property _google.appengine.sdk_
pointing to your current Google App Engine SDK installation. In case you have both variables set the system property takes
precedence over the environment variable.

## Tasks

The GAE plugin defines the following tasks:

* `gaeRun`: Starts a local development server running your project code.
* `gaeEnhance`: Enhances DataNucleus classes by using byte-code manipulation to make your normal Java classes "persistable".
* `gaeUpload`: Uploads files for an application given the application's root directory. The application ID and version are taken from the appengine-web.xml file.
* `gaeRollback`: Undoes a partially completed update for the given application.
* `gaeUpdateIndexes`: Updates datastore indexes in App Engine to include newly added indexes.
* `gaeVacuumIndexes`: Deletes unused indexes in App Engine server.
* `gaeUpdateQueues`: Updates the task queue configuration (queue.xml) in App Engine.
* `gaeUpdateDos`: Updates the DoS protection configuration for the app, based on the dos.xml file.
* `gaeUpdateCron`: Updates the schedule task (cron) configuration for the app, based on the cron.xml file.
* `gaeCronInfo`: Verifies and prints the scheduled task (cron) configuration.
* `gaeLogs`: Retrieves log data for the application running on App Engine.
* `gaeVersion`: Prints detailed version information about the SDK, Java and the operating system.

## Project layout

The GAE plugin uses the same layout as the War plugin.