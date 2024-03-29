/*
 * Copyright 2008 The Microinstall project @sourceforge.net
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.microinstall;

/**
 * This interface
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public interface InstallListener {

    /**
     * This is called then the installation object has been sent.
     */
    public void installationFinished();

    /**
     * This is called if the installation has failed.
     * 
     * @param message
     *            the specified message that should indicate what happened.
     * @param cause
     *            the exception that was thrown when the installation failed.
     *            This could be <code>null</code> if no exception casued the
     *            problem.
     */
    public void installationFailed(String message, Exception cause);
}
