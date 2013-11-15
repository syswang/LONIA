package edu.ucla.cs.lonia.client.parser;

public class ResultRow {
  private String name;
  private String description;

  public void setName(String str) {
    // String[] names;
    // names = str.split(" ");
    this.name = str.replaceFirst("(.)*.It", "");
  }

  public void setDescription(String des) {
    this.description = des.replaceAll(" +", " ");
    // System.out.println(cmdDes);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
