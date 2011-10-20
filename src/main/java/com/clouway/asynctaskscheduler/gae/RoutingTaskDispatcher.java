package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskParams;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.Map;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RoutingTaskDispatcher {

  private final Injector injector;

  @Inject
  public RoutingTaskDispatcher(Injector injector) {
    this.injector = injector;
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
