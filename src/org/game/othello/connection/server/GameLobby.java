package org.game.othello.connection.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.connection.InitializationCommando;
import org.game.othello.connection.InteruptCommando;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;

public class GameLobby implements Loggable, OthelloConstants {

	private Thread threadPlayerOne;
	private Thread threadPlayerTwo;
	private ClientConnection playerOne;
	private ClientConnection playerTwo;
	private int onlineGameState;
	private int lobbyNumber;
	private Logger logger;
	
	/**
	 * Instantiates a new GameLobby.
	 * @param lobbyNumber the lobby number.
	 */
	public GameLobby(int lobbyNumber) {
		this.initializeLogging();
		this.lobbyNumber = lobbyNumber;
		this.onlineGameState = ONLINE_GAME_STATE_NOT_STARTED;
		this.log(INFO, "Created a new lobby #" + this.lobbyNumber);
	}

	/**
	 * Add a new ClientConnection to this lobby. If both players are present, the game will start.
	 * @param con the new ClientConnection
	 */
	public void newClientConnection(ClientConnection con) {
		if (this.playerOne == null) {
			this.playerOne = con;
			this.playerOne.sendObject(this.lobbyNumber);
		} else if (playerTwo == null) {
			this.playerTwo = con;
		}
		if (playerOne != null && playerTwo != null) {
			this.onlineGameState = ONLINE_GAME_STATE_STARTED;
			this.startGame();
		}
	}
	
	/**
	 * Closes the connection of both players.
	 */
	public void endGame() {
		this.playerOne.closeConnection();
		this.playerTwo.closeConnection();
		this.playerOne = null;
		this.playerTwo = null;
		this.onlineGameState = ONLINE_GAME_STATE_FINISHED;
		this.log(INFO, "#" + this.lobbyNumber + " is over.");
	}

	/**
	 * Starts the game, and send initial information to both players.
	 */
	private void startGame() {
		playerOne.sendObject(new InitializationCommando(ONLINE_GAME_I_START, this.lobbyNumber));
		playerTwo.sendObject(new InitializationCommando(ONLINE_GAME_OPPONENT_START, this.lobbyNumber));
		this.log(INFO, "#" + this.lobbyNumber + " is starting right now!");
	}

	/**
	 * Forwards the received object to the opponent.
	 * @param obj 
	 * @param source The client where the obj comes from (usally this)
	 */
	public void forwardObjectToOtherPlayer(Object obj, ClientConnection source) {
		if (this.playerOne.equals(source)) {
			this.playerTwo.sendObject(obj);
		} else {
			this.playerOne.sendObject(obj);
		}
	}

	public int getState() {
		return this.onlineGameState;
	}

	/**
	 * If a player quit unexpected, this method will inform the other player.
	 * @param lostConClient the client who lost connection
	 */
	public void handleConnectionLost(ClientConnection lostConClient) {
		if (this.onlineGameState == ONLINE_GAME_STATE_FINISHED) {
			return;
		}
		this.onlineGameState = ONLINE_GAME_STATE_FINISHED;
		try {
			if (lostConClient != null 
					&& this.playerOne != null 
					&& lostConClient.equals(playerOne)
					&& !playerOne.getSocket().isClosed()) {
				playerTwo.sendObject(new InteruptCommando());
				playerOne.closeConnection();
				playerTwo.closeConnection();
				if (threadPlayerOne != null) {
					threadPlayerOne.interrupt();
				}
			} else if (lostConClient != null 
					&& this.playerTwo != null 
					&& lostConClient.equals(playerTwo)
					&& !playerTwo.getSocket().isClosed()) {
				playerOne.sendObject(new InteruptCommando());
				if (threadPlayerTwo != null) {
					threadPlayerTwo.interrupt();
				}
			}
			this.log(INFO, "Closed connection from both players in the game " + this.toString());
		} catch (Exception e)  {
			// Nothing needed here, the game is over anyway.
		}
		
	}
	
	@Override
	public String toString() {
		String rc = "#" + this.lobbyNumber + " ";
		if (this.onlineGameState == ONLINE_GAME_STATE_FINISHED) {
			return rc + " is finished.";
		}
		rc += "between ";
		if (playerOne != null) {
			rc += playerOne.getSocket().getInetAddress();
		} else {
			rc += "yet not connected";
		}
		rc += " and ";
		if (playerTwo != null) {
			rc += playerTwo.getSocket().getInetAddress();
		} else {
			rc += "yet not connected";
		}
		if (this.onlineGameState == ONLINE_GAME_STATE_STARTED) {
			rc += " has started!";
		} else if (this.onlineGameState == ONLINE_GAME_STATE_NOT_STARTED) {
			rc += " has not started";
		}
		return rc;
	}

	public int getLobbyNumber() {
		return lobbyNumber;
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
		this.logger = Logger.getLogger("Log.Server.GameLobby");
		this.logger.setLevel(null);
		this.log(FINER, "Initialized GameLobby Logger");
	}
}
