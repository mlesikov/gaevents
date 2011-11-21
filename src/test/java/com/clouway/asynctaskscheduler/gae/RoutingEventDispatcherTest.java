package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.common.ActionEvent;
import com.clouway.asynctaskscheduler.common.ActionEventHandler;
import com.clouway.asynctaskscheduler.common.IndexingListener;
import com.clouway.asynctaskscheduler.common.TestEventListener;
import com.clouway.asynctaskscheduler.spi.AsyncEvent;
import com.clouway.asynctaskscheduler.spi.AsyncEventHandler;
import com.clouway.asynctaskscheduler.spi.AsyncEventHandlerFactory;
import com.clouway.asynctaskscheduler.spi.AsyncEventListener;
import com.clouway.asynctaskscheduler.spi.AsyncEventListenersFactory;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RoutingEventDispatcherTest {
  @Inject
  private RoutingEventDispatcher dispatcher;

  @Inject
  private Gson gson;

  private ActionEventHandler handler = new ActionEventHandler();
  private AsyncEventHandlerFactory handlerFactory = new AsyncEventHandlerFactory() {
    @Override
    public AsyncEventHandler create(Class<? extends AsyncEventHandler> evenHandlerClass) {
      return handler;
    }
  };

  IndexingListener indexingListener = new IndexingListener();
  TestEventListener testEventListener = new TestEventListener();
  private AsyncEventListenersFactory listenersFactory = new AsyncEventListenersFactory() {
    @Override
    public List<AsyncEventListener> create(Class<? extends AsyncEvent> eventClass) {
      return Lists.newArrayList(indexingListener, testEventListener);
    }
  };

  private String eventClassAsString = ActionEvent.class.getName();
  private ActionEvent event = new ActionEvent("test message");
  private String eventAsJson;


  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(new BackgroundTasksModule() {

      @Override
      public EventListenerBindingsBuilder bindEventAdditionalEventListeners() {
        return EventListenerBindingsBuilder.binder().bind(ActionEvent.class, IndexingListener.class, TestEventListener.class);
      }


      @Override
      public AsyncEventHandlerFactory getAsyncEventHandlerFactory(Injector injector) {
        return handlerFactory;
      }

      @Override
      public AsyncEventListenersFactory getAsyncEventListenerFactory(Injector injector) {
        return listenersFactory;
      }
    });
    injector.injectMembers(this);

    eventAsJson = gson.toJson(event);

  }

  @Test
  public void shouldDispatchAsyncEvent() throws Exception {

    dispatcher.dispatchAsyncEvent(eventClassAsString, eventAsJson);

    assertEquals(event.getMessage(), handler.message);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncEventWhenEventClassNull() throws Exception {
    dispatcher.dispatchAsyncEvent(null, eventAsJson);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncEventWhenEventAsJsonIsNull() throws Exception {
    dispatcher.dispatchAsyncEvent(eventClassAsString, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncEventWhenEventClassDoesNotRepresentAsyncEvent() throws Exception {
    eventClassAsString = RoutingEventDispatcherTest.class.getName();
    dispatcher.dispatchAsyncEvent(eventClassAsString, eventAsJson);
  }

  @Test(expected = ClassNotFoundException.class)
  public void shouldNotDispatchAsyncEventWhenEventClassDoesNotRepresentAClass() throws Exception {
    eventClassAsString = "blabla";
    dispatcher.dispatchAsyncEvent(eventClassAsString, eventAsJson);
  }

  @Test
  public void shouldExecuteAllConfiguredListernerWhenEventWasReceived() throws Exception {

    dispatcher.dispatchAsyncEvent(eventClassAsString, eventAsJson);

    assertEquals(event.getMessage(), ((ActionEvent) indexingListener.event).getMessage());
    assertEquals(event.getMessage(), ((ActionEvent) testEventListener.event).getMessage());
  }

}
