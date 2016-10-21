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
package bibliothek.gui.dock.disable;

import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This class offers a convenient way to observe a set of {@link Dockable} and find
 * out whether their tab should be disabled according to the current {@link DisablingStrategy}. 
 * @author Benjamin Sigg
 */
public abstract class TabDisablingStrategyObserver {
	/** the strategy that is currently used */
	private PropertyValue<DisablingStrategy> disablingStrategy = new PropertyValue<DisablingStrategy>( DisablingStrategy.STRATEGY ){
		@Override
		protected void valueChanged( DisablingStrategy oldValue, DisablingStrategy newValue ){
			if( oldValue != null ){
				oldValue.removeDisablingStrategyListener( disablingStrategyListener );
			}
			if( newValue != null ){
				newValue.addDisablingStrategyListener( disablingStrategyListener );
			}
			for( Dockable item : items ){
				setDisabled( item, isDisabled( item ));
			}
		}
	};
	
	/** Listener added to {@link #disablingStrategy} */
	private DisablingStrategyListener disablingStrategyListener = new DisablingStrategyListener(){
		public void changed( DockElement item ){
			Dockable dockable = item.asDockable();
			if( dockable != null && items.contains( dockable )){
				setDisabled( dockable, isDisabled( dockable ) );
			}
		}
	};
	
	/** all the {@link Dockable}s that are observed by this strategy */
	private Set<Dockable> items = new HashSet<Dockable>();
	
	/**
	 * Sets the controller in whose realm this observer is used.
	 * @param controller the controller which provides the {@link DisablingStrategy}
	 */
	public void setController( DockController controller ){
		disablingStrategy.setProperties( controller );
	}

	/**
	 * Adds <code>dockable</code> to the set of dockables that must be observed
	 * @param dockable the new item
	 */
	public void add( Dockable dockable ){
		items.add( dockable );
		setDisabled( dockable, isDisabled( dockable ));
	}
	
	/**
	 * Removes <code>dockable</code> from the set of dockables that must be observed
	 * @param dockable the item to remove
	 */
	public void remove( Dockable dockable ){
		items.remove( dockable );
		setDisabled( dockable, false );
	}
	
	
	/**
	 * Tells whether the tab of <code>dockable</code> is disabled.
	 * @param dockable the item to test
	 * @return whether the tab is disabled according to the current {@link DisablingStrategy}
	 */
	public boolean isDisabled( Dockable dockable ){
		DisablingStrategy strategy = disablingStrategy.getValue();
		if( strategy == null ){
			return false;
		}
		return strategy.isTabDisabled( dockable );
	}
	
	/**
	 * Called if <code>dockable</code> was enabled or disabled.
	 * @param dockable the element whose state changed
	 * @param disabled the new state
	 */
	public abstract void setDisabled( Dockable dockable, boolean disabled );
}
