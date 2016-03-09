package org.game.othello.interfaces;

import java.util.logging.Level;

/**
 * The interface provide basic logging functions.
 * @author Oliver Scherf
 */
public interface Loggable {

	public static final Level ALL = Level.ALL;
	public static final Level ERROR = Level.SEVERE;
	public static final Level WARNING = Level.WARNING;
	public static final Level INFO = Level.INFO;
	public static final Level FINE = Level.FINE;
	public static final Level FINER = Level.FINER;
	public static final Level FINEST = Level.FINEST;

	public static final Level CONSOLE_LOG_LEVEL = INFO;
	public static final Level ERROR_LOG_LEVEL = ERROR;
	public static final Level FILE_LOG_LEVEL = ALL;

	public void log(Level level, String msg);

	public void err(String msg, Exception e);

	public void initializeLogging();
}
