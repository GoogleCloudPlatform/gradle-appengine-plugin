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
package org.gradle.api.plugins.gae.task.appcfg.backends

import org.gradle.api.tasks.Input

/**
 * Google App Engine task deleting backend on the server.
 *
 * @see <a href="https://developers.google.com/appengine/docs/java/backends/overview?hl=cs#Commands">Documentation</a>
 * @author Benjamin Muschko
 */
class GaeDeleteBackendTask extends AbstractGaeBackendsTask {
    static final String COMMAND = 'delete'
    @Input String backend

    @Override
    String startLogMessage() {
        "Starting to delete backend '${getBackend()}'..."
    }

    @Override
    String errorLogMessage() {
        "An error occurred deleting backend '${getBackend()}'."
    }

    @Override
    String finishLogMessage() {
        "Finished deleting backend '${getBackend()}'."
    }

    @Override
    List getParams() {
        List params = super.getParams()
        params << COMMAND
        params << getBackend()
        params
    }
}

