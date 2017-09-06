package com.google.gcloud.wrapper

import com.google.gcloud.exec.command.CommandBuilder
import spock.lang.Specification

/**
 * Command Builder test
 */
class CommandBuilderTest extends Specification {
    def "Test command builder"() {
        given: "A builder"
        CommandBuilder builder = new CommandBuilder()

        when: "Create a command"
        builder.addCommand("gcloud", "test1", "test2")
               .addOption("bool1", true.toString())
               .addOption("bool2", false.toString()) // false value
               .addArgument("path1")
               .addOption("opt1", "value")
               .addOption("--opt2", "value")
               .addOption("--multi", ["value1", "value2"])
               .addArgument("path2");
        String[] command = builder.buildCommand()

        then: "Check command and order"
        command.length == 10
        command[0] == "gcloud"
        command[1] == "test1"
        command[2] == "test2"
        command[3] == "--bool1"
        command[4] == "--opt1=value"
        command[5] == "--opt2=value"
        command[6] == "--multi=value1"
        command[7] == "--multi=value2"
        command[8] == "path1"
        command[9] == "path2"
    }

    def "Illegal option test"() {
        given: "A builder"
        CommandBuilder builder = new CommandBuilder()

        when: "Illegal option added"
        builder.addOption(a, b)

        then:
        thrown(IllegalArgumentException)

        where:
        a    | b
        null | "x"
        ""   | "x"
        "   "| "x"
    }

    def "Illegal argument test"() {
        given: "A builder"
        CommandBuilder builder = new CommandBuilder()

        when: "Illegal argument added"
        builder.addArgument(a)

        then:
        thrown(IllegalArgumentException)

        where:
        a    | _
        null | _
        ""   | _
        "   "| _
    }
}
