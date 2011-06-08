package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncTaskScheduler;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class GaeAsyncTasksModule extends AbstractModule {

  final Module servlets = new ServletModule() {
    @Override
    protected void configureServlets() {
      serve(TaskQueueAsyncTaskExecutorServlet.URL).with(TaskQueueAsyncTaskExecutorServlet.class);
      bind(TaskQueueAsyncTaskExecutorServlet.class).in(Singleton.class);
    }
  };

  @Override
  protected void configure() {
    install(servlets);
  }

  @Provides
  public AsyncTaskScheduler getAsyncTaskScheduler() {
    return new TaskQueueAsyncTaskScheduler();
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof GaeAsyncTasksModule;
  }

  @Override
  public int hashCode() {
    return GaeAsyncTasksModule.class.hashCode();
  }

}