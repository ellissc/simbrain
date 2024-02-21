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
package org.simbrain.plot.rasterchart;

import org.simbrain.workspace.AttributeContainer;
import org.simbrain.workspace.Workspace;
import org.simbrain.workspace.WorkspaceComponent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents raster data.
 */
public class RasterPlotComponent extends WorkspaceComponent {

    /**
     * The data model.
     */
    private final RasterModel model;

    /**
     * Create new raster plot component.
     *
     * @param name name
     */
    public RasterPlotComponent(final String name) {
        super(name);
        model = new RasterModel(() -> getWorkspace().getTime());
    }

    /**
     * Creates a new raster plot component from a specified model. Used in
     * deserializing.
     *
     * @param name  chart name
     * @param model chart model
     */
    public RasterPlotComponent(final String name, final RasterModel model) {
        super(name);
        this.model = model;
    }

    public RasterModel getModel() {
        return model;
    }

    public void postOpenInit(Workspace workspace) {
        model.setTimeSupplier(workspace::getTime);
    }

    /**
     * Opens a saved raster plot.
     *
     * @param input  stream
     * @param name   name of file
     * @param format format
     * @return bar chart component to be opened
     */
    public static RasterPlotComponent open(final InputStream input, final String name, final String format) {
        RasterModel dataModel = (RasterModel) RasterModel.getXStream().fromXML(input);
        return new RasterPlotComponent(name, dataModel);
    }

    @Override
    public void save(final OutputStream output, final String format) {
        RasterModel.getXStream().toXML(model, output);
    }

    @Override
    public boolean hasChangedSinceLastSave() {
        return false;
    }

    @Override
    public String getXML() {
        return RasterModel.getXStream().toXML(model);
    }

    @Override
    public List<AttributeContainer> getAttributeContainers() {
        List<AttributeContainer> containers = new ArrayList<>();
        for(RasterModel.RasterConsumer consumer : model.getRasterConsumerList()) {
            containers.add(consumer);
        }
        return containers;
    }


}
