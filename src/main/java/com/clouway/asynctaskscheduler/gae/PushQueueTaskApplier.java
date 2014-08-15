package com.clouway.asynctaskscheduler.gae;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Strings;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class PushQueueTaskApplier implements TaskApplier {

  @Override
  public void apply(TaskOptions task, String queueName, Boolean transactionless) {

    Queue queue = getQueue(queueName);

    if (transactionless) {
      queue.add(null, task);
    } else {
      queue.add(task);
    }
  }

  /**
   * Gets the Task Queue from the given name or the Default Task Queue
   *
   * @param queueName
   * @return
   */
  private Queue getQueue(String queueName) {
    Queue queue;

    if (!Strings.isNullOrEmpty(queueName)) {

      queue = QueueFactory.getQueue(queueName);

    } else {
      queue = QueueFactory.getDefaultQueue();
    }

    return queue;
  }


}
