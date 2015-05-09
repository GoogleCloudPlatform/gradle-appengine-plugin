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
package com.google.gcloud.task

import com.google.gcloud.exec.GradleExecProcessRunner
import com.google.gcloud.exec.ProcessRunner
import com.google.gcloud.exec.command.CommandBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Incubating
import org.gradle.api.tasks.TaskAction

/**
 * GCloud raw task is never exposed, but users can override it if they so please
 */
@Incubating
class GCloudTask extends DefaultTask {

    static final String GCLOUD_HOME_ENV_VAR_KEY = 'GCLOUD_HOME'
    static final String GCLOUD_HOME_SYS_PROP_KEY = 'gcloud.home'

    // inputs
    List<String> command
    Map<String, ?> args

    @TaskAction
    protected void start() {
        validateCloudSdk()
        executeTask()
    }

    void validateCloudSdk() {
        // TODO: validate the cloud sdk install somehow here
    }

    // automatically find the gcloud executable
    private String getGCloudCommand() {
        def gcloudHomeSysProp = System.getProperty(GCLOUD_HOME_SYS_PROP_KEY)
        if (gcloudHomeSysProp) {
            File gcloudBin = new File(gcloudHomeSysProp, "bin/gcloud")
            if (!gcloudBin.exists()) {
                logger.warn("WARNING: gcloud not found in " + gcloudBin + ", have you set ${GCLOUD_HOME_SYS_PROP_KEY} correctly?");
                logger.warn("looking for gcloud in other locations")
            }
            else {
                return gcloudBin.absolutePath
            }
        }

        // get gcloud path from env var
        def gcloudHomeEnvVar = System.getenv(GCLOUD_HOME_ENV_VAR_KEY)
        if (gcloudHomeEnvVar) {
            File gcloudBin = new File(gcloudHomeEnvVar, "bin/gcloud")
            if (!gcloudBin.exists()) {
                logger.warn("WARNING: gcloud not found in " + gcloudBin + ", have you set ${GCLOUD_HOME_ENV_VAR_KEY} correctly?");
                logger.warn("looking for gcloud in other locations")
            } else {
                return gcloudBin.absolutePath
            }
        }

        return "gcloud";
    }

    // override this to add more parts to the command
    protected void configureCommand(CommandBuilder commandBuilder) {
        // add the gcloud command
        commandBuilder.addCommand(getGCloudCommand())

        // add subcommand parts
        getCommand()?.each { part ->
            commandBuilder.addCommand(part)
        }

        // add all options/args
        getArgs()?.each { key, val ->
            if (val instanceof Collection) {
                commandBuilder.addOption(key, val)
            }
            else {
                commandBuilder.addOption(key, val.toString())
            }
        }
    }

    protected void executeTask() {
        ProcessRunner pr = new GradleExecProcessRunner(getCommandLine(), project)
        int exitCode = pr.run()
        if (exitCode != 0) {
            throw new GradleException("gcloud failed with code " + exitCode)
        }
    }

    String[] getCommandLine() {
        CommandBuilder commandBuilder = new CommandBuilder();
        configureCommand(commandBuilder)
        String[] commandLine = commandBuilder.buildCommand()
        logger.info("Running command : ${commandLine.join(' ')}");
        commandLine
    }
}
