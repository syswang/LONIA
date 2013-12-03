package edu.ucla.cs.lonia.client.model;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.ucla.cs.lonia.client.parser.ResultRow;

public class ParseResult implements IsSerializable{
  private String key;
  private ArrayList<ResultRow> value;
  
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public ArrayList<ResultRow> getValue() {
    return value;
  }

  public void setValue(ArrayList<ResultRow> value) {
    this.value = value;
  }
}
