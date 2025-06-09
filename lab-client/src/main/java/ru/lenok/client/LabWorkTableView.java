package ru.lenok.client;

import javafx.beans.property.SimpleObjectProperty;
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
import ru.lenok.common.models.Coordinates;
import ru.lenok.common.models.Difficulty;
import ru.lenok.common.models.Discipline;
import ru.lenok.common.models.LabWorkWithKey;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class LabWorkTableView extends TableView<LabWorkWithKey> {
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final FilteredList<LabWorkWithKey> filteredData;
    private final SortedList<LabWorkWithKey> sortedData;

    private final Locale defaultLocale = Locale.getDefault();

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(defaultLocale);
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(defaultLocale);
    private final NumberFormat integerFormat = NumberFormat.getIntegerInstance(defaultLocale);

    {
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(3);
    }

    private final Map<TableColumn<LabWorkWithKey, ?>, String> columnFilters = new HashMap<>();

    public LabWorkTableView(ObservableList<LabWorkWithKey> data) {
        this.filteredData = new FilteredList<>(data, p -> true);
        this.sortedData = new SortedList<>(filteredData);
        this.sortedData.comparatorProperty().bind(this.comparatorProperty());

        setItems(sortedData);

        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        getColumns().clear();

        this.<String>addFilterableColumn(languageManager.get("label.key"),
                c -> c.getValue().getKey(),
                Comparator.nullsLast(String::compareToIgnoreCase),
                s -> s);

        this.<Long>addFilterableColumn("ID",
                c -> c.getValue().getId(),
                Comparator.nullsLast(Long::compareTo),
                integerFormat::format);

        this.<String>addFilterableColumn(languageManager.get("label.name"),
                c -> c.getValue().getName(),
                Comparator.nullsLast(String::compareToIgnoreCase),
                s -> s);

        this.<Coordinates>addFilterableColumn(languageManager.get("title.coordinates"),
                c -> c.getValue().getCoordinates(),
                Comparator.nullsLast((a, b) -> {
                    if (a == null && b == null) return 0;
                    if (a == null) return -1;
                    if (b == null) return 1;
                    return a.compareTo(b);
                }),
                coords -> "(" + numberFormat.format(coords.getX()) + " ; " + numberFormat.format(coords.getY()) + ")");

        this.<java.time.LocalDateTime>addFilterableColumn(languageManager.get("label.creation_date"),
                c -> c.getValue().getCreationDate(),
                Comparator.nullsLast(java.time.LocalDateTime::compareTo),
                d -> d.format(dateFormatter));

        this.<Double>addFilterableColumn(languageManager.get("label.minimal_point"),
                c -> c.getValue().getMinimalPoint(),
                Comparator.nullsLast(Double::compareTo),
                numberFormat::format);

        this.<String>addFilterableColumn(languageManager.get("label.description"),
                c -> c.getValue().getDescription(),
                Comparator.nullsLast(String::compareToIgnoreCase),
                s -> s);

        this.<Difficulty>addFilterableColumn(languageManager.get("label.difficulty"),
                c -> c.getValue().getDifficulty(),
                Comparator.nullsLast(Comparator.comparingInt(Enum::ordinal)),
                d -> d.name());

        this.<String>addFilterableColumn(languageManager.get("title.discipline"),
                c -> {
                    Discipline d = c.getValue().getDiscipline();
                    return d.getName() + " (" + integerFormat.format(d.getPracticeHours()) + languageManager.get("label.hour") + ")";
                },
                Comparator.nullsLast(String::compareToIgnoreCase),
                s -> s);

        this.<Long>addFilterableColumn(languageManager.get("label.owner_id"),
                c -> c.getValue().getOwnerId(),
                Comparator.nullsLast(Long::compareTo),
                integerFormat::format);
    }

    private <T extends Comparable<? super T>> void addFilterableColumn(
            String title,
            Function<TableColumn.CellDataFeatures<LabWorkWithKey, T>, T> extractor,
            Comparator<T> comparator,
            Function<T, String> toStringMapper) {

        TableColumn<LabWorkWithKey, T> column = new TableColumn<>();
        column.setCellValueFactory(cell -> new SimpleObjectProperty<>(extractor.apply(cell)));
        column.setSortable(true);
        column.setComparator(comparator);

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : toStringMapper.apply(item));
            }
        });

        Label label = new Label(title);

        Button filterButton = new Button("\uD83D\uDD0D");
        filterButton.setFocusTraversable(false);
        filterButton.setPadding(new Insets(0, 3, 0, 3));
        filterButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Button clearFilterButton = new Button("✖");
        clearFilterButton.setFocusTraversable(false);
        clearFilterButton.setPadding(new Insets(0, 3, 0, 3));
        clearFilterButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");



        filterButton.setOnAction(e -> {
            String currentFilter = columnFilters.getOrDefault(column, "");
            showFilterPopup(filterButton, currentFilter, filterText -> {
                if (filterText == null || filterText.isBlank()) {
                    columnFilters.remove(column);
                } else {
                    columnFilters.put(column, filterText.toLowerCase());
                }
                applyFilters();
                updateFilterButtonStyle(filterButton, column);
            });
        });

        clearFilterButton.setOnAction(e -> {
            columnFilters.remove(column);
            applyFilters();
            updateFilterButtonStyle(filterButton, column);
        });

        updateFilterButtonStyle(filterButton, column);

        HBox buttonsBox = new HBox(filterButton, clearFilterButton);
        buttonsBox.setSpacing(5);
        buttonsBox.setPadding(new Insets(2, 0, 0, 0));
        buttonsBox.setStyle("-fx-alignment: center;");

        VBox headerBox = new VBox(label, buttonsBox);
        headerBox.setSpacing(2);
        headerBox.setPadding(new Insets(2, 0, 2, 0));
        headerBox.setStyle("-fx-alignment: center;");


        column.setGraphic(headerBox);

        getColumns().add(column);
    }
    void updateFilterButtonStyle(Button button, TableColumn column){
        String filter = columnFilters.get(column);
        if (filter != null && !filter.isBlank()) {
            button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-weight: bold; -fx-text-fill: #0078D7;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        }
    };

    private void applyFilters() {
        filteredData.setPredicate(item -> {
            for (var entry : columnFilters.entrySet()) {
                TableColumn<LabWorkWithKey, ?> column = entry.getKey();
                String filterText = entry.getValue();

                Object cellData = column.getCellData(item);
                String cellString = cellData == null ? "" : cellData.toString().toLowerCase();

                if (!cellString.contains(filterText)) {
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
