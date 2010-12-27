/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.flap.button;

import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;

/**
 * This interface describes a property that is used by a {@link ButtonContent}
 * @author Benjamin Sigg
 *
 */
public interface ButtonContentCondition {

	/**
	 * Tells whether some property is <code>true</code> or <code>false</code> depending on the properties that
	 * are actually available.
	 * @param dockable the dockable for which the property will be used
	 * @param themeSuggestion whether the current {@link DockTheme} would choose this property to be <code>true</code> or <code>false</code>
	 * @return the value of the property
	 */
	public abstract boolean shouldShow( Dockable dockable, boolean themeSuggestion );

	/**
	 * Informs this {@link ButtonContentCondition} that <code>dockable</code> has to be monitored
	 * for changes. 
	 * @param dockable the element to monitor
	 * @param content the {@link ButtonContent} which is using this condition and whose method
	 * {@link ButtonContent#handleChange(Dockable)} should be called if this condition changes
	 * its value for <code>dockable</code>
	 */
	public abstract void install( Dockable dockable, ButtonContent content );

	/**
	 * Informs this {@link ButtonContentCondition} that <code>dockable</code> no longer has to
	 * be monitored
	 * @param dockable the element that no longer has to be monitored
	 * @param content the {@link ButtonContent} which was using this condition
	 */
	public abstract void uninstall( Dockable dockable, ButtonContent content );

}