package com.google.gradle.test.fixture

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AppProjectBuilder extends ProjectBuilder {

    // don't instantiate
    private AppProjectBuilder() {}

    boolean appengine = false;
    boolean gcloud = false;

    public static AppProjectBuilder builder() {
        return new AppProjectBuilder()
    }

    public AppProjectBuilder withAppEngine() {
        appengine = true
        this
    }

    public AppProjectBuilder withGCloud() {
        gcloud = true
        this
    }

    @Override
    public Project build() {
        Project project = super.build()
        project.buildscript.repositories {
            maven {
                url System.getProperty("test.maven.repo")
            }
            mavenCentral()
        }
        project.buildscript.dependencies {
            classpath "com.google.appengine:gradle-appengine-plugin:${System.getProperty('appengine.pluginversion')}"
        }
        project.apply plugin: "war"
        project.apply plugin: "java"

        if (appengine) {
            project.apply plugin: "com.google.appengine"
        }

        if (gcloud) {
            project.apply plugin: "com.google.gcloud-preview"
        }

        project
    }
}
