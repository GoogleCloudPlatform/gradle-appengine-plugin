/*
 * Copyright 2014 the original author or authors.
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
package com.google.appengine.tooling

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppEnginePluginExtension
import com.google.appengine.task.AbstractTask
import com.google.appengine.task.DownloadSdkTask
import com.google.appengine.task.appcfg.AppConfigExtension;
import com.google.appengine.gradle.model.AppCfgOptions
import com.google.appengine.gradle.model.AppEngineModel
import com.google.appengine.gradle.model.impl.DefaultAppCfgOptions;
import com.google.appengine.gradle.model.impl.DefaultAppEngineModel;

import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.tooling.provider.model.ToolingModelBuilder;

/**
 * AppEngine implementation of ToolingModelBuilder, populates the AppEngineModel
 */
public class AppEngineToolingBuilderModel implements ToolingModelBuilder {
    @Override
    public boolean canBuild(String modelName) {
        return modelName.equals(AppEngineModel.class.getName());
    }

    @Override
    public Object buildAll(String modelName, Project project) {
        AppEnginePluginExtension conf = project.extensions.appengine
        AppConfigExtension appCfg = conf.getAppCfg()

        AppCfgOptions appCfgOptions = new DefaultAppCfgOptions(appCfg.email,
                                                               appCfg.server,
                                                               appCfg.host,
                                                               appCfg.noCookies,
                                                               appCfg.passIn,
                                                               appCfg.password,
                                                               appCfg.httpProxy,
                                                               appCfg.httpsProxy,
                                                               appCfg.oauth2,
                                                               appCfg.update.useJava7)
        return new DefaultAppEngineModel(conf.httpAddress,
                                         conf.httpPort,
                                         conf.disableUpdateCheck,
                                         conf.enhancer.version,
                                         conf.enhancer.api,
                                         conf.jvmFlags*.toString(), // convert everything to a java string
                                         conf.warDir ?: AppEnginePlugin.getExplodedAppDirectory(project),
                                         AppEnginePlugin.getAppDir(project),
                                         getSdkLocation(conf, project),
                                         appCfgOptions)

    }

    /**
     * Determine the App Engine SDK location
     * @return SDK location or null otherwise
     */
    static String getSdkLocation(AppEnginePluginExtension conf, Project project) {
        if(conf.downloadSdk) {
            try {
                def explodedSdkDir = AppEnginePlugin.getExplodedSdkDirectory(project)
                def zipFile = project.configurations.getByName(AppEnginePlugin.APPENGINE_SDK_CONFIGURATION_NAME).singleFile
                return DownloadSdkTask.getDownloadedSdkRoot(zipFile, explodedSdkDir)
            } catch (UnknownConfigurationException e) {
                // couldn't find download sdk config, try other methods
            }
        }
        String sdkRoot = System.getProperty(AbstractTask.APPENGINE_SDK_ROOT_SYS_PROP_KEY);
        if(!sdkRoot) {
            sdkRoot = System.getenv(AbstractTask.APPENGINE_HOME_ENV_PROP_KEY)
        }
        sdkRoot
    }
}
