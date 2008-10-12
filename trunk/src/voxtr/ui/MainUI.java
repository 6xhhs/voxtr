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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import voxtr.controller.AudioController;
import voxtr.data.C;
import voxtr.data.Recording;
import voxtr.midlet.VoxtrMidlet;
import voxtr.service.RecordingService;
import voxtr.util.Logger;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 */
public class MainUI implements CommandListener, Showable {

    protected MIDlet mMidlet;
    
    protected Command mSelectCommand;
    protected Command mDeleteCommand;
    protected List mList;
    
    protected Showable mRecorderUI;
    
    protected AudioController mAudioController;
    
    protected Recording[] mRecMeta;
    
    public MainUI(MIDlet pMidlet) {
        mMidlet = pMidlet;
        
        mSelectCommand = new Command(C.APP_STRING_SOFTKEY_SELECT, Command.OK, 10);
        mDeleteCommand = new Command("Delete", Command.STOP, 10);
        mList = new List(C.APP_STRING_APPLICATION_NAME+
                " ("+C.APP_STRING_APPLICATION_VERSION+")", List.IMPLICIT);
        mList.setSelectCommand(mSelectCommand);
        mList.addCommand(mDeleteCommand);
        mList.setCommandListener((CommandListener)this);

        mAudioController = new AudioController();
        
        mRecorderUI = new RecorderUI(mMidlet, (Showable)this, mAudioController);
        
//        log("supports.recording = "+System.getProperty("supports.recording"));
//        log("supports.audio.capture = "+System.getProperty("supports.audio.capture"));
//        log("supports.video.capture = "+System.getProperty("supports.video.capture"));        
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
            if (selected == 0) { // record
                mRecorderUI.show();
            } else if (selected == mRecMeta.length+1) { // exit
                ((VoxtrMidlet)mMidlet).destroyApp(true);
                mMidlet.notifyDestroyed();
            } else { // play
                int index = selected-1;
                Recording meta = mRecMeta[index];
                long timestamp = meta.getStartTimeMillis();
                Recording rec = RecordingService.getRecording(timestamp);
                mAudioController.playSound(rec.getContentType(), rec.getData());
            }
        } else if (pCommand == mDeleteCommand) {
            int selected = mList.getSelectedIndex();
            if (selected != 0 && selected != mRecMeta.length+1) { // record
                int index = selected - 1;
                Recording rec = mRecMeta[index];
                RecordingService.deleteRecording(rec);
                updateUI();
            }
        } else {
            log("WARNING! Unkown command was executed.");
        }
    }
    
    protected Displayable updateUI() {
        mList.deleteAll();
        mList.append("<Record>", null);
        
        mRecMeta = RecordingService.getAllRecordingsMeta();
        for (int i=0, len=mRecMeta.length; i<len; ++i) {
            String name = mRecMeta[i].getName();
            mList.append(name, null);
        }
        
        mList.append("<Exit>", null);
        
        return mList;
    }
    
    // Utility methods
    
    protected void log(String pMessage) {
        Logger.log(this, pMessage);
    }
}
 
