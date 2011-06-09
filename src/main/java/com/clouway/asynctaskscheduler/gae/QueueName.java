package com.clouway.asynctaskscheduler.gae;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the queue name of the  queue where the task queue of for the annotated AsyncTask will be add
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface QueueName {
  String name() default "";
}
