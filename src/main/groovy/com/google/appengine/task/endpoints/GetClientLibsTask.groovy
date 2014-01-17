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

import org.gradle.api.Incubating

/**
 * Endpoints task to download Java client libraries from the Endpoints service
 *
 * @author Appu Goundan
 */
@Incubating
class GetClientLibsTask extends EndpointsTask {

    @Override
    void executeTask() {
        logger.debug "Generating Client Libraries"
        List<String> extras = []
        extras << "-o" << endpointsClientLibDirectory.getCanonicalPath()
        extras << "-l" << "java"
        extras << "-bs" << "gradle"
        runEndpointsCommand("get-client-lib", extras)
    }
}
