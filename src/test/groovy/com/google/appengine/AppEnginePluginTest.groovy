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

