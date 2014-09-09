package com.google.gcloud

import com.google.gcloud.task.GCloudAppDeployExtension
import com.google.gcloud.task.GCloudAppRunExtension
import org.gradle.api.Incubating
import org.gradle.api.Project

/**
 * Defines the GCloud plugin extension
 */
@Incubating
class GCloudPluginExtension {
    private final Project gradleProject // to differentiate from gcloud project
    String gcloudHome
    String project
    String verbosity
    List<String> externalModules; //TODO : non java module directories
    GCloudAppDeployExtension app = new GCloudAppDeployExtension()
    GCloudAppRunExtension run = new GCloudAppRunExtension()

    public GCloudPluginExtension(Project project) {
        this.gradleProject = project;
    }

    void app(Closure closure) {
        gradleProject.configure(app, closure)
    }

    void run(Closure closure) {
        gradleProject.configure(run, closure)
    }
}
