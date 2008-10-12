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

package voxtr.controller;

import voxtr.data.Recording;
import voxtr.service.MMAPIService;
import voxtr.service.RecordingService;
import voxtr.util.Logger;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 */
public class AudioController {

    protected MMAPIService mMMAPIService;
    protected long mStartTimeMillis;
    
    public AudioController() {
        mMMAPIService = new MMAPIService();
    }
    
    public void playSound(String pContentType, byte[] pAudioData) {
        mMMAPIService.setContentType(pContentType);
        mMMAPIService.setAudioData(pAudioData);
        mMMAPIService.startAudioPlaying();
    }
    
    public void startCapture() {
        mMMAPIService.startAudioCapture();
        mStartTimeMillis = System.currentTimeMillis();
    }    

    public void stopCapture() {
        long stopTimeMillis = System.currentTimeMillis();
        mMMAPIService.stopAudioCapture();
        String contentType = mMMAPIService.getContentType();
        byte[] audioData = mMMAPIService.getAudioData();
        
        
        if (audioData != null) {
            if (audioData.length > 0) {
                String name = RecordingService.getSecondsString(
                        mStartTimeMillis, stopTimeMillis);
                Recording recording = new Recording(
                        mStartTimeMillis, stopTimeMillis, name, 
                        contentType, audioData);
                RecordingService.addRecording(recording);
            } else {
                log("ERROR! AudioData length is 0.");
            }
        } else {
            log("ERROR! AudioData is null.");
        }        
    }    

    // Utility methods
    
    protected void log(String pMessage) {
        Logger.log(this, pMessage);
    }
    
}

