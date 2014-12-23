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
package com.google.appengine.gradle.model;

import java.io.File;
import java.util.List;

/**
 * Interface for the AppEngine Model
 * NOTE: If you change this, update the builder model version
 */
public interface AppEngineModel {
    public String getModelVersion();
    public String getHttpAddress();
    public Integer getHttpPort();
    public Boolean isDisableUpdateCheck();
    public Boolean isDisableDatagram();
    public String getEnhancerVersion();
    public String getEnhancerApi();
    public List<String> getJvmFlags();
    public File getWarDir();
    public File getWebAppDir();
    public String getAppEngineSdkRoot();
    public AppCfgOptions getAppCfgOptions();
}
