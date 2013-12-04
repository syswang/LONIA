package edu.ucla.cs.lonia.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Form.SubmitEvent;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.ValueListBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.ucla.cs.lonia.client.GreetingService;
import edu.ucla.cs.lonia.client.GreetingServiceAsync;
import edu.ucla.cs.lonia.client.model.CustomSelectionCell;
import edu.ucla.cs.lonia.client.model.DroppableEditTextCell;
import edu.ucla.cs.lonia.client.model.Parameter;
import edu.ucla.cs.lonia.client.model.ParseResult;
import edu.ucla.cs.lonia.client.model.Parameter.PType;
import edu.ucla.cs.lonia.client.model.Parameter.State;
import edu.ucla.cs.lonia.client.parser.ManuFileParser;
import edu.ucla.cs.lonia.client.parser.ResultRow;
import edu.ucla.cs.lonia.client.util.DisplayLabelRenderer;

public class CSVEditor extends Composite implements Editor<Parameter> {

  public static final class TableColumName {
    static final String ID = "#";
    static final String PARAMETER_NAME = "Parameter Name";
    static final String DESCRIPTION = "Description";
    static final String TYPE = "Type";
    static final String STATE = "State";
    static final String PREFIX = "Prefix";
    static final String CARDINALITY = "Cardinality";
    static final String REQUIRED = "Required";
  };

  SimpleEditor<Integer> id = SimpleEditor.of();

  @UiField
  TextBox name;

  @UiField
  TextBox description;

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

  @UiField
  Pagination dataGridPagination;

  @UiField
  Form submitExampleForm;

  @UiField
  Form submitFileForm;

  @UiField
  Modal editModal;

  @UiField
  Modal browseFileModal;

  @UiField
  Tab manSourceTab;

  @UiField
  @Editor.Ignore
  AlertBlock uploadAlert;

  @UiField
  @Editor.Ignore
  AlertBlock previewImport;

  @UiField
  @Editor.Ignore
  Paragraph previewText;

  @UiField
  com.github.gwtbootstrap.client.ui.Column mainPanel;

  @UiField
  @Editor.Ignore
  Label dragTest;

  @UiField
  @Editor.Ignore
  Button addRow;

  @UiField
  @Editor.Ignore
  Button editRow;

  @UiField
  @Editor.Ignore
  Button deleteRow;
  @UiField
  @Editor.Ignore
  SplitDropdownButton exportBtn;

  @UiField
  @Editor.Ignore
  TextArea textArea;

  @UiField
  @Editor.Ignore
  Button parse;

  @UiField
  @Editor.Ignore
  FileUpload fileUpload;

  @UiField
  @Editor.Ignore
  SubmitButton uploadFile;

  AlertBlock ab = null;

  String fileContent = null;

  SimplePager dataGridPager = new SimplePager();

  @Editor.Ignore
  HTML prettyCode = null;

  ListDataProvider<Parameter> dataProvider = new ListDataProvider<Parameter>();

  SingleSelectionModel<Parameter> selectionModel;

  private static CSVEditorUiBinder uiBinder = GWT.create(CSVEditorUiBinder.class);

  interface CSVEditorUiBinder extends UiBinder<Widget, CSVEditor> {
  }

  interface Driver extends SimpleBeanEditorDriver<Parameter, CSVEditor> {
  }

  Driver driver = GWT.create(Driver.class);

  interface Template extends SafeHtmlTemplates {
    @Template("<label>{0}</label><br/>Drop to cell in the table")
    SafeHtml display(String draggedText);
  }

  private static Template template = GWT.create(Template.class);;

  public CSVEditor() {

    injectJsAndJsniBinding(this);

    type = new ValueListBox<PType>(new DisplayLabelRenderer<PType>());

    type.setAcceptableValues(Arrays.asList(PType.values()));

    state = new ValueListBox<Parameter.State>(new DisplayLabelRenderer<Parameter.State>());

    initWidget(uiBinder.createAndBindUi(this));

    driver.initialize(this);

    setPerson(new Parameter());

    state.setAcceptableValues(Arrays.asList(State.values()));

    initTable(csvDataGrid, dataGridPager, dataGridPagination);

    parse.setEnabled(false);
    addRow.setEnabled(false);
    deleteRow.setEnabled(false);
    editRow.setEnabled(false);

    prettyCode = new HTML();
    manSourceTab.add(prettyCode);

    textArea.setWidth("98%");
    textArea.setHeight("200px");
    textArea.setText("");
    textArea
        .setPlaceholder("Please paste text here, or import a file by clicking import button below.");
    // textArea.setText(CustomResources.RESOURCES.manFileText().getText());
    textArea.addDragStartHandler(new DragStartHandler() {

      @Override
      public void onDragStart(DragStartEvent event) {
        // event.preventDefault();
        DataTransfer dt = event.getDataTransfer();
        dt.setData("DraggedText", textArea.getSelectedText());
        if (ab == null) {
          ab = new AlertBlock();
          ab.setAnimation(true);
          ab.setType(AlertType.INFO);
          ab.getElement().setId("dragHelper");
          ab.getElement().getStyle().setFontSize(20, Unit.PX);
          ab.getElement().getStyle().setWidth(100, Unit.PX);
          ab.getElement().getStyle().setHeight(25, Unit.PX);
          CSVEditor.this.getElement().appendChild(ab.getElement());
        }
        String text = textArea.getSelectedText();
        if (text.length() > 20) {
          text = text.substring(0, 20) + "...";
        }
        ab.setText(text);

        dt.setDragImage(ab.getElement(), -5, -5);
      }

    });
  }

  public native void JsPrettyPrint() /*-{
		$wnd.prettyPrint();
		prettyPrint();
  }-*/;

  public native void JsExportCsvFile(String text, String ext) /*-{
		var a = document.createElement('a');
		a.href = 'data:attachment/' + ext + "," + encodeURIComponent(text);
		a.target = '_blank';
		a.download = 'result.' + ext;
		document.body.appendChild(a);
		a.click();
  }-*/;

  public native void injectJsAndJsniBinding(CSVEditor editor) /*-{
		$wnd.updateDataGrid = function(text, a, b) {
			editor.@edu.ucla.cs.lonia.client.widget.CSVEditor::updateDataGrid(Ljava/lang/String;Ljava/lang/String;I)(text,a,b);
		}
		$wnd.allowDrop = function(event) {
			event.preventDefault();
		}
		$wnd.drop = function(ev, x) {
			ev.preventDefault();
			var data = ev.dataTransfer.getData("DraggedText");
			var childArray = x.children;
			var a = childArray[0].innerHTML;
			var b = childArray[1].innerHTML;
			// note, must use $wnd.myfunction to call myfunction, not window.myfunction
			$wnd.updateDataGrid(data, a, parseInt(b, 10));
		}
		$wnd.drag = function(ev) {
			alert("dragged!");
		}
		$wnd.readText = function(that) {
			if (that.files && that.files[0]) {
				var reader = new FileReader();
				reader.onload = function(e) {
					var output = e.target.result;
					var filename = that.files[0].name;
					editor.@edu.ucla.cs.lonia.client.widget.CSVEditor::setUploadTextPreview(Ljava/lang/String;Ljava/lang/String;)(output, filename);
					//process text to show only lines with "@":       
					//output=output.split("\n").filter(/./.test, /\@/).join("\n");
				};
				reader.readAsText(that.files[0]);
			}
		}
  }-*/;

  public void updateDataGrid(String text, String a, int b) {
    // Window.alert(text + Integer.toString(a) + " " + Integer.toString(b));
    assert (b >= 0 && b < this.dataProvider.getList().size());
    Parameter p = this.dataProvider.getList().get(b);
    if (text != null && a != null) {
      if (a.equals(TableColumName.PARAMETER_NAME)) {
        p.setName(text);
      } else if (a.equals(TableColumName.DESCRIPTION)) {
        p.setDescription(text);
      } else if (a.equals(TableColumName.PREFIX)) {
        p.setPrefix(text);
      }
    }
    this.dataProvider.getList().set(b, p);
  }

  private void initTable(AbstractCellTable<Parameter> csvTable, final SimplePager pager,
      final Pagination pagination) {
    csvTable.setEmptyTableWidget(new Label("Please add data."));

    // Name
    DroppableEditTextCell etc = new DroppableEditTextCell(TableColumName.PARAMETER_NAME);

    Column<Parameter, String> firstNameColumn = new Column<Parameter, String>(etc) {
      @Override
      public String getValue(Parameter object) {
        return object.getName();
      }
    };
    firstNameColumn.setSortable(true);
    ListHandler<Parameter> paraNameColHandler = new ListHandler<Parameter>(dataProvider.getList());
    paraNameColHandler.setComparator(firstNameColumn, new Comparator<Parameter>() {
      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    firstNameColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
      @Override
      public void update(int index, Parameter object, String value) {
        // Called when the user changes the value.
        object.setName(value);
        CSVEditor.this.driver.edit(object);
        dataProvider.refresh();
        rebuildPager(dataGridPagination, dataGridPager);
      }
    });
    csvTable.addColumn(firstNameColumn, TableColumName.PARAMETER_NAME);
    csvTable.setColumnWidth(firstNameColumn, 10, Unit.PCT);
    csvTable.addColumnSortHandler(paraNameColHandler);

    // Description
    Column<Parameter, String> descriptionCol =
        new Column<Parameter, String>(new DroppableEditTextCell(TableColumName.DESCRIPTION)) {
          @Override
          public String getValue(Parameter object) {
            return object.getDescription();
          }
        };
    descriptionCol.setSortable(true);
    ListHandler<Parameter> descriptionColHandler =
        new ListHandler<Parameter>(dataProvider.getList());
    descriptionColHandler.setComparator(descriptionCol, new Comparator<Parameter>() {
      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getDescription().compareTo(o2.getDescription());
      }
    });
    descriptionCol.setFieldUpdater(new FieldUpdater<Parameter, String>() {
      @Override
      public void update(int index, Parameter object, String value) {
        // Called when the user changes the value.
        object.setDescription(value);
        CSVEditor.this.driver.edit(object);
        dataProvider.refresh();
        rebuildPager(dataGridPagination, dataGridPager);
      }
    });
    csvTable.addColumn(descriptionCol, TableColumName.DESCRIPTION);
    csvTable.setColumnWidth(descriptionCol, 20, Unit.PCT);
    csvTable.addColumnSortHandler(descriptionColHandler);

    // PType
    final List<PType> ptypes = Arrays.asList(PType.values());
    List<String> ptypeNames = new ArrayList<String>();
    for (PType ptype : ptypes) {
      ptypeNames.add(ptype.getDisplayLabel());
    }
    CustomSelectionCell typeCell = new CustomSelectionCell(ptypeNames);
    Column<Parameter, String> ptypeColumn = new Column<Parameter, String>(typeCell) {
      @Override
      public String getValue(Parameter object) {
        return object.getType().getDisplayLabel();
      }
    };
    ptypeColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
      @Override
      public void update(int index, Parameter object, String value) {
        for (PType ptype : ptypes) {
          if (ptype.getDisplayLabel().equals(value)) {
            object.setType(ptype);
            dataProvider.getList().get(index).setType(ptype);
          }
        }
        dataProvider.refresh();
      }
    });
    ListHandler<Parameter> ptypeColumnHandler = new ListHandler<Parameter>(dataProvider.getList());
    ptypeColumnHandler.setComparator(ptypeColumn, new Comparator<Parameter>() {
      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getType().getDisplayLabel().compareTo(o2.getType().getDisplayLabel());
      }
    });
    csvTable.addColumn(ptypeColumn, TableColumName.TYPE);
    csvTable.setColumnWidth(ptypeColumn, 10, Unit.PCT);
    csvTable.addColumnSortHandler(ptypeColumnHandler);

    // State
    final List<State> states = Arrays.asList(State.values());
    List<String> stateNames = new ArrayList<String>();
    for (State state : states) {
      stateNames.add(state.getDisplayLabel());
    }
    CustomSelectionCell stateCell = new CustomSelectionCell(stateNames);
    Column<Parameter, String> stateColumn = new Column<Parameter, String>(stateCell) {
      @Override
      public String getValue(Parameter object) {
        return object.getType().getDisplayLabel();
      }
    };
    stateColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
      @Override
      public void update(int index, Parameter object, String value) {
        for (State state : states) {
          if (state.getDisplayLabel().equals(value)) {
            object.setState(state);
            dataProvider.getList().get(index).setState(state);
          }
        }
        dataProvider.refresh();
      }
    });
    ListHandler<Parameter> stateColumnHandler = new ListHandler<Parameter>(dataProvider.getList());
    stateColumnHandler.setComparator(stateColumn, new Comparator<Parameter>() {
      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getState().getDisplayLabel().compareTo(o2.getState().getDisplayLabel());
      }
    });
    csvTable.addColumn(stateColumn, TableColumName.STATE);
    csvTable.setColumnWidth(stateColumn, 10, Unit.PCT);
    csvTable.addColumnSortHandler(stateColumnHandler);

    // Prefix
    DroppableEditTextCell prefixCell = new DroppableEditTextCell(TableColumName.PREFIX);

    Column<Parameter, String> prefixColumn = new Column<Parameter, String>(prefixCell) {
      @Override
      public String getValue(Parameter object) {
        return object.getPrefix();
      }
    };
    prefixColumn.setSortable(true);
    ListHandler<Parameter> prefixColHandler = new ListHandler<Parameter>(dataProvider.getList());
    prefixColHandler.setComparator(prefixColumn, new Comparator<Parameter>() {
      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getPrefix().compareTo(o2.getPrefix());
      }
    });
    prefixColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
      @Override
      public void update(int index, Parameter object, String value) {
        // Called when the user changes the value.
        object.setPrefix(value);
        CSVEditor.this.driver.edit(object);
        dataProvider.refresh();
        rebuildPager(dataGridPagination, dataGridPager);
      }
    });
    csvTable.addColumn(prefixColumn, TableColumName.PREFIX);
    csvTable.setColumnWidth(prefixColumn, 10, Unit.PCT);
    csvTable.addColumnSortHandler(prefixColHandler);

    DroppableEditTextCell cardinCell = new DroppableEditTextCell(TableColumName.CARDINALITY);

    Column<Parameter, String> cardinColumn = new Column<Parameter, String>(cardinCell) {
      @Override
      public String getValue(Parameter object) {
        return object.getCardinality().toString();
      }
    };
    cardinColumn.setSortable(true);
    ListHandler<Parameter> cardinColHandler = new ListHandler<Parameter>(dataProvider.getList());
    cardinColHandler.setComparator(cardinColumn, new Comparator<Parameter>() {
      @Override
      public int compare(Parameter o1, Parameter o2) {
        return o1.getCardinality().compareTo(o2.getCardinality());
      }
    });
    cardinColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
      @Override
      public void update(int index, Parameter object, String value) {
        // Called when the user changes the value.
        Integer tmp = 0;
        try {
          tmp = Integer.valueOf(value);
        } catch (Exception e) {
          e.printStackTrace();
          Window.alert("Please input a number");
        }
        object.setCardinality(tmp);
        CSVEditor.this.driver.edit(object);
        dataProvider.refresh();
        rebuildPager(dataGridPagination, dataGridPager);
      }
    });
    csvTable.addColumn(cardinColumn, TableColumName.CARDINALITY);
    csvTable.setColumnWidth(cardinColumn, 10, Unit.PCT);
    csvTable.addColumnSortHandler(cardinColHandler);

    // Require
    Column<Parameter, Boolean> checkColumn =
        new Column<Parameter, Boolean>(new CheckboxCell(true, false)) {
          @Override
          public Boolean getValue(Parameter object) {
            // Get the value from the selection model.
            return object.getIsRequired();
          }
        };
    csvTable.addColumn(checkColumn, TableColumName.REQUIRED);
    csvTable.setColumnWidth(checkColumn, 10, Unit.PCT);
    // csvTable.setSelectionEnabled(true);

    selectionModel = new SingleSelectionModel<Parameter>();

    selectionModel.addSelectionChangeHandler(new Handler() {

      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        Parameter para = selectionModel.getSelectedObject();
        CSVEditor.this.driver.edit(para);
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

    // if (event.getCharCode() < '0' || event.getCharCode() > '9') {
    // descriptionControlGroup.setType(ControlGroupType.ERROR);
    // descriptionHelpInline.setText("input a number for test.");
    //
    // event.preventDefault();
    // } else {
    // descriptionControlGroup.setType(ControlGroupType.NONE);
    // descriptionHelpInline.setText("");
    //
    // }

  }

  @UiHandler("submitExampleForm")
  public void onSubmitForm(SubmitEvent e) {

    Parameter para = driver.flush();

    boolean hasError = false;

    if (para.getName() == null || para.getName().isEmpty()) {
      nameControlGroup.setType(ControlGroupType.ERROR);
      nameHelpInline.setText("Name should not be empty");
      hasError = true;
    }

    if (para.getDescription() == null) {
      descriptionControlGroup.setType(ControlGroupType.ERROR);
      descriptionHelpInline.setText("Description should not be empty");
      hasError = true;
    }

    if (hasError) {
      e.cancel();
      return;
    }

    addParameter(para);

    setPerson(new Parameter());
    e.cancel();
  }

  private void addParameter(Parameter para) {
    if (para.getId() == null) {
      para.setId(dataProvider.getList().size() + 1);
      dataProvider.getList().add(para);
    } else {
      csvDataGrid.getSelectionModel().setSelected(para, false);
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

    final NavLink prev = new NavLink("<");
    final NavLink next = new NavLink(">");
    final ArrayList<NavLink> pageNavs = new ArrayList<NavLink>();

    prev.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        GWT.log(String.valueOf("prev"));
        pager.previousPage();
        for (int i = 0; i < pageNavs.size(); i++) {
          pageNavs.get(i).setActive(false);
        }
        pageNavs.get(pager.getPage()).setActive(true);
        prev.setDisabled(!pager.hasPreviousPage());
        next.setDisabled(!pager.hasNextPage());
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

      final NavLink page = new NavLink(String.valueOf(index));

      page.addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          pager.setPage(index - 1);
          for (int i = 0; i < pageNavs.size(); i++) {
            pageNavs.get(i).setActive(false);
          }
          pageNavs.get(pager.getPage()).setActive(true);
          prev.setDisabled(!pager.hasPreviousPage());
          next.setDisabled(!pager.hasNextPage());
        }
      });

      if (i == pager.getPage()) {
        page.setActive(true);
      }
      pageNavs.add(page);
      pagination.add(page);
    }

    next.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        GWT.log(String.valueOf("next"));
        pager.nextPage();
        for (int i = 0; i < pageNavs.size(); i++) {
          pageNavs.get(i).setActive(false);
        }
        pageNavs.get(pager.getPage()).setActive(true);
        prev.setDisabled(!pager.hasPreviousPage());
        next.setDisabled(!pager.hasNextPage());
      }
    });

    next.setDisabled(!pager.hasNextPage());

    pagination.add(next);
  }

  public void onClickAdd5Entity() {

    for (int i = 0; i < 15; i++) {

      Parameter p = new Parameter();
      // p.setDescription(csvDataGrid.getRowCount());
      p.setState(State.values()[Random.nextInt(State.values().length)]);
      p.setName("paraName" + csvDataGrid.getRowCount());
      p.setType(PType.values()[Random.nextInt(PType.values().length)]);
      addParameter(p);
    }

  }

  public void setPerson(Parameter person) {
    driver.edit(person);

    descriptionControlGroup.setType(ControlGroupType.NONE);
    descriptionHelpInline.setText("");

    nameControlGroup.setType(ControlGroupType.NONE);
    nameHelpInline.setText("");
  }

  public void setUploadTextPreview(String text, String filename) {
    if (text == null) {
      Window.alert("File loading failed!");
      return;
    }
    uploadFile.setEnabled(true);
    fileContent = text;
    previewImport.setVisible(true);
    previewImport.setClose(false);
    previewImport.setHeading("Preview");
    String[] list = text.split("\n");
    StringBuilder content = new StringBuilder();
    for (String s : list) {
      content.append("<br>" + SafeHtmlUtils.htmlEscape(s));
    }
    previewText.getElement().setInnerHTML(content.toString());
  }

  @UiHandler("cancelButton")
  public void onCancelClick(ClickEvent e) {
    submitExampleForm.reset();
    editModal.hide();
  }

  @UiHandler("addRow")
  void onClickAddRow(ClickEvent event) {
  }

  @UiHandler("editRow")
  void onClickEditRow(ClickEvent event) {
    if (selectionModel != null) {
      Parameter para = selectionModel.getSelectedObject();
      if (para != null) {
        CSVEditor.this.driver.edit(para);
        editModal.show();
      } else {
        Window.alert("Please select a row !");
      }
    } else {
      Window.alert("Please select a row !");
    }
  }

  @UiHandler("deleteRow")
  void onClickDeleteRow(ClickEvent event) {
    if (selectionModel != null) {
      Parameter para = selectionModel.getSelectedObject();
      if (para != null) {
        CSVEditor.this.driver.edit(para);
        dataProvider.getList().remove(para);
        dataProvider.flush();
        dataProvider.refresh();
        rebuildPager(dataGridPagination, dataGridPager);
      } else {
        Window.alert("Please select a row !");
      }
    } else {
      Window.alert("Please select a row !");
    }
  }

  @UiHandler("saveButton")
  void onClickSave(ClickEvent event) {
    editModal.hide();
  }

  @UiHandler("parse")
  void onParseClick(ClickEvent event) {
    this.dataProvider.getList().clear();
    rebuildPager(dataGridPagination, dataGridPager);
    parse();
  }

  // @UiHandler("sendToServer")
  // void onSendClick(ClickEvent event) {
  // ParseResult pr = new ParseResult();
  // pr.setKey(this.textArea.getText());
  // pr.setValue(null);
  // sendResultToServer(pr);
  // }

  @UiHandler("browseFile")
  void onBrowseClick(ClickEvent event) {
    browseFileModal.show();
    fileUpload.getElement().setAttribute("onChange", "readText(this)");
    uploadAlert.setVisible(false);
    uploadAlert.setClose(false);
    previewImport.setVisible(false);
    uploadFile.setEnabled(false);
  }

  @UiHandler("uploadFile")
  void onUploadFileClick(ClickEvent event) {
    String name = fileUpload.getFilename();
    if (name.length() != 0 && fileContent != null) {
      browseFileModal.hide();
      textArea.setText(fileContent);
      parse.setEnabled(true);
      prettyCode.setHTML("<pre class=\"prettyprint linenums pre-scrollable\">"
          + SafeHtmlUtils.htmlEscape(fileContent) + "</pre>");
      JsPrettyPrint();
    } else {
      uploadAlert.setVisible(true);
      uploadAlert.setHeading("Error");
      uploadAlert.setText("Please select a file !");
    }
  }

  @UiHandler("cancelUpload")
  public void onCancelUploadClick(ClickEvent e) {
    submitFileForm.reset();
    browseFileModal.hide();
  }

  @UiHandler("exportBtn")
  public void onExportClick(ClickEvent e) {
    exportFile("csv");
  }

  @UiHandler("exportCSV")
  public void onExportCSVClick(ClickEvent e) {
    exportFile("csv");
  }

  @UiHandler("exportTXT")
  public void onExportTXTClick(ClickEvent e) {
    exportFile("txt");
  }

  private void exportFile(String ext) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < dataProvider.getList().size(); i++) {
      Parameter para = dataProvider.getList().get(i);
      sb.append(para.getCsvRow() + "\n");
    }
    JsExportCsvFile(sb.toString(), ext);
  }

  public boolean flag = false;

  void parse() {
    StringBuffer in = new StringBuffer();
    try {
      if (flag == true) {
        return;
      }
      ArrayList<ResultRow> result = ManuFileParser.readToBuffer(in, this.textArea.getText());
      for (int i = 0; i < result.size(); i++) {
        ResultRow row = result.get(i);
        Parameter p = new Parameter();
        p.setDescription(row.getDescription());
        State state = row.getState() ? State.ENABLED : State.DISABLED;
        p.setState(state);
        p.setName(row.getName());
        PType type = PType.NONE;
        try {
          type = PType.valueOf(row.getType().toUpperCase());
        } catch (Exception e) {
          e.printStackTrace();
        }
        p.setType(type);
        p.setCardinality(row.getCardinality());
        p.setPrefix(row.getPrefix());
        p.setIsRequired(row.getRequire());
        addParameter(p);
      }
      if (result.size() == 0) {
        Window.alert("Sorry, parsing is failed, please edit manually.");
      }
      addRow.setEnabled(true);
      deleteRow.setEnabled(true);
      editRow.setEnabled(true);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

  // void sendResultToServer(String textToServer) {
  private void sendResultToServer(ParseResult result) {
    // First, we validate the input.

    // Then, we send the input to the server.

    greetingService.sendToServer(result, new AsyncCallback<ParseResult>() {
      public void onFailure(Throwable caught) {
        // print something to report failure
        Window.alert("Failed sending result to server");
      }

      public void onSuccess(ParseResult result) {
        // print something to report success
        if (result.getValue() == null) {
          Window.alert("Succeeded sending result to server: null");
          flag = false;
        } else {
          Window.alert("Succeeded sending result to server: done");
          flag = true;
        }
      }
    });
  }
}