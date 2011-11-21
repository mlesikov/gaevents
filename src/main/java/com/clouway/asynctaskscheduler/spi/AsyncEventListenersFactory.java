package com.clouway.asynctaskscheduler.spi;

import java.util.List;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface AsyncEventListenersFactory {
  public List<AsyncEventListener> create(Class<? extends AsyncEvent> eventClass);
}
