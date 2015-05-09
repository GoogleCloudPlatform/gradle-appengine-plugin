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

/**
 * Default implementation of ProcessStarter, starts a process, redirects stderr to stdout and adds a shutdown hook
 */
class ErrorRedirectingProcessStarter implements ProcessStarter {

    private final String[] command;

    public ErrorRedirectingProcessStarter(String[] command) {
        this.command = command;
    }

    @Override
    public Process startProcess() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command)
        pb.redirectInput()
        pb.redirectErrorStream(true)

        final Process process = pb.start()

        Runtime.getRuntime().addShutdownHook(new Thread() {
            //TODO : Figure out if sending TERM signal will work
            @Override
            public void run() {
                if (process != null) {
                    process.destroy()
                }
            }
        });
        return process
    }

}
