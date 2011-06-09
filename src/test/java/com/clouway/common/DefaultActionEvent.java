package com.clouway.common;

import com.clouway.asynctaskscheduler.spi.AsyncEvent;
import com.clouway.asynctaskscheduler.gae.QueueName;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@QueueName(name = "customTaskQueue")
public class DefaultActionEvent implements AsyncEvent<DefaultActionEventHandler> {
  public static final String CUSTOM_TASK_QUEUE_NAME = "customTaskQueue";
  private final String message;

  public DefaultActionEvent(String message) {
    this.message = message;
  }

  @Override
  public Class<DefaultActionEventHandler> getAssociatedHandlerClass() {
    return DefaultActionEventHandler.class;
  }

  @Override
  public void dispatch(DefaultActionEventHandler handler) {
    handler.onDefaultAction(this);
  }
}
