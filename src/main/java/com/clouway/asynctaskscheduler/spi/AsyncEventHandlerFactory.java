package com.clouway.asynctaskscheduler.spi;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface AsyncEventHandlerFactory {
  AsyncEventHandler create(Class<? extends AsyncEventHandler> evenHandlerClass);
}
