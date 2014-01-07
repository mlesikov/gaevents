package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.common.ActionEvent;
import com.clouway.asynctaskscheduler.common.CustomTaskQueueAsyncTask;
import com.clouway.asynctaskscheduler.common.DefaultActionEvent;
import com.clouway.asynctaskscheduler.common.DefaultTaskQueueAsyncTask;
import com.clouway.asynctaskscheduler.common.TaskQueueParamParser;
import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskOptions;
import com.clouway.asynctaskscheduler.util.FakeCommonParamBinder;
import com.clouway.asynctaskscheduler.util.FakeRequestScopeModule;
import com.clouway.asynctaskscheduler.util.SampleTestDateFormat;
import com.clouway.asynctaskscheduler.util.SimpleScope;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.util.Modules;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static com.clouway.asynctaskscheduler.spi.AsyncTaskOptions.task;
import static com.clouway.asynctaskscheduler.util.DateUtil.newDateAndTime;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TaskQueueAsyncTaskSchedulerTest {
  private LocalServiceTestHelper helper;

  @Inject
  private TaskQueueAsyncTaskScheduler taskScheduler;

  @Inject
  private Gson gson;

  @Inject
  private Provider<HttpServletRequest> requestProvider;

  private final SimpleScope fakeRequestScope = new SimpleScope();

  private Injector injector;

  private FakeCommonParamBinder fakeBinder;

  @Before
  public void setUp() {
    LocalTaskQueueTestConfig localTaskQueueTestConfig = new LocalTaskQueueTestConfig();
    localTaskQueueTestConfig.setQueueXmlPath("src/test/java/queue.xml");
    helper = new LocalServiceTestHelper(localTaskQueueTestConfig);

    fakeBinder = new FakeCommonParamBinder();

    helper.setUp();
    injector = Guice.createInjector(Modules.override(new BackgroundTasksModule()).with(
            new FakeRequestScopeModule(fakeRequestScope),
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(CommonParamBinder.class).toInstance(fakeBinder);
              }

            })
    );
    injector.injectMembers(this);

    fakeRequestScope.enter();
  }

  @After
  public void tearDown() {
    helper.tearDown();

    fakeRequestScope.exit();
  }

  @Test
  public void addCommonParamsTaskQueue() throws Exception {
    fakeBinder.pretendCommonParamsIs("param1", "value1");
    fakeBinder.pretendCommonParamsIs("param2", "value2");

    taskScheduler.add(AsyncTaskOptions.event(new ActionEvent("test message"))).now();

    QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertParams(qsi.getTaskInfo().get(0).getBody(), "param1", "value1");
    assertParams(qsi.getTaskInfo().get(0).getBody(), "param2", "value2");
  }

  @Test
  public void shouldAddTaskToTheDefaultTaskQueue() throws Exception {
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)).now();

    QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertEquals(1, qsi.getTaskInfo().size());

    assertTaskQueueName(qsi.getTaskInfo().get(0).getBody(), DefaultTaskQueueAsyncTask.class);
  }

  @Test
  public void namedTasksCouldBeAddedOnlyOne() {
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class).named("work-index-1")).now();
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class).named("work-index-1")).now();

    assertAddedTasks(1);

  }


  @Test
  public void transactionalTaskQueuesAreAddedAfterTransactionCommit() {

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Transaction transaction = datastoreService.beginTransaction();

    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)).now();

    assertAddedTasks(0);

    transaction.commit();

    assertAddedTasks(1);

  }

  @Test
  public void onlyTransactionLessTaskQueuesAreAddedAfterTransactionCommit() {

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Transaction transaction = datastoreService.beginTransaction();

    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class).transactionless());
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)).now();

    assertAddedTasks(1);

    transaction.commit();

    assertAddedTasks(2);

  }

  @Test
  public void taskQueuesAreNotAddedAfterRollBack() {

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Transaction transaction = datastoreService.beginTransaction();

    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)).now();

    assertAddedTasks(0);

    transaction.rollback();

    assertAddedTasks(0);

  }

  @Test
  public void transactionLessTaskQueuesAreAddedEventTransactionRollBack() {

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Transaction transaction = datastoreService.beginTransaction();

    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class).transactionless());
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)).now();

    assertAddedTasks(1);

    transaction.rollback();

    assertAddedTasks(1);

  }

  @Test
  public void namedTransactionLessTaskQueuesInATransaction() {

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Transaction transaction = datastoreService.beginTransaction();

    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class).transactionless().named("name")).now();

    assertAddedTasks(1);

    transaction.commit();

    assertAddedTasks(1);
  }

  private void assertAddedTasks(int expectedTaskCount) {
     QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
     assertEquals(expectedTaskCount, qsi.getTaskInfo().size());
   }

  @Test
  public void shouldAddTaskToTheDefaultTaskQueueWithTheGivenParams() throws Exception {
    String paramName = "paramName";
    String paramValue = "paramValue";

    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)
            .param(paramName, paramValue))
            .now();

    QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());

    assertParams(qsi.getTaskInfo().get(0).getBody(), paramName, paramValue);
  }

  @Test
  public void shouldAddTaskToDefaultTaskQueueWhenTaskOptionsForEventIsProvided() throws Exception {
    ActionEvent event = new ActionEvent("test message");
    taskScheduler.add(AsyncTaskOptions.event(event))
            .now();

    QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertParams(qsi.getTaskInfo().get(0).getBody(), TaskQueueAsyncTaskScheduler.EVENT, event.getClass().getName());
    assertParams(qsi.getTaskInfo().get(0).getBody(), TaskQueueAsyncTaskScheduler.EVENT_AS_JSON, encode(gson.toJson(event)));
  }

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, "UTF-8");
  }

  @Test
  public void shouldAddTaskToCustomTaskQueueWhenTaskOptionsForEventIsProvided() throws Exception {
    DefaultActionEvent event = new DefaultActionEvent("test message");
    taskScheduler.add(AsyncTaskOptions.event(event))
            .now();

    QueueStateInfo qsi = getQueueStateInfo(DefaultActionEvent.CUSTOM_TASK_QUEUE_NAME);
    assertParams(qsi.getTaskInfo().get(0).getBody(), TaskQueueAsyncTaskScheduler.EVENT, event.getClass().getName());
    assertParams(qsi.getTaskInfo().get(0).getBody(), TaskQueueAsyncTaskScheduler.EVENT_AS_JSON, encode(gson.toJson(event)));
  }

  @Test
  public void shouldNotAddParamsToTheTaskWhenParamNameOrParamValueAreNull() throws Exception {
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)
            .param(null, "value")
            .param("key", null)
    ).now();

    QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertNotContainsParams(qsi.getTaskInfo().get(0).getBody(), null);
    assertNotContainsParams(qsi.getTaskInfo().get(0).getBody(), "key");
  }

  @Test
  public void shouldAddParamDateToTheTaskWhenParamDateIsAdded() throws Exception {
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)
            .paramDate("date", newDateAndTime(2010, 12, 1, 0, 0, 0, 0))
            .paramDate("dateAndTime", newDateAndTime(2010, 12, 1, 11, 30, 0, 0))
    ).now();

    QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertParams(qsi.getTaskInfo().get(0).getBody(), "date", "01-12-2010 00:00");
    assertParams(qsi.getTaskInfo().get(0).getBody(), "dateAndTime", "01-12-2010 11:30");
  }

  @Test
  public void shouldAddParamToTheTaskWhenParamIsAddedAndParamFormatIsProvided() throws Exception {
    taskScheduler.add(AsyncTaskOptions.task(DefaultTaskQueueAsyncTask.class)
            .param("date", newDateAndTime(2010, 12, 1, 0, 0, 0, 0), SampleTestDateFormat.class)
            .param("dateAndTime", newDateAndTime(2010, 12, 1, 11, 30, 0, 0), SampleTestDateFormat.class)
    ).now();

    QueueStateInfo qsi = getQueueStateInfo(QueueFactory.getDefaultQueue().getQueueName());
    assertParams(qsi.getTaskInfo().get(0).getBody(), "date", "01/12/2010");
    assertParams(qsi.getTaskInfo().get(0).getBody(), "dateAndTime", "01/12/2010");
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

  private void assertNotContainsParams(String body, String key) throws UnsupportedEncodingException {
    Map<String, String> params = TaskQueueParamParser.parse(body);
    assertFalse(params.containsKey(key));
  }


  private void assertParams(String taskQueueBody, String paramName, String paramValue) throws UnsupportedEncodingException {
    Map<String, String> params = TaskQueueParamParser.parse(taskQueueBody);
    assertEquals(params.get(paramName), paramValue);
  }

  private QueueStateInfo getQueueStateInfo(String queueName) {
    LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
    return ltq.getQueueStateInfo().get(queueName);
  }

  private void assertTaskQueueName(String taskQueueBody, Class<? extends AsyncTask> asyncTaskClass) throws UnsupportedEncodingException {
    Map<String, String> params = TaskQueueParamParser.parse(taskQueueBody);
    assertEquals(params.get(TaskQueueAsyncTaskScheduler.TASK_QUEUE), asyncTaskClass.getName());
  }
}
