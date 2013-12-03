package edu.ucla.cs.lonia.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cs.lonia.client.model.ParseResult;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
  void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

  void sendToServer(ParseResult input, AsyncCallback<ParseResult> callback)
      throws IllegalArgumentException;
}
