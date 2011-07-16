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
package bibliothek.gui.dock.event;

import javax.swing.LookAndFeel;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;

/**
 * A listener added to the {@link DockController}. This listener gets informed
 * when the {@link LookAndFeel} or the {@link DockTheme} changes.
 * @author Benjamin Sigg
 */
public interface UIListener {
    /**
     * Called when the {@link LookAndFeel} has been exchanged.
     * @param controller the calling controller
     */
    public void updateUI( DockController controller );
    
	/**
	 * Called before the {@link DockTheme} of <code>controller</code> changes.
	 * @param controller the source of the event
	 * @param oldTheme the current theme
	 * @param newTheme the theme that gets applied
	 */
	public void themeWillChange( DockController controller, DockTheme oldTheme, DockTheme newTheme );
	
	/**
	 * Called after the {@link DockTheme} of <code>controller</code> was changed.
	 * @param controller the source of the event
	 * @param oldTheme the theme that was used before the event
	 * @param newTheme the current theme
	 */
	public void themeChanged( DockController controller, DockTheme oldTheme, DockTheme newTheme );
}
