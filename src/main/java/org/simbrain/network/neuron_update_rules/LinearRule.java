/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.neuron_update_rules;

import org.simbrain.network.core.Layer;
import org.simbrain.network.core.Network.TimeType;
import org.simbrain.network.core.Neuron;
import org.simbrain.network.core.NeuronUpdateRule;
import org.simbrain.network.neuron_update_rules.interfaces.DifferentiableUpdateRule;
import org.simbrain.network.updaterules.interfaces.BoundedUpdateRule;
import org.simbrain.network.updaterules.interfaces.NoisyUpdateRule;
import org.simbrain.network.util.BiasedMatrixData;
import org.simbrain.network.util.BiasedScalarData;
import org.simbrain.util.UserParameter;
import org.simbrain.util.stats.ProbabilityDistribution;
import org.simbrain.util.stats.distributions.UniformRealDistribution;

import static java.lang.Math.max;

/**
 * <b>LinearNeuron</b> is a standard linear neuron.
 */
public class LinearRule extends NeuronUpdateRule<BiasedScalarData, BiasedMatrixData> implements DifferentiableUpdateRule,
        BoundedUpdateRule, NoisyUpdateRule {

    /**
     * The Default upper bound.
     */
    private static final double DEFAULT_UPPER_BOUND = 10.0;

    /**
     * The Default lower bound.
     */
    private static final double DEFAULT_LOWER_BOUND = -10.0;

    /**
     * Default clipping setting.
     */
    private static final boolean DEFAULT_CLIPPING = true;

    @UserParameter(
            label = "Type",
            description = "No clipping, clip floor and ceiling (piecewise linear), clip floor (relu)",
            order = 10)
    private ClippingType clippingType = ClippingType.PiecewiseLinear;

    /**
     * Note that Relu case ignores provided bounds, though those bounds are still used by contextual increment and
     * decrement.
     */
    public enum ClippingType {NoClipping, PiecewiseLinear, Relu}

    @UserParameter(
            label = "Slope",
            description = "Slope of linear rule",
            increment = .1,
            order = 20)
    private double slope = 1;

    private ProbabilityDistribution noiseGenerator = new UniformRealDistribution();

    /**
     * Add noise to the neuron.
     */
    private boolean addNoise = false;

    /**
     * Clipping.
     */
    private boolean clipping = DEFAULT_CLIPPING;

    /**
     * The upper bound of the activity if clipping is used.
     */
    private double upperBound = DEFAULT_UPPER_BOUND;

    /**
     * The lower bound of the activity if clipping is used.
     */
    private double lowerBound = DEFAULT_LOWER_BOUND;


    @Override
    public void apply(Neuron neuron, BiasedScalarData data) {
        neuron.setActivation(linearRule(neuron.getInput(), data.getBias()));
    }
    @Override
    public void apply(Layer array, BiasedMatrixData data) {
        for (int i = 0; i < array.getOutputs().nrow() ; i++) {
            array.getOutputs().set(i, 0, linearRule(array.getInputs().get(i, 0), data.getBiases().get(i, 0)));
        }
    }

    public double linearRule(double input, double bias) {
        double ret = input * slope + bias;
        if (addNoise) {
            ret  += noiseGenerator.sampleDouble();
        }
        return switch (clippingType) {
            case NoClipping -> ret;
            case Relu -> max(0, ret);
            case PiecewiseLinear -> clip(ret);
        };
    }

    @Override
    public BiasedMatrixData createMatrixData(int size) {
        return new BiasedMatrixData(size);
    }

    @Override
    public BiasedScalarData createScalarData() {
        return new BiasedScalarData();
    }

    @Override
    public TimeType getTimeType() {
        return TimeType.DISCRETE;
    }

    @Override
    public LinearRule deepCopy() {
        LinearRule ln = new LinearRule();
        ln.setSlope(getSlope());
        ln.setClipped(isClipped());
        ln.setAddNoise(getAddNoise());
        ln.setUpperBound(getUpperBound());
        ln.setLowerBound(getLowerBound());
        ln.noiseGenerator = noiseGenerator.deepCopy();
        return ln;
    }

    @Override
    public double getDerivative(double val) {
        return switch (clippingType) {
            case NoClipping -> slope;
            case Relu -> val <= 0 ? 0 : slope;
            case PiecewiseLinear -> (val <= lowerBound || val >= upperBound) ? 0 : slope;
        };
    }

    public void setSlope(final double slope) {
        this.slope = slope;
    }

    public double getSlope() {
        return slope;
    }

    @Override
    public ProbabilityDistribution getNoiseGenerator() {
        return noiseGenerator;
    }

    @Override
    public void setNoiseGenerator(final ProbabilityDistribution noise) {
        this.noiseGenerator = noise;
    }

    @Override
    public boolean getAddNoise() {
        return addNoise;
    }

    @Override
    public void setAddNoise(final boolean addNoise) {
        this.addNoise = addNoise;
    }

    @Override
    public String getName() {
        return "Linear";
    }

    @Override
    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public double getLowerBound() {
        return lowerBound;
    }

    @Override
    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public boolean isClipped() {
        return clipping;
    }

    @Override
    public void setClipped(boolean clipping) {
        this.clipping = clipping;
    }

    public ClippingType getClippingType() {
        return clippingType;
    }

    public void setClippingType(ClippingType clippingType) {
        this.clippingType = clippingType;
    }
}
