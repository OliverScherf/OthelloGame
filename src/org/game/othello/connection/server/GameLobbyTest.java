/**
 * 
 */
package org.game.othello.connection.server;

import org.game.othello.interfaces.OthelloConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Oliver Scherf
 *
 */
public class GameLobbyTest implements OthelloConstants {

	private GameLobby gl;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		 gl = new GameLobby(300);
	}

	/**
	 * Test method for {@link org.game.othello.connection.server.GameLobby#GameLobby(int)}.
	 */
	@Test
	public void testGameLobby() {
		Assert.assertEquals(300, this.gl.getLobbyNumber());
		Assert.assertEquals(ONLINE_GAME_STATE_NOT_STARTED, this.gl.getState());
	}
}
