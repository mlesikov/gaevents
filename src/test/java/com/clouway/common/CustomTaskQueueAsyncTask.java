package com.clouway.common;

import com.clouway.asynctaskscheduler.gae.QueueName;
import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskParams;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */

@QueueName(name = "customTaskQueue")
public class CustomTaskQueueAsyncTask implements AsyncTask {
  public static final String CUSTOM_TASK_QUEUE_NAME = "customTaskQueue";

  @Override
  public void execute(AsyncTaskParams params) {
  }
}
