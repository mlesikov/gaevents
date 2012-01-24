package com.clouway.asynctaskscheduler.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class DateUtil {

  public static Date newDateAndTime(int year, int month, int day, int hour, int minute, int second, int millisecond) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, millisecond);
    return calendar.getTime();
  }
}
