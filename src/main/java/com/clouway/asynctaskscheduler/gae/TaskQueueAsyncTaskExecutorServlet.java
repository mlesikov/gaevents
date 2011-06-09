package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynceventbus.spi.AsyncEvent;
import com.clouway.asynceventbus.spi.AsyncEventHandler;
import com.clouway.asynceventbus.spi.EventHandler;
import com.clouway.asynctaskscheduler.spi.AsyncTask;
import com.clouway.asynctaskscheduler.spi.AsyncTaskParams;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
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
  private final Gson gson;

  @Inject
  public TaskQueueAsyncTaskExecutorServlet(Injector injector, Gson gson) {
    this.injector = injector;
    this.gson = gson;
  }

  @Override
  protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
    doPost(httpServletRequest, httpServletResponse);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      String taskQueueName = request.getParameter(TaskQueueAsyncTaskScheduler.TASK_QUEUE);

      String eventClassAsString = request.getParameter(TaskQueueAsyncTaskScheduler.EVENT);
      String eventAsJson = request.getParameter(TaskQueueAsyncTaskScheduler.EVENT);


      if (!Strings.isNullOrEmpty(eventClassAsString) && !Strings.isNullOrEmpty(eventAsJson)) {

        dispatchAsyncEvent(eventClassAsString, eventAsJson);

      } else if (!Strings.isNullOrEmpty(taskQueueName)) {

        executeAsyncTask(request, taskQueueName);

      }


    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  private void dispatchAsyncEvent(String eventClassAsString, String eventAsJson) throws ClassNotFoundException {

    Class eventClass = Class.forName(eventClassAsString);

    AsyncEvent event = (AsyncEvent) gson.fromJson(eventAsJson, eventClass);

    EventHandler annotation = (EventHandler) eventClass.getAnnotation(EventHandler.class);

    if (annotation == null || annotation.type() != null) {
      throw new IllegalArgumentException("Did you forget to put @EvenHandler annotation to a " + eventClass.getName());
    }

    Class evenHandlerClass = annotation.type();

    Object object = injector.getInstance(evenHandlerClass);

    AsyncEventHandler handler = (AsyncEventHandler) object;

    event.dispatch(handler);
  }

  private void executeAsyncTask(HttpServletRequest request, String taskQueueName) throws ClassNotFoundException {

    Class taskQueueClass = Class.forName(taskQueueName);

    Object object = injector.getInstance(taskQueueClass);

    if (object instanceof AsyncTask) {

      Map<String, String[]> params = Maps.newHashMap(request.getParameterMap());

      AsyncTask taskQueue = (AsyncTask) object;

      taskQueue.execute(new AsyncTaskParams(params));

    }
  }
}

