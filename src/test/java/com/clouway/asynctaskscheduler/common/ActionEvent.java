package com.clouway.asynctaskscheduler.common;

import com.clouway.asynctaskscheduler.spi.AsyncEvent;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class ActionEvent implements AsyncEvent<ActionEventHandler> {
    private String message;

    public ActionEvent() {
      // no-args constructor
    }

    public ActionEvent(String message) {
      this.message = message;
    }

    @Override
    public Class<ActionEventHandler> getAssociatedHandlerClass() {
      return ActionEventHandler.class;
    }

    @Override
    public void dispatch(ActionEventHandler handler) {
      handler.onAction(this);
    }

    public String getMessage() {
      return message;
    }
  }
