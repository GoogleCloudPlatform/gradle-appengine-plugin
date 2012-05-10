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

import org.gradle.api.plugins.gae.task.Explodable
import org.gradle.api.plugins.gae.task.appcfg.GaeAppConfigTaskTemplate
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory

/**
 * Google App Engine task updating backend on the server.
 *
 * @see <a href="https://developers.google.com/appengine/docs/java/backends/overview?hl=cs#Commands">Documentation</a>
 * @author Benjamin Muschko
 */
class GaeUpdateBackendTask extends GaeAppConfigTaskTemplate implements Explodable {
    static final String COMMAND = 'update'
    @Input String backend
    @InputDirectory File explodedWarDirectory

    @Override
    String startLogMessage() {
        "Starting to update backend '${getBackend()}'..."
    }

    @Override
    String errorLogMessage() {
        "An error occurred updating backend '${getBackend()}'."
    }

    @Override
    String finishLogMessage() {
        "Finished updating backend '${getBackend()}'."
    }

    @Override
    List getParams() {
        [AbstractGaeBackendsTask.COMMAND, getExplodedWarDirectory().canonicalPath, COMMAND, getBackend()]
    }
}
