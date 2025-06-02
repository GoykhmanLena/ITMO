package ru.lenok.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import ru.lenok.common.models.LabWorkWithKey;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class LabWorkTableView extends TableView<LabWorkWithKey> {
    private final FilteredList<LabWorkWithKey> filteredData;
    private final SortedList<LabWorkWithKey> sortedData;

    public LabWorkTableView(ObservableList<LabWorkWithKey> data) {
        this.filteredData = new FilteredList<>(data, p -> true);
        this.sortedData = new SortedList<>(filteredData);
        // Обязательный биндинг для сортировки
        this.sortedData.comparatorProperty().bind(this.comparatorProperty());

        setItems(sortedData);

        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        getColumns().clear();

        addFilterableColumn("Key", c -> c.getValue().getKey());
        addFilterableColumn("ID", c -> String.valueOf(c.getValue().getId()));
        addFilterableColumn("Name", c -> c.getValue().getName());
        addFilterableColumn("Coordinates", c -> {
            var coords = c.getValue().getCoordinates();
            return "(" + coords.getX() + ", " + coords.getY() + ")";
        });
        addFilterableColumn("Created", c -> c.getValue().getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        addFilterableColumn("Minimal Point", c -> String.valueOf(c.getValue().getMinimalPoint()));
        addFilterableColumn("Description", c -> c.getValue().getDescription());
        addFilterableColumn("Difficulty", c -> c.getValue().getDifficulty().name());
        addFilterableColumn("Discipline", c -> {
            var d = c.getValue().getDiscipline();
            return d.getName() + " (" + d.getPracticeHours() + " ч)";
        });
        addFilterableColumn("Owner ID", c -> String.valueOf(c.getValue().getOwnerId()));

        setRowFactory(tv -> {
            TableRow<LabWorkWithKey> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    LabWorkWithKey selected = row.getItem();
                    // TODO: Open Edit dialog
                }
            });
            return row;
        });
    }

    private void addFilterableColumn(String title,
                                     javafx.util.Callback<TableColumn.CellDataFeatures<LabWorkWithKey, String>, String> extractor) {
        TableColumn<LabWorkWithKey, String> column = new TableColumn<>();

        column.setCellValueFactory(cell -> new SimpleStringProperty(extractor.call(cell)));
        column.setSortable(true);

        Label label = new Label(title);

        Button filterButton = new Button("\uD83D\uDD0D"); // лупа
        filterButton.setFocusTraversable(false);
        filterButton.setPadding(new Insets(0, 3, 0, 3));
        filterButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        filterButton.setOnAction(e -> showFilterPopup(filterButton, filterText -> {
            filteredData.setPredicate(item -> {
                Object cellData = column.getCellData(item);
                return cellData != null && cellData.toString().toLowerCase().contains(filterText.toLowerCase());
            });
        }));

        HBox headerBox = new HBox(label, filterButton);
        headerBox.setSpacing(5);
        HBox.setHgrow(label, Priority.ALWAYS);

        // Клик по заголовку (кроме лупы) — сортировка
        headerBox.setOnMouseClicked(event -> {
            if (event.getTarget() == filterButton) {
                return; // клик по кнопке фильтра - не сортируем
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                TableView<LabWorkWithKey> table = column.getTableView();
                if (table == null) table = this;

                ObservableList<TableColumn<LabWorkWithKey, ?>> sortOrder = table.getSortOrder();

                if (sortOrder.contains(column)) {
                    column.setSortType(column.getSortType() == TableColumn.SortType.ASCENDING
                            ? TableColumn.SortType.DESCENDING
                            : TableColumn.SortType.ASCENDING);
                } else {
                    sortOrder.clear();
                    sortOrder.add(column);
                    column.setSortType(TableColumn.SortType.ASCENDING);
                }
                table.sort();
            }
        });

        column.setGraphic(headerBox);
        getColumns().add(column);
    }

    private void showFilterPopup(Node owner, Consumer<String> onFilterEntered) {
        TextField filterField = new TextField();
        filterField.setPromptText("Введите фильтр...");
        VBox box = new VBox(filterField);
        box.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-padding: 5;");
        Popup popup = new Popup();
        popup.getContent().add(box);
        popup.setAutoHide(true);

        var bounds = owner.localToScreen(owner.getBoundsInLocal());
        popup.show(owner, bounds.getMinX(), bounds.getMaxY());

        filterField.setOnAction(e -> {
            popup.hide();
            onFilterEntered.accept(filterField.getText());
        });

        filterField.requestFocus();
    }
}
