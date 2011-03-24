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

import org.gradle.api.plugins.gae.task.Explodable
import org.gradle.api.tasks.InputDirectory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Google App Engine task uploading your application to the server.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/tools/uploadinganapp.html#Uploading_the_App">Documentation</a>
 * @author Benjamin Muschko
 */
class GaeUploadTask extends GaeAppConfigTaskTemplate implements Explodable {
    static final Logger LOGGER = LoggerFactory.getLogger(GaeUploadTask.class)
    final String COMMAND = "update"
    private File explodedWarDirectory
    private String password

    @Override
    String startLogMessage() {
        "Starting upload process..."
    }

    @Override
    String errorLogMessage() {
        "An error occurred uploading the application to App Engine."
    }

    @Override
    String finishLogMessage() {
        "Finished uploading process."
    }

    @Override
    void executeTask() {
        // User has to enter credentials
        if(requiresUserInput()) {
            uploadApplication()
        }
        // All credentials were provided
        else {
            Thread appConfigThread = new Thread(new AppConfigRunnable())
            appConfigThread.start()
            appConfigThread.join()
        }
    }

    @Override
    List getParams() {
        [COMMAND, getExplodedWarDirectory().getCanonicalPath()]
    }

    @Override
    @InputDirectory
    public File getExplodedWarDirectory() {
        explodedWarDirectory
    }

    @Override
    public void setExplodedWarDirectory(File explodedWarDirectory) {
        this.explodedWarDirectory = explodedWarDirectory
    }

    public String getPassword() {
        password
    }

    public void setPassword() {
        this.password = password
    }

    private boolean requiresUserInput() {
        !(getEmail() && getPassword())
    }

    void uploadApplication() {
        super.executeTask()
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

                GaeUploadTask.this.uploadApplication()
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
            stdinWriter.write(GaeUploadTask.this.getPassword())
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
}
