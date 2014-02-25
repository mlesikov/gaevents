package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncEvent;
import com.clouway.asynctaskscheduler.spi.EventTransport;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public abstract class AsyncEventSerializationContractTest {

  private EventTransport eventTransport;

  private AsyncEvent expectedEvent;

  @Before
  public void init() {

    eventTransport = getEventTransport();

    expectedEvent = getAsyncEvent();
  }

  @Test
  public void serializeAsyncEvent() throws NoSuchFieldException {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    eventTransport.out(expectedEvent.getClass(), expectedEvent, outputStream);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    eventTransport.in(this.expectedEvent.getClass(), inputStream);
  }

  public abstract EventTransport getEventTransport();

  public abstract AsyncEvent getAsyncEvent();
}
