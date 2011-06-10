package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncEvent;
import com.clouway.asynctaskscheduler.spi.AsyncEventBus;
import com.clouway.asynctaskscheduler.spi.AsyncTaskOptions;
import com.clouway.asynctaskscheduler.spi.AsyncTaskScheduler;
import com.google.inject.Inject;

import java.util.logging.Logger;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class TaskQueueEventBus implements AsyncEventBus {
  private final Logger log = Logger.getLogger(TaskQueueEventBus.class.getName());

  private final AsyncTaskScheduler taskScheduler;

  @Inject
  public TaskQueueEventBus(AsyncTaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  @Override
  public void fireEvent(AsyncEvent<?> event) {
    log.info("fired async event : " + event.getClass().getSimpleName());
    taskScheduler.add(AsyncTaskOptions.event(event)).now();
  }
}