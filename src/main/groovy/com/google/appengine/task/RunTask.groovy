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

import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import com.google.appengine.task.internal.*
/**
 * Google App Engine task starting up a local development server.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/tools/devserver.html#Running_the_Development_Web_Server">Documentation</a>
 * @author Benjamin Muschko
 */
class RunTask extends AbstractTask implements Explodable {
    String httpAddress
    Integer httpPort
    File explodedAppDirectory
    Boolean daemon
    Boolean disableUpdateCheck
    Boolean disableDatagram
    List<String> jvmFlags
    final KickStartSynchronizer kickStartSynchronizer = new KickStartSynchronizer()
    private static final String DEV_SERVER_STARTED = 'Dev App Server is now running'

    @Override
    void validateConfiguration() {
        super.validateConfiguration()
        validatePort(getHttpPort(), 'HTTP')
    }

    private void validatePort(Integer port, String type) {
        if(!PortUtility.isValidPortNumber(port)) {
            throw new InvalidUserDataException("Invalid $type port number: $port")
        }

        if(!PortUtility.isAvailable(port, disableDatagram)) {
            throw new InvalidUserDataException("$type port number already in use: $port")
        }

        logger.info "$type port = $port"
    }

    @Override
    void executeTask() {
        startLocalDevelopmentServer()
    }

    private void startLocalDevelopmentServer() {
        try {
            logger.info 'Starting local development server...'

            if(!getDaemon()) {
                runKickStart()
            }
            else {
                Thread kickStartThread = new Thread(new KickStartRunnable())
                kickStartThread.start()

                // Pause current thread until local development server is fully started
                kickStartSynchronizer.pause()
            }
        }
        catch(Exception e) {
            throw new GradleException('An error occurred starting the local development server.', e)
        }
        finally {
            if(!getDaemon()) {
                logger.info 'Local development server exiting.'
            }
        }
    }

    private void runKickStart() {
        KickStartParams kickStartParams = new KickStartParams()
        kickStartParams.httpPort = getHttpPort()
        kickStartParams.httpAddress = getHttpAddress()
        kickStartParams.disableUpdateCheck = getDisableUpdateCheck()
        kickStartParams.disableDatagram = getDisableDatagram()
        kickStartParams.jvmFlags = getJvmFlags()
        kickStartParams.explodedWarDirectory = getExplodedAppDirectory()

        List<String> params = KickStartParamsBuilder.instance.buildCommandLineParams(kickStartParams)
        logger.info "Using params = $params"

        ClassLoader classLoader = Thread.currentThread().contextClassLoader
        Class kickStart = Class.forName('com.google.appengine.tools.KickStart', true, classLoader)
        kickStart.main(params as String[])
    }

    private class KickStartRunnable implements Runnable {
        @Override
        void run() {
            InputStream systemInOriginal = System.in
            PrintStream systemOutOriginal = System.out
            PrintStream systemErrOriginal = System.err
            PipedOutputStream pipeOut = new PipedOutputStream()
            PipedInputStream pipeIn = new PipedInputStream(pipeOut)
            PrintStream out = new PrintStream(pipeOut, true)
            System.setIn(pipeIn)
            System.setOut(out)
            System.setErr(out)

            Thread outputCommandLineConsumer = new CommandLineStreamConsumer(pipeIn, new OutStreamHandler(), RunTask.this.kickStartSynchronizer)
            outputCommandLineConsumer.start()

            try {
                runKickStart()
            }
            catch(Exception e) {
                throw new GradleException('An error occurred starting the local development server.', e)
            }
            finally {
                System.setIn(systemInOriginal)
                System.setOut(systemOutOriginal)
                System.setErr(systemErrOriginal)
            }
        }

        private class OutStreamHandler implements StreamOutputHandler {
            final Logger logger;

            OutStreamHandler() {
              logger = Logging.getLogger("DevAppServer Out");
            }

            @Override
            void handleLine(String line) {
                logger.info line
                checkServerStartupProgress(line)
            }
        }

        private class ErrStreamHandler implements StreamOutputHandler {
            @Override
            void handleLine(String line) {
                logger.error line
                checkServerStartupProgress(line)
            }
        }

        private void checkServerStartupProgress(final String line) {
            if(line.contains(DEV_SERVER_STARTED)) {
                RunTask.this.kickStartSynchronizer.resume()
            }
        }
    }
}

