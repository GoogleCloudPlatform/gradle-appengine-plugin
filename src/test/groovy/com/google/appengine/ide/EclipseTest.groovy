/*
 * Copyright 2015 the original author or authors.
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
package com.google.appengine.ide

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppProjectTest
import com.google.appengine.util.VersionComparator
import org.gradle.api.artifacts.Configuration
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.junit.Test

/**
 * Simple test to ensure the plugin is interacting nicely with the eclipse plugin
 *
 * @author Appu Goundan
 */
class EclipseTest extends AppProjectTest {

    @Test
    void functionalConfigTest() {
        project.apply plugin: "eclipse"
        Configuration testConfig = project.configurations.getByName(AppEnginePlugin.FUNCTIONAL_TEST_RUNTIME_CONFIGURATION)

        if (VersionComparator.compare(project.gradle.gradleVersion, "2.3") < 0) {
            assert(project.plugins.hasPlugin(EclipsePlugin))
            project.plugins.withType(EclipsePlugin) { EclipsePlugin plugin ->
                assert(plugin.model.classpath.plusConfigurations.contains(testConfig))
            }
        }
        else {
            // TODO this doesn't actually do anything unless the builder uses gradle 2.3 or higher
            // but it can be tested manually by using gradle 2.3, I can't to find a way using projectbuilder
            // to set the gradle version. (The Gradle Connector can)
            project.afterEvaluate {
                assert(project.plugins.hasPlugin(EclipsePlugin))
                assert(project.extensions.getByType(EclipseModel).classpath.plusConfigurations.contains(testConfig))
            }
        }
    }
}
