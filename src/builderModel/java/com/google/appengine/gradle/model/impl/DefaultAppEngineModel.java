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
package com.google.appengine.gradle.model.impl;

import com.google.appengine.gradle.model.AppCfgOptions;
import com.google.appengine.gradle.model.AppEngineModel;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Default implementation of AppEngineModel
 */
public class DefaultAppEngineModel implements AppEngineModel, Serializable {
    final private String httpAddress;
    final private Integer httpPort;
    final private Boolean disableUpdateCheck;
    final private String enhancerVersion;
    final private String enhancerApi;
    final private List<String> jvmFlags;
    final private File warDir;
    final private File webAppDir;
    final private String sdkRoot;
    final private AppCfgOptions appCfgOptions;

    public DefaultAppEngineModel(String httpAddress, Integer httpPort, Boolean disableUpdateCheck,
                                 String enhancerVersion, String enhancerApi, List<String> jvmFlags, File warDir,
                                 File webAppDir, String sdkRoot, AppCfgOptions appCfgOptions) {
        this.httpAddress = httpAddress;
        this.httpPort = httpPort;
        this.disableUpdateCheck = disableUpdateCheck;
        this.enhancerVersion = enhancerVersion;
        this.enhancerApi = enhancerApi;
        this.jvmFlags = jvmFlags;
        this.warDir = warDir;
        this.webAppDir = webAppDir;
        this.sdkRoot = sdkRoot;
        this.appCfgOptions = appCfgOptions;
    }

    @Override
    public String getModelVersion() {
        return "not specified";
    }

    @Override
    public String getHttpAddress() {
        return httpAddress;
    }

    @Override
    public Integer getHttpPort() {
        return httpPort;
    }

    @Override
    public Boolean isDisableUpdateCheck() {
        return disableUpdateCheck;
    }

    @Override
    public String getEnhancerVersion() {
        return enhancerVersion;
    }

    @Override
    public String getEnhancerApi() {
        return enhancerApi;
    }

    @Override
    public List<String> getJvmFlags() {
        return jvmFlags;
    }

    @Override
    public File getWarDir() {
        return warDir;
    }

    @Override
    public File getWebAppDir() {
        return webAppDir;
    }

    @Override
    public String getAppEngineSdkRoot() {
        return sdkRoot;
    }

    @Override
    public AppCfgOptions getAppCfgOptions() {
        return appCfgOptions;
    }
}
