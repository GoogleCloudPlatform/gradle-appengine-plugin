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
import org.gradle.api.tasks.OutputDirectory

/**
 * Endpoints task to expand client library sources for use with another project
 * This task has a dependency (task-level) on GetClientLibsTask.
 *
 * @author Rajeev Dayal
 */
@Incubating
class ExportClientLibsTask extends ClientLibProcessingTask {

    @OutputDirectory
    File clientLibSrcOut;

    @Override
    void executeTask() {
        logger.info "Expanding sources to " + getClientLibSrcOut()
        expandSourceLocally()
    }

    private expandSourceLocally() {

        def zips = getClientLibZips()

        assert zips.length > 0
        logger.debug "Copying client libraries sources from zip : ${zips*.toString()}"

        zips.each { clientZip ->

            File clientLibProjectRoot = expandClientLib(clientZip)
            if(clientLibProjectRoot == null) {
                return;
            }

            // look for src/main/java in the client lib project root
            File srcRoot = new File((File) clientLibProjectRoot, "src/main/java");
            if (!srcRoot.exists()) {
                logger.error "Unable to find src/main/java in ${clientZip.name}"
                return
            }

            runGradleTask(srcRoot, "assemble")
            ant.copy(todir: getClientLibSrcOut(), overwrite: true) {
                fileset(dir: clientLibProjectRoot, includes : "build/libs/*SNAPSHOT*.jar")
            }
        }
    }
}
