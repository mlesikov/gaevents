package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.common.ActionEvent;
import com.clouway.asynctaskscheduler.spi.AsyncEvent;
import com.clouway.asynctaskscheduler.spi.EventTransport;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.Before;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GsonEventSerializationTest extends AsyncEventSerializationContractTest {

  @Inject
  public EventTransport eventTransport;

  private AsyncEvent asyncEvent;

  @Before
  public void init() {

    Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
        bind(EventTransport.class).to(GsonEventTransport.class);
      }
    }).injectMembers(this);

    super.init();
  }

  @Override
  public EventTransport getEventTransport() {
    return eventTransport;
  }

  @Override
  public AsyncEvent getAsyncEvent() {
    return new ActionEvent("Some Action Event");
  }
}
