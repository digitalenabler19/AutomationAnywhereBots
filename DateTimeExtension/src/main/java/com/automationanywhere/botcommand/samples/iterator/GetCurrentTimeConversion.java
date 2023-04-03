package com.automationanywhere.botcommand.samples.iterator;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.DATETIME;

@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "GetCurrentTime", label = "GetCurrentTime",
        node_label = "GetCurrentTime.node_label", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "GetCurrentTime.return_label", return_type = DATETIME, return_required = true)
public class GetCurrentTimeConversion {

    @Execute
    public Value<?> action(
            @Idx(index = "1", type = AttributeType.DATETIME)
            @Pkg(label = "Enter Date & Time for the value you want to convert")
            @NotEmpty
            ZonedDateTime source,
            @Idx(index = "2", type = TEXT)
            @Pkg(label = "Enter timezone e.g Asia/Kolkata")
            @NotEmpty
            String tm){
        DateTimeValue result = new DateTimeValue();
        ZoneId timeZone = ZoneId.of(tm);
        ZonedDateTime currentTime = source.withZoneSameInstant(timeZone);
        result.set(currentTime);
        return result;
    }
}
