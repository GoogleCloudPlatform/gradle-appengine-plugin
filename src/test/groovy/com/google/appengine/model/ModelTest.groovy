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
package com.google.appengine.model

import com.google.gradle.test.integration.AppIntegrationTest
import com.google.appengine.gradle.model.AppEngineModel
import org.gradle.tooling.ProjectConnection
import org.junit.Assert
import org.junit.Test

/**
 * Smoke test to see that we can load in an App Engine Model
 */
class ModelTest extends AppIntegrationTest {

    public static final String TEST_APP = "modelTestApp"

    @Override
    protected String getTestAppName() {
        TEST_APP
    }

    @Test
    void modelLoadTest() {
        runOnProject { ProjectConnection connection ->
            AppEngineModel model = connection.getModel(AppEngineModel.class)
            Assert.assertTrue(model.getHttpAddress() == "0.1.2.3")
            Assert.assertTrue(model.getHttpPort() == 1234)
            Assert.assertTrue(model.getAppCfgOptions().isOauth2() == true)
        }
    }
}
