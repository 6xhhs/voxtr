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

import voxtr.data.Recording;
import voxtr.persist.RecordingDB;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 */
public class RecordingService {

    protected static final String SECONDS_STRING = "sec";
    
    public static Recording getRecording(long pTimestamp) {
        return RecordingDB.select(pTimestamp);
    }
    
    public static Recording[] getAllRecordingsMeta() {
        return RecordingDB.selectAll(false);
    }
    
    public static void addRecording(Recording pRecording) {
        RecordingDB.insert(pRecording);
    }
    
    public static void editRecording(Recording pRecording) {
        RecordingDB.update(pRecording);
    }
    
    public static void deleteRecording(Recording pRecording) {
        RecordingDB.remove(pRecording);
    }
    
    public static long getLengthTimeMillis(Recording pRecording) {
        return pRecording.getStopTimeMillis() - pRecording.getStartTimeMillis();
    }

    public static String getSecondsString(Recording pRecording) {
        return getSecondsString(pRecording.getStartTimeMillis(), 
                pRecording.getStopTimeMillis());
    }
    
    public static String getSecondsString(long pStartTime, long pStopTime) {
        return ((pStopTime-pStartTime)/1000)+" "+SECONDS_STRING;
    }
    
}
