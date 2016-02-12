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

import java.io.Serializable;

public class DefaultAppCfgOptions implements AppCfgOptions, Serializable {
    final private String email;
    final private String server;
    final private String host;
    final private Boolean noCookies;
    final private Boolean passIn;
    final private String password;
    final private String httpProxy;
    final private String httpsProxy;
    final private Boolean oauth2;
    final private Boolean useJava7;

    public DefaultAppCfgOptions(String email, String server, String host, Boolean noCookies, Boolean passIn,
                                String password, String httpProxy, String httpsProxy, Boolean oauth2,
                                Boolean useJava7) {
        this.email = email;
        this.server = server;
        this.host = host;
        this.noCookies = noCookies;
        this.passIn = passIn;
        this.password = password;
        this.httpProxy = httpProxy;
        this.httpsProxy = httpsProxy;
        this.oauth2 = oauth2;
        this.useJava7 = useJava7;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Boolean isNoCookies() {
        return noCookies;
    }

    @Override
    public Boolean isPassIn() {
        return passIn;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getHttpProxy() {
        return httpProxy;
    }

    @Override
    public String getHttpsProxy() {
        return httpsProxy;
    }

    @Override
    public Boolean isOauth2() {
        return oauth2;
    }

    @Override
    public Boolean isUseJava7() {
        return useJava7;
    }
}
