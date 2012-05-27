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
package org.gradle.api.plugins.gae.task.appcfg

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.GradleException

/**
 * Google App Engine task that retrieves the most current version of your application.
 *
 * @author Benjamin Muschko
 */
class GaeDownloadAppTask extends GaeAppConfigTaskTemplate {
    static final String COMMAND = 'download_app'
    @Input String appId
    @Input @Optional String appVersion
    @OutputDirectory File outputDirectory

    @Override
    String startLogMessage() {
        'Starting downloading application...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred downloading application from App Engine.'
    }

    @Override
    String finishLogMessage() {
        'Finished downloading application.'
    }

    @Override
    void executeTask() {
        if(!getOutputDirectory().exists()) {
            boolean success = getOutputDirectory().mkdirs()

            if(!success) {
                throw new GradleException("Failed to create output directory '${getOutputDirectory().canonicalFile}'.")
            }
        }

        super.executeTask()
    }

    @Override
    List getParams() {
        def params = []
        params << "--application=${getAppId()}"

        if(getAppVersion()) {
            params << "--version=${getAppVersion()}"
        }

        params << COMMAND
        params << getOutputDirectory().canonicalFile
        params
    }
}
