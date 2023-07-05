/* Copyright (c) 2023 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package automationanywhere;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.core.security.SecureString;

import java.io.IOException;
import java.util.logging.Logger;

import static automationanywhere.DATreansformation.dAEngineJson;
import static automationanywhere.MakingList.reFormat;
import static com.automationanywhere.commandsdk.model.DataType.STRING;



@BotCommand
@CommandPkg(
        name = "ExtractionCommand",
        label = "Extraction Command",
        description = "Extraction Command description",
        node_label = "Extraction Command Node Label",
        return_type = STRING,
        return_label = "Extraction Command return Label",
        minimum_botagent_version = "21.98",
        minimum_controlroom_version = "10520")
public class ExtractionCommand {

    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @com.automationanywhere.commandsdk.annotations.GlobalSessionContext
    private GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(final GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }

    public ExtractionCommand (){

    }

    @Execute
    public StringValue compute(
            @Idx(index = "1", type = AttributeType.FILE)
                    @LocalFile
                    @Pkg(label = "Image File Path")
                    @NotEmpty
                    final String inputFilePath
//            ,@Idx(index = "2", type = AttributeType.CREDENTIAL) @Pkg(label = "Service Account")
//            final SecureString serviceAccount
    ) throws Exception {

        return new StringValue(extract(inputFilePath));


    }

    public String extract(String inputFilePath) throws Exception {
        return reFormat(inputFilePath);
    }

}
