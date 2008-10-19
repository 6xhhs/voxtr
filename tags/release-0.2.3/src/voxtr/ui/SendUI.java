/*
 * Copyright 2008 Voxtr - The Open Source Project 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package voxtr.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import net.sf.microinstall.SmsInstaller;

import voxtr.data.C;
import voxtr.util.Logger;

/**
 * 
 * @author Darius Katz (dariusmailbox@gmail.com)
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class SendUI implements CommandListener, Showable {

	protected MIDlet mMidlet;
	protected Showable mBackUI;

	protected Form mForm;
	protected TextField mPhoneTextField;
	protected TextField mUserTextField;

	protected Command mSendCommand;
	protected Command mBackCommand;

	protected SmsInstaller installer;

	public SendUI(MIDlet pMidlet, Showable pBackUI) {
		mMidlet = pMidlet;
		mBackUI = pBackUI;

		mForm = new Form("Send Voxtr");
		mForm.append("Send Voxtr to:");
		mPhoneTextField = new TextField("Phone", "", 30, TextField.PHONENUMBER);
		mForm.append(mPhoneTextField);
		mUserTextField = new TextField("Text", null, 100, TextField.ANY);
		mForm.append(mUserTextField);

		mSendCommand = new Command(C.APP_STRING_SOFTKEY_SEND, Command.OK, 10);
		mBackCommand = new Command(C.APP_STRING_SOFTKEY_BACK, Command.BACK, 10);

		mForm.addCommand(mSendCommand);
		mForm.addCommand(mBackCommand);

		mForm.setCommandListener(this);

		installer = new SmsInstaller();
	}

	// Implementation of Showable interface
	public void show() {
		Displayable ui = updateUI();
		Display.getDisplay(mMidlet).setCurrent(ui);
	}

	// Implementation of CommandListener interface
	public void commandAction(Command pCommand, Displayable pDisplayable) {
		if (pCommand == mSendCommand) {
			String phoneNo = mPhoneTextField.getString();
			String userText = mUserTextField.getString();

			if (userText != null && userText.length() < 1) {
				userText = "A friend wants you to install Voxtr. Select the link below to install Voxtr.    ";
			}else{
				userText += "   ";
			}

			if (phoneNo != null && phoneNo.length() > 1) {
				installer.install(phoneNo, userText,
						"http://voxtr.googlecode.com/files/Voxtr.jad");
			} else {
				this.info("No phone no entered", "Please enter a phone no");
			}
		} else if (pCommand == mBackCommand) {
			mBackUI.show();
		} else {
			log("WARNING! Unknown command was executed.");
		}
	}

	protected Displayable updateUI() {

		return mForm;
	}

	// Utility methods

	protected void info(String pTitle, String pMessage) {
		Alert info = new Alert(pTitle, pMessage, null, AlertType.INFO);
		info.setTimeout(Alert.FOREVER);
		Display.getDisplay(mMidlet).setCurrent(info);

	}

	protected void log(String pMessage) {
		Logger.log(this, pMessage);
	}
}
