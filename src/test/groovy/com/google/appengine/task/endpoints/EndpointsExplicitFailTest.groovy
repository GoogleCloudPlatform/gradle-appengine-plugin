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

import org.gradle.tooling.BuildException
import org.gradle.tooling.ProjectConnection
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * Simple test to ensure the plugin is interacting correctly with the Endpoints service
 *
 * @author Appu Goundan
 */
class EndpointsExplicitFailTest extends EndpointsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none()

    /** Generate endpoints docs and libs against a minimal endpoints project */
    @Test(expected = BuildException)
    void smokeTest() {
        // TODO: presumably the best way to run this test is to actually check
        // for ClassNotFoundException but it's nested pretty deep
        runOnProject { ProjectConnection connection ->
            connection.newBuild().forTasks(
                    "appengineEndpointsGetDiscoveryDoc",
                    "appengineEndpointsGetClientLibs",
                    "appengineExplodeApp"
            ).run()
        }
    }

    protected String getBuildGradleTemplateName() {
        return "build.gradle.explicit.fail.template"
    }
}
