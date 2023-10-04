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
package org.simbrain.util.widgets;

import org.simbrain.util.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A drop down triangle that can be used to hide information that is only
 * revealed when the triangle is clicked. Note that this is just the triangle.
 * It does not "contain" the thing that becomes visible or invisible An action
 * listener must be added for that.
 * <p>
 * Suggested usage is with {@link org.simbrain.util.DetailTrianglePanel}
 *
 * @author Zoë Tosi
 */
public class DropDownTriangle extends JPanel implements MouseListener {

    /**
     * Direction the triangle faces when it is "up".
     */
    public enum UpDirection {
        LEFT, RIGHT;
    }

    /**
     * The label for the triangle when it is in the "up" state.
     */
    private final JLabel upTriLabel;

    /**
     * The label for the triangle when it is in the down state.
     */
    private final JLabel downTriLabel;

    private JLabel label;

    /**
     * The clickable triangle.
     */
    private final ClickableTriangle ddTriangle;

    /**
     * Whether or not the triangle is pointing down.
     */
    private boolean down;

    /**
     * Creates an unlabeled drop down triangle pointing either left or right in
     * the "up" state and starting either "up" or down.
     *
     * @param upState The direction the triangle points when in the "up" state
     * @param down    Whether or not the triangle is initialized in the "up" or down
     *                state
     */
    public DropDownTriangle(UpDirection upState, boolean down) {
        this(upState, down, "", "");
    }

    /**
     * Creates a drop down triangle with a label displayed when it is in the
     * "up" state and a label displayed when it is in the "down" state. The
     * triangle points either left or right in the "up" state and starts out
     * either "up" or down.
     *
     * @param upState   The direction the triangle points when in the "up" state
     * @param down      Whether or not the triangle is initialized in the "up" or down
     *                  state
     * @param upLabel   The label displayed when the triangle is in the "up" state
     * @param downLabel The label displayed when the triangle is in the down state.
     */
    public DropDownTriangle(final UpDirection upState, final boolean down, final String upLabel, final String downLabel) {
        ddTriangle = new ClickableTriangle(upState, down);
        upTriLabel = new JLabel(upLabel);
        downTriLabel = new JLabel(downLabel);
        this.down = down;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        initLayout();
        addMouseListener(this);
    }

    /**
     * Lays out the label and triangle based on the current state.
     */
    private void initLayout() {
        label = down ? downTriLabel : upTriLabel;
        this.add(label);
        this.add(Box.createHorizontalStrut(10));
        this.add(ddTriangle);
    }

    /**
     * Changes the state of the triangle: if called when pointing "up"
     * (left/right) the triangle changes to point down and vice versa.
     */
    public void changeState() {
        // Whatever state it's in, change it to the other state.
        setState(!down);
    }

    /**
     * Set the state of the drop down triangle.
     *
     * @param isDown if true, the triangle should be "open". False is closed.
     */
     public void setState(boolean isDown) {
        down = isDown;
        ddTriangle.setState(down);
        removeAll();
        initLayout();
        repaint();
    }

    public boolean isDown() {
        return down;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        changeState();
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }

    @Override
    public void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        ddTriangle.addMouseListener(l);
        label.addMouseListener(l);
    }

    /**
     * Sets the color of the label when the triangle is in the up state.
     */
    public void setUpLabelColor(Color color) {
        upTriLabel.setForeground(color);
    }

    /**
     * Sets the color of the label when the triangle is in the down state.
     */
    public void setDownLabelColor(Color color) {
        downTriLabel.setForeground(color);
    }

    /**
     * Changes the text displayed when the triangle is in the "up" state
     */
    public void setUpText(String upText) {
        upTriLabel.setText(upText);
    }

    /**
     * Changes the text displayed when the triangle is in the down state
     */
    public void setDownText(String downText) {
        downTriLabel.setText(downText);
    }

    /**
     * Changes the text displayed next to the triangle.
     */
    public void setBothTexts(String upText, String downText) {
        setUpText(upText);
        setDownText(downText);
        repaint();
    }

    /**
     * @author Zoë
     */
    private class ClickableTriangle extends JPanel {

        /**
         * The image icon for the triangle in it's down pointing state.
         */
        private ImageIcon downTriangle = ResourceManager.getImageIcon("menu_icons/DownTriangle.png");

        /**
         * The image icon for the triangle in it's left pointing state.
         */
        private ImageIcon leftTriangle = ResourceManager.getImageIcon("menu_icons/LeftTriangle.png");

        /**
         * The image icon for the triangle in it's left pointing state.
         */
        private ImageIcon rightTriangle = ResourceManager.getImageIcon("menu_icons/RightTriangle.png");

        /**
         * The image icon for the triangle in it's "up" pointing state
         * (Left/Right).
         */
        private ImageIcon upTriangle;

        /**
         * The currently displayed triangle image.
         */
        private Image triangle;

        /**
         * Construct the triangle
         */
        public ClickableTriangle(UpDirection upState, boolean down) {

            if (upState == UpDirection.LEFT)
                upTriangle = leftTriangle;
            else
                upTriangle = rightTriangle;

            triangle = down ? downTriangle.getImage() : upTriangle.getImage();
            setSize();
            setLayout(null);
            repaint();

        }

        /**
         * Sets the size of the panel to fit the triangle image.
         */
        private void setSize() {
            Dimension size = new Dimension((int) triangle.getWidth(null), (int) triangle.getHeight(null));
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);
            setSize(size);
        }

        /**
         * Sets the state of the triangle, to either the "up" position (Left or
         * Right) or the down position.
         *
         * @param down the desired state of the triangle. False, puts the
         *             triangle in the "up" position: pointing left or right,
         *             specified at the time of creation, true points the
         *             triangle down.
         */
        public void setState(boolean down) {
            triangle = down ? downTriangle.getImage() : upTriangle.getImage();
            setSize();
        }

        @Override
        public void paintComponent(Graphics g) {
            removeAll();
            super.paintComponent(g);
            setSize();
            g.drawImage(triangle, 0, 0, null);
        }

    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        DropDownTriangle ddt = new DropDownTriangle(UpDirection.LEFT, false, "X", "Y");
        f.setContentPane(ddt);
        f.pack();
        f.setVisible(true);
    }
}
