package edu.ucla.cs.lonia.client.widget;

import com.github.gwtbootstrap.client.ui.Brand;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Navbar;
import com.github.gwtbootstrap.client.ui.constants.NavbarPosition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NaviBar extends Composite implements ClickHandler {
  
  public final static String HOME_HISTORY_TOKEN = "home"; 
  
  public final static String OVERVIEW_HISTORY_TOKEN = "overview";
  
  public final static String PARSER_HISTORY_TOKEN = "parser"; 

  private static NaviBarUiBinder uiBinder = GWT.create(NaviBarUiBinder.class);

  interface NaviBarUiBinder extends UiBinder<Widget, NaviBar> {
  }
  
  public interface NaviBarClickListener {
    void onLinkClicked(Widget sender, String historyToken);
  }

  @UiField
  Navbar topNavBar;
  
  @UiField
  NavLink overview;
  
  @UiField
  NavLink lonipipeline;
  
  @UiField
  NavLink parser;
  
  @UiField
  Brand manparser;
  
  NaviBarClickListener clickListener = null;
  
  
  public NaviBar() {
    initWidget(uiBinder.createAndBindUi(this));
    
    // init style
    topNavBar.setInverse(true);
    topNavBar.setPosition(NavbarPosition.TOP);
    
    overview.addClickHandler(this);
    parser.addClickHandler(this);
    lonipipeline.addClickHandler(this);
    manparser.addClickHandler(this);
  }
  
  public void setNaviBarClickListener(NaviBarClickListener listener) {
    this.clickListener = listener;
  }

  @Override
  public void onClick(ClickEvent event) {
    Widget sender = (Widget) event.getSource();
    if (this.clickListener != null) {
      String token = null;
      if (sender == overview.getAnchor()) {
        token = OVERVIEW_HISTORY_TOKEN;  
        overview.setActive(true);
        parser.setActive(false);
      } else if (sender == parser.getAnchor()) {
        token = PARSER_HISTORY_TOKEN;
        overview.setActive(false);
        parser.setActive(true);
      } else if (sender == lonipipeline.getAnchor()) {
        
      } else if (sender == manparser) {
        token = HOME_HISTORY_TOKEN;
      }
      clickListener.onLinkClicked(sender, token);
    }
  }

}
