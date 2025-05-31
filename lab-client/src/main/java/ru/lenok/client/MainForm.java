package ru.lenok.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import ru.lenok.common.models.LabWork;
import ru.lenok.common.models.LabWorkWithKey;

import java.util.List;
import java.util.Map;

public class MainForm {
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final ClientService clientService = ClientService.getINSTANCE();
    private final ObservableList<LabWorkWithKey> labWorks = FXCollections.observableArrayList();
    private final LabWorkTableView tableView = new LabWorkTableView(labWorks);
    private final LabWorkCanvasPane labCanvas = new LabWorkCanvasPane(labWorks);

    public MainForm(List<LabWorkWithKey> labWorkList) {
        labWorks.addAll(labWorkList);
    }

    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1200, 800);

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setStyle("-fx-background-color: #f0f0f0");

        Label userLabel = new Label("User: Иван Иванов");
        ComboBox<String> langBox = new ComboBox<>();
        langBox.getItems().addAll("Русский", "Македонски", "Shqip", "English (NZ)");
        langBox.getSelectionModel().select(languageManager.getCurrentLanguageName());

        langBox.setOnAction(e -> {
            languageManager.setLanguage(langBox.getSelectionModel().getSelectedItem());
            start(stage);
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

        Button addButton = new Button("Add");
        leftPane.getChildren().addAll(tableView, addButton);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        StackPane rightPane = new StackPane();
        rightPane.getChildren().add(labCanvas);

        splitPane.getItems().addAll(leftPane, rightPane);
        root.setCenter(splitPane);

        // СИНХРОНИЗАЦИЯ ВЫДЕЛЕНИЯ
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
        stage.setTitle("LabWork Manager");
        stage.setScene(scene);
        stage.show();
    }
}
