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

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import net.sf.microlog.Logger;
import voxtr.controller.AudioController;
import voxtr.data.C;
import voxtr.data.Recording;
import voxtr.service.RecordingService;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class MainUI implements CommandListener, Showable {
	
	private final static Logger log = Logger.getLogger();

    protected MIDlet mMidlet;
    protected Command mSelectCommand;
    protected Command mMoreCommand;
    protected List mList;
    protected Showable mRecorderUI;
    protected MoreUI mMoreUI;
    protected AudioController mAudioController;
    protected Recording[] mRecMeta;
    protected Image playImage;
    protected Image recordImage;

    public MainUI(MIDlet pMidlet) {
        mMidlet = pMidlet;

        mSelectCommand = new Command(C.APP_STRING_SOFTKEY_SELECT, Command.OK,
                10);
        mMoreCommand = new Command(C.APP_STRING_SOFTKEY_MORE, Command.STOP, 10); // .STOP

        // menu
        mList = new List(C.APP_STRING_APPLICATION_NAME + " (v" + C.APP_STRING_APPLICATION_VERSION + ")", List.IMPLICIT);
        mList.setSelectCommand(mSelectCommand);
        mList.addCommand(mMoreCommand);
        mList.setCommandListener(this);

        mAudioController = new AudioController();

        mMoreUI = new MoreUI(mMidlet, this);
        mRecorderUI = new RecorderUI(mMidlet, this, mAudioController);
        
        try {
            recordImage = Image.createImage("/record.png");
            playImage = Image.createImage("/play.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
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
            } else { // play
                int index = selected - 1;
                Recording meta = mRecMeta[index];
                long timestamp = meta.getStartTimeMillis();
                Recording rec = RecordingService.getRecording(timestamp);
                mAudioController.playSound(rec.getContentType(), rec.getData());
            }
        } else if (pCommand == mMoreCommand) {
            int selected = mList.getSelectedIndex();
            if (selected > 0) { // a recording is active
                Recording rec = mRecMeta[selected - 1];
                mMoreUI.setRecording(rec);
            } else { // the record-button is active
                mMoreUI.setRecording(null);
            }
            mMoreUI.show();
        } else {
            log.warn("WARNING! Unknown command was executed.");
        }
    }

    protected Displayable updateUI() {
        mList.deleteAll();
        mList.append("Record", recordImage);

        mRecMeta = RecordingService.getAllRecordingsMeta();
        for (   int i = 0, len = mRecMeta.length; i < len; ++i) {
            String name = mRecMeta[i].getName();
            mList.append(name, playImage);
        }

        return mList;
    }

}
