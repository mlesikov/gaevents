package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class BackgroundTasksModule extends AbstractModule {

  final Module servletsModule = new ServletModule() {
    @Override
    protected void configureServlets() {
      serve(TaskQueueAsyncTaskExecutorServlet.URL).with(TaskQueueAsyncTaskExecutorServlet.class);
      bind(TaskQueueAsyncTaskExecutorServlet.class).in(Singleton.class);
    }
  };

  /**
   * Configures which listeners to be executed after handling of a given event
   * Should be override
   * and implement like this :
   * EventListenerBindingsBuilder.binder().bind(AsyncEvent.class,Lists.newArrayList(AsyncEventListenr.class,...))
   *
   * @return
   */
  public EventListenerBindingsBuilder bindEventAdditionalEventListeners() {
    return EventListenerBindingsBuilder.binder();
  }

  public static class EventListenerBindingsBuilder {
    Map<Class<? extends AsyncEvent>, List<Class<? extends AsyncEventListener>>> map = Maps.newHashMap();

    public EventListenerBindingsBuilder bind(Class<? extends AsyncEvent> eventClass, Class<? extends AsyncEventListener>... listenerClasses) {
      map.put(eventClass, Lists.newArrayList(listenerClasses));
      return this;
    }


    public static EventListenerBindingsBuilder binder() {
      return new EventListenerBindingsBuilder();
    }

    private List<Class<? extends AsyncEventListener>> get(Class<? extends AsyncEvent> eventClass) {
      return map.get(eventClass);
    }
  }

  @Override
  protected void configure() {
    install(servletsModule);
    bind(EventTransport.class).to(getEventTransport()).in(Singleton.class);
  }

  protected Class<? extends EventTransport> getEventTransport() {
    return GsonEventTransport.class;
  }

  @Provides
  public AsyncEventBus getAsyncEventBus(Provider<AsyncTaskScheduler> asyncTaskScheduler) {
    return new TaskQueueEventBus(asyncTaskScheduler);
  }

  @Provides
  public AsyncTaskScheduler getAsyncTaskScheduler(EventTransport eventTransport, Provider<CommonParamBinder> commonParamBinderProvider) {
    return new TaskQueueAsyncTaskScheduler(eventTransport, commonParamBinderProvider.get());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof BackgroundTasksModule;
  }

  @Override
  public int hashCode() {
    return BackgroundTasksModule.class.hashCode();
  }

  @Provides
  public AsyncEventHandlerFactory getAsyncEventHandlerFactory(final Injector injector) {
    return new AsyncEventHandlerFactory() {
      @Override
      public AsyncEventHandler create(Class<? extends AsyncEventHandler> evenHandlerClass) {
        return injector.getInstance(evenHandlerClass);
      }
    };
  }

  @Provides
  public AsyncEventListenersFactory getAsyncEventListenerFactory(final Injector injector) {
    return new AsyncEventListenersFactory() {
      @Override
      public List<AsyncEventListener> create(Class<? extends AsyncEvent> eventClass) {

        ArrayList<AsyncEventListener> listeners = Lists.newArrayList();

        List<Class<? extends AsyncEventListener>> listenerClassList = bindEventAdditionalEventListeners().get(eventClass);
        if (listenerClassList != null) {
          for (Class<? extends AsyncEventListener> listenerClass : listenerClassList) {
            AsyncEventListener listener = injector.getInstance(listenerClass);
            listeners.add(listener);
          }
        }
        return listeners;
      }
    };
  }

}