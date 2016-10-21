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
package bibliothek.gui.dock.station.screen.magnet;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.util.ClientOnly;

/**
 * The {@link AttractorStrategy} is used by the {@link MagnetController} to find out
 * whether two {@link ScreenDockWindow}s are attracting each other. More than one 
 * {@link AttractorStrategy} can be used at the same time when creating a {@link MultiAttractorStrategy}.
 * @author Benjamin Sigg
 */
public interface AttractorStrategy {
	/**
	 * Tells how two {@link Dockable}s interact with each other.
	 * @author Benjamin Sigg
	 */
	public static enum Attraction{
		/** 
		 * The {@link Dockable}s are attracted to each other. This value is never used
		 * by the framework, only by clients. It overrides all other values.
		 */
		@ClientOnly
		STRONGLY_ATTRACTED(0), 
		
		/**
		 * The {@link Dockable}s are attracted to each other. This value overrides
		 * than {@link Attraction#NEUTRAL} and {@link Attraction#REPELLED}.
		 */
		ATTRACTED(2), 
		
		/**
		 * There is no attraction between the {@link Dockable}s. This is the weakest
		 * value of all.
		 */
		NEUTRAL(4), 
		
		/**
		 * The {@link Dockable}s do not interact with each other. In fact they prefer not
		 * to be placed near each other. Most {@link MagnetStrategy}s will interpret this
		 * as "they do not interact". This value overrides {@link #NEUTRAL}.
		 */
		REPELLED(3),
		
		/**
		 * The {@link Dockable}s do not interact with each other. In fact they prefer not
		 * to be placed near each other. Most {@link MagnetStrategy}s will interpret this
		 * as "they do not interact". This value is never used by the framework, only by
		 * clients. It overrides any other value expect {@link #STRONGLY_ATTRACTED}.
		 */
		@ClientOnly
		STRONGLY_REPELLED(1);
		
		/** how strong this {@link Attraction} is */
		private int priority;
		
		private Attraction(int priority){
			this.priority = priority;
		}
		
		/**
		 * Gets the stronger of <code>this</code> and <code>other</code> {@link Attraction}.
		 * @param other some value, not <code>null</code>
		 * @return the stronger value
		 */
		public Attraction stronger( Attraction other ){
			if( other.priority < priority ){
				return other;
			}
			else{
				return this;
			}
		}
	}
	
	/**
	 * Called when the {@link Dockable} <code>moved</code> has been moved. This method
	 * tells whether the unmoved {@link Dockable} <code>fixed</code> can attract <code>moved</code>. This
	 * method does not have to check the actual position or size of any {@link Dockable}, this method
	 * only has to tell whether an attraction is theoretically possible or not.  
	 * @param parent the parent {@link DockStation} of <code>fixed</code> and of <code>moved</code>
	 * @param moved a {@link Dockable} that did change its position or size
	 * @param fixed a {@link Dockable} that did not change its position or size
	 * @see Attraction how <code>fixed</code> and <code>moved</code> interact
	 */
	public Attraction attract( ScreenDockStation parent, Dockable moved, Dockable fixed );
	
	/**
	 * Called when the {@link Dockable} <code>moved</code> has been moved. This method then tells whether
	 * the unmoved {@link Dockable} <code>fixed</code> can stick to <code>moved</code> and be moved
	 * as well. This method does not need to check the actual position or size of any {@link Dockable}, 
	 * this method only has to tell whether the two items could stick together. Also 
	 * stickiness does not imply {@link #attract(ScreenDockStation, Dockable, Dockable) attraction} or the other
	 * way around.
	 * @param parent the parent {@link DockStation} of <code>moved</code> and of <code>fixed</code>
	 * @param moved a {@link Dockable} that did change its position or size
	 * @param fixed a {@link Dockable} that did not change its position or size
	 * @see Attraction how <code>fixed</code> and <code>moved</code> interact
	 */
	public Attraction stick( ScreenDockStation parent, Dockable moved, Dockable fixed );
}
