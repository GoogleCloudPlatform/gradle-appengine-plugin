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
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

import java.util.zip.ZipFile

/**
 * Endpoints task to install the downloaded client libraries into the local Maven repository.
 *
 * This task has a dependency (task-level) on GetClientLibsTask.
 *
 * TODO (rdayal): Refactor this; it shares a bunch of code with InstallClientLibsTask
 *
 * @author Rajeev Dayal
 */
@Incubating
class ExpandSourceTask extends EndpointsTask {

    /*
     This property should never be set directly. It's set based on the value of clientLibDirectory
     for the GetClientsLib task, and it is set once the task execution graphs is ready. See
     AppEnginePlugin.configureEndpoints for more information.
     */
    @InputDirectory
    File clientLibDirectory;

    @OutputDirectory
    File copySourceToDirectory;

    @Override
    void executeTask() {
        logger.debug "expanding sources to " + getCopySourceToDirectory()
        expandSourceLocally()
    }

    private expandSourceLocally() {

        def zips = getClientLibDirectory().listFiles([accept:{dir, file -> file.endsWith(".zip")}] as FilenameFilter);

        assert zips.length > 0
        logger.info "Installing client libraries from zip : ${zips*.toString()}"

        def clientLibGradleProjRoot = null;

        // Create a temporary directory (a well-known method for doing this prior to Java 7)
        def destDir = File.createTempFile('clientlib', 'archive')
        destDir.delete()
        destDir.mkdir()

        zips.each { clientZip ->
            // Iterate through all of the entries of the zip file and extract its contents into destDir
            def clientLibZipFile = new ZipFile(clientZip);
            clientLibZipFile.entries().each {
                if (!it.directory) {
                    def fos = null
                    try {
                        fos = new FileOutputStream(new File(destDir, it.name))
                        fos.write(clientLibZipFile.getInputStream(it).bytes)
                    } finally {
                        if (fos) {
                            fos.close()
                        }
                    }

                    // Keep track of the zip entry named 'build.gradle'. This is the 'root' of the endpoints client
                    // library Gradle package
                    if (it.name =~ /.*build.gradle$/) {
                        clientLibGradleProjRoot = new File(destDir, it.name).getParentFile();
                    }
                } else {
                    new File(destDir, it.name).mkdirs()
                }
            }

            if (!clientLibGradleProjRoot) {
                logger.warn "Unable to locate the build.gradle file under " + destDir.absolutePath + "; unable to install the client libraries into the local Maven repository."
                return
            }

            // look for src/main/java as a peer of build.gradle
            File srcRoot = new File((File) clientLibGradleProjRoot, "src/main/java");
            if (!srcRoot.exists()) {
                logger.warn "Unable to find src/main/java"
                return
            }

            new AntBuilder().copy(todir: getCopySourceToDirectory()) {
                fileset(dir: srcRoot)
            }
        }
    }
}
