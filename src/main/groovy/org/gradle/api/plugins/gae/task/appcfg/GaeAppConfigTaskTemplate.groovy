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
package org.gradle.api.plugins.gae.task.appcfg

import com.google.appengine.tools.admin.AppCfg
import org.gradle.api.GradleException
import org.gradle.api.plugins.gae.task.GaeWebAppDirTask
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Abstract Google App Engine task used for application configuration.
 *
 * @author Benjamin Muschko
 */
abstract class GaeAppConfigTaskTemplate extends GaeWebAppDirTask {
    static final Logger LOGGER = LoggerFactory.getLogger(GaeAppConfigTaskTemplate.class)
    private String email
    private String server
    private String host
    private Boolean passIn
    private String password
    private String httpProxy
    private String httpsProxy

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
            LOGGER.info startLogMessage()

            def params = []
            addCommonParams(params)
            params.addAll getParams()
            LOGGER.info "Using params = $params"

            AppCfg.main(params as String[])
        }
        catch(Exception e) {
            throw new GradleException(errorLogMessage(), e)
        }
        finally {
            LOGGER.info finishLogMessage()
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

        if(getPassIn() || getPassword()) {
            params << "--passin"
        }

        if(getHttpProxy()) {
            params << "--proxy=${getHttpProxy()}"
        }

        if(getHttpsProxy()) {
            params << "--proxy_https=${getHttpsProxy()}"
        }
    }

    public String getEmail() {
        email
    }

    public void setEmail(String email) {
        this.email = email
    }

    public String getServer() {
        server
    }

    public void setServer(String server) {
        this.server = server
    }

    public String getHost() {
        host
    }

    public void setHost(String host) {
        this.host = host
    }

    public Boolean getPassIn() {
        passIn
    }

    public void setPassIn(Boolean passIn) {
        this.passIn = passIn
    }

    public String getHttpProxy() {
        httpProxy
    }

    public void setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy
    }

    public String getHttpsProxy() {
        httpsProxy
    }

    public void setHttpsProxy(String httpsProxy) {
        this.httpsProxy = httpsProxy
    }

    public String getPassword() {
        password
    }

    public void setPassword() {
        this.password = password
    }

    private class AppConfigRunnable implements Runnable {
        final Logger LOGGER = LoggerFactory.getLogger(AppConfigRunnable.class)

        @Override
        public void run() {
            PrintStream systemOutOriginal = System.out
            InputStream systemInOriginal = System.in
            PipedInputStream inputStreamReplacement = new PipedInputStream()
            OutputStream stdin = new PipedOutputStream(inputStreamReplacement)

            try {
                System.setIn(inputStreamReplacement)
                BufferedWriter stdinWriter = new BufferedWriter(new OutputStreamWriter(stdin))
                PrintStream printStream = new PrintStream(new PasswordOutputStream(stdinWriter, systemOutOriginal), true)
                System.setOut(printStream)

                GaeAppConfigTaskTemplate.this.runAppConfig()
            }
            finally {
                System.setOut(systemOutOriginal)
                System.setIn(systemInOriginal)
            }
        }
    }

    private class PasswordWriterRunnable implements Runnable {
        private final BufferedWriter stdinWriter

        public PasswordWriterRunnable(BufferedWriter stdinWriter) {
            this.stdinWriter = stdinWriter
        }

        @Override
        void run() {
            stdinWriter.write(GaeAppConfigTaskTemplate.this.getPassword())
            stdinWriter.newLine()
            stdinWriter.flush()
        }
    }

    private class PasswordOutputStream extends OutputStream {
        private final BufferedWriter stdinWriter
        private final PrintStream out

        public PasswordOutputStream(BufferedWriter stdinWriter, PrintStream out) {
            this.stdinWriter = stdinWriter
            this.out = out
        }

        @Override
        public void write(int b) throws IOException {
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
