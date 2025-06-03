package ru.lenok.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.models.Difficulty;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.models.LabWorkWithKey;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

public class LabWorkForm extends Stage {

    private final LanguageManager languageManager = LanguageManager.getInstance();

    private LabWorkWithKey result;
    private final VBox errorBox;
    private final DatePicker datePicker;
    private final ProgressIndicator progressIndicator;
    private final Button okBtn;
    private final Button cancelBtn;

    public LabWorkForm(LabWorkWithKey existing) {
        setTitle(existing == null
                ? languageManager.get("title.create_labwork")
                : languageManager.get("title.edit_labwork"));
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
        if (existing != null) {
            keyField.setDisable(true);
        }
        grid.add(new Label(languageManager.get("label.key")), 0, row);
        grid.add(keyField, 1, row++);

        TextField nameField = new TextField();
        grid.add(new Label(languageManager.get("label.name")), 0, row);
        grid.add(nameField, 1, row++);

        VBox coordBox = new VBox(5);
        TextField xField = new TextField();
        TextField yField = new TextField();
        coordBox.getChildren().addAll(new Label(languageManager.get("label.x")), xField, new Label(languageManager.get("label.y")), yField);
        TitledPane coordGroup = new TitledPane(languageManager.get("title.coordinates"), coordBox);
        coordGroup.setCollapsible(false);
        grid.add(coordGroup, 0, row++, 2, 1);

        datePicker = new DatePicker(existing == null ? LocalDate.now() : existing.getCreationDate().toLocalDate());
        datePicker.getEditor().setDisable(true);
        Label dateLabel = new Label(languageManager.get("label.creation_date"));
        if (existing == null) {
            datePicker.setVisible(false);
            datePicker.setManaged(false);
            dateLabel.setVisible(false);
            dateLabel.setManaged(false);
        }
        grid.add(dateLabel, 0, row);
        grid.add(datePicker, 1, row++);

        TextField minPointField = new TextField();
        grid.add(new Label(languageManager.get("label.minimal_point")), 0, row);
        grid.add(minPointField, 1, row++);

        TextArea descArea = new TextArea();
        descArea.setWrapText(true);
        descArea.setPrefRowCount(3);
        grid.add(new Label(languageManager.get("label.description")), 0, row);
        grid.add(descArea, 1, row++);

        ComboBox<Difficulty> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().setAll(Difficulty.values());
        grid.add(new Label(languageManager.get("label.difficulty")), 0, row);
        grid.add(difficultyBox, 1, row++);

        VBox disciplineBox = new VBox(5);
        TextField discNameField = new TextField();
        TextField discHoursField = new TextField();
        disciplineBox.getChildren().addAll(
                new Label(languageManager.get("label.discipline_name")),
                discNameField,
                new Label(languageManager.get("label.practice_hours")),
                discHoursField);
        TitledPane disciplineGroup = new TitledPane(languageManager.get("title.discipline"), disciplineBox);
        disciplineGroup.setCollapsible(false);
        grid.add(disciplineGroup, 0, row++, 2, 1);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        okBtn = new Button(languageManager.get("button.ok"));
        okBtn.setDefaultButton(true);
        cancelBtn = new Button(languageManager.get("button.cancel"));
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(24, 24);
        buttonBox.getChildren().addAll(okBtn, cancelBtn, progressIndicator);
        grid.add(buttonBox, 0, row, 2, 1);

        if (existing != null) {
            keyField.setText(existing.getKey());
            nameField.setText(existing.getName());
            xField.setText(NumberFormat.getNumberInstance().format(existing.getCoordinates().getX()));
            yField.setText(NumberFormat.getNumberInstance().format(existing.getCoordinates().getY()));
            minPointField.setText(NumberFormat.getNumberInstance().format(existing.getMinimalPoint()));
            descArea.setText(existing.getDescription());
            difficultyBox.setValue(existing.getDifficulty());
            discNameField.setText(existing.getDiscipline().getName());
            discHoursField.setText(NumberFormat.getIntegerInstance().format(existing.getDiscipline().getPracticeHours()));
        }

        cancelBtn.setOnAction(e -> close());

        okBtn.setOnAction(e -> {
            okButtonHandler(existing, keyField, nameField, xField, yField, minPointField, descArea, difficultyBox, discNameField, discHoursField);
        });

        VBox root = new VBox(errorBox, grid);
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, Region.USE_COMPUTED_SIZE);
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.ESCAPE),
                () -> cancelBtn.fire()
        );
        setScene(scene);
        setMinWidth(400);
        setMinHeight(400);
    }

    private void okButtonHandler(LabWorkWithKey existing, TextField keyField, TextField nameField, TextField xField, TextField yField, TextField minPointField, TextArea descArea, ComboBox<Difficulty> difficultyBox, TextField discNameField, TextField discHoursField) {
        errorBox.getChildren().clear();
        resetFieldStyles(keyField, nameField, xField, yField, minPointField, descArea, difficultyBox, discNameField, discHoursField);

        boolean valid = true;
        Locale locale = Locale.getDefault();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        NumberFormat integerFormat = NumberFormat.getIntegerInstance(locale);

        String key = keyField.getText().trim();
        if (key.isEmpty()) {
            addError(keyField, languageManager.get("error.key.empty"));
            valid = false;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            addError(nameField, languageManager.get("error.name.empty"));
            valid = false;
        }

        double x = 0;
        try {
            x = numberFormat.parse(xField.getText().trim()).doubleValue();
        } catch (ParseException ex) {
            addError(xField, languageManager.get("error.x.invalid"));
            valid = false;
        }

        float y = 0;
        try {
            y = numberFormat.parse(yField.getText().trim()).floatValue();
        } catch (ParseException ex) {
            addError(yField, languageManager.get("error.y.invalid"));
            valid = false;
        }

        double minPoint = 0;
        try {
            minPoint = numberFormat.parse(minPointField.getText().trim()).doubleValue();
            if (minPoint < 0) {
                addError(minPointField, languageManager.get("error.min_point.negative"));
                valid = false;
            }
        } catch (ParseException ex) {
            addError(minPointField, languageManager.get("error.min_point.invalid"));
            valid = false;
        }

        String desc = descArea.getText().trim();
        if (desc.isEmpty()) {
            addError(descArea, languageManager.get("error.description.empty"));
            valid = false;
        } else if (desc.length() > 2863) {
            addError(descArea, languageManager.get("error.description.toolong"));
            valid = false;
        }

        Difficulty difficulty = difficultyBox.getValue();
        if (difficulty == null) {
            addError(difficultyBox, languageManager.get("error.difficulty.empty"));
            valid = false;
        }

        String discName = discNameField.getText().trim();
        if (discName.isEmpty()) {
            addError(discNameField, languageManager.get("error.discipline_name.empty"));
            valid = false;
        }

        long hours = 0;
        try {
            hours = integerFormat.parse(discHoursField.getText().trim()).longValue();
        } catch (ParseException ex) {
            addError(discHoursField, languageManager.get("error.practice_hours.invalid"));
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
                        .build();

                result = new LabWorkWithKey(key, lw);
                CommandResponse insertResponse = ClientService.getInstance().createOrUpdateLabWork(result);

                Platform.runLater(() -> {
                    if (insertResponse.getError() != null) {
                        new Alert(Alert.AlertType.ERROR, languageManager.get("error.insert") + ": " + insertResponse.getError()).showAndWait();
                        okBtn.setDisable(false);
                        progressIndicator.setVisible(false);
                    } else {
                        close();
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, languageManager.get("error.exception") + ": " + ex.getMessage()).showAndWait();
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
}