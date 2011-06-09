package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynceventbus.gae.GaeAsyncTaskEventsModule;
import com.clouway.common.ActionEvent;
import com.clouway.common.ActionEventHandler;
import com.clouway.common.DefaultTaskQueueAsyncTask;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class FooDispatcherTest {


  @Inject
  private FooDispatcher dispatcher;

  @Inject
  private Gson gson;

  private String eventClassAsString = ActionEvent.class.getName();
  private ActionEvent event = new ActionEvent("test message");
  private String eventAsJson;

  private String taskClassAsString = DefaultTaskQueueAsyncTask.class.getName();
  private Map<String, String[]> params = Maps.newHashMap();

  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(new GaeAsyncTaskEventsModule());
    injector.injectMembers(this);

    eventAsJson = gson.toJson(event);
  }

  @Test
  public void shouldDispatchAsyncEvent() throws Exception {

    dispatcher.dispatchAsyncEvent(eventClassAsString, eventAsJson);

    assertEquals(event.getMessage(), ActionEventHandler.message);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncEventWhenEventClassNull() throws Exception {
    dispatcher.dispatchAsyncEvent(null, eventAsJson);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncEventWhenEventAsJsonIsNull() throws Exception {
    dispatcher.dispatchAsyncEvent(eventClassAsString, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncEventWhenEventClassDoesNotRepresentAsyncEvent() throws Exception {
    eventClassAsString = FooDispatcherTest.class.getName();
    dispatcher.dispatchAsyncEvent(eventClassAsString, eventAsJson);
  }

  @Test(expected = ClassNotFoundException.class)
  public void shouldNotDispatchAsyncEventWhenEventClassDoesNotRepresentAClass() throws Exception {
    eventClassAsString = "blabla";
    dispatcher.dispatchAsyncEvent(eventClassAsString, eventAsJson);
  }

  @Test
  public void shouldDispatchAsyncTask() throws Exception {
    String key = "key";
    String[] value = new String[1];
    params.put(key, value);

    dispatcher.dispatchAsyncTask(params, taskClassAsString);

    assertNotNull(DefaultTaskQueueAsyncTask.params);
    assertEquals(DefaultTaskQueueAsyncTask.params.getString("key"), value[0]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncTaskWhenAsyncTaskClassIsNull() throws Exception {
    dispatcher.dispatchAsyncTask(params, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncTaskWhenParamsIsNull() throws Exception {
    dispatcher.dispatchAsyncTask(null, taskClassAsString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotDispatchAsyncTaskWhenTaskClassAsStringNotAsyncTask() throws Exception {
    taskClassAsString = this.getClass().getName();
    dispatcher.dispatchAsyncTask(params, taskClassAsString);
  }
}
