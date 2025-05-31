package ru.lenok.client;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.Group;
import javafx.util.Duration;
import ru.lenok.common.models.LabWork;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class LabWorkCanvasPane extends Pane {

    private final ObservableList<LabWork> labWorks;
    private final Map<Long, Color> ownerColorMap = new HashMap<>();
    private final Map<LabWork, Group> visualized = new HashMap<>();
    private final Random rand = new Random();

    public LabWorkCanvasPane(ObservableList<LabWork> labWorks) {
        this.labWorks = labWorks;

        labWorks.addListener((ListChangeListener<LabWork>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (LabWork lw : change.getAddedSubList()) {
                        addWithAnimation(lw);
                    }
                }
                if (change.wasRemoved()) {
                    for (LabWork lw : change.getRemoved()) {
                        removeObject(lw);
                    }
                }
            }
        });

        widthProperty().addListener(observable -> redrawAll());
        heightProperty().addListener(observable -> redrawAll());

        redrawAll();
    }

    private void addWithAnimationOld(LabWork lw) {
        double targetX = normalizeX(lw.getCoordinates().getX());
        double targetY = normalizeY(lw.getCoordinates().getY());
        double finalRadius = Math.max(lw.getMinimalPoint() / 2.0, 10); // минимальный радиус = 10

        Color color = getColorForOwner(lw.getOwnerId());

        Circle circle = new Circle(0); // начальный радиус
        circle.setFill(color);
        circle.setStroke(Color.BLACK);

        Text label = new Text(String.valueOf(lw.getOwnerId()));
        label.setFont(Font.font(12));
        label.setFill(Color.BLACK);
        label.setTranslateX(-5); // грубая центровка
        label.setTranslateY(4);

        Group group = new Group(circle, label);
        group.setTranslateX(0);
        group.setTranslateY(0);

        getChildren().add(group);
        visualized.put(lw, group);

        // Анимация перемещения
        TranslateTransition move = new TranslateTransition(Duration.millis(700), group);
        move.setToX(targetX);
        move.setToY(targetY);
        move.play();

        // Анимация роста круга
        javafx.animation.Timeline radiusGrow = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.millis(0),
                        new javafx.animation.KeyValue(circle.radiusProperty(), 0)),
                new javafx.animation.KeyFrame(Duration.millis(700),
                        new javafx.animation.KeyValue(circle.radiusProperty(), finalRadius))
        );
        radiusGrow.play();

        group.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (e.getClickCount() == 2) {
                    // new EditDialog(lw, labWorks).showAndWait();
                } else {
                    showInfo(lw);
                }
            }
        });
    }
    private void addWithAnimation(LabWork lw) {
        double canvasWidth = getWidth();
        double canvasHeight = getHeight();
        double radius = Math.max(lw.getMinimalPoint() / 2.0, 10);
        double mass = radius * radius; // масса ∝ площадь круга

        Color color = getColorForOwner(lw.getOwnerId());

        Circle circle = new Circle(radius, color);
        circle.setStroke(Color.BLACK);

        Text label = new Text(String.valueOf(lw.getOwnerId()));
        label.setFont(Font.font(12));
        label.setFill(Color.BLACK);
        label.setTranslateX(-5); // грубая центровка
        label.setTranslateY(4);

        Group group = new Group(circle, label);
        group.setTranslateX(rand.nextDouble() * (canvasWidth - 2 * radius));
        group.setTranslateY(rand.nextDouble() * (canvasHeight - 2 * radius));

        getChildren().add(group);
        visualized.put(lw, group);

        // Начальные скорости
        double[] velocity = {(rand.nextDouble() * 200 - 100) * 10, (rand.nextDouble() * 200 - 100) * 10};


        long startTime = System.currentTimeMillis();

        AnimationTimer timer = new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                double x = group.getTranslateX();
                double y = group.getTranslateY();

                x += velocity[0] * deltaTime;
                y += velocity[1] * deltaTime;

                // Столкновение со стенками
                if (x <= 0 || x + radius * 2 >= canvasWidth) {
                    velocity[0] *= -1;
                    x = Math.max(0, Math.min(x, canvasWidth - radius * 2));
                }
                if (y <= 0 || y + radius * 2 >= canvasHeight) {
                    velocity[1] *= -1;
                    y = Math.max(0, Math.min(y, canvasHeight - radius * 2));
                }

                // Простейшее столкновение с другими
                for (Map.Entry<LabWork, Group> entry : visualized.entrySet()) {
                    if (entry.getValue() == group) continue;

                    Group other = entry.getValue();
                    Circle otherCircle = (Circle) other.getChildren().get(0);
                    double ox = other.getTranslateX();
                    double oy = other.getTranslateY();
                    double oradius = otherCircle.getRadius();
                    double dx = ox - x;
                    double dy = oy - y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < radius + oradius && dist > 0) {
                        // Обмен импульсами
                        double overlap = radius + oradius - dist;
                        double nx = dx / dist;
                        double ny = dy / dist;

                        // Простейшее отталкивание
                        x -= nx * overlap / 2;
                        y -= ny * overlap / 2;
                        other.setTranslateX(ox + nx * overlap / 2);
                        other.setTranslateY(oy + ny * overlap / 2);

                        // Упрощённая модель импульса
                        double dvx = velocity[0];
                        double dvy = velocity[1];


                        velocity[0] -= dvx * 0.5;
                        velocity[1] -= dvy * 0.5;
                    }
                }

                group.setTranslateX(x);
                group.setTranslateY(y);

                // По истечении 3 секунд — плавно переместить к целевым координатам
                if (System.currentTimeMillis() - startTime >= 3000) {
                    stop();
                    animateToTarget(group, lw);
                }
            }
        };

        timer.start();

        group.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (e.getClickCount() == 2) {
                    // new EditDialog(lw, labWorks).showAndWait();
                } else {
                    showInfo(lw);
                }
            }
        });
    }

    private void animateToTarget(Group group, LabWork lw) {
        double targetX = normalizeX(lw.getCoordinates().getX());
        double targetY = normalizeY(lw.getCoordinates().getY());

        TranslateTransition tt = new TranslateTransition(Duration.millis(800), group);
        tt.setToX(targetX);
        tt.setToY(targetY);
        tt.play();
    }



    private void removeObject(LabWork lw) {
        Group group = visualized.remove(lw);
        if (group != null) {
            getChildren().remove(group);

        }
    }

    private void redrawAll() {
        getChildren().clear();
        visualized.clear();
        for (LabWork lw : labWorks) {
            addWithAnimation(lw);
        }
    }

    private void showInfo(LabWork lw) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("LabWork Info");
        alert.setHeaderText("Объект: " + lw.getName());
        alert.setContentText(lw.toString());
        alert.showAndWait();
    }

    private double normalizeX(double x) {
        return (x % 1_000) / 1_000 * (getWidth() - 40);
    }

    private double normalizeY(double y) {
        return (y % 1_000) / 1_000 * (getHeight() - 40);
    }

    private Color getColorForOwner(Long ownerId) {
        return ownerColorMap.computeIfAbsent(ownerId, id ->
                Color.hsb(rand.nextDouble() * 360, 0.7, 0.9)
        );
    }
}
