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

/**
 * Defines App Engine Endpoints plugin convention.
 *
 * @author Appu Goundan
 */
class EndpointsConvention {
    List<String> discoveryDocFormat = ["rpc", "rest"]
    boolean getDiscoveryDocsOnBuild = false
    boolean getClientLibsOnBuild = false
    boolean installClientLibsOnBuild = false // will override getClientLibsOnBuild when true
    boolean exportClientLibsOnBuild = false // will override getClientLibsOnBuild when true
    File clientLibJarOut
    File clientLibSrcJarOut
    List<String> serviceClasses
}
