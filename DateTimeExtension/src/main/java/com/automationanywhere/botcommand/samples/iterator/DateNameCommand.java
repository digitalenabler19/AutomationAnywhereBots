package com.automationanywhere.botcommand.samples.iterator;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.DATETIME;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "DateNameCommand", label = "DateNameCommand",
        node_label = "DateNameCommand.node_label", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "DateNameCommand.return_label", return_type = STRING, return_required = true)
public class DateNameCommand {

    @Execute
    public StringValue action(
            @Idx(index = "1", type = AttributeType.DATETIME)
            @Pkg(label = "Enter Date")
            @NotEmpty
            ZonedDateTime source){
        Locale locale = Locale.getDefault();
        String dayName = source.getDayOfWeek().getDisplayName(TextStyle.FULL,locale);
        String monthName = source.getMonth().getDisplayName(TextStyle.FULL,locale);
        return new StringValue(dayName +"/"+monthName);
    }
}

