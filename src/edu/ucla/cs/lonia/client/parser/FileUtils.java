package edu.ucla.cs.lonia.client.parser;

import java.util.ArrayList;

public class FileUtils {
  public static ArrayList<ResultRow> readToBuffer(StringBuffer buffer, String text) {
    // InputStream is = new FileInputStream(filePath);
    String line;
    String[] lines = text.split("\n");
    ArrayList<ResultRow> result = new ArrayList<ResultRow>();
    int k = 0;
    int p = 0;
    line = lines[p++];
    while (p < lines.length) {
      ResultRow row = new ResultRow();
      if (line.startsWith(".It Fl")) {
        row.setName((replaceSign(line)));
        line = lines[p++];
        StringBuilder lineBuilder = new StringBuilder();
        do {
          line = replaceSign(line);
          lineBuilder = lineBuilder.append(" " + line);
          line = lines[p++];
        } while (!line.startsWith(".It") && !line.startsWith(".El"));
        row.setDescription(lineBuilder.toString());
        result.add(row);
        // System.out.println(row.getName());
        // System.out.println(row.getDescription());
      } else {
        line = lines[p++];
      }
    }
    return result;
    // is.close();
  }

  public static String readName(String str) {
    String[] names;
    names = str.split(" ");
    return "-" + names[2];
  }

  public static String replaceSign(String str) {
    str = str.replaceAll(".Nm", "");
    str = str.replaceAll(".Em ", "");
    str = str.replaceAll(".?Fl ", "-");
    str = str.replaceAll(".?Ar ", " ");
    str = str.replaceAll(".Ev ", " ");

    if (str.startsWith(".Sm")) {
      return "";
    }

    str = replaceWordQuote(str, ".Sq", "'", "'");
    if (str.contains(".Sq")) {
      str = replaceWordQuote(str, ".Sq", "'", "'");
    }
    if (str.contains("Sq")) {
      str = replaceWordQuote(str, "Sq", "'", "'");
    }

    if (str.contains(".Dq")) {
      str = replaceSentenceBracket(str, ".Dq", "''", "''");
    }
    if (str.contains("Op")) {
      str = replaceSentenceBracket(str, "Op", "[", "]");
    }
    if (str.contains(".Pq")) {
      str = replaceSentenceBracket(str, ".Pq", "(", ")");
    }

    if (str.contains(".Xr")) {
      str = replaceFunctionParen(str, ".Xr", "(", ")");
    }

    str = str.replaceAll(" Ns = Ns ", "=");
    str = str.replaceAll(" +", " ");
    str = str.replaceAll(" , ?", ", ");
    str = str.replaceAll(" /.", ". ");
    return str;
  }

  public static String replaceWordQuote(String str, String qut, String leftsign, String rightsign) {
    // if (str.contains(qut)) {
    String result = new String();
    String[] strArr = str.split(" ");
    for (int i = 0; i < strArr.length; i++) {
      if (strArr[i] == null)
        continue;
      if (strArr[i].equals(qut)) {
        strArr[i] = leftsign + strArr[i + 1] + rightsign;
        strArr[i + 1] = null;
      }
      result += strArr[i] + " ";
    }
    return result;
    // } else return str;
  }

  public static String replaceSentenceBracket(String str, String brk, String leftsign,
      String rightsign) {
    // if (str.contains(brk)) {
    String[] strArr = str.split(" ");
    String resultStr = new String();
    int bracketCnt = 0;
    for (int i = 0; i < strArr.length; i++) {
      if (strArr[i] == null)
        continue;
      if (strArr[i].equals(brk)) {
        strArr[i] = leftsign + strArr[i + 1];
        ++bracketCnt;
        strArr[i + 1] = null;
        resultStr += " " + strArr[i];
      } else {
        resultStr += " " + strArr[i];
      }
    }
    for (int i = 0; i < bracketCnt; i++) {
      resultStr += rightsign;
    }
    return resultStr;
  }

  public static String replaceFunctionParen(String str, String par, String leftsign,
      String rightsign) {
    String result = new String();
    String[] strArr = str.split(" ");
    for (int i = 0; i < strArr.length; i++) {
      if (strArr[i] == null)
        continue;
      if (strArr[i].equals(par)) {
        strArr[i] = strArr[i + 1] + leftsign + strArr[i + 2] + rightsign;
        strArr[i + 1] = null;
        strArr[i + 2] = null;
      }
      result += strArr[i] + " ";
    }
    return result;
  }
}