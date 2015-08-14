/* Controller.java - The Colorful app controller class
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

import java.util.Collections;

import javafx.fxml.FXML;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import javafx.scene.paint.Color;

public class Controller {

    private final DoubleSpinnerValueFactory hueValueFactory
    = new DoubleSpinnerValueFactory(0.0, 360.0);

    private final DoubleSpinnerValueFactory saturationValueFactory
    = new DoubleSpinnerValueFactory(0.0, 1.0, 0.0, 0.01);

    private final DoubleSpinnerValueFactory brightnessValueFactory
    = new DoubleSpinnerValueFactory(0.0, 1.0, 1.0, 0.01);

    @FXML private Spinner<Double> hueSpinner;
    @FXML private Spinner<Double> saturationSpinner;
    @FXML private Spinner<Double> brightnessSpinner;

    private final IntegerSpinnerValueFactory redValueFactory
    = new IntegerSpinnerValueFactory(0, 255, 255);

    private final IntegerSpinnerValueFactory greenValueFactory
    = new IntegerSpinnerValueFactory(0, 255, 255);

    private final IntegerSpinnerValueFactory blueValueFactory
    = new IntegerSpinnerValueFactory(0, 255, 255);

    @FXML private Spinner<Integer> redSpinner;
    @FXML private Spinner<Integer> greenSpinner;
    @FXML private Spinner<Integer> blueSpinner;

    @FXML private SBPickerControl sbPicker;
    @FXML private HuePickerControl huePicker;
    @FXML private Region colorView;

    @FXML private TextField hexField;

    private boolean isUpdating = false;

    @FXML
    private void copyHSBToClipboard() {

        double h = this.hueValueFactory.valueProperty().getValue();
        double s = this.saturationValueFactory.valueProperty().getValue();
        double b = this.brightnessValueFactory.valueProperty().getValue();
        String value = "hsl(";
        value += Math.round(h) + ", ";
        value += Math.round(s * 100) + "%, "; 
        value += Math.round(b * 100) + "%)";
        Clipboard.getSystemClipboard().setContent(
            Collections.<DataFormat, Object>singletonMap(
                DataFormat.PLAIN_TEXT, value
            )
        );
    }

    @FXML
    private void copyRGBToClipboard() {

        int r = this.redValueFactory.valueProperty().getValue();
        int g = this.greenValueFactory.valueProperty().getValue();
        int b = this.blueValueFactory.valueProperty().getValue();
        String value = "rgb(" + r + ", " + g + ", " + b + ")";
        Clipboard.getSystemClipboard().setContent(
            Collections.<DataFormat, Object>singletonMap(
                DataFormat.PLAIN_TEXT, value
            )
        );
    }

    @FXML
    private void copyHexToClipboard() {

        Clipboard.getSystemClipboard().setContent(
            Collections.<DataFormat, Object>singletonMap(
                DataFormat.PLAIN_TEXT, this.hexField.textProperty().getValue()
            )
        );
    }

    @FXML
    private void initialize() {

        Bindings.<Double>bindBidirectional(
            this.sbPicker.brightnessProperty(),
            this.brightnessValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.sbPicker.hueProperty(),
            this.hueValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.sbPicker.saturationProperty(),
            this.saturationValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.huePicker.hueProperty(),
            this.hueValueFactory.valueProperty()
        );

        final Controller ctrl = this;

        ChangeListener<Double> hsbListener = new ChangeListener<Double>() {

            @Override
            public void changed(ObservableValue<? extends Double> value,
                Double oldValue, Double newValue)
            {
                if (ctrl.isUpdating)
                    return;
                else
                    ctrl.isUpdating = true;

                Color c = Color.hsb(
                    ctrl.hueValueFactory.valueProperty().getValue(),
                    ctrl.saturationValueFactory.valueProperty().getValue(),
                    ctrl.brightnessValueFactory.valueProperty().getValue()
                );
                ctrl.redValueFactory.valueProperty().setValue(
                    (int)Math.round(c.getRed() * 255)
                );
                ctrl.greenValueFactory.valueProperty().setValue(
                    (int)Math.round(c.getGreen() * 255)
                );
                ctrl.blueValueFactory.valueProperty().setValue(
                    (int)Math.round(c.getBlue() * 255)
                );
                ctrl.colorView.setBackground(
                    new Background(new BackgroundFill(c, null, null))
                );
                ctrl.hexField.textProperty().setValue(
                    String.format(
                        "#%02X%02X%02X",
                        Math.round(c.getRed() * 255), 
                        Math.round(c.getGreen() * 255), 
                        Math.round(c.getBlue() * 255) 
                    )
                );

                ctrl.isUpdating = false;
            }
        };
        this.brightnessValueFactory.valueProperty().addListener(hsbListener);
        this.hueValueFactory.valueProperty().addListener(hsbListener);
        this.saturationValueFactory.valueProperty().addListener(hsbListener);

        ChangeListener<Integer> rgbListener = new ChangeListener<Integer>() {

            @Override
            public void changed(ObservableValue<? extends Integer> value,
                Integer oldValue, Integer newValue)
            {
                if (ctrl.isUpdating)
                    return;
                else
                    ctrl.isUpdating = true;

                Color c = Color.rgb(
                    ctrl.redValueFactory.valueProperty().getValue(),
                    ctrl.greenValueFactory.valueProperty().getValue(),
                    ctrl.blueValueFactory.valueProperty().getValue()
                );
                ctrl.hueValueFactory.valueProperty().setValue(c.getHue());
                ctrl.saturationValueFactory.valueProperty().setValue(
                    c.getSaturation()
                );
                ctrl.brightnessValueFactory.valueProperty().setValue(
                    c.getBrightness()
                );
                ctrl.colorView.setBackground(
                    new Background(new BackgroundFill(c, null, null))
                );
                ctrl.hexField.textProperty().setValue(
                    String.format(
                        "#%02X%02X%02X",
                        Math.round(c.getRed() * 255), 
                        Math.round(c.getGreen() * 255), 
                        Math.round(c.getBlue() * 255) 
                    )
                );

                ctrl.isUpdating = false;
            }
        };
        this.redValueFactory.valueProperty().addListener(rgbListener);
        this.greenValueFactory.valueProperty().addListener(rgbListener);
        this.blueValueFactory.valueProperty().addListener(rgbListener);

        this.hueSpinner.setValueFactory(this.hueValueFactory);
        this.saturationSpinner.setValueFactory(this.saturationValueFactory);
        this.brightnessSpinner.setValueFactory(this.brightnessValueFactory);

        this.redSpinner.setValueFactory(this.redValueFactory);
        this.greenSpinner.setValueFactory(this.greenValueFactory);
        this.blueSpinner.setValueFactory(this.blueValueFactory);

        this.colorView.setBackground(
            new Background(new BackgroundFill(Color.WHITE, null, null))
        );
    }
}
