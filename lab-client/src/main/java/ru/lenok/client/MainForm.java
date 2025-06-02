package ru.lenok.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ru.lenok.common.models.LabWorkWithKey;

import java.util.List;

public class MainForm {
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final ClientService clientService = ClientService.getINSTANCE();
    private final ObservableList<LabWorkWithKey> labWorks = FXCollections.observableArrayList();
    private final FilteredList<LabWorkWithKey> filteredLabWorks = new FilteredList<>(labWorks, s -> true);
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private final Scene scene = new Scene(root, 1200, 800);
    private boolean initialized = false;

    public MainForm(List<LabWorkWithKey> labWorkList, Stage stage) {
        this.stage = stage;
        labWorks.addAll(labWorkList);
        clientService.registerNotificationListener(this::notifyListChanged);

        this.stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public void notifyListChanged(List<LabWorkWithKey> list) {
        Platform.runLater(() -> {
            labWorks.setAll(list);
            start();
        });
    }

    public void start() {
        stage.setTitle(languageManager.get("title.main"));
        if (!initialized) {
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            initialized = true;
        }

        root.setTop(createTopBar());
        root.setCenter(createSplitPane());
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setStyle("-fx-background-color: #f0f0f0");

        Label userLabel = new Label(languageManager.get("user_label") + ": " + clientService.getUser().getUsername());
        ComboBox<String> langBox = new ComboBox<>();
        langBox.getItems().addAll("Русский", "Македонски", "Shqip", "English (NZ)");
        langBox.getSelectionModel().select(languageManager.getCurrentLanguageName());

        langBox.setOnAction(e -> {
            languageManager.setLanguage(langBox.getSelectionModel().getSelectedItem());
            start();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(userLabel, spacer, langBox);
        return topBar;
    }

    private SplitPane createSplitPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        LabWorkTableView tableView = new LabWorkTableView(filteredLabWorks);

        Button addButton = new Button(languageManager.get("button.create"));
        addButton.setOnAction(e -> {
            LabWorkForm form = new LabWorkForm(null);
            form.showAndWait();
        });

        Button editButton = new Button(languageManager.get("button.edit"));
        editButton.setOnAction(e -> {
            LabWorkWithKey selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                LabWorkForm form = new LabWorkForm(selected);
                form.showAndWait();
            }
        });

        Button deleteButton = new Button(languageManager.get("button.delete"));
        deleteButton.setOnAction(e -> {
            LabWorkWithKey selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Exception error = clientService.deleteLabWork(selected.getKey());
                if (error != null) {
                    new Alert(Alert.AlertType.ERROR, "Ошибка при удалении: " + error).showAndWait();
                }
            }
        });

        Button clearButton = new Button(languageManager.get("button.clear"));
        clearButton.setOnAction(e -> {
            Exception error = clientService.clearLabWorks();
            if (error != null) {
                new Alert(Alert.AlertType.ERROR, "Ошибка при очистке: " + error).showAndWait();
            }
        });

        HBox buttonBar = new HBox(10, addButton, editButton, deleteButton, clearButton);
        leftPane.getChildren().addAll(tableView, buttonBar);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        LabWorkCanvasPane labCanvas = new LabWorkCanvasPane(labWorks);
        StackPane rightPane = new StackPane(labCanvas);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                labCanvas.highlight(selected);
            }
        });

        labCanvas.setOnLabWorkSelected(labWork -> {
            tableView.getSelectionModel().select(labWork);
            tableView.scrollTo(labWork);
        });

        splitPane.getItems().addAll(leftPane, rightPane);
        return splitPane;
    }
}
