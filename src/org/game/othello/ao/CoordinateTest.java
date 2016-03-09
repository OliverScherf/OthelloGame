/**
 * 
 */
package org.game.othello.ao;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver
 *
 */
public class CoordinateTest {

	/**
	 * Test method for {@link org.game.othello.ao.Coordinate#Coordinate(int, int)}.
	 */
	@Test
	public void testCoordinate() {
		Coordinate coordinate = new Coordinate(0, 1);
		Assert.assertNotNull(coordinate);
		Assert.assertEquals(0, coordinate.getX());
		Assert.assertEquals(1, coordinate.getY());
	}

	/**
	 * Test method for {@link org.game.othello.ao.Coordinate#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Coordinate c1 = new Coordinate(1, 1);
		Coordinate c2 = new Coordinate(1, 1);
		Coordinate c3 = new Coordinate(0, 0);
		Assert.assertEquals(true, c1.equals(c2));
		Assert.assertEquals(true, c2.equals(c1));
		Assert.assertEquals(false, c1.equals(c3));
		Assert.assertEquals(false, c2.equals(c3));
	}

	/**
	 * Test method for {@link org.game.othello.ao.Coordinate#toString()}.
	 */
	@Test
	public void testToString() {
		Assert.assertEquals("X: 1 Y: 1", new Coordinate(1, 1).toString());
		Assert.assertEquals("X: 33 Y: 1", new Coordinate(33, 1).toString());
		Assert.assertEquals("X: 24 Y: 1", new Coordinate(24, 1).toString());
		Assert.assertEquals("X: 11 Y: 33", new Coordinate(11, 33).toString());
	}

	/**
	 * Test method for {@link org.game.othello.ao.Coordinate#toSimpleString()}.
	 */
	@Test
	public void testToSimpleString() {
		Assert.assertEquals("[0][0]", new Coordinate(0, 0).toSimpleString());
		Assert.assertEquals("[0][30]", new Coordinate(0, 30).toSimpleString());
		Assert.assertEquals("[22][6]", new Coordinate(22, 6).toSimpleString());
		Assert.assertEquals("[12][99]", new Coordinate(12, 99).toSimpleString());
	}
}
