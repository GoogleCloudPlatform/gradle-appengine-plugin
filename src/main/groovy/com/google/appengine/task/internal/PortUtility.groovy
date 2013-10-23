/*
 * Copyright 2012 the original author or authors.
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
package com.google.appengine.task.internal

/**
 * Port utility.
 *
 * @author Benjamin Muschko
 */
final class PortUtility {
    static final int MIN_PORT_NUMBER = 1
    static final int MAX_PORT_NUMBER = 65535

    private PortUtility() {}

    /**
     * Checks if port value is not null and within valid range of 1 to 65535.
     *
     * @param port Port
     * @return Flag
     */
    static boolean isValidPortNumber(Integer port) {
        port ? port >= MIN_PORT_NUMBER || port <= MAX_PORT_NUMBER : false
    }

    /**
     * Checks if port is available.
     *
     * @param port Port
     * @return Flag
     */
    static boolean isAvailable(Integer port) {
        ServerSocket ss
        DatagramSocket ds

        try {
            ss = new ServerSocket(port, 0, InetAddress.getByName('127.0.0.1'))
            ss.reuseAddress = true
            ds = new DatagramSocket(port, InetAddress.getByName('127.0.0.1'))
            ds.reuseAddress = true
            return true
        }
        catch(IOException e) {
        }
        finally {
            if(ds != null) {
                ds.close()
            }

            if(ss != null) {
                try {
                    ss.close()
                }
                catch(IOException e) {
                    // should not be thrown
                }
            }
        }

        false
    }
}
