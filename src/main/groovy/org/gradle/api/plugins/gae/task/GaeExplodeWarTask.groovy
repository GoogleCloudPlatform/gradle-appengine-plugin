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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

/**
 * Google App Engine task that explodes the WAR archive into a given directory.
 *
 * @author Benjamin Muschko
 */
class GaeExplodeWarTask extends DefaultTask implements Explodable {
    @InputFile File warArchive
    File explodedWarDirectory
    Boolean cleanClasses

    @TaskAction
    protected void start() {
        ant.delete(dir: getExplodedWarDirectory())
        ant.unzip(src: getWarArchive(), dest: getExplodedWarDirectory())
        if(getCleanClasses()){
            ant.delete(dir: new File(getExplodedWarDirectory(), 'WEB-INF/classes'))
        }
    }
}
