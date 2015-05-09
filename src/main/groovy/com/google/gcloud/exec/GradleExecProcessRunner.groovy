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
package com.google.gcloud.exec

import org.gradle.api.Project

/**
 * Run an external process using gradle exec
 */
class GradleExecProcessRunner implements ProcessRunner {

    final String[] command
    final Project project

    public GradleExecProcessRunner(String[] command, Project project) {
        this.command = command
        this.project = project
    }
    @Override
    int run() {
        def result = project.exec {
            commandLine command
            standardInput = System.in
        }
        result.exitValue
    }
}
