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

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction

/**
 * Google App Engine task stopping the local development server.
 *
 * @author Benjamin Muschko
 */
class StopTask extends DefaultTask {
    Integer stopPort
    String stopKey

    @TaskAction
    void stop() {
        if(!getStopPort()) {
            throw new InvalidUserDataException('Please specify a valid port')
        }
        if(!getStopKey()) {
            throw new InvalidUserDataException('Please specify a valid stopKey')
        }

        Socket socket

        try {
            socket = new Socket(InetAddress.getByName('127.0.0.1'), getStopPort())
            socket.setSoLinger(false, 0)

            OutputStream out = socket.outputStream
            out.write((getStopKey() + '\r\nstop\r\n').bytes)
            out.flush()
        }
        catch(ConnectException e) {
            logger.info 'Local App Engine server not running!'
        }
        catch(Exception e) {
            logger.error 'Exception during stopping', e
        }
        finally {
            if(socket && !socket.isClosed()) {
                socket.close()
            }
        }
    }
}
