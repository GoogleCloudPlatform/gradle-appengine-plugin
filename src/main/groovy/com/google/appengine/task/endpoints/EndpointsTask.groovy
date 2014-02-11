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

import com.google.api.server.spi.tools.EndpointsTool
import com.google.appengine.task.AbstractTask
import com.google.appengine.task.internal.ClasspathBuilder
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

/**
 * Abstract Endpoints task to run endpoints commands on the EndpointsTool
 * https://developers.google.com/appengine/docs/java/endpoints/endpoints_tool
 *
 * @author Appu Goundan
 */
abstract class EndpointsTask extends AbstractTask {
    @InputDirectory File classesDirectory
    @InputDirectory File webappDirectory

    void runEndpointsCommand(String action, List<String> extraParams) {
        try {
            logger.info "Running endpoints command ${action}"
            List<String> params = []
            params << action
            params += getCommonParams()
            params += extraParams
            params += getServiceClassParams()
            logger.debug "Command params " + params.toString()
            new EndpointsTool().execute(params as String[])
        }
        catch(Exception e) {
            throw new GradleException("There was an error running endpoints command ${action}: ${e.getMessage()}", e)
        }
        finally {
            logger.info "Done running endpoints command ${action}"
        }
    }

    List<String> getCommonParams() {
        List<String> params = []
        params << "-cp" << ClasspathBuilder.getClasspath(project)
        params << "-w" << getWebappDirectory().getCanonicalPath()
        return params;
    }

    List<String> getServiceClassParams() {
        return WebXmlProcessing.getApiServiceClasses(new File(getWebappDirectory(), "WEB-INF/web.xml"))
    }
}
