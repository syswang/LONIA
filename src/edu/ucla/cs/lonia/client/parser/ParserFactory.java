package edu.ucla.cs.lonia.client.parser;

public class ParserFactory {
  private static ParserFactory instance = null;

  public synchronized static ParserFactory getInstance() {
    if (instance == null) {
      instance = new ParserFactory();
    }
    return instance;
  }

  public BasicParser createParser(String parserName) {
    if (parserName.equals("manuParser"))
      return new ManuFileParser();
    else {
      return null;
    }
  }
}
