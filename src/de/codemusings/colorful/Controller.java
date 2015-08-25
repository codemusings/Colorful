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
import javafx.scene.layout.Pane;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import javafx.scene.paint.Color;

public class Controller implements ChangeListener<Number> {

    @FXML private Spinner<Double> hueSpinner;
    @FXML private Spinner<Double> saturationSpinner;
    @FXML private Spinner<Double> brightnessSpinner;
    @FXML private Spinner<Double> opacitySpinner;

    @FXML private Spinner<Integer> redSpinner;
    @FXML private Spinner<Integer> greenSpinner;
    @FXML private Spinner<Integer> blueSpinner;
    @FXML private TextField hexField;

    @FXML private SBPickerControl sbPicker;
    @FXML private HuePickerControl huePicker;
    @FXML private OpacityPickerControl opacityPicker;
    @FXML private ColorView colorView;

    private final DoubleSpinnerValueFactory hueValueFactory
    = new DoubleSpinnerValueFactory(0.0, 360.0);

    private final DoubleSpinnerValueFactory saturationValueFactory
    = new DoubleSpinnerValueFactory(0.0, 1.0, 0.0, 0.01);

    private final DoubleSpinnerValueFactory brightnessValueFactory
    = new DoubleSpinnerValueFactory(0.0, 1.0, 1.0, 0.01);

    private final DoubleSpinnerValueFactory opacityValueFactory
    = new DoubleSpinnerValueFactory(0.0, 1.0, 1.0, 0.01);

    private final IntegerSpinnerValueFactory redValueFactory
    = new IntegerSpinnerValueFactory(0, 255, 255);

    private final IntegerSpinnerValueFactory greenValueFactory
    = new IntegerSpinnerValueFactory(0, 255, 255);

    private final IntegerSpinnerValueFactory blueValueFactory
    = new IntegerSpinnerValueFactory(0, 255, 255);

    private boolean isUpdating = false;

    @Override
    public void changed(ObservableValue<? extends Number> observable,
        Number oldValue, Number newValue)
    {
        if (this.isUpdating)
            return;

        this.isUpdating = true;

        Color c = null;
        if (this.isRGBSource(observable)) {
            c = Color.rgb(
                this.redValueFactory.getValue(),
                this.greenValueFactory.getValue(),
                this.blueValueFactory.getValue(),
                this.opacityValueFactory.getValue()
            );
        } else {
            c = Color.hsb(
                this.hueValueFactory.getValue(),
                this.saturationValueFactory.getValue(),
                this.brightnessValueFactory.getValue(),
                this.opacityValueFactory.getValue()
            );
        }

        if (this.isHSBSource(observable)) {
            this.redValueFactory.setValue((int)Math.round(c.getRed() * 255));
            this.greenValueFactory.setValue(
                (int)Math.round(c.getGreen() * 255)
            );
            this.blueValueFactory.setValue((int)Math.round(c.getBlue() * 255));
        } else if (this.isRGBSource(observable)) {
            this.hueValueFactory.setValue(c.getHue());
            this.saturationValueFactory.setValue(c.getSaturation());
            this.brightnessValueFactory.setValue(c.getBrightness());
        }

        if (this.opacityValueFactory.getValue() == 1.0) {
            this.hexField.setText(String.format(
                "#%02X%02X%02X",
                Math.round(c.getRed() * 255), 
                Math.round(c.getGreen() * 255), 
                Math.round(c.getBlue() * 255) 
            ));
        } else {
            this.hexField.setText(String.format(
                "#%02X%02X%02X%02X",
                Math.round(c.getRed() * 255), 
                Math.round(c.getGreen() * 255), 
                Math.round(c.getBlue() * 255),
                Math.round(c.getOpacity() * 255) 
            ));
        }

        this.isUpdating = false;
    }

    @FXML
    private void copyHexToClipboard() {
        this.copyToClipboard(this.hexField.textProperty().getValue());
    }

    @FXML
    private void copyHSBToClipboard() {
        double h = this.hueValueFactory.getValue();
        double s = this.saturationValueFactory.getValue();
        double b = this.brightnessValueFactory.getValue();
        double o = this.opacityValueFactory.getValue();
        String value = o < 1.0 ? "hsla" : "hsl";
        value += "(";
        value += Math.round(h) + ", ";
        value += Math.round(s * 100) + "%, "; 
        value += Math.round(b * 100) + "%";
        if (o < 1.0)
            value += ", " + String.format(java.util.Locale.US, "%.2f", o);
        value += ")";
        this.copyToClipboard(value);
    }

    @FXML
    private void copyRGBToClipboard() {
        int r = this.redValueFactory.valueProperty().getValue();
        int g = this.greenValueFactory.valueProperty().getValue();
        int b = this.blueValueFactory.valueProperty().getValue();
        double o = this.opacityValueFactory.getValue();
        String value = o < 1.0 ? "rgba" : "rgb";
        value += "(" + r + ", " + g + ", " + b;
        if (o < 1.0)
            value += ", " + String.format(java.util.Locale.US, "%.2f", o);
        value += ")";
        this.copyToClipboard(value);
    }

    private void copyToClipboard(String value) {
        Clipboard.getSystemClipboard().setContent(
            Collections.<DataFormat, Object>singletonMap(
                DataFormat.PLAIN_TEXT, value
            )
        );
    }

    @FXML
    private void initialize() {

        /* saturation brightness picker bindings */
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

        /* hue picker binding */
        Bindings.<Double>bindBidirectional(
            this.huePicker.hueProperty(),
            this.hueValueFactory.valueProperty()
        );

        /* opacity picker bindings */
        Bindings.<Double>bindBidirectional(
            this.opacityPicker.brightnessProperty(),
            this.brightnessValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.opacityPicker.hueProperty(),
            this.hueValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.opacityPicker.opacityValueProperty(),
            this.opacityValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.opacityPicker.saturationProperty(),
            this.saturationValueFactory.valueProperty()
        );

        /* color view bindings */
        Bindings.<Double>bindBidirectional(
            this.colorView.brightnessProperty(),
            this.brightnessValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.colorView.hueProperty(),
            this.hueValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.colorView.opacityValueProperty(),
            this.opacityValueFactory.valueProperty()
        );
        Bindings.<Double>bindBidirectional(
            this.colorView.saturationProperty(),
            this.saturationValueFactory.valueProperty()
        );

        /* attach listener to update fields */
        this.brightnessValueFactory.valueProperty().addListener(this);
        this.hueValueFactory.valueProperty().addListener(this);
        this.saturationValueFactory.valueProperty().addListener(this);
        this.opacityValueFactory.valueProperty().addListener(this);

        this.redValueFactory.valueProperty().addListener(this);
        this.greenValueFactory.valueProperty().addListener(this);
        this.blueValueFactory.valueProperty().addListener(this);

        /* link value factories */
        this.hueSpinner.setValueFactory(this.hueValueFactory);
        this.saturationSpinner.setValueFactory(this.saturationValueFactory);
        this.brightnessSpinner.setValueFactory(this.brightnessValueFactory);
        this.opacitySpinner.setValueFactory(this.opacityValueFactory);

        this.redSpinner.setValueFactory(this.redValueFactory);
        this.greenSpinner.setValueFactory(this.greenValueFactory);
        this.blueSpinner.setValueFactory(this.blueValueFactory);
    }

    private boolean isHSBSource(ObservableValue<? extends Number> observable) {

        return observable == this.hueValueFactory.valueProperty() ||
               observable == this.saturationValueFactory.valueProperty() ||
               observable == this.brightnessValueFactory.valueProperty();
    }

    private boolean isRGBSource(ObservableValue<? extends Number> observable) {

        return observable == this.redValueFactory.valueProperty() ||
               observable == this.greenValueFactory.valueProperty() ||
               observable == this.blueValueFactory.valueProperty();
    }
}
