/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.plugins.gae.task

import com.google.appengine.tools.enhancer.EnhancerTask
import org.apache.tools.ant.Project
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.selectors.AndSelector
import org.apache.tools.ant.types.selectors.FilenameSelector
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputDirectory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Google App Engine task enhancing DataNucleus classes.
 *
 * @see <a href="http://www.datanucleus.org/products/accessplatform/enhancer.html">Documentation</a>
 * @author Benjamin Muschko
 */
class GaeEnhanceTask extends GaeWebAppDirTask {
    static final Logger logger = LoggerFactory.getLogger(GaeEnhanceTask.class)
    private File classesDirectory

    @Override
    void executeTask() {
        enhanceClasses()
    }

    private void enhanceClasses() {
        try {
            logger.info "Enhancing DataNucleus classes..."

            def enhancerTask = new EnhancerTask()
            enhancerTask.setVerbose(true)
            enhancerTask.setEnhancerName "Gradle GAE plugin enhancer"
            enhancerTask.setProject createProject()

            if(getClassesDirectory().exists()) {
                logger.info "Using classes dir '${getClassesDirectory()}' for enhancement process."
                def fileSet = new FileSet()
                fileSet.setDir(getClassesDirectory())
                AndSelector andSelector = new AndSelector()
                FilenameSelector filenameSelector = new FilenameSelector()
                filenameSelector.setName("**/*")
                andSelector.add(filenameSelector)
                fileSet.addAnd(andSelector)
                enhancerTask.addFileSet(fileSet)
            }
            else {
                logger.debug "Classes directory '${getClassesDirectory()}' does not exist."
            }

            enhancerTask.execute()
        }
        catch(Exception e) {
            throw new GradleException("An error occurred enhancing DataNucleus classes.", e)
        }
        finally {
            logger.info "Finished enhancing DataNucleus classes."
        }
    }

    private Project createProject() {
        def project = new Project()

        project.with {
            setName "GAE project"
            init()
        }

        project
    }

    @InputDirectory
    public File getClassesDirectory() {
        classesDirectory
    }

    public void setClassesDirectory(File classesDirectory) {
        this.classesDirectory = classesDirectory
    }
}
