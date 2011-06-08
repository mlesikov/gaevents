package com.clouway.asynceventbus.gae;

import com.clouway.asynceventbus.spi.AsyncEvent;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class DefaultActionEvent implements AsyncEvent<DefaultActionEventHandler> {
  @Override
  public void dispatch(DefaultActionEventHandler handler) {
    handler.onDefaultAction();
  }
}
