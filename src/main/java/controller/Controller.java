package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import view.View;

import java.util.concurrent.atomic.AtomicReference;

public class Controller {

    private final View view;
    private final SpreadSheetClickListeners spreadSheetListeners;


    public Controller(View view) {
        this.view = view;
        spreadSheetListeners = new SpreadSheetClickListeners(this.view.getRoomSpreadSheetView());
        view.init();

        initComboBoxes();
        configureSpreadSheet(Integer.parseInt(view.getRowNumberTextField().getText()),
                Integer.parseInt(view.getColumnNumberTextField().getText()));
        createListeners();
    }

    private void initComboBoxes() {
        view.getGroupNumberComboBox().setItems(ComboBoxStrings.GROUP_SELECTOR_STRINGS);
        view.getGroupNumberComboBox().setValue(ComboBoxStrings.GROUP_SELECTOR_STRINGS.get(0));
        view.getSolveMethodComboBox().setItems(ComboBoxStrings.SOLVER_METHOD_STRINGS);
        view.getSolveMethodComboBox().setValue("Válasszon...");
    }

    /**
     * Listenerek elkészítése a különböző beviteli mezőknek.
     */
    private void createListeners() {
        view.getRoomSpreadSheetView().getSelectionModel().getSelectedCells().addListener(spreadSheetListeners.getAllocationSelectorListener());

        view.getRowNumberTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.matches("\\d*")) {
                    view.getRowNumberTextField().setText(newValue.replaceAll("[^\\d]", ""));
                }
                if(!newValue.replaceAll("[^\\d]", "").equals(""))
                    configureSpreadSheet(Integer.parseInt(newValue.replaceAll("[^\\d]", "")), Integer.parseInt(view.getColumnNumberTextField().getText()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        view.getColumnNumberTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.matches("\\d*")) {
                    view.getColumnNumberTextField().setText(newValue.replaceAll("[^\\d]", ""));
                }
                if(!newValue.replaceAll("[^\\d]", "").equals(""))
                    configureSpreadSheet(Integer.parseInt(view.getRowNumberTextField().getText()), Integer.parseInt(newValue.replaceAll("[^\\d]", "")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        view.getDisableSeatsCheckBox().selectedProperty().addListener(observable -> {
            try {
                if(view.getDisableSeatsCheckBox().isSelected()) {
                    view.getRoomSpreadSheetView().getSelectionModel().getSelectedCells().removeListener(spreadSheetListeners.getAllocationSelectorListener());
                    view.getRoomSpreadSheetView().getSelectionModel().getSelectedCells().addListener(spreadSheetListeners.getCellDisableListener());
                } else {
                    view.getRoomSpreadSheetView().getSelectionModel().getSelectedCells().removeListener(spreadSheetListeners.getCellDisableListener());
                    view.getRoomSpreadSheetView().getSelectionModel().getSelectedCells().addListener(spreadSheetListeners.getAllocationSelectorListener());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * SpreadSheetView konfigurálása
     *  -
     * @param rowNumber
     * @param columnNumber
     */
    private void configureSpreadSheet(int rowNumber, int columnNumber) {
        view.getRoomSpreadSheetView().setGrid(null);

        GridBase gridBase = createGrid(rowNumber, columnNumber);

        /**
         * Automatikus méretezés, hogy ne kelljen görgetni
         */
        AtomicReference<Double> maxSize = new AtomicReference<>();
        maxSize.set(66.0);
        while ((rowNumber >= columnNumber ? rowNumber : columnNumber) * maxSize.get() > 660 && maxSize.get() != 35) {
            maxSize.set(maxSize.get()-1);
        }

        gridBase.setRowHeightCallback(param -> {
            try {
                return maxSize.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        view.getRoomSpreadSheetView().setGrid(gridBase);
        view.getRoomSpreadSheetView().getColumns().forEach(col -> {
            try {
                col.setMaxWidth(maxSize.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * SpreadSheetView-ban megjelenő Griden lévő cellák inicializálása
     * @param rowNumber: sor szám
     * @param columnNumber: oszlop szám
     * @return
     */
    private GridBase createGrid(int rowNumber, int columnNumber) {
        GridBase gridBase = new GridBase(rowNumber, columnNumber);
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for(int i = 0; i < rowNumber; i++) {
            ObservableList<SpreadsheetCell> rowList = FXCollections.observableArrayList();
            for(int j = 0; j < columnNumber; j++) {
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(i, j, 1, 1, "");
                cell.setStyle(spreadSheetListeners.getNormalStyle());
                rowList.add(cell);
            }
            rows.add(rowList);
        }
        gridBase.setRows(rows);
        return gridBase;
    }
}
