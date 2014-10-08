package com.google.appengine

import org.gradle.tooling.BuildException
import spock.lang.Specification

/**
 * Tests for the AppEnginePlugin
 */
class AppEnginePluginTest extends Specification {

    def "Check version below minimum"() {
        given:
            String gradleVersionLow = "1.0"

        when:
            AppEnginePlugin.checkGradleVersion(gradleVersionLow)

        then:
            thrown BuildException
    }

    def "Check version ok"() {
        given:
            String gradleVersionEqual = AppEnginePlugin.GRADLE_MIN_VERSION
            String gradleVersionHigh = "1" + AppEnginePlugin.GRADLE_MIN_VERSION

        when:
            AppEnginePlugin.checkGradleVersion(gradleVersionEqual)
            AppEnginePlugin.checkGradleVersion(gradleVersionHigh)

        then:
            notThrown BuildException
    }

}

