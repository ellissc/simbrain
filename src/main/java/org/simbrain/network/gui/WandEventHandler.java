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
package org.simbrain.network.gui;

import org.piccolo2d.PCamera;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PDragSequenceEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.util.PNodeFilter;
import org.simbrain.network.core.Neuron;
import org.simbrain.network.gui.dialogs.NetworkPreferences;
import org.simbrain.network.gui.nodes.NeuronNode;

import java.awt.event.InputEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;

/**
 * Wand event handler. Change activation when dragging over neurons.
 */
final class WandEventHandler extends PDragSequenceEventHandler {

    /**
     * Bounds filter.
     */
    private final BoundsFilter boundsFilter;

    /**
     * Network Panel.
     */
    private final NetworkPanel networkPanel;

    /**
     * Create a new selection event handler.
     *
     * @param networkPanel
     */
    public WandEventHandler(NetworkPanel networkPanel) {
        super();
        boundsFilter = new BoundsFilter();
        setEventFilter(new ZoomEventFilter());
        this.networkPanel = networkPanel;
    }

    @Override
    public void mousePressed(final PInputEvent event) {
        super.mousePressed(event);
        //networkPanel.setLastClickedPosition(event.getPosition());
        //if (event.getPath().getPickedNode() instanceof PCamera) {
        //    networkPanel.setBeginPosition(event.getPosition());
        //}
    }

    @Override
    public void mouseClicked(final PInputEvent event) {

        super.mouseClicked(event);

        if (event.getClickCount() != 1) {
            return;
        }

        PNode node = event.getPath().getPickedNode();
        if (node instanceof NeuronNode) {
            modifyNode((NeuronNode) node);
        }
    }

    @Override
    protected void startDrag(final PInputEvent event) {
        super.startDrag(event);
    }

    @Override
    protected void drag(final PInputEvent event) {

        super.drag(event);

        int radius = NetworkPreferences.INSTANCE.getWandRadius();

        // Create elliptical bounds
        Point2D position = event.getPosition();
        Ellipse2D.Double ellipse = new Ellipse2D.Double(position.getX() - radius / 2, position.getY() - radius / 2, radius, radius);
        boundsFilter.setEllipse(ellipse);

        Collection highlightedNodes = networkPanel.getCanvas().getLayer().getRoot().getAllNodes(boundsFilter, null);

        // Auto-highlighter mode
        for (Object node : highlightedNodes) {
            if (node instanceof NeuronNode) {
                modifyNode((NeuronNode) node);
            }

        }

    }

    @Override
    protected void endDrag(final PInputEvent event) {
        super.endDrag(event);
    }

    /**
     * The wand "action" goes here.
     *
     * @param node node to act on
     */
    private void modifyNode(NeuronNode node) {
        Neuron neuron = node.getNeuron();
        neuron.setActivation(neuron.getUpperBound());
    }

    /**
     * Bounds filter.
     */
    private class BoundsFilter implements PNodeFilter {

        /**
         * Bounds.
         */
        private Ellipse2D.Double ellipse;

        /**
         * Set the bounds for this bounds filter to <code>bounds</code>.
         *
         * @param ellipse bounds for this bounds filter
         */
        public void setEllipse(Ellipse2D.Double ellipse) {
            this.ellipse = ellipse;
        }

        /**
         * @param node
         * @return
         * @see PNodeFilter
         */
        public boolean accept(final PNode node) {
            boolean isPickable = node.getPickable();
            boolean boundsIntersects = ellipse.intersects(node.getGlobalBounds());
            boolean isLayer = (node instanceof PLayer);
            boolean isCamera = (node instanceof PCamera);

            return (isPickable && boundsIntersects && !isLayer && !isCamera);
        }

        @Override
        public boolean acceptChildrenOf(final PNode node) {
            boolean areChildrenPickable = node.getChildrenPickable();
            boolean isCamera = (node instanceof PCamera);
            boolean isLayer = (node instanceof PLayer);
            return (areChildrenPickable || isCamera || isLayer);
        }
    }

    /**
     * Selection event filter, accepts various mouse events, but only when the
     * network panel's edit mode is <code>EditMode.WAND</code>.
     */
    private class ZoomEventFilter extends PInputEventFilter {

        /**
         * Create a new selection event filter.
         */
        public ZoomEventFilter() {
            super(InputEvent.BUTTON1_MASK);
        }

        @Override
        public boolean acceptsEvent(final PInputEvent event, final int type) {

            EditMode editMode = networkPanel.getEditMode();

            if (editMode.isWand() && super.acceptsEvent(event, type)) {
                return true;
            } else {
                return false;
            }
        }
    }

}
