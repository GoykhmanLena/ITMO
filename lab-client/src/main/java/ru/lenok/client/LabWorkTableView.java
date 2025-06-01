package ru.lenok.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import ru.lenok.common.models.LabWorkWithKey;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LabWorkTableView extends TableView<LabWorkWithKey> {

    public LabWorkTableView(ObservableList<LabWorkWithKey> data, Consumer<Predicate<LabWorkWithKey>> setFilter) {
        getColumns().clear();
        setItems(data);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        addFilterableColumn("Key", c -> c.getValue().getKey(), setFilter);
        addFilterableColumn("ID", c -> String.valueOf(c.getValue().getId()), setFilter);
        addFilterableColumn("Name", c -> c.getValue().getName(), setFilter);
        addFilterableColumn("Coordinates", c -> {
            var coords = c.getValue().getCoordinates();
            return "(" + coords.getX() + ", " + coords.getY() + ")";
        }, setFilter);
        addFilterableColumn("Created", c -> c.getValue().getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE), setFilter);
        addFilterableColumn("Minimal Point", c -> String.valueOf(c.getValue().getMinimalPoint()), setFilter);
        addFilterableColumn("Description", c -> c.getValue().getDescription(), setFilter);
        addFilterableColumn("Difficulty", c -> c.getValue().getDifficulty().name(), setFilter);
        addFilterableColumn("Discipline", c -> {
            var d = c.getValue().getDiscipline();
            return d.getName() + " (" + d.getPracticeHours() + " ч)";
        }, setFilter);
        addFilterableColumn("Owner ID", c -> String.valueOf(c.getValue().getOwnerId()), setFilter);

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
                                     javafx.util.Callback<TableColumn.CellDataFeatures<LabWorkWithKey, String>, String> extractor,
                                     Consumer<Predicate<LabWorkWithKey>> setFilter) {
        TableColumn<LabWorkWithKey, String> column = new TableColumn<>();
        column.setCellValueFactory(cell -> new SimpleStringProperty(extractor.call(cell)));

        // Меню: фильтровать
        MenuItem filterItem = new MenuItem("Фильтровать...");
        filterItem.setOnAction(e -> showFilterPopup(column.getGraphic(), filterText ->
                setFilter.accept(item -> extractor.call(new TableColumn.CellDataFeatures<>(null, column, item))
                        .toLowerCase().contains(filterText.toLowerCase()))
        ));

        // Меню: убрать все фильтры
        MenuItem clearFilterItem = new MenuItem("Убрать все фильтры");
        clearFilterItem.setOnAction(e -> setFilter.accept(item -> true));

        ContextMenu contextMenu = new ContextMenu(filterItem, clearFilterItem);

        Label label = new Label(title);
        label.setContextMenu(contextMenu);
        label.setStyle("-fx-font-weight: bold; -fx-padding: 4px;");
        column.setGraphic(label);

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
