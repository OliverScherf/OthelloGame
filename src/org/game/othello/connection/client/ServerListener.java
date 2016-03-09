package org.game.othello.connection.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.game.othello.connection.server.OthelloServer;
import org.game.othello.interfaces.Loggable;

/**
 * This class listens for new Objects that come from the {@link OthelloServer}.
 * @author Oliver Scherf
 */
public class ServerListener implements Loggable, Runnable {

	private Logger logger;
	private OthelloClient othelloClient;
	private ObjectInputStream inputFromServer;

	/**
	 * Instantiate a new ServerListener.
	 * @param othelloClient
	 * @throws IOException thrown when an error orrucs when instantiating the {@link ObjectInputStream}-
	 */
	public ServerListener(OthelloClient othelloClient) throws IOException {
		this.initializeLogging();
		this.othelloClient = othelloClient;
		this.inputFromServer = new ObjectInputStream(this.othelloClient.getClientSocket().getInputStream());
		this.log(INFO, "ServerListener was initialized.");
	}

	/**
	 * Listen for new Objects send from the server.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Object received = this.inputFromServer.readObject();
				if (received != null) {
					this.othelloClient.serverObjectReceived(received);
				} 
			} catch (IOException e) {
				this.log(INFO, "Server Connection lost.");
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void initializeLogging() {
		this.logger = Logger.getLogger("Log.ServerListener");
		this.log(INFO, "Initialized ServerListener Logger");
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
