package com.google.gcloud.task

import com.google.appengine.AppEnginePlugin
import com.google.gcloud.GCloudPluginExtension
import com.google.gcloud.wrapper.GCloudCommandBuilder
import org.gradle.api.Incubating

/**
 * Task to Run App Engine projects locally
 */
@Incubating
class GCloudAppRunTask extends GCloudTask {
    @Override
    protected String[] getCommand() {
        GCloudPluginExtension gcEx = project.extensions.getByName("gcloud");
        GCloudCommandBuilder builder = new GCloudCommandBuilder("preview", "app", "run")
        addBaseCommandOptions(builder);
        builder.addOption("api-host", gcEx.run.apiHost)
               .addOption("admin-host", gcEx.run.adminHost)
               .addOption("storage-path", gcEx.run.storagePath)
               .addOption("log-level", gcEx.run.logLevel)
               .addOption("host", gcEx.run.host)
               .add(gcEx.run.extraParams)
               .add(AppEnginePlugin.getExplodedAppDirectory(project).absolutePath)
        //List<String> modules; //non java module directories
        return builder.buildCommand()
    }
}
