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
package bibliothek.gui.dock.frontend;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.DockElement;

/**
 * A set of {@link Setting}s, represents the content of the file that is used by
 * {@link DockFrontend} to store the layout. The {@link SettingsBlop} is not yet
 * associated with {@link DockElement}s, hence it is lightweight and an application
 * can easily store more than one blop.
 * @author Benjamin Sigg
 */
public class SettingsBlop {
	/** all the settings that are known */
	private Map<String, Setting> settings = new HashMap<String, Setting>();
	
	/** The name of {@link #currentSetting}, can be <code>null</code> */
	private String currentName;
	
	/** the setting that is selected and whose layout is shown */
	private Setting currentSetting;
	
	/**
	 * Stores <code>setting</code> in a {@link Map} using <code>name</code> as key.
	 * @param name the key of <code>setting</code>
	 * @param setting the data to store
	 */
	public void put( String name, Setting setting ){
		if( name == null ){
			throw new IllegalArgumentException( "name must not be null" );
		}
		if( setting == null ){
			throw new IllegalArgumentException( "setting must not be null" );
		}
		settings.put( name, setting );
	}
	
	/**
	 * Removes the {@link Setting} <code>name</code> from this blop.
	 * @param name the name of the setting to remove
	 */
	public void remove( String name ){
		settings.remove( name );
	}
	
	/**
	 * Gets the names of all {@link Setting}s that are stored in this blop.
	 * @return all the names
	 */
	public String[] getNames(){
		return settings.keySet().toArray( new String[ settings.size() ] );
	}
	
	/**
	 * Gets the {@link Setting} which was {@link #put(String, Setting) stored} using the
	 * key <code>name</code>.
	 * @param name the name of a setting
	 * @return the setting or <code>null</code> if <code>name</code> is not known
	 */
	public Setting getSetting( String name ){
		return settings.get( name );
	}
	
	/**
	 * Sets name and setting of the current layout. The current layout is the layout
	 * that is shown on the application (or would be shown if this {@link SettingsBlop}
	 * is not loaded).
	 * @param name the name of the current setting, can be <code>null</code>
	 * @param setting the current setting, should not be <code>null</code>
	 */
	public void setCurrent( String name, Setting setting ){
		this.currentName = name;
		this.currentSetting = setting;
	}
	
	/**
	 * Gets the name of the {@link #getCurrentSetting() current setting}.
	 * @return the name of the current setting, may be <code>null</code>
	 */
	public String getCurrentName(){
		return currentName;
	}
	
	/**
	 * Gets the current setting, the layout that is currently shown by the application.
	 * @return the current setting, may be <code>null</code>
	 */
	public Setting getCurrentSetting(){
		return currentSetting;
	}
}
