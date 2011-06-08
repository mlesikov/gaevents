package com.clouway.asynctaskscheduler.spi;

/**
 * Represents a service that adds adds jobs that will be run asynchronously
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface AsyncTaskScheduler {

  /**
   * Adds new {@link AsyncTaskOptions} object for specifying the options
   * for the asynchronous task that will be prepared for execution
   *
   * @param asyncTaskOptions
   * @return
   */
  public AsyncTaskScheduler add(AsyncTaskOptions asyncTaskOptions);

  /**
   * Adds new {@link AsyncTaskOptions} objects for specifying the options
   * for the asynchronous tasks that will be prepared for execution
   *
   * @param asyncTaskOptions
   * @return
   */
  public AsyncTaskScheduler add(AsyncTaskOptions... asyncTaskOptions);

  /**
   * builds the asynchronous tasks form the given {@link AsyncTaskOptions} object form the task options
   */
  public void now();

}
