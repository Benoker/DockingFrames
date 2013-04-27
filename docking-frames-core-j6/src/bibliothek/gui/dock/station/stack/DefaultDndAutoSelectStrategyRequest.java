/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.TransferHandler.TransferSupport;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;

/**
 * Default implementation of a {@link DndAutoSelectStrategy}, it is based on {@link TransferSupport}.
 * @author Benjamin Sigg
 */
public class DefaultDndAutoSelectStrategyRequest implements DndAutoSelectStrategyRequest{
	/** the station that detected the event */
	private StackDockStation station;
	
	/** the {@link Dockable} beneath the mouse */
	private Dockable dockable;
	
	/** additional information about the event */
	private TransferSupport transferSupport;
	
	/**
	 * Creates a new request object.
	 * @param station the source of the event
	 * @param dockable the dockable beneath the mouse
	 * @param transferSupport additional information about the event
	 */
	public DefaultDndAutoSelectStrategyRequest( StackDockStation station, Dockable dockable, TransferSupport transferSupport ){
		this.station = station;
		this.dockable = dockable;
		this.transferSupport = transferSupport;
	}

	@Override
	public StackDockStation getStation(){
		return station;
	}

	@Override
	public Dockable getDockable(){
		return dockable;
	}

	@Override
	public DataFlavor[] getDataFlavors(){
		return transferSupport.getDataFlavors();
	}

	@Override
	public Transferable getTransferable(){
		return transferSupport.getTransferable();
	}

	@Override
	public void toFront(){
		DockController controller = dockable.getController();
		if( controller != null ){
			controller.setFocusedDockable( dockable, false );
		}
	}
}
