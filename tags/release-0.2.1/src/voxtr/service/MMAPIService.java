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

package voxtr.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;
import voxtr.util.Logger;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 */
public class MMAPIService {
    
    protected Player mPlayer;
    protected RecordControl mRecordControl;
    protected ByteArrayOutputStream mRecordingOutput;
    protected String mContentType;
    protected byte[] mAudioData;

    public String getContentType() {
        return mContentType;
    }
    
    public void setContentType(String pContentType) {
        mContentType = pContentType;
    }
    
    public byte[] getAudioData() {
        return mAudioData;
    }
    
    public void setAudioData(byte[] pAudioData) {
        mAudioData = pAudioData;
    }
    
    
    public void startAudioPlaying() {
        log("startAudioPlaying() BEGIN");

//        String[] protocols = Manager.getSupportedProtocols(null);
//        for (int i=0, len=protocols.length; i<len; ++i) {
//            log("["+protocols[i]+"]");
//            String[] contentTypes = Manager.getSupportedContentTypes(protocols[i]);
//            for (int j=0, len2=contentTypes.length; j<len2; ++j) {
//                log("  ["+contentTypes[j]+"]");                
//            }
//        }
//        log("- - - - -");

        try {
            InputStream is = new ByteArrayInputStream(mAudioData);
            Player player = Manager.createPlayer(is, mContentType);
            player.realize();
            player.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (MediaException ex) {
            ex.printStackTrace();
        }
        log("startAudioPlaying() END");
    }
    
    public void startAudioCapture() {
        mAudioData = null;
        mContentType = null;
        (new Thread() {
            public void run() {
                log("startAudioCapture()->Thread.run() BEGIN");
                try {
                    mPlayer = Manager.createPlayer("capture://audio");
                    mPlayer.realize();
                    mRecordControl = (RecordControl)mPlayer.getControl("RecordControl");
                    mRecordingOutput = new ByteArrayOutputStream();
                    mRecordControl.setRecordStream(mRecordingOutput);
                    mRecordControl.startRecord();        
                    mPlayer.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (MediaException ex) {
                    ex.printStackTrace();
                }                
                log("startAudioCapture()->Thread.run() END");
            }
        }).start();
    }
    
    public void stopAudioCapture() {        
        log("stopAudioCapture() BEGIN");
        if (mRecordControl != null) {
            try {
                mRecordControl.stopRecord();
                mRecordControl.commit();
                mPlayer.stop();
                mContentType = mRecordControl.getContentType();
                mRecordingOutput.close();
                mPlayer.close();
                mAudioData = mRecordingOutput.toByteArray();
                mRecordingOutput = null;
                mPlayer = null;
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (MediaException ex) {
                ex.printStackTrace();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        } else {
            log("ERROR! RecordControl is null.");
        }
        log("stopAudioCapture() END");
    }

    // Utility methods
    
    protected void log(String pMessage) {
        Logger.log(this, pMessage);
    }
    
}
