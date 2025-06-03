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
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import ru.lenok.common.models.LabWorkWithKey;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class LabWorkTableView extends TableView<LabWorkWithKey> {
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final FilteredList<LabWorkWithKey> filteredData;
    private final SortedList<LabWorkWithKey> sortedData;

    private final Locale defaultLocale = Locale.getDefault();

    // Настройка форматирования с учетом локали
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(defaultLocale);
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(defaultLocale);
    private final NumberFormat integerFormat = NumberFormat.getIntegerInstance(defaultLocale);

    {
        // Можно настроить минимальное количество знаков после запятой
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(3);
    }

    private final Map<TableColumn<LabWorkWithKey, String>, String> columnFilters = new HashMap<>();

    public LabWorkTableView(ObservableList<LabWorkWithKey> data) {
        this.filteredData = new FilteredList<>(data, p -> true);
        this.sortedData = new SortedList<>(filteredData);
        this.sortedData.comparatorProperty().bind(this.comparatorProperty());

        setItems(sortedData);

        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        getColumns().clear();

        addFilterableColumn(languageManager.get("label.key"), c -> c.getValue().getKey());
        addFilterableColumn("ID", c -> integerFormat.format(c.getValue().getId()));
        addFilterableColumn(languageManager.get("label.name"), c -> c.getValue().getName());
        addFilterableColumn(languageManager.get("title.coordinates"), c -> {
            var coords = c.getValue().getCoordinates();
            return "(" + numberFormat.format(coords.getX()) + " ; " + numberFormat.format(coords.getY()) + ")";
        });
        addFilterableColumn(languageManager.get("label.creation_date"), c -> c.getValue().getCreationDate().format(dateFormatter));
        addFilterableColumn(languageManager.get("label.minimal_point"), c -> numberFormat.format(c.getValue().getMinimalPoint()));
        addFilterableColumn(languageManager.get("label.description"), c -> c.getValue().getDescription());
        addFilterableColumn(languageManager.get("label.difficulty"), c -> c.getValue().getDifficulty().name());
        addFilterableColumn(languageManager.get("title.discipline"), c -> {
            var d = c.getValue().getDiscipline();
            return d.getName() + " (" + integerFormat.format(d.getPracticeHours()) + languageManager.get("label.hour")+ ")";
        });
        addFilterableColumn(languageManager.get("label.owner_id"), c -> integerFormat.format(c.getValue().getOwnerId()));
    }

    private void addFilterableColumn(String title,
                                     javafx.util.Callback<TableColumn.CellDataFeatures<LabWorkWithKey, String>, String> extractor) {
        TableColumn<LabWorkWithKey, String> column = new TableColumn<>();

        column.setCellValueFactory(cell -> new SimpleStringProperty(extractor.call(cell)));
        column.setSortable(true);

        Label label = new Label(title);

        Button filterButton = new Button("\uD83D\uDD0D");
        filterButton.setFocusTraversable(false);
        filterButton.setPadding(new Insets(0, 3, 0, 3));
        filterButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Button clearFilterButton = new Button("✖");
        clearFilterButton.setFocusTraversable(false);
        clearFilterButton.setPadding(new Insets(0, 3, 0, 3));
        clearFilterButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Runnable updateFilterButtonStyle = () -> {
            String filter = columnFilters.get(column);
            if (filter != null && !filter.isBlank()) {
                filterButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-weight: bold; -fx-text-fill: #0078D7;");
            } else {
                filterButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            }
        };

        filterButton.setOnAction(e -> {
            String currentFilter = columnFilters.getOrDefault(column, "");
            showFilterPopup(filterButton, currentFilter, filterText -> {
                if (filterText == null || filterText.isBlank()) {
                    columnFilters.remove(column);
                } else {
                    columnFilters.put(column, filterText.toLowerCase());
                }
                applyFilters();
                updateFilterButtonStyle.run();
            });
        });

        clearFilterButton.setOnAction(e -> {
            columnFilters.remove(column);
            applyFilters();
            updateFilterButtonStyle.run();
        });

        updateFilterButtonStyle.run();

        HBox buttonsBox = new HBox(filterButton, clearFilterButton);
        buttonsBox.setSpacing(5);
        buttonsBox.setPadding(new Insets(2, 0, 0, 0));
        buttonsBox.setStyle("-fx-alignment: center;");

        VBox headerBox = new VBox(label, buttonsBox);
        headerBox.setSpacing(2);
        headerBox.setPadding(new Insets(2, 0, 2, 0));
        headerBox.setStyle("-fx-alignment: center;");

        headerBox.setOnMouseClicked(event -> {
            if (event.getTarget() == filterButton || event.getTarget() == clearFilterButton) {
                return;
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

    private void applyFilters() {
        filteredData.setPredicate(item -> {
            for (var entry : columnFilters.entrySet()) {
                TableColumn<LabWorkWithKey, String> column = entry.getKey();
                String filterText = entry.getValue();
                Object cellData = column.getCellData(item);
                if (cellData == null || !cellData.toString().toLowerCase().contains(filterText)) {
                    return false;
                }
            }
            return true;
        });
    }

    private void showFilterPopup(Node owner, String currentFilter, Consumer<String> onFilterEntered) {
        TextField filterField = new TextField(currentFilter);
        filterField.setPromptText(languageManager.get("prompt.enter_filter"));
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

        filterField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                popup.hide();
                onFilterEntered.accept(filterField.getText());
            }
        });

        filterField.requestFocus();
        filterField.selectAll();
    }
}
