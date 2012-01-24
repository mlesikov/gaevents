package com.clouway.asynctaskscheduler.spi;

import com.clouway.asynctaskscheduler.util.SampleTestDateFormat;
import org.junit.Test;

import java.text.ParseException;

import static com.clouway.asynctaskscheduler.util.DateUtil.newDateAndTime;
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
    assertThat("different date value was returned?",AsyncTaskParams.with("date", "10/11/2011").format("date", SampleTestDateFormat.class), is(equalTo(newDateAndTime(2011,11,10,0,0,0,0))));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToFormat() {
    assertThat(AsyncTaskParams.with("test","test").format("date", SampleTestDateFormat.class), is(nullValue()));
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

}
