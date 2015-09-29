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
package com.google.appengine.task.appcfg

/**
 * Google App Engine task updating DoS protection configuration on the server.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/tools/uploadinganapp.html#Managing_DoS_Protection">Documentation</a>
 * @author Benjamin Muschko
 */
class UpdateDoSTask extends AppConfigTaskTemplate {
    static final String COMMAND = 'update_dos'

    @Override
    String startLogMessage() {
        'Starting updating DoS protection configuration...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred updating DoS protection configuration.'
    }

    @Override
    String finishLogMessage() {
        'Finished updating DoS protection configuration.'
    }

    @Override
    List getParams() {
        [COMMAND, getWebAppSourceDirectory().canonicalPath]
    }
}
