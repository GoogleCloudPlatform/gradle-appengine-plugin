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
package com.google.appengine.task.appcfg

import org.gradle.api.GradleException
import com.google.appengine.task.WebAppDirTask

/**
 * Abstract Google App Engine task used for application configuration.
 *
 * @author Benjamin Muschko
 */
abstract class AppConfigTaskTemplate extends WebAppDirTask {
    String email
    String server
    String host
    Boolean noCookies
    Boolean passIn
    String password
    String httpProxy
    String httpsProxy
    Boolean oauth2

    @Override
    void executeTask() {
        // User has to enter credentials
        if(requiresUserInput()) {
            runAppConfig()
        }
        // All credentials were provided
        else {
            Thread appConfigThread = new Thread(new AppConfigRunnable())
            appConfigThread.start()
            appConfigThread.join()
        }
    }

    private boolean requiresUserInput() {
        !(getEmail() && getPassword())
    }

    void runAppConfig() {
        try {
            logger.info startLogMessage()

            def params = []
            addCommonParams(params)
            params.addAll getParams()
            logger.info "Using params = $params"

            ClassLoader classLoader = Thread.currentThread().contextClassLoader
            def appCfg = classLoader.loadClass('com.google.appengine.tools.admin.AppCfg')
            appCfg.main(params as String[])
        }
        catch(Exception e) {
            throw new GradleException(errorLogMessage(), e)
        }
        finally {
            logger.info finishLogMessage()
        }
    }

    private void addCommonParams(params) {
        if(getEmail()) {
            params << "--email=${getEmail()}"
        }

        if(getServer()) {
            params << "--server=${getServer()}"
        }

        if(getHost()) {
            params << "--host=${getHost()}"
        }

        if(getNoCookies()) {
            params << '--no_cookies'
        }

        if(getPassIn() || getPassword()) {
            params << '--passin'
        }

        if(getHttpProxy()) {
            params << "--proxy=${getHttpProxy()}"
        }

        if(getHttpsProxy()) {
            params << "--proxy_https=${getHttpsProxy()}"
        }

        if(getOauth2()) {
            params << '--oauth2'
        }

    }

    private class AppConfigRunnable implements Runnable {
        @Override
        void run() {
            PrintStream systemOutOriginal = System.out
            InputStream systemInOriginal = System.in
            PipedInputStream inputStreamReplacement = new PipedInputStream()
            OutputStream stdin = new PipedOutputStream(inputStreamReplacement)

            try {
                System.setIn(inputStreamReplacement)
                BufferedWriter stdinWriter = new BufferedWriter(new OutputStreamWriter(stdin))
                PrintStream printStream = new PrintStream(new PasswordOutputStream(stdinWriter, systemOutOriginal), true)
                System.setOut(printStream)

                AppConfigTaskTemplate.this.runAppConfig()
            }
            finally {
                System.setOut(systemOutOriginal)
                System.setIn(systemInOriginal)
            }
        }
    }

    private class PasswordWriterRunnable implements Runnable {
        final BufferedWriter stdinWriter

        public PasswordWriterRunnable(BufferedWriter stdinWriter) {
            this.stdinWriter = stdinWriter
        }

        @Override
        void run() {
            stdinWriter.write(AppConfigTaskTemplate.this.password)
            stdinWriter.newLine()
            stdinWriter.flush()
        }
    }

    private class PasswordOutputStream extends OutputStream {
        final BufferedWriter stdinWriter
        final PrintStream out

        public PasswordOutputStream(BufferedWriter stdinWriter, PrintStream out) {
            this.stdinWriter = stdinWriter
            this.out = out
        }

        @Override
        void write(int b) throws IOException {
            Thread passwordWriterThread = new Thread(new PasswordWriterRunnable(stdinWriter))
            passwordWriterThread.setDaemon(true)
            passwordWriterThread.start()
            out.write(b)
        }
    }

    abstract String startLogMessage()
    abstract String errorLogMessage()
    abstract String finishLogMessage()
    abstract List getParams()
}
