package org.game.othello.connection.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;
import org.game.othello.connection.GameEndsCommando; 
import org.game.othello.connection.InitializationCommando; 
import org.game.othello.connection.NoTurnIsPossibleCommand; 
import org.game.othello.connection.NoLobbyFoundCommando;
import org.game.othello.connection.SurrenderCommando;
import org.game.othello.connection.InteruptCommando;

/**
 * @author Oliver Scherf
 */
@SuppressWarnings("unused") // We need these unused imports, to avoid ClassNotFound Exception
public class ClientConnection implements Runnable, Loggable, OthelloConstants {

	private Logger logger;
	private Socket socket;
	private ObjectInputStream inputFromClient;
	private ObjectOutputStream outputToClient;
	private GameLobby lobby;
	private OthelloServer srv;

	/**
	 * Instantiate a new Connection.
	 * @param socket
	 * @param srv
	 */
	public ClientConnection(Socket socket, OthelloServer srv) {
		this.initializeLogging();
		this.srv = srv;
		this.socket = socket;
		try {
			this.inputFromClient = new ObjectInputStream(this.socket.getInputStream());
			this.outputToClient = new ObjectOutputStream(this.socket.getOutputStream());
			this.outputToClient.flush();
		} catch (IOException e) {
			this.err("Error while initializing ObjectIn- or Outputstream", e);
		}
		this.log(INFO, "Successfully created a connection to " + this.socket.getInetAddress());
	}

	@Override
	public void run() {
		if (!this.waitForLobbyNumberAndVerify()) {
			return;
		}
		while (true) {
			try {
				Object obj = this.inputFromClient.readObject();
				if (obj instanceof GameEndsCommando || 
						obj instanceof SurrenderCommando) {
					this.log(INFO, "The game " + this.lobby.toString() + " is ending now.");
					this.clientObjectReceived(obj);
					this.lobby.endGame();
					break;
				} else if (obj != null) {
					this.clientObjectReceived(obj);
				}
			} catch (IOException e) {
				this.log(WARNING, "IOException (Connection lost or game ended normally.): " + this.socket.getInetAddress() + ". Closing the game now.");
				if (this.lobby != null && this.lobby.getState() != ONLINE_GAME_STATE_FINISHED) {
					this.lobby.handleConnectionLost(this);
				}
				this.closeConnection();
				break;
			} catch (ClassNotFoundException e) {
				this.err("Class was not found, please make sure client and server are up to date! Closing connection now.", e);
				this.closeConnection();
				break;
			} catch (InterruptedException e) {
				this.err("The Thread " + this.socket.getInetAddress() + " was interrupted.", e);
				this.closeConnection();
				return;
			}
		}
		this.log(INFO, "The Thread of the client " + this.getSocket().getInetAddress() + " terminated normally.");
	}
	/**
	 * This method tries to find or create a lobby.
	 * @return true, when the first object was an Integer.
	 */
	private boolean waitForLobbyNumberAndVerify() {
		Object obj = null;
		try {
			obj = this.inputFromClient.readObject();
		} catch (IOException e) {
			this.err("Error while receiving first object. Closing connection now.", e);
			this.closeConnection();
			return false;
		} catch (ClassNotFoundException e) {
			this.err("Class was not found, please make sure client and server are up to date! Closing connection now.", e);
			this.closeConnection();
			return false;
		}
		// The first object has to be the lobby number.
		if (obj instanceof Integer) {
			this.srv.findAndJoinLobby((Integer) obj, this);
		} else {
			this.log(ERROR, "The first object was not an Integer. Closing connection now.");
			this.closeConnection();
			return false;
		}
		return true;
	}

	/**
	 * Forwards the Object to the opponent.
	 * @param obj the object to be send to the opponent.
	 * @throws InterruptedException
	 */
	private void clientObjectReceived(Object obj) throws InterruptedException {
		this.log(INFO, this.socket.getInetAddress() + ": Received an Object: " + obj.toString());
		if (lobby != null && lobby.getState() == ONLINE_GAME_STATE_STARTED) {
			this.lobby.forwardObjectToOtherPlayer(obj, this);
		}
	}
	
	public void setLobby(GameLobby lobby) {
		this.lobby = lobby;
	}

	/**
	 * Sends the obj to the client.
	 * @param obj the object that should be send.
	 */
	public void sendObject(Object obj) {
		try {
			this.outputToClient.writeObject(obj);
			this.outputToClient.flush();
			this.log(INFO, "Send " + obj.toString() + " to the client " + this.socket.getInetAddress());
		} catch (IOException e) {
			this.err("Error while sending object: " + obj.toString(), e);
		}
	}
	
	/**
	 * Closes the socket.
	 */
	public void closeConnection() {
		try {
			if (!this.socket.isClosed()) {
				this.socket.close();
			}
		} catch (IOException e) {
			this.err("Error while closing socket", e);
		}
	}

	public Socket getSocket() {
		return this.socket;
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
		this.logger = Logger.getLogger("Log.Server.ClientConnection");
		this.logger.setLevel(null);
		this.log(FINER, "Initialized Connection Logger");
	}

}
