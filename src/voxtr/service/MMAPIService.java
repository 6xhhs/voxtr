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

import net.sf.microlog.Logger;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 */
public class MMAPIService {
	
	private final static Logger log = Logger.getLogger();
    
    protected Player mRecordingPlayer;
    protected Player mPlayingPlayer;
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
        log.debug("startAudioPlaying() BEGIN");

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
            if (mPlayingPlayer!=null && mPlayingPlayer.getState()==Player.STARTED) {
                mPlayingPlayer.stop();
            }            
            InputStream is = new ByteArrayInputStream(mAudioData);
            mPlayingPlayer = Manager.createPlayer(is, mContentType);
            mPlayingPlayer.realize();
            mPlayingPlayer.start();                
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (MediaException ex) {
            ex.printStackTrace();
        }
        log.debug("startAudioPlaying() END");
    }
    
    public void startAudioCapture() {
        mAudioData = null;
        mContentType = null;
        (new Thread() {
            public void run() {
                log.debug("startAudioCapture()->Thread.run() BEGIN");
                try {
                    mRecordingPlayer = Manager.createPlayer("capture://audio");
                    mRecordingPlayer.realize();
                    mRecordControl = (RecordControl)mRecordingPlayer.getControl("RecordControl");
                    mRecordingOutput = new ByteArrayOutputStream();
                    mRecordControl.setRecordStream(mRecordingOutput);
                    mRecordControl.startRecord();        
                    mRecordingPlayer.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (MediaException ex) {
                    ex.printStackTrace();
                }                
                log.debug("startAudioCapture()->Thread.run() END");
            }
        }).start();
    }
    
    public void stopAudioCapture() {        
        log.debug("stopAudioCapture() BEGIN");
        if (mRecordControl != null) {
            try {
                mRecordControl.stopRecord();
                mRecordControl.commit();
                mRecordingPlayer.stop();
                mContentType = mRecordControl.getContentType();
                mRecordingOutput.close();
                mRecordingPlayer.close();
                mAudioData = mRecordingOutput.toByteArray();
                mRecordingOutput = null;
                mRecordingPlayer = null;
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (MediaException ex) {
                ex.printStackTrace();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        } else {
            log.error("ERROR! RecordControl is null.");
        }
        log.debug("stopAudioCapture() END");
    }
    
}
