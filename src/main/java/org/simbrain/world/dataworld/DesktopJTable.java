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
// package org.simbrain.world.dataworld;
//
// import org.simbrain.util.table.NumericTable;
// import org.simbrain.util.table.SimbrainJTable;
// import org.simbrain.workspace.gui.CouplingMenu;
//
// import javax.swing.*;
//
// /**
//  * Extends SimbrainJTable context menu with attribute menus.
//  *
//  * @author jyoshimi
//  */
// public class DesktopJTable extends SimbrainJTable {
//
//     /**
//      * Parent component.
//      */
//     private DataWorldComponent component;
//
//     /**
//      * Construct the table.
//      *
//      * @param dataModel base table
//      * @param component parent component
//      */
//     public DesktopJTable(NumericTable dataModel, DataWorldComponent component) {
//         super(dataModel);
//         this.component = component;
//         this.initJTable();
//     }
//
//     /**
//      * Build the context menu for the table.
//      *
//      * @return The context menu.
//      */
//     protected JPopupMenu buildPopupMenu() {
//
//         JPopupMenu ret = super.buildPopupMenu();
//
//         CouplingMenu rowCouplingMenu = new CouplingMenu(component, component);
//         rowCouplingMenu.setCustomName("Create Vector Coupling");
//         ret.addSeparator();
//         ret.add(rowCouplingMenu);
//
//         int selectedLogicalColumn = getSelectedColumn()-1;
//         DataWorldComponent.TableColumn tc = component.getTableColumn(selectedLogicalColumn);
//         CouplingMenu columnMenu = new CouplingMenu(component, tc);
//         columnMenu.setCustomName("Create Scalar Coupling");
//         ret.add(columnMenu);
//
//         return ret;
//     }
// }
