package com.clouway.asynctaskscheduler.spi;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * EventTransport class is used to transport events that are passed between the client and the server.
 * By default the {@link com.clouway.asynctaskscheduler.spi.EventTransport} is bound
 * to the {@link com.clouway.asynctaskscheduler.gae.GsonEventTransport}.
 *
 * In order to use your own custom EventTransport you should configure it
 * when installing the {@link com.clouway.asynctaskscheduler.gae.BackgroundTasksModule}.
 *
 * Here is a quick example of how to do that
 *
 * <pre>
 *
 * public class MyModule extends AbstractModule {
 *
 *  @Override
 *  protected void configure() {
 *
 *    install(new BackgroundTasksModule() {
 *
 *      @Override
 *      protected Class<? extends EventTransport> getEventTransport() {
 *        return MyEventTransport.class;
 *      }
 *    })
 *  }
 * }
 *
 * </pre>
 *
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface EventTransport {

  <T> T in(Class<T> eventClass, InputStream inputStream);

  <T> void out(Class<? extends T> eventClass, T event, OutputStream outputStream);
}
