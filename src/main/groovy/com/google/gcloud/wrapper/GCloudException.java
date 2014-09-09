package com.google.gcloud.wrapper;

/**
 * Exception thrown by the gcloud wrapper functionality
 */
public class GCloudException extends Exception {

    public GCloudException() {
        super();
    }

    public GCloudException(String message) {
        super(message);
    }

    public GCloudException(String message, Throwable cause) {
        super(message, cause);
    }

    public GCloudException(Throwable cause) {
        super(cause);
    }

    protected GCloudException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
