/**
 * 
 */
package org.game.othello.connection.client;

import org.game.othello.interfaces.OthelloConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Oliver
 *
 */
public class OthelloClientTest implements OthelloConstants {

	private static OthelloClient client;	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		client = new OthelloClient();
	}

	/**
	 * Test method for {@link org.game.othello.connection.client.OthelloClient#OthelloClient()}.
	 */
	@Test
	public void testOthelloClient() {
		Assert.assertEquals(false, client.getClientSocket().isClosed());
		Assert.assertEquals(SERVER_PORT, client.getClientSocket().getPort());
		Assert.assertEquals(true, client.getClientSocket().isBound());
		Assert.assertEquals(true, client.getClientSocket().isConnected());
	}

	/**
	 * Test method for {@link org.game.othello.connection.client.OthelloClient#sendObject(java.lang.Object)}.
	 */
	@Test
	public void testSendObject() {
		client.sendObject("Testing");
	}

	/**
	 * Test method for {@link org.game.othello.connection.client.OthelloClient#closeConnection()}.
	 */
	@Test
	public void testCloseConnection() {
		client.closeConnection();
		Assert.assertEquals(true, client.getClientSocket().isClosed());
		Assert.assertEquals(SERVER_PORT, client.getClientSocket().getPort());
		Assert.assertEquals(true, client.getClientSocket().isBound());
		Assert.assertEquals(true, client.getClientSocket().isConnected()); // see javadoc, closing does not change isConnected()
	}
}
