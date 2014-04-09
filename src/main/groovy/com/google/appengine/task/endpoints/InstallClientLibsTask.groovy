/*
 * Copyright 2014 the original author or authors.
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
package com.google.appengine.task.endpoints

import org.gradle.api.Incubating
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection


/**
 * Endpoints task to install the downloaded client libraries into the local Maven repository.
 * This task has a dependency (task-level) on GetClientLibsTask.
 *
 * @author Rajeev Dayal
 */
@Incubating
class InstallClientLibsTask extends ClientLibProcessingTask {

    @Override
    void executeTask() {
        logger.debug "Installing client libraries into local Maven repository"
        installLibsLocally()
    }

    private installLibsLocally() {
        // First, look for a zip file in the directory where the client library was downloaded to
        def zips = getClientLibZips()

        assert zips.length > 0
        logger.info "Installing client libraries from zips : ${zips*.toString()}"

        zips.each { clientZip ->

            File clientLibProjectRoot = expandClientLib(clientZip)
            if(clientLibProjectRoot == null) {
                return
            }

            runGradleTasks(clientLibProjectRoot, "install")
        }
    }
}
