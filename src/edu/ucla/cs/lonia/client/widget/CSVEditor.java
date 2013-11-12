package edu.ucla.cs.lonia.client.widget;

import java.util.Arrays;
import java.util.Comparator;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Form.SubmitEvent;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.TooltipCellDecorator;
import com.github.gwtbootstrap.client.ui.ValueListBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.ucla.cs.lonia.client.resources.CustomResources;
import edu.ucla.cs.lonia.client.util.DisplayLabelRenderer;
import edu.ucla.cs.lonia.client.util.Parameter;
import edu.ucla.cs.lonia.client.util.Parameter.PType;
import edu.ucla.cs.lonia.client.util.Parameter.State;

public class CSVEditor extends Composite implements Editor<Parameter> {

  public static final class TableColumName {
    static final String ID = "#";
    static final String PARAMETER_NAME = "Parameter Name";
    static final String DESCRIPTION = "Description";
    static final String TYPE = "Type";
    static final String STATE = "State";
  };

  SimpleEditor<Integer> id = SimpleEditor.of();

  @UiField
  TextBox name;

  @UiField
  IntegerBox description;

  @UiField
  ControlGroup descriptionControlGroup;

  @UiField
  @Editor.Ignore
  HelpInline descriptionHelpInline;

  @UiField
  ControlGroup nameControlGroup;

  @UiField
  @Editor.Ignore
  HelpInline nameHelpInline;

  @UiField(provided = true)
  ValueListBox<Parameter.State> state;

  @UiField(provided = true)
  DataGrid<Parameter> csvDataGrid = new DataGrid<Parameter>(20, GWT
      .<DataGrid.SelectableResources> create(DataGrid.SelectableResources.class));

  @UiField
  SubmitButton saveButton;

  @UiField(provided = true)
  ValueListBox<PType> type;

  // @UiField
  Pagination pagination = new Pagination();

  @UiField
  Pagination dataGridPagination;

  @UiField
  Form submitExampleForm;

  @UiField
  Modal editModal;

  @UiField
  Button displayManReader;

  @UiField
  Tab manSourceTab;

  @UiField
  com.github.gwtbootstrap.client.ui.Column leftPanel;
  
  @UiField
  com.github.gwtbootstrap.client.ui.Column mainPanel;
  
  
  SimplePager dataGridPager = new SimplePager();

  ListDataProvider<Parameter> dataProvider = new ListDataProvider<Parameter>();

  private static CSVEditorUiBinder uiBinder = GWT.create(CSVEditorUiBinder.class);

  interface CSVEditorUiBinder extends UiBinder<Widget, CSVEditor> {
  }

  interface Driver extends SimpleBeanEditorDriver<Parameter, CSVEditor> {
  }

  Driver driver = GWT.create(Driver.class);

  public CSVEditor() {

    type = new ValueListBox<PType>(new DisplayLabelRenderer<PType>());

    type.setAcceptableValues(Arrays.asList(PType.values()));

    state = new ValueListBox<Parameter.State>(new DisplayLabelRenderer<Parameter.State>());

    initWidget(uiBinder.createAndBindUi(this));

    driver.initialize(this);

    setPerson(new Parameter());

    state.setAcceptableValues(Arrays.asList(State.values()));

    initTable(csvDataGrid, dataGridPager, dataGridPagination);

    onClickAdd5Entity();

    displayManReader.setToggle(true);

    manSourceTab.add(new HTML("<pre class=\"prettyprint linenums pre-scrollable\">"
        + CustomResources.RESOURCES.manSourceDemo().getText() + "</pre>"));
    
    changeLayout(displayManReader.isToggled());
  }

  private void initTable(AbstractCellTable<Parameter> csvTable, final SimplePager pager,
      final Pagination pagination) {
    csvTable.setEmptyTableWidget(new Label("Please add data."));

    TextColumn<Parameter> idCol = new TextColumn<Parameter>() {

      @Override
      public String getValue(Parameter object) {
        return String.valueOf(object.getId());
      }
    };

    idCol.setSortable(true);

    csvTable.addColumn(idCol, TableColumName.ID);

    ListHandler<Parameter> idColHandler = new ListHandler<Parameter>(dataProvider.getList());

    idColHandler.setComparator(idCol, new Comparator<Parameter>() {

      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });

    csvTable.addColumnSortHandler(idColHandler);

    csvTable.getColumnSortList().push(idCol);

    // // test
    // EditTextCell etc = new EditTextCell();
    // etc.
    // Column<Person, String> firstNameColumn = new Column<Person, String>(etc) {
    // @Override
    // public String getValue(Person object) {
    // return object.getName();
    // }
    // };
    // firstNameColumn.setSortable(true);
    // firstNameColumn.setFieldUpdater(new FieldUpdater<Person, String>() {
    // @Override
    // public void update(int index, Person object, String value) {
    // // Called when the user changes the value.
    // object.setName(value);
    // dataProvider.refresh();
    // }
    // });
    //
    // exampleTable.addColumn(firstNameColumn, "First Name");
    // ListHandler<Person> firstNameColHandler = new ListHandler<Person>(dataProvider.getList());
    //
    // firstNameColHandler.setComparator(firstNameColumn, new Comparator<Person>() {
    //
    // @Override
    // public int compare(Person o1, Person o2) {
    // return o1.getName().compareTo(o2.getName());
    // }
    // });
    //
    // exampleTable.addColumnSortHandler(firstNameColHandler);

    TextColumn<Parameter> paraNameCol = new TextColumn<Parameter>() {

      @Override
      public String getValue(Parameter object) {
        return object.getName();
      }
    };
    paraNameCol.setSortable(true);
    csvTable.addColumn(paraNameCol, TableColumName.PARAMETER_NAME);

    ListHandler<Parameter> paraNameColHandler = new ListHandler<Parameter>(dataProvider.getList());

    paraNameColHandler.setComparator(paraNameCol, new Comparator<Parameter>() {

      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

    csvTable.addColumnSortHandler(paraNameColHandler);

    //
    TextColumn<Parameter> descriptionCol = new TextColumn<Parameter>() {

      @Override
      public String getValue(Parameter object) {
        return object.getDescription() != null ? String.valueOf(object.getDescription()) : "";
      }
    };
    descriptionCol.setSortable(true);
    csvTable.addColumn(descriptionCol, TableColumName.DESCRIPTION);

    ListHandler<Parameter> descriptionColHandler =
        new ListHandler<Parameter>(dataProvider.getList());

    descriptionColHandler.setComparator(descriptionCol, new Comparator<Parameter>() {

      @Override
      public int compare(Parameter o1, Parameter o2) {
        if (o2.getDescription() == null) {
          return 1;
        }

        if (o1.getDescription() == null) {
          return -1;
        }
        return o1.getDescription().compareTo(o2.getDescription());
      }
    });

    csvTable.addColumnSortHandler(descriptionColHandler);

    //
    TextColumn<Parameter> typeCol = new TextColumn<Parameter>() {

      @Override
      public String getValue(Parameter object) {
        return object.getType().getDisplayLabel();
      }
    };

    typeCol.setSortable(true);
    csvTable.addColumn(typeCol, TableColumName.TYPE);

    ListHandler<Parameter> paraTypeColHandler = new ListHandler<Parameter>(dataProvider.getList());

    paraTypeColHandler.setComparator(typeCol, new Comparator<Parameter>() {

      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getType().compareTo(o2.getType());
      }
    });

    csvTable.addColumnSortHandler(paraTypeColHandler);

    //
    TextColumn<Parameter> stateCol = new TextColumn<Parameter>() {

      @Override
      public String getValue(Parameter object) {
        return object.getState().getDisplayLabel();
      }
    };

    stateCol.setSortable(true);
    csvTable.addColumn(stateCol, TableColumName.STATE);

    ListHandler<Parameter> stateColHandler = new ListHandler<Parameter>(dataProvider.getList());

    stateColHandler.setComparator(stateCol, new Comparator<Parameter>() {

      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getState().compareTo(o2.getState());
      }
    });

    csvTable.addColumnSortHandler(stateColHandler);

    //
    csvTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {

      @Override
      public void onRangeChange(RangeChangeEvent event) {
        rebuildPager(pagination, pager);
      }
    });

    ButtonCell buttonCell = new ButtonCell(IconType.REMOVE, ButtonType.DANGER);

    final TooltipCellDecorator<String> decorator = new TooltipCellDecorator<String>(buttonCell);
    decorator.setText("delete row, if click");

    Column<Parameter, String> buttonCol = new Column<Parameter, String>(decorator) {

      @Override
      public String getValue(Parameter object) {
        return "delete";
      }
    };

    buttonCol.setFieldUpdater(new FieldUpdater<Parameter, String>() {

      @Override
      public void update(int index, Parameter object, String value) {
        dataProvider.getList().remove(object);
        dataProvider.flush();
        dataProvider.refresh();
        rebuildPager(pagination, pager);
        rebuildPager(dataGridPagination, dataGridPager);

      }
    });

    csvTable.addColumn(buttonCol);

    final SingleSelectionModel<Parameter> selectionModel = new SingleSelectionModel<Parameter>();

    selectionModel.addSelectionChangeHandler(new Handler() {

      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        Parameter person = selectionModel.getSelectedObject();
        CSVEditor.this.driver.edit(person);
      }
    });

    csvTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

    csvTable.setSelectionModel(selectionModel);

    pager.setDisplay(csvTable);

    pagination.clear();

    dataProvider.addDataDisplay(csvTable);
  }

  @UiHandler("description")
  public void onDescriptionUpdate(KeyPressEvent event) {

    if (event.getCharCode() < '0' || event.getCharCode() > '9') {
      descriptionControlGroup.setType(ControlGroupType.ERROR);
      descriptionHelpInline.setText("input a number for test.");

      event.preventDefault();
    } else {
      descriptionControlGroup.setType(ControlGroupType.NONE);
      descriptionHelpInline.setText("");

    }

  }

  @UiHandler("submitExampleForm")
  public void onSubmitForm(SubmitEvent e) {

    Parameter person = driver.flush();

    boolean hasError = false;

    if (person.getName() == null || person.getName().isEmpty()) {

      nameControlGroup.setType(ControlGroupType.ERROR);
      nameHelpInline.setText("paraName should be input");
      hasError = true;
    }

    if (person.getDescription() == null) {
      descriptionControlGroup.setType(ControlGroupType.ERROR);
      descriptionHelpInline.setText("input a number for test.");
      hasError = true;
    }

    if (hasError) {
      e.cancel();
      return;
    }

    addPerson(person);

    setPerson(new Parameter());
    e.cancel();
  }

  private void addPerson(Parameter person) {
    if (person.getId() == null) {
      person.setId(dataProvider.getList().size() + 1);
      dataProvider.getList().add(person);
    } else {
      csvDataGrid.getSelectionModel().setSelected(person, false);
      dataProvider.refresh();
    }

    dataProvider.flush();

    rebuildPager(dataGridPagination, dataGridPager);

  }

  private void rebuildPager(final Pagination pagination, final SimplePager pager) {
    pagination.clear();

    if (pager.getPageCount() == 0) {
      return;
    }

    NavLink prev = new NavLink("<");

    prev.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        GWT.log(String.valueOf("prev"));
        pager.previousPage();
      }
    });

    prev.setDisabled(!pager.hasPreviousPage());

    pagination.add(prev);

    int before = 2;
    int after = 2;

    while (!pager.hasPreviousPages(before) && before > 0) {
      before--;
      if (pager.hasNextPages(after + 1)) {
        after++;
      }
    }

    while (!pager.hasNextPages(after) && after > 0) {
      after--;
      if (pager.hasPreviousPages(before + 1)) {
        before++;
      }
    }

    for (int i = pager.getPage() - before; i <= pager.getPage() + after; i++) {

      final int index = i + 1;

      NavLink page = new NavLink(String.valueOf(index));

      page.addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          pager.setPage(index - 1);
        }
      });

      if (i == pager.getPage()) {
        page.setActive(true);
      }

      pagination.add(page);
    }

    NavLink next = new NavLink(">");

    next.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        GWT.log(String.valueOf("next"));
        pager.nextPage();
      }
    });

    next.setDisabled(!pager.hasNextPage());

    pagination.add(next);
  }

  public void onClickAdd5Entity() {

    for (int i = 0; i < 15; i++) {

      Parameter p = new Parameter();
      p.setDescription(csvDataGrid.getRowCount());
      p.setState(State.values()[Random.nextInt(State.values().length)]);
      p.setName("paraName" + csvDataGrid.getRowCount());
      p.setType(PType.values()[Random.nextInt(PType.values().length)]);
      addPerson(p);
    }

  }

  public void setPerson(Parameter person) {
    driver.edit(person);

    descriptionControlGroup.setType(ControlGroupType.NONE);
    descriptionHelpInline.setText("");

    nameControlGroup.setType(ControlGroupType.NONE);
    nameHelpInline.setText("");
  }

  @UiHandler("cancelButton")
  public void onCancelClick(ClickEvent e) {
    submitExampleForm.reset();
    editModal.hide();

  }

  @UiHandler("editRow")
  void onClickEditRow(ClickEvent event) {
    editModal.show();
  }

  //
  // @UiHandler("displayManReader")
  // void onAddClick(ClickEvent event) {
  // editModal.show();
  // }

  @UiHandler("displayManReader")
  void onClickDisplayManReader(ClickEvent event) {
    displayManReader.setFocus(false);
    changeLayout(!displayManReader.isToggled());
  }
  
  void changeLayout(boolean showLeftPanel) {
    if (showLeftPanel) {
      leftPanel.setVisible(true);
      mainPanel.setSize(8);
    } else {
      leftPanel.setVisible(false);
      mainPanel.setSize(12);
    }
  }
  //
  // @UiField("manSource")
  // manSource

}