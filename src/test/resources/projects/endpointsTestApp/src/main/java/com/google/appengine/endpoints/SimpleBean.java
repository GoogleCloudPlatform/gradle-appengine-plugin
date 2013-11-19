package com.google.appengine.endpoints;

import javax.inject.Named;

public class SimpleBean {
  String s = "hello";

  public String getS() {
    return s;
  }

  public void setS(@Named("s") String s) {
    this.s = s;
  }
}
