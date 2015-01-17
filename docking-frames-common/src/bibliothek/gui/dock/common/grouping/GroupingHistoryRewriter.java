/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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
package bibliothek.gui.dock.common.grouping;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.support.mode.HistoryRewriter;

/**
 * This {@link HistoryRewriter} gets the current {@link CGroupingBehavior} using the key {@link CControl#GROUPING_BEHAVIOR}
 * from its {@link CControl}. It then accesses the {@link DockableGrouping} of a {@link Dockable} to rewrite its history.
 * This rewriter makes use of another {@link HistoryRewriter} called "validation", to make sure that a valid location is
 * created.<br>
 * Clients should not have any need to create instances of this class, because any {@link CControl} will already have
 * a {@link GroupingHistoryRewriter} pre-installed.
 * @author Benjamin Sigg
 */
public class GroupingHistoryRewriter implements HistoryRewriter<Location, CLocationMode>{
	/** the {@link CControl} in whose realm this rewriter works */
	private CControl control;
	/** Rewriter that will validate the locations. */
	private HistoryRewriter<Location, CLocationMode> validation;
	
	/**
	 * Creates a new {@link GroupingHistoryRewriter}.
	 * @param control the {@link CControl} in whose realm this history rewriter works
	 * @param validation a rewriter that will be used to validate the location of dockables
	 */
	public GroupingHistoryRewriter( CControl control, HistoryRewriter<Location, CLocationMode> validation ){
		this.control = control;
		this.validation = validation;
	}
	
	public Location rewrite( Dockable dockable, CLocationMode mode, Location history ) {
		if( history != null && history.isApplicationDefined() ){
			return validation.rewrite( dockable, mode, history );
		}
		
		CGroupingBehavior groupingBehavior = control.getProperty( CControl.GROUPING_BEHAVIOR );
		DockableGrouping grouping = groupingBehavior.getGrouping( dockable );
		
		if( grouping == null ){
			return validation.rewrite( dockable, mode, history );
		}
		
		Location result = history;
		result = grouping.getStoredLocation( dockable, mode, result );
		result = validation.rewrite( dockable, mode, result );
		result = grouping.getValidatedLocation( dockable, mode, result );
		return result;
	}
}
