/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gcloud

import com.google.gcloud.dsl.GCloudConfigurable
import com.google.gcloud.dsl.GCloudPluginExtension
import com.google.gcloud.task.app.DeployTask
import com.google.gcloud.task.app.RunTask
import com.google.gcloud.task.app.StopTask
import org.gradle.api.Action
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry

import javax.inject.Inject
/**
 * <p>A {@link Plugin} that provides tasks for generic use of gcloud in gradle and
 * for uploading and running of Google App Engine projects via gcloud.</p>
 *
 */
@Incubating
public class GCloudPlugin implements Plugin<Project> {

    static final String TASK_GCLOUD_RUN = "gcloudAppRun"
    static final String TASK_GCLOUD_STOP = "gcloudAppStop"
    static final String TASK_GCLOUD_DEPLOY = "gcloudAppDeploy"
    static final String TASK_GROUP_GCLOUD = "Google gcloud preview experimental"

    final ToolingModelBuilderRegistry registry
    final Instantiator instantiator
    Project project
    GCloudPluginExtension extension

    @Inject
    public GCloudPlugin(Instantiator instantiator, ToolingModelBuilderRegistry registry) {
        this.instantiator = instantiator
        this.registry = registry
    }

    @Override
    void apply(Project p) {
        project = p
        extension = project.extensions.create('gcloud', GCloudPluginExtension, project, instantiator)

        createDynamicTasks()
    }

    void createDynamicTasks() {
        project.afterEvaluate() {
            extension.configurations.each { GCloudConfigurable configuration ->

                // deploy task
                String deployTaskName = TASK_GCLOUD_DEPLOY + configuration.name.capitalize()
                project.tasks.create(deployTaskName, DeployTask, new Action<DeployTask>() {
                    @Override
                    void execute(DeployTask deployTask) {
                        deployTask.setArgs(configuration.args + configuration.deployArgs)
                        deployTask.setModules(configuration.modules)
                        deployTask.setDescriptors(configuration.descriptors)
                        deployTask.description = 'Deploy the ' + configuration.name + ' configuration to Google Cloud'
                        deployTask.group = TASK_GROUP_GCLOUD
                        configuration.modules.each { Project subProject ->
                            addStageTaskDependency(deployTask, subProject)
                        }
                    }
                })

                // only allow local run/stop for configurations with app.yaml
                if (configuration.getDescriptors().contains("app.yaml")) {
                    // run task
                    String runTaskName = TASK_GCLOUD_RUN + configuration.name.capitalize()
                    project.tasks.create(runTaskName, RunTask, new Action<RunTask>() {
                        @Override
                        void execute(RunTask runTask) {
                            runTask.setArgs(configuration.args + configuration.runArgs)
                            runTask.setModules(configuration.modules)
                            runTask.setDescriptors(configuration.APP_DESCRIPTOR)
                            runTask.setAsync(configuration.runAsync)
                            runTask.description = 'Run the ' + configuration.name + ' configuration on Dev App Server'
                            runTask.group = TASK_GROUP_GCLOUD
                            configuration.modules.each { Project subProject ->
                                addStageTaskDependency(runTask, subProject)
                            }
                        }
                    })

                    // stop task
                    String stopTaskName = TASK_GCLOUD_STOP + configuration.name.capitalize()
                    project.tasks.create(stopTaskName, StopTask, new Action<StopTask>() {
                        @Override
                        void execute(StopTask stopTask) {
                            stopTask.description = 'Stop the ' + configuration.name + ' configuration on Dev App Server'
                            stopTask.group = TASK_GROUP_GCLOUD
                            def adminHost = configuration.args.get("admin-host") ?: configuration.args.get("--admin-host")
                            if (adminHost) {
                                stopTask.setAdminHost(adminHost.toString())
                            }
                        }
                    })
                }
            }
        }
    }

    // figure out if the subProject has been evaluated or not before trying to add task dependency
    private static void addStageTaskDependency(Task task, Project subProject) {
        if (subProject.state.executed) {
            task.dependsOn subProject.tasks.getByName("appengineStage")
        } else {
            subProject.afterEvaluate {
                task.dependsOn subProject.tasks.getByName("appengineStage")
            }
        }
    }
    // TODO: take a look at RuleSource for configuring the plugin (https://gradle.org/docs/current/userguide/new_model.html)
}
