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
package com.google.appengine.task.appcfg

import org.gradle.api.GradleException
import com.google.appengine.task.WebAppDirTask

/**
 * Abstract Google App Engine task used for application configuration.
 *
 * @author Benjamin Muschko
 */
abstract class AppConfigTaskTemplate extends WebAppDirTask {
    String email
    String server
    String host
    Boolean noCookies
    Boolean passIn
    String password
    String httpProxy
    String httpsProxy
    Boolean oauth2
    List<String> extraOptions

    @Override
    void executeTask() {
        if (!getOauth2()) {
            logger.lifecycle("*************************************************************************")
            logger.lifecycle("DEPRECATED: Using client login (email + password) will NOT be supported")
            logger.lifecycle("in future versions of appengine, use oauth2 instead")
            logger.lifecycle("see https://github.com/GoogleCloudPlatform/gradle-appengine-plugin#oauth2")
            logger.lifecycle("*************************************************************************")
        }
        // User has to enter credentials
        if (requiresUserInput()) {
            runAppConfig()
        }
        // All credentials were provided
        else {
            Thread appConfigThread = new Thread(new AppConfigRunnable())
            appConfigThread.start()
            appConfigThread.join()
        }
    }

    private boolean requiresUserInput() {
        !(getEmail() && getPassword())
    }

    void runAppConfig() {
        try {
            logger.info startLogMessage()

            def params = []
            addCommonParams(params)
            params.addAll getExtraOptions()
            params.addAll getParams()
            logger.info "Using params = $params"

            if (Boolean.getBoolean("exec.appcfg")) {
                // experimental
                // break out to javaexec, so non zero System.exit() in appcfg will fail the build
                project.javaexec {
                    main = APPENGINE_TOOLS_MAIN
                    classpath getToolsJar()
                    args params.toArray()
                    standardInput = System.in
                }
            }
            else {
                ClassLoader classLoader = Thread.currentThread().contextClassLoader
                def appCfg = classLoader.loadClass(APPENGINE_TOOLS_MAIN)
                appCfg.main(params as String[])
            }
        }
        catch(Exception e) {
            throw new GradleException(errorLogMessage(), e)
        }
        finally {
            logger.info finishLogMessage()
        }
    }

    private void addCommonParams(params) {
        if(getEmail()) {
            params << "--email=${getEmail()}"
        }

        if(getServer()) {
            params << "--server=${getServer()}"
        }

        if(getHost()) {
            params << "--host=${getHost()}"
        }

        if(getNoCookies()) {
            params << '--no_cookies'
        }

        if(getPassIn() || getPassword()) {
            params << '--passin'
        }

        if(getHttpProxy()) {
            params << "--proxy=${getHttpProxy()}"
        }

        if(getHttpsProxy()) {
            params << "--proxy_https=${getHttpsProxy()}"
        }

        if(getOauth2()) {
            params << '--oauth2'
        }

    }

    private class AppConfigRunnable implements Runnable {
        @Override
        void run() {
            InputStream systemInOriginal = System.in
            String passwordInput = getPassword() + "\n"
            InputStream inputStreamReplacement = new ByteArrayInputStream(passwordInput.getBytes("UTF-8"))
            try {
                System.setIn(inputStreamReplacement)
                AppConfigTaskTemplate.this.runAppConfig()
            }
            finally {
                System.setIn(systemInOriginal)
            }
        }
    }

    abstract String startLogMessage()
    abstract String errorLogMessage()
    abstract String finishLogMessage()
    abstract List getParams()
}
