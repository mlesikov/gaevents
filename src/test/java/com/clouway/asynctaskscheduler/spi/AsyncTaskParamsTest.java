package com.clouway.asynctaskscheduler.spi;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class AsyncTaskParamsTest {

  @Test
  public void getValueAsLong() {
    assertThat("different long value was returned?",AsyncTaskParams.with("index", "1").getLong("index"), is(equalTo(1l)));
  }

  @Test
  public void unknownValuesAreReturnedAsNull() {
    assertThat(AsyncTaskParams.with("test","test").getLong("anotherkey"), is(nullValue()));
  }
}
