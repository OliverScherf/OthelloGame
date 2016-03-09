package org.game.othello.connection.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.connection.NoLobbyFoundCommando;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;
import org.game.othello.utils.LoggingManager;

/**
 * The server for the Othello game.
 * @author Oliver Scherf
 */
public class OthelloServer implements Runnable, Loggable, OthelloConstants {

	private Logger logger;
	private ServerSocket welcomeSocket;
	private Vector<GameLobby> onlineLobbyList = new Vector<GameLobby>();

	public static void main(String argv[]) throws Exception {
		new Thread(new OthelloServer()).start();
	}

	/**
	 * Instantiates a new Othello Client.
	 */
	public OthelloServer() {
		this.initialize();
	}
	
	@Override
	public void run() {
		while (true) {
			Socket newPlayer = null;
			try {
				newPlayer = this.welcomeSocket.accept();
				this.log(INFO, "A new client connected: " + newPlayer.getInetAddress());
			} catch (IOException e) {
				this.err("Error while socket.accept()", e);
			}
			this.removeOldGames();
			ClientConnection con = new ClientConnection(newPlayer, this);
			Thread client = new Thread(con);
			client.start();
		}
	}

	/**
	 * Creates and bind the socket.
	 */
	private void initialize() {
		new LoggingManager();
		this.initializeLogging();
		try {
			this.welcomeSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			this.err("Error while creating the socket. Is the Port: " + SERVER_PORT + " already is use? Exiting programm now", e);
			System.exit(0);
		}
		Thread consoleListener = new Thread(new ConsoleListener(this));
		consoleListener.start();
		this.log(INFO, "The Othello Server was started! Listening on port: " + SERVER_PORT);
	}
	
	/**
	 * Either create of joins a lobby.
	 * @param lobbyNumber If -1 -> create new lobby, otherwise try to find an join the lobby with that number.
	 * @param newPlayer
	 * @return true, when either a lobby was created, or a existing lobby was found
	 */
	public boolean findAndJoinLobby(int lobbyNumber, ClientConnection newPlayer) {
		this.log(INFO, "The lobby number is: " + lobbyNumber);
		if (lobbyNumber == -1) {
			lobbyNumber = this.generateNewLobbyNumber();
			GameLobby gameLobby = new GameLobby(lobbyNumber);
			newPlayer.setLobby(gameLobby);
			this.onlineLobbyList.add(gameLobby);
			gameLobby.newClientConnection(newPlayer);
			this.log(INFO, "Added a new GameLobby: " + gameLobby.toString());
		} else {
			boolean lobbyFound = false;
			this.log(INFO,"Now looking for the lobby: #" + lobbyNumber);
			for (GameLobby othelloOnlineGame : onlineLobbyList) {
				if (othelloOnlineGame.getState() == ONLINE_GAME_STATE_NOT_STARTED && othelloOnlineGame.getLobbyNumber() == lobbyNumber) {
					othelloOnlineGame.newClientConnection(newPlayer);
					newPlayer.setLobby(othelloOnlineGame);
					lobbyFound = true;
					this.log(INFO, "Found and joined the lobby #" + lobbyNumber);
					break;
				}
			}
			if (!lobbyFound) {
				this.log(INFO, "Did not find the lobbynumber #" + lobbyNumber + ". Sending a NoLobbyFoundCommando to the client "
						+ " and close the connection.");
				newPlayer.sendObject(new NoLobbyFoundCommando());
				newPlayer.closeConnection();
				return false;
			}
		}
		return true;
	}

	/**
	 * Generates a lobby number, that is not already in use.
	 * @return An Integer between 100-999.
	 */
	private int generateNewLobbyNumber() {
		int max = 999;
		int min = 100;
		boolean newNumberFound = false;
		int number = -1;
		while (!newNumberFound) {
			newNumberFound = true;
			number = new Random().nextInt(max - min + 1) + min;
			synchronized (this.onlineLobbyList) {
				for (GameLobby gameLobby : this.onlineLobbyList) {
					if (gameLobby.getLobbyNumber() == number) {
						newNumberFound = false;
						break;
					}
				}
			}
		}
		return number;
	}

	/**
	 * Clean up the memory, by removing finished games.
	 */
	private void removeOldGames() {
		List<GameLobby> gamesToRemove = new ArrayList<GameLobby>();
		for (GameLobby othelloOnlineGame : this.onlineLobbyList) {
			if (othelloOnlineGame.getState() == ONLINE_GAME_STATE_FINISHED) {
				gamesToRemove.add(othelloOnlineGame);
			}
		}
		for (GameLobby gameToRemove : gamesToRemove) {
			this.log(INFO, "Removed the game " + gamesToRemove.toString());
			this.onlineLobbyList.remove(gameToRemove);
			gamesToRemove = null;
		}
	}
	
	/**
	 * Print all existing lobbies.
	 */
	public void listLobbies() {
		int i = 1;
		if (!this.onlineLobbyList.isEmpty()) {
			for (GameLobby othelloOnlineGame : this.onlineLobbyList) {
				System.out.println(i++ + ". " +  othelloOnlineGame.toString());
			}
		} else {
			System.out.println("There are no lobbies at the moment.");
		}
	}

	/**
	 * Closes the connection of both players in the lobby that is specified in the cmd.
	 * @param cmd kill command
	 */
	public void killLobby(String cmd) {
		if (cmd.length() == 8) {
			int lobbyToKill = -1;
			try {
				lobbyToKill = Integer.valueOf(cmd.substring(5));
			} catch (NumberFormatException e) {
				System.out.println("The syntax was not right, please type help to get more informations.");
			}
			boolean foundLobby = false;
			for (GameLobby gameLobby : this.onlineLobbyList) {
				if (gameLobby.getLobbyNumber() == lobbyToKill) {
					if (gameLobby.getState() != ONLINE_GAME_STATE_FINISHED){
						gameLobby.endGame();
					} else {
						System.out.println("The game was already finished.");
						this.removeOldGames();
					}
					foundLobby = true;
					System.out.println("Successfully killed " + gameLobby.toString());
					break;
				}
			}
			if (!foundLobby) {
				System.out.println("Found no lobby with that number.");
			}
		} else {
			System.out.println("The syntax was not right, please type help to get more informations.");
		}
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
		this.logger = Logger.getLogger("Log.Server");
		this.logger.setLevel(null);
		this.log(FINER, "Initialized Server Logger");
	}


}