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

import java.awt.Component;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.container.Tuple;

/**
 * {@link TransferHandler} added to a {@link StackDockComponent}, will be informed if the mouse hovers over a tab, and
 * forwards that information to the client.<br>
 * This class extends {@link TransferHandler}, but it will always return <code>false</code> when
 * calling  {@link TransferHandler#canImport(TransferSupport)}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class DnDAutoSelectSupport extends TransferHandler implements StackDnDAutoSelectSupport{
	/** all the components that are {@link #install(StackDockComponent)} on this {@link DnDAutoSelectSupport} */
	private Map<JComponent, Tuple<StackDockStation, StackDockComponent>> components = new HashMap<JComponent, Tuple<StackDockStation, StackDockComponent>>();
	
	@Override
	public void install( StackDockStation station, StackDockComponent component ){
		Component comp = component.getComponent();
		if( comp instanceof JComponent ){
			JComponent jcomp = (JComponent)comp;
			jcomp.setTransferHandler( this );
			components.put( jcomp, Tuple.of( station, component ) );
		}
	}
	
	@Override
	public void uninstall(StackDockComponent component){
		Component comp = component.getComponent();
		if( comp instanceof JComponent ){
			JComponent jcomp = (JComponent)comp;
			jcomp.setTransferHandler( null );
			components.remove( jcomp );
		}
	}
	
	@Override
	public boolean canImport( TransferSupport support ){
		if( support.isDrop() ){
			Tuple<StackDockStation, StackDockComponent> tuple = components.get( support.getComponent() );
			if( tuple != null ){
				forward( tuple.getA(), tuple.getB(), support );
			}
		}
		return false;
	}
	
	private void forward( StackDockStation station, StackDockComponent component, TransferSupport support ){
		DropLocation location = support.getDropLocation();
		Point mouse = location.getDropPoint();
		int tab = component.getIndexOfTabAt( mouse );
		if( tab != -1 ){
			forward( station, component.getDockableAt( tab ), support );
		}
	}
	
	private void forward( StackDockStation station, Dockable dockable, TransferSupport support ){
		DockController controller = station.getController();
		DndAutoSelectStrategy strategy = controller.getProperties().get( StackDockStation.DND_AUTO_SELECT_STRATEGY );
		if( strategy != null ){
			strategy.handleRequest( new DefaultDndAutoSelectStrategyRequest( station, dockable, support ) );
		}
	}
}
