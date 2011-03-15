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
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.plugins.gae.task.GaeEnhanceTask
import org.gradle.api.plugins.gae.task.GaeRunTask
import org.gradle.api.plugins.gae.task.GaeStopTask
import org.gradle.api.plugins.gae.task.GaeWebAppDirTask
import org.gradle.api.plugins.gae.task.appcfg.*

/**
 * <p>A {@link Plugin} that provides tasks for uploading, running and managing of Google App Engine projects.</p>
 *
 * @author Benjamin Muschko
 */
class GaePlugin implements Plugin<Project> {
    static final String GAE_GROUP = "Google App Engine"
    static final String GAE_RUN = "gaeRun"
    static final String GAE_STOP = "gaeStop"
    static final String GAE_ENHANCE = "gaeEnhance"
    static final String GAE_UPLOAD = "gaeUpload"
    static final String GAE_ROLLBACK = "gaeRollback"
    static final String GAE_UPDATE_INDEXES = "gaeUpdateIndexes"
    static final String GAE_VACUUM_INDEXES = "gaeVacuumIndexes"
    static final String GAE_UPDATE_TASK_QUEUES = "gaeUpdateQueues"
    static final String GAE_UPDATE_DOS = "gaeUpdateDos"
    static final String GAE_UPDATE_CRON = "gaeUpdateCron"
    static final String GAE_CRON_INFO = "gaeCronInfo"
    static final String GAE_LOGS = "gaeLogs"
    static final String GAE_VERSION = "gaeVersion"

    @Override
    public void apply(Project project) {
        project.plugins.apply(WarPlugin.class)
        GaePluginConvention gaePluginConvention = new GaePluginConvention()
        project.convention.plugins.gae = gaePluginConvention

        configureWebAppDir(project)
        configureAppConfig(project, gaePluginConvention)
        configureGaeRun(project, gaePluginConvention)
        configureGaeStop(project, gaePluginConvention)
        configureGaeEnhance(project)
        configureGaeUpload(project)
        configureGaeRollback(project)
        configureGaeUpdateIndexes(project)
        configureGaeVacuumIndexes(project)
        configureGaeUpdateTaskQueues(project)
        configureGaeUpdateDoS(project)
        configureGaeUpdateCron(project)
        configureGaeCronInfo(project)
        configureGaeDownloadLogs(project, gaePluginConvention)
        configureGaeVersion(project)
    }

    private void configureWebAppDir(final Project project) {
        project.tasks.withType(GaeWebAppDirTask.class).whenTaskAdded { GaeWebAppDirTask gaeWebAppDirTask ->
            gaeWebAppDirTask.conventionMapping.map("webAppSourceDirectory") { getWarConvention(project).webAppDir }
        }
    }

    private void configureAppConfig(final Project project, final GaePluginConvention gaePluginConvention) {
        project.tasks.withType(GaeAppConfigTaskTemplate.class).whenTaskAdded { GaeAppConfigTaskTemplate gaeAppConfigTaskTemplate ->
            gaeAppConfigTaskTemplate.conventionMapping.map("email") { gaePluginConvention.appCfg.email }
            gaeAppConfigTaskTemplate.conventionMapping.map("server") { gaePluginConvention.appCfg.server }
            gaeAppConfigTaskTemplate.conventionMapping.map("host") { gaePluginConvention.appCfg.host }
            gaeAppConfigTaskTemplate.conventionMapping.map("passIn") { gaePluginConvention.appCfg.passIn }
            gaeAppConfigTaskTemplate.conventionMapping.map("httpProxy") { gaePluginConvention.appCfg.httpProxy }
            gaeAppConfigTaskTemplate.conventionMapping.map("httpsProxy") { gaePluginConvention.appCfg.httpsProxy }
        }
    }

    private void configureGaeRun(final Project project, GaePluginConvention gaePluginConvention) {
        project.tasks.withType(GaeRunTask.class).whenTaskAdded { GaeRunTask gaeRunTask ->
            gaeRunTask.conventionMapping.map("httpPort") { gaePluginConvention.httpPort }
            gaeRunTask.conventionMapping.map("stopPort") { gaePluginConvention.stopPort }
            gaeRunTask.conventionMapping.map("stopKey") { gaePluginConvention.stopKey }
        }

        GaeRunTask gaeRunTask = project.tasks.add(GAE_RUN, GaeRunTask.class)
        gaeRunTask.description = "Starts up a local App Engine development server."
        gaeRunTask.group = GAE_GROUP
    }

    private void configureGaeStop(final Project project, final GaePluginConvention gaePluginConvention) {
        GaeStopTask gaeStopTask = project.tasks.add(GAE_STOP, GaeStopTask.class)
        gaeStopTask.description = "Stops local App Engine development server."
        gaeStopTask.group = GAE_GROUP
        gaeStopTask.conventionMapping.map("stopPort") { gaePluginConvention.stopPort }
        gaeStopTask.conventionMapping.map("stopKey") { gaePluginConvention.stopKey }
    }

    private void configureGaeEnhance(final Project project) {
        project.tasks.withType(GaeEnhanceTask.class).whenTaskAdded { GaeEnhanceTask gaeEnhanceTask ->
            gaeEnhanceTask.conventionMapping.map("classesDirectory") { project.tasks.compileJava.destinationDir }
        }

        GaeEnhanceTask gaeEnhanceTask = project.tasks.add(GAE_ENHANCE, GaeEnhanceTask.class)
        gaeEnhanceTask.description = "Enhances DataNucleus classes."
        gaeEnhanceTask.group = GAE_GROUP
    }

    private void configureGaeUpload(final Project project) {
        GaeUploadTask gaeUploadTask = project.tasks.add(GAE_UPLOAD, GaeUploadTask.class)
        gaeUploadTask.description = "Uploads your application to App Engine."
        gaeUploadTask.group = GAE_GROUP
    }

    private void configureGaeRollback(final Project project) {
        GaeRollbackTask gaeRollbackTask = project.tasks.add(GAE_ROLLBACK, GaeRollbackTask.class)
        gaeRollbackTask.description = "Undoes a partially completed update for the given application."
        gaeRollbackTask.group = GAE_GROUP
    }

    private void configureGaeUpdateIndexes(final Project project) {
        GaeUpdateIndexesTask gaeUpdateIndexesTask = project.tasks.add(GAE_UPDATE_INDEXES, GaeUpdateIndexesTask.class)
        gaeUpdateIndexesTask.description = "Updates indexes on App Engine."
        gaeUpdateIndexesTask.group = GAE_GROUP
    }

    private void configureGaeVacuumIndexes(final Project project) {
        GaeVacuumIndexesTask gaeVacuumIndexesTask = project.tasks.add(GAE_VACUUM_INDEXES, GaeVacuumIndexesTask.class)
        gaeVacuumIndexesTask.description = "Deletes unused indexes on App Engine."
        gaeVacuumIndexesTask.group = GAE_GROUP
    }

    private void configureGaeUpdateTaskQueues(final Project project) {
        GaeUpdateQueuesTask gaeUpdateQueuesTask = project.tasks.add(GAE_UPDATE_TASK_QUEUES, GaeUpdateQueuesTask.class)
        gaeUpdateQueuesTask.description = "Updates task queues on App Engine."
        gaeUpdateQueuesTask.group = GAE_GROUP
    }

    private void configureGaeUpdateDoS(final Project project) {
        GaeUpdateDoSTask gaeUpdateDoSTask = project.tasks.add(GAE_UPDATE_DOS, GaeUpdateDoSTask.class)
        gaeUpdateDoSTask.description = "Updates DoS protection configuration on App Engine."
        gaeUpdateDoSTask.group = GAE_GROUP
    }

    private void configureGaeUpdateCron(final Project project) {
        GaeUpdateCronTask gaeUpdateCronTask = project.tasks.add(GAE_UPDATE_CRON, GaeUpdateCronTask.class)
        gaeUpdateCronTask.description = "Updates scheduled tasks definition (known as cron jobs) on App Engine."
        gaeUpdateCronTask.group = GAE_GROUP
    }

    private void configureGaeCronInfo(final Project project) {
        GaeCronInfoTask gaeCronInfoTask = project.tasks.add(GAE_CRON_INFO, GaeCronInfoTask.class)
        gaeCronInfoTask.description = "Get cron information from App Engine."
        gaeCronInfoTask.group = GAE_GROUP
    }

    private void configureGaeDownloadLogs(final Project project, final GaePluginConvention gaePluginConvention) {
        GaeDownloadLogsTask gaeDownloadLogsTask = project.tasks.add(GAE_LOGS, GaeDownloadLogsTask.class)
        gaeDownloadLogsTask.description = "Download logs from App Engine."
        gaeDownloadLogsTask.group = GAE_GROUP
        gaeDownloadLogsTask.conventionMapping.map("numDays") { gaePluginConvention.appCfg.logs.numDays }
        gaeDownloadLogsTask.conventionMapping.map("severity") { gaePluginConvention.appCfg.logs.severity }
        gaeDownloadLogsTask.conventionMapping.map("append") { gaePluginConvention.appCfg.logs.append }
        gaeDownloadLogsTask.conventionMapping.map("outputFile") { gaePluginConvention.appCfg.logs.outputFile }
    }

    private void configureGaeVersion(final Project project) {
        GaeVersionTask gaeVersionTask = project.tasks.add(GAE_VERSION, GaeVersionTask.class)
        gaeVersionTask.description = "Prints detailed version information about the SDK, Java and the operating system."
        gaeVersionTask.group = GAE_GROUP
    }

    private WarPluginConvention getWarConvention(Project project) {
        project.convention.getPlugin(WarPluginConvention.class)
    }
}
