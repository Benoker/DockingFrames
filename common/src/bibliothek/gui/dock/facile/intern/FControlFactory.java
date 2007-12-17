/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.facile.intern;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;

/**
 * A factory creating various elements that are needed in a {@link FControl}
 * and its associated components.
 * @author Benjamin Sigg
 */
public interface FControlFactory {
    /**
     * Creates or gets the {@link DockController}.
     * @return the controller, always the same object
     */
    public DockController createController();
    
    /**
     * Creates a new {@link FlapDockStation}.
     * @return the new station
     */
    public FlapDockStation createFlapDockStation();
    
    /**
     * Creates a new {@link ScreenDockStation}.
     * @param owner the owner of the dialogs of the station
     * @return the new station
     */
    public ScreenDockStation createScreenDockStation( JFrame owner );
    
    /**
     * Makes sure that <code>component</code> is monitored for mouse- and
     * keyevents. Might create a new {@link Component} to perform this task.
     * @param component the component which is to be monitored
     * @param control the environment in which <code>component</code> is used
     * @return either <code>component</code> or a <code>Component</code> which
     * has <code>component</code> as a child.
     */
    public Component monitor( Component component, FControl control );
    
    /**
     * Makes sure that <code>component</code> is monitored for mouse- and
     * keyevents. Might create a new {@link Component} to perform this task.
     * @param component the component which is to be monitored
     * @param control the environment in which <code>component</code> is used
     * @return either <code>component</code> or a <code>Component</code> which
     * has <code>component</code> as a child.
     */
    public Container monitor( Container component, FControl control );
    
    /**
     * Makes sure that <code>component</code> is monitored for mouse- and
     * keyevents. Might create a new {@link Component} to perform this task.
     * @param component the component which is to be monitored
     * @param control the environment in which <code>component</code> is used
     * @return either <code>component</code> or a <code>Component</code> which
     * has <code>component</code> as a child.
     */
    public JComponent monitor( JComponent component, FControl control );
}
