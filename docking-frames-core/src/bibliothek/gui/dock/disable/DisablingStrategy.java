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

import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTab;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;

/**
 * An {@link DisablingStrategy} can be used to globally disable {@link DockElement}s and parts of
 * {@link DockElement}s like their {@link DockAction}s or their {@link DockTitle}s.<br>
 * Implementation wise an {@link DisablingStrategy} only offers a hint, each item has to decide on its own
 * whether it should heed the hint. All the default items of the framework do however heed the hint.<br>
 * This interface was added due to popular request. Developers should be aware that many use cases involving disabling
 * items are rather dubious. Used in the wrong way, the {@link DisablingStrategy} can break a lot functionality of
 * the framework. E.g. it can prevent the user from closing a {@link Dockable} he just does not need right now.  
 * @author Benjamin Sigg
 */
public interface DisablingStrategy {
	/** The unique identifier for the {@link DockProperties}, need to get or set the current {@link DisablingStrategy}. */
	public static final PropertyKey<DisablingStrategy> STRATEGY = new PropertyKey<DisablingStrategy>( "disabling strategy" );
	
	/**
	 * Adds <code>listener</code> to this object, the listener will be informed when the state of this
	 * {@link DisablingStrategy} changes.
	 * @param listener the listener to add, not <code>null</code>
	 */
	public void addDisablingStrategyListener( DisablingStrategyListener listener );
	
	/**
	 * Removes <code>listener</code> from this object.
	 * @param listener the listener to remove
	 */
	public void removeDisablingStrategyListener( DisablingStrategyListener listener );
	
	/**
	 * Tells whether the item <code>DockElement</code> is disabled in general. The exact effects of being disabled are
	 * not defined, but when using the default implementation developers can expect that <code>item</code> will not
	 * participate in any kind of drag and drop operation.
	 * @param item the item which may be disabled
	 * @return whether <code>item</code> is disabled
	 */
	public boolean isDisabled( DockElement item );
	
	/**
	 * Tells whether the action <code>item</code>, which is shown together with <code>dockable</code>, is disabled.
	 * @param dockable the dockable which shows <code>item</code>
	 * @param item the action that might be disabled
	 * @return whether <code>item</code> is disabled
	 */
	public boolean isDisabled( Dockable dockable, DockAction item );
	
	/**
	 * Tells whether the title <code>item</code>, which is shown together with <code>dockable</code>, is disabled.
	 * @param dockable the dockable which shows <code>item</code>
	 * @param item the title that might be disabled
	 * @return whether <code>item</code> is disabled
	 */
	public boolean isDisabled( Dockable dockable, DockTitle item );
	
	/**
	 * Assuming <code>dockable</code> is shown with some tabs (e.g. some {@link EclipseTab}s), this method decides
	 * whether the tabs are disabled.
	 * @param dockable the dockable which is shown together with some tab
	 * @return whether the tab is disabled
	 */
	public boolean isTabDisabled( Dockable dockable );
}
