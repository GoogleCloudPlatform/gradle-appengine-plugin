/*
 * Copyright 2013 the original author or authors.
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

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.junit.Assert
import org.junit.Test

/**
 * Simple test to ensure the plugin is interacting correctly with the Endpoints service
 *
 * @author Appu Goundan
 */
class EndpointsExplicitFailTest extends EndpointsTest {

    /** Generate endpoints docs and libs against a minimal endpoints project */
    @Test
    void smokeTest() {

        ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(projectRoot).connect()
        try {
            connection.newBuild().forTasks(
                    "appengineEndpointsGetDiscoveryDoc",
                    "appengineEndpointsGetClientLibs",
                    "appengineExplodeApp"
            ).run()
            Assert.fail("Should have thrown a ClassNotFoundException")
        }
        catch (Exception e){
            // This is what the builder should throw because we have a bad class name defined
            // in the build file
        }
        finally {
            connection.close()
        }
    }

    protected String getBuildGradleTemplateName() {
        return "build.gradle.explicit.fail.template"
    }
}
