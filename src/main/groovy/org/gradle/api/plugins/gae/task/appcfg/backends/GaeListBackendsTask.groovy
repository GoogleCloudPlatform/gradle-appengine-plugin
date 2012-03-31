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

/**
 * Google App Engine task listing backends on the server.
 *
 * @see <a href="https://developers.google.com/appengine/docs/java/backends/overview?hl=cs#Commands">Documentation</a>
 * @author Benjamin Muschko
 */
class GaeListBackendsTask extends AbstractGaeBackendsTask {
    static final String COMMAND = 'list'

    @Override
    String startLogMessage() {
        'Starting to list backends...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred listing backends.'
    }

    @Override
    String finishLogMessage() {
        'Finished listing backends.'
    }

    @Override
    List getParams() {
        List params = super.getParams()
        params << COMMAND
        params
    }
}
