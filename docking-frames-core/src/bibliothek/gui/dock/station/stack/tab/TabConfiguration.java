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
package bibliothek.gui.dock.station.stack.tab;


/**
 * A set of configurations for defining the look of tabs. New configurations are
 * created by the {@link TabConfigurations} factory.
 * @author Benjamin Sigg
 */
public class TabConfiguration {
	/** Tells at which moment actions on a tab should be made invisible */
	public static enum ActionHiding{
		/** they just stay */
		NEVER,
		/** actions are made invisible when the tab itself is smaller then an action-button */
		NO_SPACE_LEFT,
		/** actions are made invisible right before the icon on the tab would disappear */
		ICON_DISAPPEARING,
		/** actions are made invisible right before the text on the tab would disappear */
		TEXT_DISAPPEARING
	}
	
	/** if and when to hide the actions */
	private ActionHiding actionHiding = ActionHiding.NO_SPACE_LEFT;
	
	/**
	 * Tells if and when to make buttons on the tab invisible.
	 * @param actionHiding hiding strategy, not <code>null</code>
	 */
	public void setActionHiding( ActionHiding actionHiding ){
		if( actionHiding == null ){
			throw new IllegalArgumentException( "actionHiding must not be null" );
		}
		this.actionHiding = actionHiding;
	}
	
	/**
	 * Gets if and when buttons on the tab are made invisible.
	 * @return the strategy, not <code>null</code>
	 */
	public ActionHiding getActionHiding(){
		return actionHiding;
	}
}
