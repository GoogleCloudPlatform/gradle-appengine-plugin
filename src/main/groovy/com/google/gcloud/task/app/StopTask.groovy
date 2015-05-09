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
package com.google.gcloud.task.app

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Incubating
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Task to stop appengine applications running locally
 */
@Incubating
class StopTask extends DefaultTask {
    @Input int adminPort = 8000
    @Input String adminAddress = "localhost"

    void setAdminHost(String adminHost) {
        String[] splitHost = adminHost.split(":")
        if (splitHost.length == 1) {
            if (splitHost[0].trim()) {
                adminAddress = splitHost[0]
            }
        }
        else if(splitHost.length == 2) {
            if (splitHost[0].trim()) {
                adminAddress = splitHost[0] ?: adminAddress
            }
            if (splitHost[1].trim()) {
                adminPort = splitHost[1] ? splitHost[1].toInteger() : adminPort
            }
        }
    }

    @TaskAction
    protected void stopDevAppServer() {
        HttpURLConnection connection
        URL url = new URL("http", adminAddress, adminPort, "/quit")
        try {
            connection = (HttpURLConnection) url.openConnection()
            connection.setDoOutput(true)
            connection.setDoInput(true)
            connection.setRequestMethod("GET")
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))
            while(responseReader.readLine() != null);
            connection.disconnect()
            logger.info("Shutting down Cloud SDK Server on port " + adminPort + " and waiting 4 seconds...")
            Thread.sleep(4000)
        } catch (IOException e) {
            logger.debug("Was not able to contact the server to shut it down.  It may not be running anymore. ", e)
        } catch (InterruptedException e) {
            throw new GradleException("Interrupted when trying to shutdown devappserver", e)
        } finally {
            if(connection != null) {
                connection.disconnect()
            }
        }
    }
 }
