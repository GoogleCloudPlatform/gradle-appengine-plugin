package com.google.appengine.task.internal

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

/**
 * Get the classpath before the war is built/exploded, this is useful for tasks that need the classpath
 * to run some of the Tooling from the App Engine SDK tool set
 */
class ClasspathBuilder {
    public static String getClasspath(Project project) {
        asClasspathString(project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName(
                SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath())
    }

    private static String asClasspathString(FileCollection files) {
        files.collect() { File file ->
            file.getCanonicalPath()
        }.join(File.pathSeparator);
    }
}
