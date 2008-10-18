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

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

/**
 * This class is used for sending an SMS to another phone with an installation
 * link to your <code>MIDlet</code>.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class SmsInstaller {

	private MessageConnection messageConnection;

	/**
	 * Sends an installation SMS to the specified telephone number and with the
	 * specified installation text.
	 * 
	 * @param telephoneNo
	 *            the telephone number to send the installation SMS to.
	 * @param installationText
	 *            the text that precedes the installation link.
	 * @param installationURL
	 *            the URL where to find the MIDlet. This could either be a link
	 *            to an JAD file or an JAR file, where the JAD file is the
	 *            preferred way to do it.
	 * 
	 */
	public void install(final String telephoneNo, String installationText,
			String installationURL) throws IllegalArgumentException {
		if (installationURL == null) {
			throw new IllegalArgumentException(
					"The installationURL must not be null.");
		}

		StringBuffer messageBuffer = new StringBuffer(200);
		if (installationText != null) {
			messageBuffer.append(installationText);
		} else {
			messageBuffer.append("Please press the link below.");
		}

		messageBuffer.append(new char[] { 0x0A });
		messageBuffer.append(installationURL);
		messageBuffer.append(new char[] { 0x0A });
		final String messageString = messageBuffer.toString();

		new Thread(new Runnable() {
			public void run() {
				sendMessage(telephoneNo, messageString);
			}

		}).start();

	}

	/**
	 * Send the actual message.
	 * 
	 * @param phoneNo
	 *            the phone number to send the message to.
	 * @param messageString
	 *            the message string that is contained in the message.
	 */
	private void sendMessage(String phoneNo, String messageString) {
		try {
			messageConnection = (MessageConnection) Connector.open("sms://"
					+ phoneNo);
			final TextMessage textMessage = (TextMessage) messageConnection
					.newMessage(MessageConnection.TEXT_MESSAGE);

			textMessage.setPayloadText(messageString);
			messageConnection.send(textMessage);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
