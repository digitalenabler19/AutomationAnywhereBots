package com.automationanywhere.botcommand.samples.iterator;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import static com.automationanywhere.commandsdk.model.DataType.*;

@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "GetCurrentTime", label = "GetCurrentTime",
        node_label = "GetCurrentTime.node_label", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "GetCurrentTime.return_label", return_type = DATETIME, return_required = true)
public class GetCurrentTime {

    @Execute
    public Value<?> action(
            @Idx(index = "1", type = TEXT)
            @Pkg(label = "Enter timezone e.g Asia/Kolkata")
            @NotEmpty
            String tm){
        DateTimeValue result = new DateTimeValue();
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(tm));
        result.set(currentTime);
        return result;
    }
}
