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

package voxtr.persist;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import voxtr.data.Recording;

/**
 *
 * @author Darius Katz (dariusmailbox@gmail.com)
 */
public class RecordingDB {
    protected static final String RECORDSTORE_NAME = "VOXTR_RECORDING_DB";

    protected static RecordStore mRecordStore;
    protected static boolean mIsRecordStoreOpen = false;

    protected RecordingDB(){} //Singleton

    //Select one based on timestamp
    public static Recording select(long pTimestamp) {
        openRecordStore();

        try {
            final long filter = pTimestamp;
            RecordEnumeration records = mRecordStore.enumerateRecords(
                    new RecordFilter() {
                        public boolean matches(byte[] pCandidate) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(pCandidate);
                            DataInputStream dis = new DataInputStream(bais);
                            try {
                                long timestamp = dis.readLong();
                                if (timestamp == filter) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            return false;
                        }
            }, null, false);

            if (records.hasNextElement()) {
                byte[] data = records.nextRecord();
                Recording recording = new Recording(data, true);

                // nothing went wrong
                return recording;
            }
        } catch (InvalidRecordIDException ex) {
            ex.printStackTrace();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        } finally {
            closeRecordStore();
        }

        // someting went wrong
        return null;
    }
    
    public static Recording[] selectAll(boolean pReadFully) {
        openRecordStore();

        try {
            int numRecords = mRecordStore.getNumRecords();
            Recording[] recordings = new Recording[numRecords];

            RecordEnumeration recordEnum = 
                    mRecordStore.enumerateRecords(null, null, false);
            int r=0;
            while (recordEnum.hasNextElement()) {
                byte[] data = recordEnum.nextRecord();
                Recording recording = new Recording(data, pReadFully);
                recordings[r] = recording;
                ++r;
            }
            
            // nothing went wrong
            return recordings;
            
        } catch (InvalidRecordIDException ex) {
            ex.printStackTrace();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        } finally {
            closeRecordStore();
        }

        // someting went wrong
        return null;
    }

    public static boolean insert(Recording pRecording) {
        openRecordStore();
        try {
            byte[] data = pRecording.serialize();
            mRecordStore.addRecord(data, 0, data.length);

            // nothing went wrong
            return true;
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        } catch (RecordStoreFullException ex) {
            ex.printStackTrace();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        } finally {
            closeRecordStore();
        }

        // something went wrong
        return false;
    }

    public static void update(Recording pRecording) {
        openRecordStore();

        try {
            final long filter = pRecording.getStartTimeMillis();
            RecordEnumeration records = mRecordStore.enumerateRecords(
                    new RecordFilter() {
                        public boolean matches(byte[] pCandidate) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(pCandidate);
                            DataInputStream dis = new DataInputStream(bais);
                            try {
                                long timestamp = dis.readLong();
                                if (timestamp == filter) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            return false;
                        }
            }, null, false);

            while (records.hasNextElement()) {
                int id = records.nextRecordId();
                byte[] data = pRecording.serialize();
                mRecordStore.setRecord(id, data, 0, data.length);
            }
        } catch (InvalidRecordIDException ex) {
            ex.printStackTrace();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        } finally {
            closeRecordStore();
        }
    }

    public static void remove(Recording pRecording) {
        openRecordStore();

        try {
            final long filter = pRecording.getStartTimeMillis();
            RecordEnumeration records = mRecordStore.enumerateRecords(
                    new RecordFilter() {
                        public boolean matches(byte[] pCandidate) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(pCandidate);
                            DataInputStream dis = new DataInputStream(bais);
                            try {
                                long timestamp = dis.readLong();
                                if (timestamp == filter) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            return false;
                        }
            }, null, false);

            while (records.hasNextElement()) {
                int id = records.nextRecordId();
                mRecordStore.deleteRecord(id);
            }
        } catch (InvalidRecordIDException ex) {
            ex.printStackTrace();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        } finally {
            closeRecordStore();
        }
    }

    public static void removeAll() {
        try {
            RecordStore.deleteRecordStore(RECORDSTORE_NAME);
        } catch (RecordStoreNotFoundException ex) {
            //fine. might not exist
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }

    // Not public methods

    protected static void openRecordStore() {
        if (!mIsRecordStoreOpen) {
            try {
                mRecordStore = RecordStore.openRecordStore(RECORDSTORE_NAME, true);
                mIsRecordStoreOpen = true;
            } catch (RecordStoreNotFoundException ex) {
                ex.printStackTrace();
            } catch (RecordStoreFullException ex) {
                ex.printStackTrace();
            } catch (RecordStoreException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected static void closeRecordStore() {
        try {
            mRecordStore.closeRecordStore();
        } catch (RecordStoreNotOpenException ex) {
            // ignore
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
        mIsRecordStoreOpen = false;
    }

}
