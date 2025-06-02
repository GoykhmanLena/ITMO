package ru.lenok.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LabWorkForm extends Stage {

    private LabWorkWithKey result;
    private final VBox errorBox;
    private final DatePicker datePicker;
    private final ProgressIndicator progressIndicator;
    private final Button okBtn;
    private final Button cancelBtn;

    public LabWorkForm(LabWorkWithKey existing) {
        setTitle(existing == null ? "Create LabWork" : "Edit LabWork");
        initModality(Modality.APPLICATION_MODAL);

        errorBox = new VBox(5);
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

        datePicker = new DatePicker(existing == null ? LocalDate.now() : existing.getCreationDate().toLocalDate());
        Label dateLabel = new Label("Creation Date:");
        if (existing == null) {
            datePicker.setVisible(false);
            datePicker.setManaged(false);
            dateLabel.setVisible(false);
            dateLabel.setManaged(false);
        }
        grid.add(dateLabel, 0, row);
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
        okBtn = new Button("OK");
        okBtn.setDefaultButton(true);
        cancelBtn = new Button("Cancel");
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(24, 24);
        buttonBox.getChildren().addAll(progressIndicator, okBtn, cancelBtn);
        grid.add(buttonBox, 0, row, 2, 1);

        if (existing != null) {
            keyField.setText(existing.getKey());
            nameField.setText(existing.getName());
            xField.setText(String.valueOf(existing.getCoordinates().getX()));
            yField.setText(String.valueOf(existing.getCoordinates().getY()));
            minPointField.setText(String.valueOf(existing.getMinimalPoint()));
            descArea.setText(existing.getDescription());
            difficultyBox.setValue(existing.getDifficulty());
            discNameField.setText(existing.getDiscipline().getName());
            discHoursField.setText(String.valueOf(existing.getDiscipline().getPracticeHours()));
        }

        cancelBtn.setOnAction(e -> close());

        okBtn.setOnAction(e -> {
            okButtonHandler(existing, keyField, nameField, xField, yField, minPointField, descArea, difficultyBox, discNameField, discHoursField);
        });

        VBox root = new VBox(errorBox, grid);
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, Region.USE_COMPUTED_SIZE);
        setScene(scene);
        setMinWidth(400);
        setMinHeight(400);
    }

    private void okButtonHandler(LabWorkWithKey existing, TextField keyField, TextField nameField, TextField xField, TextField yField, TextField minPointField, TextArea descArea, ComboBox<Difficulty> difficultyBox, TextField discNameField, TextField discHoursField) {
        errorBox.getChildren().clear();
        resetFieldStyles(keyField, nameField, xField, yField, minPointField, descArea, difficultyBox, discNameField, discHoursField);

        boolean valid = true;

        String key = keyField.getText().trim();
        if (key.isEmpty()) {
            addError(keyField, "Поле 'Key': значение не может быть пустым, пожалуйста введите хоть что-то");
            valid = false;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            addError(nameField, "Поле 'Name': значение не может быть пустым, пожалуйста введите хоть что-то");
            valid = false;
        }

        double x = 0;
        try {
            x = Double.parseDouble(xField.getText().trim());
        } catch (NumberFormatException ex) {
            addError(xField, "Поле 'X': значение должно быть double");
            valid = false;
        }

        float y = 0;
        try {
            y = Float.parseFloat(yField.getText().trim());
        } catch (NumberFormatException ex) {
            addError(yField, "Поле 'Y': значение должно быть float");
            valid = false;
        }

        double minPoint = 0;
        try {
            minPoint = Double.parseDouble(minPointField.getText().trim());
            if (minPoint < 0) {
                addError(minPointField, "Поле 'Minimal Point': значение должно быть >= 0");
                valid = false;
            }
        } catch (NumberFormatException ex) {
            addError(minPointField, "Поле 'Minimal Point': значение должно быть double");
            valid = false;
        }

        String desc = descArea.getText().trim();
        if (desc.isEmpty()) {
            addError(descArea, "Поле 'Description': значение не может быть пустым, пожалуйста введите хоть что-то");
            valid = false;
        } else if (desc.length() > 2863) {
            addError(descArea, "Поле 'Description': слишком много букав, сократи!!!");
            valid = false;
        }

        Difficulty difficulty = difficultyBox.getValue();
        if (difficulty == null) {
            addError(difficultyBox, "Поле 'Difficulty': это не вариант из списка, повторите ввод");
            valid = false;
        }

        String discName = discNameField.getText().trim();
        if (discName.isEmpty()) {
            addError(discNameField, "Поле 'Discipline Name': значение не может быть пустым, пожалуйста введите хоть что-то");
            valid = false;
        }

        long hours = 0;
        try {
            hours = Long.parseLong(discHoursField.getText().trim());
        } catch (NumberFormatException ex) {
            addError(discHoursField, "Поле 'Practice Hours': поле должно быть Long");
            valid = false;
        }

        if (!valid) {
            sizeToScene();
            return;
        }

        okBtn.setDisable(true);
        progressIndicator.setVisible(true);

        double finalX = x;
        float finalY = y;
        double finalMinPoint = minPoint;
        long finalHours = hours;
        new Thread(() -> {
            try {
                LocalDateTime date = existing == null
                        ? LocalDateTime.now()
                        : datePicker.getValue().atStartOfDay();

                Long ownerId = ClientService.getINSTANCE().getUser().getId();

                LabWork lw = new LabWork.Builder()
                        .setName(name)
                        .setCoordinateX(finalX)
                        .setCoordinateY(finalY)
                        .setCreationDate(date)
                        .setMinimalPoint(finalMinPoint)
                        .setDescription(desc)
                        .setDifficulty(difficulty)
                        .setDisciplineName(discName)
                        .setDisciplinePracticeHours(finalHours)
                        .setOwnerId(ownerId)
                        .build();

                result = new LabWorkWithKey(key, lw);
                CommandResponse insertResponse = ClientService.getINSTANCE().insertLabWork(result);

                Platform.runLater(() -> {
                    if (insertResponse.getError() != null) {
                        new Alert(Alert.AlertType.ERROR, "Ошибка: " + insertResponse.getError()).showAndWait();
                        okBtn.setDisable(false);
                        progressIndicator.setVisible(false);
                    } else {
                        close();
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Ошибка: " + ex.getMessage()).showAndWait();
                    okBtn.setDisable(false);
                    progressIndicator.setVisible(false);
                });
            }
        }).start();
    }

    private void addError(Control field, String msg) {
        field.setStyle("-fx-border-color: red;");
        Text text = new Text(msg);
        text.setFill(Color.RED);
        errorBox.getChildren().add(text);
    }

    private void resetFieldStyles(Control... fields) {
        for (Control f : fields) {
            f.setStyle(null);
        }
    }

    public LabWorkWithKey getResult() {
        return result;
    }
}
