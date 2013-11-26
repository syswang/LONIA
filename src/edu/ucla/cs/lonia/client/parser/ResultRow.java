package edu.ucla.cs.lonia.client.parser;

public class ResultRow {
  private String name;
  private String description;
  private String type;
  private String prefix;
  private int cardinality;
  private boolean state;
  private boolean require;

  public void setName(String str) {
    this.name = str;
  }

  public void setDescription(String des) {
    this.description = des;
    // System.out.println(cmdDes);
  }

  public void setType(String str) {
    this.type = str;
  }

  public void setPrefix(String str) {
    this.prefix = str;
  }

  public void setCardinality(int n) {
    this.cardinality = n;
  }

  public void setState(boolean bool) {
    this.state = bool;
  }

  public void setRequire(boolean bool) {
    this.require = bool;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getType() {
    return type;
  }

  public String getPrefix() {
    return prefix;
  }

  public int getCardinality() {
    return cardinality;
  }

  public boolean getState() {
    return state;
  }

  public boolean getRequire() {
    return require;
  }

}
