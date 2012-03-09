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
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Google App Engine task that downloads SDK to be used.
 *
 * @author Benjamin Muschko
 */
class GaeDownloadSdkTask extends DefaultTask {
    @InputFile File gaeSdkZipFile
    @OutputDirectory File explodedSdkDirectory

    @TaskAction
    void start() {
        if(!getExplodedSdkDirectory().exists()) {
            boolean success = getExplodedSdkDirectory().mkdirs()

            if(!success) {
                throw new GradleException("Could not create exploded Google App Engine SDK directory in ${getExplodedSdkDirectory().canonicalPath}")
            }
        }

        if(!new File(getDownloadedSdkRoot()).exists()) {
             ant.unzip(src: getGaeSdkZipFile(), dest: getExplodedSdkDirectory())
        }

        System.setProperty(AbstractGaeTask.APPENGINE_SDK_ROOT_SYS_PROP_KEY, getDownloadedSdkRoot())
    }

    private String getDownloadedSdkRoot() {
        String filename = getGaeSdkZipFile().name.substring(0, getGaeSdkZipFile().name.lastIndexOf('.'))
        getExplodedSdkDirectory().canonicalPath + System.getProperty('file.separator') + filename
    }
}
