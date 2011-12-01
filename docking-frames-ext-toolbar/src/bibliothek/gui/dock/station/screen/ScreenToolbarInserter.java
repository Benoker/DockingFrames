/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen;

import java.awt.Dimension;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.control.relocator.Inserter;
import bibliothek.gui.dock.control.relocator.InserterSource;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OrientingDockStation;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;

/**
 * This {@link Inserter} searches for toolbars using the {@link ToolbarStrategy}, and adds them to
 * {@link ScreenDockStation}s, creating any additional layers that are required for the operation.
 * @author Benjamin Sigg
 */
public class ScreenToolbarInserter implements Inserter{
	private DockController controller;
	
	/**
	 * Creates a new inserter
	 * @param controller the controller in whose realm this inserter will be used
	 */
	public ScreenToolbarInserter( DockController controller ){
		this.controller = controller;
	}
	
	@Override
	public StationDropOperation before( InserterSource source ){
		return null;
	}
	
	/**
	 * Gets the {@link ToolbarStrategy} that is currently in use.
	 * @return the current strategy
	 */
	protected ToolbarStrategy getStrategy(){
		return controller.getProperties().get( ToolbarStrategy.STRATEGY );
	}
	
	@Override
	public StationDropOperation after( InserterSource source ){
		if( source.getOperation() != null || !(source.getParent() instanceof ScreenDockStation) ){
			return null;
		}
		ToolbarStrategy strategy = getStrategy();
		if( !strategy.isToolbarPart( source.getChild() )){
			return null;
		}
		
		if( source.getParent().accept( source.getChild() ) && source.getChild().accept( source.getParent() )){
			// if a DockAcceptance does not allow the combination, while the involved elements does, we better
			// assume the DockAcceptance has a good reason.
			return null;
		}
		
		return new Operation( source );
	}
	
	/**
	 * Called if the toolbar item defined by <code>source</code> has to be put onto
	 * the {@link ScreenDockStation} defined by <code>source</code>.
	 * @param source all the information about the operation
	 * @param orientation the preferred orientation of the new window, might be <code>null</code>
	 */
	protected void execute( InserterSource source, Orientation orientation ){
		ToolbarStrategy strategy = getStrategy();
				
		Dockable item = strategy.ensureToolbarLayer( source.getParent(), source.getChild() );
		if( orientation != null && item.asDockStation() instanceof OrientedDockStation ){
			((OrientedDockStation)item).setOrientation( orientation );
		}
		
		ScreenDockStation station = (ScreenDockStation)source.getParent();
		
		item.getComponent().validate();
		Dimension size = item.getComponent().getPreferredSize();
		
		ScreenDockProperty location = new ScreenDockProperty( source.getTitleX(), source.getTitleY(), size.width, size.height );
		station.drop( item, location, false );
	}
	
	/**
	 * This {@link StationDropOperation} will add a toolbar part to a {@link ScreenDockStation}
	 * using {@link ToolbarStrategy#ensureToolbarLayer(DockStation, Dockable)}
	 * @author Benjamin Sigg
	 */
	private class Operation implements StationDropOperation{
		private InserterSource source;
		private Orientation orientation;
		
		/**
		 * Creates a new operation
		 * @param source information about the operation that is going to happen
		 */
		public Operation( InserterSource source ){
			this.source = source;
			
			Dockable dockable = source.getChild();
			DockStation parent = dockable.getDockParent();
			
			orientation = null;
			if( parent instanceof OrientingDockStation ){
				orientation = ((OrientingDockStation)parent).getOrientationOf( dockable );
			}
			else if( dockable.asDockStation() instanceof OrientedDockStation ){
				orientation = ((OrientedDockStation)dockable.asDockStation()).getOrientation();
			}
		}
		
		@Override
		public void draw(){
			// nothing to do
		}

		@Override
		public void destroy(){
			// nothing to do
		}

		@Override
		public boolean isMove(){
			return false;
		}

		@Override
		public void execute(){
			ScreenToolbarInserter.this.execute( source, orientation );
		}

		@Override
		public DockStation getTarget(){
			return source.getParent();
		}

		@Override
		public Dockable getItem(){
			return source.getChild();
		}

		@Override
		public CombinerTarget getCombination(){
			return null;
		}

		@Override
		public DisplayerCombinerTarget getDisplayerCombination(){
			return null;
		}
		
	}
}
