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
package bibliothek.gui.dock.util.laf;

import java.awt.Color;

import javax.swing.LookAndFeel;

import bibliothek.gui.DockUI;

/**
 * {@link LookAndFeelColors} is a wrapper around a {@link LookAndFeel}
 * and allows access to a set of colors used for specific purposes.
 * @author Benjamin Sigg
 * @see DockUI#registerColors(String, LookAndFeelColors)
 */
public interface LookAndFeelColors {
    /**
     * The background of unselected title components. Normally the same
     * as an unselected menu item.
     */
    public static final String TITLE_BACKGROUND = "dock.title.background";
    
    /**
     * The foreground of unselected title components. Normally the same
     * as an unselected menu item.
     */
    public static final String TITLE_FOREGROUND = "dock.title.foreground";
    
    /**
     * The background of selected title components. Normally the same as
     * a selected menu item.
     */
    public static final String TITLE_SELECTION_BACKGROUND = "dock.title.selection.background";
    
    /**
     * The foreground of selected title components. Normally the same as
     * a selected menu item.
     */
    public static final String TITLE_SELECTION_FOREGROUND = "dock.title.selection.foreground";
    
    /**
     * The color used for ordinary selections. Normally the same as the
     * background color of a selection in a textfield.
     */
    public static final String SELECTION = "dock.selection.background";
    
    /**
     * The background color for any kind of panels
     */
    public static final String PANEL_BACKGROUND = "dock.background";
    
    /**
     * The foreground color for any kind of panels
     */
    public static final String PANEL_FOREGROUND = "dock.foreground";
    
    /**
     * The shadow around controls
     */
    public static final String CONTROL_SHADOW = "dock.control.shadow";
    
    /**
     * The color for borders around internal windows.
     */
    public static final String WINDOW_BORDER = "dock.window.border";
    
    /**
     * Searches for a color respecting the properties of the current
     * LookAndFeel.
     * @param key one of the string keys defined in this interface, clients
     * may extend the set of keys.
     * @return the color that matches <code>key</code> or <code>null</code>
     */
    public Color getColor( String key );
    
    /**
     * Called when this objects gets activated. The object may now register
     * listeners or do other resource hungry stuff.
     */
    public void bind();
    
    /**
     * Called when this objects gets deactivated. The object may now
     * unregister listeners and free resources.
     */
    public void unbind();
    
    /**
     * Adds a listener to this object, the listener must be informed when 
     * a color changes.
     * @param listener the listener to add
     */
    public void addListener( LookAndFeelColorsListener listener );
    
    /**
     * Removes a listener from this object.
     * @param listener the listener to remove
     */
    public void removeListener( LookAndFeelColorsListener listener );
}
