package com.clouway.asynceventbus.spi;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface AsyncEvent<H extends AsyncEventHandler> {


    /**
   * Should only be called by {@link AsyncEventBus}. In other words, do not use
   * or call.
   *
   * @param handler handler
   */
  void dispatch(H handler);

}
