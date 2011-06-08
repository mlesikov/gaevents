package com.clouway.asynceventbus.gae;

import com.clouway.asynceventbus.spi.AsyncEventBus;
import com.clouway.asynctaskscheduler.gae.GaeAsyncTasksModule;
import com.clouway.asynctaskscheduler.spi.AsyncTaskScheduler;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class GaeAsyncTaskEventsModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new GaeAsyncTasksModule());
  }

  @Provides
  public AsyncEventBus getAsyncEventBus(AsyncTaskScheduler asyncTaskScheduler) {
    return new TaskQueueEventBus(asyncTaskScheduler);
  }

}
