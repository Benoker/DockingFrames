/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.action;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionOffer;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.util.FrameworkOnly;

/**
 * An {@link ActionOffer} that searches for {@link CommonDockable}s
 * and asks them for their {@link CommonDockable#getSources() sources}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class CActionOffer implements ActionOffer {
	/** the owner */
	private CControl control;
	
	/** whether the secondary search is in progress*/
	private boolean onRecall = false;
	
	/**
	 * Creates a new action offer
	 * @param control the control for which this offer is used
	 */
	public CActionOffer( CControl control ){
		this.control = control;
	}
	
	public boolean interested( Dockable dockable ){
		if( onRecall )
			return false;
		
		return dockable instanceof CommonDockable;
	}

	public DockActionSource getSource( Dockable dockable,
			DockActionSource source, DockActionSource[] guards,
			DockActionSource parent, DockActionSource[] parents ){

		DockActionSource[] sources = ((CommonDockable)dockable).getSources();
		
		int sizeSources = sources == null ? 0 : sources.length;
		int sizeGuards = guards == null ? 0 : guards.length;
		
		DockActionSource[] newGuards;
		if( sizeSources == 0 ){
			newGuards = guards;
		}
		else if( sizeGuards == 0 ){
			newGuards = sources;
		}
		else{
			newGuards = new DockActionSource[ sizeSources + sizeGuards ];
			System.arraycopy( sources, 0, newGuards, 0, sizeSources );
			System.arraycopy( guards, 0, newGuards, sizeSources, sizeGuards );
		}
		
		try{
			onRecall = true;
			return control.intern().getController().getActionOffer( dockable ).getSource( dockable, source, newGuards, parent, parents );
		}
		finally{
			onRecall = false;
		}
	}
	
	
}
