/* SBPickerControl.java - A picker control for the saturation and brightness
 *                        value of a color.
 *
 * Copyright (c) 2015, Tilo Villwock <codemusings at gmail dot com>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   
 *   * Neither the name of Colorful nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package de.codemusings.colorful;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.beans.value.ChangeListener;

import javafx.event.EventHandler;

import javafx.scene.input.MouseEvent;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;

public class SBPickerControl extends Pane {

    private final ObjectProperty<Double> hueProperty;
    private final ObjectProperty<Double> saturationProperty;
    private final ObjectProperty<Double> brightnessProperty;

    private final WritableImage canvas;

    public SBPickerControl() {
        this(200, 200);
    }

    public SBPickerControl(double width, double height) {

        super.setMinSize(width, height);
        super.setMaxSize(width, height);

        this.hueProperty = new SimpleObjectProperty<>(0.0);
        this.saturationProperty = new SimpleObjectProperty<>(0.0);
        this.brightnessProperty = new SimpleObjectProperty<>(1.0);

        this.canvas = new WritableImage((int)width - 2, (int)height - 2);
        this.updateCanvas();
        ImageView view = new ImageView(this.canvas);
        super.getChildren().add(view);
        view.relocate(1, 1);

        final SBPickerControl control = this;
        EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent e) {

                double x, y;

                if (e.getX() < 0)
                    x = 0;
                else if (e.getX() > canvas.getWidth() - 1)
                    x = canvas.getWidth() - 1;
                else
                    x = e.getX();

                if (e.getY() < 0)
                    y = 0;
                else if (e.getY() > canvas.getHeight() - 1)
                    y = canvas.getHeight() - 1;
                else
                    y = e.getY();

                double w = canvas.getWidth();
                double h = canvas.getHeight();

                control.saturationProperty.setValue(x / (w - 1));
                control.brightnessProperty.setValue((h - 1 - y) / (h - 1));

                control.updateCanvas();
            }
        };
        view.setOnMouseClicked(handler);
        view.setOnMouseDragged(handler);

        ChangeListener<Double> listener = (observable, oldValue, newValue) -> {
                control.updateCanvas();
        };
        this.brightnessProperty.addListener(listener);
        this.hueProperty.addListener(listener);
        this.saturationProperty.addListener(listener);
    }

    public ObjectProperty<Double> brightnessProperty() {
        return this.brightnessProperty;
    }

    public ObjectProperty<Double> hueProperty() {
        return this.hueProperty;
    }

    public ObjectProperty<Double> saturationProperty() {
        return this.saturationProperty;
    }

    private void updateCanvas() {

        double w = this.canvas.getWidth();
        double h = this.canvas.getHeight();

        PixelWriter writer = this.canvas.getPixelWriter();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double saturation = x / (w - 1);
                double brightness = (h - 1 - y) / (h - 1);
                writer.setColor(x, y, Color.hsb(
                    this.hueProperty.getValue(),
                    saturation,
                    brightness
                ));
            }
        }

        double s = this.saturationProperty.getValue();
        double b = this.brightnessProperty.getValue();
        int x = (int)Math.round(s * (w - 1) / 1.0);
        int y = (int)(h - 1) - (int)Math.round(b * (h - 1) / 1.0);
        int[][] rectangle = {
            {x - 1, y - 1}, {x, y - 1}, {x + 1, y - 1},
            {x - 1, y},                 {x + 1, y},
            {x - 1, y + 1}, {x, y + 1}, {x + 1, y + 1}
        };
        Color color = this.canvas.getPixelReader().getColor(x, y).invert();
        for (int i = 0; i < rectangle.length; i++) {
            if (rectangle[i][0] < 0)
                continue;
            if (rectangle[i][1] < 0)
                continue;
            if (rectangle[i][0] > this.canvas.getWidth() - 1)
                continue;
            if (rectangle[i][1] > this.canvas.getHeight() - 1)
                continue;
            writer.setColor(rectangle[i][0], rectangle[i][1], color);
        }
    }
}
