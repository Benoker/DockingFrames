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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link bibliothek.gui.dock.action.DockAction} that it should be shown
 * in the tabs when the {@link bibliothek.extension.gui.dock.theme.EclipseTheme}
 * is used. This annotation receives only attention when the 
 * {@link DefaultEclipseThemeConnector} is used.
 * @author Benjamin Sigg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EclipseTabDockAction {
	/**
	 * The location of the action if the tab is neither selected nor focused.
	 * @return behavior if the tab is not selected, not <code>null</code>
	 */
	public EclipseTabDockActionLocation normal() default EclipseTabDockActionLocation.TAB;
	
	/**
	 * The location of the action if the tab is selected.
	 * @return behavior if the tab is selected, not <code>null</code>
	 */
	public EclipseTabDockActionLocation selected() default EclipseTabDockActionLocation.TAB;
	
	/**
	 * The location of the action if the tab is selected and focused.
	 * @return behavior if the tab is selected and focused, not <code>null</code>
	 */	
	public EclipseTabDockActionLocation focused() default EclipseTabDockActionLocation.TAB;
}
