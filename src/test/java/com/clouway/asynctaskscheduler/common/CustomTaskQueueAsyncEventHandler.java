package com.clouway.asynctaskscheduler.common;

import com.clouway.asynctaskscheduler.gae.QueueName;
import com.clouway.asynctaskscheduler.spi.AsyncEventHandler;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@QueueName(name = "customActionEventTaskQueue")
public class CustomTaskQueueAsyncEventHandler implements AsyncEventHandler {
  public void onAction(CustomTaskQueueAsyncEvent event) {
    //To change body of created methods use File | Settings | File Templates.
  }
}
