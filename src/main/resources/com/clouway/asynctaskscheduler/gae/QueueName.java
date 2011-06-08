package com.clouway.asynctaskscheduler.gae;

/**
 * Represents the queue name of the  queue where the task queue of for the annotated AsyncTask will be add
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public @interface QueueName {
  String name() default "";
}
