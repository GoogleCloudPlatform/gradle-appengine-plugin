package com.google.appengine.task.appcfg

import org.gradle.api.Incubating

/**
 * Google App Engine task to migrate traffic to a new module version
 */
@Incubating
class MigrateTrafficTask extends AppConfigTaskTemplate {
    static final String COMMAND = 'migrate_traffic'

    @Override
    String startLogMessage() {
        'Starting to activate traffic migration...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred activating traffic migration'
    }

    @Override
    String finishLogMessage() {
        'Finished activating traffic migration'
    }

    @Override
    List getParams() {
        [COMMAND, getWebAppSourceDirectory().canonicalPath]
    }
}
