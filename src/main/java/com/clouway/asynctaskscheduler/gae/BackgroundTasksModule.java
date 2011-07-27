package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncEventBus;
import com.clouway.asynctaskscheduler.spi.AsyncTaskScheduler;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;


/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class BackgroundTasksModule extends AbstractModule {

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
  public AsyncEventBus getAsyncEventBus(Provider<AsyncTaskScheduler> asyncTaskScheduler) {
    return new TaskQueueEventBus(asyncTaskScheduler);
  }

  @Provides
  public AsyncTaskScheduler getAsyncTaskScheduler(Gson gson) {
    return new TaskQueueAsyncTaskScheduler(gson);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof BackgroundTasksModule;
  }

  @Override
  public int hashCode() {
    return BackgroundTasksModule.class.hashCode();
  }

}