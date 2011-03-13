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
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFiles
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
    private Set<File> sourceDirectories

    @Override
    void executeTask() {
        enhanceClasses()
    }

    private void enhanceClasses() {
        try {
            logger.info "Enhancing DataNucleus classes..."

            def enhancerTask = new EnhancerTask()
            enhancerTask.setEnhancerName "Gradle GAE plugin enhancer"
            enhancerTask.setProject createProject()

            getSourceDirectories().each { sourceDir ->
                if(sourceDir.exists()) {
                    logger.info "Adding source dir '$sourceDir' for enhancement process."
                    def fileSet = new FileSet()
                    fileSet.setDir(sourceDir)
                    enhancerTask.addFileSet(fileSet)
                }
                else {
                    logger.debug "Available source directory '$sourceDir' does not exist. It will not be used for the enhancement process."
                }
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

    @InputFiles
    public Set<File> getSourceDirectories() {
        sourceDirectories
    }

    public void setSourceDirectories(Set<File> sourceDirectories) {
        this.sourceDirectories = sourceDirectories
    }
}
