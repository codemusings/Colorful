/* ColorView.java - A view for the currently selected color.
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

import javafx.scene.effect.BlendMode;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

public class ColorView extends Pane {

    private final ObjectProperty<Double> hueProperty;
    private final ObjectProperty<Double> saturationProperty;
    private final ObjectProperty<Double> brightnessProperty;
    private final ObjectProperty<Double> opacityProperty;

    private final WritableImage background;
    private final Region foreground;

    public ColorView() {
        this(200, 200);
    }

    public ColorView(double width, double height) {

        super.setMinSize(width, height);
        super.setMaxSize(width, height);
        super.setBlendMode(BlendMode.SRC_OVER);

        this.hueProperty = new SimpleObjectProperty<>(0.0);
        this.saturationProperty = new SimpleObjectProperty<>(0.0);
        this.brightnessProperty = new SimpleObjectProperty<>(1.0);
        this.opacityProperty = new SimpleObjectProperty<>(1.0);

        /* image view used to draw the background grid */
        this.background = new WritableImage((int)width - 2, (int)height - 2);
        this.drawBackground();
        ImageView view = new ImageView(this.background);
        super.getChildren().add(view);
        view.relocate(1, 1);

        /* region to display the currently selected color */
        this.foreground = new Region();
        this.foreground.setPrefSize((int)width - 2, (int)height - 2);
        this.updateForeground();
        super.getChildren().add(foreground);
        this.foreground.relocate(1, 1);

        /* update region color on change */
        ChangeListener<Double> listener = (observable, oldValue, newValue) -> {
            this.updateForeground();
        };
        this.brightnessProperty.addListener(listener);
        this.hueProperty.addListener(listener);
        this.saturationProperty.addListener(listener);
        this.opacityProperty.addListener(listener);
    }

    private void drawBackground() {

        double w = this.background.getWidth();
        double h = this.background.getHeight();

        PixelWriter writer = this.background.getPixelWriter();

        for (int y = 0; y < h; y++) {
            double opacity = (h - 1 - y) / (h - 1); 
            for (int x = 0; x < w; x++) {
                if (x / 3 % 2 == y / 3 % 2)
                    writer.setColor(x, y, Color.gray(0.75));
                else
                    writer.setColor(x, y, Color.gray(1.0));
            }
        }
    }

    public ObjectProperty<Double> brightnessProperty() {
        return this.brightnessProperty;
    }

    public ObjectProperty<Double> hueProperty() {
        return this.hueProperty;
    }

    public ObjectProperty<Double> opacityValueProperty() {
        return this.opacityProperty;
    }

    public ObjectProperty<Double> saturationProperty() {
        return this.saturationProperty;
    }

    private void updateForeground() {

        Color c = Color.hsb(
            this.hueProperty.getValue(),
            this.saturationProperty.getValue(),
            this.brightnessProperty.getValue(),
            this.opacityProperty.getValue()
        );
        this.foreground.setBackground(
            new Background(new BackgroundFill(c, null, null))
        );
    }
}
