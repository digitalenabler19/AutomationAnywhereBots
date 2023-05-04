package com.automationanywhere.botcommand.psd.authentication;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

@BotCommand
//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "PCHI_RPA_Authentication", label = "[[PCHI_RPA_Authentication.label]]",
        node_label = "[[PCHI_RPA_Authentication.node_label]]",  description = "[[PCHI_RPA_Authentication.description]]", icon = "ruler_icon.svg",
        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[PCHI_RPA_Authentication.return_label]]", return_type = STRING, return_required = true)

public class PCHI_RPA_Authentication {
    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public StringValue action(
            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "1", type = TEXT)
            //UI labels.
            @Pkg(label = "Enter Username")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
            String username,

            @Idx(index = "2", type = TEXT)
            //UI labels.
            @Pkg(label = "Enter Password")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
            String password) {
        //Internal validation, to disallow empty inputs. No null check needed as we have NotEmpty on CMInput.
        if ("".equals(username.trim()))
            throw new BotCommandException("Please enter username");

        // Username configuration will come / rules check
        if ("".equals(password.trim()))
            throw new BotCommandException("Please enter password");

        // Password configuration will come / rules check

        //Business logic
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL (new EnateAPI_EndPoints().PCHI_RPA_Authentication);
            HttpURLConnection con1 = (HttpURLConnection)url.openConnection();
            con1.setRequestMethod("POST");
            con1.setRequestProperty("Content-Type", "application/json");
            con1.setRequestProperty("Accept", "application/json");
            con1.setDoOutput(true);

            String jsonInputString = "{\"Username\":\""+username+"\",\"Password\":\""+password+"\"}";

            try(OutputStream os = con1.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con1.getInputStream(), "utf-8"))) {

                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                //System.out.println(response.toString());
            }

        }
        catch (Exception e){
            throw new BotCommandException("There was an issue getting the Enate API call. Full Exception Text: " + e.toString());
        }

        //Return NumberValue
        return new StringValue(response.toString());

    }
  
}
