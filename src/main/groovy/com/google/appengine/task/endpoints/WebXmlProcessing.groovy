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

import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import org.gradle.api.GradleException

/**
 * web.xml Processing tools for Endpoints, primarily used for extraction of Endpoints classes
 *
 * @Author Appu Goundan
 */
class WebXmlProcessing {

    static List<String> getApiServiceClasses(webXmlFile) {
        def root = new XmlSlurper().parse(webXmlFile)
        getApiServiceClasses(root)
    }

    static List<String> getApiServiceClasses(GPathResult root) {
        getClassNames(getServicesParam(getSystemServiceServlet(root)))
    }

    private static NodeChild getSystemServiceServlet(GPathResult root) {
        def servlets = root."servlet".findAll { it."servlet-class".text().trim() == "com.google.api.server.spi.SystemServiceServlet" ? it : null }
        if(servlets.size() != 1) {
            throw new GradleException("web.xml must have 1 (found:${servlets.size()}) SystemServiceServlet servlet")
        }
        return servlets.list()[0]
    }

    private static NodeChild getServicesParam(NodeChild servlet) {
        assert servlet.name() == "servlet"
        def servicesParam = servlet."init-param".findAll { it."param-name".text().trim() == "services" ? it : null }
        if(servicesParam.size() != 1) {
            throw new GradleException("web.xml must have 1 (found:${servicesParam.size()}) SystemServiceServlet 'services' init-param")
        }
        return servicesParam.list()[0]
    }

    private static String[] getClassNames(NodeChild servicesParam) {
        assert servicesParam.name() == "init-param"
        return servicesParam."param-value".text().trim().split(",").collect { String service ->
            service.trim()
        }
    }

    public static NodeChild getSystemServiceServletMapping(GPathResult root, NodeChild servlet) {
        def servletName = servlet."servlet-name".getText()
        def servletMapping = root."servlet-mapping".findAll { it."servlet-name".text().trim() == servletName ? it : null }
        if(servletMapping.size() != 1) {
            throw new GradleException("must have one ${servletName} servlet-mapping")
        }
        return servletMapping.list()[0]
    }

}
