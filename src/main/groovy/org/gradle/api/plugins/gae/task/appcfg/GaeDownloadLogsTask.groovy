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

/**
 * Google App Engine task downloading application logs from the server.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/tools/uploadinganapp.html#Downloading_Logs">Documentation</a>
 * @author Benjamin Muschko
 */
class GaeDownloadLogsTask extends GaeAppConfigTaskTemplate {
    final String COMMAND = "request_logs"

    @Override
    String startLogMessage() {
        "Starting downloading logs process..."
    }

    @Override
    String errorLogMessage() {
        "An error occurred downloading logs from App Engine."
    }

    @Override
    String finishLogMessage() {
        "Finished downloading logs process."
    }

    @Override
    List getParams() {
        [COMMAND, getWebAppSourceDirectory().getCanonicalPath(), "somefile.txt"]
    }
}
