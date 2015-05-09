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
package com.google.gcloud.task.app
import com.google.appengine.AppEnginePlugin
import com.google.gcloud.exec.command.CommandBuilder
import com.google.gcloud.task.GCloudTask
import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.tooling.BuildException

/**
 * Generic application task that adds application directory(ies) to the command
 */
@Incubating
class AppTask extends GCloudTask {

    Set<Project> modules
    Set<String> descriptors

    @Override
    protected void configureCommand(CommandBuilder commandBuilder) {
        super.configureCommand(commandBuilder)
        modules.each { Project module ->
            checkAppEnginePlugin(module)
            descriptors.each { String descriptor ->
                commandBuilder.addArgument(getAppPath(module, descriptor));
            }
        }
    }

    protected void checkAppEnginePlugin(Project project) {
        if (!project.plugins.hasPlugin(AppEnginePlugin)) {
            throw new BuildException("${project.getPath()} does not have appengine plugin", null)
        }
    }

    protected String getAppPath(Project project, String descriptor) {
        File yaml =  new File(AppEnginePlugin.getStagedAppDirectory(project), descriptor);
        if (!yaml.exists()) {
            throw new BuildException("${yaml.absolutePath} does not exist", null)
        }
        yaml.absolutePath
    }
}
