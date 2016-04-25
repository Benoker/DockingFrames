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

package bibliothek.gui.dock.station.split;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockHierarchyLock;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.DockHierarchyLock.Token;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * Provides access to some internal methods and attributes of a {@link SplitDockStation}. An access
 * is granted only to a few selected friend classes.
 * @author Benjamin Sigg
 */
public interface SplitDockAccess {
	/**
	 * Gets the station to which this object grants access.
	 * @return the owner
	 */
    public SplitDockStation getOwner();
        
    /**
     * Gets the kind of title {@link #getOwner() owner} uses.
     * @return the kind of title, can be <code>null</code>
     */
    public DockTitleVersion getTitleVersion();
    
    /**
     * Gets the {@link DockableDisplayer} which is currently in fullscreen-mode.
     * @return the displayer, may be <code>null</code>
     */
    public StationChildHandle getFullScreenDockable();
    
    /**
     * Tests whether the given value of the divider is a legal value or not.
     * @param divider The value of the divider
     * @param node the node for which the divider is validated
     * @return a legal value, as near as possible to <code>divider</code>.
     */
    public double validateDivider( double divider, Node node );
    
    /**
     * Creates a new handle but does not take any other action.
     * @param dockable the target for the new handle
     * @return the new handle
     */
    public StationChildHandle newHandle( Dockable dockable );
    
    /**
     * Adds the new <code>handle</code> to the station and adds
     * the displayer to the station. Binds the <code>dockable</code>.
     * @param handle the new handle
     * @param token if <code>null</code>, then a token will be acquired by this method
     * and this method will fire events, otherwise this methods is executed silently
     */
    public void addHandle( StationChildHandle handle, DockHierarchyLock.Token token );
    
    /**
     * Removes an element from the station.
     * @param handle the element to remove
     * @param token if <code>null</code>, then a token will be acquired by this method
     * and this method will fire events, otherwise this methods is executed silently
     */
    public void removeHandle( StationChildHandle handle, DockHierarchyLock.Token token );
    
    /**
     * Tries to add <code>Dockable</code> such that the boundaries given
     * by <code>property</code> are full filled.
     * @param dockable a new child of this station
     * @param property the preferred location of the child
     * @param root the root of all possible parents where the child could be inserted
     * @return <code>true</code> if the child could be added, <code>false</code>
     * if no location could be found
     */
    public boolean drop( Dockable dockable, final SplitDockProperty property, SplitNode root );
    
    /**
     * Invoked whenever a node changes its shape. Leads to a call to {@link DockStationListener#dockablesRepositioned(bibliothek.gui.DockStation, Dockable[])}
     * for all {@link Dockable}s that are in <code>node</code> or children of <code>node</code>.
     * @param node the source of the event
     */
    public void repositioned( SplitNode node );
    
    /**
     * Checks whether <code>info</code> is valid or not.
     * @param info the preferred drop location
     * @return <code>info</code> if it is valid, <code>null</code> otherwise
     */
    public PutInfo validatePutInfo( PutInfo info );
    
    /**
     * Creates a unique id for a {@link SplitNode}.
     * @return the new unique id
     */
    public long uniqueID();
    
    /**
     * Tells whether nodes can currently be automatically removed from the tree.
     * @return <code>true</code> if auto-removal is enabled
     */
	public boolean isTreeAutoCleanupEnabled();
	
	/**
	 * Gets the set which keeps track of all placeholders and makes sure that
	 * no placeholder is used twice.
	 * @return the placeholder set
	 */
	public SplitPlaceholderSet getPlaceholderSet();
	
	/**
	 * Gets the object that manages all {@link Span}s.
	 * @return access to the current {@link SpanFactory}
	 */
	public SplitSpanStrategy getSpanStrategy();
	
	/**
	 * Creates a new {@link Leaf}.
	 * @param id the unique identifier of the leaf
	 * @return the new leaf
	 */
	public Leaf createLeaf( long id );
	
	/**
	 * Creates a new {@link Node}.
	 * @param id the unique identifier of the node
	 * @return the new node
	 */
	public Node createNode( long id );
	
	/**
	 * Creates a new {@link Placeholder}.
	 * @param id the unique identifier of this placeholder
	 * @return the new placeholder
	 */
	public Placeholder createPlaceholder( long id );
	
	/**
	 * Sets the current information telling where and how an item is to be dropped.
	 * @param putInfo the current drop information
	 */
	public void setDropInfo( PutInfo putInfo );

	/**
	 * Resets the information telling where and how an item is to be dropped.
	 */
	public void unsetDropInfo();
	
	/**
	 * Moves the <code>dockable</code> described by <code>putInfo</code> at a new location
	 * @param putInfo description of the new location
	 * @param item more information about the drag and drop operation that is currently happening
	 */
	public void move( PutInfo putInfo, StationDropItem item );
	
	/**
	 * Adds the {@link Dockable} given by <code>putInfo</code> to this station.
     * @param token if <code>null</code>, then a token will be acquired by this method
     * and this method will fire events, otherwise this methods is executed silently
     * @param putInfo information about where to drop the new {@link Dockable}
     * @param item detailed information about the drag and drop operation that is going on
	 */
	public void drop( Token token, PutInfo putInfo, StationDropItem item );
}
