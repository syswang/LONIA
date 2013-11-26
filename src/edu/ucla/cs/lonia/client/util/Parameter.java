package edu.ucla.cs.lonia.client.util;

public class Parameter {

  private Integer id;

  private String description;

  private String name;

  private PType type;
  
  private Boolean isRequired;
  
  public Boolean getIsRequired() {
    return isRequired;
  }

  public void setIsRequired(Boolean isRequired) {
    this.isRequired = isRequired;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public Integer getCardinality() {
    return cardinality;
  }

  public void setCardinality(Integer cardinality) {
    this.cardinality = cardinality;
  }

  private String prefix;
  
  private Integer cardinality;

  public PType getType() {
    return type;
  }

  public void setType(PType type) {
    this.type = type;
  }

  private State state = State.DISABLED;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public Parameter() {

  }

  public Parameter(Integer id, String userName, String age, State choice, PType type) {
    this.id = id;
    this.name = userName;
    this.description = age;
    this.state = choice;
    this.type = type;
  }

  public enum State implements HasDisplayLabel {
    ENABLED("Enabled"), DISABLED("Disabled");

    private final String displayLabel;

    private State(String displayLabel) {
      this.displayLabel = displayLabel;
    }

    @Override
    public String getDisplayLabel() {
      return displayLabel;
    }
  }

  public enum PType implements HasDisplayLabel {
    NONE("none"), STRING("String"), FILE("File");

    private final String displayLabel;

    private PType(String displayLabel) {
      this.displayLabel = displayLabel;
    }

    @Override
    public String getDisplayLabel() {
      return displayLabel;
    }
  }

}