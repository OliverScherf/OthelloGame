/**
 * 
 */
package org.game.othello.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver Scherf
 */
public class DateUtilsTest {

	/**
	 * Test method for {@link org.game.othello.utils.DateUtils#getCurrentTime()}.
	 */
	@Test
	public void testGetCurrentTime() {
		for (int i = 10; i < 10; ++i) {
			Assert.assertEquals(DateUtils.getCurrentTime(), DateUtils.getCurrentTime()); 
		}
	}

}
