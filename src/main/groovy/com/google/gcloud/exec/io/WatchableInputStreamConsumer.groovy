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
package com.google.gcloud.exec.io

import org.codehaus.groovy.runtime.IOGroovyMethods
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A stream consume and watcher that will consume and inspect only the input stream
 * from a process, ensure the stderr redirects to stdin for this implementation
 *
 * Set the stream consumer on this object directly (StreamWatcher.setInspector) after
 * instantiating the object.
 */
class WatchableInputStreamConsumer implements StreamConsumer, StreamWatcher {

    final ExecutorService executorService
    final Logger logger
    final LogLevel logLevel

    WatchableInputStreamConsumer(Logger logger, LogLevel logLevel) {
        executorService = Executors.newSingleThreadExecutor()
        this.logger = logger
        this.logLevel = logLevel
    }

    @Override
    void consumeStreams(Process process) {
        executorService.submit(new Runnable() {
            @Override
            void run() {
                try {
                    InputStream streamReader = process.getInputStream()
                    IOGroovyMethods.withReader(streamReader) {
                        streamReader.eachLine { String line ->
                            if (Thread.currentThread().interrupted) {
                                throw new InterruptedException()
                            }
                            inspector?.inspectLine(line)
                            logger.log(logLevel ?: LogLevel.LIFECYCLE, line)
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing, executor probably killed us
                }
            }
        })
    }

    @Override
    void stop() {
        executorService.shutdownNow()
    }
}
