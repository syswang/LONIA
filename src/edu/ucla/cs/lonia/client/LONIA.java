package edu.ucla.cs.lonia.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cs.lonia.client.widget.CSVEditor;
import edu.ucla.cs.lonia.client.widget.NaviBar;
import edu.ucla.cs.lonia.client.widget.NaviBar.NaviBarClickListener;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LONIA implements EntryPoint, NaviBarClickListener, ValueChangeHandler<String> {
  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  public static final String HISTORY_INIT_TOKEN = "initstate";

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

  private NaviBar naviBar;

  private CSVEditor csvEditor = null;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    initHistorySupport();
    initAllWidgets();

    //RootPanel.get().add(new CSVEditor());
  }

  private CSVEditor getCSVEditor() {
    if (csvEditor == null) {
      csvEditor = new CSVEditor();
    }
    return csvEditor;
  }

  private void initAllWidgets() {
    naviBar = new NaviBar();
    naviBar.setNaviBarClickListener(this);

    RootPanel.get().add(naviBar);
  }

  public void initHistorySupport() {
    // add the MainPanel as a history listener
    History.addValueChangeHandler(this);
    // check to see if there are any tokens passed at startup via the browser's URI
    String token = History.getToken();
    if (token.length() == 0) {
      History.newItem(HISTORY_INIT_TOKEN);
    }
    History.fireCurrentHistoryState();
  }

  @Override
  public void onLinkClicked(Widget source, String historyToken) {
    History.newItem(historyToken);
  }

  @Override
  public void onValueChange(ValueChangeEvent<String> event) {
    String historyToken = event.getValue();

    // Parse the history token
    if (historyToken.equals(NaviBar.PARSER_HISTORY_TOKEN)) {
      loadParser();
    } else if (historyToken.equals(NaviBar.HOME_HISTORY_TOKEN)) {
      loadHome();
    } else if (historyToken.equals(NaviBar.OVERVIEW_HISTORY_TOKEN)) {
      loadOverview();
    }
  }

  private void loadParser() {
    RootPanel.get().add(getCSVEditor());
    //RootPanel.get().add(new Buttons());
  }
  
  private void loadHome() {
    RootPanel.get().remove(getCSVEditor());
  }
  
  private void loadOverview() {
    RootPanel.get().remove(getCSVEditor());
  }
}
