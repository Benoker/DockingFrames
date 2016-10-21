/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.flap.button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This abstract implementation of a {@link ButtonContentCondition} adds itself as {@link DockableListener} to any
 * {@link Dockable} that is installed. Subclasses may override the methods of the {@link DockableListener} and
 * react on changes (the default behavior is always to do nothing).
 * @author Benjamin Sigg
 */
public abstract class AbstractButtonContentCondition implements ButtonContentCondition, DockableListener{
	private Map<Dockable, List<ButtonContent>> contents = new HashMap<Dockable, List<ButtonContent>>();
	
	public void install( Dockable dockable, ButtonContent content ){
		List<ButtonContent> list = contents.get( dockable );
		if( list == null ){
			list = new ArrayList<ButtonContent>();
			contents.put( dockable, list );
			install( dockable );
		}
		list.add( content );
	}
	
	public void uninstall( Dockable dockable, ButtonContent content ){
		List<ButtonContent> list = contents.get( dockable );
		if( list != null ){
			list.remove( content );
			if( list.isEmpty() ){
				contents.remove( dockable );
				uninstall( dockable );
			}
		}
	}
	
	/**
	 * Calls the method {@link ButtonContent#handleChange(Dockable)} on all {@link ButtonContent}s that are
	 * currently using this {@link ButtonContentCondition} that that have installed <code>dockable</code>.
	 * @param dockable the element whose properties have changed
	 */
	protected void fire( Dockable dockable ){
		List<ButtonContent> list = contents.get( dockable );
		if( list != null ){
			for( ButtonContent content : list ){
				content.handleChange( dockable );
			}
		}
	}

	/**
	 * Called when <code>dockable</code> has to be observed.
	 * @param dockable the element to observe
	 */
	protected void install( Dockable dockable ){
		dockable.addDockableListener( this );
	}
	
	/**
	 * Called when <code>dockable</code> no longer has to be observed.
	 * @param dockable the element that no longer needs to be observed
	 */
	protected void uninstall( Dockable dockable ){
		dockable.removeDockableListener( this );
	}
	
	public void titleBound( Dockable dockable, DockTitle title ){
		// ignore
	}
	
	public void titleExchanged( Dockable dockable, DockTitle title ){
		// ignore
	}
	
	public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ){
		// ignore
	}
	
	public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ){
		// ignore		
	}
	
	public void titleToolTipChanged( Dockable dockable, String oldToolTip, String newToolTip ){
		// ignore	
	}
	
	public void titleUnbound( Dockable dockable, DockTitle title ){
		// ignore
	}
}
