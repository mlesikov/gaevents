package com.clouway.asynceventbus.gae;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.appengine.repackaged.com.google.common.base.X.assertTrue;
import static junit.framework.Assert.assertEquals;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TaskQueueEventBusTest {

  @Inject
  private TaskQueueEventBus eventBus;

  private final LocalServiceTestHelper helper =
          new LocalServiceTestHelper(new LocalTaskQueueTestConfig());


  @Before
  public void setUp() throws Exception {
    helper.setUp();
    Injector injector = Guice.createInjector(new GaeAsyncTaskEventsModule());
    injector.injectMembers(this);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void shouldAddTaskQueueToDefaultTaskQueueForExecutingHandlingTheFiredEvent() throws Exception {
    DefaultActionEvent event = new DefaultActionEvent();
    eventBus.fireEvent(event);

    QueueStateInfo defaultQueueStateInfo = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertEquals(1, defaultQueueStateInfo.getTaskInfo().size());
    assertEvent(defaultQueueStateInfo.getTaskInfo().get(0).getBody(), event);
  }

  private void assertEvent(String taskQueueBody, DefaultActionEvent event) {
    assertTrue(taskQueueBody.contains(TaskQueueEventBus.EVENT));
    assertTrue(taskQueueBody.contains(event.getClass().getName()));
    assertTrue(taskQueueBody.contains(TaskQueueEventBus.EVENT_AS_JSON));
    assertTrue(taskQueueBody.contains("event as Json"));
  }

  private QueueStateInfo getQueueStateInfo(String queueName) {
    LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
    return ltq.getQueueStateInfo().get(queueName);
  }
}
