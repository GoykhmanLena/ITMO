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

import java.util.Map;

public class MainForm {
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final ClientService clientService = ClientService.getINSTANCE();
    private final ObservableList<LabWork> labWorks = FXCollections.observableArrayList();
    private final LabWorkCanvasPane labCanvas = new LabWorkCanvasPane(labWorks);
    private Map<String, LabWork> labWorkMap;

    public MainForm(Map<String, LabWork> labWorkMap) {
        this.labWorkMap = labWorkMap;
        labWorks.addAll(labWorkMap.values());
    }

    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1200, 800);

        // Top - Language and user info
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
            start(stage); // restart for simplicity
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(userLabel, spacer, langBox);
        root.setTop(topBar);

        // SplitPane - left (table) and right (canvas)
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setVgrow(splitPane, Priority.ALWAYS);

        LabWorkTableView tableView = new LabWorkTableView(labWorks);
        Button addButton = new Button("Add");
        // addButton.setOnAction(e -> new EditDialog(null, labWorks).showAndWait());

        leftPane.getChildren().addAll(tableView, addButton);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        StackPane rightPane = new StackPane();
        rightPane.getChildren().add(labCanvas);

        splitPane.getItems().addAll(leftPane, rightPane);
        root.setCenter(splitPane);

        Scene scene = new Scene(root);
        stage.setTitle("LabWork Manager");
        stage.setScene(scene);
        stage.show();
    }
}