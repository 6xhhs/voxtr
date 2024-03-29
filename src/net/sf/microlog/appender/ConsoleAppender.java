/*
 * Copyright 2008 The Microlog project @sourceforge.net
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
package net.sf.microlog.appender;

import java.io.IOException;
import java.io.PrintStream;

import net.sf.microlog.Level;

/**
 * An appender for the console, i.e. the logs could be appended to System.out
 * (by default). It is possible to re-direct the output to another
 * <code>PrintStream</code>.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @since 0.1
 */
public class ConsoleAppender extends AbstractAppender {

	private PrintStream console = System.out;

	/**
	 * Create a <code>ConsoleAppender</code> that is using the
	 * <code>System.out</code> for logging.
	 */
	public ConsoleAppender() {

	}

	/**
	 * Create a <code>ConsoleAppender</code> that is using
	 * <code>console</code> for logging.
	 * 
	 * @param console
	 *            the <code>PrintStream</code> to be used for logging.
	 * @throws IllegalArgumentException
	 *             if the <code>console</code> is null.
	 */
	public ConsoleAppender(PrintStream console) throws IllegalArgumentException {
		if (console == null) {
			throw new IllegalArgumentException("The console must not be null.");
		}

		this.console = console;
	}

	/**
	 * Set the console that the output will be appended to.
	 * 
	 * @param console
	 *            The console to set.
	 * @throws IllegalArgumentException
	 *             if <code>console</code> is null.
	 */
	public final void setConsole(PrintStream console)
			throws IllegalArgumentException {
		if (console == null) {
			throw new IllegalArgumentException("The console must not be null.");
		}

		this.console = console;
	}

	/**
	 * Do the logging.
	 * @param level
	 *            the level at which the logging shall be done.
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the exception to log.
	 */
	public void doLog(String name, long time, Level level, Object message, Throwable t) {
		if (logOpen && formatter != null) {
			console.println(formatter.format(name, time, level, message, t));
		} else if (formatter == null) {
			System.err.println("Please set a formatter.");
		}
	}

	/**
	 * Do nothing, as this is not applicable for the ConsoleAppender.
	 * 
	 * @see net.sf.microlog.appender.AbstractAppender#clear()
	 */
	public void clear() {
		if (console != null) {
			console.flush();
		}
	}

	/**
	 * @see net.sf.microlog.appender.AbstractAppender#close()
	 */
	public void close() throws IOException {
		if (console != null) {
			console.flush();
		}
		logOpen = false;
	}

	/**
	 * @see net.sf.microlog.appender.AbstractAppender#open()
	 */
	public void open() throws IOException {
		logOpen = true;
	}

	/**
	 * Get the size of the log. Always returns SIZE_UNDEFINED, since it is not
	 * applicable to the ConsoleAppender.
	 * 
	 * @return the size of the log.
	 */
	public long getLogSize() {
		return SIZE_UNDEFINED;
	}

}
