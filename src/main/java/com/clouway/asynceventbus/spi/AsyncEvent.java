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
//  /**
//   * Type class used to register events with the {@link HandlerManager}.
//   * <p>
//   * Type is parameterized by the handler type in order to make the addHandler
//   * method type safe.
//   * </p>
//   *
//   * @param <H> handler type
//   */
//  public static class Type<H> {
//    private static int nextHashCode;
//    private final int index;
//
//    /**
//     * Constructor.
//     */
//    public Type() {
//      index = ++nextHashCode;
//    }
//
//    // We override hash code to make it as efficient as possible.
//    @Override
//    public final int hashCode() {
//      return index;
//    }
//
//    @Override
//    public String toString() {
//      return "async event type";
//    }
//  }
//
//
//  /**
//   * Constructor.
//   */
//  protected AsyncEvent() {
//  }
//
//  /**
//   * Returns the type used to register this event. Used by handler manager to
//   * dispatch events to the correct handlers.
//   *
//   * @return the type
//   */
//  public abstract Type<H> getAssociatedType();
}
