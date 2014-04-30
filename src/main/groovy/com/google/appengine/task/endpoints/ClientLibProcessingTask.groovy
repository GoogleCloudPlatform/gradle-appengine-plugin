package com.google.appengine.task.endpoints

import com.google.appengine.task.AbstractTask
import org.apache.maven.model.Model
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.InputDirectory
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.GradleProject

/**
 * Base class for client library processing tasks
 *
 * @author Appu Goundan
 */
abstract class ClientLibProcessingTask extends EndpointsTask {

    /*
     This property should never be set directly. It's set based on the value of clientLibDirectory
     for the GetClientsLib task, and it is set once the task execution graphs is ready. See
     AppEnginePlugin.configureEndpoints for more information.
     */
    @InputDirectory
    File clientLibDirectory

    File[] getClientLibZips() {
        getClientLibDirectory().listFiles([accept:{dir, file -> file.endsWith(".zip")}] as FilenameFilter)
    }

    File expandClientLib(File clientZip) {

        File outputDir = getTemporaryDir()
        File subDir = new File(outputDir, clientZip.name + "-unzipped")

        ant.unzip(src: clientZip, dest: subDir.getAbsolutePath())
        String[] buildFiles = new FileNameFinder().getFileNames(subDir.getAbsolutePath(), "**/build.gradle")

        if (buildFiles.length != 1) {
            logger.error "When looking for project root in ${clientZip.name}, found ${buildFiles.length} build.gradle files; was expecting 1"
            return null
        }

        File buildFile = new File(buildFiles[0])
        assert buildFile.exists()
        assert buildFile.getParentFile().isDirectory()
        return buildFile.parentFile
    }

    void runGradleTasks(File projectRoot, String... tasks) {
        ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(projectRoot).connect()
        try {
            connection.newBuild().forTasks(tasks).run()
            // this will throw exceptions that will propagate upwards on failure
        } finally {
            connection.close()
        }
    }

}
