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
package org.gradle.api.plugins.gae.task.internal

/**
 * Consumes command line output.
 *
 * @author Benjamin Muschko
 */
class CommandLineStreamConsumer extends Thread {
    private BufferedReader bReader
    private StreamOutputHandler streamHandler
    private KickStartSynchronizer kickStartSynchronizer
    private boolean done

    CommandLineStreamConsumer(InputStream stdin, StreamOutputHandler streamHandler, KickStartSynchronizer kickStartSynchronizer) {
        this.bReader = new BufferedReader(new InputStreamReader(stdin, 'UTF-8'))
        this.streamHandler = streamHandler
        this.kickStartSynchronizer = kickStartSynchronizer
    }

    @Override
    void run() {
        try {
            String line = bReader.readLine()

            while(!done && line != null) {
                streamHandler.handleLine(line)
                line = bReader.readLine()
            }
        }
        catch(Exception e) {
            // Don't care
        }
        finally {
            bReader.close()
            done = true
            kickStartSynchronizer.resume()
        }
    }
}
