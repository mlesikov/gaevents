package com.clouway.asynctaskscheduler.spi;

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


  public AsyncTaskOptions(Class<? extends AsyncTask> asyncTask) {
    this.asyncTask = asyncTask;
    this.params = Maps.newHashMap();
  }

  public AsyncTaskOptions param(String name, String value) {
    params.put(name, value);
    return this;
  }

  public static AsyncTaskOptions task(Class<? extends AsyncTask> asyncTaskClass) {

    return new AsyncTaskOptions(asyncTaskClass);

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
}
