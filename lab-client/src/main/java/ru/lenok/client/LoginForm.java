package ru.lenok.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.CommandWithArgument;
import ru.lenok.common.auth.User;
import ru.lenok.common.commands.CommandBehavior;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.models.LabWorkWithKey;

import java.util.List;
import java.util.Map;

public class LoginForm {
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final ClientService clientService = ClientService.getINSTANCE();
    public LoginForm() {
    }

    public void start(Stage stage) {
        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();
        CheckBox registerBox = new CheckBox();
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button();
        Button cancelButton = new Button("Cancel");

        ComboBox<String> langBox = new ComboBox<>();
        langBox.getItems().addAll("Русский", "Македонски", "Shqip", "English (NZ)");
        langBox.getSelectionModel().select(languageManager.getCurrentLanguageName());
        langBox.setOnAction(e -> {
            languageManager.setLanguage(langBox.getSelectionModel().getSelectedItem());
            start(stage);
        });

        loginButton.setOnAction(e -> {
            if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                errorLabel.setText(languageManager.get("error.empty_fields"));
            } else {
                try {
                    User user = new User(loginField.getText(), passwordField.getText());
                    Map<String, CommandBehavior> commandDefinitions = clientService.getConnector().sendHello(registerBox.isSelected(), user);
                    clientService.setCommandDefinitions(commandDefinitions);
                    CommandBehavior show = commandDefinitions.get("show");
                    CommandRequest showRequest = new CommandRequest(new CommandWithArgument("show", show, null, null), null, user);
                    CommandResponse commandResponse = clientService.getConnector().sendCommand(showRequest);
                    List<LabWorkWithKey> labWorkList = (List<LabWorkWithKey>) commandResponse.getOutputObject();
                    new MainForm(labWorkList).start(stage);
                } catch (Exception ex) {
                    errorLabel.setText(ex.getMessage());
                }
            }
        });

        cancelButton.setOnAction(e -> stage.close());

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

        HBox buttonBox = new HBox(10, cancelButton, loginButton);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        HBox.setHgrow(loginButton, Priority.ALWAYS);
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        grid.add(buttonBox, 0, 4, 2, 1);

        loginButton.setText(languageManager.get("button.login"));

        BorderPane root = new BorderPane();
        root.setTop(topLangBox);
        root.setCenter(grid);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle(languageManager.get("title.login"));
        stage.show();
    }
}