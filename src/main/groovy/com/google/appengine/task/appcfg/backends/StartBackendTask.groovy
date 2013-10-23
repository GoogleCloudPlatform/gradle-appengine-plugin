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
package com.google.appengine.task.appcfg.backends

/**
 * Google App Engine task starting backend on the server.
 *
 * @see <a href="https://developers.google.com/appengine/docs/java/backends/overview?hl=cs#Commands">Documentation</a>
 * @author Benjamin Muschko
 */
class StartBackendTask extends AbstractSingleBackendTask {
    static final String COMMAND = 'start'

    @Override
    String startLogMessage() {
        "Starting to start backend '${getBackend()}'..."
    }

    @Override
    String errorLogMessage() {
        "An error occurred starting backend '${getBackend()}'."
    }

    @Override
    String finishLogMessage() {
        "Finished starting backend '${getBackend()}'."
    }

    @Override
    String getCommand() {
        COMMAND
    }
}

