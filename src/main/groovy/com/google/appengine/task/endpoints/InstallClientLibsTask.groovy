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

import java.util.zip.ZipFile

/**
 * Endpoints task to install the downloaded client libraries into the local Maven repository.
 *
 * This task has a dependency (task-level) on GetClientLibsTask.
 *
 * @author Rajeev Dayal
 */
@Incubating
class InstallClientLibsTask extends GetClientLibsTask {

    @Override
    void executeTask() {
        logger.debug "Installing client libraries into local Maven repository"
        installLibsLocally()
    }

    private installLibsLocally() {
        // First, look for a zip file in the directory where the client library was downloaded to
        def zips = clientLibDirectory.listFiles()
        assert zips.length > 0

        def clientLibGradleProjRoot = null;

        // Create a temporary directory (a well-known method for doing this prior to Java 7)
        def destDir = File.createTempFile('clientlib', 'archive')
        destDir.delete()
        destDir.mkdir()

        // Iterate through all of the entries of the zip file and extract its contents into destDir
        def clientLibZipFile = new ZipFile(zips[0]);
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

        ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(clientLibGradleProjRoot).connect()
        try {
            // Run the 'install' task, which causes the client library sources to be compiled and then installed as a local Maven artifact
            connection.newBuild().forTasks('install').run()
        } finally {
            connection.close()
        }
    }
}
