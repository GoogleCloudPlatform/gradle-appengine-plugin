package com.google.gcloud.wrapper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Simple gcloud command builder with helper functions
 */
public class GCloudCommandBuilder {

    ArrayList<String> command = new ArrayList<String>();

    public GCloudCommandBuilder() {
    }

    public GCloudCommandBuilder(String... parts) {
        Collections.addAll(command, parts);
    }

    /**
     * Returns a command String[] and adds gcloud if necessary
     */
    public String[] buildCommand() {
        if (command.size() == 0) {
            throw new RuntimeException("No Command Specified");
        }
        if (!command.get(0).equals("gcloud")) {
            command.add(0, "gcloud");
        }

        return command.toArray(new String[command.size()]);
    }

    public GCloudCommandBuilder add(String... parts) {
        Collections.addAll(command, parts);
        return this;
    }

    /**
     * Convenience method to add non-null values as options to a gcloud command
     * in the format "--optionName=value", does not escape special characters
     */
    public GCloudCommandBuilder addOption(String optionName, String value) {
        if (optionName != null && optionName.trim().length() != 0
                && value != null && value.trim().length() != 0) {
            command.add("--" + optionName + "=" + value.trim());
        }
        return this;
    }

    /**
     * Convenience method to add an option if value is true
     */
    public GCloudCommandBuilder addBoolOption(String optionName, Boolean value) {
        if (optionName != null && optionName.trim().length() != 0
                && value != null && value) {
            command.add("--" + optionName);
        }
        return this;
    }
}
