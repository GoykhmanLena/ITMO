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
import ru.lenok.common.models.LabWork;
import ru.lenok.common.models.LabWorkWithKey;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class LabWorkCanvasPaneLowLevel extends Pane {
    private LanguageManager lm = LanguageManager.getInstance();
    private final ObservableList<LabWorkWithKey> labWorks;
    private final Map<Long, Color> ownerColorMap = new HashMap<>();
    private final Map<LabWorkWithKey, double[]> positions = new HashMap<>();
    private final Map<LabWorkWithKey, double[]> velocities = new HashMap<>();
    private final Random rand = new Random();

    private final Locale defaultLocale = Locale.getDefault();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(defaultLocale);
    private final NumberFormat integerFormat = NumberFormat.getIntegerInstance(defaultLocale);
    Locale locale = Locale.getDefault();

    private LabWorkWithKey selectedLabWork;
    private LabWorkWithKey hoveredLabWork;

    private Consumer<LabWorkWithKey> onLabWorkSelected;

    private Canvas canvas;

    private final Popup tooltipPopup = new Popup();
    private final Label tooltipLabel = new Label();

    private AnimationTimer animationTimer;

    private long animationStartTime = 0;

    private boolean isTransitioningToTargets = false;
    private final Map<LabWorkWithKey, double[]> targetPositions = new HashMap<>();
    private final double transitionDurationSeconds = 2.0;
    private long transitionStartTime = 0;

    public LabWorkCanvasPaneLowLevel(ObservableList<LabWorkWithKey> labWorks) {
        this.labWorks = labWorks;
        this.canvas = new Canvas();
        getChildren().add(canvas);

        tooltipLabel.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-padding: 5; -fx-font-size: 12;");
        tooltipPopup.getContent().add(tooltipLabel);
        tooltipPopup.setAutoHide(true);

        widthProperty().addListener(observable -> {
            resizeCanvas();
            if (!positions.isEmpty()) {
                updateTargetPositions();
            }
        });
        heightProperty().addListener(observable -> {
            resizeCanvas();
            if (!positions.isEmpty()) {
                updateTargetPositions();
            }
        });

        labWorks.addListener((InvalidationListener) obs -> {
            initializePositionsAndRedraw();
            updateTargetPositions();
        });

        initializePositionsAndRedraw();
        updateTargetPositions();
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

    private void updateTargetPositions() {
        targetPositions.clear();
        double width = getWidth();
        double height = getHeight();

        if (width == 0 || height == 0) return;

        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (LabWorkWithKey lw : labWorks) {
            double x = lw.getCoordinates().getX();
            double y = lw.getCoordinates().getY();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        if (minX == maxX) {
            minX -= 1;
            maxX += 1;
        }
        if (minY == maxY) {
            minY -= 1;
            maxY += 1;
        }

        double margin = 20;
        for (LabWorkWithKey lw : labWorks) {
            double x = lw.getCoordinates().getX();
            double y = lw.getCoordinates().getY();

            double scaledX = margin + (x - minX) / (maxX - minX) * (width - 2 * margin);
            double scaledY = margin + (y - minY) / (maxY - minY) * (height - 2 * margin);

            targetPositions.put(lw, new double[]{scaledX, scaledY});
        }
    }

    private double randVel() {
        return (rand.nextDouble() - 0.5) * 2000;
    }

    private void startAnimation() {
        animationTimer = new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (animationStartTime == 0) {
                    animationStartTime = now;
                }

                if (isTransitioningToTargets) {
                    if (lastUpdate == 0) {
                        lastUpdate = now;
                        return;
                    }
                    double dt = (now - lastUpdate) / 1_000_000_000.0;
                    lastUpdate = now;

                    transitionStep(now);
                    redraw();
                    return;
                }

                if (now - animationStartTime > 3_000_000_000L) {
                    isTransitioningToTargets = true;
                    transitionStartTime = now;
                    lastUpdate = now;
                    return;
                }

                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double dt = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                updatePositions(dt);
                redraw();
            }
        };
        animationTimer.start();
    }

    private void transitionStep(long now) {
        double elapsedSeconds = (now - transitionStartTime) / 1_000_000_000.0;
        double t = Math.min(elapsedSeconds / transitionDurationSeconds, 1.0);

        for (LabWorkWithKey lw : labWorks) {
            double[] currentPos = positions.get(lw);
            double[] targetPos = targetPositions.get(lw);
            if (targetPos == null || currentPos == null) continue;

            double newX = currentPos[0] * (1 - t) + targetPos[0] * t;
            double newY = currentPos[1] * (1 - t) + targetPos[1] * t;

            positions.put(lw, new double[]{newX, newY});
        }

        if (t >= 1.0) {
            isTransitioningToTargets = false;
            velocities.clear();
            for (LabWorkWithKey lw : labWorks) {
                velocities.put(lw, new double[]{0, 0});
            }
        }
    }

    private void updatePositions(double dt) {
        double width = getWidth();
        double height = getHeight();

        for (LabWorkWithKey lw : labWorks) {
            double[] pos = positions.get(lw);
            double[] vel = velocities.get(lw);
            if (pos == null || vel == null) continue;

            double radius = getRadius(lw);

            pos[0] += vel[0] * dt;
            pos[1] += vel[1] * dt;

            if (pos[0] < radius || pos[0] > width - radius) vel[0] = -vel[0];
            if (pos[1] < radius || pos[1] > height - radius) vel[1] = -vel[1];

            pos[0] = Math.max(radius, Math.min(width - radius, pos[0]));
            pos[1] = Math.max(radius, Math.min(height - radius, pos[1]));
        }

        // Обработка столкновений
        for (int i = 0; i < labWorks.size(); i++) {
            for (int j = i + 1; j < labWorks.size(); j++) {
                LabWorkWithKey a = labWorks.get(i);
                LabWorkWithKey b = labWorks.get(j);
                double[] pa = positions.get(a);
                double[] pb = positions.get(b);
                double[] va = velocities.get(a);
                double[] vb = velocities.get(b);
                double ra = getRadius(a);
                double rb = getRadius(b);

                double dx = pb[0] - pa[0];
                double dy = pb[1] - pa[1];
                double distSq = dx * dx + dy * dy;
                double minDist = ra + rb;

                if (distSq < minDist * minDist) {
                    double dist = Math.sqrt(distSq);
                    if (dist == 0) {
                        dist = 0.1;
                        dx = 0.1;
                        dy = 0.1;
                    }
                    double nx = dx / dist;
                    double ny = dy / dist;

                    double tx = -ny;
                    double ty = nx;

                    double vna = va[0] * nx + va[1] * ny;
                    double vnb = vb[0] * nx + vb[1] * ny;
                    double vta = va[0] * tx + va[1] * ty;
                    double vtb = vb[0] * tx + vb[1] * ty;

                    double m = 1;

                    double vnaAfter = vnb;
                    double vnbAfter = vna;

                    va[0] = vnaAfter * nx + vta * tx;
                    va[1] = vnaAfter * ny + vta * ty;

                    vb[0] = vnbAfter * nx + vtb * tx;
                    vb[1] = vnbAfter * ny + vtb * ty;

                    double overlap = minDist - dist;
                    pa[0] -= nx * overlap / 2;
                    pa[1] -= ny * overlap / 2;
                    pb[0] += nx * overlap / 2;
                    pb[1] += ny * overlap / 2;
                }
            }
        }
    }

    private void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (LabWorkWithKey lw : labWorks) {
            double[] pos = positions.get(lw);
            if (pos == null) continue;

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
        String tooltipText = buildTooltipText(lw);

        tooltipLabel.setText(tooltipText);
        if (!tooltipPopup.isShowing()) {
            tooltipPopup.show(canvas, screenX + 10, screenY + 10);
        } else {
            tooltipPopup.setX(screenX + 10);
            tooltipPopup.setY(screenY + 10);
        }
    }

    private void hideTooltip() {
        tooltipPopup.hide();
    }

    private Color getColorForOwner(long ownerId) {
        return ownerColorMap.computeIfAbsent(ownerId, k -> Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
    }

    private double getRadius(LabWorkWithKey lw) {
        return lw.getMinimalPoint()/2;
    }

    public void highlight(LabWorkWithKey lw) {
        selectedLabWork = lw;
        redraw();
    }

    private String buildTooltipText(LabWork lw) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(lw.getId()).append("\n");
        sb.append(lm.get("label.name")).append(": ").append(lw.getName()).append("\n");

        sb.append(lm.get("title.coordinates")).append(": ")
                .append(lm.get("label.x")).append(" = ").append(numberFormat.format(lw.getCoordinates().getX()))
                .append(", ")
                .append(lm.get("label.y")).append(" = ").append(numberFormat.format(lw.getCoordinates().getY()))
                .append("\n");

        sb.append(lm.get("label.creation_date")).append(": ")
                .append(lw.getCreationDate().toLocalDate().format(java.time.format.DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM).withLocale(locale)))
                .append("\n");

        sb.append(lm.get("label.minimal_point")).append(": ").append(numberFormat.format(lw.getMinimalPoint())).append("\n");

        sb.append(lm.get("label.description")).append(": ").append(lw.getDescription()).append("\n");

        sb.append(lm.get("label.difficulty")).append(": ").append(lw.getDifficulty()).append("\n");

        if (lw.getDiscipline() != null) {
            sb.append(lm.get("title.discipline")).append(": ")
                    .append(lm.get("label.discipline_name")).append(" = ").append(lw.getDiscipline().getName())
                    .append(", ")
                    .append(lm.get("label.practice_hours")).append(" = ").append(integerFormat.format(lw.getDiscipline().getPracticeHours()))
                    .append("\n");
        }

        return sb.toString();
    }
}
