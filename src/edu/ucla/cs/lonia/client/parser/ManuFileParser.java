package edu.ucla.cs.lonia.client.parser;

import java.util.ArrayList;

public class ManuFileParser extends BasicParser {
  
  public static IndexFromTo findPrefix(String str) {
    IndexFromTo idxft = new IndexFromTo();
    if (str.startsWith("[") || str.startsWith("|")) {
      idxft.setFrom(1);
    } else
      idxft.setFrom(0);

    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == ']' || str.charAt(i) == ' ') {
        idxft.setTo(i);
        break;
      }
    }
    return idxft;
  }

  public static String getParameterName(String str) {
    if (str.startsWith("-")) {
      int n = 0;
      while (str.charAt(n) == '-') {
        n++;
      }
      return str.substring(n);
    } else
      return str;
  }

  public static IndexFromTo rangeParameter(String str) {
    IndexFromTo paraIdxFromTo = new IndexFromTo();
    int offset;
    if (str.startsWith("["))
      offset = -1;
    else
      offset = 0;

//    int n = 0;
//    while(str.charAt(n) != ' ') {
//      n++;
//    }
//    paraIdxFromTo.setFrom(n);
    
    for (int i=0; i<str.length(); i++) {
      if(str.charAt(i) == ' ') {
        paraIdxFromTo.setFrom(i);
        break;
      }
    }

    for (int i = paraIdxFromTo.getFrom(); i < str.length(); i++) {
      if (str.charAt(i) == '[' || str.charAt(i) == '<')
        offset--;
      if (str.charAt(i) == ']' || str.charAt(i) == '>') {
        offset++;
        if (offset == 0) {
          
          paraIdxFromTo.setTo(i);
          
          break;
        }
      }
      if (i == str.length()-1 ) paraIdxFromTo.setTo(str.length()-1);
    }
    return paraIdxFromTo;
  }

  public static String parameterType(String str, int startAt, int endAt) {
    if (endAt < 0 || startAt < 0 || startAt >= str.length() || endAt >= str.length()) {

    }

    if (str.substring(startAt, endAt).contains("file")
        || str.substring(startAt, endAt).contains("File"))
      return "File";
    else
      return "String";
  }

  public static boolean withParameter(String str, int startAt) {
    if (str.charAt(startAt) == ']')
      return false;
    if (str.contains("]") || str.contains(">"))
      return true;
    else
      return false;
  }

  public static boolean requireParameter(String str, int startAt) {
    str = str.trim();
    if (str.charAt(startAt) == '[')
      return false;
    else
      return true;
  }

  public static int countCardinality(String str, int startAt) {
    int cardinality = 0;
    str = str.trim();
    if (str.substring(startAt).contains("filename") || 
        str.substring(startAt).contains("Filename"))
      return 1; 
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == ' ')
        cardinality++;
      if (i==str.length()-1) {
        return 0;
      }
      if (str.charAt(i) == ']' || str.charAt(i) == '>')
        break;

    }
    return cardinality;
  }

  public static String getDes(String str, int startAt, int endAt) {
    String desStr = str.substring(startAt, endAt).trim();
    if (desStr.startsWith(":")) {
      return desStr.substring(1).trim();
  }
    return desStr;
  }

  @Override
  public ArrayList<ResultRow> parse(String text) {
    int p = 0;
    String[] lines = text.split("\n");
    ArrayList<ResultRow> result = new ArrayList<ResultRow>();
    while (p < lines.length) {
      String line = lines[p++];
      line = line.trim();
      if (!(line.startsWith("[") || line.startsWith("-"))) {
        continue;
      }

      try { // try parse a line

        ResultRow row = new ResultRow();
        String pfx;
        IndexFromTo prefixIdx = findPrefix(line);
        IndexFromTo paraIdx = new IndexFromTo();

        pfx = line.substring(prefixIdx.getFrom(), prefixIdx.getTo()); // find prefix
        row.setPrefix(pfx); // set Prefix
        row.setName(getParameterName(pfx)); // set Parameter Name

        if (withParameter(line, prefixIdx.getTo())) {
          if (requireParameter(line, prefixIdx.getTo() + 1)) { // set State & Require
            row.setState(true);
            row.setRequire(true);
          } else {
            row.setState(false);
            row.setRequire(false);
          }
          paraIdx = rangeParameter(line);
          row.setType(parameterType(line, paraIdx.getFrom(), paraIdx.getTo())); // set Type
          row.setCardinality(countCardinality(line, paraIdx.getFrom())); // set Cardinality

        } else {
          row.setCardinality(0);
          row.setType("String");
          row.setState(false);
          row.setRequire(false);
          paraIdx.setTo(prefixIdx.getTo());
        }

        row.setDescription(getDes(line, paraIdx.getTo() + 1, line.length())); // set Description
        result.add(row); // add to result
      } catch (Exception e) { e.printStackTrace(); }    //catch exception
      
    }
    return result;
  }
  
  
}
