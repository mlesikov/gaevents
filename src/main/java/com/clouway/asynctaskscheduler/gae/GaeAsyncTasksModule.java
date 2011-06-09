package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynceventbus.gae.GaeAsyncTaskEventsModule;
import com.clouway.asynctaskscheduler.spi.AsyncTaskScheduler;
import com.google.gson.Gson;
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
    install(new GaeAsyncTaskEventsModule());
  }

  @Provides
  public AsyncTaskScheduler getAsyncTaskScheduler(Gson gson) {
    return new TaskQueueAsyncTaskScheduler(gson);
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