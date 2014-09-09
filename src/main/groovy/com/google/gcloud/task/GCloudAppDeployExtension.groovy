package com.google.gcloud.task

import org.gradle.api.Incubating

/**
 * Defines the GCloud App Deploy extension
 */
@Incubating
class GCloudAppDeployExtension {
    String dockerHost
    String server
    String version
    Boolean forceDeploy
    String[] extraParams = []
}
