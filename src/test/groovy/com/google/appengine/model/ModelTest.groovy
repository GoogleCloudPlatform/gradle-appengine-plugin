package com.google.appengine.model

import com.google.appengine.AppIntegrationTest
import com.google.appengine.gradle.model.AppEngineModel
import org.gradle.tooling.GradleConnector
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
        ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(projectRoot).connect()
        try {
            AppEngineModel model = connection.getModel(AppEngineModel.class)
            Assert.assertTrue(model.getHttpAddress() == "0.1.2.3")
            Assert.assertTrue(model.getHttpPort() == 1234)
            Assert.assertTrue(model.getAppCfgOptions().isOauth2() == true)
        }
        finally {
            connection.close()
        }
    }
}
