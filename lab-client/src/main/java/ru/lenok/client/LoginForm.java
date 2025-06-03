package ru.lenok.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.auth.User;
import ru.lenok.common.models.LabWorkWithKey;

import java.util.List;

public class LoginForm {
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final ClientService clientService = ClientService.getInstance();

    public void start(Stage stage) {
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        User user = clientService.getUser();
        boolean register = clientService.isRegister();

        TextField loginField = new TextField(user != null ? user.getUsername() : "");
        PasswordField passwordField = new PasswordField();
        passwordField.setText(user != null ? user.getPassword() : "");
        CheckBox registerBox = new CheckBox();
        registerBox.setSelected(register);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button();
        Button cancelButton = new Button(languageManager.get("button.cancel"));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(25, 25);

        ComboBox<String> langBox = new ComboBox<>();
        langBox.getItems().addAll(languageManager.getAllLanguages());
        langBox.getSelectionModel().select(languageManager.getCurrentLanguageName());
        langBox.setOnAction(e -> {
            languageManager.setLanguage(langBox.getSelectionModel().getSelectedItem());
            start(stage);
        });

        loginButton.setOnAction(e -> {
            if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                errorLabel.setText(languageManager.get("error.empty_fields"));
            } else {
                errorLabel.setText("");
                loginButton.setDisable(true);
                progressIndicator.setVisible(true);

                new Thread(() -> {
                    try {
                        clientService.login(loginField.getText(), passwordField.getText(), registerBox.isSelected());
                        CommandResponse commandResponse = clientService.getAllLabWorks();
                        List<LabWorkWithKey> labWorkList = (List<LabWorkWithKey>) commandResponse.getOutputObject();

                        Platform.runLater(() -> {
                            stage.close();
                            Stage mainStage = new Stage();
                            mainStage.setMaximized(true);
                            new MainForm(labWorkList, mainStage).start();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            errorLabel.setText(ex.getMessage());
                            loginButton.setDisable(false);
                            cancelButton.setDisable(false);
                            progressIndicator.setVisible(false);
                        });
                    }
                }).start();
            }
        });

        cancelButton.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        HBox topLangBox = new HBox(langBox);
        topLangBox.setAlignment(Pos.TOP_RIGHT);
        topLangBox.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(new Label(languageManager.get("label.login")), 0, 0);
        grid.add(loginField, 1, 0);

        grid.add(new Label(languageManager.get("label.password")), 0, 1);
        grid.add(passwordField, 1, 1);

        grid.add(new Label(languageManager.get("label.register")), 0, 2);
        grid.add(registerBox, 1, 2);

        grid.add(errorLabel, 0, 3, 2, 1);

        HBox buttonBox = new HBox(10, cancelButton, loginButton, progressIndicator);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        HBox.setHgrow(loginButton, Priority.ALWAYS);
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        grid.add(buttonBox, 0, 4, 2, 1);

        loginButton.setText(languageManager.get("button.login"));
        loginButton.setDefaultButton(true);

        BorderPane root = new BorderPane();
        root.setTop(topLangBox);
        root.setCenter(grid);

        Scene scene = new Scene(root, 400, Region.USE_COMPUTED_SIZE);
        stage.setScene(scene);
        stage.setTitle(languageManager.get("title.login"));
        stage.sizeToScene();
        stage.show();
    }
}
