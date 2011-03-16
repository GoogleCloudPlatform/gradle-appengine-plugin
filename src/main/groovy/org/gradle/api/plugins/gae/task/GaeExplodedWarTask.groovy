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
package org.gradle.api.plugins.gae.task

import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.InputDirectory

/**
 * Abstract Google App Engine task that requires exploded WAR dir.
 *
 * @author Benjamin Muschko
 */
abstract class GaeExplodedWarTask extends GaeWebAppDirTask {
    private File explodedWarDirectory

    @Override
    void validateConfiguration(){
        super.validateConfiguration()

        try {
            if(!getExplodedWarDirectory() || !getExplodedWarDirectory().exists()) {
                throw new InvalidUserDataException("Exploded WAR directory "
                        + (getExplodedWarDirectory() == null ? "null" : getExplodedWarDirectory().getCanonicalPath())
                        + " does not exist")
            }
            else {
                logger.info "Exploded WAR directory = ${getExplodedWarDirectory().getCanonicalPath()}"
            }
        }
        catch(IOException e) {
            throw new InvalidUserDataException("Exploded WAR directory does not exist", e)
        }
    }

    @InputDirectory
    public File getExplodedWarDirectory() {
        explodedWarDirectory
    }

    public void setExplodedWarDirectory(File explodedWarDirectory) {
        this.explodedWarDirectory = explodedWarDirectory
    }
}
