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
package com.google.appengine.task

import org.gradle.api.GradleException
import org.gradle.api.tasks.InputDirectory

/**
 * Google App Engine task enhancing DataNucleus classes.
 *
 * @see <a href="http://www.datanucleus.org/products/accessplatform/enhancer.html">Documentation</a>
 * @author Benjamin Muschko
 */
class EnhanceTask extends WebAppDirTask {
    @InputDirectory File classesDirectory

    @Override
    void executeTask() {
        enhanceClasses()
    }

    private void enhanceClasses() {
        try {
            logger.info 'Enhancing DataNucleus classes...'

            ant.taskdef(name: 'enhance', classpath: System.getProperty(JAVA_CLASSPATH_SYS_PROP_KEY), classname: 'com.google.appengine.tools.enhancer.EnhancerTask')
            ant.enhance(failonerror: true, verbose: true) {
                classpath {
                    pathelement(path: System.getProperty(JAVA_CLASSPATH_SYS_PROP_KEY))
                    pathelement(path: getClassesDirectory().canonicalPath)
                    fileset(dir: "${getWebAppSourceDirectory().canonicalPath}/WEB-INF/lib", erroronmissingdir: "false", includes: '*.jar')
                    pathelement(path: project.configurations.compile.asPath)
                }
                fileset(dir: getClassesDirectory().canonicalPath, includes: '**/*.class')
            }
        }
        catch(Exception e) {
            throw new GradleException('An error occurred enhancing DataNucleus classes.', e)
        }
        finally {
            logger.info 'Finished enhancing DataNucleus classes.'
        }
    }
}