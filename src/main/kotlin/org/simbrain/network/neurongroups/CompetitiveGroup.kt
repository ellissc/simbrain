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
package org.simbrain.network.subnetworks;

import org.simbrain.network.core.Network;
import org.simbrain.network.core.Neuron;
import org.simbrain.network.core.Synapse;
import org.simbrain.network.groups.NeuronGroup;
import org.simbrain.network.neuron_update_rules.LinearRule;
import org.simbrain.util.UserParameter;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/**
 * <b>Competitive</b> implements a simple competitive network.
 * <p>
 * Current implementations include Rummelhart-Zipser (PDP, 151-193), and
 * Alvarez-Squire 1994, PNAS, 7041-7045.
 *
 * @author Jeff Yoshimi
 */
public class CompetitiveGroup extends NeuronGroup {

    // TODO: Add "recall" function as with SOM

    public static final double DEFAULT_LEARNING_RATE  = .1;
    public static final double DEFAULT_WIN_VALUE  = 1;
    public static final double DEFAULT_LOSE_VALUE  = 0;
    public static final boolean DEFAULT_NORM_INPUTS  = true;
    public static final boolean DEFAULT_USE_LEAKY  = false;
    public static final double DEFAULT_LEAKY_RATE  = DEFAULT_LEARNING_RATE/4;
    public static final double DEFAULT_DECAY_PERCENT  = .0008;
    public static final UpdateMethod DEFAULT_UPDATE_METHOD = UpdateMethod.RUMM_ZIPSER;

    @UserParameter(label = "Update method", order = 30)
    private UpdateMethod updateMethod = DEFAULT_UPDATE_METHOD;

    @UserParameter(label = "Learning rate", order = 40)
    private double learningRate = DEFAULT_LEARNING_RATE;

    @UserParameter(label = "Winner Value", order = 50)
    private double winValue = DEFAULT_WIN_VALUE;

    @UserParameter(label = "Lose Value", order = 60)
    private double loseValue = DEFAULT_LOSE_VALUE;

    @UserParameter(label = "Normalize inputs", order = 70)
    private boolean normalizeInputs = DEFAULT_NORM_INPUTS;

    @UserParameter(label = "Use Leaky learning", order = 80)
    private boolean useLeakyLearning = DEFAULT_USE_LEAKY;

    @UserParameter(label = "Leaky learning rate", conditionalEnablingMethod = "usesLeakyLearning", order = 90)
    private double leakyLearningRate = DEFAULT_LEAKY_RATE;

    /**
     * Percentage by which to decay synapses on each update for Alvarez-Squire update.
     */
    @UserParameter(label = "Decay percent", order = 100)
    private double synpaseDecayPercent = DEFAULT_DECAY_PERCENT;

    /**
     * Max, value and activation values.
     */
    private double max, val, activation;

    /**
     * Winner value.
     */
    private int winner;

    /**
     * Specific implementation of competitive learning.
     */
    public enum UpdateMethod {
        /**
         * Rummelhart-Zipser.
         */
        RUMM_ZIPSER {
            @Override
            public String toString() {
                return "Rummelhart-Zipser";
            }
        },

        /**
         * Alvarez-Squire.
         */
        ALVAREZ_SQUIRE {
            @Override
            public String toString() {
                return "Alvarez-Squire";
            }
        }
    }

    /**
     * Constructs a competitive network with specified number of neurons.
     *
     * @param numNeurons size of this network in neurons
     * @param root       reference to Network.
     */
    public CompetitiveGroup(final Network root, final int numNeurons) {
        super(root);
        for (int i = 0; i < numNeurons; i++) {
            addNeuron(new Neuron(root, new LinearRule()));
        }
        setLabel("Competitive Group");
    }

    /**
     * Copy constructor.
     *
     * @param newRoot new root network
     * @param oldNet  old network.
     */
    public CompetitiveGroup(Network newRoot, CompetitiveGroup oldNet) {
        super(newRoot, oldNet);
        this.learningRate = oldNet.getLearningRate();
        this.winValue = oldNet.getWinValue();
        this.loseValue = oldNet.loseValue;
        this.normalizeInputs = oldNet.normalizeInputs;
        this.useLeakyLearning = oldNet.useLeakyLearning;
        this.leakyLearningRate = oldNet.leakyLearningRate;
        this.synpaseDecayPercent = oldNet.synpaseDecayPercent;
        this.max = oldNet.max;
        this.val = oldNet.val;
        this.activation = oldNet.activation;
        this.winner = oldNet.winner;
        this.updateMethod = oldNet.updateMethod;
        setLabel("Competitive Group (copy)");
    }

    @Override
    public CompetitiveGroup deepCopy(Network newParent) {
        return new CompetitiveGroup(newParent, this);
    }

    @Override
    public String getTypeDescription() {
        return "Competitive Group";
    }

    @Override
    public void update() {

        super.update();

        max = 0;
        winner = 0;

        // Determine Winner
        for (int i = 0; i < getNeuronList().size(); i++) {
            Neuron n = getNeuronList().get(i);
            if (!n.isClamped()) {
                n.update();
            }
            if (n.getActivation() > max) {
                max = n.getActivation();
                winner = i;
            }
        }

        // Update weights on winning neuron
        for (int i = 0; i < getNeuronList().size(); i++) {
            Neuron neuron = getNeuronList().get(i);
            if (i == winner) {
                neuron.setActivation(winValue);
                neuron.setSpike(neuron.isSpike());
                if (updateMethod == UpdateMethod.RUMM_ZIPSER) {
                    rummelhartZipser(neuron);
                } else if (updateMethod == UpdateMethod.ALVAREZ_SQUIRE) {
                    squireAlvarezWeightUpdate(neuron);
                    decayAllSynapses();
                }
            } else {
                neuron.setActivation(loseValue);
                neuron.setSpike(neuron.isSpike());
                if (useLeakyLearning) {
                    leakyLearning(neuron);
                }

            }
        }
        // normalizeIncomingWeights();
    }

    /**
     * Update winning neuron's weights in accordance with Alvarez and Squire
     * 1994, eq 2. TODO: rate is unused... in fact everything before
     * "double deltaw = learningRate" (line 200 at time of writing) cannot
     * possibly change any variables in the class.
     *
     * @param neuron winning neuron.
     */
    private void squireAlvarezWeightUpdate(final Neuron neuron) {
        for (Synapse synapse : neuron.getFanIn()) {
            double deltaw = learningRate * synapse.getTarget().getActivation() * (synapse.getSource().getActivation() - synapse.getTarget().getAverageInput());
            synapse.setStrength(synapse.clip(synapse.getStrength() + deltaw));
        }
    }

    /**
     * Update winning neuron's weights in accordance with PDP 1, p. 179.
     *
     * @param neuron winning neuron.
     */
    private void rummelhartZipser(final Neuron neuron) {
        double sumOfInputs = neuron.getTotalInput();
        // Apply learning rule
        for (Synapse synapse : neuron.getFanIn()) {
            activation = synapse.getSource().getActivation();

            // Normalize the input values
            if (normalizeInputs) {
                if (sumOfInputs != 0) {
                    activation = activation / sumOfInputs;
                }
            }

            double deltaw = learningRate * (activation - synapse.getStrength());
            synapse.setStrength(synapse.clip(synapse.getStrength() + deltaw));
        }
    }

    /**
     * Decay attached synapses in accordance with Alvarez and Squire 1994, eq 3.
     */
    private void decayAllSynapses() {
        for (Neuron n : getNeuronList()) {
            for (Synapse synapse : n.getFanIn()) {
                synapse.decay(synpaseDecayPercent);
            }
        }

    }

    /**
     * Apply leaky learning to provided learning.
     *
     * @param neuron neuron to apply leaky learning to
     */
    private void leakyLearning(final Neuron neuron) {
        double sumOfInputs = neuron.getTotalInput();
        for (Synapse incoming : neuron.getFanIn()) {
            activation = incoming.getSource().getActivation();
            if (normalizeInputs) {
                if (sumOfInputs != 0) {
                    activation = activation / sumOfInputs;
                }
            }
            val = incoming.getStrength() + leakyLearningRate * (activation - incoming.getStrength());
            incoming.setStrength(val);
        }
    }

    /**
     * Normalize weights coming in to this network, separately for each neuron.
     */
    public void normalizeIncomingWeights() {

        for (Neuron n : getNeuronList()) {
            double normFactor = n.getSummedIncomingWeights();
            for (Synapse s : n.getFanIn()) {
                s.setStrength(s.getStrength() / normFactor);
            }
        }
    }

    /**
     * Normalize all weights coming in to this network.
     */
    public void normalizeAllIncomingWeights() {

        double normFactor = getSummedIncomingWeights();
        for (Neuron n : getNeuronList()) {
            for (Synapse s : n.getFanIn()) {
                s.setStrength(s.getStrength() / normFactor);
            }
        }
    }

    /**
     * Randomize all weights coming in to this network.
     * <p>
     * TODO: Add gaussian option...
     */
    public void randomizeIncomingWeights() {

        for (Iterator<Neuron> i = getNeuronList().iterator(); i.hasNext(); ) {
            Neuron n = i.next();
            for (Synapse s : n.getFanIn()) {
                s.randomize();
            }
        }
    }

    /**
     * Returns the sum of all incoming weights to this network.
     *
     * @return the sum of all incoming weights to this network.
     */
    private double getSummedIncomingWeights() {
        double ret = 0;
        for (Iterator<Neuron> i = getNeuronList().iterator(); i.hasNext(); ) {
            Neuron n = i.next();
            ret += n.getSummedIncomingWeights();
        }
        return ret;
    }

    /**
     * Randomize and normalize weights.
     */
    public void randomize() {
        randomizeIncomingWeights();
        normalizeIncomingWeights();
    }

    /**
     * Return the learning rate.
     *
     * @return the learning rate
     */
    public double getLearningRate() {
        return learningRate;
    }

    /**
     * Sets learning rate.
     *
     * @param rate The new epsilon value.
     */
    public void setLearningRate(final double rate) {
        this.learningRate = rate;
    }

    /**
     * Return the loser value.
     *
     * @return the loser Value
     */
    public final double getLoseValue() {
        return loseValue;
    }

    /**
     * Sets the loser value.
     *
     * @param loseValue The new loser value
     */
    public final void setLoseValue(final double loseValue) {
        this.loseValue = loseValue;
    }

    /**
     * Return the winner value.
     *
     * @return the winner value
     */
    public final double getWinValue() {
        return winValue;
    }

    /**
     * Sets the winner value.
     *
     * @param winValue The new winner value
     */
    public final void setWinValue(final double winValue) {
        this.winValue = winValue;
    }

    /**
     * Return leaky learning rate.
     *
     * @return Leaky learning rate
     */
    public double getLeakyLearningRate() {
        return leakyLearningRate;
    }

    /**
     * Sets the leaky learning rate.
     *
     * @param leakyRate Leaky rate value to set
     */
    public void setLeakyLearningRate(final double leakyRate) {
        this.leakyLearningRate = leakyRate;
    }

    /**
     * Return the normalize inputs value.
     *
     * @return the normailize inputs value
     */
    public boolean getNormalizeInputs() {
        return normalizeInputs;
    }

    /**
     * Sets the normalize inputs value.
     *
     * @param normalizeInputs Normalize inputs value to set
     */
    public void setNormalizeInputs(final boolean normalizeInputs) {
        this.normalizeInputs = normalizeInputs;
    }

    /**
     * Return the leaky learning value.
     *
     * @return the leaky learning value
     */
    public boolean getUseLeakyLearning() {
        return useLeakyLearning;
    }

    /**
     * Called by reflection via {@link UserParameter#conditionalEnablingMethod()}
     */
    public Function<Map<String, Object>, Boolean> usesLeakyLearning() {
        return (map) -> (Boolean) map.get("Use Leaky learning");
    }

    /**
     * Sets the leaky learning value.
     *
     * @param useLeakyLearning The leaky learning value to set
     */
    public void setUseLeakyLearning(final boolean useLeakyLearning) {
        this.useLeakyLearning = useLeakyLearning;
    }

    /**
     * @return the synpaseDecayPercent
     */
    public double getSynpaseDecayPercent() {
        return synpaseDecayPercent;
    }

    /**
     * @param synpaseDecayPercent the synpaseDecayPercent to set
     */
    public void setSynpaseDecayPercent(double synpaseDecayPercent) {
        this.synpaseDecayPercent = synpaseDecayPercent;
    }

    /**
     * @return the updateMethod
     */
    public UpdateMethod getUpdateMethod() {
        return updateMethod;
    }

    /**
     * @param updateMethod the updateMethod to set
     */
    public void setUpdateMethod(UpdateMethod updateMethod) {
        this.updateMethod = updateMethod;
    }

    /**
     * Convenience method for setting update style from scripts.
     *
     * @param updateMethod string name of method: "RZ" for Rummelhart Zipser;
     *                     "AS" for Alvarez-Squire
     */
    public void setUpdateMethod(String updateMethod) {
        if (updateMethod.equalsIgnoreCase("RZ")) {
            this.updateMethod = UpdateMethod.RUMM_ZIPSER;
        } else if (updateMethod.equalsIgnoreCase("AS")) {
            this.updateMethod = UpdateMethod.ALVAREZ_SQUIRE;
        }
    }

}
