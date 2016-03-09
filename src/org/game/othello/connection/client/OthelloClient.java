package org.game.othello.connection.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.GameController;
import org.game.othello.ao.Coordinate;
import org.game.othello.connection.GameEndsCommando;
import org.game.othello.connection.InitializationCommando;
import org.game.othello.connection.InteruptCommando;
import org.game.othello.connection.NoLobbyFoundCommando;
import org.game.othello.connection.NoTurnIsPossibleCommand;
import org.game.othello.connection.SurrenderCommando;
import org.game.othello.connection.server.OthelloServer;
import org.game.othello.interfaces.Loggable;
import org.game.othello.interfaces.OthelloConstants;

/**
 * This Class handles the Connection to the server. 
 * @author Oliver Scherf
 */
public class OthelloClient implements OthelloConstants, Loggable {

	private Logger logger;
	private Socket clientSocket;
	private ObjectOutputStream outputToServer;
	private int lobbyNumber;

	/**
	 * Instantiate a new OthelloClient.
	 */
	public OthelloClient() {
		this.initializeLogging();
		try {
			this.initialize();
		} catch (UnknownHostException e) {
			this.err("Unknown Host", e);
		} catch (ConnectException e) {
			this.err("Cannot connect to the server.", e);
		} catch (IOException e) {
			this.err("Error while initializing OthelloClient", e);
		}
	}

	/**
	 * Create a socket to connect to the {@link OthelloServer} and initializes the 
	 * Object In- and OutputStream.
	 * @throws UnknownHostException When the Host is not available.
	 * @throws IOException when a IOException occurs.
	 */
	private void initialize() throws UnknownHostException, IOException {
		this.clientSocket = new Socket(SERVER_IP, SERVER_PORT);
		this.outputToServer = new ObjectOutputStream(this.clientSocket.getOutputStream());
		Thread srvListener = new Thread(new ServerListener(this));
		srvListener.setDaemon(true);
		srvListener.start();
		this.log(INFO, "Othello Client was initialized.");
	}

	/**
	 * @param obj Object to send to the Server.
	 */
	public synchronized void sendObject(Object obj) {
		if (obj != null) {
			try {
				this.outputToServer.writeObject(obj);
				this.outputToServer.flush();
			} catch (IOException e) {
				this.err("Error while sending message: " + obj.toString(), e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * The handling of the received Object depends on the type.
	 * @param obj Object that was received from the server.
	 * 		  If obj is an instance of {@link InitializationCommando} - The game starts.
	 * 		  If obj is an instance of {@link Integer} - Opponent did a turn.
	 * 		  If obj is an instance of {@link String} - Simple Chat Message from Opponent
	 * 		  If obj is an instance of {@link Coordinate} - Opponent did a turn.
	 * 		  If obj is an instance of {@link InteruptCommando} - see {@link InteruptCommando}
	 * 		  If obj is an instance of {@link GameEndsCommando} - see {@link GameEndsCommando}
	 * 		  If obj is an instance of {@link NoTurnIsPossibleCommand} - see {@link NoTurnIsPossibleCommand}
	 * 		  If obj is an instance of {@link SurrenderCommando} - see {@link SurrenderCommando}
	 * 		  If obj is an instance of {@link NoLobbyFoundCommando} - see {@link NoLobbyFoundCommando}
	 */
	public void serverObjectReceived(Object obj) {
		this.log(INFO, "Server Object received: " + obj);
		if (obj instanceof InitializationCommando) {
			this.log(INFO, "InitializationCommando: " + ((InitializationCommando) obj).toString());
			this.lobbyNumber = ((InitializationCommando) obj).getLobbyNumer();
			GameController.getSingleton().getGameLogic().startOnlineGame((InitializationCommando) obj);
		} else if (obj instanceof Integer) {
			this.lobbyNumber = (Integer) obj;
			GameController.getSingleton().getRenderController().getMessageRender().printInfoMessage("You created lobby #" + this.lobbyNumber);
			GameController.getSingleton().getRenderController().update();
		} else if (obj instanceof String) {
			this.log(INFO, "Chat Message Received: " + obj.toString());
			GameController.getSingleton().getRenderController().getMessageRender()
					.printMessageInMessageArea(CHAT_MESSAGE_OPPONENT_CLIENT, obj.toString());
		} else if (obj instanceof Coordinate) {
			this.log(INFO, "Coordiante received: " + ((Coordinate) obj).toString());
			GameController.getSingleton().handleServerTurn((Coordinate) obj);
		} else if (obj instanceof InteruptCommando) {
			this.log(INFO, "ServerCommando received: " + ((InteruptCommando) obj).toString());
			GameController.getSingleton().handleInteruptCommando((InteruptCommando) obj);
		} else if (obj instanceof GameEndsCommando) {
			this.log(INFO, "ServerCommando received: " + ((GameEndsCommando) obj).toString());
			GameController.getSingleton().handleGameEndsCommando((GameEndsCommando) obj);
			this.closeConnection();
		} else if (obj instanceof NoTurnIsPossibleCommand) {
			this.log(INFO, "ServerCommando received: " + ((NoTurnIsPossibleCommand) obj).toString());
			GameController.getSingleton().getGameLogic().setCurrentPlayer(GameController.getSingleton().getGameLogic().getClientPlayer());
			GameController.getSingleton().getRenderController().update();
		} else if (obj instanceof SurrenderCommando) {
			this.log(INFO, "ServerCommando received: " + ((SurrenderCommando) obj).toString());
			GameController.getSingleton().opponentSurrendered();
		} else if (obj instanceof NoLobbyFoundCommando) {
			this.log(INFO, "ServerCommando received: " + ((NoLobbyFoundCommando) obj).toString());
			GameController.getSingleton().handleNoLobbyFound();
		} 
	}

	/**
	 * Closes the socket to the server.
	 */
	public void closeConnection() {
		try {
			if (clientSocket != null) {
				this.clientSocket.close();
			}
			this.log(INFO, "Closed the socket.");
		} catch (IOException e) {
			this.err("Error while closing the socket.", e);
		}
	}
	
	@Override
	public void initializeLogging() {
		this.logger = Logger.getLogger("Log.OthelloClient");
		this.log(INFO, "Initialized OthelloClient Logger");
	}

	@Override
	public void log(Level level, String msg) {
		this.logger.log(level, msg);
	}

	@Override
	public void err(String msg, Exception e) {
		this.logger.log(ERROR, msg, e);
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public int getLobbyNumber() {
		return lobbyNumber;
	}
}
