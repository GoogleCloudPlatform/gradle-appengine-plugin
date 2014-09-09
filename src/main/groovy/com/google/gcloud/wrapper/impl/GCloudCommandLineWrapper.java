package com.google.gcloud.wrapper.impl;

import com.google.gcloud.wrapper.GCloud;
import com.google.gcloud.wrapper.GCloudException;

import java.io.IOException;

/**
 * Basic command line implementation of GCloud
 */
public class GCloudCommandLineWrapper extends GCloud {

    public GCloudCommandLineWrapper(String... command) {
        super(command);
    }

    /**
     * Run in synchronous mode
     * @return exitCode
     * @throws GCloudException
     */
    @Override
    public int runSync() throws GCloudException {
        try {
            Process p = startProcess();
            p.waitFor();
            return p.exitValue();
        } catch (IOException e) {
            throw new GCloudException(e);
        } catch (InterruptedException e) {
            throw new GCloudException(e);
        }
    }

    /**
     * Run in asynchronous mode
     * @param callback
     */
    @Override
    public void runAsync(final Callback callback) {
        Thread processRunnerThread = new Thread() {
            @Override
            public void run() {
                try {
                    Process p = startProcess();
                    p.waitFor();
                    if (callback != null) {
                        callback.onCompleted(p.exitValue());
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onFailedWithException(e);
                    }
                }
            }
        };
        processRunnerThread.start();
    }

    /**
     * Start a processbuilder process
     * @return Process object
     * @throws IOException
     */
    protected Process startProcess() throws IOException {
        if (command == null) {
            throw new RuntimeException("Command not configured");
        }
        ProcessBuilder pb = new ProcessBuilder(command);
        if (gcloudHome != null) {
            pb.directory(gcloudHome);
        }
        if (logFile != null) {
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
        }
        else {
            pb.inheritIO();
        }

        final Process gcloudProcess = pb.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            //TODO : Figure out if sending TERM signal will work
            @Override
            public void run() {
                if (gcloudProcess != null) {
                    gcloudProcess.destroy();
                }
            }
        });
        return gcloudProcess;
    }
}
