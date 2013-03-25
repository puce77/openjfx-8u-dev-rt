/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.javafx.scene.control.skin;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import com.sun.javafx.Utils;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
/**
 *
 * @author paru
 */
public class CustomColorDialog extends StackPane {
    
    private static final int CONTENT_PADDING = 10;
    private static final int RECT_SIZE = 200;
    private static final int CONTROLS_WIDTH = 256;
    private static final int COLORBAR_GAP = 9;
    private static final int LABEL_GAP = 2;
    
    private final Stage dialog = new Stage();
    private ColorRectPane colorRectPane;
    private ControlsPane controlsPane;

    private Circle colorRectIndicator;
    private Rectangle colorRect;
    private Rectangle colorRectOverlayOne;
    private Rectangle colorRectOverlayTwo;
    private Rectangle colorBar;
    private Rectangle colorBarIndicator;

    private Color currentColor = Color.WHITE;
    private ObjectProperty<Color> customColorProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);
    private Runnable onSave;
    private Runnable onUse;
    private Runnable onCancel;
    
    private WebColorField webField = null;
    private Scene customScene;
    
    public CustomColorDialog(Window owner) {
        getStyleClass().add("custom-color-dialog");
        if (owner != null) dialog.initOwner(owner);
        dialog.setTitle("Custom Colors");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        colorRectPane = new ColorRectPane();
        controlsPane = new ControlsPane();
        
        customScene = new Scene(this);
        getChildren().addAll(colorRectPane, controlsPane);
        
        dialog.setScene(customScene);
        dialog.addEventHandler(KeyEvent.ANY, keyEventListener);
    }
    
    private final EventHandler<KeyEvent> keyEventListener = new EventHandler<KeyEvent>() {
        @Override public void handle(KeyEvent e) {
            switch (e.getCode()) {
                case ESCAPE :
                    dialog.setScene(null);
                    dialog.close();
            default:
                break;
            }
        }
    };
    
    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
        controlsPane.currentColorRect.setFill(currentColor);
    }

    ObjectProperty<Color> customColorProperty() {
        return customColorProperty;
    }

    void setCustomColor(Color color) {
        customColorProperty.set(color);
    }

    Color getCustomColor() {
        return customColorProperty.get();
    }

    public Runnable getOnSave() {
        return onSave;
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public Runnable getOnUse() {
        return onUse;
    }

    public void setOnUse(Runnable onUse) {
        this.onUse = onUse;
    }

    public Runnable getOnCancel() {
        return onCancel;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    Stage getDialog() {
        return dialog;
    }
    
    public void show(double x, double y) {
        if (x != 0 && y != 0) {
            dialog.setX(x);
            dialog.setY(y);
        }
        if (dialog.getScene() == null) dialog.setScene(customScene);
        colorRectPane.updateValues();
        dialog.show();
    }
    
    @Override public void layoutChildren() {
        double x = getInsets().getLeft();
        controlsPane.relocate(x+colorRectPane.prefWidth(-1), 0);
    }
    
    @Override public double computePrefWidth(double height) {
        return getInsets().getLeft() + colorRectPane.prefWidth(height) +
                controlsPane.prefWidth(height) + getInsets().getRight();
    }
    
    @Override public double computePrefHeight(double width) {
        return getInsets().getTop() + Math.max(colorRectPane.prefHeight(width),
                controlsPane.prefHeight(width) + getInsets().getBottom());
    }
    
    static double computeXOffset(double width, double contentWidth, HPos hpos) {
        switch(hpos) {
            case LEFT:
               return 0;
            case CENTER:
               return (width - contentWidth) / 2;
            case RIGHT:
               return width - contentWidth;
        }
        return 0;
    }

    static double computeYOffset(double height, double contentHeight, VPos vpos) {
       switch(vpos) {
            case TOP:
               return 0;
            case CENTER:
               return (height - contentHeight) / 2;
            case BOTTOM:
               return height - contentHeight;
            default:
                return 0;
        }
       
    }
    
    /* ------------------------------------------------------------------------*/
    
    private class ColorRectPane extends StackPane {
        
        private boolean changeIsLocal = false;
        private DoubleProperty hue = new SimpleDoubleProperty(-1) {
            @Override protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private DoubleProperty sat = new SimpleDoubleProperty(-1) {
            @Override protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private DoubleProperty bright = new SimpleDoubleProperty(-1) {
            @Override protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private IntegerProperty red = new SimpleIntegerProperty(-1) {
            @Override protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };
        
        private IntegerProperty green = new SimpleIntegerProperty(-1) {
            @Override protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };
        
        private IntegerProperty blue = new SimpleIntegerProperty(-1) {
            @Override protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };
        
        private DoubleProperty alpha = new SimpleDoubleProperty(100) {
            @Override protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    setCustomColor(new Color(
                            getCustomColor().getRed(), 
                            getCustomColor().getGreen(), 
                            getCustomColor().getBlue(), 
                            clamp(alpha.get() / 100)));
                    changeIsLocal = false;
                }
            }
        };
         
        private void updateRGBColor() {
            Color newColor = Color.rgb(red.get(), green.get(), blue.get(), clamp(alpha.get() / 100));
            hue.set(newColor.getHue());
            sat.set(newColor.getSaturation() * 100);
            bright.set(newColor.getBrightness() * 100);
            setCustomColor(newColor);
        }
        
        private void updateHSBColor() {
            Color newColor = Color.hsb(hue.get(), clamp(sat.get() / 100), 
                            clamp(bright.get() / 100), clamp(alpha.get() / 100));
            red.set(doubleToInt(newColor.getRed()));
            green.set(doubleToInt(newColor.getGreen()));
            blue.set(doubleToInt(newColor.getBlue()));
            setCustomColor(newColor);
        }
       
        private void colorChanged() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                hue.set(getCustomColor().getHue());
                sat.set(getCustomColor().getSaturation() * 100);
                bright.set(getCustomColor().getBrightness() * 100);
                red.set(doubleToInt(getCustomColor().getRed()));
                green.set(doubleToInt(getCustomColor().getGreen()));
                blue.set(doubleToInt(getCustomColor().getBlue()));
                changeIsLocal = false;
            }
        }
        
        public ColorRectPane() {
            
            getStyleClass().add("color-rect-pane");
            
            customColorProperty().addListener(new ChangeListener<Color>() {

                @Override
                public void changed(ObservableValue<? extends Color> ov, Color t, Color t1) {
                    colorChanged();
                }
            });
            
            colorRectIndicator = new Circle(60, 60, 5, null);
            colorRectIndicator.setStroke(Color.WHITE);
            colorRectIndicator.setEffect(new DropShadow(2, 0, 1, Color.BLACK));
        
            colorRect = new Rectangle(RECT_SIZE, RECT_SIZE);
            customColorProperty().addListener(new ChangeListener<Color>() {
                @Override
                public void changed(ObservableValue<? extends Color> ov, Color t, Color t1) {
                    colorRect.setFill(Color.hsb(hue.getValue(), 1.0, 1.0, clamp(alpha.get()/100)));
                }
            });
            
            colorRectOverlayOne = new Rectangle(RECT_SIZE, RECT_SIZE);
            colorRectOverlayOne.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, 
                    new Stop(0, Color.rgb(255, 255, 255, 1)), 
                    new Stop(1, Color.rgb(255, 255, 255, 0))));
            colorRectOverlayOne.setStroke(Utils.deriveColor(Color.web("#d0d0d0"), -20/100));
        
            EventHandler<MouseEvent> rectMouseHandler = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    final double x = event.getX();
                    final double y = event.getY();
                    sat.set(clamp(x / RECT_SIZE) * 100);
                    bright.set(100 - (clamp(y / RECT_SIZE) * 100));
                }
            };
        
            colorRectOverlayTwo = new Rectangle(RECT_SIZE, RECT_SIZE);
            colorRectOverlayTwo.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, 
                        new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))));
            colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
            colorRectOverlayTwo.setOnMouseClicked(rectMouseHandler);
            
            colorBar = new Rectangle(20, RECT_SIZE);
            colorBar.setFill(createHueGradient());
            colorBar.setStroke(Utils.deriveColor(Color.web("#d0d0d0"), -20/100));

            colorBarIndicator = new Rectangle(24, 10, null);
            colorBarIndicator.setLayoutX(CONTENT_PADDING+colorRect.getWidth()+13);
            colorBarIndicator.setLayoutY((CONTENT_PADDING+(colorBar.getHeight()*(hue.get() / 360))));
            colorBarIndicator.setArcWidth(4);
            colorBarIndicator.setArcHeight(4);
            colorBarIndicator.setStroke(Color.WHITE);
            colorBarIndicator.setEffect(new DropShadow(2, 0, 1, Color.BLACK));
            
            // *********************** Listeners ******************************
            hue.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    colorBarIndicator.setLayoutY((CONTENT_PADDING) + (RECT_SIZE * (hue.get() / 360)));
                }
            });
            sat.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    colorRectIndicator.setCenterX((CONTENT_PADDING + 
                            colorRectIndicator.getRadius()) + (RECT_SIZE * (sat.get() / 100)));
                }
            });
            bright.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    colorRectIndicator.setCenterY((CONTENT_PADDING + 
                            colorRectIndicator.getRadius()) + (RECT_SIZE * (1 - bright.get() / 100)));
                }
            });
            alpha.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    
                }
            });
            EventHandler<MouseEvent> barMouseHandler = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    final double y = event.getY();
                    hue.set(clamp(y / RECT_SIZE) * 360);
                }
            };
            
            colorBar.setOnMouseDragged(barMouseHandler);
            colorBar.setOnMouseClicked(barMouseHandler);
            // create rectangle to capture mouse events to hide
        
            getChildren().addAll(colorRect, colorRectOverlayOne, colorRectOverlayTwo, 
                    colorBar, colorRectIndicator, colorBarIndicator);
           
        }
        
        private void updateValues() {
            changeIsLocal = true;
            //Initialize hue, sat, bright, color, red, green and blue
            hue.set(currentColor.getHue());
            sat.set(currentColor.getSaturation()*100);
            bright.set(currentColor.getBrightness()*100);
            setCustomColor(Color.hsb(hue.get(), clamp(sat.get() / 100), clamp(bright.get() / 100), 
                    clamp(alpha.get()/100)));
            red.set(doubleToInt(getCustomColor().getRed()));
            green.set(doubleToInt(getCustomColor().getGreen()));
            blue.set(doubleToInt(getCustomColor().getBlue()));
            changeIsLocal = false;
        }
        
        @Override public void layoutChildren() {
            double x = getInsets().getLeft();
            double y = getInsets().getTop();
//            double w = getWidth() - (getInsets().getLeft() + getInsets().getRight());
//            double h = getHeight() - (getInsets().getTop() + getInsets().getBottom());
            colorRect.relocate(x, y);
            colorRectOverlayOne.relocate(x, y);
            colorRectOverlayTwo.relocate(x, y);
            
            colorBar.relocate(x+colorRect.prefWidth(-1) + COLORBAR_GAP, y);
        }
        
        @Override public double computePrefWidth(double height) {
            return getInsets().getLeft() + colorRect.prefWidth(-1) + COLORBAR_GAP +
                    colorBar.prefWidth(-1) + (colorBarIndicator.getBoundsInParent().getWidth() - colorBar.prefWidth(-1))
                    + getInsets().getRight();
        }
    }
    
    /* ------------------------------------------------------------------------*/
    
    private enum ColorSettingsMode {
        HSB,
        RGB,
        WEB
    }
    
    private class ControlsPane extends StackPane {
        
        private Label currentColorLabel;
        private Label newColorLabel;
        private Rectangle currentColorRect;
        private Rectangle newColorRect;
        private StackPane currentTransparent; // for opacity
        private StackPane newTransparent; // for opacity
        private GridPane currentAndNewColor;
        private Rectangle currentNewColorBorder;
        private ToggleButton hsbButton;
        private ToggleButton rgbButton;
        private ToggleButton webButton;
        private HBox hBox;
        private GridPane hsbSettings;
        private GridPane rgbSettings;
        private GridPane webSettings;
        
        private GridPane alphaSettings;
        private HBox buttonBox;
        private StackPane whiteBox;
        private ColorSettingsMode colorSettingsMode = ColorSettingsMode.HSB;
        
        private StackPane settingsPane = new StackPane();
        
        public ControlsPane() {
            getStyleClass().add("controls-pane");
            
            currentNewColorBorder = new Rectangle(CONTROLS_WIDTH, 18, null);
            currentNewColorBorder.setStroke(Utils.deriveColor(Color.web("#d0d0d0"), -20/100));
            currentNewColorBorder.setStrokeWidth(2);
            
            currentTransparent = new StackPane();
            currentTransparent.setPrefSize(CONTROLS_WIDTH/2, 18);
            currentTransparent.setId("transparent-current");
            
            newTransparent = new StackPane();
            newTransparent.setPrefSize(CONTROLS_WIDTH/2, 18);
            newTransparent.setId("transparent-new");
            
            currentColorRect = new Rectangle(CONTROLS_WIDTH/2, 18);
            currentColorRect.setFill(currentColor);

            newColorRect = new Rectangle(CONTROLS_WIDTH/2, 18);
            newColorRect.fillProperty().bind(customColorProperty());

            currentColorLabel = new Label("Current Color");
            newColorLabel = new Label("New Color");
            Rectangle spacer = new Rectangle(0, 12);
            
            whiteBox = new StackPane();
            whiteBox.getStyleClass().add("customcolor-controls-background");
            
            hsbButton = new ToggleButton("HSB");
            hsbButton.setId("toggle-button-left");
            rgbButton = new ToggleButton("RGB");
            rgbButton.setId("toggle-button-center");
            webButton = new ToggleButton("Web");
            webButton.setId("toggle-button-right");
            
            hBox = new HBox();
            hBox.getChildren().addAll(hsbButton, rgbButton, webButton);
            
            currentAndNewColor = new GridPane();
            currentAndNewColor.getStyleClass().add("current-new-color-grid");
            currentAndNewColor.add(currentColorLabel, 0, 0, 2, 1);
            currentAndNewColor.add(newColorLabel, 2, 0, 2, 1);
            Region r = new Region();
            r.setPadding(new Insets(1, 128, 1, 128));
            currentAndNewColor.add(r, 0, 1, 4, 1);
            currentAndNewColor.add(currentTransparent, 0, 2, 2, 1);
            currentAndNewColor.add(currentColorRect, 0, 2, 2, 1);
            currentAndNewColor.add(newTransparent, 2, 2, 2, 1);
            currentAndNewColor.add(newColorRect, 2, 2, 2, 1);
            currentAndNewColor.add(spacer, 0, 3, 4, 1);
            
            // Color settings Grid Pane
            alphaSettings = new GridPane();
            alphaSettings.setHgap(5);
            alphaSettings.setVgap(0);
            alphaSettings.setManaged(false);
            alphaSettings.getStyleClass().add("alpha-settings");
//            alphaSettings.setGridLinesVisible(true);
            
            Rectangle spacer4 = new Rectangle(0, 12);
            alphaSettings.add(spacer4, 0, 0, 3, 1);
            
            Label alphaLabel = new Label("Opacity:");
            alphaLabel.setPrefWidth(68);
            alphaSettings.add(alphaLabel, 0, 1);
            
            Slider alphaSlider = new Slider(0, 100, 50);
            alphaSlider.setPrefWidth(100);
            alphaSettings.add(alphaSlider, 1, 1);
            
            IntegerField alphaField = new IntegerField(100);
            alphaField.setSkin(new IntegerFieldSkin(alphaField));
            alphaField.setPrefColumnCount(3);
            alphaField.setMaxWidth(38);
            alphaSettings.add(alphaField, 2, 1);
            
               
            alphaField.valueProperty().bindBidirectional(colorRectPane.alpha);
            alphaSlider.valueProperty().bindBidirectional(colorRectPane.alpha);
            
            Rectangle spacer5 = new Rectangle(0, 15);
            alphaSettings.add(spacer5, 0, 2, 3, 1);
            
            final ToggleGroup group = new ToggleGroup();
            hsbButton.setToggleGroup(group);
            rgbButton.setToggleGroup(group);
            webButton.setToggleGroup(group);
            group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    if (newValue == null) {
                        group.selectToggle(oldValue);
                    } else {
                        if (newValue == hsbButton) {
                            showHSBSettings();
                        } else if (newValue == rgbButton) {
                            showRGBSettings();
                        } else {
                            showWebSettings();
                        }
                    }
                }
            });
            group.selectToggle(hsbButton);            
            
            buttonBox = new HBox(4);
            
            Button saveButton = new Button("Save");
            saveButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent t) {
                    if (colorSettingsMode == ColorSettingsMode.WEB) {
                        customColorProperty.set(webField.valueProperty().get());
                    } else {
                        customColorProperty.set(Color.rgb(colorRectPane.red.get(), 
                            colorRectPane.green.get(), colorRectPane.blue.get(), 
                            clamp(colorRectPane.alpha.get() / 100)));
                    }
                    if (onSave != null) {
                        onSave.run();
                    }
                    dialog.hide();
                }
            });
            
            Button useButton = new Button("Use");
            useButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent t) {
                    customColorProperty.set(Color.rgb(colorRectPane.red.get(), 
                            colorRectPane.green.get(), colorRectPane.blue.get(), 
                            clamp(colorRectPane.alpha.get() / 100)));
                    if (onUse != null) {
                        onUse.run();
                    }
                    dialog.hide();
                }
            });
            
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    customColorProperty.set(currentColor);
                    if (onCancel != null) {
                        onCancel.run();
                    }
                    dialog.hide();
                }
            });
            buttonBox.getChildren().addAll(saveButton, useButton, cancelButton);
            
            getChildren().addAll(currentAndNewColor, currentNewColorBorder, whiteBox, 
                                            hBox, settingsPane, alphaSettings, buttonBox);
        }
        
        private void showHSBSettings() {
            colorSettingsMode = ColorSettingsMode.HSB;
            if (hsbSettings == null) {
                hsbSettings = new GridPane();
                hsbSettings.setHgap(5);
                hsbSettings.setVgap(4);
                hsbSettings.setManaged(false);
                
                Region spacer2 = new Region();
                spacer2.setPrefHeight(3);
                hsbSettings.add(spacer2, 0, 0, 3, 1);
                
                addRow(1, "Hue:", 360, colorRectPane.hue, hsbSettings);
                addRow(2, "Saturation:", 100, colorRectPane.sat, hsbSettings);
                addRow(3, "Brightness:", 100, colorRectPane.bright, hsbSettings);
                
                Region spacer3 = new Region();
                spacer3.setPrefHeight(4);
                hsbSettings.add(spacer3, 0, 4, 3, 1);
            }
            settingsPane.getChildren().setAll(hsbSettings);
        }
        
        
        private void showRGBSettings() {
            colorSettingsMode = ColorSettingsMode.RGB;
            if (rgbSettings == null) {
                rgbSettings = new GridPane();
                rgbSettings.setHgap(5);
                rgbSettings.setVgap(4);
                rgbSettings.setManaged(false);
                
                Region spacer2 = new Region();
                spacer2.setPrefHeight(3);
                rgbSettings.add(spacer2, 0, 0, 3, 1);

                addRow(1, "Red:", 255, colorRectPane.red, rgbSettings);
                addRow(2, "Green:", 255, colorRectPane.green, rgbSettings);
                addRow(3, "Blue:", 255, colorRectPane.blue, rgbSettings);
                
                Region spacer3 = new Region();
                spacer3.setPrefHeight(4);
                rgbSettings.add(spacer3, 0, 4, 3, 1);
            }
            settingsPane.getChildren().setAll(rgbSettings);
            settingsPane.requestLayout();
        }
        
        private void showWebSettings() {
            colorSettingsMode = ColorSettingsMode.WEB;
            if (webSettings == null) {
                webSettings = new GridPane();
                webSettings.setHgap(5);
                webSettings.setVgap(4);
                webSettings.setManaged(false);
                
                Region spacer2 = new Region();
                spacer2.setPrefHeight(3);
                webSettings.add(spacer2, 0, 0, 3, 1);

                Label webLabel = new Label("Web:        ");
                webLabel.setMinWidth(Control.USE_PREF_SIZE);
                webSettings.add(webLabel, 0, 1);

                webField = new WebColorField();
                webField.setSkin(new WebColorFieldSkin(webField));
                webField.valueProperty().bindBidirectional(customColorProperty());
                webField.setPrefColumnCount(6);
                webSettings.add(webField, 1, 1);
                
                Region spacer3 = new Region();
                spacer3.setPrefHeight(22);
                webSettings.add(spacer3, 0, 2, 3, 1);

                Region spacer4 = new Region();
                spacer4.setPrefHeight(22);
                webSettings.add(spacer4, 0, 3, 3, 1);

                Region spacer5 = new Region();
                spacer5.setPrefHeight(4);
                webSettings.add(spacer5, 0, 4, 3, 1);
            } 
            settingsPane.getChildren().setAll(webSettings);
        }
        
        public Label getCurrentColorLabel() {
            return currentColorLabel;
        }
        
        @Override public void layoutChildren() {
            double x = getInsets().getLeft();
            double y = getInsets().getTop();
//            double w = getWidth() - (getInsets().getLeft() + getInsets().getRight());
//            double h = getHeight() - (getInsets().getTop() + getInsets().getBottom());
            currentAndNewColor.resizeRelocate(x,
                    y, CONTROLS_WIDTH, 18);
            currentNewColorBorder.relocate(x, 
                    y+controlsPane.currentColorLabel.prefHeight(-1)+LABEL_GAP); 
            double hBoxX = computeXOffset(currentAndNewColor.prefWidth(-1), hBox.prefWidth(-1), HPos.CENTER);
            
            GridPane settingsGrid = (GridPane)settingsPane.getChildren().get(0);
            settingsGrid.resize(CONTROLS_WIDTH-28, settingsGrid.prefHeight(-1));
            
            double settingsHeight = settingsPane.getChildren().get(0).prefHeight(-1);
            
            whiteBox.resizeRelocate(x, y+currentAndNewColor.prefHeight(-1)+hBox.prefHeight(-1)/2, 
                    CONTROLS_WIDTH, settingsHeight+hBox.prefHeight(-1)/2);
            
            hBox.resizeRelocate(x+hBoxX, y+currentAndNewColor.prefHeight(-1), 
                    hBox.prefWidth(-1), hBox.prefHeight(-1));
            
            settingsPane.resizeRelocate(x+10, y+currentAndNewColor.prefHeight(-1)+hBox.prefHeight(-1),
                    CONTROLS_WIDTH-28, settingsHeight);
            
            alphaSettings.resizeRelocate(x+10, 
                    y+currentAndNewColor.prefHeight(-1)+hBox.prefHeight(-1)+settingsHeight,
                    CONTROLS_WIDTH-28, alphaSettings.prefHeight(-1));
             
            double buttonBoxX = computeXOffset(currentAndNewColor.prefWidth(-1), buttonBox.prefWidth(-1), HPos.RIGHT);
            buttonBox.resizeRelocate(x+buttonBoxX, y+currentAndNewColor.prefHeight(-1)+hBox.prefHeight(-1)+
                    settingsHeight+alphaSettings.prefHeight(-1), buttonBox.prefWidth(-1), buttonBox.prefHeight(-1));
        }
        
        @Override public double computePrefHeight(double width) {
            double settingsHeight = settingsPane.getChildren().get(0).prefHeight(-1);
            return getInsets().getTop() + currentAndNewColor.prefHeight(-1) +
                    hBox.prefHeight(-1) + settingsHeight + 
                    alphaSettings.prefHeight(-1) + buttonBox.prefHeight(-1) +
                    getInsets().getBottom();
            
        }
        
        @Override public double computePrefWidth(double height) {
            return getInsets().getLeft() + CONTROLS_WIDTH + getInsets().getRight();
        }

        private void addRow(int row, String caption, int maxValue, Property<Number> prop, GridPane gridPane) {
            Label label = new Label(caption);
            label.setMinWidth(68);
            gridPane.add(label, 0, row);

            Slider slider = new Slider(0, maxValue, 100);
            slider.setPrefWidth(100);
            gridPane.add(slider, 1, row);

            IntegerField field = new IntegerField(maxValue);
            field.setSkin(new IntegerFieldSkin(field));
            field.setPrefColumnCount(3);
            field.setMaxWidth(38);
            gridPane.add(field, 2, row);
            field.valueProperty().bindBidirectional(prop);
            slider.valueProperty().bindBidirectional(prop);
        }
    }
    
    static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }
    
    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int y = 0; y < 255; y++) {
            offset = (double)(1 - (1.0 / 255) * y);
            int h = (int)((y / 255.0) * 360);
            stops[y] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 1f, 0f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }
    
    private static int doubleToInt(double value) {
        return (int) (value * 255 + 0.5); // Adding 0.5 for rounding only
    }
}
