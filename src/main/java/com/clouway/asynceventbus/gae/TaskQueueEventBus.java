package com.clouway.asynceventbus.gae;

import com.clouway.asynceventbus.spi.AsyncEvent;
import com.clouway.asynceventbus.spi.AsyncEventBus;
import com.clouway.asynctaskscheduler.spi.AsyncTaskOptions;
import com.clouway.asynctaskscheduler.spi.AsyncTaskScheduler;
import com.google.inject.Inject;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class TaskQueueEventBus implements AsyncEventBus {

  private final AsyncTaskScheduler taskScheduler;

  @Inject
  public TaskQueueEventBus(AsyncTaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  @Override
  public void fireEvent(AsyncEvent<?> event) {
    taskScheduler.add(AsyncTaskOptions.event(event)).now();
  }
}