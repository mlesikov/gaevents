package com.clouway.asynctaskscheduler.util;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.RequestScoped;

import javax.servlet.http.HttpServletRequest;

/**
* @author Miroslav Genov (mgenov@gmail.com)
*/
public class FakeRequestScopeModule extends AbstractModule {
  private final SimpleScope fakeRequestScope;
  private final FakeCommonParamBinder binder;

  public FakeRequestScopeModule(SimpleScope fakeRequestScope,FakeCommonParamBinder binder) {

    this.fakeRequestScope = fakeRequestScope;
    this.binder = binder;
  }

  @Override
  protected void configure() {
    bind(HttpServletRequest.class)
            .to(FakeHttpServletRequest.class)
            .in(RequestScoped.class);
  }
}
