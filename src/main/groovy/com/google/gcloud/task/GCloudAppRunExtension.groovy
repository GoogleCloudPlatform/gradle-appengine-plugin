package com.google.gcloud.task

import org.gradle.api.Incubating

/**
 * Defines the GCloud App Run extension
 */
@Incubating
class GCloudAppRunExtension {
    String apiHost
    String host
    String adminHost
    String storagePath
    String logLevel
    String[] extraParams = []
}
