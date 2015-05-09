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

import com.google.gradle.test.integration.AppIntegrationTest
import org.gradle.tooling.ProjectConnection
import org.junit.Assert
import org.junit.Test

/**
 * Simple test to ensure the plugin is interacting correctly with the Endpoints service
 *
 * @author Appu Goundan
 */
class EndpointsTest extends AppIntegrationTest {

    public static final String TEST_APP = "endpointsTestApp"

    /** Generate endpoints docs and libs against a minimal endpoints project */
    @Test
    void smokeTest() {

        runOnProject { ProjectConnection connection ->
            connection.newBuild().forTasks(
                    "appengineEndpointsGetDiscoveryDoc",
                    "appengineEndpointsGetClientLibs",
                    "appengineExplodeApp"
            ).run()
        }
        File webInf = new File(projectRoot, "build/exploded-app/WEB-INF")
        ["mysimpleendpoint-v1-rest.discovery",
         "mysimpleendpoint-v1-rpc.discovery"].each {
            assertExists(webInf, it)
        }
        File clientLib = new File(projectRoot, "build/client-libs")
        assertExists(clientLib, "mysimpleendpoint-v1-java.zip")
    }

    protected void assertExists(File dir, String fileName) {
        println "checking ${fileName} in ${dir.getCanonicalPath()}"
        Assert.assertTrue(new File(dir, fileName).exists())

    }

    @Override
    protected String getTestAppName() {
        TEST_APP
    }
}
