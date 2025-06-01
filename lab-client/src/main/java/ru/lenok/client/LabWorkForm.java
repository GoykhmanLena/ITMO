package ru.lenok.client;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.lenok.common.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public class LabWorkForm extends Stage {

    private LabWorkWithKey result;

    public LabWorkForm(LabWorkWithKey existing) {
        setTitle(existing == null ? "Create LabWork" : "Edit LabWork");
        initModality(Modality.APPLICATION_MODAL);

        VBox errorBox = new VBox(5);
        errorBox.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(15));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        grid.getColumnConstraints().addAll(col1, col2);

        int row = 0;

        TextField keyField = new TextField();
        grid.add(new Label("Key:"), 0, row);
        grid.add(keyField, 1, row++);

        TextField nameField = new TextField();
        grid.add(new Label("Name:"), 0, row);
        grid.add(nameField, 1, row++);

        VBox coordBox = new VBox(5);
        TextField xField = new TextField();
        TextField yField = new TextField();
        coordBox.getChildren().addAll(new Label("X:"), xField, new Label("Y:"), yField);
        TitledPane coordGroup = new TitledPane("Coordinates", coordBox);
        coordGroup.setCollapsible(false);
        grid.add(coordGroup, 0, row++, 2, 1);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        grid.add(new Label("Creation Date:"), 0, row);
        grid.add(datePicker, 1, row++);

        TextField minPointField = new TextField();
        grid.add(new Label("Minimal Point:"), 0, row);
        grid.add(minPointField, 1, row++);

        TextArea descArea = new TextArea();
        descArea.setWrapText(true);
        descArea.setPrefRowCount(3);
        grid.add(new Label("Description:"), 0, row);
        grid.add(descArea, 1, row++);

        ComboBox<Difficulty> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().setAll(Difficulty.values());
        grid.add(new Label("Difficulty:"), 0, row);
        grid.add(difficultyBox, 1, row++);

        VBox disciplineBox = new VBox(5);
        TextField discNameField = new TextField();
        TextField discHoursField = new TextField();
        disciplineBox.getChildren().addAll(new Label("Name:"), discNameField, new Label("Practice Hours:"), discHoursField);
        TitledPane disciplineGroup = new TitledPane("Discipline", disciplineBox);
        disciplineGroup.setCollapsible(false);
        grid.add(disciplineGroup, 0, row++, 2, 1);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");
        buttonBox.getChildren().addAll(okBtn, cancelBtn);
        grid.add(buttonBox, 0, row, 2, 1);

        if (existing != null) {
            keyField.setText(existing.getKey());
            nameField.setText(existing.getName());
            xField.setText(String.valueOf(existing.getCoordinates().getX()));
            yField.setText(String.valueOf(existing.getCoordinates().getY()));
            datePicker.setValue(existing.getCreationDate().toLocalDate());
            minPointField.setText(String.valueOf(existing.getMinimalPoint()));
            descArea.setText(existing.getDescription());
            difficultyBox.setValue(existing.getDifficulty());
            discNameField.setText(existing.getDiscipline().getName());
            discHoursField.setText(String.valueOf(existing.getDiscipline().getPracticeHours()));
        }

        cancelBtn.setOnAction(e -> close());

        okBtn.setOnAction(e -> {
            errorBox.getChildren().clear();
            keyField.setStyle(null);
            nameField.setStyle(null);
            xField.setStyle(null);
            yField.setStyle(null);
            minPointField.setStyle(null);
            descArea.setStyle(null);
            difficultyBox.setStyle(null);
            discNameField.setStyle(null);
            discHoursField.setStyle(null);

            boolean valid = true;

            String key = keyField.getText().trim();
            if (key.isEmpty()) {
                addError(errorBox, keyField, "Значение поля не может быть пустым, пожалуйста введите хоть что-то");
                valid = false;
            }

            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                addError(errorBox, nameField, "Значение поля не может быть пустым, пожалуйста введите хоть что-то");
                valid = false;
            }

            double x = 0;
            try {
                x = Double.parseDouble(xField.getText().trim());
            } catch (NumberFormatException ex) {
                addError(errorBox, xField, "Значение должно быть double");
                valid = false;
            }

            float y = 0;
            try {
                y = Float.parseFloat(yField.getText().trim());
            } catch (NumberFormatException ex) {
                addError(errorBox, yField, "Значение должно быть float");
                valid = false;
            }

            double minPoint = 0;
            try {
                minPoint = Double.parseDouble(minPointField.getText().trim());
            } catch (NumberFormatException ex) {
                addError(errorBox, minPointField, "Значение должно быть double");
                valid = false;
            }

            String desc = descArea.getText().trim();
            if (desc.isEmpty()) {
                addError(errorBox, descArea, "Значение поля не может быть пустым, пожалуйста введите хоть что-то");
                valid = false;
            } else if (desc.length() > 2863) {
                addError(errorBox, descArea, "Слишком много букав, сократи!!!");
                valid = false;
            }

            Difficulty difficulty = difficultyBox.getValue();
            if (difficulty == null) {
                addError(errorBox, difficultyBox, "Это не вариант из списка, повторите ввод");
                valid = false;
            }

            String discName = discNameField.getText().trim();
            if (discName.isEmpty()) {
                addError(errorBox, discNameField, "Значение поля не может быть пустым, пожалуйста введите хоть что-то");
                valid = false;
            }

            long hours = 0;
            try {
                hours = Long.parseLong(discHoursField.getText().trim());
            } catch (NumberFormatException ex) {
                addError(errorBox, discHoursField, "Поле должно быть Long");
                valid = false;
            }

            if (!valid) return;

            try {
                LocalDateTime date = datePicker.getValue().atStartOfDay();
                Long ownerId = ClientService.getINSTANCE().getUser().getId();

                LabWork lw = new LabWork.Builder()
                        .setName(name)
                        .setCoordinateX(x)
                        .setCoordinateY(y)
                        .setCreationDate(date)
                        .setMinimalPoint(minPoint)
                        .setDescription(desc)
                        .setDifficulty(difficulty)
                        .setDisciplineName(discName)
                        .setDisciplinePracticeHours(hours)
                        .setOwnerId(ownerId)
                        .build();

                result = new LabWorkWithKey(key, lw);
                close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Ошибка: " + ex.getMessage()).showAndWait();
            }
        });

        VBox root = new VBox(errorBox, grid);
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 500, Region.USE_COMPUTED_SIZE);
        setScene(scene);
        setMinWidth(400);
    }

    private void addError(Pane errorBox, Control field, String msg) {
        field.setStyle("-fx-border-color: red;");
        Text text = new Text(msg);
        text.setFill(Color.RED);
        errorBox.getChildren().add(text);
    }

    public LabWorkWithKey getResult() {
        return result;
    }
}
