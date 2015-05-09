/*
 * Copyright 2015 the original author or authors.
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
package com.google.gcloud.dsl

import org.gradle.api.Project
import org.gradle.api.UnknownProjectException

/**
 * Named domain object for gcloud configurations
 */
class GCloudConfigurable {

    //NamedDomainObject name
    final String name;
    final Project project;

    Map<String, ?> args = [:]
    Set<Project> modules // defaults to [project] in constructor

    // run specific
    Map<String, ?> runArgs = [:]
    boolean runAsync = false;

    // deploy specific
    public static final Set<String> APP_DESCRIPTOR = ["app.yaml"];
    Map<String, ?> deployArgs = [:]
    Set<String> descriptors = APP_DESCRIPTOR;

    public GCloudConfigurable(String name, Project project) {
        this.name = name;
        this.project = project;
        this.modules = [project];
    }

    void args(Map<String, ?> args) {
        this.args = args;
    }

    // convert module strings to project objects
    void modules(String... modules) throws UnknownProjectException {
        this.modules = []
        for (String module : modules) {
            this.modules << project.project(module)
            // TODO: maybe check if the module has the appengine plugin here?
        }
    }

    void runAsync(boolean async) {
        this.runAsync = async
    }

    void runArgs(Map<String, ?> args) {
        this.runArgs = args;
    }

    void deployArgs(Map<String, ?> args) {
        this.deployArgs = args;
    }

    void descriptors(String... descriptors) {
        this.descriptors = descriptors as Set
    }
}
