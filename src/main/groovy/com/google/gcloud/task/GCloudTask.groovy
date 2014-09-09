package com.google.gcloud.task

import com.google.gcloud.GCloudPluginExtension
import com.google.gcloud.wrapper.GCloud
import com.google.gcloud.wrapper.GCloudCommandBuilder
import com.google.gcloud.wrapper.impl.GCloudCommandLineWrapper
import org.gradle.api.DefaultTask
import org.gradle.api.Incubating
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * GCloud task is never exposed, but users can override it if they so please
 */
@Incubating
class GCloudTask extends DefaultTask {
    boolean isRunAsync = false
    @Input String[] command

    @TaskAction
    protected void executeTask() {
        GCloud gcloud = new GCloudCommandLineWrapper(getCommand())
        GCloudPluginExtension gcEx = project.extensions.getByName("gcloud")
        if (gcEx.gcloudHome) {
            gcloud.setGCloudHome(new File(gcEx.gcloudHome));
        }
        println(getCommand());
        if (isRunAsync) {
            gcloud.runAsync(null)
        }
        else {
            gcloud.runSync()
        }
    }

    protected String[] getCommand() {
        return command;
    }

    protected void addBaseCommandOptions(GCloudCommandBuilder builder) {
        GCloudPluginExtension gcEx = project.extensions.getByName("gcloud")
        builder.addOption("project", gcEx.project)
               .addOption("verbosity", gcEx.verbosity)
    }
}
