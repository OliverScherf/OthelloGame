package org.game.othello;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.ao.Coordinate;
import org.game.othello.connection.GameEndsCommando;
import org.game.othello.connection.InteruptCommando;
import org.game.othello.connection.NoTurnIsPossibleCommand;
import org.game.othello.connection.SurrenderCommando;
import org.game.othello.connection.client.OthelloClient;
import org.game.othello.connection.server.OthelloServer;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;
import org.game.othello.renderer.GameBoardRenderer;
import org.game.othello.renderer.RenderController;
import org.game.othello.rules.GameLogic;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * This Class manages the infrastructure of the program.
 * @author Oliver Scherf
 */
public class GameController implements OthelloConstants, Loggable {

	private static GameController singleton = new GameController();
	private Logger logger;
	private GameLogic gameLogic;
	private RenderController rendererController;
	private OthelloClient othelloClient;

	/**
	 * Main Menu will show up.
	 * @param stage JavaFX stage
	 */
	public void initializeMenu(Stage stage) {
		this.rendererController = new RenderController(stage);
		this.rendererController.startMenu();
		this.log(INFO, "Menu was started");
	}

	/**
	 * This will start the offline multiplayer mode.
	 * The {@link OthelloClient} will not be initialized.
	 */
	public void startOfflineMultiplayer() {
		this.gameLogic = new GameLogic();
		this.gameLogic.setOfflineFlags();
		this.rendererController.startGameScene();
		this.log(INFO, "Offline Multiplayer mode was started.");
	}

	/**
	 * This will start the online Multiplayer mode.
	 * The {@link OthelloClient} will be initialized and tries to connect
	 * to the server. The game starts when an opponent was found.
	 * @param lobbyNumber The lobby number to join, -1 will create a new lobby. 
	 */
	public void startOnlineMultiplayer(int lobbyNumber) {
		this.gameLogic = new GameLogic();
		this.gameLogic.setOnlineFlags();
		this.othelloClient = new OthelloClient();
		this.rendererController.startGameScene();
		if (this.othelloClient.getClientSocket() == null) {
			this.rendererController.getMessageRender().printInfoMessage("Cannot connect to the server. Please return to the menu.");
		} else {
			this.othelloClient.sendObject(lobbyNumber);
			this.enableChat();
		}
		this.log(INFO, "Online Multiplayermode was started.");
	}

	/**
	 * Handles a mouse event of the gameboard.
	 * @param indexClicked the {@link Coordinate} where the mouse event happened.
	 * @param mouseEvent If the primary button: normal turn attempt 
	 */
	public void handleGameBoardMouseAction(Coordinate indexClicked, MouseEvent mouseEvent) {
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			this.handleTurnAttempt(indexClicked);
		}
	}

	/**
	 * Put a gamecharacter on the gameboard when possible, updates the {@link GameBoardRenderer}
	 * If the turn was valid and the game is in online multiplayer mode, the {@link Coordinate} will send to the Opponent.
	 * @param indexClicked The {@link Coordinate} where a gamecharacter should be placed.
	 */
	private void handleTurnAttempt(Coordinate indexClicked) {
		if (!this.gameLogic.handleTurnAttempt(indexClicked)) {
			return;
		}
		if (!this.gameLogic.isOfflineMode()) {
			this.othelloClient.sendObject(indexClicked);
		}
		this.log(INFO, "The turn attempt was valid: Placed a gamecharacter on: " + indexClicked.toString());
		this.rendererController.update();
	}

	/**
	 * When the {@link OthelloClient} receive a {@link Coordinate}
	 * The {@link GameLogic} will make a turn. The {@link GameBoardRenderer} will update
	 * afterwards.
	 * @param index The {@link Coordinate} received from the {@link OthelloClient}
	 */
	public void handleServerTurn(Coordinate index) {
		this.gameLogic.doTurn(index);
		this.gameLogic.switchTurnOrder();
		if (!this.gameLogic.isAnyMovePossibleForThisClient()) {
			this.othelloClient.sendObject(new NoTurnIsPossibleCommand());
			this.gameLogic.switchTurnOrder();
		}
		this.rendererController.update();
		this.log(INFO, "Server turn was made. Opponent put a gamecharacter on " + index.toString());
	}

	/**
	 * This handles a {@link InteruptCommando}. This means, the opponent quit.
	 * @param cmd The command received from the  {@link OthelloServer}
	 */
	public void handleInteruptCommando(InteruptCommando cmd) {
		this.log(INFO, "Server Commando Interupt: Interupting the game.");
		this.rendererController.getMessageRender().printServerMessage("The opponent left the game. You won!");
		this.disableChat();
		this.othelloClient.closeConnection();
		this.gameLogic.setGameEndFlags();
	}

	/**
	 * This handles a {@link GameEndsCommando}. This is the normal outcome of a match.
	 * @param cmd The command received from the  {@link OthelloServer}
	 */
	public void handleGameEndsCommando(GameEndsCommando cmd) {
		this.log(INFO, "Server Commando GameEnds: End game now.");
		this.gameLogic.doTurn(cmd.getLastIndex());
		this.gameLogic.setGameEndFlags();
		this.othelloClient.closeConnection();
		this.rendererController.getMessageRender().printWinningMessage(this.gameLogic.getWinner());
		this.disableChat();
		this.rendererController.update();
	}

	/**
	 * When the user attempts to join a non existing lobby, this method is called.
	 */
	public void handleNoLobbyFound() {
		this.log(INFO, "No Lobby was found with this number");
		this.disableChat();
		this.othelloClient.closeConnection();
		this.gameLogic.setGameEndFlags();
		this.rendererController.noLobbyFound();
	}

	/**
	 * This client will lose the game, and sends it to the server if its an online game.
	 */
	public void surrender() {
		this.log(INFO, "This client surrendered.");
		if (!this.getGameLogic().isGameFinished() && !this.getGameLogic().isGameNotStarted()) {
			if (!this.getGameLogic().isOfflineMode()) {
				this.getOthelloClient().sendObject(new SurrenderCommando());
				this.getOthelloClient().closeConnection();
			}
			this.disableChat();
			this.gameLogic.setGameEndFlags();
			this.rendererController.getMessageRender().printInfoMessage("Game ends: You lost by surrender.");
		}
	}

	/**
	 * If the opponent surrenders, this method handles the further actions.
	 */
	public void opponentSurrendered() {
		this.log(INFO, "The opponent surrendered.");
		this.gameLogic.setGameEndFlags();
		this.rendererController.getMessageRender().printInfoMessage("Game ends: You won, opponent surrndered.");
		this.othelloClient.closeConnection();
		this.disableChat();
	}

	/**
	 * Instantiates the {@link GameController}. At runtime there can be only ONE {@link GameController}.
	 * Initializes logging. 
	 */
	private GameController() {
		this.initializeLogging();
	}

	/**
	 * This method simply closes the application.
	 */
	public void quitGame() {
		this.log(WARNING, "Exiting now");
		System.exit(0);
	}

	/**
	 * This method send the last turn made to the opponent and closes the connection. It also announces the winner.
	 * @param index the last turn made
	 */
	public void endGame(Coordinate index) {
		this.rendererController.getMessageRender().printWinningMessage(this.gameLogic.getWinner());
		if (!this.gameLogic.isOfflineMode()) {
			this.othelloClient.sendObject(new GameEndsCommando(index));
			this.othelloClient.closeConnection();
		}
		this.disableChat();
		this.rendererController.update();
		this.log(INFO, "The game ends.");
	}

	/**
	 * Sets the visibility of the chatField to false.
	 */
	private void disableChat() {
		this.rendererController.getMessageRender().getChatField().setVisible(false);
	}

	/**
	 * Sets the visibility of the chatField to true.
	 */
	private void enableChat() {
		this.rendererController.getMessageRender().getChatField().setVisible(true);
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
		this.logger = Logger.getLogger("Log.GameController");
		this.logger.setLevel(null);
		this.log(FINER, "Initialized GameController Logger");
	}

	/**
	 * @return The only instance of {@link GameController}
	 */
	public static GameController getSingleton() {
		return singleton;
	}

	public GameLogic getGameLogic() {
		return this.gameLogic;
	}

	public OthelloClient getOthelloClient() {
		return this.othelloClient;
	}

	public RenderController getRenderController() {
		return this.rendererController;
	}
}
