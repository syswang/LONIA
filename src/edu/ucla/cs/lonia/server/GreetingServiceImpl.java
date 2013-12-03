package edu.ucla.cs.lonia.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ucla.cs.lonia.client.GreetingService;
import edu.ucla.cs.lonia.client.model.ParseResult;
import edu.ucla.cs.lonia.client.parser.ResultRow;
import edu.ucla.cs.lonia.shared.FieldVerifier;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

  public Map<String, ArrayList<ResultRow>> prs = new HashMap<String, ArrayList<ResultRow>>();

  public ParseResult sendToServer(ParseResult result) throws IllegalArgumentException {
    // Entity pr = new Entity("ParseResult", result.getKey());
    // pr.setProperty("result", result.getValue());
    //
    // DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    // datastore.put(pr);
    //
    // Query query = new Query(result.getKey());
    //
    // Entity data = datastore.prepare(query).asSingleEntity();
    // ParseResult rst = (ParseResult) data.getProperty("ParseResult");
    // return rst;
    if (result.getValue() == null) {
      // TODO get parsed result by key
      if (prs.get(result.getKey()) != null) {
        result.setValue(prs.get(result.getKey()));
      }
    } else {
      // TODO put parsed result
      prs.put(result.getKey(), result.getValue());
    }
    return result;
  }

  public String greetServer(String input) throws IllegalArgumentException {
    // Verify that the input is valid.
    if (!FieldVerifier.isValidName(input)) {
      // If the input is not valid, throw an IllegalArgumentException back to
      // the client.
      throw new IllegalArgumentException("Name must be at least 4 characters long");
    }

    String serverInfo = getServletContext().getServerInfo();
    String userAgent = getThreadLocalRequest().getHeader("User-Agent");

    // Escape data from the client to avoid cross-site script vulnerabilities.
    input = escapeHtml(input);
    userAgent = escapeHtml(userAgent);

    return "Hello, " + input + "!<br><br>I am running " + serverInfo
        + ".<br><br>It looks like you are using:<br>" + userAgent;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to prevent cross-site
   * script vulnerabilities.
   * 
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }
}
