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
import com.google.gradle.test.fixture.AppProjectBuilder
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.junit.Before
import org.junit.Test

class IdeaTest {

    Project project;

    @Before
    void setUp() {
        project = AppProjectBuilder.builder().withAppEngine().build();
    }

    @Test
    void functionalConfigTest() {
        project.apply plugin: "idea"
        Configuration testCompileConfig = project.configurations.getByName(AppEnginePlugin.FUNCTIONAL_TEST_COMPILE_CONFIGURATION)
        Configuration testRuntimeConfig = project.configurations.getByName(AppEnginePlugin.FUNCTIONAL_TEST_RUNTIME_CONFIGURATION)
        SourceSet testSourceSet = project.convention.getPlugin(JavaPluginConvention).sourceSets.getByName(AppEnginePlugin.FUNCTIONAL_TEST_SOURCE_SET)

        assert(project.plugins.hasPlugin(IdeaPlugin))
        project.evaluate()

        project.plugins.withType(IdeaPlugin) { IdeaPlugin plugin ->
            testSourceSet.allSource.srcDirs.each { File dir ->
                assert (plugin.model.module.testSourceDirs.contains(dir))
            }
            assert (plugin.model.module.scopes.TEST.plus.contains(testRuntimeConfig))
            assert (plugin.model.module.scopes.TEST.plus.contains(testCompileConfig))
        }
    }
}
