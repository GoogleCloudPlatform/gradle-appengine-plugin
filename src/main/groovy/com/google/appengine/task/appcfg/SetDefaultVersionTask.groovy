/*
 * Copyright 2016 the original author or authors.
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

import org.gradle.api.tasks.InputDirectory

/**
 * Google App Engine task that sets the default version for a module
 *
 */
class SetDefaultVersionTask extends AppConfigTaskTemplate {
    static final String COMMAND = 'set_default_version'

    @InputDirectory File explodedAppDirectory

    @Override
    String startLogMessage() {
        'Starting setting default version...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred setting default version.'
    }

    @Override
    String finishLogMessage() {
        'Finished setting default version.'
    }

    @Override
    List getParams() {
        [COMMAND, getExplodedAppDirectory().canonicalPath]
    }
}
