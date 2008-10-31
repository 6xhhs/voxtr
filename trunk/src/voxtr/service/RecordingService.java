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

import java.util.Calendar;
import java.util.Date;

import net.sf.microlog.Logger;
import voxtr.data.Recording;
import voxtr.persist.RecordingDB;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class RecordingService {
	
	private final static Logger log = Logger.getLogger();

    protected static final String SECONDS_STRING = "s";
    
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
    
    public static String getSecondsString(long pStartTime, long pStopTime) {
        return ((pStopTime-pStartTime)/1000)+SECONDS_STRING;
    }
    
    public static String createName(long pStartTime, long pStopTime) {
        Date time = new Date(pStopTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        String monthString;
        switch (month) {
            case Calendar.JANUARY:
                monthString = "Jan";
                break;
            case Calendar.FEBRUARY:
                monthString = "Feb";
                break;
            case Calendar.MARCH:
                monthString = "Mar";
                break;
            case Calendar.APRIL:
                monthString = "Apr";
                break;
            case Calendar.MAY:
                monthString = "May";
                break;
            case Calendar.JUNE:
                monthString = "Jun";
                break;
            case Calendar.JULY:
                monthString = "Jul";
                break;
            case Calendar.AUGUST:
                monthString = "Aug";
                break;
            case Calendar.SEPTEMBER:
                monthString = "Sep";
                break;
            case Calendar.OCTOBER:
                monthString = "Oct";
                break;
            case Calendar.NOVEMBER:
                monthString = "Nov";
                break;
            case Calendar.DECEMBER:
                monthString = "Dec";
                break;
            default:
                monthString = "???";
                break;
        }
        
        String hourString = hour<10 ? "0"+hour : Integer.toString(hour);
        String minuteString = minute<10 ? "0"+minute : Integer.toString(minute);
        
        String length = getSecondsString(pStartTime, pStopTime);
        return day+"-"+monthString+" "+hourString+":"+minuteString+" "+length;
    }
    
}
