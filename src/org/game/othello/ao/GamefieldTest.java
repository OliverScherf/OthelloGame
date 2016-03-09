/**
 * 
 */
package org.game.othello.ao;

import org.game.othello.interfaces.OthelloConstants;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver
 *
 */
public class GamefieldTest implements OthelloConstants {

	/**
	 * Test method for {@link org.game.othello.ao.Gamefield#Gamefield(int, org.game.othello.ao.Coordinate)}.
	 */
	@Test
	public void testGamefield() {
		Gamefield g = new Gamefield(PLAYER_BLACK , new Coordinate(2, 2));
		Assert.assertNotNull(g);
		Assert.assertEquals(PLAYER_BLACK, g.getOwner());
		Assert.assertEquals(new Coordinate(2, 2), g.getIndex());
	}

	/**
	 * Test method for {@link org.game.othello.ao.Gamefield#isEmpty()}.
	 */
	@Test
	public void testIsEmpty() {
		Gamefield g1 = new Gamefield(PLAYER_BLACK, new Coordinate(2, 2));
		Gamefield g2 = new Gamefield(PLAYER_NOBODY, new Coordinate(2, 2));
		Gamefield g3 = new Gamefield(PLAYER_NOBODY, new Coordinate(2, 2));
		Gamefield g4 = new Gamefield(PLAYER_BLACK, new Coordinate(2, 3));
		Assert.assertEquals(false, g1.isEmpty());
		Assert.assertEquals(true, g2.isEmpty());
		Assert.assertEquals(true, g3.isEmpty());
		Assert.assertEquals(false, g4.isEmpty());
	}

	/**
	 * Test method for {@link org.game.othello.ao.Gamefield#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Gamefield g1 = new Gamefield(PLAYER_BLACK, new Coordinate(2, 2));
		Gamefield g2 = new Gamefield(PLAYER_BLACK, new Coordinate(2, 2));
		Gamefield g3 = new Gamefield(PLAYER_WHITE, new Coordinate(2, 2));
		Gamefield g4 = new Gamefield(PLAYER_BLACK, new Coordinate(2, 3));
		Assert.assertEquals(true, g1.equals(g2));
		Assert.assertEquals(true, g2.equals(g1));
		Assert.assertEquals(false, g1.equals(g3));
		Assert.assertEquals(false, g2.equals(g3));
		Assert.assertEquals(false, g1.equals(g4));
		Assert.assertEquals(false, g2.equals(g4));
	}
}
