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
package bibliothek.gui.dock.common;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.intern.FDockable;
import bibliothek.gui.dock.common.intern.FStateManager;
import bibliothek.gui.dock.common.intern.FacileDockable;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * The maximize-behavior is used by the {@link FStateManager} to decide, what
 * happens when the user maximizes or un-maximizes some {@link FDockable}.<br>
 * Clients can use {@link FControl#setMaximizeBehavior(FMaximizeBehavior)} to
 * change the behavior.
 * @author Benjamin Sigg
 *
 */
public interface FMaximizeBehavior {
	/**
	 * A behavior that allows only one {@link Dockable} to be maximized at
	 * a time.
	 */
	public static final FMaximizeBehavior TOPMOST = new FMaximizeBehavior(){
		public Dockable getMaximizingElement( Dockable dockable ){
			return dockable;
		}
		public Dockable getMaximizingElement( Dockable old, Dockable dockable ){
			if( dockable == old )
				return null;
			return old;
		}
	};
	
	public static final FMaximizeBehavior STACKED = new FMaximizeBehavior(){
		public Dockable getMaximizingElement( Dockable dockable ){
			DockStation parent = dockable.getDockParent();
			
			if( parent instanceof StackDockStation ){
				for( int i = 0, n = parent.getDockableCount(); i<n; i++ ){
					Dockable check = parent.getDockable( i );
					if( check != dockable ){
						if( check instanceof FacileDockable ){
							FDockable fdock = ((FacileDockable)check).getDockable();
							if( !fdock.isMaximizable() )
								return dockable;
						}
					}
				}
				return (StackDockStation)parent;
			}
			
			return dockable;
		}
		
		public Dockable getMaximizingElement( Dockable old, Dockable dockable ){
			if( old == dockable )
				return null;
			
			if( !DockUtilities.isAncestor( old, dockable ) )
				return null;
			
			DockStation station = old.asDockStation();
			if( station == null )
				return old;
			
			if( station.getDockableCount() == 2 ){
				if( station.getDockable( 0 ) == dockable )
					return station.getDockable( 1 );
				if( station.getDockable( 1 ) == dockable )
					return station.getDockable( 0 );
			}
			
			return old;
		}
	};
	
    /**
     * Gets the element which must be maximized when the user requests that
     * <code>dockable</code> is maximized. Normally <code>dockable</code> itself
     * is returned, or a parent {@link DockStation} of <code>dockable</code>.
     * @param dockable some element, not <code>null</code>
     * @return the element that must be maximized, might be <code>dockable</code>
     * itself, not <code>null</code>
     */
    public Dockable getMaximizingElement( Dockable dockable );
    
    /**
     * Gets the element which would be maximized if <code>old</code> is currently
     * maximized, and <code>dockable</code> is or will not be maximized.
     * @param old some element
     * @param dockable some element, might be <code>old</code>
     * @return the element which would be maximized if <code>dockable</code> is
     * no longer maximized, can be <code>null</code>
     */
    public Dockable getMaximizingElement( Dockable old, Dockable dockable );
}
