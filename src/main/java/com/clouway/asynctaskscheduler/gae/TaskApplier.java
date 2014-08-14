package com.clouway.asynctaskscheduler.gae;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.inject.ImplementedBy;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@ImplementedBy(PushQueueTaskApplier.class)
public interface TaskApplier {

  void apply(TaskOptions task, String queueName, Boolean transactionless);
}
