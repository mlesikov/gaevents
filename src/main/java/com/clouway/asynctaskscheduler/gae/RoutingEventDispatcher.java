package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncEvent;
import com.clouway.asynctaskscheduler.spi.AsyncEventHandler;
import com.clouway.asynctaskscheduler.spi.AsyncEventHandlerFactory;
import com.clouway.asynctaskscheduler.spi.AsyncEventListener;
import com.clouway.asynctaskscheduler.spi.AsyncEventListenersFactory;
import com.clouway.asynctaskscheduler.spi.EventTransport;
import com.google.common.base.Strings;
import com.google.inject.Inject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RoutingEventDispatcher {


  private final EventTransport eventTransport;
  private final AsyncEventHandlerFactory handlerFactory;
  private final AsyncEventListenersFactory listenersFactory;

  @Inject
  public RoutingEventDispatcher(EventTransport eventTransport,
                                AsyncEventHandlerFactory handlerFactory,
                                AsyncEventListenersFactory listenersFactory) {
    this.eventTransport = eventTransport;
    this.handlerFactory = handlerFactory;
    this.listenersFactory = listenersFactory;
  }

  /**
   * @param eventClassAsString
   * @param eventAsJson
   * @throws ClassNotFoundException
   */
  public void dispatchAsyncEvent(String eventClassAsString, String eventAsJson) throws ClassNotFoundException {

    if (Strings.isNullOrEmpty(eventClassAsString) || Strings.isNullOrEmpty(eventAsJson)) {
      throw new IllegalArgumentException("No AsyncEvent class as string or evnt as json provided.");
    }

    Class<?> eventClass = Class.forName(eventClassAsString);

    if (!Arrays.asList(eventClass.getInterfaces()).contains(AsyncEvent.class)) {
      throw new IllegalArgumentException("No AsyncEvent class provided.");
    }

    AsyncEvent<AsyncEventHandler> event = getAsyncEvent(eventAsJson, eventClass);

    Class<? extends AsyncEventHandler> evenHandlerClass = event.getAssociatedHandlerClass();

    //1.
    dispatchHandler(event, evenHandlerClass);
    //2.
    dispatchListeners(event);
  }

  private AsyncEvent<AsyncEventHandler> getAsyncEvent(String eventAsJson, Class<?> eventClass) {

    ByteArrayInputStream inputStream = new ByteArrayInputStream(eventAsJson.getBytes());

    AsyncEvent<AsyncEventHandler> event = (AsyncEvent) eventTransport.in(eventClass, inputStream);

    try {
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return event;
  }

  private void dispatchListeners(AsyncEvent<AsyncEventHandler> event) {
    List<? extends AsyncEventListener> listeners  = listenersFactory.create(event.getClass());
    for (AsyncEventListener listener : listeners) {
      listener.onEvent(event);
    }
  }

  /**
   * Dsipatches the event to it's handler
   * @param event
   * @param evenHandlerClass
   */
  private void dispatchHandler(AsyncEvent<AsyncEventHandler> event, Class<? extends AsyncEventHandler> evenHandlerClass) {
    AsyncEventHandler handler = handlerFactory.create(evenHandlerClass);

    event.dispatch(handler);
  }




}
