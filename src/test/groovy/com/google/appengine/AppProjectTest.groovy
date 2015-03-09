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
package com.google.appengine

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before

/**
 * Base class for gradle configuration tests involving an test project with the appengine
 * plugin already applied
 *
 * @author Appu Goundan
 */
class AppProjectTest {

    protected Project project

    /** Create a basic appengine project */
    @Before
    void setUp() {
        project = ProjectBuilder.builder().build();
        project.buildscript.repositories {
            maven {
                url System.getProperty("test.maven.repo")
            }
            mavenCentral()
        }
        project.buildscript.dependencies {
            classpath "com.google.appengine:gradle-appengine-plugin:${System.getProperty('appengine.pluginversion')}"
        }
        project.apply plugin: "war"
        project.apply plugin: "java"
        project.apply plugin: "appengine"
    }
}
