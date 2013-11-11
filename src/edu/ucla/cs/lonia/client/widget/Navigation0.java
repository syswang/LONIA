/*
 *  Copyright 2012 GWT-Bootstrap
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.ucla.cs.lonia.client.widget;

import java.util.Arrays;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabLink;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TabPanel.ShowEvent;
import com.github.gwtbootstrap.client.ui.TabPanel.ShownEvent;
import com.github.gwtbootstrap.client.ui.ValueListBox;
import com.github.gwtbootstrap.client.ui.base.ProgressBarBase;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cs.lonia.client.util.EnumRenderer;

public class Navigation0 extends Composite {

    @UiField
    NavLink navlink1;

    @UiField
    NavLink navlink2;
    
    @UiField
    TabLink lazyLoadTab;
    
    @UiField(provided=true)
    ValueListBox<Bootstrap.Tabs> tabPosition = new ValueListBox<Bootstrap.Tabs>(new EnumRenderer<Bootstrap.Tabs>());
    
    @UiField
    TabPanel tabPanel;
    
    @UiField
    Tab firstTab;
    
    @UiField
    TabPanel utilitiesTab;
    
    @UiField
    Button cancelButton;

    private static NavigationEntriesUiBinder uiBinder = GWT.create(NavigationEntriesUiBinder.class);

    interface NavigationEntriesUiBinder extends UiBinder<Widget, Navigation0> {
    }

    public Navigation0() {
        
        tabPosition.setValue(Tabs.LEFT);
        
        tabPosition.setAcceptableValues(Arrays.asList(Tabs.values()));
        
        initWidget(uiBinder.createAndBindUi(this));
        ClickHandler handler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("ClickHandler navlink example.");
            }
        };
        navlink1.addClickHandler(handler);
        navlink2.addClickHandler(handler);
    }
    
    @UiHandler("lazyLoadTab")
    public void onClickLazyLoadTab(ClickEvent e) {
        final TabPane tabPane = lazyLoadTab.getTabPane();
        tabPane.clear();
        final ProgressBar progressBar = new ProgressBar();
        progressBar.setPercent(0);
        progressBar.setType(ProgressBarBase.Style.ANIMATED);
        tabPane.add(progressBar);
        
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            
            @Override
            public boolean execute() {
                
                if(progressBar.getPercent() != 100) {
                    progressBar.setPercent(progressBar.getPercent() + 10);
                    return true;
                }
                
                tabPane.clear();
                tabPane.add(new Label(LabelType.INFO,"loaded"));
                return false;
            }
        }, 1000);
        
    }
    
    @UiHandler("tabPosition")
    void onChangeTabPosition(ValueChangeEvent<Tabs> e) {
        tabPanel.setTabPosition(e.getValue().name().toLowerCase());
    }
    
    @UiField Button removeTab;
    @UiHandler("removeTab")
    void onClickRemoveTab(ClickEvent e) {
        if(firstTab.asTabLink().isActive()) tabPanel.remove(firstTab);
        removeTab.setEnabled(false);
    }
    
    @UiHandler({
        "tabButton1",
        "tabButton2",
        "tabButton3",
        "tabButton4",
        "tabButton5",
        "tabButton6",
        "tabButton7"
    })
    void onClickTabButtons(ClickEvent e) {
        AnchorElement anchor = e.getRelativeElement().cast();
        utilitiesTab.selectTab(Integer.parseInt(anchor.getInnerText().trim()) -1);
    }
    
    @UiHandler("utilitiesTab")
    void onShow(ShowEvent e) {
        if(cancelButton.isActive()) {
            e.preventDefault();
        }
    }

    @UiHandler("utilitiesTab")
    void onShown(ShownEvent e) {
        Window.alert("Change tab from " + e.getRelatedTarget().getText() + " to " + e.getTarget().getText() + ".");
    }

}
