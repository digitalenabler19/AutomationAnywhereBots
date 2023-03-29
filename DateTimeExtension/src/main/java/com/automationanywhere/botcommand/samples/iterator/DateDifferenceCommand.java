package com.automationanywhere.botcommand.samples.iterator;


import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.automationanywhere.commandsdk.model.AttributeType.DATETIME;
import static com.automationanywhere.commandsdk.model.DataType.NUMBER;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "DateDifference", label = "DateDifference",
        node_label = "DateDifference.node_label", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "DateDifference.return_label", return_type = NUMBER, return_required = true)
public class DateDifferenceCommand {
    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public NumberValue action(@Idx(index = "1", type = DATETIME)
                              @Pkg(label = "Enter First Date")
                              @NotEmpty
                              ZonedDateTime source,
                              @Idx(index = "2", type = DATETIME)
                              @Pkg(label = "Enter Second Date")
                              @NotEmpty
                              ZonedDateTime source1,
                              @Idx(index = "3", type = AttributeType.SELECT,
                                      options = { @Idx.Option(index = "3.1",
                                              pkg = @Pkg(label = "MILLIS", value = "MILLIS")),
                                              @Idx.Option(index = "3.2", pkg = @Pkg(label = "SECONDS",
                                              value = "SECONDS")), @Idx.Option(index = "3.3", pkg = @Pkg(label = "MINUTES",
                                              value = "MINUTES")), @Idx.Option(index = "3.4", pkg = @Pkg(label = "HOURS",
                                              value = "HOURS")), @Idx.Option(index = "3.5", pkg = @Pkg(label = "DAYS",
                                              value = "DAYS")), @Idx.Option(index = "3.6", pkg = @Pkg(label = "WEEKS",
                                              value = "WEEKS")), @Idx.Option(index = "3.7", pkg = @Pkg(label = "MONTHS",
                                              value = "MONTHS")), @Idx.Option(index = "3.8", pkg = @Pkg(label = "YEARS",
                                              value = "YEARS")) }) @Pkg(label = "Choose an option for units")
                                  @NotEmpty
                                  String unit){
        ChronoUnit chronoUnit = ChronoUnit.valueOf(unit.toUpperCase());
        long difference = source.until(source1, chronoUnit);
        return new NumberValue(difference);
    }

}

