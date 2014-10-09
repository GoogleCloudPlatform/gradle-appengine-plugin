package com.google.appengine.task.appcfg

/**
 * Google App Engine task updating dispatch configuration on the server.
 *
 * To be used on the "default" module, will not work on EAR
 * See <a href="https://cloud.google.com/appengine/docs/java/modules/routing">Dispatch Docs</a>
 */
class UpdateDispatchTask extends AppConfigTaskTemplate {
    static final String COMMAND = 'update_dispatch'

    @Override
    String startLogMessage() {
        'Starting update-dispatch process...'
    }

    @Override
    String errorLogMessage() {
        'An error occurred updating the dispatch file on App Engine.'
    }

    @Override
    String finishLogMessage() {
        'Finished updating dispatch file.'
    }

    @Override
    List getParams() {
        [COMMAND, getWebAppSourceDirectory().canonicalPath]
    }
}
