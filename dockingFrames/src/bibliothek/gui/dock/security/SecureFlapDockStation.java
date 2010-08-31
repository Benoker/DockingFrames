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

package bibliothek.gui.dock.security;

import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.FlapWindow;

/**
 * A {@link FlapDockStation} that uses {@link SecureFlapWindow} instead
 * of {@link FlapWindow}.
 * @author Benjamin Sigg
 */
public class SecureFlapDockStation extends FlapDockStation {
	/** the panel monitoring mouse-events */
	private GlassedPane glassedPane;
	
	/**
	 * Creates a new station
	 */
	public SecureFlapDockStation(){
		super( false );
		init();
	}
	
	@Override
	public void setController( DockController controller ){
		DockController old = getController();
		if( old != null ){
			((SecureDockController)old).getFocusObserver().removeGlassPane( glassedPane );
		}
		
		super.setController( controller );
		
		if( controller != null ){
			((SecureDockController)controller).getFocusObserver().addGlassPane( glassedPane );
		}
	}
	
	@Override
	protected ButtonPane createButtonPane(){
		glassedPane = new GlassedPane();
		ButtonPane pane = new ButtonPane( this );
		JComponent content = pane.getContentPane();
		pane.setBasePane( glassedPane );
		glassedPane.setContentPane( content );
		pane.setContentPane( content );
		return pane;
	}
	
    @Override
    public String getFactoryID() {
        return SecureFlapDockStationFactory.ID;
    }
}
