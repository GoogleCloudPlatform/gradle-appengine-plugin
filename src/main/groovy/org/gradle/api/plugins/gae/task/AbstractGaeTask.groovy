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

import org.gradle.api.InvalidUserDataException
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Abstract Google App Engine task taking care of general setup.
 *
 * @author Benjamin Muschko
 */
abstract class AbstractGaeTask extends ConventionTask {
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractGaeTask.class)
    static final String APPENGINE_HOME_ENV_PROP_KEY = 'APPENGINE_HOME'
    static final String APPENGINE_SDK_ROOT_SYS_PROP_KEY = 'appengine.sdk.root'
    static final String JAVA_CLASSPATH_SYS_PROP_KEY = 'java.class.path'

    @TaskAction
    protected void start() {
        validateConfiguration()
        executeTask()
    }

    void validateConfiguration() {
        validateAppEngineSdkRoot()
        validateAppEngineToolsApiJar()
    }

    private void validateAppEngineSdkRoot() {
        setAppEngineSdkRoot()
        def appEngineSdkRoot = System.getProperty(APPENGINE_SDK_ROOT_SYS_PROP_KEY)

        if(!appEngineSdkRoot) {
            throw new InvalidUserDataException("""The App Engine SDK root was not set. Please make sure you set the environment
            variable '${APPENGINE_HOME_ENV_PROP_KEY}' system property '${APPENGINE_SDK_ROOT_SYS_PROP_KEY}'!""")
        }
        else {
            if(!new File(appEngineSdkRoot).isDirectory()) {
                throw new InvalidUserDataException("Set App Engine SDK root directory does not exist: '${appEngineSdkRoot}'!")
            }
        }

        LOGGER.info "App Engine SDK root = ${appEngineSdkRoot}"
    }

    private void setAppEngineSdkRoot() {
        def appEngineSdkRoot = System.getProperty(APPENGINE_SDK_ROOT_SYS_PROP_KEY)

        if(!appEngineSdkRoot) {
            def appEngineHomeEnvVariable = System.getenv(APPENGINE_HOME_ENV_PROP_KEY)

            if(appEngineHomeEnvVariable) {
                System.setProperty APPENGINE_SDK_ROOT_SYS_PROP_KEY, appEngineHomeEnvVariable
            }
        }
    }

    private void validateAppEngineToolsApiJar() {
        def fileSeparator = System.getProperty('file.separator')
        def pathSeparator = File.pathSeparator
        def appEngineSdkRoot = System.getProperty(APPENGINE_SDK_ROOT_SYS_PROP_KEY)
        def javaClasspath = System.getProperty(JAVA_CLASSPATH_SYS_PROP_KEY)
        def appEngineToolsApiJar = "${appEngineSdkRoot}${fileSeparator}lib${fileSeparator}appengine-tools-api.jar"

        if(!new File(appEngineSdkRoot).exists()) {
            throw new InvalidUserDataException("Required library 'appengine-tools-api.jar' could not be found in specified path: '${appEngineToolsApiJar}'!")
        }

        if(!javaClasspath.contains(appEngineToolsApiJar)) {
            System.setProperty JAVA_CLASSPATH_SYS_PROP_KEY, "${javaClasspath}${pathSeparator}${appEngineToolsApiJar}"
        }

        LOGGER.info "Java classpath = ${System.getProperty(JAVA_CLASSPATH_SYS_PROP_KEY)}"
    }

    abstract void executeTask()
}
