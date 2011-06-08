package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskParams;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Represents the HttpServlet that is executed form the added Task Queues and executes the Job that they specify.
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TaskQueueAsyncTaskExecutorServlet extends HttpServlet {
  public static final String URL = "/worker/taskQueue";
  private Injector injector;

  @Inject
  public TaskQueueAsyncTaskExecutorServlet(Injector injector) {
    this.injector = injector;
  }

  @Override
  protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
    doPost(httpServletRequest, httpServletResponse);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      String taskQueueName = request.getParameter(TaskQueueAsyncTaskScheduler.TASK_QUEUE);

      Class taskQueueClass = Class.forName(taskQueueName);

      Object object = injector.getInstance(taskQueueClass);

      if (object instanceof AsyncTask) {

        Map<String, String[]> params = Maps.newHashMap(request.getParameterMap());
        AsyncTask taskQueue = (AsyncTask) object;

        taskQueue.execute(new AsyncTaskParams(params));

      }


    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }
}

