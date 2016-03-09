package org.game.othello.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Oliver Scherf
 * This class formats the records from the Logger.
 */
public class LoggingFormatter extends Formatter {

	@Override
	public String format(LogRecord r) {
		StringBuilder sb = new StringBuilder();

		String time = new SimpleDateFormat().format(new java.util.Date(r.getMillis()));

		sb.append("[" + time + "] " + this.getLoggerName(r));

		sb.append("reports: " + this.getLevel(r) + this.getWhitespaceAfterLevel(r) + r.getMessage());
		if (null != r.getThrown()) {
			sb.append(" Throwable occurred: ");
			Throwable t = r.getThrown();
			PrintWriter pw = null;
			try {
				StringWriter sw = new StringWriter();
				pw = new PrintWriter(sw);
				t.printStackTrace(pw);
				sb.append(sw.toString());
			} finally {
				if (pw != null) {
					try {
						pw.close();
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}
		sb.append('\n');
		return sb.toString();
	}

	/**
	 * Calculates the whitespace in order to get a clean log entry.
	 * @param r
	 * @return
	 */
	private String getWhitespaceAfterLevel(LogRecord r) {
		StringBuffer whitespace = new StringBuffer();
		for (int i = this.getLevel(r).length(); i < 13; ++i) {
			whitespace.append(' ');
		}
		return whitespace.toString();
	}

	/**
	 * Equals the length of all Loggernames
	 * @param r
	 * @return
	 */
	private String getLoggerName(LogRecord r) {
		StringBuffer loggerName = new StringBuffer(r.getLoggerName());
		for (int i = loggerName.length(); i < 29; ++i) {
			loggerName.append(' ');
		}
		return loggerName.toString();
	}

	/**
	 * 
	 * @param record
	 * @return Level
	 */
	private String getLevel(LogRecord r) {
		String level = r.getLevel().toString();
		if (level.equals(Level.SEVERE)) {
			level = "FATAL";
		}

		return "[" + level + "]";
	}
}
