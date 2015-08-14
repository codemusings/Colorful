/* HuePickerControl.java - A picker control for the hue value of a color.
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

import javafx.event.EventHandler;

import javafx.scene.input.MouseEvent;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;

public class HuePickerControl extends Pane {

    private final ObjectProperty<Double> hueProperty;

    private final WritableImage canvas;

    public HuePickerControl() {
        this(15, 200);
    }

    public HuePickerControl(double width, double height) {

        super.setMinSize(width, height);
        super.setMaxSize(width, height);

        this.hueProperty = new SimpleObjectProperty<>(0.0);

        this.canvas = new WritableImage((int)width - 2, (int)height - 2);
        this.updateCanvas();
        ImageView view = new ImageView(this.canvas);
        super.getChildren().add(view);
        view.relocate(1, 1);

        final HuePickerControl control = this;
        EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent e) {

                double y;

                if (e.getY() < 0)
                    y = 0;
                else if (e.getY() > canvas.getHeight() - 1)
                    y = canvas.getHeight() - 1;
                else
                    y = e.getY();

                double h = y * 360.0 / (control.canvas.getHeight() - 1); 
                control.hueProperty.setValue(h);

                control.updateCanvas();
            }
        };
        view.setOnMouseClicked(handler);
        view.setOnMouseDragged(handler);

        this.hueProperty.addListener(
            (observable, oldValue, newValue) -> {
                control.updateCanvas();
            }
        );
    }

    public ObjectProperty<Double> hueProperty() {
        return this.hueProperty;
    }

    private void updateCanvas() {

        double w = this.canvas.getWidth();
        double h = this.canvas.getHeight();

        PixelWriter writer = this.canvas.getPixelWriter();
        for (int y = 0; y < h; y++) {
            double hue = y * 360.0 / (h - 1); 
            for (int x = 0; x < w; x++) {
                writer.setColor(x, y, Color.hsb(hue, 1.0, 1.0));
            }
        }

        double hue = this.hueProperty.getValue();
        int y = (int)Math.round(hue * (h - 1) / 360.0);
        int[][] left = {
            {0, y - 2},
            {0, y - 1}, {1, y - 1},
            {0, y},     {1, y},     {2, y},
            {0, y + 1}, {1, y + 1},
            {0, y + 2}
        };
        int[][] right = {
            {(int)w - 1, y - 2},
            {(int)w - 1, y - 1}, {(int)w - 2, y - 1},
            {(int)w - 1, y},     {(int)w - 2, y},     {(int)w - 3, y},
            {(int)w - 1, y + 1}, {(int)w - 2, y + 1},
            {(int)w - 1, y + 2}
        };
        for (int i = 0; i < left.length; i++) {
            if (left[i][0] < 0 || left[i][1] < 0)
                continue;
            if (left[i][0] > this.canvas.getWidth() - 1)
                continue;
            if (left[i][1] > this.canvas.getHeight() - 1)
                continue;
            writer.setColor(left[i][0], left[i][1], Color.BLACK);
        }
        for (int i = 0; i < left.length; i++) {
            if (right[i][0] < 0 || right[i][1] < 0)
                continue;
            if (right[i][0] > this.canvas.getWidth() - 1)
                continue;
            if (right[i][1] > this.canvas.getHeight() - 1)
                continue;
            writer.setColor(right[i][0], right[i][1], Color.BLACK);
        }
    }
}
