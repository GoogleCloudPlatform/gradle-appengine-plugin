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
import org.gradle.api.tasks.Optional
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
    @Optional @OutputDirectory
    File clientLibSrcJarOut

    @Override
    void executeTask() {
        logger.info "Exporting jar to " + getClientLibJarOut()
        if(getClientLibSrcJarOut()) {
            logger.info "Exporting src-jar to " + getClientLibSrcJarOut()
        }
        exportJar()
    }

    private exportJar() {

        def zips = getClientLibZips()

        assert zips.length > 0
        logger.debug "Archiving client libraries from zip : ${zips*.toString()}"

        zips.each { clientZip ->

            File clientLibProjectRoot = expandClientLib(clientZip)
            if(clientLibProjectRoot == null) {
                return
            }

            runGradleTasks(clientLibProjectRoot, "assemble")
            File libDir = new File(clientLibProjectRoot, "build/libs")
            assert libDir.isDirectory()

            logger.info "Exporting \n" +
                    "${(libDir.listFiles([accept:{dir, file -> file.endsWith(".jar") && !file.endsWith("-sources.jar")}] as FilenameFilter)*.toString()).join("\n")}" +
                    "\nto ${getClientLibJarOut()}"
            ant.copy(todir: getClientLibJarOut(), overwrite: true) {
                fileset(dir: libDir, includes : "*.jar", excludes : "*-sources.jar")
            }


            if(getClientLibSrcJarOut()) {
                logger.info "Exporting \n" +
                        "${(libDir.listFiles([accept:{dir, file -> file.endsWith("-sources.jar")}] as FilenameFilter)*.toString()).join("\n")}" +
                        "\nto ${getClientLibSrcJarOut()}"
                ant.copy(todir: getClientLibSrcJarOut(), overwrite: true) {
                    fileset(dir: libDir, includes : "*-sources.jar")
                }
            }
        }
    }
}
