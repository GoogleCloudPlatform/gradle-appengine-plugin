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

import com.google.appengine.task.Explodable
import com.google.appengine.task.appcfg.AppConfigTaskTemplate
import org.gradle.api.tasks.InputDirectory

/**
 * Google App Engine task updating all backends on the server.
 *
 * @see <a href="https://developers.google.com/appengine/docs/java/backends/overview?hl=cs#Commands">Documentation</a>
 * @author Benjamin Muschko
 */
class UpdateAllBackendsTask extends AppConfigTaskTemplate implements Explodable {
    static final String COMMAND = 'update'
    @InputDirectory File explodedAppDirectory

    @Override
    String startLogMessage() {
        "Starting to update all backends..."
    }

    @Override
    String errorLogMessage() {
        "An error occurred updating backends."
    }

    @Override
    String finishLogMessage() {
        "Finished updating backends."
    }

    @Override
    List getParams() {
        [AbstractBackendsTask.COMMAND, getExplodedAppDirectory().canonicalPath, COMMAND]
    }
}
