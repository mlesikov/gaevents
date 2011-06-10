package com.clouway.asynctaskscheduler.spi;

/**
 * Async Event that is fired to dispatch it's handler.
 *
 * The implemetation must have Default constructor
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface AsyncEvent<H extends AsyncEventHandler> {

  /**
   * Returns the class used to instanciating the handler for this. Used by handler manager to
   * dispatch events to the correct handlers.
   *
   * @return the type
   */
  Class<H> getAssociatedHandlerClass();


  /**
   * Should only be called by {@link AsyncEventBus}. In other words, do not use
   * or call.
   *
   * @param handler handler
   */
  void dispatch(H handler);

}
