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
package org.simbrain.world.odorworld.sensors;

import org.simbrain.util.UserParameter;
import org.simbrain.util.propertyeditor2.EditableObject;
import org.simbrain.workspace.Consumable;
import org.simbrain.workspace.Producible;
import org.simbrain.world.odorworld.entities.OdorWorldEntity;

/**
 * A sensor which detects whether the entity is in a given sector or "tile" of
 * the world. Broadly inspired by "place cells".
 *
 * @author jyoshimi
 */
public class LocationSensor extends Sensor implements VisualizableEntityAttribute {

    /**
     * Default Activation Amount.
     */
    public static final double DEFAULT_ACTIVATION = 1;

    /**
     * Default X coordinate of the upper left corner.
     */
    public static final int DEFAULT_X = 0;

    /**
     * Default Y coordinate of the upper left corner.
     */
    public static final int DEFAULT_Y = 0;

    /**
     * Default width.
     */
    public static final int DEFAULT_WIDTH = 32;

    /**
     * Default height.
     */
    public static final int DEFAULT_HEIGHT = 32;

    /**
     * Current value of the sensor; activationAmount if "active", 0 otherwise.
     */
    private double[] values;

    /**
     * Value to return when the tile sensor is activated.
     */
    @UserParameter(label = "Activation amount",
            description = "Amount of activation that a neuron coupled with the tile sensor receives "
                    + "when the tile sensor is activated. ",
            defaultValue = "" + DEFAULT_ACTIVATION, order = 3)
    private double activationAmount = DEFAULT_ACTIVATION;

    /**
     * Upper left corner.
     */
    @UserParameter(label = "X",
            description = "x coordinates for the location of the top-left corner of the tile sensor.",
            order = 4)
    private int x;


    /**
     * Upper left corner.
     */
    @UserParameter(label = "Y",
            description = "y coordinates for the location of the top-left corner of the tile sensor.",
            order = 5)
    private int y;
    /**
     * Width of the sensor.
     */
    @UserParameter(label = "Width",
            description = "Determines the size of the tile. Width specifies the horizontal length of the tile sensor.",
            order = 6)
    private int width = DEFAULT_WIDTH;

    /**
     * Height of the sensor.
     */
    @UserParameter(label = "Height",
            description = "Determines the size of the tile. Height specifies the vertical length.",
            order = 7)
    private int height = DEFAULT_HEIGHT;

    @UserParameter(
            label = "Columns",
            description = "Number of columns of the sensor grid",
            order = 8
    )
    private int columns = 5;

    @UserParameter(
            label = "Rows",
            description = "Number of columns of the sensor grid",
            order = 9
    )
    private int rows = 5;

    /**
     * Construct a tile sensor.
     *
     * @param parent parent entity
     * @param x      upper left
     * @param y      upper right
     * @param width  width in pixels
     * @param height height
     */
    public LocationSensor(OdorWorldEntity parent, int x, int y, int width, int height) {
        super(parent, "Tile (" + x + "," + y + "):" + width + "x" + height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Default constructor for {@link org.simbrain.util.propertyeditor2.AnnotatedPropertyEditor}.
     *
     * NOTE:
     * {@link org.simbrain.world.odorworld.dialogs.AddSensorDialog} handles the set up of {@link #parent}.
     * When calling this directly, remember to set up the required field {@link #parent} accordingly.
     */
    public LocationSensor() {
        super();
    }

    @Override
    public void update() {
        values = new double[columns * rows];
        int gridX = (int) (getLocation()[0] / width - x);
        int gridY = (int) (getLocation()[1] / height - y);
        if (gridX < columns && gridY < rows) {
            values[gridX + gridY * rows] = DEFAULT_ACTIVATION;
        }
    }

    /**
     * @return value associated with this sensor, 0 if occupied,
     */
    @Producible(idMethod = "getMixedId")
    public double[] getValues() {
        return values;
    }

    // /**
    //  * @param values the value to set
    //  */
    // @Consumable(idMethod = "getMixedId")
    // public void setValue(double[] values) {
    //     this.values = values;
    // }

    /**
     * @return the activationAmount
     */
    public double getActivationAmount() {
        return activationAmount;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @param amount the activation amount to set
     */
    public void setActivationAmount(double amount) {
        activationAmount = amount;
    }

    @Override
    public String getTypeDescription() {
        return "Location";
    }

    @Override
    public void setParent(OdorWorldEntity parent) {
        this.parent = parent;
    }

    @Override
    public EditableObject copy() {
        return new LocationSensor(parent, x, y, width, height);
    }

    @Override
    public String getName() {
        return "Location";
    }
}
