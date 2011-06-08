package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.AsyncTaskOptions;
import com.clouway.asynctaskscheduler.spi.AsyncTaskScheduler;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

/**
 * Represents a service that adds  task queues that runs asynchronous tasks
 * <p/>
 * <p/>
 * example usage :
 * taskScheduler
 * .add(task(AsyncTaskImpl.class)
 * .param("name", "param value"))
 * .now();
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
class TaskQueueAsyncTaskScheduler implements AsyncTaskScheduler {
  public static final String TASK_QUEUE = "taskQueue";
  public static final String EVENT = "event";
  public static final String EVENT_AS_JSON = "eventJson";

  private List<AsyncTaskOptions> taskOptions;
  private final Gson gson;

  public TaskQueueAsyncTaskScheduler(Gson gson) {
    this.gson = gson;
    this.taskOptions = Lists.newArrayList();
  }


  /**
   * builds the task queue form the task options
   */
  public void now() {
    for (AsyncTaskOptions taskOption : taskOptions) {

      if (taskOption.isEventTaskOption()) {

        addEventTaskQueueOption(taskOption);

      } else {

        addTaskQueue(taskOption);

      }

    }
  }

  private void addEventTaskQueueOption(AsyncTaskOptions taskOptions) {

    Queue queue = getQueue(taskOptions.getAsyncEvent().getClass());

    TaskOptions task = createEventTaskOptions(taskOptions);


    setExecutionDate(taskOptions, task);

    queue.add(task);

  }

  private TaskOptions createEventTaskOptions(AsyncTaskOptions taskOptions) {
    TaskOptions task;
    task = withUrl(TaskQueueAsyncTaskExecutorServlet.URL);

    //main task queue parameter
    task.param(EVENT, taskOptions.getAsyncEvent().getClass().getName());
    task.param(EVENT_AS_JSON, gson.toJson(taskOptions.getAsyncEvent()));

    //adds all other parameters
    task = addParams(task, taskOptions.getParams());
    return task;
  }

  /**
   * Adds the task queue formed form the {@link com.clouway.asynctaskscheduler.spi.AsyncTaskOptions}
   *
   * @param taskOptions the AsyncTaskOptions
   */
  private void addTaskQueue(AsyncTaskOptions taskOptions) {

    Queue queue = getQueue(taskOptions.getAsyncTask());

    TaskOptions task = createTaskOptions(taskOptions);


    setExecutionDate(taskOptions, task);

    queue.add(task);
  }

  private void setExecutionDate(AsyncTaskOptions taskOptions, TaskOptions task) {
    if (taskOptions.getDelayMills() > 0) {

      task.countdownMillis(taskOptions.getDelayMills());

    } else if (taskOptions.getExecutionDateMills() > 0) {

      task.etaMillis(taskOptions.getExecutionDateMills());

    }
  }

  /**
   * Creates a {@link TaskOptions} objects formed form the given {@link com.clouway.asynctaskscheduler.spi.AsyncTaskOptions}
   *
   * @param taskOptions the given {@link com.clouway.asynctaskscheduler.spi.AsyncTaskOptions}
   * @return
   */
  private TaskOptions createTaskOptions(AsyncTaskOptions taskOptions) {
    TaskOptions task;
    task = withUrl(TaskQueueAsyncTaskExecutorServlet.URL);

    //main task queue parameter
    task.param(TASK_QUEUE, taskOptions.getAsyncTaskAsString());

    //adds all other parameters
    task = addParams(task, taskOptions.getParams());
    return task;
  }

  /**
   * Adds new {@link com.clouway.asynctaskscheduler.spi.AsyncTaskOptions} object
   *
   * @param asyncTaskOptions
   * @return
   */
  public AsyncTaskScheduler add(AsyncTaskOptions asyncTaskOptions) {
    taskOptions.add(asyncTaskOptions);
    return this;
  }

  /**
   * Adds many {@link com.clouway.asynctaskscheduler.spi.AsyncTaskOptions} objects
   *
   * @param asyncTaskOptions
   * @return
   */
  public AsyncTaskScheduler add(AsyncTaskOptions... asyncTaskOptions) {
    for (AsyncTaskOptions asyncTaskOption : asyncTaskOptions) {
      taskOptions.add(asyncTaskOption);
    }
    return this;
  }

  /**
   * Adds parameters to the given {@link com.google.appengine.api.taskqueue.TaskOptions}
   *
   * @param task
   * @param params
   * @return
   */
  private TaskOptions addParams(TaskOptions task, Map<String, String> params) {

    for (String key : params.keySet()) {
      task.param(key, params.get(key));
    }

    return task;
  }

  /**
   * Gets the Task Queue from the given name or the Default Task Queue
   *
   * @param asyncJobClass
   * @return
   */
  private Queue getQueue(Class asyncJobClass) {
    Queue queue;

    QueueName queueName = (QueueName) asyncJobClass.getAnnotation(QueueName.class);

    if (queueName != null && !Strings.isNullOrEmpty(queueName.name())) {

      queue = QueueFactory.getQueue(queueName.name());

    } else {
      queue = QueueFactory.getDefaultQueue();
    }

    return queue;
  }
}
