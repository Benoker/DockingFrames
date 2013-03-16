/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.path;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.extension.css.doc.CssDocKey;
import bibliothek.gui.dock.extension.css.doc.CssDocPathNode;
import bibliothek.gui.dock.extension.css.doc.CssDocText;

/**
 * This node describes the child of a {@link StackDockStation}.
 * @author Benjamin Sigg
 */
@CssDocPathNode(
		name=@CssDocKey(key=StackDockStationNode.NAME),
		description=@CssDocText(text="Relation between a StackDockStation and its child(ren)"),
		properties={
			@CssDocKey(key="index", description=@CssDocText(text="Location of the child on this StackDockStation"))},
		pseudoClasses={
			@CssDocKey(key="selected", description=@CssDocText(text="Applied if the child is selected"))})
public class StackDockStationNode extends AbstractCssNode{
	/** the name of this node */
	public static final String NAME = "stack-child";
	
	/** the station to observe */
	private StackDockStation station;
	
	/** the dockable whose location has to be monitored */
	private Dockable dockable;
	
	private DockStationListener stationListener = new DockStationAdapter(){
		@Override
		public void dockablesRepositioned( DockStation station, Dockable[] dockables ){
			for( Dockable item : dockables ){
				if( item == dockable ){
					fireNodeChanged();
					break;
				}
			}
		}
		@Override
		public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
			if( oldSelection == dockable || newSelection == dockable ){
				fireNodeChanged();
			}
		}
	};
	
	/**
	 * Creates a new node
	 * @param station the station to observe
	 * @param dockable the child which is represented by this node
	 */
	public StackDockStationNode( StackDockStation station, Dockable dockable ){
		if( station == null ){
			throw new IllegalArgumentException( "station must not be null" );
		}
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		this.station = station;
		this.dockable = dockable;
	}
	
	@Override
	public String getName(){
		return NAME;
	}
	
	@Override
	public String getProperty( String key ){
		if( "index".equals( key )){
			return String.valueOf( station.indexOf( dockable ));
		}
		return null;
	}
	
	@Override
	public boolean hasPseudoClass( String className ){
		if( "selected".equals( className )){
			return station.getFrontDockable() == dockable;
		}
		return false;
	}
	
	@Override
	protected void bind(){
		station.addDockStationListener( stationListener );
		
	}
	
	@Override
	protected void unbind(){
		station.removeDockStationListener( stationListener );
	}
}
