package org.game.othello.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;

/**
 * Handles the logging, including log files.
 * @author Oliver Scherf
 */
public class LoggingManager implements OthelloConstants, Loggable {

	private static final int MAX_LOG_FILES_COUNT = 10;

	private static final String LOG_FILE_PATH = "../logs/log/";
	private static final String ERROR_FILE_PATH = "../logs/error/";

	private Logger logger;

	public LoggingManager() {
		this.initializeLogging();
	}

	@Override
	public void initializeLogging() {
		String currentTime = new SimpleDateFormat().format(new java.util.Date());
		currentTime = currentTime.replace(':', '-');
		this.logger = Logger.getLogger("Log");
		this.logger.setUseParentHandlers(false);

		/*
		 * Will format every record into: ["Time"] "Logger" reports: ["Level"]:
		 * "message". e.g: [06.12.15 20:04] Logger reports: [OK]: Logger was
		 * initialized.
		 */
		LoggingFormatter logFormatter = new LoggingFormatter();

		
		// Console Handler Only prints records of level Info or above.
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(logFormatter);
		consoleHandler.setLevel(CONSOLE_LOG_LEVEL);
		this.logger.addHandler(consoleHandler);

		// Need to create directories if they don't exist.
		try {
			this.createDir(LOG_FILE_PATH);
			this.createDir(ERROR_FILE_PATH);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			// Logfile Handler Save all log reports.
			String newLogFile = LOG_FILE_PATH + "log_" + currentTime + ".txt";
			File f = new File(newLogFile);
			if (!f.getParentFile().exists())
			    f.getParentFile().mkdirs();
			f.createNewFile();
			Handler logfileHandler;
			logfileHandler = new FileHandler(newLogFile);
			logfileHandler.setFormatter(logFormatter);
			logfileHandler.setLevel(FILE_LOG_LEVEL);
			this.logger.addHandler(logfileHandler);

			// Errorfile Handler Saves all Errors (Level.SEVERE)
			String newErrorFile = ERROR_FILE_PATH + "error_" + currentTime + ".txt";
			File e = new File(newErrorFile);
			if (!e.getParentFile().exists())
			    e.getParentFile().mkdirs();
			e.createNewFile();
			Handler errorfileHandler = new FileHandler(newErrorFile);
			errorfileHandler.setFormatter(logFormatter);
			errorfileHandler.setLevel(ERROR_LOG_LEVEL);
			this.logger.addHandler(errorfileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Every log entry pass the Logging level.
		this.logger.setLevel(ALL);
		this.logger.log(INFO, "Log was initialized.");
		this.removeOldLogFiles();

	}

	/**
	 * Create a directory
	 * @param path where the directory should be created
	 * @throws IOException
	 */
	private void createDir(String path) throws IOException {
		File logDir = new File(path);
		logDir.mkdirs();
	}

	/**
	 * Removes old logfiles.
	 */
	private void removeOldLogFiles() {
		File logFiles = new File(LOG_FILE_PATH);
		File errorFiles = new File(ERROR_FILE_PATH);
		if (logFiles.listFiles().length > MAX_LOG_FILES_COUNT && removeOldLogs(logFiles)) {
			this.logger.log(WARNING, "Old Log files were deleted.");
		}
		if (errorFiles.listFiles().length > MAX_LOG_FILES_COUNT && removeOldLogs(errorFiles)) {
			this.logger.log(WARNING, "Old Error files were deleted.");
		}
	}

	private boolean removeOldLogs(File logFiles) {
		File[] allFilesArray = logFiles.listFiles();
		Arrays.sort(allFilesArray, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			}
		});
		LinkedList<File> allFiles = new LinkedList<File>(Arrays.asList(allFilesArray));
		while (allFiles.size() > MAX_LOG_FILES_COUNT) {
			File fileToDelete = allFiles.get(0);
			fileToDelete.delete();
			allFiles.remove(0);
		}
		return true;
	}

	@Override
	public void log(Level level, String msg) {
		this.logger.log(level, msg);
	}

	@Override
	public void err(String msg, Exception e) {
		this.logger.log(ERROR, msg, e);
	}
}
