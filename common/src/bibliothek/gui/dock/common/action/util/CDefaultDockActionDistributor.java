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
package bibliothek.gui.dock.common.action.util;

import java.lang.annotation.Annotation;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.FilteredDockActionSource;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonDockAction;
import bibliothek.gui.dock.station.stack.action.DefaultDockActionDistributor;
import bibliothek.gui.dock.station.stack.action.InfoDockAction;
import bibliothek.gui.dock.station.stack.action.TabDockAction;
import bibliothek.gui.dock.station.stack.action.TitleDockAction;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This class decides which {@link DockAction} is shown at which place, places are
 * {@link DockTitle}s, tabs and info-components placed alongside tabs.
 * @author Benjamin Sigg
 */
public class CDefaultDockActionDistributor extends DefaultDockActionDistributor{
	protected DockActionSource createTabSource( DockActionSource source ){
		return new FilteredDockActionSource( source ){
			@Override
			protected boolean include( DockAction action ){
				return hasAnnotation( action, TabDockAction.class );
			}
		};
	}
	
	protected DockActionSource createInfoSource( DockActionSource source ){
		return new FilteredDockActionSource( source ){
			@Override
			protected boolean include( DockAction action ){
				return hasAnnotation( action, InfoDockAction.class );
			}
		};
	}
	
	protected DockActionSource createTitleSource( DockActionSource source ){
		return new FilteredDockActionSource( source ){
			@Override
			protected boolean include( DockAction action ){
				if( hasAnnotation( action, TitleDockAction.class ) ){
					return true;
				}
				return !hasAnnotation( action, TabDockAction.class ) && !hasAnnotation( action, InfoDockAction.class );
			}
		};
	}
	
	private boolean hasAnnotation( DockAction action, Class<? extends Annotation> annotation ){
		if( action.getClass().getAnnotation( annotation ) != null ){
			return true;
		}
		if( action instanceof CommonDockAction ){
			CAction caction = ((CommonDockAction)action).getAction();
			if( caction.getClass().getAnnotation( annotation ) != null ){
				return true;
			}
		}
		return false;
	}
}
