package com.google.gcloud

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppEnginePluginExtension
import com.google.gcloud.task.GCloudAppDeployTask
import com.google.gcloud.task.GCloudAppRunTask
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project;

/**
 * <p>A {@link Plugin} that provides tasks for generic use of gcloud in gradle and
 * for uploading and running of Google App Engine projects via gcloud.</p>
 *
 * @author Appu Goundan
 */
@Incubating
public class GCloudPlugin implements Plugin<Project> {

    static final String TASK_GCLOUD_APP_RUN = "gcloudAppRun"
    static final String TASK_GCLOUD_APP_DEPLOY = "gcloudAppDeploy"

    @Override
    void apply(Project project) {

        project.apply plugin: "appengine"

        project.extensions.create('gcloud', GCloudPluginExtension, project)

        project.tasks.create(TASK_GCLOUD_APP_RUN, GCloudAppRunTask)
        project.tasks.create(TASK_GCLOUD_APP_DEPLOY, GCloudAppDeployTask)

        // TODO : Move this into app engine plugin when behavior is stabilized
        // old run task depends on null warDir for certain behavior
        AppEnginePluginExtension aEx = project.extensions.getByName("appengine")
        aEx.warDir = AppEnginePlugin.getAppDir(project)
    }
}
