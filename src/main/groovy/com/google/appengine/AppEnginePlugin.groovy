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
package com.google.appengine

import com.google.appengine.task.DownloadSdkTask
import com.google.appengine.task.EnhanceTask
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.RunTask
import com.google.appengine.task.StopTask
import com.google.appengine.task.WebAppDirTask
import com.google.appengine.task.endpoints.EndpointsTask
import com.google.appengine.task.endpoints.GetClientLibsTask
import com.google.appengine.task.endpoints.GetDiscoveryDocsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.*
import com.google.appengine.task.appcfg.*
import com.google.appengine.task.appcfg.backends.*
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.plugins.ear.EarPluginConvention
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin

import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME

/**
 * <p>A {@link Plugin} that provides tasks for uploading, running and managing of Google App Engine projects.</p>
 *
 * @author Benjamin Muschko
 * @author Matt Stephenson
 */
class AppEnginePlugin implements Plugin<Project> {
    static final String APPENGINE_SDK_CONFIGURATION_NAME = 'appengineSdk'
    static final String APPENGINE_GROUP = 'Google App Engine'
    static final String APPENGINE_DOWNLOAD_SDK = 'appengineDownloadSdk'
    static final String APPENGINE_RUN = 'appengineRun'
    static final String APPENGINE_STOP = 'appengineStop'
    static final String APPENGINE_ENHANCE = 'appengineEnhance'
    static final String APPENGINE_UPDATE = 'appengineUpdate'
    static final String APPENGINE_ROLLBACK = 'appengineRollback'
    static final String APPENGINE_UPDATE_INDEXES = 'appengineUpdateIndexes'
    static final String APPENGINE_VACUUM_INDEXES = 'appengineVacuumIndexes'
    static final String APPENGINE_UPDATE_TASK_QUEUES = 'appengineUpdateQueues'
    static final String APPENGINE_UPDATE_DOS = 'appengineUpdateDos'
    static final String APPENGINE_UPDATE_CRON = 'appengineUpdateCron'
    static final String APPENGINE_CRON_INFO = 'appengineCronInfo'
    static final String APPENGINE_LOGS = 'appengineLogs'
    static final String APPENGINE_VERSION = 'appengineVersion'
    static final String APPENGINE_DOWNLOAD_APP = 'appengineDownloadApp'
    static final String APPENGINE_EXPLODE_WAR = 'appengineExplodeApp'
    static final String APPENGINE_UPDATE_BACKEND = 'appengineUpdateBackend'
    static final String APPENGINE_UPDATE_ALL_BACKENDS = 'appengineUpdateAllBackends'
    static final String APPENGINE_ROLLBACK_BACKEND = 'appengineRollbackBackend'
    static final String APPENGINE_LIST_BACKENDS = 'appengineListBackends'
    static final String APPENGINE_START_BACKEND = 'appengineStartBackend'
    static final String APPENGINE_STOP_BACKEND = 'appengineStopBackend'
    static final String APPENGINE_DELETE_BACKEND = 'appengineDeleteBackend'
    static final String APPENGINE_CONFIGURE_BACKENDS = 'appengineConfigureBackends'
    static final String APPENGINE_UPDATE_ALL = 'appengineUpdateAll'
    static final String APPENGINE_FUNCTIONAL_TEST = 'appengineFunctionalTest'
    static final String APPENGINE_ENDPOINTS_GET_DISCOVERY_DOCS = "appengineEndpointsGetDiscoveryDocs"
    static final String APPENGINE_ENDPOINTS_GET_CLIENT_LIBS = "appengineEndpointsGetClientLibs"
    static final String GRADLE_USER_PROP_PASSWORD = 'appenginePassword'
    static final String STOP_PORT_CONVENTION_PARAM = 'stopPort'
    static final String STOP_KEY_CONVENTION_PARAM = 'stopKey'
    static final String EXPLODED_WAR_DIR_CONVENTION_PARAM = 'explodedAppDirectory'
    static final String EXPLODED_SDK_DIR_CONVENTION_PARAM = 'explodedSdkDirectory'
    static final String ENDPOINTS_CLIENT_LIB_CONVENTION_PARAM = "endpointsClientLibDirectory"
    static final String BACKEND_PROJECT_PROPERTY = 'backend'
    static final String SETTING_PROJECT_PROPERTY = 'setting'
    static final String FUNCTIONAL_TEST_COMPILE_CONFIGURATION = 'functionalTestCompile'
    static final String FUNCTIONAL_TEST_RUNTIME_CONFIGURATION = 'functionalTestRuntime'
    static final String FUNCTIONAL_TEST_SOURCE_SET = 'functionalTest'

    @Override
    void apply(Project project) {
        project.configurations.create(APPENGINE_SDK_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
                .setDescription('The Google App Engine SDK to be downloaded and used for this project.')

        AppEnginePluginConvention appenginePluginConvention = new AppEnginePluginConvention()
        project.convention.plugins.appengine = appenginePluginConvention

        File explodedSdkDirectory = getExplodedSdkDirectory(project)
        File explodedAppDirectory = getExplodedAppDirectory(project)
        File downloadedAppDirectory = getDownloadedAppDirectory(project)
        File endpointsClientLibDirectory = getEndpointsClientLibDirectory(project)
        configureDownloadSdk(project, explodedSdkDirectory)
        configureWebAppDir(project)
        configureAppConfig(project, appenginePluginConvention)
        configureExplodeWarTask(project, appenginePluginConvention, explodedAppDirectory)
        configureRun(project, appenginePluginConvention, explodedAppDirectory)
        configureStop(project, appenginePluginConvention)
        configureUpdate(project, appenginePluginConvention, explodedAppDirectory)
        configureRollback(project)
        configureSdk(project, appenginePluginConvention)
        configureEnhance(project)
        configureUpdateIndexes(project)
        configureVacuumIndexes(project)
        configureUpdateTaskQueues(project)
        configureUpdateDoS(project)
        configureUpdateCron(project)
        configureCronInfo(project)
        configureDownloadLogs(project, appenginePluginConvention)
        configureVersion(project)
        configureDownloadApplication(project, appenginePluginConvention, downloadedAppDirectory)
        configureSingleBackendTask(project)
        configureUpdateBackends(project, explodedAppDirectory)
        configureUpdateAllBackends(project, explodedAppDirectory)
        configureRollbackBackends(project)
        configureListBackends(project)
        configureStartBackend(project)
        configureStopBackend(project)
        configureDeleteBackend(project)
        configureConfigureBackends(project)
        configureUpdateAll(project)
        configureEndpoints(project, explodedAppDirectory, endpointsClientLibDirectory, appenginePluginConvention)
        configureFunctionalTest(project, appenginePluginConvention)
    }

    private File getExplodedSdkDirectory(Project project) {
        new File(project.gradle.gradleUserHomeDir, 'appengine-sdk')
    }

    private File getExplodedAppDirectory(Project project) {
        getBuildSubDirectory(project, 'exploded-app')
    }

    private File getDownloadedAppDirectory(Project project) {
        getBuildSubDirectory(project, 'downloaded-app')
    }

    private File getEndpointsClientLibDirectory(Project project) {
        getBuildSubDirectory(project, 'client-libs');
    }

    private File getBuildSubDirectory(Project project, String subDirectory) {
        def explodedWarDirName = new StringBuilder()
        explodedWarDirName <<= project.buildDir
        explodedWarDirName <<= System.getProperty('file.separator')
        explodedWarDirName <<= subDirectory
        new File(explodedWarDirName.toString())
    }

    private void configureDownloadSdk(Project project, File explodedSdkDirectory) {
        project.tasks.withType(DownloadSdkTask).whenTaskAdded { DownloadSdkTask appengineDownloadSdkTask ->
            appengineDownloadSdkTask.conventionMapping.map('appengineSdkZipFile') {
                try {
                    project.configurations.getByName(APPENGINE_SDK_CONFIGURATION_NAME).singleFile
                }
                catch(IllegalStateException e) {
                    // make "gradle tasks" happy in case we don't declare configuration!
                }
            }
            appengineDownloadSdkTask.conventionMapping.map(EXPLODED_SDK_DIR_CONVENTION_PARAM) { explodedSdkDirectory }
        }

        DownloadSdkTask appengineDownloadSdkTask = project.tasks.create(APPENGINE_DOWNLOAD_SDK, DownloadSdkTask)
        appengineDownloadSdkTask.description = 'Downloads and sets Google App Engine SDK.'
        appengineDownloadSdkTask.group = APPENGINE_GROUP
    }

    private void configureWebAppDir(Project project) {
        project.tasks.withType(WebAppDirTask).whenTaskAdded { WebAppDirTask appengineWebAppDirTask ->
            appengineWebAppDirTask.conventionMapping.map('webAppSourceDirectory') { getAppDir(project) }
        }
    }

    private void configureAppConfig(Project project, AppEnginePluginConvention appenginePluginConvention) {
        project.tasks.withType(AppConfigTaskTemplate).whenTaskAdded { AppConfigTaskTemplate appengineAppConfigTaskTemplate ->
            appengineAppConfigTaskTemplate.conventionMapping.map('email') { appenginePluginConvention.appCfg.email }
            appengineAppConfigTaskTemplate.conventionMapping.map('server') { appenginePluginConvention.appCfg.server }
            appengineAppConfigTaskTemplate.conventionMapping.map('host') { appenginePluginConvention.appCfg.host }
            appengineAppConfigTaskTemplate.conventionMapping.map('noCookies') { appenginePluginConvention.appCfg.noCookies }
            appengineAppConfigTaskTemplate.conventionMapping.map('passIn') { appenginePluginConvention.appCfg.passIn }
            appengineAppConfigTaskTemplate.conventionMapping.map('password') {
                // Password from gradle.properties takes precedence
                project.hasProperty(GRADLE_USER_PROP_PASSWORD) ? project.property(GRADLE_USER_PROP_PASSWORD) : appenginePluginConvention.appCfg.password
            }
            appengineAppConfigTaskTemplate.conventionMapping.map('httpProxy') { appenginePluginConvention.appCfg.httpProxy }
            appengineAppConfigTaskTemplate.conventionMapping.map('httpsProxy') { appenginePluginConvention.appCfg.httpsProxy }
            appengineAppConfigTaskTemplate.conventionMapping.map('oauth2') { appenginePluginConvention.appCfg.oauth2 }
        }
    }

    private void configureExplodeWarTask(Project project, AppEnginePluginConvention appenginePluginConvention, File explodedWarDirectory) {
        project.tasks.withType(ExplodeAppTask).whenTaskAdded { ExplodeAppTask appengineExplodeAppTask ->
            appengineExplodeAppTask.conventionMapping.map('archive') { project.hasProperty('ear') ? project.ear.archivePath : project.war.archivePath }
            appengineExplodeAppTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
        }

        ExplodeAppTask appengineExplodeAppTask = project.tasks.create(APPENGINE_EXPLODE_WAR, ExplodeAppTask)
        appengineExplodeAppTask.description = 'Explodes WAR archive into directory.'
        appengineExplodeAppTask.group = APPENGINE_GROUP

        project.afterEvaluate {
            if(project.hasProperty('ear')) {
                appengineExplodeAppTask.dependsOn project.ear
            } else if(project.hasProperty('war')) {
                appengineExplodeAppTask.dependsOn project.war
            }
        }
    }

    private void configureRun(Project project, AppEnginePluginConvention appenginePluginConvention, File explodedAppDirectory) {
        project.tasks.withType(RunTask).whenTaskAdded { RunTask appengineRunTask ->
            appengineRunTask.conventionMapping.map('httpAddress') { appenginePluginConvention.httpAddress }
            appengineRunTask.conventionMapping.map('httpPort') { appenginePluginConvention.httpPort }
            appengineRunTask.conventionMapping.map(STOP_PORT_CONVENTION_PARAM) { appenginePluginConvention.stopPort }
            appengineRunTask.conventionMapping.map(STOP_KEY_CONVENTION_PARAM) { appenginePluginConvention.stopKey }
            appengineRunTask.conventionMapping.map('daemon') { appenginePluginConvention.daemon }
            appengineRunTask.conventionMapping.map('disableUpdateCheck') { appenginePluginConvention.disableUpdateCheck }
            appengineRunTask.conventionMapping.map('jvmFlags') { appenginePluginConvention.jvmFlags }
            appengineRunTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { appenginePluginConvention.warDir ?: explodedAppDirectory }
        }

        RunTask appengineRunTask = project.tasks.create(APPENGINE_RUN, RunTask)
        appengineRunTask.description = 'Starts up a local App Engine development server.'
        appengineRunTask.group = APPENGINE_GROUP

        ExplodeAppTask appengineExplodeAppTask = project.tasks.getByName(APPENGINE_EXPLODE_WAR)
        appengineRunTask.dependsOn appengineExplodeAppTask

        // If WAR directory gets set we assume we have a fully functional web application, WAR creation/explosion is skipped
        appengineExplodeAppTask.onlyIf { !appenginePluginConvention.warDir || !project.gradle.taskGraph.hasTask(appengineRunTask) }
    }

    private void configureStop(Project project, AppEnginePluginConvention appenginePluginConvention) {
        StopTask appengineStopTask = project.tasks.create(APPENGINE_STOP, StopTask)
        appengineStopTask.description = 'Stops local App Engine development server.'
        appengineStopTask.group = APPENGINE_GROUP
        appengineStopTask.conventionMapping.map(STOP_PORT_CONVENTION_PARAM) { appenginePluginConvention.stopPort }
        appengineStopTask.conventionMapping.map(STOP_KEY_CONVENTION_PARAM) { appenginePluginConvention.stopKey }
    }

    private void configureEnhance(Project project) {
        project.tasks.withType(EnhanceTask).whenTaskAdded { EnhanceTask appengineEnhanceTask ->
            appengineEnhanceTask.conventionMapping.map('classesDirectory') { project.tasks.compileJava.destinationDir }
        }

        EnhanceTask appengineEnhanceTask = project.tasks.create(APPENGINE_ENHANCE, EnhanceTask)
        appengineEnhanceTask.description = 'Enhances DataNucleus classes.'
        appengineEnhanceTask.group = APPENGINE_GROUP

        if(project.plugins.hasPlugin('groovy')) {
            project.tasks.getByName(APPENGINE_ENHANCE).dependsOn project.compileGroovy
        }
        else {
            project.tasks.getByName(APPENGINE_ENHANCE).dependsOn project.compileJava
        }
    }

    private void configureUpdate(Project project, AppEnginePluginConvention appenginePluginConvention, File explodedWarDirectory) {
        project.tasks.withType(UpdateTask).whenTaskAdded { UpdateTask appengineUpdateTask ->
            appengineUpdateTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
            appengineUpdateTask.conventionMapping.map('useJava7') { appenginePluginConvention.appCfg.update.useJava7 }
        }

        UpdateTask appengineUpdateTask = project.tasks.create(APPENGINE_UPDATE, UpdateTask)
        appengineUpdateTask.description = 'Updates your application on App Engine.'
        appengineUpdateTask.group = APPENGINE_GROUP
        appengineUpdateTask.dependsOn project.appengineExplodeApp
    }

    private void configureRollback(Project project) {
        RollbackTask appengineRollbackTask = project.tasks.create(APPENGINE_ROLLBACK, RollbackTask)
        appengineRollbackTask.description = 'Undoes a partially completed update for the given application.'
        appengineRollbackTask.group = APPENGINE_GROUP
    }

    private void configureUpdateIndexes(Project project) {
        UpdateIndexesTask appengineUpdateIndexesTask = project.tasks.create(APPENGINE_UPDATE_INDEXES, UpdateIndexesTask)
        appengineUpdateIndexesTask.description = 'Updates indexes on App Engine.'
        appengineUpdateIndexesTask.group = APPENGINE_GROUP
    }

    private void configureVacuumIndexes(Project project) {
        VacuumIndexesTask appengineVacuumIndexesTask = project.tasks.create(APPENGINE_VACUUM_INDEXES, VacuumIndexesTask)
        appengineVacuumIndexesTask.description = 'Deletes unused indexes on App Engine.'
        appengineVacuumIndexesTask.group = APPENGINE_GROUP
    }

    private void configureUpdateTaskQueues(Project project) {
        UpdateQueuesTask appengineUpdateQueuesTask = project.tasks.create(APPENGINE_UPDATE_TASK_QUEUES, UpdateQueuesTask)
        appengineUpdateQueuesTask.description = 'Updates task queues on App Engine.'
        appengineUpdateQueuesTask.group = APPENGINE_GROUP
    }

    private void configureUpdateDoS(Project project) {
        UpdateDoSTask appengineUpdateDoSTask = project.tasks.create(APPENGINE_UPDATE_DOS, UpdateDoSTask)
        appengineUpdateDoSTask.description = 'Updates DoS protection configuration on App Engine.'
        appengineUpdateDoSTask.group = APPENGINE_GROUP
    }

    private void configureUpdateCron(Project project) {
        UpdateCronTask appengineUpdateCronTask = project.tasks.create(APPENGINE_UPDATE_CRON, UpdateCronTask)
        appengineUpdateCronTask.description = 'Updates scheduled tasks definition (known as cron jobs) on App Engine.'
        appengineUpdateCronTask.group = APPENGINE_GROUP
    }

    private void configureCronInfo(Project project) {
        CronInfoTask appengineCronInfoTask = project.tasks.create(APPENGINE_CRON_INFO, CronInfoTask)
        appengineCronInfoTask.description = 'Get cron information from App Engine.'
        appengineCronInfoTask.group = APPENGINE_GROUP
    }

    private void configureDownloadLogs(Project project, AppEnginePluginConvention appenginePluginConvention) {
        DownloadLogsTask appengineDownloadLogsTask = project.tasks.create(APPENGINE_LOGS, DownloadLogsTask)
        appengineDownloadLogsTask.description = 'Download logs from App Engine.'
        appengineDownloadLogsTask.group = APPENGINE_GROUP
        appengineDownloadLogsTask.conventionMapping.map('numDays') { appenginePluginConvention.appCfg.logs.numDays }
        appengineDownloadLogsTask.conventionMapping.map('severity') { appenginePluginConvention.appCfg.logs.severity }
        appengineDownloadLogsTask.conventionMapping.map('append') { appenginePluginConvention.appCfg.logs.append }
        appengineDownloadLogsTask.conventionMapping.map('outputFile') { appenginePluginConvention.appCfg.logs.outputFile }
    }

    private void configureVersion(Project project) {
        VersionTask appengineVersionTask = project.tasks.create(APPENGINE_VERSION, VersionTask)
        appengineVersionTask.description = 'Prints detailed version information about the SDK, Java and the operating system.'
        appengineVersionTask.group = APPENGINE_GROUP
    }

    private void configureDownloadApplication(Project project, AppEnginePluginConvention appenginePluginConvention, File downloadedAppDirectory) {
        DownloadAppTask appengineDownloadAppTask = project.tasks.create(APPENGINE_DOWNLOAD_APP, DownloadAppTask)
        appengineDownloadAppTask.description = 'Retrieves the most current version of your application.'
        appengineDownloadAppTask.group = APPENGINE_GROUP
        appengineDownloadAppTask.conventionMapping.map('appId') { appenginePluginConvention.appCfg.app.id }
        appengineDownloadAppTask.conventionMapping.map('appVersion') { appenginePluginConvention.appCfg.app.version }
        appengineDownloadAppTask.conventionMapping.map('outputDirectory') { appenginePluginConvention.appCfg.app.outputDirectory ?: downloadedAppDirectory }
    }

    /**
     * Configures Google App Engine SDK. If convention property was set to download specific SDK version use that one
     * for all tasks of the plugin.
     *
     * @param project Project
     * @param appenginePluginConvention APPENGINE plugin convention
     */
    private void configureSdk(Project project, AppEnginePluginConvention appenginePluginConvention) {
        project.tasks.all { Task task ->
            if(task.name.startsWith('appengine') && task.name != APPENGINE_DOWNLOAD_SDK) {
                task.dependsOn {
                    if(appenginePluginConvention.downloadSdk) {
                        return task.project.tasks.getByName(APPENGINE_DOWNLOAD_SDK)
                    }
                }
            }
        }
    }

    private void configureSingleBackendTask(Project project) {
        project.tasks.withType(AbstractSingleBackendTask).whenTaskAdded { AbstractSingleBackendTask appengineSingleBackendTask ->
            appengineSingleBackendTask.conventionMapping.map('backend') { project.property(BACKEND_PROJECT_PROPERTY) }
        }
    }

    private void configureUpdateBackends(Project project, File explodedWarDirectory) {
        UpdateBackendTask appengineUpdateBackendsTask = project.tasks.create(APPENGINE_UPDATE_BACKEND, UpdateBackendTask)
        appengineUpdateBackendsTask.description = 'Updates backend on App Engine.'
        appengineUpdateBackendsTask.group = APPENGINE_GROUP
        appengineUpdateBackendsTask.conventionMapping.map('backend') { project.property(BACKEND_PROJECT_PROPERTY) }
        appengineUpdateBackendsTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
        appengineUpdateBackendsTask.dependsOn project.appengineExplodeApp
    }

    private void configureUpdateAllBackends(Project project, File explodedWarDirectory) {
        UpdateAllBackendsTask appengineUpdateAllBackendsTask = project.tasks.create(APPENGINE_UPDATE_ALL_BACKENDS, UpdateAllBackendsTask)
        appengineUpdateAllBackendsTask.description = 'Updates all backends on App Engine.'
        appengineUpdateAllBackendsTask.group = APPENGINE_GROUP
        appengineUpdateAllBackendsTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
        appengineUpdateAllBackendsTask.dependsOn project.appengineExplodeApp
    }

    private void configureRollbackBackends(Project project) {
        RollbackBackendTask appengineRollbackBackendsTask = project.tasks.create(APPENGINE_ROLLBACK_BACKEND, RollbackBackendTask)
        appengineRollbackBackendsTask.description = 'Rolls back backend on App Engine.'
        appengineRollbackBackendsTask.group = APPENGINE_GROUP
    }

    private void configureListBackends(Project project) {
        ListBackendsTask appengineListBackendsTask = project.tasks.create(APPENGINE_LIST_BACKENDS, ListBackendsTask)
        appengineListBackendsTask.description = 'Lists backends on App Engine.'
        appengineListBackendsTask.group = APPENGINE_GROUP
    }

    private void configureStartBackend(Project project) {
        StartBackendTask appengineStartBackendTask = project.tasks.create(APPENGINE_START_BACKEND, StartBackendTask)
        appengineStartBackendTask.description = 'Starts backend on App Engine.'
        appengineStartBackendTask.group = APPENGINE_GROUP
    }

    private void configureStopBackend(Project project) {
        StopBackendTask appengineStopBackendTask = project.tasks.create(APPENGINE_STOP_BACKEND, StopBackendTask)
        appengineStopBackendTask.description = 'Stops backend on App Engine.'
        appengineStopBackendTask.group = APPENGINE_GROUP
    }

    private void configureDeleteBackend(Project project) {
        DeleteBackendTask appengineDeleteBackendTask = project.tasks.create(APPENGINE_DELETE_BACKEND, DeleteBackendTask)
        appengineDeleteBackendTask.description = 'Deletes backend on App Engine.'
        appengineDeleteBackendTask.group = APPENGINE_GROUP
        appengineDeleteBackendTask.conventionMapping.map('backend') { project.property(BACKEND_PROJECT_PROPERTY) }
    }

    private void configureConfigureBackends(Project project) {
        ConfigureBackendsTask appengineConfigureBackendsTask = project.tasks.create(APPENGINE_CONFIGURE_BACKENDS, ConfigureBackendsTask)
        appengineConfigureBackendsTask.description = 'Configures backends on App Engine.'
        appengineConfigureBackendsTask.group = APPENGINE_GROUP
        appengineConfigureBackendsTask.conventionMapping.map('setting') { project.property(SETTING_PROJECT_PROPERTY) }
    }

    private void configureUpdateAll(Project project) {
        Task appengineUpdateAllTask = project.tasks.create(APPENGINE_UPDATE_ALL)
        appengineUpdateAllTask.description = 'Updates your application and all backends on App Engine.'
        appengineUpdateAllTask.group = APPENGINE_GROUP
        appengineUpdateAllTask.dependsOn project.appengineUpdate, project.appengineUpdateAllBackends
    }

    public void configureEndpoints(Project project, File explodedWarDirectory, File endpointsClientLibDirectory, AppEnginePluginConvention appEnginePluginConvention) {
        project.tasks.withType(EndpointsTask).whenTaskAdded { EndpointsTask endpointsTask ->
            endpointsTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
            endpointsTask.conventionMapping.map(ENDPOINTS_CLIENT_LIB_CONVENTION_PARAM) { endpointsClientLibDirectory }
            endpointsTask.conventionMapping.map("discoveryDocFormat") { appEnginePluginConvention.endpoints.discoveryDocFormat }
        }

        GetDiscoveryDocsTask endpointsGetDiscoveryDocs = project.tasks.create(APPENGINE_ENDPOINTS_GET_DISCOVERY_DOCS, GetDiscoveryDocsTask)
        endpointsGetDiscoveryDocs.description = 'Generate Endpoints discovery docs for classes defined in web.xml'
        endpointsGetDiscoveryDocs.group = APPENGINE_GROUP

        GetClientLibsTask endpointsGetClientLibs = project.tasks.create(APPENGINE_ENDPOINTS_GET_CLIENT_LIBS, GetClientLibsTask)
        endpointsGetClientLibs.description = 'Generate Endpoints java client libraries for classes defined in web.xml'
        endpointsGetClientLibs.group = APPENGINE_GROUP

        project.gradle.projectsEvaluated {
            if(appEnginePluginConvention.endpoints.getDiscoveryDocsOnBuild) {
                endpointsGetDiscoveryDocs.dependsOn(project.tasks.getByName(APPENGINE_EXPLODE_WAR))
                project.tasks.getByName(JavaBasePlugin.BUILD_TASK_NAME).dependsOn(endpointsGetDiscoveryDocs);
            }
            if(appEnginePluginConvention.endpoints.getClientLibsOnBuild) {
                endpointsGetClientLibs.dependsOn(project.tasks.getByName(APPENGINE_EXPLODE_WAR))
                project.tasks.getByName(JavaBasePlugin.BUILD_TASK_NAME).dependsOn(endpointsGetClientLibs);
            }
        }
    }

    private void configureFunctionalTest(Project project, AppEnginePluginConvention convention) {
        SourceSet functionalSourceSet = addFunctionalTestConfigurationsAndSourceSet(project)

        Test appengineFunctionalTest = project.tasks.create(APPENGINE_FUNCTIONAL_TEST, Test)
        appengineFunctionalTest.description = 'Runs functional tests'
        appengineFunctionalTest.group = APPENGINE_GROUP
        appengineFunctionalTest.testClassesDir = functionalSourceSet.output.classesDir
        appengineFunctionalTest.classpath = functionalSourceSet.runtimeClasspath
        appengineFunctionalTest.dependsOn(project.tasks.getByName(APPENGINE_RUN))

        project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
            if(taskGraph.hasTask(appengineFunctionalTest)) {
                convention.daemon = true
            }
        }

        project.tasks.getByName(JavaBasePlugin.CHECK_TASK_NAME).dependsOn appengineFunctionalTest
    }

    private SourceSet addFunctionalTestConfigurationsAndSourceSet(Project project) {
        ConfigurationContainer configurations = project.configurations
        Configuration functionalTestCompileConfiguration = configurations.create(FUNCTIONAL_TEST_COMPILE_CONFIGURATION)
        Configuration testCompileConfiguration = configurations.getByName(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME)
        functionalTestCompileConfiguration.extendsFrom(testCompileConfiguration)

        Configuration functionalTestRuntimeConfiguration = configurations.create(FUNCTIONAL_TEST_RUNTIME_CONFIGURATION)
        Configuration testRuntimeConfiguration = configurations.getByName(JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME)
        functionalTestRuntimeConfiguration.extendsFrom(functionalTestCompileConfiguration, testRuntimeConfiguration)

        SourceSetContainer sourceSets = project.convention.getPlugin(JavaPluginConvention).sourceSets
        SourceSet mainSourceSet = sourceSets.getByName(MAIN_SOURCE_SET_NAME)
        SourceSet functionalSourceSet = sourceSets.create(FUNCTIONAL_TEST_SOURCE_SET)
        functionalSourceSet.compileClasspath = mainSourceSet.output + functionalTestCompileConfiguration
        functionalSourceSet.runtimeClasspath = functionalSourceSet.output + functionalTestRuntimeConfiguration

        addIdeaConfigurationForFunctionalTestSourceSet(project, functionalTestCompileConfiguration, functionalTestRuntimeConfiguration, functionalSourceSet)
        addEclipseConfigurationForFunctionalTestRuntimeConfiguration(project, functionalTestRuntimeConfiguration)

        functionalSourceSet
    }

    private void addIdeaConfigurationForFunctionalTestSourceSet(Project project, Configuration compile, Configuration runtime, SourceSet sourceSet) {
        project.plugins.withType(IdeaPlugin) { IdeaPlugin plugin ->
            plugin.model.module {
                testSourceDirs += sourceSet.allSource.srcDirs
                scopes.TEST.plus += compile
                scopes.TEST.plus += runtime
            }
        }
    }

    private void addEclipseConfigurationForFunctionalTestRuntimeConfiguration(Project project, Configuration functionalTestRuntimeConfiguration) {
        project.plugins.withType(EclipsePlugin) { EclipsePlugin plugin ->
            plugin.model.classpath.plusConfigurations += functionalTestRuntimeConfiguration
        }
    }

    private File getAppDir(Project project) {
        if(project.hasProperty('ear')) {
            return new File(project.projectDir, project.convention.getPlugin(EarPluginConvention).appDirName)
        } else if(project.hasProperty('war')) {
            return project.convention.getPlugin(WarPluginConvention).webAppDir
        }
    }
}

