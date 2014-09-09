package com.google.gcloud.wrapper;

import java.io.File;
import java.security.InvalidParameterException;

/**
 * gcloud interface
 */
public abstract class GCloud {

    protected String[] command;
    protected File logFile = null;
    protected File gcloudHome = null;

    public static boolean canGCloud() {
        return false;
        // find gcloud
    }

    public GCloud(String... command) {
        this.command = command;
    }

    public void setGCloudHome(File file) {
        if (!file.exists() || !file.isDirectory()) {
            throw new InvalidParameterException("Trying to set gcloud home to an invalid location");
            // invalid gcloud home
        }
        gcloudHome = file;
    }

    public void setCommand(String... command) {
        this.command = command;
    }

    public void setLogFile(File file) {
        logFile = file;
    }

    public abstract int runSync() throws GCloudException;

    public abstract void runAsync(Callback callback);

    /**
     * Interface for callback of asynchronous mode calls to gcloud
     */
    public static interface Callback {
        public void onCompleted(int exitCode);
        public void onFailedWithException(Exception ex);
    }
}
