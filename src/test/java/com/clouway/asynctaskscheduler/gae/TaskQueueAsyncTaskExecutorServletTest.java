package com.clouway.asynctaskscheduler.gae;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskQueueAsyncTaskExecutorServletTest {
  @Mock private RoutingEventDispatcher eventDisplatcher;
  @Mock private RoutingTaskDispatcher routingTaskDispatcher;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  private TaskQueueAsyncTaskExecutorServlet servlet;

  @Before
  public void setUp() throws Exception {
    servlet = new TaskQueueAsyncTaskExecutorServlet(eventDisplatcher, routingTaskDispatcher);
  }

  @Test
  public void dispatchingAsyncEvent() throws Exception {
    String eventValue = "event as json";
    String encodedEventValue = URLEncoder.encode("event as json","UTF-8");

    when(request.getParameter(TaskQueueAsyncTaskScheduler.EVENT)).thenReturn("event.class");
    when(request.getParameter(TaskQueueAsyncTaskScheduler.EVENT_AS_JSON)).thenReturn(encodedEventValue);

    servlet.doGet(request, response);

    verify(eventDisplatcher).dispatchAsyncEvent("event.class", eventValue);
    verifyZeroInteractions(routingTaskDispatcher);

  }

  @Test
  public void dispatchingAsyncTask() throws Exception {

    when(request.getParameter(TaskQueueAsyncTaskScheduler.TASK_QUEUE)).thenReturn("task.class");
    HashMap<String, String[]> params = Maps.newHashMap();
    when(request.getParameterMap()).thenReturn(params);

    servlet.doGet(request, response);

    verify(routingTaskDispatcher).dispatchAsyncTask(params, "task.class");
    verifyZeroInteractions(eventDisplatcher);
  }

  @Test
  public void noAsyncActionPassed() throws Exception {

    servlet.doGet(request, response);

    verifyZeroInteractions(routingTaskDispatcher);
    verifyZeroInteractions(eventDisplatcher);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notValidEncodedEventValue() throws Exception {

    when(request.getParameter(TaskQueueAsyncTaskScheduler.EVENT_AS_JSON)).thenReturn("event as json &!@$%^&*()");

    servlet.doGet(request, response);
  }

}
