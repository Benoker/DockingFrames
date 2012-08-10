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
package bibliothek.gui.dock.common.intern.layout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockFrontend;
import bibliothek.gui.dock.common.perspective.CControlPerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.frontend.SettingsBlop;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * An {@link ApplicationResource} that emulates reading and writing layouts
 * like {@link CControl} would do, but creates {@link CPerspective}s instead
 * of actual layouts.
 * @author Benjamin Sigg
 */
public class CControlPerspectiveResource implements ApplicationResource{
	/** Used to create perspectives when necessary */
	private CControlPerspective control;
	
	/** All the content that was read from the {@link CDockFrontend} */
	private SettingsBlop settings = new SettingsBlop();
	
	/**
	 * Creates a new resource.
	 * @param control the control which creates the {@link CPerspective}s
	 */
	public CControlPerspectiveResource( CControlPerspective control ){
		if( control == null ){
			throw new IllegalArgumentException( "control must not be null" );
		}
		this.control = control;
	}
	
	/**
	 * Gets the settings that were read by this resource.
	 * @return the settings that were read
	 */
	public SettingsBlop getSettings(){
		return settings;
	}
	
	/**
	 * Sets the settings which should be written by this resource.
	 * @param settings the settings to write
	 */
	public void setSettings( SettingsBlop settings ){
		this.settings = settings;
	}
	
	private DockFrontend getFrontend(){
		return control.getControl().intern();
	}
	
    public void write( DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_0_4 );
        getFrontend().writeBlop( settings, out );
    }
    
    public void read( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        if( Version.VERSION_1_1_1.compareTo( version ) > 0 ){
        	throw new IOException( "The perspective API cannot read files which were written before version 1.1.1" );
        }
        version.checkCurrent();
        settings = getFrontend().readBlop( in );
    }
    
    public void writeXML( XElement element ) {
        getFrontend().writeBlopXML( settings, element.addElement( "frontend" ) );
    }
    
    public void readXML( XElement element ) {
        settings = getFrontend().readBlopXML( element.getElement( "frontend" ) );
    }    
}
