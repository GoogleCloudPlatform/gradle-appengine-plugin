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
package com.google.appengine.task

import com.google.common.base.Objects
import com.google.common.base.Throwables
import com.google.common.io.ByteStreams
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.TaskAction

/**
 * Google App Engine task stopping the local development server.
 *
 * @author Benjamin Muschko
 */
class StopTask extends DefaultTask {
    String httpAddress
    Integer httpPort

    @TaskAction
    void stop() throws GradleScriptException {
        HttpURLConnection connection = null;
        try {
            Integer port = Objects.firstNonNull(httpPort, 8080);
            URL url = new URL("http", Objects.firstNonNull(httpAddress, "localhost"), port, "/_ah/admin/quit");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.getOutputStream().write(0);
            ByteStreams.toByteArray(connection.getInputStream());
            connection.disconnect();
            logger.warn("Shutting down devappserver on port " + port);
            Thread.sleep(2000);
        } catch (MalformedURLException e) {
            throw new GradleScriptException("URL malformed attempting to stop the devappserver : " + e.getMessage());
        } catch (IOException e) {
            logger.debug("Could not contact the devappserver to shut it down.  It is most likely not running anymore. ", e);
        } catch (InterruptedException e) {
            Throwables.propagate(e);
        }
    }
}
