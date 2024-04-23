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
package org.simbrain.world.odorworld.effectors;

import org.simbrain.util.UserParameter;
import org.simbrain.util.decayfunctions.DecayFunction;
import org.simbrain.util.decayfunctions.LinearDecayFunction;
import org.simbrain.workspace.Consumable;
import org.simbrain.world.odorworld.entities.OdorWorldEntity;
import org.simbrain.world.odorworld.sensors.VisualizableEntityAttribute;

import java.util.List;

/**
 * Model simple speech behaviors. Each speech effector is associated with a
 * specific phrase. To activate speech either manually activate or de-activate
 * it or set the "value" to a number above a threshold (currently set to 0).
 *
 * @author Jeff Yoshimi
 */
public class Speech extends Effector implements VisualizableEntityAttribute {

    // TODO: Possibly add a radius of influence
    // Possibly encapsulate phrase String in an utterance class

    /**
     * Default phrase.
     */
    public static final String DEFAULT_PHRASE = "Hi!";

    /**
     * The thing this speech effector says.
     */
    @UserParameter(label = "Utterance",
            description = "The thing this speech effector says.",
            order = 3)
    private String phrase = DEFAULT_PHRASE;

    /**
     * Maximum characters per row before warping around in a SpeechNode.
     */
    @UserParameter(label = "Characters per Row",
            description = "The maximum number of characters that can be displayed in one row in the speech bubble. "
                        + "This setting only affects visual representation.",
            order = 4)
    private int charactersPerRow = 32;

    /**
     * Default threshold.
     */
    public static final double DEFAULT_THRESHOLD = .01;

    /**
     * Threshold above which to "the message.
     */
    @UserParameter(label = "Threshold",
            description = "Threshold above which to \"the message\".",
            order = 5)
    private double threshold = DEFAULT_THRESHOLD;

    @UserParameter(label = "Decay Function", order = 10, tab = "Dispersion")
    private DecayFunction decayFunction = new LinearDecayFunction(128);

    /**
     * Whether this is activated. If so, display the phrase and notify all
     * hearing sensors.
     */
    private boolean activated;

    /**
     * If amount is greater than threshold, activate the speech.
     */
    private double amount;

    /**
     * Construct the speech effector.
     *
     * @param phrase    the phrase associated with this effector
     */
    public Speech( String phrase, double threshold) {
        super("Say: \"" + phrase + "\"");
        this.phrase = phrase;
        this.threshold = threshold;
    }

    /**
     * Construct the speech effector with default values.
     */
    public Speech() {
        super("Say: \"" + DEFAULT_PHRASE + "\"");
    }

    public Speech(Speech speech) {
        super(speech);
        this.phrase = speech.phrase;
        this.charactersPerRow = speech.charactersPerRow;
        this.threshold = speech.threshold;
        this.decayFunction = (DecayFunction) speech.decayFunction.copy();
    }


    @Override
    public void update(OdorWorldEntity parent) {
        if (amount > threshold) {
            if (!activated) {
                activated = true;
                getEvents().getUpdated().fire();
            }
            amount = 0; // reset
        } else {
            if (activated) {
                activated = false;
                getEvents().getUpdated().fire();
            }
        }
        if (activated) {
            // TODO: now using dispersion distance only, get real decay value and set threshold later.
            List<OdorWorldEntity> entitiesInRadius = parent.getEntitiesInRadius(decayFunction.getDispersion());

            for (OdorWorldEntity entity : entitiesInRadius) {
                // Don't talk to yourself
                if (entity != parent) {
                    entity.speakToEntity(phrase);
                }
            }
        }
    }

    public String getPhrase() {
        return phrase;
    }

    @Consumable(customDescriptionMethod = "getAttributeDescription")
    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Consumable(customDescriptionMethod = "getAttributeDescription")
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public Speech copy() {
        return new Speech(this);
    }

    @Override
    public String getName() {
        return "Speech Effector";
    }

    public int getCharactersPerRow() {
        return charactersPerRow;
    }

    public void setCharactersPerRow(int charactersPerRow) {
        this.charactersPerRow = charactersPerRow;
    }
    
    @Override
    public String getLabel() {
        if (super.getLabel().isEmpty()) {
            return "Say " + phrase;
        } else {
            return super.getLabel();
        }
    }

}