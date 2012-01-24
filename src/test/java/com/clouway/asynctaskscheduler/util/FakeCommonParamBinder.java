package com.clouway.asynctaskscheduler.util;

import com.clouway.asynctaskscheduler.gae.CommonParamBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class FakeCommonParamBinder implements CommonParamBinder {

  private Map<String, String> addedCommonParams = new HashMap<String, String>();

  public void pretendCommonParamsIs(String key, String value) {
    addedCommonParams.put(key, value);
  }

  @Override
  public void bindCommonParams(Map<String, String> commonParams) {
    for (String key : addedCommonParams.keySet()) {
      commonParams.put(key, addedCommonParams.get(key));
    }
  }

}
