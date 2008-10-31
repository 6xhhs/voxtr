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

package voxtr.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.sf.microlog.Logger;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class Recording {
	
	private final static Logger log = Logger.getLogger();

    protected long mStartTimeMillis;
    protected long mStopTimeMillis;
    protected String mName;
    protected String mContentType;
    protected int mSize;
    protected byte[] mData;

    public Recording(long pStartTimeMillis, long pStopTimeMillis, String pName, 
            String pContentType, byte[] pData) {
        mStartTimeMillis = pStartTimeMillis;
        mStopTimeMillis = pStopTimeMillis;
        mName = pName;
        mContentType = pContentType;
        mData = pData;
        
        mSize = pData != null ? pData.length : 0;
    }
    
    public Recording(byte[] pData, boolean pReadFully) {
        deserialize(pData, pReadFully);
    }

    public long getStartTimeMillis() {
        return mStartTimeMillis;
    }
    
    public long getStopTimeMillis() {
        return mStopTimeMillis;
    }
    
    public String getName() {
        return mName;
    }
    
    public String getContentType() {
        return mContentType;
    }
    
    public int getSize() {
        return mSize;
    }

    public byte[] getData() {
        return mData;
    }
    
    public byte[] serialize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeLong(mStartTimeMillis);
            dos.writeLong(mStopTimeMillis);
            dos.writeUTF(mName);
            dos.writeUTF(mContentType);
            if (mData != null) {
                dos.writeInt(mSize);
                dos.writeBoolean(true);
                dos.write(mData, 0, mSize);                
            } else {
                dos.writeInt(0);
                dos.writeBoolean(false);
                log.warn("WARNING! Recording has no data, only metadata.");
            }
            
            // nothing went wrong
            return baos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        // something went wrong
        return null;
    }
    
    protected void deserialize(byte[] pRawData, boolean pReadFully) {
        ByteArrayInputStream bais = new ByteArrayInputStream(pRawData);
        DataInputStream dis = new DataInputStream(bais);
        try {
            mStartTimeMillis = dis.readLong();
            mStopTimeMillis = dis.readLong();
            mName = dis.readUTF();
            mContentType = dis.readUTF();
            mSize = dis.readInt();
            boolean isDataAvailable = dis.readBoolean();
            if (isDataAvailable && pReadFully) {
                mData = new byte[mSize];
                int len = dis.read(mData, 0, mSize);
                if (len != mSize) {
                    // not enough bytes were copied, but why?
                    log.warn("WARNING. Less bytes read than expected.");
                }
            } else {
                mData = null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
