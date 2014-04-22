package com.google.appengine.task.endpoints

import groovy.util.slurpersupport.GPathResult
import spock.lang.Specification

/**
 * Simple test to ensure the services classes are read from a properly formatted file
 *
 * @author Appu Goundan
 */
class WebXmlTest extends Specification {
    def "Get single service classes"() {
        given: "A web.xml"
            def webXmlParsed = createWebXml("com.google.appengine.endpoints.OneEndpoint")

        when: "Parsing service classes"
            List<String> services = WebXmlProcessing.getApiServiceClasses(webXmlParsed)

        then: "The classes parsed"
            services.size() == 1
            services.get(0) == "com.google.appengine.endpoints.OneEndpoint"
    }

    def "Get multiple service classes"() {
        given: "A web.xml"
           def webXmlParsed = createWebXml("com.google.appengine.endpoints.OneEndpoint,  com.google.appengine.endpoints.TwoEndpoint")

        when: "Parsing service classes"
            List<String> services = WebXmlProcessing.getApiServiceClasses(webXmlParsed)

        then: "The classes parsed"
            services.size() == 2
            services.get(0) == "com.google.appengine.endpoints.OneEndpoint"
            services.get(1) == "com.google.appengine.endpoints.TwoEndpoint"
    }

    private GPathResult createWebXml(String serviceClassParam) {
        String webXml =
                """
                    <web-app xmlns="http://java.sun.com/xml/ns/javaee"
                        xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        version="2.5"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
                        <servlet>
                            <servlet-name>SystemServiceServlet</servlet-name>
                            <servlet-class>com.google.api.server.spi.SystemServiceServlet
                            </servlet-class>
                                <init-param>
                                <param-name>services</param-name>
                                <param-value>${serviceClassParam}</param-value>
                            </init-param>
                        </servlet>
                        <servlet-mapping>
                            <servlet-name>SystemServiceServlet</servlet-name>
                            <url-pattern>/_ah/spi/*</url-pattern>
                        </servlet-mapping>
                    </web-app>
                """
        new XmlSlurper().parseText(webXml)
    }
}
