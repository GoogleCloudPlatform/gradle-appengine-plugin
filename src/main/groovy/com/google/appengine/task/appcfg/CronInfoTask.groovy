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
 * Google App Engine task pulling cron information from the server.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/tools/uploadinganapp.html#Managing_Scheduled_Tasks">Documentation</a>
 * @author Benjamin Muschko
 */
class CronInfoTask extends AppConfigTaskTemplate {
    static final String COMMAND = 'cron_info'

    @Override
    String startLogMessage() {
        'Starting getting cron information...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred getting cron information.'
    }

    @Override
    String finishLogMessage() {
        'Finished getting cron information.'
    }

    @Override
    List getParams() {
        [COMMAND, getWebAppSourceDirectory().canonicalPath]
    }
}