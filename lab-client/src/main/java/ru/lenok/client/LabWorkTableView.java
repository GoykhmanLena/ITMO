package ru.lenok.client;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import ru.lenok.common.models.LabWork;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LabWorkTableView extends TableView<LabWork> {

    public LabWorkTableView(ObservableList<LabWork> data) {
        setItems(data);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        // Колонка ключа (например, "pr_lab2")
        TableColumn<LabWork, String> keyCol = new TableColumn<>("Key");
        //keyCol.setCellValueFactory(c -> new SimpleStringProperty(keyMap.getOrDefault(c.getValue(), "")));

        TableColumn<LabWork, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));

        TableColumn<LabWork, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<LabWork, String> coordinatesCol = new TableColumn<>("Coordinates");
        coordinatesCol.setCellValueFactory(c -> {
            var coords = c.getValue().getCoordinates();
            return new SimpleStringProperty("(" + coords.getX() + ", " + coords.getY() + ")");
        });

        TableColumn<LabWork, String> creationDateCol = new TableColumn<>("Created");
        creationDateCol.setCellValueFactory(c -> {
            var date = c.getValue().getCreationDate();
            return new SimpleStringProperty(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        });

        TableColumn<LabWork, Number> minPointCol = new TableColumn<>("Minimal Point");
        minPointCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getMinimalPoint()));

        TableColumn<LabWork, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        TableColumn<LabWork, String> difficultyCol = new TableColumn<>("Difficulty");
        difficultyCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDifficulty().name()));

        TableColumn<LabWork, String> disciplineCol = new TableColumn<>("Discipline");
        disciplineCol.setCellValueFactory(c -> {
            var d = c.getValue().getDiscipline();
            return new SimpleStringProperty(d.getName() + " (" + d.getPracticeHours() + " ч)");
        });

        TableColumn<LabWork, Number> ownerCol = new TableColumn<>("Owner ID");
        ownerCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getOwnerId()));

        getColumns().addAll(
                keyCol,
                nameCol,
                coordinatesCol,
                disciplineCol,
                idCol,
                ownerCol,
                minPointCol,
                creationDateCol,
                difficultyCol,
                descriptionCol
        );

        // Обработчик двойного клика
        setRowFactory(tv -> {
            TableRow<LabWork> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    LabWork selected = row.getItem();
                    // TODO: Открытие формы редактирования
                   // new EditDialog(selected, getItems()).showAndWait();
                }
            });
            return row;
        });
    }
}