/*
 * Copyright (c) 2019 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
/**
 *
 */
package com.automationanywhere.botcommand.psd.validatesignature;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.BooleanValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import java.io.File;
import java.io.IOException;

import static com.automationanywhere.commandsdk.model.AttributeType.FILE;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;


 // @author Syed Hasnain


//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
		//Unique name inside a package and label to display.
		name = "ValidateDigitalSignature", label = "ValidateDigitalSignature",
		node_label = "ValidateDigitalSignature.node_label", description = "[[PdfSignatureChecker.description]]", icon = "pkg.svg",
		
		//Return type information. return_type ensures only the right kind of variable is provided on the UI. 
		return_label = "[[PdfSignatureChecker.return_label]]", return_type = STRING, return_required = true)
public class PdfSignatureChecker {
	
	//Messages read from full qualified property file name and provide i18n capability.
	private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.samples.messages");

	//Identify the entry point for the action. Returns a Value<String> because the return type is String. 
	@Execute
	public StringValue action(
			//Idx 1 would be displayed first, with a text box for entering the value.
			@Idx(index = "1", type = FILE)
			//UI labels.
			@Pkg(label = "Enter the path to your file")
			@NotEmpty 
			String filePath) {
		
		//Internal validation, to disallow empty strings. No null check needed as we have NotEmpty on firstString.
		if ("".equals(filePath.trim()))
			throw new BotCommandException(MESSAGES.getString("emptyInputString", "firstString"));

		File pdfFile = new File(filePath);
		try (PDDocument document = PDDocument.load(pdfFile)) {
			for (PDSignature signature : document.getSignatureDictionaries()) {
				if (signature.getContents() != null) {
					return new StringValue("true");

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new StringValue("false");
	}
}
