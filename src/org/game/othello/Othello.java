package org.game.othello;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.interfaces.Loggable;
import org.game.othello.utils.LoggingManager;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This Class initializes the JavaFX Application.
 * @author Oliver Scherf
 */
public class Othello extends Application implements Loggable {

	private Logger logger;

	public static void main(String[] args) throws IOException {
		Othello.launch(args);
	}

	/**
	 * Gets called by {@link Application#launch(String...)}
	 */
	@Override
	public void start(Stage stage) throws Exception {
		this.initializeLogging();
		GameController.getSingleton().initializeMenu(stage);
		this.log(FINE, "The Othello was launched.");
	}

	@Override
	public void initializeLogging() {
		new LoggingManager();
		this.logger = Logger.getLogger("Log.Othello");
		this.logger.setLevel(null);
		this.log(INFO, "Initialized Othello Logger");
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
