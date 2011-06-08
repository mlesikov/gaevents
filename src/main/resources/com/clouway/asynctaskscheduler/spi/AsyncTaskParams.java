package com.clouway.asynctaskscheduler.spi;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Represents an object that consist the params, given for the {@link AsyncTask} execution.
 * Consist methods that can return the values of the parameters in different Types
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class AsyncTaskParams {
  public static AsyncTaskParams with(String p1, String v1, String p2, String v2) {
    Map<String, String[]> map = Maps.newHashMap();
    map.put(p1, array(v1));
    map.put(p2, array(v2));
    return new AsyncTaskParams(map);
  }

  public static AsyncTaskParams with(String p1, String v1) {
    Map<String, String[]> map = Maps.newHashMap();
    map.put(p1, array(v1));
    return new AsyncTaskParams(map);
  }

  private Map<String, String[]> params;

  public AsyncTaskParams(Map<String, String[]> params) {
    this.params = params;
  }

  /**
   * Checks if the parameters contains parameter with given key
   *
   * @param key
   * @return true if contains or false otherwise
   */
  public boolean containsKey(String key) {
    return params.containsKey(key);
  }

  /**
   * Gets the value of the parameter with given key as String
   *
   * @param key the given key
   * @return the value of the parameter as String
   */
  public String getString(String key) {
    return params.get(key)[0];
  }

  /**
   * Gets the value of the parameter with given key as Integer
   *
   * @param key the given key
   * @return the value of the parameter as Integer
   */
  public Integer getInteger(String key) {
    return Integer.parseInt(params.get(key)[0]);
  }

  /**
   * Gets the value of the parameter with given key as Double
   *
   * @param key the given key
   * @return the value of the parameter as Double
   */
  public Double getDouble(String key) {
    return Double.parseDouble(params.get(key)[0]);
  }


  private static String[] array(String... array) {
    return array;
  }

}
