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

import com.google.gcloud.exec.io.StreamConsumer

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * run a process asynchronously
 */
class AsynchronousProcessRunner implements ProcessRunner {

    final ExecutorService executorService
    final StreamConsumer streamConsumer
    final ProcessStarter processStarter
    final Callback callback

    public AsynchronousProcessRunner(ProcessStarter processStarter, StreamConsumer streamConsumer, Callback callback) {
        this.processStarter = processStarter
        this.streamConsumer = streamConsumer
        this.callback = callback
        this.executorService = Executors.newSingleThreadExecutor()
    }

    @Override
    public int run() {
        executorService.submit(new Runnable() {
            @Override
            void run() {
                try {
                    Process p = processStarter.startProcess()
                    streamConsumer.consumeStreams(p)
                    p.waitFor();
                    if (callback != null) {
                        callback.onCompleted(p.exitValue());
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onFailedWithException(e);
                    }
                } finally {
                    streamConsumer.stop()
                }
            }
        })
        return 0
    }

    public interface Callback {
        public void onCompleted(int exitCode);
        public void onFailedWithException(Exception ex);
    }
}
