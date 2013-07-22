/*
 * Copyright 2012 the original author or authors.
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
package org.gradle.api.plugins.gae.task.internal

import spock.lang.Specification

/**
 * KickStart command line parameters builder tests.
 *
 * @author Benjamin Muschko
 */
class KickStartParamsBuilderTest extends Specification {
    def "Build parameters for minimal arguments"() {
        given: "minimal arguments are provided"
            KickStartParams kickStartParams = createBasicParams()

        when: "the params are built"
            List<String> params = KickStartParamsBuilder.instance.buildCommandLineParams(kickStartParams)

        then: "the basic parameters are set up"
            params.size() == 3
            params.get(0) == KickStartParamsBuilder.MAIN_CLASS
            params.get(1) == "$KickStartParamsBuilder.PORT=8080"
            params.get(2) == '/dev/main/war'
    }

    def "Build parameters for minimal arguments plus disable update check"() {
        given: "no extra arguments are provided"
            KickStartParams kickStartParams = createBasicParams()
            kickStartParams.disableUpdateCheck = true

        when: "the params are built"
            List<String> params = KickStartParamsBuilder.instance.buildCommandLineParams(kickStartParams)

        then: "the basic parameters are set up"
            params.size() == 4
            params.get(0) == KickStartParamsBuilder.MAIN_CLASS
            params.get(1) == "$KickStartParamsBuilder.PORT=8080"
            params.get(2) == KickStartParamsBuilder.DISABLE_UPDATE_CHECK
            params.get(3) == '/dev/main/war'
    }

    def "Build parameters for minimal arguments plus disable update check, httAddress and additional JVM flags"() {
        given: "no extra arguments are provided"
            KickStartParams kickStartParams = createBasicParams()
            kickStartParams.disableUpdateCheck = true
            kickStartParams.jvmFlags = ['-Xmx1024', '-Dappengine.user.timezone=UTC']
            kickStartParams.httpAddress = '0.0.0.0'

        when: "the params are built"
            List<String> params = KickStartParamsBuilder.instance.buildCommandLineParams(kickStartParams)

        then: "the basic parameters are set up"
            params.size() == 7
            params.get(0) == KickStartParamsBuilder.MAIN_CLASS
            params.get(1) == "$KickStartParamsBuilder.PORT=8080"
            params.get(2) == "$KickStartParamsBuilder.ADDRESS=0.0.0.0"

            params.get(3) == KickStartParamsBuilder.DISABLE_UPDATE_CHECK
            params.get(4) == "$KickStartParamsBuilder.JVM_FLAG=-Xmx1024"
            params.get(5) == "$KickStartParamsBuilder.JVM_FLAG=-Dappengine.user.timezone=UTC"
            params.get(6) == '/dev/main/war'
    }

    private KickStartParams createBasicParams() {
        KickStartParams kickStartParams = new KickStartParams()
        kickStartParams.httpPort = 8080
        kickStartParams.explodedWarDirectory = new File('/dev/main/war')
        kickStartParams
    }
}
