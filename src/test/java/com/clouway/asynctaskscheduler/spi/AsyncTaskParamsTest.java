package com.clouway.asynctaskscheduler.spi;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class AsyncTaskParamsTest {

  @Test
  public void getValueAsLong() {
    assertThat("different long value was returned?",AsyncTaskParams.with("index", "1").getLong("index"), is(equalTo(1l)));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetLong() {
    assertThat(AsyncTaskParams.with("test","test").getLong("anotherkey"), is(nullValue()));
  }

  @Test
  public void getValueAsInteger() {
    assertThat("different integer value was returned?",AsyncTaskParams.with("index", "1").getInteger("index"), is(equalTo(1)));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetInteger() {
    assertThat(AsyncTaskParams.with("test","test").getInteger("anotherkey"), is(nullValue()));
  }

  @Test(expected = NumberFormatException.class)
  public void throwsExceptionWhenTryingToGetIntegerWithInvalidData() {
    AsyncTaskParams.with("test","test").getInteger("test");
  }

  @Test
  public void getValueAsDouble() {
    assertThat("different double value was returned?",AsyncTaskParams.with("index", "1").getDouble("index"), is(equalTo(1d)));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetDouble() {
    assertThat(AsyncTaskParams.with("test","test").getDouble("anotherkey"), is(nullValue()));
  }

  @Test
  public void formatAsDate() {
    assertThat("different date value was returned?",AsyncTaskParams.with("date", "10/11/2011").format("date", SampleDateFormat.class), is(equalTo(newDateAndTime(2011,11,10,0,0,0,0))));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToFormat() {
    assertThat(AsyncTaskParams.with("test","test").format("date", SampleDateFormat.class), is(nullValue()));
  }

  @Test
  public void getValueAsDate() throws ParseException {
    assertThat("different date value was returned?",AsyncTaskParams.with("date", "12-01-2012").getDate("date"), is(equalTo(newDateAndTime(2012,1,12,0,0,0,0))));
    assertThat("different date value was returned?",AsyncTaskParams.with("date", "12-01-2012 asd").getDate("date"), is(equalTo(newDateAndTime(2012,1,12,0,0,0,0))));
  }

  @Test
  public void throwsExceptionWhenInvalidDataIsFetchForTheParamOngetDate() {
    try {
      AsyncTaskParams.with("date", "12012012").getDate("date");
      fail("hm, Parse Exception must be thrown");
    } catch (ParseException e) {

    }
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetDate() throws ParseException {
    assertThat(AsyncTaskParams.with("test","test").getDate("anotherkey"), is(nullValue()));
  }

  @Test
  public void getValueAsDateAndTime() throws ParseException {
    assertThat("different date value was returned?",AsyncTaskParams.with("date", "12-01-2012 12:33").getDateAndTime("date"), is(equalTo(newDateAndTime(2012,1,12,12,33,0,0))));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetDateAndTime() throws ParseException {
    assertThat(AsyncTaskParams.with("test","test").getDateAndTime("anotherkey"), is(nullValue()));
  }

  @Test
  public void throwsExceptionWhenInvalidDataIsFetchForTheParamOnGeDateAndTime() {
    try {
      AsyncTaskParams.with("date", "12012012").getDateAndTime("date");
      fail("hm, Parse Exception must be thrown");
    } catch (ParseException e) {

    }
  }

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
