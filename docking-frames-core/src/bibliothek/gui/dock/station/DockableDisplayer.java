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

package bibliothek.gui.dock.station;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.component.DockComponentRoot;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A panel which shows one {@link Dockable} and one {@link DockTitle}. The location
 * of the {@link DockTitle} is at one of the four borders (left,
 * right, top, bottom). The title may be <code>null</code>, in this case only
 * the Dockable is shown.<br>
 * Clients using a displayer should try to set the {@link #setController(DockController) controller}
 * and the {@link #setStation(DockStation) station} property.
 * @see DisplayerCollection
 * @see DisplayerFactory
 * @author Benjamin Sigg
 */
public interface DockableDisplayer extends DockComponentRoot{
    /** The four possible locations of the title */
    public static enum Location{
        /** the left side */
        LEFT, 
        /** the right side */
        RIGHT, 
        /** the top side */
        TOP,
        /** the bottom side */
        BOTTOM };
    
        
    /**
     * Sets the controller for which this displayer is used. That property
     * must be set by the client. Note that there is no guarantee, that a
     * client sets this property.
     * @param controller the controller, can be <code>null</code>
     */
    public void setController( DockController controller );
    
    /**
     * Gets the controller for which this displayer is used. Note that
     * there is no guarantee, that this property is set.
     * @return the controller or <code>null</code>
     */
    public DockController getController();
    
    /**
     * Adds <code>listener</code> to this displayer.
     * @param listener the new listener, not <code>null</code>
     */
    public void addDockableDisplayerListener( DockableDisplayerListener listener );
    
    /**
     * Removes <code>listener</code> from this displayer.
     * @param listener the listener to remove
     */
    public void removeDockableDisplayerListener( DockableDisplayerListener listener );
    
    /**
     * Sets the station on which this displayer is shown. That property
     * must be set by the station itself. Note that there is no guarantee,
     * that a station sets this property.
     * @param station the station or <code>null</code>
     */
    public void setStation( DockStation station );
    
    /**
     * Gets the station on which this displayer might be shown. Note that
     * there is no guarantee, that a parent-station sets this property.
     * @return the station or <code>null</code>
     */
    public DockStation getStation();
    
    /**
     * Gets the Dockable which is shown on this displayer.
     * @return the child, can be <code>null</code>
     */
    public Dockable getDockable();

    /**
     * Sets the Dockable which should be shown on this displayer. A value
     * of <code>null</code> means that no Dockable should be visible at all.
     * @param dockable the child, can be <code>null</code>
     */
    public void setDockable( Dockable dockable );

    /**
     * Gets the location of the title in respect to the Dockable.
     * @return the location
     */
    public Location getTitleLocation();

    /**
     * Sets the location of the title in respect to the Dockable.
     * @param location the location, a value of <code>null</code> is transformed
     * into the default-value
     */
    public void setTitleLocation( Location location );
    
    /**
     * Gets a {@link DockElementRepresentative representation} of the {@link Dockable} that can
     * be used for grabbing and moving around the displayer. The result of this method should be
     * the first match of this list:
     * <ol>
     * 	<li>A {@link DockTitle}</li>
     *  <li>Any kind of {@link DockElementRepresentative}</li>
     *  <li>The {@link Dockable} itself</li>
     *  <li> <code>null</code> </li>
     * </ol>
     * Changes of the result of this method should be communicated through the 
     * {@link DockableDisplayerListener}s.
     * @return an element for moving around this displayer, can be <code>null</code>
     */
    public DockElementRepresentative getMoveableElement();
    
    /**
     * Gets the title which is shown on this displayer.
     * @return the title, can be <code>null</code>
     */
    public DockTitle getTitle();

    /**
     * Gets the center point of the {@link #getTitle() title} or any {@link Component} that
     * behaves as if it would be the title. Some {@link DockStation}s may use this information
     * to make sure, that the user can always grab and move around the {@link Dockable}.
     * @return the center point, may be <code>null</code>
     */
    public Point getTitleCenter();
    
    /**
     * Sets the title of this displayer. If the title is set to <code>null</code>,
     * no title is visible. The displayer will change the 
     * {@link DockTitle#setOrientation(bibliothek.gui.dock.title.DockTitle.Orientation) orientation}
     * of the title.
     * @param title the title or <code>null</code>
     */
    public void setTitle( DockTitle title );
    
    /**
     * Tells whether the point <code>x/y</code> is inside the title of this 
     * displayer or not.
     * @param x the x-coordinate, relatively to this component
     * @param y the y-coordinate, relatively to this component
     * @return <code>true</code> if the title contains the point
     */
    public boolean titleContains( int x, int y );
    
    /**
     * Gets an estimate of the insets around the {@link Dockable} of this 
     * displayer compared to the whole size of this displayer.
     * @return the estimate of the insets
     */
    public Insets getDockableInsets();
    
    /**
     * Gets the Component which represents this displayer.
     * @return the component
     */
    public Component getComponent();
    
    /**
     * This method tells how this displayer would like to combine itself with a {@link Dockable} that is dropped
     * over it. This method is usually called by a {@link Combiner} or by a {@link DockStation}, but other modules
     * may call this method as well.
     * @param source information about the dockable that is dropped, the location of the mouse, etc...
     * @param force tells how much the caller would like the result not to be <code>null</code>, if the
     * {@link Enforcement#getForce() force} property is high, then the result should more likely not be <code>null</code>.
     * Note that a result of <code>null</code> is always a valid result, even if the caller does not like it. 
     * @return the operation that could be performed or <code>null</code> if this displayer does not
     * have any specific information
     */
    public DisplayerCombinerTarget prepareCombination( CombinerSource source, Enforcement force );
}
