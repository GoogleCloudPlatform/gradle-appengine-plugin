package com.google.appengine

import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.Logger
import org.gradle.tooling.BuildException
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Tests for the AppEnginePlugin
 */
class AppEnginePluginTest extends Specification {

    def "Check version below minimum"() {
        when:
        AppEnginePlugin.checkGradleVersion(p)

        then:
        thrown BuildException

        where:
        _ | p
        _ | mockOutProject("1.0")
        _ | mockOutProject(AppEnginePlugin.GRADLE_MIN_VERSION + "-rc")

    }

    def "Check version ok"() {
        when:
        AppEnginePlugin.checkGradleVersion(p)

        then:
        notThrown BuildException

        where:
        _ | p
        _ | mockOutProject(AppEnginePlugin.GRADLE_MIN_VERSION)
        _ | mockOutProject("1" + AppEnginePlugin.GRADLE_MIN_VERSION)
        _ | mockOutProject(AppEnginePlugin.GRADLE_MIN_VERSION + "-1.2.3+0000")

    }

    Project mockOutProject(String version) {
        Project mockProject = Mock(Project)
        Gradle mockGradle = Mock(Gradle)
        mockGradle.getGradleVersion() >> version
        mockProject.getGradle() >> mockGradle
        mockProject
    }

}

