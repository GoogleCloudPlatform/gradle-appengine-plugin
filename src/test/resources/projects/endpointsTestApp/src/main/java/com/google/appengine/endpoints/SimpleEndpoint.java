package com.google.appengine.endpoints;

import com.google.api.server.spi.config.Api;

import javax.inject.Named;

@Api(name = "mysimpleendpoint")
public class SimpleEndpoint {

  SimpleBean b;

  public SimpleBean getMyBean() {
    return b;
  }

  public void setMyBean(@Named("mybean") String s) {
    this.b.setS(s);
  }
}
