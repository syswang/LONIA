package edu.ucla.cs.lonia.client.model;

import java.io.Serializable;

public class ParseResult implements Serializable {

  private static final long serialVersionUID = -2766763063117600511L;
  private String key;
  private String value;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
