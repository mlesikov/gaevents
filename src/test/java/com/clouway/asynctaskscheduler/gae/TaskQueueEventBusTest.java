package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncEvent;
import com.clouway.asynctaskscheduler.common.ActionEvent;
import com.clouway.asynctaskscheduler.common.TaskQueueParamParser;
import com.clouway.asynctaskscheduler.spi.AsyncEventBus;
import com.clouway.asynctaskscheduler.util.FakeCommonParamBinder;
import com.clouway.asynctaskscheduler.util.FakeRequestScopeModule;
import com.clouway.asynctaskscheduler.util.SimpleScope;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TaskQueueEventBusTest {

  @Inject
  private AsyncEventBus eventBus;

  @Inject
  private Gson gson;

  private Injector injector;

  private final LocalServiceTestHelper helper =
          new LocalServiceTestHelper(new LocalTaskQueueTestConfig());

  private SimpleScope fakeRequestScope = new SimpleScope();

  @Before
  public void setUp() throws Exception {
    helper.setUp();
    injector = Guice.createInjector(Modules.override(new BackgroundTasksModule()).with(new FakeRequestScopeModule(fakeRequestScope)));
    injector.injectMembers(this);
    fakeRequestScope.enter();
  }

  @After
  public void tearDown() {
    helper.tearDown();
    fakeRequestScope.exit();
  }

  @Test
  public void shouldAddTaskQueueToDefaultTaskQueueForExecutingHandlingTheFiredEvent() throws Exception {
    ActionEvent event = new ActionEvent("test");
    eventBus.fireEvent(event);

    QueueStateInfo defaultQueueStateInfo = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertEquals(1, defaultQueueStateInfo.getTaskInfo().size());
    assertEvent(defaultQueueStateInfo.getTaskInfo().get(0).getBody(), event);
  }

  @Test
  public void shouldAddOnlyTaskQueueToDefaultTaskQueueForExecutingHandlingTheFiredEvent() throws Exception {
    ActionEvent event = new ActionEvent("test");
    eventBus.fireEvent(event);

    QueueStateInfo defaultQueueStateInfo = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertEquals(1, defaultQueueStateInfo.getTaskInfo().size());
    assertEvent(defaultQueueStateInfo.getTaskInfo().get(0).getBody(), event);

    eventBus.fireEvent(event);

    defaultQueueStateInfo = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertEquals(2, defaultQueueStateInfo.getTaskInfo().size());
    assertEvent(defaultQueueStateInfo.getTaskInfo().get(0).getBody(), event);
  }

  private void assertEvent(String taskQueueBody, AsyncEvent event) throws UnsupportedEncodingException {
    Map<String, String> params = TaskQueueParamParser.parse(taskQueueBody);
    assertEquals(params.get(TaskQueueAsyncTaskScheduler.EVENT), event.getClass().getName());
    assertEquals(params.get(TaskQueueAsyncTaskScheduler.EVENT_AS_JSON), gson.toJson(event));
  }

  private QueueStateInfo getQueueStateInfo(String queueName) {
    LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
    return ltq.getQueueStateInfo().get(queueName);
  }

}
