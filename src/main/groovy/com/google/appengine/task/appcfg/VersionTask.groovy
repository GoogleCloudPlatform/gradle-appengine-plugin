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
 * Google App Engine task printing detailed version information about the SDK, Java and the operating system.
 *
 * @author Benjamin Muschko
 */
class VersionTask extends AppConfigTaskTemplate {
    static final String COMMAND = 'version'

    @Override
    String startLogMessage() {
        'Starting to get version information...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred getting version information.'
    }

    @Override
    String finishLogMessage() {
        'Finished to get version information.'
    }

    @Override
    List getParams() {
        [COMMAND]
    }
}
