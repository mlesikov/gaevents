package com.clouway.asynctaskscheduler.common;

import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskParams;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class DefaultTaskQueueAsyncTask implements AsyncTask {
  public static AsyncTaskParams params;


  @Override
  public void execute(AsyncTaskParams params) {
    this.params = params;
  }
}
