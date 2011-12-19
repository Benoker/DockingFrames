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
package bibliothek.gui.dock.themes.basic.action;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;

/**
 * {@link DockActionImportanceOrder} is used by {@link ButtonPanel} to decide which 
 * actions are more important than others. If there is not enough space, the actions
 * that are less important are moved to a menu. 
 * @author Benjamin Sigg
 */
public interface DockActionImportanceOrder {
	/** A {@link PropertyKey} used to set the order in the {@link DockProperties} */
	public static final PropertyKey<DockActionImportanceOrder> ORDER = new PropertyKey<DockActionImportanceOrder>( "dock.actionImportance",
			new ConstantPropertyFactory<DockActionImportanceOrder>( new DefaultDockActionImportanceOrder() ), true );
	
	/**
	 * Sorts the actions by their important, the most important action
	 * has to be at index <code>0</code>, while the least important action
	 * is at index <code>actions.length-1</code>. This method must not add or remove
	 * any actions from <code>action</code>, it must only rearrange the content of
	 * <code>actions</code>
	 * @param actions the array of actions to sort
	 */
	public void order( DockAction[] actions );
}
