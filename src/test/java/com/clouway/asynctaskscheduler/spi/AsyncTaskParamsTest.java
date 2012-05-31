package com.clouway.asynctaskscheduler.spi;

import com.clouway.asynctaskscheduler.util.SampleTestDateFormat;
import org.junit.Test;

import java.text.ParseException;

import static com.clouway.asynctaskscheduler.spi.AsyncTaskParams.aNewParams;
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
    assertThat("different long value was returned?", aNewParams().addString("index", "1").build().getLong("index"), is(equalTo(1l)));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetLong() {
    assertThat(aNewParams().addString("test", "test").build().getLong("anotherkey"), is(nullValue()));
  }

  @Test
  public void getValueAsInteger() {
    assertThat("different integer value was returned?", aNewParams().addString("index", "1").build().getInteger("index"), is(equalTo(1)));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetInteger() {
    assertThat(aNewParams().addString("test", "test").build().getInteger("anotherkey"), is(nullValue()));
  }

  @Test(expected = NumberFormatException.class)
  public void throwsExceptionWhenTryingToGetIntegerWithInvalidData() {
    aNewParams().addString("test", "test").build().getInteger("test");
  }

  @Test
  public void getValueAsDouble() {
    assertThat("different double value was returned?", aNewParams().addString("index", "1").build().getDouble("index"), is(equalTo(1d)));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetDouble() {
    assertThat(aNewParams().addString("test", "test").build().getDouble("anotherkey"), is(nullValue()));
  }

  @Test
  public void formatAsDate() {
    assertThat("different date value was returned?", aNewParams().addString("date", "10/11/2011").build().format("date", SampleTestDateFormat.class), is(equalTo(newDateAndTime(2011, 11, 10, 0, 0, 0, 0))));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToFormat() {
    assertThat(aNewParams().addString("test", "test").build().format("date", SampleTestDateFormat.class), is(nullValue()));
  }

  @Test
  public void getValueAsDate() throws ParseException {
    assertThat("different date value was returned?", aNewParams().addString("date", "12-01-2012").build().getDate("date"), is(equalTo(newDateAndTime(2012, 1, 12, 0, 0, 0, 0))));
    assertThat("different date value was returned?", aNewParams().addString("date", "12-01-2012 asd").build().getDate("date"), is(equalTo(newDateAndTime(2012, 1, 12, 0, 0, 0, 0))));
  }

  @Test
  public void throwsExceptionWhenInvalidDataIsFetchForTheParamOngetDate() {
    try {
      aNewParams().addString("date", "12012012").build().getDate("date");
      fail("hm, Parse Exception must be thrown");
    } catch (ParseException e) {

    }
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetDate() throws ParseException {
    assertThat(aNewParams().addString("test", "test").build().getDate("anotherkey"), is(nullValue()));
  }

  @Test
  public void getValueAsDateAndTime() throws ParseException {
    assertThat("different date value was returned?", aNewParams().addString("date", "12-01-2012 12:33").build().getDateAndTime("date"), is(equalTo(newDateAndTime(2012, 1, 12, 12, 33, 0, 0))));
  }

  @Test
  public void unknownValuesAreReturnedAsNullWhenTryingToGetDateAndTime() throws ParseException {
    assertThat(aNewParams().addString("test", "test").build().getDateAndTime("anotherkey"), is(nullValue()));
  }

  @Test
  public void throwsExceptionWhenInvalidDataIsFetchForTheParamOnGeDateAndTime() {
    try {
      aNewParams().addString("date", "12012012").build().getDateAndTime("date");
      fail("hm, Parse Exception must be thrown");
    } catch (ParseException e) {

    }
  }

}
