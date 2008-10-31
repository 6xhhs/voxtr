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

package voxtr.midlet;

import javax.microedition.midlet.MIDlet;

import net.sf.microlog.Logger;
import net.sf.microlog.util.Properties;
import voxtr.ui.MainUI;
import voxtr.ui.Showable;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class VoxtrMidlet extends MIDlet {
	
	private final static Logger log = Logger.getLogger();

    protected boolean mIsFirstTime = true;

    protected Showable mStartUI;
    
    protected void startApp() {
        if (mIsFirstTime) {
            mIsFirstTime = false;
            Properties properties = new Properties(this);
            log.configure(properties);
            
            mStartUI = new MainUI(this);
            mStartUI.show();
        }         
    }

    protected void pauseApp() {
        
    }

    public void destroyApp(boolean pUnconditional) {
        mStartUI = null;
        mIsFirstTime = true;
    }

}
