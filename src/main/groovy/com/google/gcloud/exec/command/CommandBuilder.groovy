/*
 * Copyright 2015 the original author or authors.
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
package com.google.gcloud.exec.command

/**
 * build a gcloud command
 */

class CommandBuilder {
    List<String> command = []
    List<String> options = []
    List<String> arguments = []

    public CommandBuilder(String... parts) {
        Collections.addAll(command, parts);
    }

    /**
     * Returns a command String[] of the form command + options + arguments
     */
    public String[] buildCommand() {
        if (command.size() == 0) {
            throw new RuntimeException("No Command Specified");
        }
        List<String> commandLine = []
        commandLine.addAll(command)
        commandLine.addAll(options)
        commandLine.addAll(arguments)

        return commandLine.toArray(new String[commandLine.size()])
    }

    public CommandBuilder addCommand(String... parts) {
        command.addAll(parts)
        return this;
    }

    /**
     * Convenience method to add non-null values as options to a gcloud command
     * in the format "--optionName=value" or "--optionName", does NOT escape special characters
     */
    public CommandBuilder addOption(String optionName, String value) {
        if (optionName == null || optionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Option is null or empty")
        }
        if (value == null || value.trim().isEmpty() || value.trim().equals("true")) {
            if (optionName.startsWith("--")) {
                options.add(optionName)
            }
            else {
                options.add("--" + optionName)
            }
            return this
        }
        else if (value.trim().equals(false.toString())) {
            return this
        }
        if (optionName.startsWith("--")) {
            options.add(optionName + "=" + value)
        }
        else {
            options.add("--" + optionName + "=" + value)
        }
        return this
    }

    public CommandBuilder addOption(String optionName, Collection<?> values) {
        values.each { value ->
            addOption(optionName, value.toString())
        }

        return this
    }

    public CommandBuilder addOption(String option) {
        if (option == null || option.trim().isEmpty()) {
            throw new IllegalArgumentException("Option is null or empty")
        }
        if (option.startsWith("--")) {
            options.add(option)
        }
        else {
            options.add("--" + option)
        }
        return this
    }

    /**
     * Convenience method to add an argument, does NOT escape special characters,
     * arguments are inserted at the END of the commandline
     */
    public CommandBuilder addArgument(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            throw new IllegalArgumentException("Argument is null or empty")
        }
        arguments.add(argument)
        return this
    }
}
