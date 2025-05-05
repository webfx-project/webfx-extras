package dev.webfx.extras.visual.controls.grid.peers.openjfx;

import dev.webfx.extras.cell.renderer.ValueApplier;
import dev.webfx.extras.cell.rowstyle.RowAdapter;
import dev.webfx.extras.cell.rowstyle.RowStyleUpdater;
import dev.webfx.extras.imagestore.ImageStore;
import dev.webfx.extras.label.Label;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.extras.visual.VisualResultBuilder;
import dev.webfx.extras.visual.VisualSelection;
import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.extras.visual.controls.grid.peers.base.VisualGridPeerBase;
import dev.webfx.extras.visual.controls.grid.peers.base.VisualGridPeerImageTextMixin;
import dev.webfx.extras.visual.controls.grid.peers.base.VisualGridPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.openjfx.FxLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.openjfx.FxRegionPeer;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.util.collection.IdentityList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class FxVisualGridPeer
        <N extends VisualGrid, NB extends VisualGridPeerBase<TableCell, N, NB, NM>, NM extends VisualGridPeerMixin<TableCell, N, NB, NM>>

        extends FxRegionPeer<TableView<Integer>, N, NB, NM>
        implements VisualGridPeerImageTextMixin<TableCell, N, NB, NM>, FxLayoutMeasurable {

    public FxVisualGridPeer() {
        super((NB) new VisualGridPeerBase());
    }

    static final String DEFER_TO_PARENT_PREF_WIDTH = "deferToParentPrefWidth";

    @Override
    protected TableView<Integer> createFxNode() {
        TableView<Integer> tableView = new TableView<>();
        tableView.setRowFactory(createRowFactory());
        tableView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<Integer>) c -> syncVisualSelectionFromTableViewIfEnabled());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Disabling sort for now as the default sort policy doesn't work in Modality with ReactiveVisualMapper (the
        // selected line doesn't match the selected entity)
        tableView.setSortPolicy(param -> false);
        // Overriding the table skin for a better pref width computation, and height management (with fullHeight mode)
        try { // Try catch block for java 9 where TableViewSkin is not accessible anymore
            tableView.setSkin(new TableViewSkin<>(tableView) {
                @Override
                protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
                    double pw = leftInset + rightInset;
                    for (TableColumn<Integer, ?> tc : tableView.getVisibleLeafColumns()) {
                        if (!tc.isResizable())
                            pw += tc.getWidth();
                        else {
                            int rows = getItemCount();
                            if (rows == 0) continue;

                            Callback/*<TableColumn<T, ?>, TableCell<T,?>>*/ cellFactory = tc.getCellFactory();
                            if (cellFactory == null) continue;

                            TableCell<Integer, ?> cell = (TableCell<Integer, ?>) cellFactory.call(tc);
                            if (cell == null) continue;

                            // set this property to tell the TableCell we want to know its actual
                            // preferred width, not the width of the associated TableColumnBase
                            cell.getProperties().put(DEFER_TO_PARENT_PREF_WIDTH, Boolean.TRUE);

                            // determine cell padding
                            double padding = 10;
                            Node n = cell.getSkin() == null ? null : cell.getSkin().getNode();
                            if (n instanceof Region) {
                                Region r = (Region) n;
                                padding = r.snappedLeftInset() + r.snappedRightInset();
                            }

                            //int rows = items.size(); //maxRows == -1 ? items.size() : Math.min(items.size(), maxRows);
                            double maxWidth = 0;
                            for (int row = 0; row < rows; row++) {
                                cell.updateTableColumn(tc);
                                cell.updateTableView(tableView);
                                cell.updateIndex(row);

                                if ((cell.getText() != null && !cell.getText().isEmpty()) || cell.getGraphic() != null) {
                                    getChildren().add(cell);
                                    cell.applyCss();
                                    double prefWidth = cell.prefWidth(-1);
                                    maxWidth = Math.max(maxWidth, prefWidth);
                                    getChildren().remove(cell);
                                    //System.out.println("prefWidth = " + prefWidth + " for " + cell.getText() + ", font = " + cell.getFont());
                                }
                            }

                            // dispose of the cell to prevent it retaining listeners (see RT-31015)
                            cell.updateIndex(-1);

                            pw += maxWidth + padding;
                        }
                    }
                    return pw;
                }
                @Override
                protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
                    N visualGrid = FxVisualGridPeer.this.getNode();
                    if (visualGrid == null || !visualGrid.isFullHeight())
                        return super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
                    return computeTableViewFullHeight(tableView); // TODO: omtimize and prevent 3 calls for min/pref/max
                }

                @Override
                protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
                    N visualGrid = FxVisualGridPeer.this.getNode();
                    if (visualGrid == null || !visualGrid.isFullHeight())
                        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
                    return computeTableViewFullHeight(tableView); // TODO: omtimize and prevent 3 calls for min/pref/max
                }

                @Override
                protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
                    N visualGrid = FxVisualGridPeer.this.getNode();
                    if (visualGrid == null || !visualGrid.isFullHeight())
                        return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
                    return computeTableViewFullHeight(tableView); // TODO: omtimize and prevent 3 calls for min/pref/max
                }
            });
        } catch (Throwable e) { // Probably java 9
            System.out.println("FxDataGridPeer can't access TableViewSkin to override computePrefWidth() -- Java 9 issue");
        }
        return tableView;
    }

    @Override
    protected void onFxNodeCreated() {
        TableView<Integer> tableView = getFxNode();
        N visualGrid = getNode();
        tableView.minHeightProperty().bind(visualGrid.minHeightProperty());
        tableView.prefHeightProperty().bind(visualGrid.prefHeightProperty());
        tableView.maxHeightProperty().bind(visualGrid.maxHeightProperty());
    }

    @Override
    public void updateSelectionMode(dev.webfx.extras.visual.SelectionMode mode) {
        javafx.scene.control.SelectionMode fxSelectionMode = null;
        switch (mode) {
            case DISABLED:
            case SINGLE:   fxSelectionMode = javafx.scene.control.SelectionMode.SINGLE; break;
            case MULTIPLE: fxSelectionMode = javafx.scene.control.SelectionMode.MULTIPLE; break;
        }
        getFxNode().getSelectionModel().setSelectionMode(fxSelectionMode);
    }

    private boolean enableSyncVisualSelectionFromTableView;
    private boolean syncingVisualSelectionFromTableView;

    private void syncVisualSelectionFromTableViewIfEnabled() {
        // Skipping if not enabled
        if (!enableSyncVisualSelectionFromTableView)
            return;

        // Preventing reentrant calls from internal operations
        if (syncingVisualSelectionFromTableView)
            return;

        syncingVisualSelectionFromTableView = true;
        VisualSelection rowsSelection = VisualSelection.createRowsSelection(getFxNode().getSelectionModel().getSelectedIndices());
        FXProperties.setIfNotEquals(getNode().visualSelectionProperty(), rowsSelection);
        syncingVisualSelectionFromTableView = false;
    }

    private void syncTableViewSelectionFromVisualSelection() {
        updateVisualSelection(getNode().getVisualSelection());
    }

    @Override
    public void updateVisualSelection(VisualSelection selection) {
        // Preventing reentrant calls from internal operations
        if (syncingVisualSelectionFromTableView)
            return;

        enableSyncVisualSelectionFromTableView = false;
        TableView.TableViewSelectionModel<Integer> selectionModel = getFxNode().getSelectionModel();
        selectionModel.clearSelection();
        if (selection != null)
            selection.forEachRow(selectionModel::select);
        enableSyncVisualSelectionFromTableView = true;
    }

    @Override
    public void updateHeaderVisible(boolean headerVisible) {
        TableView<Integer> tableView = getFxNode();
        if (headerVisible)
            tableView.getStyleClass().remove("noHeader");
        else
            tableView.getStyleClass().add("noHeader");
    }

    private final static EventHandler<Event> eventConsumer = Event::consume;
    @Override
    public void updateFullHeight(boolean fullHeight) {
        TableView<Integer> tableView = getFxNode();
        if (fullHeight) {
            tableView.getStyleClass().add("fullHeight");
            tableView.addEventFilter(ScrollEvent.ANY, eventConsumer);
        } else {
            tableView.getStyleClass().remove("fullHeight");
            tableView.removeEventFilter(ScrollEvent.ANY, eventConsumer);
        }
    }

    private List<TableColumn<Integer, ?>> currentColumns, newColumns;

    @Override
    public void updateVisualResult(VisualResult rs) {
        if (rs == null)
            return;
        rs = transformVisualResultValuesToProperties(rs);
        TableView<Integer> tableView = getFxNode();
        N dataGrid = getNode();
        synchronized (this) {
            currentColumns = tableView.getColumns();
            newColumns = new ArrayList<>();
            // Clearing the columns to completely rebuild them when table was empty (as the columns widths were not considering the content)
            int rowCount = rs.getRowCount();
            if (tableView.getItems().isEmpty() && rowCount > 0)
                currentColumns.clear();
            getNodePeerBase().fillGrid(rs);
            enableSyncVisualSelectionFromTableView = false;
            tableView.getSelectionModel().clearSelection(); // To avoid internal java 8 API call on Android
            tableView.getColumns().setAll(newColumns);
            currentColumns = newColumns = null;
            tableView.getSelectionModel().clearSelection(); // Clearing selection otherwise an undesired selection event is triggered on new items
            tableView.getItems().setAll(new IdentityList(rowCount));
            enableSyncVisualSelectionFromTableView = true;
            syncTableViewSelectionFromVisualSelection();
            if (rowCount > 0) { // Workaround for the JavaFX wrong resize columns problem when vertical scroll bar appears
                tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
                UiScheduler.scheduleDelay(100, () -> tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY));
            }
        }
        dataGrid.requestLayout(); // this is essentially to clear the cached sized values (prefWith, etc...)
    }

    private static VisualResult transformVisualResultValuesToProperties(VisualResult rs) {
        return VisualResultBuilder.convertVisualResult(rs, SimpleObjectProperty::new);
    }

    @Override
    public void setUpGridColumn(int gridColumnIndex, int rsColumnIndex, VisualColumn visualColumn) {
        TableColumn<Integer, ?> gridColumn = gridColumnIndex < currentColumns.size() ? currentColumns.get(gridColumnIndex) : new TableColumn<>();
        newColumns.add(gridColumn);
        Label label = visualColumn.getLabel();
        // label.getText() is an object, and may be a String or a StringProperty
        ValueApplier.applyTextValue(label.getText(), gridColumn.textProperty());
        gridColumn.setGraphic(ImageStore.createImageView(label.getIconPath()));
        Double prefWidth = visualColumn.getStyle().getPrefWidth();
        if (prefWidth != null) {
            if (prefWidth > 24)
                prefWidth *= 1.3;
            prefWidth = prefWidth + 10; // because of the 5px left and right padding
            gridColumn.setPrefWidth(prefWidth);
            gridColumn.setMinWidth(prefWidth);
            gridColumn.setMaxWidth(prefWidth);
        }
        String textAlign = visualColumn.getStyle().getTextAlign();
        Pos alignment = "right".equals(textAlign) ? Pos.CENTER_RIGHT : "center".equals(textAlign) ? Pos.CENTER : Pos.CENTER_LEFT;
        gridColumn.setCellValueFactory(cdf -> (ObservableValue) getNodePeerBase().getRs().getValue(cdf.getValue(), rsColumnIndex));
        gridColumn.setCellFactory(param -> new TableCell() {
            { setAlignment(alignment); }
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty)
                    getNodePeerBase().fillCell(this, item, visualColumn);
            }
        });
    }

    @Override
    public void setCellImageAndTextContent(TableCell cell, Node image, Object text, VisualColumn visualColumn) {
        cell.setGraphic(image);
        ValueApplier.applyTextValue(text, cell.textProperty());
    }

    private Callback<TableView<Integer>, TableRow<Integer>> createRowFactory() {
        return tableView -> {
            TableRow<Integer> row = new TableRow<>();
            RowStyleUpdater rowStyleUpdater = new RowStyleUpdater(new RowAdapter() {
                @Override public int getRowIndex() { return row.getIndex(); }
                @Override public void addStyleClass(String styleClass) { row.getStyleClass().add(styleClass); }
                @Override public void removeStyleClass(String styleClass) { row.getStyleClass().remove(styleClass); }
                @Override public void applyBackground(Paint fill) {
                    if (fill == null)
                        row.backgroundProperty().unbind();
                    else
                        row.backgroundProperty().bind(new SimpleObjectProperty<>(Background.fill(fill)));
                }
            }, this::getRowStyleClasses, this::getRowBackground);
            row.getProperties().put("nodeStyleUpdater", rowStyleUpdater); // keeping strong reference to avoid garbage collection
            FXProperties.runOnPropertyChange(newRowIndex -> rowStyleUpdater.update(), row.itemProperty());
            return row;
        };
    }

    private Object[] getRowStyleClasses(int rowIndex) {
        NB base = getNodePeerBase();
        Object value = base.getRowStyleResultValue(rowIndex);
        if (value instanceof ObservableValue)
            value = ((ObservableValue) value).getValue();
        return base.getRowStyleClasses(value);
    }

    private Paint getRowBackground(int rowIndex) {
        NB base = getNodePeerBase();
        Object value = base.getRowBackgroundResultValue(rowIndex);
        if (value instanceof ObservableValue)
            value = ((ObservableValue) value).getValue();
        return base.getRowBackground(value);
    }

// Code not working anymore since Java 9 - can't access call VirtualFlow.getCellLength()
    private static double computeTableViewFullHeight(TableView tableView) {
        Insets insets = tableView.getInsets();
        double h = insets.getTop() + insets.getBottom();
        Skin<?> skin = tableView.getSkin();
        ObservableList<javafx.scene.Node> children = null;
        if (skin instanceof Parent) // happens in java 7
            children = ((Parent) skin).getChildrenUnmodifiable();
        else if (skin instanceof SkinBase) // happens in java 8
            children = ((SkinBase) skin).getChildren();
        if (children != null) {
            double tableHeaderRowHeight = -1;
            for (javafx.scene.Node node : new ArrayList<>(children)) {
                double nodePrefHeight = 0;
                if (node instanceof TableHeaderRow) {
                    nodePrefHeight = tableHeaderRowHeight = node.prefHeight(-1);
                } else if (node instanceof VirtualFlow) {
                    VirtualFlow flow = (VirtualFlow) node;
                    int size = tableView.getItems().size();
                    try {
                        // Note: not compatible with Java 9+ (VirtualFlow made inaccessible)
                        Method m = flow.getClass().getDeclaredMethod("getCellLength", int.class);
                        m.setAccessible(true); // may raise an exception
                        for (int i = 0; i < size; i++)
                            nodePrefHeight += (Double) m.invoke(flow, i);
                    } catch (Exception e) { // Java 9+
                        // If exception was raised, we just assume an empiric default height value:
                        double rowHeight = tableHeaderRowHeight; // Taking same height as table header by default
                        if (rowHeight < 0) // If not set,
                            rowHeight = 26; // taking empiric value
                        nodePrefHeight += rowHeight * size;
                    }
                }
                h += nodePrefHeight;
            }
        }
        return h;
    }
}