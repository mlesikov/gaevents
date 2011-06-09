package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynceventbus.spi.AsyncEvent;
import com.clouway.asynceventbus.spi.AsyncEventHandler;
import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskParams;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class FooDispatcher {

  private final Injector injector;
  private final Gson gson;

  @Inject
  public FooDispatcher(Injector injector, Gson gson) {
    this.injector = injector;
    this.gson = gson;
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

    AsyncEventHandler handler = injector.getInstance(evenHandlerClass);

    event.dispatch(handler);
  }

  /**
   * @param params
   * @param asyncTaskClassString
   * @throws ClassNotFoundException
   */
  public void dispatchAsyncTask(Map<String, String[]> params, String asyncTaskClassString) throws ClassNotFoundException {

    if (Strings.isNullOrEmpty(asyncTaskClassString)) {
      throw new IllegalArgumentException("Async task class as string cannot be null!");
    }

    Class<?> asyncTaskClass = Class.forName(asyncTaskClassString);

    Object object = injector.getInstance(asyncTaskClass);

    if (object instanceof AsyncTask) {

      AsyncTask task = (AsyncTask) object;

      task.execute(new AsyncTaskParams(params));

    } else {

      throw new IllegalArgumentException("given class : " + object.getClass().getName() + "does NOT implements AsyncTask interface!");

    }
  }
}
