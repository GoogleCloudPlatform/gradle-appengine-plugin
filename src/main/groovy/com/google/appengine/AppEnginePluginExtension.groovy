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
package com.google.appengine

import com.google.appengine.task.EnhancerExtension
import com.google.appengine.task.appcfg.AppConfigExtension
import com.google.appengine.task.endpoints.EndpointsExtension
import org.gradle.api.Project

/**
 * Defines App Engine plugin extension.
 *
 * @author Benjamin Muschko
 */
class AppEnginePluginExtension {
    String httpAddress = "localhost"
    Integer httpPort = 8080
    Boolean daemon = false
    Boolean disableUpdateCheck = false
    List<String> jvmFlags = []
    File warDir
    Boolean downloadSdk = false
    AppConfigExtension appCfg = new AppConfigExtension()
    EndpointsExtension endpoints = new EndpointsExtension()
    EnhancerExtension enhancer = new EnhancerExtension()

    public AppEnginePluginExtension(Project project) {
    }

    def appengine(Closure closure) {
        closure.delegate = this
        closure()
    }

    def appcfg(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = appCfg
        closure()
    }

    def app(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = appCfg.app
        closure()
    }

    def logs(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = appCfg.logs
        closure()
    }

    def update(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = appCfg.update
        closure()
    }

    def endpoints(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = endpoints
        closure()
    }

    def enhancer(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = enhancer
        closure()
    }
}
