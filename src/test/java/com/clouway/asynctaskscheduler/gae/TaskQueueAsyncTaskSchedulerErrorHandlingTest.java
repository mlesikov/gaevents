package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.common.ActionEvent;
import com.clouway.asynctaskscheduler.common.ArgumentCaptor;
import com.clouway.asynctaskscheduler.spi.AsyncTaskOptions;
import com.clouway.asynctaskscheduler.util.FakeCommonParamBinder;
import com.clouway.asynctaskscheduler.util.FakeRequestScopeModule;
import com.clouway.asynctaskscheduler.util.SimpleScope;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.util.Modules;
import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertThat;

public class TaskQueueAsyncTaskSchedulerErrorHandlingTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

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

  @Mock
  TaskApplier taskApplier;
  private final ArgumentCaptor<TaskOptions> task = new ArgumentCaptor<TaskOptions>();
  private final ArgumentCaptor<String> queueName = new ArgumentCaptor<String>();
  private final ArgumentCaptor<Boolean> transactionless = new ArgumentCaptor<Boolean>();

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
                        bind(TaskApplier.class).toInstance(taskApplier);
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
  public void taskAlreadyExists() throws Exception {
    fakeBinder.pretendCommonParamsIs("param1", "value1");
    fakeBinder.pretendCommonParamsIs("param2", "value2");

    context.checking(new Expectations() {{
      oneOf(taskApplier).apply(with(task), with(queueName), with(transactionless));
      will(throwException(new TaskAlreadyExistsException("")));
    }});

    taskScheduler.add(AsyncTaskOptions.event(new ActionEvent("test message"))).now();
  }

  @Test
  public void retryOnTransientFailureException() throws Exception {
    fakeBinder.pretendCommonParamsIs("param1", "value1");
    fakeBinder.pretendCommonParamsIs("param2", "value2");

    context.checking(new Expectations() {{
      oneOf(taskApplier).apply(with(task), with(queueName), with(transactionless));
      will(throwException(new TransientFailureException("")));

      //second call
      oneOf(taskApplier).apply(with(task), with(queueName), with(transactionless));
    }});

    taskScheduler.add(AsyncTaskOptions.event(new ActionEvent("test message"))).now();

    assertThat(queueName.getValue(), CoreMatchers.is(CoreMatchers.equalTo("")));
    assertThat(transactionless.getValue(), CoreMatchers.is(CoreMatchers.equalTo(false)));
  }
}