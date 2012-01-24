package com.clouway.asynctaskscheduler.util;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.RequestScoped;

import javax.servlet.http.HttpServletRequest;

/**
* @author Miroslav Genov (mgenov@gmail.com)
*/
public class FakeRequestScopeModule extends AbstractModule {
  private final SimpleScope fakeRequestScope;

  public FakeRequestScopeModule(SimpleScope fakeRequestScope) {

    this.fakeRequestScope = fakeRequestScope;
  }

  @Override
  protected void configure() {
    bind(HttpServletRequest.class)
            .to(FakeHttpServletRequest.class)
            .in(RequestScoped.class);
  }
}
