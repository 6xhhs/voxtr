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
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import voxtr.data.C;
import voxtr.data.Recording;
import voxtr.midlet.VoxtrMidlet;
import voxtr.service.RecordingService;
import voxtr.util.Logger;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class MoreUI implements CommandListener, Showable {

    protected MIDlet mMidlet;
    protected Showable mBackUI;
    protected Showable mSendUI;
    
    protected Command mSelectCommand;
    protected Command mBackCommand;
    protected List mList;
        
    protected Recording mRecording;
    
    
    protected int mDelete;
    protected int mHelp;
    protected int mAbout;
    protected int mSend;
    protected int mExit;
    
    public MoreUI(MIDlet pMidlet, Showable pBackUI) {
        mMidlet = pMidlet;
        mBackUI = pBackUI;
        
        mSelectCommand = new Command(C.APP_STRING_SOFTKEY_SELECT, Command.OK, 10);
        mBackCommand = new Command(C.APP_STRING_SOFTKEY_BACK, Command.BACK, 10);
        mList = new List(C.APP_STRING_SOFTKEY_MORE, List.IMPLICIT);
        mList.setSelectCommand(mSelectCommand);
        mList.addCommand(mBackCommand);
        mList.setCommandListener(this);
        
        mSendUI = new SendUI(mMidlet, this);
    }
    
    public void setRecording(Recording pRecording) {
        mRecording = pRecording;
    }
    
    // Implementation of Showable interface
    public void show() {
        Displayable ui = updateUI();
        Display.getDisplay(mMidlet).setCurrent(ui);
    }
    
    // Implementation of CommandListener interface
    public void commandAction(Command pCommand, Displayable pDisplayable) {
        if (pCommand == mSelectCommand) {
            int selected = mList.getSelectedIndex();
            if (selected == mDelete) {
                if (mRecording != null) {
                    log("DELETE pressed");
                    RecordingService.deleteRecording(mRecording);
                } else {
                    log("SYSTEM ERROR! mRecording is null.");
                }
                mBackUI.show();
            } else if (selected == mHelp) {
                info("Help", "\n\nHelp text not implemented yet.\n\n");
            } else if (selected == mAbout) {
                info("About", "\n\nVoxtr\n\nThe Voice Recorder\n\n\n\n"+
                        "Version: "+C.APP_STRING_APPLICATION_VERSION+"\n\n"+
                        "Developed by the Voxtr team:\n"+
                        "Darius Katz\n"+
                        "Johan Karlsson\n"+
                        "\n\nVoxtr is subject to the terms and conditions of Apache License v2.0\n"+
                        "More info:\nhttp://www.apache.org/licenses/LICENSE-2.0\n"+
                        "\nThe Voxtr homepage is:\n"+
                        "http://code.google.com/p/voxtr\n"+
                        "\nThanks to:\n"+
                        "Our families for their patience and understanding.\n\n\n");
            } else if(selected == mSend){
            	mSendUI.show();
            } else if (selected == mExit) {
            
                ((VoxtrMidlet)mMidlet).destroyApp(true);
                mMidlet.notifyDestroyed();                                
            } else {
                log("WARNING! Unknown index was selected.");
            }
        } else if (pCommand == mBackCommand) {
            mRecording = null;
            mBackUI.show();
        } else {
            log("WARNING! Unknown command was executed.");
        }
    }
    
    protected Displayable updateUI() {
        mList.deleteAll();
        
        int index = 0;
        
        if (mRecording != null) {
            mList.append("Delete", null);
            mDelete = index;
            ++index;
        } else {
            mDelete = -1;
        }
        
        mList.append("Help", null);
        mHelp = index;
        ++index;
        
        mList.append("About "+C.APP_STRING_APPLICATION_NAME, null);
        mAbout = index;
        ++index;
        
        mList.append("Share "+C.APP_STRING_APPLICATION_NAME, null);
        mSend = index;
        ++index;
        
        mList.append("Exit", null);
        mExit = index;
        ++index;
        
        return mList;
    }
    
    // Utility methods
    
    protected void info(String pTitle, String pMessage)  {
        Alert info = new Alert(pTitle, pMessage, null, AlertType.INFO);
        info.setTimeout(Alert.FOREVER);
        Display.getDisplay(mMidlet).setCurrent(info);
        
    }
    
    protected void log(String pMessage) {
        Logger.log(this, pMessage);
    }
}
 
