package edu.ucla.cs.lonia.client.model;

import edu.ucla.cs.lonia.client.util.HasDisplayLabel;

public class Parameter {

  private Integer id;

  private String description;

  private String name;

  private PType type;

  private Boolean isRequired;

  private String prefix;

  private Integer cardinality;

  private State state;

  public Parameter() {
    name = "";
    description = "";
    type = PType.NONE;
    state = State.DISABLED;
    prefix = "";
    cardinality = 0;
    isRequired = false;
  }

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

  public PType getType() {
    return type;
  }

  public void setType(PType type) {
    this.type = type;
  }

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

  private String CsvFormat(String text) {
    return text.replace(",", ";");
  }

  public String getCsvRow() {
    return CsvFormat(name) + "," + CsvFormat(description) + "," + CsvFormat(type.getDisplayLabel())
        + "," + CsvFormat(state.getDisplayLabel()) + "," + CsvFormat(prefix) + ","
        + CsvFormat(cardinality.toString()) + "," + CsvFormat(isRequired.toString());
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

  public static Parameter parseFromCsvRow(String line) {
    String[] words = line.split(",");
    Parameter para = new Parameter();
    para.setName(words[0]);
    para.setDescription(words[1]);
    
    State state = State.DISABLED;
    try {
      state = State.valueOf(words[2].toUpperCase());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      para.setState(state);
    }
    
    PType type = PType.NONE;
    try {
      type = PType.valueOf(words[3].toUpperCase());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      para.setType(type);
    }
    para.setPrefix(words[4]);
    para.setCardinality(Integer.parseInt(words[5]));
    para.setIsRequired(Boolean.parseBoolean(words[6]));
    return para;
  }

}