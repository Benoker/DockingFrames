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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Dimension;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;

/**
 * A {@link TabPane} is a view onto some component that shows a set of
 * {@link Dockable}s. Only one {@link Dockable} is visible at any time, the others
 * are hidden behind the visible <code>Dockable</code>. The user can select
 * the visible <code>Dockable</code> by clicking onto some button (called 
 * a "tab"). There is always one or none button per <code>Dockable</code>. <br>
 * {@link TabPane} can be seen as an extension of {@link StackDockComponent}
 * providing more control over the content of the component.
 * @author Benjamin Sigg
 */
public interface TabPane {
	/**
	 * The layout manager used by any {@link TabPane} to set the boundaries of its children.
	 */
	public static final PropertyKey<TabLayoutManager> LAYOUT_MANAGER = 
		new PropertyKey<TabLayoutManager>( "dock.tabpane.layout_manager", 
				new DynamicPropertyFactory<TabLayoutManager>(){
			public TabLayoutManager getDefault( PropertyKey<TabLayoutManager> key, DockProperties properties ){
				return new RowLayout();
			}
		}, true );
	
	/**
	 * When calculating the minimum size of a {@link TabPane}, then prefer using really small minimum sizes 
	 * over showing the content correctly.
	 */
	public static final PropertyKey<Boolean> USE_SMALL_MINIMUM_SIZE = 
			new PropertyKey<Boolean>( "dock.tabpane.layout_manager.small",
					new ConstantPropertyFactory<Boolean>( true ), 
					true);
	
	/**
	 * Adds a listener to this pane, the listener has to be informed when
	 * elements are added or removed, and when the selection changes.
	 * @param listener the new listener
	 */
	public void addTabPaneListener( TabPaneListener listener );
	
	/**
	 * Removes a listener from this pane.
	 * @param listener the listener to remove
	 */
	public void removeTabPaneListener( TabPaneListener listener );
	
	/**
	 * Gets the {@link DockController} this pane is associated with.
	 * @return the controller, might be <code>null</code>
	 */
	public DockController getController();
	
	/**
	 * Gets a list of all {@link Dockable}s that are stored on this pane. The list
	 * should be ordered.
	 * @return the list of children, not <code>null</code> but maybe empty
	 */
	public Dockable[] getDockables();
	
	/**
	 * Gets the minimum size that is required to show the child <code>dockable</code>.
	 * @param dockable one of the children of this pane
	 * @return the minimum size required to show <code>dockable</code>
	 */
	public Dimension getMinimumSize( Dockable dockable );

	/**
	 * Gets the preferred size that is required to show the child <code>dockable</code>.
	 * @param dockable one of the children of this pane
	 * @return the preferred size required to show <code>dockable</code>
	 */
	public Dimension getPreferredSize( Dockable dockable );
	
	/**
	 * Gets the currently selected {@link Dockable} on this pane (this
	 * is the one child that is visible). 
	 * @return the selection, not <code>null</code> unless there are no
	 * children at all
	 */
	public Dockable getSelectedDockable();
	
	/**
	 * Sets the dockable which is current selected on this pane. Must be
	 * called with a {@link Dockable} that is already a child of this pane.
	 * @param dockable the newly selected dockable
	 */
	public void setSelectedDockable( Dockable dockable );
	
	/**
	 * Tells at which side tabs are to be displayed. This property is
	 * intended for a {@link TabLayoutManager}, but may be ignored. 
	 * @return the side, not <code>null</code>
	 */
	public TabPlacement getDockTabPlacement();
	
	/**
	 * Gets all the tabs that are currently visible on this pane.
	 * @return the current tabs, not <code>null</code> but maybe empty. Note
	 * that the size of this array must be smaller or equal to the array returned
	 * by {@link #getDockables()}.
	 */
	public Tab[] getTabs();
	
	/**
	 * Informs this pane that its child <code>dockable</code> should have a
	 * tab-button. This <code>TabPane</code> may create a new {@link Tab}
	 * or reuse an existing <code>Tab</code>. Reusing an existing tab is
	 * recommended. If <code>dockable</code> was part of a {@link TabMenu},
	 * then it should be removed from that menu.<br>
	 * If <code>dockable</code> already is on a tab then this tab should be reused.
	 * @param dockable the element which needs a tab-button
	 * @return a <code>Tab</code> that is only used for <code>dockable</code>
	 * @throws IllegalArgumentException if <code>dockable</code> is either
	 * <code>null</code> or not child of this pane
	 */
	public Tab putOnTab( Dockable dockable );
	
	/**
	 * Gets the tab-button for <code>dockable</code>, if no tab-button
	 * for <code>dockable</code> exists a new button should be created. Reusing
	 * existing tabs is recommended. Other than {@link #putOnTab(Dockable)} this
	 * method must not affect any {@link TabMenu} showing {@link Dockable}. 
	 * @param dockable the element whose tab is requested
	 * @return the tab matching <code>dockable</code>
	 */
	public Tab getOnTab( Dockable dockable );
	
	/**
	 * Creates a new menu for this pane.
	 * @return the new menu
	 */
	public TabMenu createMenu();
	
	/**
	 * Destroys <code>menu</code> which was {@link #createMenu() created} by
	 * this pane. 
	 * @param menu a menu to destroy
	 */
	public void destroyMenu( TabMenu menu );
	
	/**
	 * Adds <code>dockable</code> somewhere on <code>menu</code>. If <code>dockable</code>
	 * is already shown on another menu or {@link Tab}, then it has to 
	 * be removed from that other menu or tab.
	 * @param menu a menu created by this {@link TabPane}
	 * @param dockable some child of this
	 * @see TabPane#putOnTab(Dockable)
	 */
	public void putInMenu( TabMenu menu, Dockable dockable );
	
	/**
	 * Gets the area in which all the {@link Tab}s, {@link TabMenu}s and
	 * the visible {@link Dockable} ({@link #setSelectedBounds(Rectangle)})
	 * must find their place.
	 * @return the available space, has at least <code>width</code> and
	 * <code>height</code> 1.
	 */
	public Rectangle getAvailableArea();
	
	/**
	 * Gets the boundaries the {@link #getSelectedDockable() selected Dockable}
	 * has (independent of whether such a {@link Dockable} exists).
	 * @return the boundaries of the selected child, not <code>null</code>
	 */
	public Rectangle getSelectedBounds();
	
	/**
	 * Sets the boundaries of the {@link #getSelectedDockable() selected Dockable}.
	 * @param bounds the boundaries of the selected child
	 * @throws IllegalArgumentException if <code>bounds</code> is <code>null</code>
	 */
	public void setSelectedBounds( Rectangle bounds );
	
	/**
	 * Gets the info component, the info component shows some additional 
	 * information. E.g. the info component may show information about the
	 * current selection.
	 * @return Gets the current info component, may be <code>null</code>
	 */
	public LonelyTabPaneComponent getInfoComponent();
}
