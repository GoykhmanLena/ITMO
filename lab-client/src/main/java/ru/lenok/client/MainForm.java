package ru.lenok.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ru.lenok.common.models.LabWorkWithKey;

import java.util.List;

public class MainForm {
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final ClientService clientService = ClientService.getINSTANCE();
    private final ObservableList<LabWorkWithKey> labWorks = FXCollections.observableArrayList();
    private final FilteredList<LabWorkWithKey> filteredLabWorks = new FilteredList<>(labWorks, s -> true);
    private Stage stage;

    public MainForm(List<LabWorkWithKey> labWorkList, Stage stage) {
        this.stage = stage;
        labWorks.addAll(labWorkList);
        clientService.registerNotificationListener(this::notifyListChanged);
    }

    public void notifyListChanged(List<LabWorkWithKey> list){
        Platform.runLater(() -> {
            labWorks.setAll(list);
            start();
        });
    }

    public void start() {
        BorderPane root = new BorderPane();
        root.setPrefSize(1200, 800);

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
        root.setTop(topBar);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setVgrow(splitPane, Priority.ALWAYS);

        LabWorkTableView tableView = new LabWorkTableView(filteredLabWorks, filteredLabWorks::setPredicate);
        Button addButton = new Button("Add");
        leftPane.getChildren().addAll(tableView, addButton);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        LabWorkCanvasPane labCanvas = new LabWorkCanvasPane(labWorks);
        StackPane rightPane = new StackPane();
        rightPane.getChildren().add(labCanvas);

        splitPane.getItems().addAll(leftPane, rightPane);
        root.setCenter(splitPane);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                labCanvas.highlight(selected);
            }
        });

        labCanvas.setOnLabWorkSelected(labWork -> {
            tableView.getSelectionModel().select(labWork);
            tableView.scrollTo(labWork);
        });

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("LabWork Manager");
        stage.show();
    }
}
