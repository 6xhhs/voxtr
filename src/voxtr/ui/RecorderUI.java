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
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;

import net.sf.microlog.Logger;
import voxtr.controller.AudioController;
import voxtr.data.C;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class RecorderUI implements CommandListener, Showable {
	
	private final static Logger log = Logger.getLogger();

    protected MIDlet mMidlet;
    protected Showable mBackUI;
    
    protected Form mForm;
    protected Command mStopCommand;
    
    protected AudioController mAudioController;
    
    public RecorderUI(MIDlet pMidlet, Showable pBackUI, AudioController pAudioController) {
        mMidlet = pMidlet;
        mBackUI = pBackUI;
        mAudioController = pAudioController;
        
        mForm = new Form("Recorder");
        mStopCommand = new Command(C.APP_STRING_SOFTKEY_STOP, Command.OK, 10);
        mForm.addCommand(mStopCommand);
        mForm.setCommandListener(this);        
    }
    
    // Implementation of Showable interface
    public void show() {
        Displayable ui = updateUI();
        Display.getDisplay(mMidlet).setCurrent(ui);
        mAudioController.startCapture();
    }
    
    // Implementation of CommandListener interface
    public void commandAction(Command pCommand, Displayable pDisplayable) {
        if (pCommand == mStopCommand) {
            mAudioController.stopCapture();
            mBackUI.show();
        }
    }
    
    protected Displayable updateUI() {
        return mForm;
    }

}
