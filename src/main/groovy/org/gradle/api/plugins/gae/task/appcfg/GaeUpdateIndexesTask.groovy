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
 * Google App Engine task updating indexes on the server.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/tools/uploadinganapp.html#Updating_Indexes">Documentation</a>
 * @author Benjamin Muschko
 */
class GaeUpdateIndexesTask extends GaeAppConfigTaskTemplate {
    final String COMMAND = "update_indexes"

    @Override
    String startLogMessage() {
        "Starting updating indexes..."
    }

    @Override
    String errorLogMessage() {
        "An error occurred updating indexes."
    }

    @Override
    String finishLogMessage() {
        "Finished updating indexes."
    }

    @Override
    List getParams() {
        [COMMAND, getWebAppSourceDirectory().getCanonicalPath()]
    }
}
