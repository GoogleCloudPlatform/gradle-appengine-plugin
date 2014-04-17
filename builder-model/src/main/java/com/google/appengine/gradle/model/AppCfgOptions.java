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

/**
 * Interface for the App Config Options in an App Engine gradle project
 * NOTE: If you change this, update the builder model version
 */
public interface AppCfgOptions {
    public String getEmail();
    public String getServer();
    public String getHost();
    public Boolean isNoCookies();
    public Boolean isPassIn();
    public String getPassword();
    public String getHttpProxy();
    public String getHttpsProxy();
    public Boolean isOauth2();
    public Boolean isUseJava7();
}
