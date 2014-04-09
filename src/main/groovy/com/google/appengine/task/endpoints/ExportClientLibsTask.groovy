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
    File clientLibJarOut

    @Override
    void executeTask() {
        logger.info "Expanding sources to " + getClientLibJarOut()
        expandSourceLocally()
    }

    private expandSourceLocally() {

        def zips = getClientLibZips()

        assert zips.length > 0
        logger.debug "Copying client libraries sources from zip : ${zips*.toString()}"

        zips.each { clientZip ->

            File clientLibProjectRoot = expandClientLib(clientZip)
            if(clientLibProjectRoot == null) {
                return
            }

            runGradleTasks(clientLibProjectRoot, "assemble")
            File libDir = new File(clientLibProjectRoot, "build/libs")
            assert libDir.isDirectory()

            logger.info "Exporting \n" +
                    "${(libDir.listFiles([accept:{dir, file -> file.endsWith(".jar")}] as FilenameFilter)*.toString()).join("\n")}" +
                    "\nto ${getClientLibJarOut()}"
            ant.copy(todir: getClientLibJarOut(), overwrite: true) {
                fileset(dir: libDir, includes : "*.jar")
            }
        }
    }
}
