package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskParams;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */

@QueueName(name = "test")
public class CustomTaskQueueAsyncTask implements AsyncTask {
  public static final String CUSTOM_TASK_QUEUE_NAME = "custom_task_queue";

  @Override
  public void execute(AsyncTaskParams params) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
