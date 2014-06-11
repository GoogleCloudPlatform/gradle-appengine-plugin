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

import com.google.appengine.task.EnhancerConvention
import com.google.appengine.task.appcfg.AppConfigConvention
import com.google.appengine.task.endpoints.EndpointsConvention

/**
 * Defines App Engine plugin convention.
 *
 * @author Benjamin Muschko
 */
class AppEnginePluginConvention {
    String httpAddress = "localhost"
    Integer httpPort = 8080
    Boolean daemon = false
    Boolean disableUpdateCheck = false
    @Deprecated
    String enhancerVersion
    @Deprecated
    String enhancerApi
    List<String> jvmFlags = []
    File warDir
    Boolean downloadSdk = false
    AppConfigConvention appCfg = new AppConfigConvention()
    EndpointsConvention endpoints = new EndpointsConvention()
    EnhancerConvention enhancer = new EnhancerConvention()

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
