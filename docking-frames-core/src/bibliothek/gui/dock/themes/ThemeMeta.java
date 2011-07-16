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
package bibliothek.gui.dock.themes;

import java.net.URI;

/**
 * A set of information related to a {@link ThemeFactory}.
 * @author Benjamin Sigg
 */
public interface ThemeMeta {
	/**
	 * Gets the factory which created this {@link ThemeMeta}.
	 * @return the source of this meta
	 */
	public ThemeFactory getFactory();
	
	/**
	 * Changes the result of {@link #getFactory()} to <code>factory</code>. This method
	 * is intended to be used by {@link ThemeFactory}s that wrap around other factories.
	 * @param factory the new factory
	 */
	public void setFactory( ThemeFactory factory );
	
	/**
	 * Adds a listener to this meta information, the listener will be 
	 * informed if this meta information changes.
	 * @param listener the new listener
	 */
	public void addListener( ThemeMetaListener listener );
	
	/**
	 * Removes a listener from this
	 * @param listener the listener to remove
	 */
	public void removeListener( ThemeMetaListener listener );
	
    /**
     * Gets a human readable description of the theme.
     * @return the description, might be <code>null</code>
     */
    public String getDescription();
    
    /**
     * Gets the name of the theme.
     * @return the name, might be <code>null</code>
     */
    public String getName();
    
    /**
     * Gets a list of strings, containing the names of the authors.
     * @return the authors, might be <code>null</code>
     */
    public String[] getAuthors();
    
    /**
     * Gets a set of links to any webpage the authors might want to
     * show the user.
     * @return the pages, might be <code>null</code>
     */
    public URI[] getWebpages();
}
