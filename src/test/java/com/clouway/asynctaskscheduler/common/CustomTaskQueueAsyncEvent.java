package com.clouway.asynctaskscheduler.common;

import com.clouway.asynctaskscheduler.spi.AsyncEvent;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class CustomTaskQueueAsyncEvent implements AsyncEvent<CustomTaskQueueAsyncEventHandler> {
    private String message;

    public CustomTaskQueueAsyncEvent() {
      // no-args constructor
    }

    public CustomTaskQueueAsyncEvent(String message) {
      this.message = message;
    }

    @Override
    public Class<CustomTaskQueueAsyncEventHandler> getAssociatedHandlerClass() {
      return CustomTaskQueueAsyncEventHandler.class;
    }

    @Override
    public void dispatch(CustomTaskQueueAsyncEventHandler handler) {
      handler.onAction(this);
    }

    public String getMessage() {
      return message;
    }
}
