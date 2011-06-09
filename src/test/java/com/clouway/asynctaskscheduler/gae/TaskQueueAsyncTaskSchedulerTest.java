package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskOptions;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.clouway.asynctaskscheduler.spi.AsyncTaskOptions.task;
import static com.google.appengine.repackaged.com.google.common.base.X.assertTrue;
import static junit.framework.Assert.assertEquals;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TaskQueueAsyncTaskSchedulerTest {
  private final LocalServiceTestHelper helper =
          new LocalServiceTestHelper(new LocalTaskQueueTestConfig());

  private TaskQueueAsyncTaskScheduler taskScheduler = new TaskQueueAsyncTaskScheduler(null);

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void shouldAddTaskToTheDefaultTaskQueue() throws Exception {
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)).now();
    // give the task time to execute if tasks are actually enabled (which they
    // aren't, but that's part of the test)
    Thread.sleep(1000);

    QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertEquals(1, qsi.getTaskInfo().size());

    assertTaskQueueName(qsi.getTaskInfo().get(0).getBody(), DefaultTaskQueueAsyncTask.class);
  }


  @Test
  public void shouldAddTaskInToDifferentTaskQueue() throws Exception {
    taskScheduler.add(task(DefaultTaskQueueAsyncTask.class))
            .add(task(CustomTaskQueueAsyncTask.class))
            .now();

    QueueStateInfo defaultQueueStateInfo = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertEquals(1, defaultQueueStateInfo.getTaskInfo().size());
    assertTaskQueueName(defaultQueueStateInfo.getTaskInfo().get(0).getBody(), DefaultTaskQueueAsyncTask.class);

    QueueStateInfo customQueueStateInfo = getQueueStateInfo(CustomTaskQueueAsyncTask.CUSTOM_TASK_QUEUE_NAME);
    assertEquals(1, customQueueStateInfo.getTaskInfo().size());
    assertTaskQueueName(customQueueStateInfo.getTaskInfo().get(0).getBody(), CustomTaskQueueAsyncTask.class);

  }

  private QueueStateInfo getQueueStateInfo(String queueName) {
    LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
    return ltq.getQueueStateInfo().get(queueName);
  }

  private void assertTaskQueueName(String taskQueueBody, Class<? extends AsyncTask> asyncTaskClass) {
    assertTrue(taskQueueBody.contains(TaskQueueAsyncTaskScheduler.TASK_QUEUE));
    assertTrue(taskQueueBody.contains(asyncTaskClass.getName()));
  }
}
