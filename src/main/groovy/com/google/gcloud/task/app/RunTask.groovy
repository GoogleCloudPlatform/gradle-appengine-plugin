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

import com.google.gcloud.exec.AsynchronousProcessRunner
import com.google.gcloud.exec.ErrorRedirectingProcessStarter
import com.google.gcloud.exec.ProcessStarter
import com.google.gcloud.exec.io.StreamWatcher
import com.google.gcloud.exec.io.WatchableInputStreamConsumer
import org.gradle.api.Incubating
import org.gradle.api.logging.LogLevel

import java.util.concurrent.CountDownLatch

/**
 * Task to Run App Engine projects locally
 */
@Incubating
class RunTask extends AppTask {
    boolean async = false

    @Override
    List<String> getCommand() {
        ['preview','app','run']
    }

    @Override
    void setCommand(List<String> command) {
       throw new UnsupportedOperationException("Can't set command on Run Task")
    }

    @Override
    protected void executeTask() {
        if (!async) {
            super.executeTask()
        }
        else {
            WatchableInputStreamConsumer streamConsumer = new WatchableInputStreamConsumer(project.logger, LogLevel.LIFECYCLE)
            CountDownLatch syncLatch = new CountDownLatch(modules.size())
            streamConsumer.setInspector(new StreamWatcher.StreamInspector() {
                @Override
                void inspectLine(String line) {
                    if (syncLatch.count == 0) {
                        return
                    }
                    if (line.contains("INFO: Dev App Server is now running")) {
                        syncLatch.countDown()
                    }
                }
            })
            ProcessStarter processStarter = new ErrorRedirectingProcessStarter(getCommandLine())
            AsynchronousProcessRunner pr = new AsynchronousProcessRunner(processStarter, streamConsumer, null)
            pr.run()
            try {
                syncLatch.await()
            } catch (InterruptedException e) {
                // Do nothing, we probably need to die anyway
            }
        }
    }
}
