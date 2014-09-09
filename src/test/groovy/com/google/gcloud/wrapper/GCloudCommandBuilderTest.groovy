package com.google.gcloud.wrapper

import spock.lang.Specification

/**
 * Command Builder test
 */
class GCloudCommandBuilderTest extends Specification {
    def "Test command builder"() {
        given: "A builder"
        GCloudCommandBuilder builder = new GCloudCommandBuilder()

        when: "Create a command"
            builder.add("gcloud", "test1", "test2")
                   .addBoolOption("bool1", true)
                   .addBoolOption("bool2", null) // null boolean
                   .addBoolOption("bool3", false) // false value
                   .addBoolOption("  ", true) // empty option name
                   .addBoolOption(null, true) // null option name
                   .addOption("opt1", "value")
                   .addOption("opt2", "   ") // empty string
                   .addOption("opt3", "") // zero length string
                   .addOption("opt4", null) // null
                   .add((String[])[]) // zero length array
                   .addOption(null, "none1") // null option
                   .addOption("   ", "none1") // empty option
            String[] command = builder.buildCommand()

        then: "Check it"
            command.length == 5
            command[0] == "gcloud"
            command[1] == "test1"
            command[2] == "test2"
            command[3] == "--bool1"
            command[4] == "--opt1=value"
    }

    def "Test auto add gcloud"() {
        given: "A builder"
            GCloudCommandBuilder builder = new GCloudCommandBuilder()
            String[] command = []

        when: "Create a command"
            builder.add("test1", "test2")
            command = builder.buildCommand()

        then: "Check it"
            command.length == 3
            command[0] == "gcloud"
            command[1] == "test1"
            command[2] == "test2"
    }
}
