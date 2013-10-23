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

import com.google.appengine.task.Explodable
import org.gradle.api.tasks.InputDirectory

/**
 * Google App Engine task updating your application on the server.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/tools/uploadinganapp.html#Uploading_the_App">Documentation</a>
 * @author Benjamin Muschko
 */
class UpdateTask extends AppConfigTaskTemplate implements Explodable {
    static final String COMMAND = 'update'
    @InputDirectory File explodedAppDirectory
    Boolean useJava7

    @Override
    String startLogMessage() {
        'Starting update process...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred updating the application on App Engine.'
    }

    @Override
    String finishLogMessage() {
        'Finished updating process.'
    }

    @Override
    List getParams() {
        def params = ['--enable_jar_splitting']

        if(getUseJava7()) {
            params << '--use_java7'
        }

        params.addAll([COMMAND, getExplodedAppDirectory().canonicalPath])
        params
    }
}
