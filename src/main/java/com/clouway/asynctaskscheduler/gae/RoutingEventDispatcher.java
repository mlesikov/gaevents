package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncEvent;
import com.clouway.asynctaskscheduler.spi.AsyncEventHandler;
import com.clouway.asynctaskscheduler.spi.AsyncEventHandlerFactory;
import com.clouway.asynctaskscheduler.spi.AsyncEventListener;
import com.clouway.asynctaskscheduler.spi.AsyncEventListenersFactory;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.Arrays;
import java.util.List;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RoutingEventDispatcher {

  private final Gson gson;
  private final AsyncEventHandlerFactory handlerFactory;
  private final AsyncEventListenersFactory listenersFactory;

  @Inject
  public RoutingEventDispatcher(Gson gson,
                                AsyncEventHandlerFactory handlerFactory,
                                AsyncEventListenersFactory listenersFactory) {
    this.gson = gson;
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

    AsyncEvent<AsyncEventHandler> event = (AsyncEvent) gson.fromJson(eventAsJson, eventClass);

    Class<? extends AsyncEventHandler> evenHandlerClass = event.getAssociatedHandlerClass();

    //1.
    dispatchHandler(event, evenHandlerClass);
    //2.
    dispatchListeners(event);
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
