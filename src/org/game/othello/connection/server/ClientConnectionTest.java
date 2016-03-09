/**
 * 
 */
package org.game.othello.connection.server;

import org.game.othello.connection.client.OthelloClient;
import org.game.othello.interfaces.OthelloConstants;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Oliver Scherf
 *
 */
public class ClientConnectionTest implements OthelloConstants {
	
	private static OthelloServer srv;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		srv = new OthelloServer();
		Thread srvThread = new Thread(srv);
		srvThread.start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.game.othello.connection.server.ClientConnection#ClientConnection(java.net.Socket, org.game.othello.connection.server.OthelloServer)}.
	 */
	@Test
	public void testClientConnection() {
		// To verify, check logs in console.
		new OthelloClient();
	}


	/**
	 * Test method for {@link org.game.othello.connection.server.ClientConnection#closeConnection()}.
	 */
	@Test
	public void testCloseConnection() {
		// Check logs: ClientConnection wont receive first object.
		new OthelloClient().closeConnection();
		
	}
}
