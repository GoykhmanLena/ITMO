package ru.lenok.client;

import javafx.animation.AnimationTimer;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import ru.lenok.common.models.LabWorkWithKey;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;

public class LabWorkCanvasPaneLowLevel extends Pane {
    private final ObservableList<LabWorkWithKey> labWorks;
    private final Map<Long, Color> ownerColorMap = new HashMap<>();
    private final Map<LabWorkWithKey, double[]> positions = new HashMap<>();
    private final Map<LabWorkWithKey, double[]> velocities = new HashMap<>();
    private final Random rand = new Random();

    private LabWorkWithKey selectedLabWork;
    private LabWorkWithKey hoveredLabWork;

    private Consumer<LabWorkWithKey> onLabWorkSelected;

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

    private  Canvas canvas;

    // Popup для кастомного Tooltip
    private final Popup tooltipPopup = new Popup();
    private final Label tooltipLabel = new Label();

    public LabWorkCanvasPaneLowLevel(ObservableList<LabWorkWithKey> labWorks) {
        this.labWorks = labWorks;
        this.canvas = new Canvas();
        getChildren().add(canvas);

        tooltipLabel.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-padding: 5; -fx-font-size: 12;");
        tooltipPopup.getContent().add(tooltipLabel);
        tooltipPopup.setAutoHide(true);

        widthProperty().addListener(observable -> resizeCanvas());
        heightProperty().addListener(observable -> resizeCanvas());

        labWorks.addListener((InvalidationListener) obs -> initializePositionsAndRedraw());

        initializePositionsAndRedraw();
        startAnimation();
        setupMouseHandling();
    }

    public void setOnLabWorkSelected(Consumer<LabWorkWithKey> listener) {
        this.onLabWorkSelected = listener;
    }

    private void resizeCanvas() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        redraw();
    }

    private void initializePositionsAndRedraw() {
        positions.clear();
        velocities.clear();
        for (LabWorkWithKey lw : labWorks) {
            double x = rand.nextDouble() * getWidth();
            double y = rand.nextDouble() * getHeight();
            positions.put(lw, new double[]{x, y});
            velocities.put(lw, new double[]{randVel(), randVel()});
        }
        redraw();
    }

    private double randVel() {
        return rand.nextDouble() * 200 - 100;
    }

    private void startAnimation() {
        new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double dt = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                updatePositions(dt);
                redraw();
            }
        }.start();
    }

    private void updatePositions(double dt) {
        double width = getWidth();
        double height = getHeight();

        for (LabWorkWithKey lw : labWorks) {
            double[] pos = positions.get(lw);
            double[] vel = velocities.get(lw);
            double radius = getRadius(lw);

            pos[0] += vel[0] * dt;
            pos[1] += vel[1] * dt;

            if (pos[0] < radius || pos[0] > width - radius) vel[0] = -vel[0];
            if (pos[1] < radius || pos[1] > height - radius) vel[1] = -vel[1];

            pos[0] = Math.max(radius, Math.min(width - radius, pos[0]));
            pos[1] = Math.max(radius, Math.min(height - radius, pos[1]));
        }
    }

    private void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (LabWorkWithKey lw : labWorks) {
            double[] pos = positions.get(lw);
            double radius = getRadius(lw);
            Color color = getColorForOwner(lw.getOwnerId());

            if (lw.equals(selectedLabWork)) {
                gc.setStroke(Color.DEEPSKYBLUE);
                gc.setLineWidth(3);
            } else {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
            }

            gc.setFill(color);
            gc.fillOval(pos[0] - radius, pos[1] - radius, radius * 2, radius * 2);
            gc.strokeOval(pos[0] - radius, pos[1] - radius, radius * 2, radius * 2);

            gc.setFill(Color.BLACK);
            String ownerIdText = String.valueOf(lw.getOwnerId());
            gc.fillText(ownerIdText, pos[0] - gc.getFont().getSize() / 2, pos[1] + 4);
        }
    }

    private void setupMouseHandling() {
        canvas.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            double x = e.getX();
            double y = e.getY();

            for (LabWorkWithKey lw : labWorks) {
                double[] pos = positions.get(lw);
                double radius = getRadius(lw);
                double dx = pos[0] - x;
                double dy = pos[1] - y;
                if (dx * dx + dy * dy <= radius * radius) {
                    selectedLabWork = lw;
                    if (onLabWorkSelected != null) onLabWorkSelected.accept(lw);
                    redraw();
                    return;
                }
            }
            // Клик вне кружков снимает выделение
            selectedLabWork = null;
            redraw();
        });

        canvas.setOnMouseMoved(e -> {
            double x = e.getX();
            double y = e.getY();
            LabWorkWithKey found = null;

            for (LabWorkWithKey lw : labWorks) {
                double[] pos = positions.get(lw);
                double radius = getRadius(lw);
                double dx = pos[0] - x;
                double dy = pos[1] - y;
                if (dx * dx + dy * dy <= radius * radius) {
                    found = lw;
                    break;
                }
            }

            if (found != null) {
                if (!found.equals(hoveredLabWork)) {
                    hoveredLabWork = found;
                    showTooltip(hoveredLabWork, e.getScreenX(), e.getScreenY());
                }
            } else {
                hoveredLabWork = null;
                hideTooltip();
            }
        });

        canvas.setOnMouseExited(e -> {
            hoveredLabWork = null;
            hideTooltip();
        });
    }

    private void showTooltip(LabWorkWithKey lw, double screenX, double screenY) {
        String text = buildTooltipText(lw);
        tooltipLabel.setText(text);
        if (!tooltipPopup.isShowing()) {
            tooltipPopup.show(getScene().getWindow(), screenX + 10, screenY + 10);
        } else {
            tooltipPopup.setX(screenX + 10);
            tooltipPopup.setY(screenY + 10);
        }
    }

    private void hideTooltip() {
        if (tooltipPopup.isShowing()) {
            tooltipPopup.hide();
        }
    }

    private String buildTooltipText(LabWorkWithKey lw) {
        return "LabWork ID: " + lw.getKey() + "\n" +
                "Owner ID: " + lw.getOwnerId() + "\n" +
                "Name: " + lw.getName() + "\n" +
                "Min Points: " + numberFormat.format(lw.getMinimalPoint());
    }

    private double getRadius(LabWorkWithKey lw) {
        // Минимальный радиус и масштаб по минимальным баллам
        return Math.max(lw.getMinimalPoint() / 2.0, 10);
    }

    private Color getColorForOwner(Long ownerId) {
        return ownerColorMap.computeIfAbsent(ownerId, id ->
                Color.hsb(rand.nextDouble() * 360, 0.7, 0.9));
    }
}
