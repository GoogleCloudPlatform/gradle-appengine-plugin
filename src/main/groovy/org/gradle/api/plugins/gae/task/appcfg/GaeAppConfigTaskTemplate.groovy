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
package org.gradle.api.plugins.gae.task.appcfg

import com.google.appengine.tools.admin.AppCfg
import org.gradle.api.GradleException
import org.gradle.api.plugins.gae.task.GaeWebAppDirTask
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Abstract Google App Engine task used for application configuration.
 *
 * @author Benjamin Muschko
 */
abstract class GaeAppConfigTaskTemplate extends GaeWebAppDirTask {
    static final Logger LOGGER = LoggerFactory.getLogger(GaeAppConfigTaskTemplate.class)
    private String email
    private String server
    private String host
    private Boolean passIn
    private String httpProxy
    private String httpsProxy

    @Override
    void executeTask() {
         try {
            LOGGER.info startLogMessage()

            def params = []
            addCommonParams(params)
            params.addAll getParams()
            LOGGER.info "Using params = $params"

            AppCfg.main(params as String[])
        }
        catch(Exception e) {
            throw new GradleException(errorLogMessage(), e)
        }
        finally {
            LOGGER.info finishLogMessage()
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

        if(getPassIn()) {
            params << "--passin"
        }

        if(getHttpProxy()) {
            params << "--proxy=${getHttpProxy()}"
        }

        if(getHttpsProxy()) {
            params << "--proxy_https=${getHttpsProxy()}"
        }
    }

    public String getEmail() {
        email
    }

    public void setEmail(String email) {
        this.email = email
    }

    public String getServer() {
        server
    }

    public void setServer(String server) {
        this.server = server
    }

    public String getHost() {
        host
    }

    public void setHost(String host) {
        this.host = host
    }

    public Boolean getPassIn() {
        passIn
    }

    public void setPassIn(Boolean passIn) {
        this.passIn = passIn
    }

    public String getHttpProxy() {
        httpProxy
    }

    public void setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy
    }

    public String getHttpsProxy() {
        httpsProxy
    }

    public void setHttpsProxy(String httpsProxy) {
        this.httpsProxy = httpsProxy
    }

    abstract String startLogMessage()
    abstract String errorLogMessage()
    abstract String finishLogMessage()
    abstract List getParams()
}
