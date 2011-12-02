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
package bibliothek.gui.dock.station.stack.action;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.FilteredDockActionSource;

/**
 * This implementation of a {@link DockActionDistributor} searches for the annotations
 * {@link TabDockAction}, {@link TitleDockAction} and {@link InfoDockAction} to decide which {@link DockAction} should
 * be shown where.
 * @author Benjamin Sigg
 */
public class DefaultDockActionDistributor implements DockActionDistributor{

	public DockActionSource createSource( Dockable dockable, Target target ){
		if( target == Target.TAB ){
			return createTabSource( dockable.getGlobalActionOffers() );
		}
		else if( target == Target.TITLE ){
			return createTitleSource( dockable.getGlobalActionOffers() );
		}
		else if( target == Target.INFO_COMPONENT ){
			return createInfoSource( dockable.getGlobalActionOffers() );
		}
		else{
			throw new IllegalArgumentException( "unkown target: " + target );
		}
	}

	/**
	 * Creates a new {@link DockActionSource} for the {@link DockActionDistributor.Target} {@link DockActionDistributor.Target#TAB}.
	 * @param source the basic actions
	 * @return the filtered actions
	 */
	protected DockActionSource createTabSource( DockActionSource source ){
		return new FilteredDockActionSource( source ){
			@Override
			protected boolean include( DockAction action ){
				return action.getClass().getAnnotation( TabDockAction.class ) != null;
			}
		};
	}
	
	/**
	 * Creates a new {@link DockActionSource} for the {@link DockActionDistributor.Target} {@link DockActionDistributor.Target#INFO_COMPONENT}.
	 * @param source the basic actions
	 * @return the filtered actions
	 */	
	protected DockActionSource createInfoSource( DockActionSource source ){
		return new FilteredDockActionSource( source ){
			@Override
			protected boolean include( DockAction action ){
				return action.getClass().getAnnotation( InfoDockAction.class ) != null;
			}
		};
	}
	
	/**
	 * Creates a new {@link DockActionSource} for the {@link DockActionDistributor.Target} {@link DockActionDistributor.Target#TITLE} or for
	 * those actions that are not marked with any annotation.
	 * @param source the basic actions
	 * @return the filtered actions
	 */
	protected DockActionSource createTitleSource( DockActionSource source ){
		return new FilteredDockActionSource( source ){
			@Override
			protected boolean include( DockAction action ){
				Class<? extends DockAction> clazz = action.getClass();
				if( clazz.getAnnotation( TitleDockAction.class ) != null ){
					return true;
				}
				return clazz.getAnnotation( TabDockAction.class ) == null && clazz.getAnnotation( InfoDockAction.class ) == null;
			}
		};
	}
}
