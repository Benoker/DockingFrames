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

package bibliothek.gui.dock.station.stack;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.event.ChangeListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * A StackDockComponent is a Component that can show other {@link Component Components}.
 * StackDockComponents are used by {@link StackDockStation StackDockStations}
 * to display their children.<br>
 * The component has one child which is "selected" (assuming there is at 
 * least one child). This one child should be visible, the other children
 * should be hidden. The user must have an interface to change the
 * selection at any time.<br>
 * A {@link StackDockComponent} should respect the {@link StackDockStation#TAB_PLACEMENT side}
 * at which the tabs are to be placed.
 * @author Janni Kovacs
 * @author Benjamin Sigg
 * @see bibliothek.gui.dock.StackDockStation
 * @see DefaultStackDockComponent
 * @see javax.swing.JTabbedPane
 */
public interface StackDockComponent {

    /**
     * Adds a {@link ChangeListener} to this component. The listener will be
     * called when the selected component changes.
     * @param listener the new listener
     */
    public void addChangeListener(ChangeListener listener);

    /**
     * Removes a {@link ChangeListener} which was added earlier.
     * @param listener the listener to remove
     */
    public void removeChangeListener(ChangeListener listener);

    /**
     * Gets the index of the currently selected dockable
     * @return the index
     */
    public int getSelectedIndex();

    /**
     * Selects the component at location <code>index</code>.
     * @param index the index of the selected component
     */
    public void setSelectedIndex(int index);

    /**
     * Gets the location and size of the graphical element that allows
     * the user to select the <code>index</code>'th child.
     * @param index the index of the child
     * @return the bounds of the graphical selection-element, <code>null</code>
     * if the element is not directly visible
     */
    public Rectangle getBoundsAt(int index);

    /**
     * Adds a new child at an unspecified location. 
     * @param title the title of the child
     * @param icon an icon to display for the child or <code>null</code> 
     * @param comp the new child to display
     * @param dockable the Dockable for which the tab is used
     */
    public void addTab(String title, Icon icon, Component comp, Dockable dockable );

    /**
     * Adds a new child at the location <code>index</code>.
     * @param title the title of the child
     * @param icon an icon to display for the child or <code>null</code> 
     * @param comp the new child to display
     * @param dockable the Dockable for which the tab is used
     * @param index the index that the new child should have
     */
    public void insertTab(String title, Icon icon, Component comp, Dockable dockable, int index);
    
    /**
     * Gets the number of children that are added to this StackDockComponent.
     * @return the number of children
     */
    public int getTabCount();

    /**
     * Removes all children from this component
     */
    public void removeAll();

    /**
     * Removes the child at location <code>index</code>.
     * @param index the index of the child
     */
    public void remove(int index);

    /**
     * Sets the title of the child at location <code>index</code>.
     * @param index the index of the child
     * @param newTitle the new title
     */
    public void setTitleAt(int index, String newTitle);

    /**
     * Sets the tooltip of the child at location <code>index</code>.
     * @param index the index of the child
     * @param newTooltip the new tooltip, can be <code>null</code>
     */
    public void setTooltipAt( int index, String newTooltip );
    
    /**
     * Sets the icon of the child at location <code>index</code>.
     * @param index the index of the child
     * @param newIcon the new icon, <code>null</code> if no icon
     * should be displayed
     */
    public void setIconAt(int index, Icon newIcon);
    
    /**
     * Sets the component which should be shown at tab <code>index</code>.
     * @param index the index where to show <code>component</code>
     * @param component the new content, not <code>null</code>
     */
    public void setComponentAt( int index, Component component );

    /**
     * Sets at which side tabs should be displayed.
     * @param tabSide the side, not <code>null</code>
     */
    public void setTabPlacement( TabPlacement tabSide );
    
    /**
     * Gets a {@link Component} on which the children of this
     * <code>StackDockComponent</code> will be displayed.
     * @return the {@link Component}
     */
    public Component getComponent();
    
    /**
     * Sets the controller for which this component manages its children.
     * @param controller the controller or <code>null</code>
     */
    public void setController( DockController controller );
    
    /**
     * Whether this kind of component already has a border.
     * @return <code>true</code> if this has a border, <code>false</code>
     * if the parent should paint one.
     */
    public boolean hasBorder();
}