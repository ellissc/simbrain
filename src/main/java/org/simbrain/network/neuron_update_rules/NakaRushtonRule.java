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
import org.simbrain.network.updaterules.interfaces.ClippedUpdateRule;
import org.simbrain.network.updaterules.interfaces.NoisyUpdateRule;
import org.simbrain.network.util.MatrixDataHolder;
import org.simbrain.network.util.NakaMatrixData;
import org.simbrain.network.util.NakaScalarData;
import org.simbrain.network.util.ScalarDataHolder;
import org.simbrain.util.UserParameter;
import org.simbrain.util.stats.ProbabilityDistribution;
import org.simbrain.util.stats.distributions.UniformRealDistribution;

/**
 * <b>NakaRushtonNeuron</b> is a firing-rate based neuron which is intended to
 * model spike rates of real neurons. It is used extensively in Hugh Wilson's
 * Spikes, Decisions, and Action. p. 20-21.
 */
public class NakaRushtonRule extends NeuronUpdateRule implements ClippedUpdateRule, NoisyUpdateRule {

    /**
     * The default activation ceiling.
     */
    public static final int DEFAULT_UPPER_BOUND = 100;

    /**
     * Steepness.
     */
    @UserParameter(
            label = "Steepness",
            description = "This value controls the steepness of the sigmoidal-like function S(W).",
            increment = .1,
            order = 1)
    private double steepness = 2;

    /**
     * Semi saturation constant.
     */
    @UserParameter(
            label = "Semi-Saturation Constant",
            description = "This value is the point at which S(W) reaches half of its maximum value.",
            increment = .1,
            order = 2)
    private double semiSaturationConstant = 120;

    /**
     * Time constant of spike rate adaptation.
     */
    @UserParameter(
            label = "Adaptation Time Constant",
            description = "This value controls the rate at which the adaptation variable tends to "
                    + "its minimum value.",
            increment = .1,
            order = 7)
    private double adaptationTimeConstant = 1;

    /**
     * Parameter of spike rate adaptation.
     */
    @UserParameter(
            label = "Adaptation Parameter",
            description = "The parameter of spike rate adaptation.",
            increment = .1,
            order = 6)
    private double adaptationParameter = .7;

    /**
     * Whether to use spike rate adaptation or not.
     */
    @UserParameter(
            label = "Use Adaptation",
            description = "If this is set to true, spike rate adaptation is utilized.",
            increment = .1,
            order = 5)
    private boolean useAdaptation = false;

    /**
     * Time constant.
     */
    @UserParameter(
            label = "Time Constant",
            description = "This value controls the rate at which the activation tends to the fixed "
                    + "point S(W).",
            increment = .1,
            order = 3)
    private double timeConstant = 1;

    /**
     * Noise generator.
     */
    private ProbabilityDistribution noiseGenerator = new UniformRealDistribution();

    /**
     * Add noise to neuron.
     */
    private boolean addNoise = false;

    /**
     * The upper bound of the activity.
     */
    private double upperBound = DEFAULT_UPPER_BOUND;

    /**
     * The lower bound of the activity.
     */
    private double lowerBound = 0;

    /**
     * Bounded update rule is automatically clippable.  It is not needed here since sigmoids automatically respect
     * upper and lower bounds but can still be turned on to constrain contextual increment and decrement.
     */
    private boolean isClipped = false;

    /**
     * Default constructor.
     */
    public NakaRushtonRule() {
    }

    @Override
    public TimeType getTimeType() {
        return TimeType.CONTINUOUS;
    }

    @Override
    public NakaRushtonRule deepCopy() {
        NakaRushtonRule rn = new NakaRushtonRule();
        rn.setSteepness(getSteepness());
        rn.setSemiSaturationConstant(getSemiSaturationConstant());
        rn.setUpperBound(getUpperBound());
        rn.setAddNoise(getAddNoise());
        rn.setUseAdaptation(getUseAdaptation());
        rn.setAdaptationParameter(getAdaptationParameter());
        rn.setAdaptationTimeConstant(getAdaptationTimeConstant());
        rn.noiseGenerator = noiseGenerator.deepCopy();
        rn.setTimeStepSupplier(getTimeStepSupplier());
        return rn;
    }

    @Override
    public void apply(Layer array, MatrixDataHolder data) {
        for (int i = 0; i < array.outputSize(); i++) {
            array.getOutputs().set(
                    i, 0,
                    nakaRushtonRule(
                            array.getInputs().get(i, 0),
                            array.getOutputs().get(i, 0),
                            array.getNetwork().getTimeStep(),
                            ((NakaMatrixData)data).getA().get(i, 0)
                    )
            );
        }
    }

    @Override
    public void apply(Neuron neuron, ScalarDataHolder data) {
        neuron.setActivation(nakaRushtonRule(neuron.getInput(), neuron.getActivation(),
                neuron.getNetwork().getTimeStep(), ((NakaScalarData)data).getA()));
    }

    public double nakaRushtonRule(double input, double activation, double timeStep, double a) {

        double val = activation;

        // Update adaptation term; see Spike, p. 81
        if (useAdaptation) {
            a += (timeStep / adaptationTimeConstant) * (adaptationParameter * val - a);
        } else {
            a = 0;
        }

        double s;
        if (input > 0) {
            s = (getUpperBound() * Math.pow(input, steepness))
                            / (Math.pow(semiSaturationConstant + a, steepness)
                            + Math.pow(input, steepness));
        } else {
            s = 0;
        }

        if (addNoise) {
            val += (timeStep * (((1 / timeConstant) * (-val + s)) + noiseGenerator.sampleDouble()));
        } else {
            val += (timeStep * ((1 / timeConstant) * (-val + s)));
        }

        return val;
    }

    @Override
    public MatrixDataHolder createMatrixData(int size) {
        return new NakaMatrixData(size);
    }

    @Override
    public ScalarDataHolder createScalarData() {
        return new NakaScalarData();
    }

    public double getSemiSaturationConstant() {
        return semiSaturationConstant;
    }

    public void setSemiSaturationConstant(final double semiSaturationConstant) {
        this.semiSaturationConstant = semiSaturationConstant;
    }

    public double getSteepness() {
        return steepness;
    }

    public void setSteepness(final double steepness) {
        this.steepness = steepness;
    }

    public double getTimeConstant() {
        return timeConstant;
    }

    public void setTimeConstant(final double timeConstant) {
        this.timeConstant = timeConstant;
    }

    public boolean getAddNoise() {
        return addNoise;
    }

    public void setAddNoise(final boolean addNoise) {
        this.addNoise = addNoise;
    }

    @Override
    public ProbabilityDistribution getNoiseGenerator() {
        return noiseGenerator;
    }

    @Override
    public void setNoiseGenerator(final ProbabilityDistribution noise) {
        this.noiseGenerator = noise;
    }

    public boolean getUseAdaptation() {
        return useAdaptation;
    }

    public void setUseAdaptation(final boolean useAdaptation) {
        this.useAdaptation = useAdaptation;
    }

    public double getAdaptationTimeConstant() {
        return adaptationTimeConstant;
    }

    public void setAdaptationTimeConstant(final double adaptationTimeConstant) {
        this.adaptationTimeConstant = adaptationTimeConstant;
    }

    @Override
    public void clear(Neuron neuron) {
        super.clear(neuron);
    }

    public double getAdaptationParameter() {
        return adaptationParameter;
    }


    public void setAdaptationParameter(final double adaptationParameter) {
        this.adaptationParameter = adaptationParameter;
    }

    @Override
    public String getName() {
        return "Naka-Rushton";
    }

    @Override
    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public void setUpperBound(double ceiling) {
        this.upperBound = ceiling;
    }

    @Override
    public double getLowerBound() {
        return lowerBound;
    }

    @Override
    public void setLowerBound(double floor) {
        this.lowerBound = floor;
    }

    @Override
    public boolean isClipped() {
        return isClipped;
    }

    @Override
    public void setClipped(boolean clipped) {
        isClipped = clipped;
    }
}
