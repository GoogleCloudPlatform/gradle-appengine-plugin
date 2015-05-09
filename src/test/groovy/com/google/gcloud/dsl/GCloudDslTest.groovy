package com.google.gcloud.dsl

import com.google.gradle.test.fixture.AppProjectBuilder
import org.gradle.api.Project
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

/**
 * Test the parsing/execution of a build file with a gcloud configuration
 */
class GCloudDslTest {

    static Project root
    static Project child1
    static Project child2

    @BeforeClass
    static void makeProject() {
        root = AppProjectBuilder.builder().withAppEngine().withGCloud().build()
        child1 = AppProjectBuilder.builder().withAppEngine().withParent(root).withName("child1").build()
        child2 = AppProjectBuilder.builder().withAppEngine().withParent(root).withName("child2").build()

        root.gcloud {
            configurations {
                allChildren {
                    args project: "test-project"
                    runArgs host: ":8889", "jvm-flag": ["-X1", "-X2"]
                    deployArgs version: "tomato"
                    modules ":child1", ":child2", ":child2"
                }
                onlyRoot {
                    args project: "other-project"
                }
                indexes {
                    args project: "other-other-project"
                    descriptors "index.yaml", "index.yaml"
                }
            }
        }

        // make sure to evaluate the project, so we can see everything
        root.evaluate()
    }

    @Test
    void testTasks() {
        // why no assertEquals for ints? some autoboxing issue here
        assertEquals(3L, root.tasks.findAll { it.name.startsWith("gcloudAppDeploy") }.size())

        assertNotNull(root.tasks.findByName("gcloudAppDeployAllChildren"))
        assertNotNull(root.tasks.findByName("gcloudAppDeployOnlyRoot"))
        assertNotNull(root.tasks.findByName("gcloudAppDeployIndexes"))

        assertTrue(root.tasks.findAll { it.name.startsWith("gcloudAppRun") }.size() == 2)
        assertNotNull(root.tasks.findByName("gcloudAppRunAllChildren"))
        assertNotNull(root.tasks.findByName("gcloudAppRunOnlyRoot"))

        assertTrue(root.tasks.findAll { it.name.startsWith("gcloudAppStop") }.size() == 2)
        assertNotNull(root.tasks.findByName("gcloudAppStopAllChildren"))
        assertNotNull(root.tasks.findByName("gcloudAppStopOnlyRoot"))
    }

    @Test
    void testExtension() {
        GCloudPluginExtension extension = root.extensions.gcloud

        assertEquals(3L, extension.configurations.size())

        GCloudConfigurable allChildren = extension.configurations.findByName("allChildren")
        GCloudConfigurable onlyRoot = extension.configurations.findByName("onlyRoot")
        GCloudConfigurable indexes = extension.configurations.findByName("indexes")

        assertNotNull(allChildren)
        assertNotNull(onlyRoot)
        assertNotNull(indexes)

        assertEquals(1L, allChildren.args.size())
        assertEquals(2L, allChildren.runArgs.size())
        assertEquals(1L, allChildren.deployArgs.size())
        assertEquals(1L, allChildren.descriptors.size())
        assertEquals(2L, allChildren.modules.size())
        assertEquals("test-project", allChildren.args.get("project"))
        assertEquals(":8889", allChildren.runArgs.get("host"))
        assertEquals("tomato", allChildren.deployArgs.get("version"))
        assertTrue(allChildren.descriptors.contains("app.yaml"))
        assertNotNull(allChildren.modules.find { Project p -> p.path.equals ":child1" })
        assertNotNull(allChildren.modules.find { Project p -> p.path.equals ":child2" })

        assertTrue(onlyRoot.args.size() == 1)
        assertTrue(onlyRoot.runArgs.size() == 0)
        assertTrue(onlyRoot.deployArgs.size() == 0)
        assertTrue(onlyRoot.descriptors.size() == 1)
        assertTrue(onlyRoot.modules.size() == 1)
        assertEquals("other-project", onlyRoot.args.get("project"))
        assertTrue(onlyRoot.descriptors.contains("app.yaml"))
        assertNotNull(onlyRoot.modules.find { Project p -> p.path.equals ":" })

        assertTrue(indexes.args.size() == 1)
        assertTrue(indexes.runArgs.size() == 0)
        assertTrue(indexes.deployArgs.size() == 0)
        assertTrue(indexes.descriptors.size() == 1)
        assertTrue(indexes.modules.size() == 1)
        assertEquals("other-other-project", indexes.args.get("project"))
        assertTrue(indexes.descriptors.contains("index.yaml"))
        assertNotNull(indexes.modules.find { Project p -> p.path.equals ":" })
    }

}
