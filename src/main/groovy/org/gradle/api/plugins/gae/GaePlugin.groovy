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

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.UnknownPluginException;
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.api.plugins.*
import org.gradle.api.plugins.gae.task.*
import org.gradle.api.plugins.gae.task.appcfg.*
import org.gradle.api.plugins.gae.task.appcfg.backends.*
import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME

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
    static final String GAE_DOWNLOAD_APP = 'gaeDownloadApp'
    static final String GAE_EXPLODE_WAR = 'gaeExplodeWar'
    static final String GAE_UPDATE_BACKEND = 'gaeUpdateBackend'
    static final String GAE_UPDATE_ALL_BACKENDS = 'gaeUpdateAllBackends'
    static final String GAE_ROLLBACK_BACKEND = 'gaeRollbackBackend'
    static final String GAE_LIST_BACKENDS = 'gaeListBackends'
    static final String GAE_START_BACKEND = 'gaeStartBackend'
    static final String GAE_STOP_BACKEND = 'gaeStopBackend'
    static final String GAE_DELETE_BACKEND = 'gaeDeleteBackend'
    static final String GAE_CONFIGURE_BACKENDS = 'gaeConfigureBackends'
    static final String GAE_UPLOAD_ALL = 'gaeUploadAll'
    static final String GAE_FUNCTIONAL_TEST = 'gaeFunctionalTest'
    static final String GRADLE_USER_PROP_PASSWORD = 'gaePassword'
    static final String STOP_PORT_CONVENTION_PARAM = 'stopPort'
    static final String STOP_KEY_CONVENTION_PARAM = 'stopKey'
    static final String EXPLODED_WAR_DIR_CONVENTION_PARAM = 'explodedWarDirectory'
    static final String EXPLODED_SDK_DIR_CONVENTION_PARAM = 'explodedSdkDirectory'
    static final String BACKEND_PROJECT_PROPERTY = 'backend'
    static final String SETTING_PROJECT_PROPERTY = 'setting'
    static final String FUNCTIONAL_TEST_COMPILE_CONFIGURATION = 'functionalTestCompile'
    static final String FUNCTIONAL_TEST_RUNTIME_CONFIGURATION = 'functionalTestRuntime'
    static final String FUNCTIONAL_TEST_SOURCE_SET = 'functionalTest'

    @Override
    void apply(Project project) {
        project.plugins.apply(WarPlugin)
        
       try {
            project.plugins.apply('fatjar')
            project.slimWar.doFirst {
                def webAppDir = project.convention.getPlugin(WarPluginConvention).webAppDir
                        project.ant.delete dir: new File(webAppDir, 'WEB-INF/lib')
            }
        } catch (UnknownPluginException e){
            project.logger.info 'FatJar not installed.'
        }
        

        project.configurations.add(GAE_SDK_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
                .setDescription('The Google App Engine SDK to be downloaded and used for this project.')

        GaePluginConvention gaePluginConvention = new GaePluginConvention()
        project.convention.plugins.gae = gaePluginConvention
        
        File explodedSdkDirectory = getExplodedSdkDirectory(project)
        File explodedWarDirectory = getExplodedWarDirectory(project)
        File downloadedAppDirectory = getDownloadedAppDirectory(project)
        configureDownloadSdk(project, explodedSdkDirectory)
        configureWebAppDir(project)
        configureAppConfig(project, gaePluginConvention)
        configureGaeExplodeWarTask(project, gaePluginConvention, explodedWarDirectory)
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
        configureGaeDownloadApplication(project, gaePluginConvention, downloadedAppDirectory)
        configureGaeSdk(project, gaePluginConvention)
        configureGaeSingleBackendTask(project)
        configureGaeUpdateBackends(project, explodedWarDirectory)
        configureGaeUpdateAllBackends(project, explodedWarDirectory)
        configureGaeRollbackBackends(project)
        configureGaeListBackends(project)
        configureGaeStartBackend(project)
        configureGaeStopBackend(project)
        configureGaeDeleteBackend(project)
        configureGaeConfigureBackends(project)
        configureGaeUploadAll(project)
        configureGaeFunctionalTest(project, gaePluginConvention)
    }

    private File getExplodedSdkDirectory(Project project) {
        new File(project.gradle.gradleUserHomeDir, 'gae-sdk')
    }

    private File getExplodedWarDirectory(Project project) {
        getBuildSubDirectory(project, 'exploded-war')
    }

    private File getDownloadedAppDirectory(Project project) {
        getBuildSubDirectory(project, 'downloaded-app')
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

    private void configureGaeExplodeWarTask(Project project, GaePluginConvention gaePluginConvention, File explodedWarDirectory) {
        project.tasks.withType(GaeExplodeWarTask).whenTaskAdded { GaeExplodeWarTask gaeExplodeWarTask ->
            gaeExplodeWarTask.conventionMapping.map('warArchive') {
                gaePluginConvention.optimizeWar && project.plugins.hasPlugin('fatjar') ? project.slimWar.archivePath : project.war.archivePath
            }
            gaeExplodeWarTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
            gaeExplodeWarTask.conventionMapping.map('cleanClasses') { gaePluginConvention.optimizeWar && project.plugins.hasPlugin('fatjar')}
        }

        GaeExplodeWarTask gaeExplodeWarTask = project.tasks.add(GAE_EXPLODE_WAR, GaeExplodeWarTask)
        gaeExplodeWarTask.description = 'Explodes WAR archive into directory.'
        gaeExplodeWarTask.group = GAE_GROUP

        project.afterEvaluate {
            if(gaePluginConvention.optimizeWar && project.plugins.hasPlugin('fatjar')){
                gaeExplodeWarTask.dependsOn project.slimWar
            } else {
                gaeExplodeWarTask.dependsOn project.war
            }
        }
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

        GaeExplodeWarTask gaeExplodeWarTask = project.tasks.getByName(GAE_EXPLODE_WAR)
        gaeRunTask.dependsOn gaeExplodeWarTask

        // If WAR directory gets set we assume we have a fully functional web application, WAR creation/explosion is skipped
        gaeExplodeWarTask.onlyIf { !gaePluginConvention.warDir || !project.gradle.taskGraph.hasTask(gaeRunTask) }
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

    private void configureGaeDownloadApplication(Project project, GaePluginConvention gaePluginConvention, File downloadedAppDirectory) {
        GaeDownloadAppTask gaeDownloadAppTask = project.tasks.add(GAE_DOWNLOAD_APP, GaeDownloadAppTask)
        gaeDownloadAppTask.description = 'Retrieves the most current version of your application.'
        gaeDownloadAppTask.group = GAE_GROUP
        gaeDownloadAppTask.conventionMapping.map('appId') { gaePluginConvention.appCfg.app.id }
        gaeDownloadAppTask.conventionMapping.map('appVersion') { gaePluginConvention.appCfg.app.version }
        gaeDownloadAppTask.conventionMapping.map('outputDirectory') { gaePluginConvention.appCfg.app.outputDirectory ?: downloadedAppDirectory }
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

    private void configureGaeUpdateBackends(Project project, File explodedWarDirectory) {
        GaeUpdateBackendTask gaeUpdateBackendsTask = project.tasks.add(GAE_UPDATE_BACKEND, GaeUpdateBackendTask)
        gaeUpdateBackendsTask.description = 'Updates backend on App Engine.'
        gaeUpdateBackendsTask.group = GAE_GROUP
        gaeUpdateBackendsTask.conventionMapping.map('backend') { project.property(BACKEND_PROJECT_PROPERTY) }
        gaeUpdateBackendsTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
        gaeUpdateBackendsTask.dependsOn project.gaeExplodeWar
    }

    private void configureGaeUpdateAllBackends(Project project, File explodedWarDirectory) {
        GaeUpdateAllBackendsTask gaeUpdateAllBackendsTask = project.tasks.add(GAE_UPDATE_ALL_BACKENDS, GaeUpdateAllBackendsTask)
        gaeUpdateAllBackendsTask.description = 'Updates all backends on App Engine.'
        gaeUpdateAllBackendsTask.group = GAE_GROUP
        gaeUpdateAllBackendsTask.conventionMapping.map(EXPLODED_WAR_DIR_CONVENTION_PARAM) { explodedWarDirectory }
        gaeUpdateAllBackendsTask.dependsOn project.gaeExplodeWar
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

    private void configureGaeUploadAll(Project project) {
        Task gaeUploadAllTask = project.tasks.add(GAE_UPLOAD_ALL)
        gaeUploadAllTask.description = 'Uploads your application to App Engine and updates all backends.'
        gaeUploadAllTask.group = GAE_GROUP
        gaeUploadAllTask.dependsOn project.gaeUpload, project.gaeUpdateAllBackends
    }

    private void configureGaeFunctionalTest(Project project, GaePluginConvention convention) {
        SourceSet functionalSourceSet = addFunctionalTestConfigurationsAndSourceSet(project)

        Test gaeFunctionalTest = project.tasks.add(GAE_FUNCTIONAL_TEST, Test)
        gaeFunctionalTest.description = 'Runs functional tests'
        gaeFunctionalTest.group = GAE_GROUP
        gaeFunctionalTest.testClassesDir = functionalSourceSet.output.classesDir
        gaeFunctionalTest.classpath = functionalSourceSet.runtimeClasspath
        gaeFunctionalTest.dependsOn(project.tasks.getByName(GAE_RUN))

        project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
            if (taskGraph.hasTask(gaeFunctionalTest)) {
                convention.daemon = true
            }
        }

        project.tasks.getByName(JavaBasePlugin.CHECK_TASK_NAME).dependsOn(gaeFunctionalTest)
    }

    private SourceSet addFunctionalTestConfigurationsAndSourceSet(Project project) {
        ConfigurationContainer configurations = project.configurations
        Configuration functionalTestCompileConfiguration = configurations.add(FUNCTIONAL_TEST_COMPILE_CONFIGURATION)
        Configuration testCompileConfiguration = configurations.getByName(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME)
        functionalTestCompileConfiguration.extendsFrom(testCompileConfiguration)

        Configuration functionalTestRuntimeConfiguration = configurations.add(FUNCTIONAL_TEST_RUNTIME_CONFIGURATION)
        Configuration testRuntimeConfiguration = configurations.getByName(JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME)
        functionalTestRuntimeConfiguration.extendsFrom(functionalTestCompileConfiguration, testRuntimeConfiguration)

        SourceSetContainer sourceSets = project.convention.getPlugin(JavaPluginConvention).sourceSets
        SourceSet mainSourceSet = sourceSets.getByName(MAIN_SOURCE_SET_NAME)
        SourceSet functionalSourceSet = sourceSets.add(FUNCTIONAL_TEST_SOURCE_SET)
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

    private WarPluginConvention getWarConvention(Project project) {
        project.convention.getPlugin(WarPluginConvention)
    }
}
