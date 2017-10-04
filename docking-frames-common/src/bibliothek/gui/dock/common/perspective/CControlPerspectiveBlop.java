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
package bibliothek.gui.dock.common.perspective;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CSetting;
import bibliothek.gui.dock.common.intern.layout.CControlPerspectiveResource;
import bibliothek.gui.dock.frontend.SettingsBlop;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A {@link CControlPerspectiveBlop} stores all the data a file contains that was written
 * with {@link CControl#write(java.io.DataOutputStream)} or {@link CControl#writeXML(java.io.File)}. This
 * class allows clients to read and modify the layout files without actually loading any {@link DockElement}s.
 * @author Benjamin Sigg
 */
public class CControlPerspectiveBlop {
	/** offers factories necessary to read and write perspectives */
	private CControlPerspective control;
	
	/** stores data that cannot be interpreted by the perspective API  */
	private ApplicationResourceManager resourceManager = new ApplicationResourceManager();
	
	/** reads and writes the actual layout data */
	private CControlPerspectiveResource resource;
	
	public CControlPerspectiveBlop( CControlPerspective control ){
		if( control == null ){
			throw new IllegalArgumentException( "control must not be null" );
		}
		this.control = control;
		resource = new CControlPerspectiveResource( control );
		try {
			resourceManager.put( "ccontrol.frontend", resource );
		}
		catch( IOException e ) {
			throw new IllegalStateException( "This cannot happen", e );
		}
	}

	/**
	 * Gets the names of all {@link #getPerspective(String) perspectives} that are
	 * available.
	 * @return the name of the perspectives
	 */
	public String[] getPerspectiveNames(){
		return resource.getSettings().getNames();
	}
	
	/**
	 * Gets the raw data about the layout that is stored using <code>name</code> as key.
	 * @param name the key of the layout
	 * @return the raw data or <code>null</code> if <code>name</code> was not found
	 * @see #getPerspective(String)
	 */
	public CSetting getSetting( String name ){
		return (CSetting)resource.getSettings().getSetting( name );
	}
	
	/**
	 * Gets the perspective which was stored using <code>name</code> as key.
	 * @param name the key of the layout
	 * @return the perspective or <code>null</code> if <code>name</code> was not found
	 */
	public CPerspective getPerspective( String name ){
		CSetting setting = getSetting( name ); 
		if( setting == null ){
			return null;
		}
		return control.read( setting, false );
	}
	
	/**
	 * Stores a new layout for <code>name</code>.
	 * @param name the name of the layout, not <code>null</code>
	 * @param perspective the new layout, not <code>null</code>
	 */
	public void putPerspective( String name, CPerspective perspective ){
		if( name == null ){
			throw new IllegalArgumentException( "name must not be null" );
		}
		if( perspective == null ){
			throw new IllegalArgumentException( "perspective must not be null" );
		}
		CSetting setting = control.write( perspective, false );
		putSetting( name, setting );
	}
	
	/**
	 * Stores raw data of a layout with name <code>name</code>.
	 * @param name the name of the layout, not <code>null</code>
	 * @param setting the new layout, not <code>null</code>
	 * @see #putPerspective(String, CPerspective)
	 */
	public void putSetting( String name, CSetting setting ){
		resource.getSettings().put( name, setting );
	}
	
	/**
	 * Removes a layout from this blop.
	 * @param name the name of the layout to remove
	 */
	public void removePerspective( String name ){
		resource.getSettings().remove( name );
	}

	/**
	 * Gets the name of the layout that is currently shown.
	 * @return the name of the current layout, can be <code>null</code>
	 */
	public String getPerspectiveName(){
		return resource.getSettings().getCurrentName();
	}
	
	/**
	 * Sets the name of the current layout.
	 * @param name the name of the current layout, can be <code>null</code>
	 */
	public void setPerspectiveName( String name ){
		SettingsBlop blop = resource.getSettings();
		blop.setCurrent( name, blop.getCurrentSetting() );
	}
	
	/**
	 * Gets the current layout.
	 * @return the currently applied layout, may be <code>null</code>
	 */
	public CPerspective getPerspective(){
		CSetting setting = getSetting();
		if( setting == null ){
			return null;
		}
		return control.read( setting, true );
	}
	
	/**
	 * Gets the raw data of the current layout.
	 * @return the raw data
	 */
	public CSetting getSetting(){
		return (CSetting)resource.getSettings().getCurrentSetting();
	}
	
	/**
	 * Sets the layout that should be loaded.
	 * @param perspective the new layout, not <code>null</code>
	 */
	public void setPerspective( CPerspective perspective ){
		CSetting setting = control.write( perspective, true );
		setSetting( setting );
	}
	
	/**
	 * Sets the raw data of the layout that should be loaded.
	 * @param setting the new layout, not <code>null</code>
	 */
	public void setSetting( CSetting setting ){
		SettingsBlop blop = resource.getSettings();
		blop.setCurrent( blop.getCurrentName(), setting );
	}

    /**
     * Performs the same actions as {@link CControl#readXML(XElement)}, this method extracts all layouts of
     * an xml file.
     * @param root the root xml element in the file
     * @throws XException if the structure of <code>root</code> is not as expected
     */
    public void readXML( XElement root ) throws XException{
    	resourceManager.readXML( root );
    }

    /**
     * Performs the same actions as {@link CControl#read(DataInputStream)}, this method extracts all layouts of
     * a byte file.
     * @param in the stream to read from
     * @throws IOException if the stream cannot be read
     */    
    public void read( DataInputStream in ) throws IOException{
    	resourceManager.readStream( in );
    }
    
    /**
     * Performs the same actions as {@link CControl#writeXML(java.io.File)}.
     * @param root the xml element to write into
     */
    public void writeXML( XElement root ){
    	resourceManager.writeXML( root );
    }
    
    /**
     * Performs the same actions as {@link CControl#write(DataOutputStream)}
     * @param out the stream to write into
     * @throws IOException if the stream is not writeable
     */
    public void write( DataOutputStream out ) throws IOException{
    	resourceManager.writeStream( out );
    }
}
