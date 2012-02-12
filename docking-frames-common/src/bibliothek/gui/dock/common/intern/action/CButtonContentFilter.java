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
package bibliothek.gui.dock.common.intern.action;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.AbstractButtonContentFilter;
import bibliothek.gui.dock.action.ButtonContentFilter;
import bibliothek.gui.dock.action.ButtonContentFilterListener;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonDockAction;

/**
 * This {@link ButtonContentFilter} searches for {@link CDecorateableAction}s and decides
 * whether their text is shown 
 * @author Benjamin Sigg
 */
public class CButtonContentFilter extends AbstractButtonContentFilter{
	/**
	 * This listener is added to all known {@link CDecorateableAction}s, events will be
	 * forwarded to the {@link ButtonContentFilterListener}s.
	 */
	private CDecorateableActionListener listener = new CDecorateableActionListener(){
		public void showTextOnButtonsChanged( CDecorateableAction<? extends DockAction> action ){
			fire( null, action.intern() );
		}
	};
	
	public boolean showText( Dockable dockable, DockAction action ){
		CDecorateableAction<?> caction = get( action );
		if( caction == null ){
			return false;
		}
		return caction.isShowTextOnButtons();
	}

	@Override
	protected void installed( DockAction action ){
		CDecorateableAction<?> caction = get( action );
		if( caction != null ){
			caction.addDecorateableActionListener( listener );
		}
	}
	
	@Override
	protected void uninstalled( DockAction action ){
		CDecorateableAction<?> caction = get( action );
		if( caction != null ){
			caction.removeDecorateableActionListener( listener );
		}
	}
	
	private CDecorateableAction<?> get( DockAction action ){
		if( action instanceof CommonDockAction ){
			CAction caction = ((CommonDockAction)action).getAction();
			if( caction instanceof CDecorateableAction<?> ){
				return (CDecorateableAction<?>)caction;
			}
		}
		return null;
	}
}
