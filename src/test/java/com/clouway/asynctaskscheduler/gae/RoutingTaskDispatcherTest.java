package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.common.DefaultTaskQueueAsyncTask;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class RoutingTaskDispatcherTest {
  @Inject
  private RoutingTaskDispatcher dispatcher;

  private String taskClassAsString = DefaultTaskQueueAsyncTask.class.getName();
  private Map<String, String[]> params = Maps.newHashMap();

  @Before
  public void setUp() throws Exception {
    Injector injector = Guice.createInjector(new BackgroundTasksModule());
    injector.injectMembers(this);
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
