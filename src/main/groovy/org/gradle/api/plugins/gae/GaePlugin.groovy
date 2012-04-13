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
package org.gradle.api.plugins.gae

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.plugins.gae.task.*
import org.gradle.api.plugins.gae.task.appcfg.*
import org.gradle.api.plugins.gae.task.appcfg.backends.*

/**
 * <p>A {@link Plugin} that provides tasks for uploading, running and managing of Google App Engine projects.</p>
 *
 * @author Benjamin Muschko
 */
class GaePlugin implements Plugin<Project> {
    static final String GAE_SDK_CONFIGURATION_NAME = 'gaeSdk'
    static final String GAE_GROUP = 'Google App Engine'
    static final String GAE_DOWNLOAD_SDK = 'gaeDownloadSdk'
    static final String GAE_RUN = 'gaeRun'
    static final String GAE_STOP = 'gaeStop'
    static final String GAE_ENHANCE = 'gaeEnhance'
    static final String GAE_UPLOAD = 'gaeUpload'
    static final String GAE_ROLLBACK = 'gaeRollback'
    static final String GAE_UPDATE_INDEXES = 'gaeUpdateIndexes'
    static final String GAE_VACUUM_INDEXES = 'gaeVacuumIndexes'
    static final String GAE_UPDATE_TASK_QUEUES = 'gaeUpdateQueues'
    static final String GAE_UPDATE_DOS = 'gaeUpdateDos'
    static final String GAE_UPDATE_CRON = 'gaeUpdateCron'
    static final String GAE_CRON_INFO = 'gaeCronInfo'
    static final String GAE_LOGS = 'gaeLogs'
    static final String GAE_VERSION = 'gaeVersion'
    static final String GAE_EXPLODE_WAR = 'gaeExplodeWar'
    static final String GAE_UPDATE_BACKEND = 'gaeUpdateBackend'
    static final String GAE_UPDATE_ALL_BACKENDS = 'gaeUpdateAllBackends'
    static final String GAE_ROLLBACK_BACKEND = 'gaeRollbackBackend'
    static final String GAE_LIST_BACKENDS = 'gaeListBackends'
    static final String GAE_START_BACKEND = 'gaeStartBackend'
    static final String GAE_STOP_BACKEND = 'gaeStopBackend'
    static final String GAE_DELETE_BACKEND = 'gaeDeleteBackend'
    static final String GAE_CONFIGURE_BACKENDS = 'gaeConfigureBackends'
    static final String GRADLE_USER_PROP_PASSWORD = 'gaePassword'
    static final String STOP_PORT_CONVENTION_PARAM = 'stopPort'
    static final String STOP_KEY_CONVENTION_PARAM = 'stopKey'
    static final String EXPLODED_WAR_DIR_CONVENTION_PARAM = 'explodedWarDirectory'
    static final String EXPLODED_SDK_DIR_CONVENTION_PARAM = 'explodedSdkDirectory'
    static final String BACKEND_PROJECT_PROPERTY = 'backend'
    static final String SETTING_PROJECT_PROPERTY = 'setting'

    @Override
    void apply(Project project) {
        project.plugins.apply(WarPlugin)

        project.configurations.add(GAE_SDK_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
                .setDescription('The Google App Engine SDK to be downloaded and used for this project.')

        GaePluginConvention gaePluginConvention = new GaePluginConvention()
        project.convention.plugins.gae = gaePluginConvention

        File explodedSdkDirectory = getExplodedSdkDirectory(project)
        File explodedWarDirectory = getExplodedWarDirectory(project)
        configureDownloadSdk(project, explodedSdkDirectory)
        configureWebAppDir(project)
        configureAppConfig(project, gaePluginConvention)
        configureGaeExplodeWarTask(project, explodedWarDirectory)
        configureGaeRun(project, gaePluginConvention, explodedWarDirectory)
        configureGaeStop(project, gaePluginConvention)
        configureGaeEnhance(project)
        configureGaeUpload(project, explodedWarDirectory)
        configureGaeRollback(project)
        configureGaeUpdateIndexes(project)
        configureGaeVacuumIndexes(project)
        configureGaeUpdateTaskQueues(project)
        configureGaeUpdateDoS(project)
        configureGaeUpdateCron(project)
        configureGaeCronInfo(project)
        configureGaeDownloadLogs(project, gaePluginConvention)
        configureGaeVersion(project)
        configureGaeSdk(project, gaePluginConvention)
        configureGaeSingleBackendTask(project)
        configureGaeUpdateBackends(project)
        configureGaeUpdateAllBackends(project)
        configureGaeRollbackBackends(project)
        configureGaeListBackends(project)
        configureGaeStartBackend(project)
        configureGaeStopBackend(project)
        configureGaeDeleteBackend(project)
        configureGaeConfigureBackends(project)
    }

    private File getExplodedSdkDirectory(Project project) {
        getBuildSubDirectory(project, 'exploded-gae-sdk')
    }

    private File getExplodedWarDirectory(Project project) {
        getBuildSubDirectory(project, 'exploded-war')
    }

    private File getBuildSubDirectory(Project project, String subDirectory) {
        def explodedWarDirName = new StringBuilder()
        explodedWarDirName <<= project.buildDir
        explodedWarDirName <<= System.getProperty('file.separator')
        explodedWarDirName <<= subDirectory
        new File(explodedWarDirName.toString())
    }

    private void configureDownloadSdk(Project project, File explodedSdkDirectory) {
        project.tasks.withType(GaeDownloadSdkTask).whenTaskAdded { GaeDownloadSdkTask gaeDownloadSdkTask ->
            gaeDownloadSdkTask.conventionMapping.map('gaeSdkZipFile') {
                try {
                    project.configurations.getByName(GAE_SDK_CONFIGURATION_NAME).singleFile
                }
                catch(IllegalStateException e) {
                    // make "gradle -t" happy in case we don't declare configuration!
                }
            }
            gaeDownloadSdkTask.conventionMapping.map(EXPLODED_SDK_DIR_CONVENTION_PARAM) { explodedSdkDirectory }
        }

        GaeDownloadSdkTask gaeDownloadSdkTask = project.tasks.add(GAE_DOWNLOAD_SDK, GaeDownloadSdkTask)
        gaeDownloadSdkTask.description = 'Downloads and sets Google App Engine SDK.'
        gaeDownloadSdkTask.group = GAE_GROUP
    }

    private void configureWebAppDir(Project project) {
        project.tasks.withType(GaeWebAppDirTask).whenTaskAdded { GaeWebAppDirTask gaeWebAppDirTask ->
            gaeWebAppDirTask.conventionMapping.map('webAppSourceDirectory') { getWarConvention(project).webAppDir }
        }
    }

    private void configureAppConfig(Project project, GaePluginConvention gaePluginConvention) {
        project.tasks.withType(GaeAppConfigTaskTemplate).whenTaskAdded { GaeAppConfigTaskTemplate gaeAppConfigTaskTemplate ->
            gaeAppConfigTaskTemplate.conventionMapping.map('email') { gaePluginConvention.appCfg.email }
            gaeAppConfigTaskTemplate.conventionMapping.map('server') { gaePluginConvention.appCfg.server }
            gaeAppConfigTaskTemplate.conventionMapping.map('host') { gaePluginConvention.appCfg.host }
            gaeAppConfigTaskTemplate.conventionMapping.map('passIn') { gaePluginConvention.appCfg.passIn }
            gaeAppConfigTaskTemplate.conventionMapping.map('password') {
                // Password from gradle.properties takes precedence
                project.hasProperty(GRADLE_USER_PROP_PASSWORD) ? project.property(GRADLE_USER_PROP_PASSWORD) : gaePluginConvention.appCfg.password
            }
            gaeAppConfigTaskTemplate.conventionMapping.map('httpProxy') { gaePluginConvention.appCfg.httpProxy }
            gaeAppConfigTaskTemplate.conventionMapping.map('httpsProxy') { gaePluginConvention.appCfg.httpsProxy }
        }
    }

    private void configureGaeExplodeWarTask(Project project, File explodedWarDirectory) {
        project.tasks.withType(GaeExplodeWarTask).whenTaskAdded { GaeExplodeWarTask gaeExplodeWarTask ->
            gaeExplodeWarTask.conventionMapping.map('warArchive') { project.war.archivePath }
            gaeExplodeWarTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
        }

        GaeExplodeWarTask gaeExplodeWarTask = project.tasks.add(GAE_EXPLODE_WAR, GaeExplodeWarTask)
        gaeExplodeWarTask.description = 'Explodes WAR archive into directory.'
        gaeExplodeWarTask.group = GAE_GROUP
        gaeExplodeWarTask.dependsOn project.war
    }

    private void configureGaeRun(Project project, GaePluginConvention gaePluginConvention, File explodedWarDirectory) {
        project.tasks.withType(GaeRunTask).whenTaskAdded { GaeRunTask gaeRunTask ->
            gaeRunTask.conventionMapping.map('httpPort') { gaePluginConvention.httpPort }
            gaeRunTask.conventionMapping.map(STOP_PORT_CONVENTION_PARAM) { gaePluginConvention.stopPort }
            gaeRunTask.conventionMapping.map(STOP_KEY_CONVENTION_PARAM) { gaePluginConvention.stopKey }
            gaeRunTask.conventionMapping.map('daemon') { gaePluginConvention.daemon }
            gaeRunTask.conventionMapping.map('disableUpdateCheck') { gaePluginConvention.disableUpdateCheck }
            gaeRunTask.conventionMapping.map('jvmFlags') { gaePluginConvention.jvmFlags }
            gaeRunTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { gaePluginConvention.warDir ?: explodedWarDirectory }
        }

        GaeRunTask gaeRunTask = project.tasks.add(GAE_RUN, GaeRunTask)
        gaeRunTask.description = 'Starts up a local App Engine development server.'
        gaeRunTask.group = GAE_GROUP

        project.afterEvaluate {
            // If WAR directory gets set we assume we have a fully functional web application, WAR creation/explosion is skipped
            if(!gaePluginConvention.warDir) {
                gaeRunTask.dependsOn project.tasks.getByName(GAE_EXPLODE_WAR)
            }
        }
    }

    private void configureGaeStop(Project project, GaePluginConvention gaePluginConvention) {
        GaeStopTask gaeStopTask = project.tasks.add(GAE_STOP, GaeStopTask)
        gaeStopTask.description = 'Stops local App Engine development server.'
        gaeStopTask.group = GAE_GROUP
        gaeStopTask.conventionMapping.map(STOP_PORT_CONVENTION_PARAM) { gaePluginConvention.stopPort }
        gaeStopTask.conventionMapping.map(STOP_KEY_CONVENTION_PARAM) { gaePluginConvention.stopKey }
    }

    private void configureGaeEnhance(Project project) {
        project.tasks.withType(GaeEnhanceTask).whenTaskAdded { GaeEnhanceTask gaeEnhanceTask ->
            gaeEnhanceTask.conventionMapping.map('classesDirectory') { project.tasks.compileJava.destinationDir }
        }

        GaeEnhanceTask gaeEnhanceTask = project.tasks.add(GAE_ENHANCE, GaeEnhanceTask)
        gaeEnhanceTask.description = 'Enhances DataNucleus classes.'
        gaeEnhanceTask.group = GAE_GROUP

        if(project.plugins.hasPlugin('groovy')) {
            project.tasks.getByName(GAE_ENHANCE).dependsOn project.compileGroovy
        }
        else {
            project.tasks.getByName(GAE_ENHANCE).dependsOn project.compileJava
        }
    }

    private void configureGaeUpload(Project project, File explodedWarDirectory) {
        project.tasks.withType(GaeUploadTask).whenTaskAdded { GaeUploadTask gaeUploadTask ->
            gaeUploadTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
        }

        GaeUploadTask gaeUploadTask = project.tasks.add(GAE_UPLOAD, GaeUploadTask)
        gaeUploadTask.description = 'Uploads your application to App Engine.'
        gaeUploadTask.group = GAE_GROUP
        gaeUploadTask.dependsOn project.gaeExplodeWar
    }

    private void configureGaeRollback(Project project) {
        GaeRollbackTask gaeRollbackTask = project.tasks.add(GAE_ROLLBACK, GaeRollbackTask)
        gaeRollbackTask.description = 'Undoes a partially completed update for the given application.'
        gaeRollbackTask.group = GAE_GROUP
    }

    private void configureGaeUpdateIndexes(Project project) {
        GaeUpdateIndexesTask gaeUpdateIndexesTask = project.tasks.add(GAE_UPDATE_INDEXES, GaeUpdateIndexesTask)
        gaeUpdateIndexesTask.description = 'Updates indexes on App Engine.'
        gaeUpdateIndexesTask.group = GAE_GROUP
    }

    private void configureGaeVacuumIndexes(Project project) {
        GaeVacuumIndexesTask gaeVacuumIndexesTask = project.tasks.add(GAE_VACUUM_INDEXES, GaeVacuumIndexesTask)
        gaeVacuumIndexesTask.description = 'Deletes unused indexes on App Engine.'
        gaeVacuumIndexesTask.group = GAE_GROUP
    }

    private void configureGaeUpdateTaskQueues(Project project) {
        GaeUpdateQueuesTask gaeUpdateQueuesTask = project.tasks.add(GAE_UPDATE_TASK_QUEUES, GaeUpdateQueuesTask)
        gaeUpdateQueuesTask.description = 'Updates task queues on App Engine.'
        gaeUpdateQueuesTask.group = GAE_GROUP
    }

    private void configureGaeUpdateDoS(Project project) {
        GaeUpdateDoSTask gaeUpdateDoSTask = project.tasks.add(GAE_UPDATE_DOS, GaeUpdateDoSTask)
        gaeUpdateDoSTask.description = 'Updates DoS protection configuration on App Engine.'
        gaeUpdateDoSTask.group = GAE_GROUP
    }

    private void configureGaeUpdateCron(Project project) {
        GaeUpdateCronTask gaeUpdateCronTask = project.tasks.add(GAE_UPDATE_CRON, GaeUpdateCronTask)
        gaeUpdateCronTask.description = 'Updates scheduled tasks definition (known as cron jobs) on App Engine.'
        gaeUpdateCronTask.group = GAE_GROUP
    }

    private void configureGaeCronInfo(Project project) {
        GaeCronInfoTask gaeCronInfoTask = project.tasks.add(GAE_CRON_INFO, GaeCronInfoTask)
        gaeCronInfoTask.description = 'Get cron information from App Engine.'
        gaeCronInfoTask.group = GAE_GROUP
    }

    private void configureGaeDownloadLogs(Project project, GaePluginConvention gaePluginConvention) {
        GaeDownloadLogsTask gaeDownloadLogsTask = project.tasks.add(GAE_LOGS, GaeDownloadLogsTask)
        gaeDownloadLogsTask.description = 'Download logs from App Engine.'
        gaeDownloadLogsTask.group = GAE_GROUP
        gaeDownloadLogsTask.conventionMapping.map('numDays') { gaePluginConvention.appCfg.logs.numDays }
        gaeDownloadLogsTask.conventionMapping.map('severity') { gaePluginConvention.appCfg.logs.severity }
        gaeDownloadLogsTask.conventionMapping.map('append') { gaePluginConvention.appCfg.logs.append }
        gaeDownloadLogsTask.conventionMapping.map('outputFile') { gaePluginConvention.appCfg.logs.outputFile }
    }

    private void configureGaeVersion(Project project) {
        GaeVersionTask gaeVersionTask = project.tasks.add(GAE_VERSION, GaeVersionTask)
        gaeVersionTask.description = 'Prints detailed version information about the SDK, Java and the operating system.'
        gaeVersionTask.group = GAE_GROUP
    }

    /**
     * Configures Google App Engine SDK. If convention property was set to download specific SDK version use that one
     * for all tasks of the plugin.
     *
     * @param project Project
     * @param gaePluginConvention GAE plugin convention
     */
    private void configureGaeSdk(Project project, GaePluginConvention gaePluginConvention) {
        project.tasks.all { Task task ->
            if(task.name.startsWith('gae') && task.name != GAE_DOWNLOAD_SDK) {
                task.dependsOn {
                    if(gaePluginConvention.downloadSdk) {
                        return task.project.tasks.getByName(GAE_DOWNLOAD_SDK)
                    }
                }
            }
        }
    }

    private void configureGaeSingleBackendTask(Project project) {
        project.tasks.withType(AbstractGaeSingleBackendTask).whenTaskAdded { AbstractGaeSingleBackendTask gaeSingleBackendTask ->
            gaeSingleBackendTask.conventionMapping.map('backend') { project.property(BACKEND_PROJECT_PROPERTY) }
        }
    }

    private void configureGaeUpdateBackends(Project project) {
        GaeUpdateBackendTask gaeUpdateBackendsTask = project.tasks.add(GAE_UPDATE_BACKEND, GaeUpdateBackendTask)
        gaeUpdateBackendsTask.description = 'Updates backend on App Engine.'
        gaeUpdateBackendsTask.group = GAE_GROUP
    }

    private void configureGaeUpdateAllBackends(Project project) {
        GaeUpdateAllBackendsTask gaeUpdateAllBackendsTask = project.tasks.add(GAE_UPDATE_ALL_BACKENDS, GaeUpdateAllBackendsTask)
        gaeUpdateAllBackendsTask.description = 'Updates all backends on App Engine.'
        gaeUpdateAllBackendsTask.group = GAE_GROUP
    }

    private void configureGaeRollbackBackends(Project project) {
        GaeRollbackBackendTask gaeRollbackBackendsTask = project.tasks.add(GAE_ROLLBACK_BACKEND, GaeRollbackBackendTask)
        gaeRollbackBackendsTask.description = 'Rolls back backend on App Engine.'
        gaeRollbackBackendsTask.group = GAE_GROUP
    }

    private void configureGaeListBackends(Project project) {
        GaeListBackendsTask gaeListBackendsTask = project.tasks.add(GAE_LIST_BACKENDS, GaeListBackendsTask)
        gaeListBackendsTask.description = 'Lists backends on App Engine.'
        gaeListBackendsTask.group = GAE_GROUP
    }

    private void configureGaeStartBackend(Project project) {
        GaeStartBackendTask gaeStartBackendTask = project.tasks.add(GAE_START_BACKEND, GaeStartBackendTask)
        gaeStartBackendTask.description = 'Starts backend on App Engine.'
        gaeStartBackendTask.group = GAE_GROUP
    }

    private void configureGaeStopBackend(Project project) {
        GaeStopBackendTask gaeStopBackendTask = project.tasks.add(GAE_STOP_BACKEND, GaeStopBackendTask)
        gaeStopBackendTask.description = 'Stops backend on App Engine.'
        gaeStopBackendTask.group = GAE_GROUP
    }

    private void configureGaeDeleteBackend(Project project) {
        GaeDeleteBackendTask gaeDeleteBackendTask = project.tasks.add(GAE_DELETE_BACKEND, GaeDeleteBackendTask)
        gaeDeleteBackendTask.description = 'Deletes backend on App Engine.'
        gaeDeleteBackendTask.group = GAE_GROUP
        gaeDeleteBackendTask.conventionMapping.map('backend') { project.property(BACKEND_PROJECT_PROPERTY) }
    }

    private void configureGaeConfigureBackends(Project project) {
        GaeConfigureBackendsTask gaeConfigureBackendsTask = project.tasks.add(GAE_CONFIGURE_BACKENDS, GaeConfigureBackendsTask)
        gaeConfigureBackendsTask.description = 'Configures backends on App Engine.'
        gaeConfigureBackendsTask.group = GAE_GROUP
        gaeConfigureBackendsTask.conventionMapping.map('setting') { project.property(SETTING_PROJECT_PROPERTY) }
    }

    private WarPluginConvention getWarConvention(Project project) {
        project.convention.getPlugin(WarPluginConvention)
    }
}