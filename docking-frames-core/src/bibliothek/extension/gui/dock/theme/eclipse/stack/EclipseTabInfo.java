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
package bibliothek.extension.gui.dock.theme.eclipse.stack;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.station.stack.DockActionCombinedInfoComponent;
import bibliothek.gui.dock.station.stack.tab.TabPane;

/**
 * This component shows a subset of {@link DockAction}s of the currently selected
 * {@link Dockable} of its parent {@link TabPane}. The global {@link EclipseThemeConnector}
 * is used to determine which actions to show, only non-tab actions are shown.
 * @author Benjamin Sigg
 */
// Note: no BackgroundComponent, this panel is completely transparent
public class EclipseTabInfo extends DockActionCombinedInfoComponent {
	private EclipseTabPane pane;
	private EclipseDockActionSource currentActions;
	
	/**
	 * Creates a new component.
	 * @param pane the owner of this info
	 */
	public EclipseTabInfo( EclipseTabPane pane ){
		super( pane );
		this.pane = pane;
	}
		
	@Override
	protected DockActionSource createActionSource( Dockable dockable ){
		EclipseTab tab = pane.getOnTab( dockable );
		currentActions = new EclipseDockActionSource( pane.getTheme(), dockable.getGlobalActionOffers(), tab.getEclipseTabStateInfo(), false );
		return currentActions;
	}
	
	@Override
	protected void updateContent(){
		super.updateContent();
		if( getSelection() == null ){
			currentActions = null;
		}
	}
	
	/**
	 * Refreshes the list of actions that are shown on this panel.
	 */
	public void refreshActions(){
		if( currentActions != null ){
			currentActions.refresh();
		}
	}
}
