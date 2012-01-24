package com.clouway.asynctaskscheduler.gae;

import com.google.inject.ImplementedBy;

import java.util.Map;

/**
 * This interface provides wey to add parameters to task queue when schedule({@link com.clouway.asynctaskscheduler.spi.AsyncTaskScheduler})
 * event {@link com.clouway.asynctaskscheduler.spi.AsyncEvent} or task {@link com.clouway.asynctaskscheduler.spi.AsyncTask}
 *
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@ImplementedBy(EmptyCommonParamBinder.class)
public interface CommonParamBinder {

  void bindCommonParams(Map<String, String> commonParams);

}
