package org.game.othello.renderer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.GameController;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;
import org.game.othello.utils.DateUtils;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * This class handles the MessageRenderer (MessageBox)
 * @author Oliver Scherf
 */
public class MessageRenderer implements OthelloConstants, Loggable {

	private Logger logger;
	private VBox messageBox;
	private TextArea messageArea;
	private TextField chatField;

	/**
	 * Instantiate a new Message Renderer
	 */
	public MessageRenderer() {
		this.initializeLogging();
		this.initializeChatField();
		this.initializeMessageArea();
		this.initializeMessageBox();
		this.log(INFO, "MessageRenderer was initialized.");
	}

	/**
	 * Initializes the chatField.
	 */
	private void initializeChatField() {
		this.chatField = new TextField();
		this.chatField.setMaxSize(CANVAS_CHATFIELD_WIDHT, CANVAS_CHATFIELD_HEIGHT);
		this.chatField.setVisible(false);
		this.chatField.setPromptText("Enter your chat messages here - hit enter to send you message to your opponent!");
		
		if (!GameController.getSingleton().getGameLogic().isOfflineMode()) {
			this.chatField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.ENTER)) {
						if (!MessageRenderer.this.chatField.getText().equals("")) {
							MessageRenderer.this.printMessageInMessageArea(CHAT_MESSAGE_THIS_CLIENT,
									MessageRenderer.this.chatField.getText());
							GameController.getSingleton().getOthelloClient()
							.sendObject(MessageRenderer.this.chatField.getText());
							MessageRenderer.this.chatField.clear();
						}
					}
				}
			});
		}
	}

	/**
	 * Initializes the messageArea.
	 */
	private void initializeMessageArea() {
		this.messageArea = new TextArea();
		this.messageArea.setMaxSize(CANVAS_CHATFIELD_WIDHT, CANVAS_MESSAGEAREA_HEIGHT);
		this.messageArea.setEditable(false);
		this.messageArea.setStyle(
				"-fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),linear-gradient(from 0px 0px to 0px 5px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background);");
	}

	/**
	 * Prints a message in the messageArea.
	 * @param source see {@link OthelloConstants#CHAT_MESSAGE_THIS_CLIENT}
	 * @param msg The msg to be printed.
	 */
	public void printMessageInMessageArea(int source, String msg) {
		StringBuilder newEntry = new StringBuilder();
		newEntry.append("\n[" + DateUtils.getCurrentTime() + "] ");
		if (source == CHAT_MESSAGE_THIS_CLIENT) {
			newEntry.append("Me: ");
		} else if (source == CHAT_MESSAGE_OPPONENT_CLIENT) {
			newEntry.append("Opponent: ");
		}
		newEntry.append(msg);
		this.messageArea.appendText(newEntry.toString());
	}

	/**
	 * Initializes the messageBox.
	 */
	private void initializeMessageBox() {
		this.messageBox = new VBox(4.0);
		this.messageBox.getChildren().addAll(this.messageArea, this.chatField);
	}

	/**
	 * TODO: Maybe move to {@link MessageRenderer#printMessageInMessageArea(int, String)}
	 * @param content
	 */
	public void printServerMessage(String content) {
		this.messageArea.appendText("\nServer: " + content);
	}
	
	/**
	 * Prints a simple message in the message area with "Info:" as Prefix.
	 * @param content
	 */
	public void printInfoMessage(String content) {		
		this.messageArea.appendText("\nInfo: " + content);
	}
	

	public VBox getMessageBox() {
		return this.messageBox;
	}
	
	public TextField getChatField() {
		return this.chatField;
	}

	public void printWinningMessage(int winner) {
		String msg = "Game Over! ";
		if (winner == PLAYER_NOBODY) {
			msg += "Draw";
		} else if (winner == PLAYER_WHITE) {
			msg += "Winning player is white.";
		} else if (winner == PLAYER_BLACK) {
			msg += "Winning player is black.";
		}
		this.printInfoMessage(msg);
	}

	@Override
	public void log(Level level, String msg) {
		this.logger.log(level, msg);
	}

	@Override
	public void err(String msg, Exception e) {
		this.logger.log(ERROR, msg, e);
	}

	@Override
	public void initializeLogging() {
		this.logger = Logger.getLogger("Log.MessageRenderer");
		this.log(INFO, "Initialized MessageRenderer Logger");
	}
}
