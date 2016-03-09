package org.game.othello.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Oliver Scherf
 */
public class DateUtils {

	/**
	 * @return the current time as string.
	 */
	public static String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		StringBuilder time = new StringBuilder();
		time.append(DateUtils.getCalendarfield(c, Calendar.HOUR_OF_DAY));
		time.append(':');
		time.append(DateUtils.getCalendarfield(c, Calendar.MINUTE));
		time.append(':');
		time.append(DateUtils.getCalendarfield(c, Calendar.SECOND));
		return time.toString();
	}

	private static String getCalendarfield(Calendar c, int field) {
		String calendarField;
		if (c.get(field) < 10) {
			calendarField = "0" + c.get(field);
		} else {
			calendarField = String.valueOf(c.get(field));
		}
		return calendarField;
	}

}
