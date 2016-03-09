/**
 * 
 */
package org.game.othello.rules;

import java.util.Vector;

import org.game.othello.ao.Coordinate;
import org.game.othello.ao.Gamefield;
import org.game.othello.interfaces.OthelloConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Oliver Scherf
 */
public class GameLogicTest implements OthelloConstants {
	
	private static GameLogic gl;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		gl = new GameLogic();
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#GameLogic()}.
	 */
	@Test
	public void testGameLogic() {
		Vector<Vector<Gamefield>> beginningGameBoard = new Vector<Vector<Gamefield>>(GAMEBOARD_SIZE);;
		for (int i = 0; i < GAMEBOARD_SIZE; i++) {
			beginningGameBoard.add(new Vector<Gamefield>());
		}
		for (int x = 0; x < GAMEBOARD_SIZE; ++x) {
			for (int y = 0; y < GAMEBOARD_SIZE; ++y) {
				beginningGameBoard.get(x).add(new Gamefield(PLAYER_NOBODY, new Coordinate(x, y)));
			}
		}
		beginningGameBoard.get(3).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(3, 3)));
		beginningGameBoard.get(4).set(4, new Gamefield(PLAYER_WHITE, new Coordinate(4, 4)));
		beginningGameBoard.get(3).set(4, new Gamefield(PLAYER_BLACK, new Coordinate(3, 4)));
		beginningGameBoard.get(4).set(3, new Gamefield(PLAYER_BLACK, new Coordinate(4, 3)));
		Assert.assertEquals(beginningGameBoard, gl.getGameBoard());
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#setOfflineFlags()}.
	 */
	@Test
	public void testSetOfflineFlags() {
		gl.setOfflineFlags();
		Assert.assertEquals(true, gl.isOfflineMode());
		Assert.assertEquals(PLAYER_BLACK, gl.getCurrentPlayer());
		Assert.assertEquals(PLAYER_BLACK, gl.getClientPlayer());
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#setOnlineFlags()}.
	 */
	@Test
	public void testSetOnlineFlags() {
		gl.setOnlineFlags();
		Assert.assertEquals(false, gl.isOfflineMode());
		Assert.assertEquals(PLAYER_NOBODY, gl.getCurrentPlayer());
		Assert.assertEquals(PLAYER_NOBODY, gl.getClientPlayer());
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#setGameEndFlags()}.
	 */
	@Test
	public void testSetGameEndFlags() {
		gl.setGameEndFlags();
		Assert.assertEquals(PLAYER_NOBODY, gl.getCurrentPlayer());
		Assert.assertEquals(PLAYER_NOBODY, gl.getClientPlayer());
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#switchTurnOrder()}.
	 */
	@Test
	public void testSwitchTurnOrder() {
		gl.setOfflineFlags();
		Assert.assertEquals(PLAYER_BLACK, gl.getCurrentPlayer());
		gl.switchTurnOrder();
		Assert.assertEquals(PLAYER_WHITE, gl.getCurrentPlayer());
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#doTurn(org.game.othello.ao.Coordinate)}.
	 */
	@Test
	public void testDo1Turn() {
		// Tests the vertical connection
		gl.setOfflineFlags();
		gl.doTurn(new Coordinate(4, 5));
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(3, 5));
		GameLogic expectedGl = new GameLogic();
		expectedGl.getGameBoard().get(3).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(3, 3)));
		expectedGl.getGameBoard().get(3).set(4, new Gamefield(PLAYER_WHITE, new Coordinate(3, 4)));
		expectedGl.getGameBoard().get(3).set(5, new Gamefield(PLAYER_WHITE, new Coordinate(3, 5)));
		expectedGl.getGameBoard().get(4).set(3, new Gamefield(PLAYER_BLACK, new Coordinate(4, 3)));
		expectedGl.getGameBoard().get(4).set(4, new Gamefield(PLAYER_BLACK, new Coordinate(4, 4)));
		expectedGl.getGameBoard().get(4).set(5, new Gamefield(PLAYER_BLACK, new Coordinate(4, 5)));
		Assert.assertEquals(expectedGl.getGameBoard(), gl.getGameBoard());
	}
	
	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#doTurn(org.game.othello.ao.Coordinate)}.
	 */
	@Test
	public void test2DoTurn() {
		// Tests the horizontal connecteion
		gl.setOfflineFlags();
		gl.doTurn(new Coordinate(5, 4));
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(5, 3));
		GameLogic expectedGl = new GameLogic();
		expectedGl.getGameBoard().get(3).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(3, 3)));
		expectedGl.getGameBoard().get(3).set(4, new Gamefield(PLAYER_BLACK, new Coordinate(3, 4)));
		expectedGl.getGameBoard().get(4).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(4, 3)));
		expectedGl.getGameBoard().get(4).set(4, new Gamefield(PLAYER_BLACK, new Coordinate(4, 4)));
		expectedGl.getGameBoard().get(5).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(5, 3)));
		expectedGl.getGameBoard().get(5).set(4, new Gamefield(PLAYER_BLACK, new Coordinate(5, 4)));
		Assert.assertEquals(expectedGl.getGameBoard(), gl.getGameBoard());
	}
	
	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#doTurn(org.game.othello.ao.Coordinate)}.
	 */
	@Test
	public void test3DoTurn() {
		// Tests this connection:
		// \
		//	\
		//	 \
		//	  \
		gl.setOfflineFlags();
		gl.doTurn(new Coordinate(4, 5));
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(5, 5));
		GameLogic expectedGl = new GameLogic();
		expectedGl.getGameBoard().get(3).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(3, 3)));
		expectedGl.getGameBoard().get(3).set(4, new Gamefield(PLAYER_BLACK, new Coordinate(3, 4)));
		expectedGl.getGameBoard().get(4).set(3, new Gamefield(PLAYER_BLACK, new Coordinate(4, 3)));
		expectedGl.getGameBoard().get(4).set(4, new Gamefield(PLAYER_WHITE, new Coordinate(4, 4)));
		expectedGl.getGameBoard().get(4).set(5, new Gamefield(PLAYER_BLACK, new Coordinate(4, 5)));
		expectedGl.getGameBoard().get(5).set(5, new Gamefield(PLAYER_WHITE, new Coordinate(5, 5)));
		Assert.assertEquals(expectedGl.getGameBoard(), gl.getGameBoard());
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(3, 2));
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(2, 2));
		expectedGl.getGameBoard().get(2).set(2, new Gamefield(PLAYER_WHITE, new Coordinate(2, 2)));
		expectedGl.getGameBoard().get(3).set(2, new Gamefield(PLAYER_BLACK, new Coordinate(3, 2)));
		Assert.assertEquals(expectedGl.getGameBoard(), gl.getGameBoard());
	}
	
	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#doTurn(org.game.othello.ao.Coordinate)}.
	 */
	@Test
	public void test4DoTurn() {
		// Tests this connection:
		// 		/
		//	   /
		//	  /
		//	 / 
		gl.setOfflineFlags();
		gl.doTurn(new Coordinate(3, 2));
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(4, 2));
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(5, 1));
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(2, 5));
		GameLogic expectedGl = new GameLogic();
		expectedGl.getGameBoard().get(2).set(5, new Gamefield(PLAYER_WHITE, new Coordinate(2, 5)));
		expectedGl.getGameBoard().get(3).set(2, new Gamefield(PLAYER_BLACK, new Coordinate(3, 2)));
		expectedGl.getGameBoard().get(3).set(3, new Gamefield(PLAYER_BLACK, new Coordinate(3, 3)));
		expectedGl.getGameBoard().get(3).set(4, new Gamefield(PLAYER_WHITE, new Coordinate(3, 4)));
		expectedGl.getGameBoard().get(4).set(2, new Gamefield(PLAYER_BLACK, new Coordinate(4, 2)));
		expectedGl.getGameBoard().get(4).set(3, new Gamefield(PLAYER_WHITE, new Coordinate(4, 3)));
		expectedGl.getGameBoard().get(4).set(4, new Gamefield(PLAYER_WHITE, new Coordinate(4, 4)));
		expectedGl.getGameBoard().get(5).set(1, new Gamefield(PLAYER_BLACK, new Coordinate(5, 1)));
		Assert.assertEquals(expectedGl.getGameBoard(), gl.getGameBoard());
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#getGameFieldCount(int)}.
	 */
	@Test
	public void testGetGameFieldCount() {
		Assert.assertEquals(2, gl.getGameFieldCount(PLAYER_BLACK));
		Assert.assertEquals(2, gl.getGameFieldCount(PLAYER_WHITE));
		gl.setOfflineFlags();
		gl.doTurn(new Coordinate(5, 4));
		Assert.assertEquals(1, gl.getGameFieldCount(PLAYER_WHITE));
		Assert.assertEquals(4, gl.getGameFieldCount(PLAYER_BLACK));
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(5, 3));
		Assert.assertEquals(3, gl.getGameFieldCount(PLAYER_WHITE));
		Assert.assertEquals(64, gl.getGameFieldCount(PLAYER_WHITE) + gl.getGameFieldCount(PLAYER_BLACK) + gl.getGameFieldCount(PLAYER_NOBODY));
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#getCurrentPlayerAsString()}.
	 */
	@Test
	public void testGetCurrentPlayerAsString() {
		Assert.assertEquals("Nobody", gl.getCurrentPlayerAsString());
		gl.setOfflineFlags();
		Assert.assertEquals("Black", gl.getCurrentPlayerAsString());
		gl.switchTurnOrder();
		Assert.assertEquals("White", gl.getCurrentPlayerAsString());
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#getClientPlayerAsString()}.
	 */
	@Test
	public void testGetClientPlayerAsString() {
		Assert.assertEquals("Nobody", gl.getClientPlayerAsString());
	}

	/**
	 * Test method for {@link org.game.othello.rules.GameLogic#getWinner()}.
	 */
	@Test
	public void testGetWinner() {
		Assert.assertEquals(PLAYER_NOBODY, gl.getWinner());
		gl.setOfflineFlags();
		gl.doTurn(new Coordinate(3, 2));
		Assert.assertEquals(PLAYER_BLACK, gl.getWinner());
		gl.switchTurnOrder();
		gl.doTurn(new Coordinate(4, 2));
		Assert.assertEquals(PLAYER_NOBODY, gl.getWinner());
		gl.doTurn(new Coordinate(5, 1));
		Assert.assertEquals(PLAYER_WHITE, gl.getWinner());
		
	}
}
