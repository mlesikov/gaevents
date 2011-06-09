package com.clouway.asynceventbus.gae;

import com.clouway.asynceventbus.spi.AsyncEvent;
import com.clouway.asynceventbus.spi.EventHandler;
import com.clouway.asynctaskscheduler.gae.QueueName;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@QueueName(name = "test queue name")
@EventHandler(type = DefaultActionEventHandler.class)
public class DefaultActionEvent implements AsyncEvent<DefaultActionEventHandler> {
  @Override
  public void dispatch(DefaultActionEventHandler handler) {
    handler.onDefaultAction(this);
  }
}
