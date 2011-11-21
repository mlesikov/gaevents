package com.clouway.asynctaskscheduler.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class FakeHttpServletRequest extends HttpServletRequestWrapper {
  /**
   * Create a stub interface via dynamic proxy that does nothing
   */
  private static HttpServletRequest makeStub() {
    return (HttpServletRequest) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{HttpServletRequest.class},
            new InvocationHandler() {
              @Override
              public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                throw new UnsupportedOperationException();
              }
            });
  }

  public Map<String, Object> attrs = new HashMap<String, Object>();

  public FakeHttpServletRequest() {
    // Can't actually pass null here
    super(makeStub());
  }

  @Override
  public Map getParameterMap() {
    return ImmutableMap.copyOf(attrs);
  }

  @Override
  public Object getAttribute(String key) {
    return attrs.get(key);
  }

  @Override
  public void setAttribute(String key, Object value) {
    attrs.put(key, new String[] { value.toString() });
  }

  @Override
  public void removeAttribute(String key) {
    attrs.remove(key);
  }
}
