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
import org.gradle.api.GradleException

/** Run a process synchronously, NOTE: think about using GradleExecProcessRunner instead of this */
class SynchronousProcessRunner implements ProcessRunner {

    final StreamConsumer streamConsumer
    final ProcessStarter processStarter

    public SynchronousProcessRunner(ProcessStarter processStarter, StreamConsumer streamConsumer) {
        this.processStarter = processStarter
        this.streamConsumer = streamConsumer
    }

    @Override
    public int run() throws GradleException {
        try {
            Process p = processStarter.startProcess()
            streamConsumer.consumeStreams(p)
            p.waitFor();
            return p.exitValue();
        } catch (IOException e) {
            throw new GradleException("Build failed during gcloud execution", e);
        } catch (InterruptedException e) {
            throw new GradleException("Build failed during gcloud execution", e);
        } finally {
            streamConsumer.stop()
        }
    }
}
