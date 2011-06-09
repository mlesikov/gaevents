package com.clouway.asynctaskscheduler.spi;

import com.clouway.asynceventbus.spi.AsyncEvent;
import com.google.common.collect.Maps;

import java.util.Date;
import java.util.Map;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class AsyncTaskOptions {
  private Class<? extends AsyncTask> asyncTask;
  private Map<String, String> params;
  private long delayMills = 0;
  private long executionDateMills = 0;
  private AsyncEvent event;


  private AsyncTaskOptions() {

  }


  public AsyncTaskOptions param(String name, String value) {
    if (event != null) {
      throw new IllegalArgumentException("parameters cannot be add to a " + this.getClass().getName() + " when event is provided!");
    }
    params.put(name, value);
    return this;
  }

  public static AsyncTaskOptions task(Class<? extends AsyncTask> asyncTaskClass) {
    AsyncTaskOptions taskOptions = new AsyncTaskOptions();
    taskOptions.asyncTask = asyncTaskClass;
    taskOptions.params = Maps.newHashMap();
    return taskOptions;

  }

  public static AsyncTaskOptions event(AsyncEvent event) {
    AsyncTaskOptions taskOptions = new AsyncTaskOptions();
    taskOptions.event = event;
    taskOptions.params = Maps.newHashMap();
    return taskOptions;
  }


  public AsyncTaskOptions delay(long delayMills) {
    this.delayMills = delayMills;
    executionDateMills = 0;
    return this;
  }

  public AsyncTaskOptions executionDate(Date executionDate) {
    delayMills = 0;
    this.executionDateMills = executionDate.getTime();
    return this;
  }

  public Class<? extends AsyncTask> getAsyncTask() {
    return asyncTask;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public long getDelayMills() {
    return delayMills;
  }

  public long getExecutionDateMills() {
    return executionDateMills;
  }

  public String getAsyncTaskAsString() {
    return asyncTask.getName();
  }

  public AsyncEvent getEvent() {
    return event;
  }

  public boolean isEventTaskOption() {
    if (event != null) {
      return true;
    }
    return false;
  }
}
