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

import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.station.stack.CombinedInfoComponent;
import bibliothek.gui.dock.station.stack.CombinedTab;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Used by some {@link DockTheme}s, this interface tells how to distribute the {@link DockAction}s
 * between {@link CombinedTab tabs}, {@link DockTitle titles} and {@link CombinedInfoComponent info components}.
 * @author Benjamin Sigg
 */
public interface DockActionDistributor {
	/**
	 * Represents one place where a {@link DockActionSource} can be used.
	 * @author Benjamin Sigg
	 */
	public static class Target {
		/** On a {@link CombinedTab} */
		public static final Target TAB = new Target( "dock.tab" );
		/** On a {@link DockTitle} */
		public static final Target TITLE = new Target( "dock.title" );
		/** On an {@link CombinedInfoComponent} */
		public static final Target INFO_COMPONENT = new Target( "dock.info" );
		
		private String id;
		
		/**
		 * Creates a new kind of target.
		 * @param id the new kind of target
		 */
		public Target( String id ){
			this.id = id;
		}
		
		@Override
		public int hashCode(){
			return id.hashCode();
		}
		
		@Override
		public boolean equals( Object obj ){
			if (obj == this) {
				return true;
			}

			if (obj == null) {
				return false;
			}

			if (obj.getClass() == this.getClass()) {
				return ((Target) obj).id.equals(id);
			}

			return false;
		}
	};

	/**
	 * Creates a selection of the {@link DockAction}s that are to be shown on a <code>target</code>.
	 * @param dockable the source of the actions
	 * @param target where the source will be used
	 * @return the actions 
	 * @throws IllegalArgumentException if <code>target</code> is unknown to this distributor
	 */
	public DockActionSource createSource( Dockable dockable, Target target );
}
