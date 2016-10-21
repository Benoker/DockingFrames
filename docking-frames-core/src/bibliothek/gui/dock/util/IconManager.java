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

package bibliothek.gui.dock.util;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownIcon;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.gui.dock.util.icon.DockIconBridge;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;

/**
 * A map of icons which are used by various objects. The icon is stored
 * under a given String-key. Every icon can be stored with three different
 * {@link Priority priorities}, only the icon with the highest priority
 * will be returned.<br>
 * Clients have to add an {@link DockIcon} in order to read values from this
 * manager.<br>
 * The default icons are stored as png files alongside the framework, the "icons.ini" files
 * tells what keys are used for the icons. Additional keys for icons are:
 * <ul>
 * 	<li>{@link DropDownIcon#ICON_KEY}: this icon is painted for {@link DropDownAction}s, it is
 * a small triangle pointing downwards. </li>
 *  <li>{@link DockUI#OVERFLOW_MENU_ICON}: the icon used for the menu that is created if there are too many buttons
 *  of actions in a row.</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class IconManager extends UIProperties<Icon, DockIcon, DockIconBridge>{
	/**
	 * What size the framework should expect the smallest icon to be. This number is used to calculate the
	 * minimum and the preferred size of various {@link Component}s. This is not a hard boundary, just expect any
	 * icon smaller than this constant to be surrounded by some additional empty space. The default size
	 * is 16x16 pixels.
	 */
	public static final PropertyKey<Dimension> MINIMUM_ICON_SIZE = new PropertyKey<Dimension>( "dock.icon.minimumSize", 
			new ConstantPropertyFactory<Dimension>( new Dimension( 16, 16 )), true );
	
    /**
     * Creates a new {@link IconManager}.
     * @param controller the owner of this map
     */
    public IconManager( DockController controller ){
    	super( controller );
    }
    
    /**
     * Stores an icon. 
     * @param key the key of the icon
     * @param priority the priority, where {@link Priority#CLIENT} is the
     * highest, {@link Priority#DEFAULT} the lowest. Icons stored with the
     * same key but another priority are not deleted.
     * @param icon the icon, <code>null</code> if the icon should be removed
     */
    public void setIcon( String key, Priority priority, Icon icon ){
     	put( priority, key, icon );
    }

    /**
     * Sets an icon with default-priority.
     * @param key the key of the icon
     * @param icon the icon or <code>null</code>
     */
    public void setIconDefault( String key, Icon icon ){
        setIcon( key, Priority.DEFAULT, icon );
    }

    /**
     * Sets an icon with theme-priority.
     * @param key the key of the icon
     * @param icon the icon or <code>null</code>
     */
    public void setIconTheme( String key, Icon icon ){
        setIcon( key, Priority.THEME, icon );
    }
    
    /**
     * Sets an icon with client-priority.
     * @param key the key of the icon
     * @param icon the icon or <code>null</code>
     */
    public void setIconClient( String key, Icon icon ){
        setIcon( key, Priority.CLIENT, icon );
    }
}
