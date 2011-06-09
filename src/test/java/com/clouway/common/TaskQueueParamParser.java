package com.clouway.common;

import com.google.common.collect.Maps;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TaskQueueParamParser {
  private class Param {
  }

  public static Map<String, String> parse(String taskQueueBody) throws UnsupportedEncodingException {
    Map<String, String> params = Maps.newHashMap();


    String[] paramsAsString = taskQueueBody.split("&");

    for (String paramAsString : paramsAsString) {
      String[] details = paramAsString.split("=");
      String decodedValue = URLDecoder.decode(details[1], "UTF8");
      params.put(details[0], decodedValue);

    }

    return params;
  }
}
