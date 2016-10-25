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
package bibliothek.gui.dock.common.perspective;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockFrontend;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CSetting;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.CommonMultipleDockableFactory;
import bibliothek.gui.dock.common.intern.CommonSingleDockableFactory;
import bibliothek.gui.dock.common.intern.CommonSingleDockableLayout;
import bibliothek.gui.dock.common.intern.RootStationAdjacentFactory;
import bibliothek.gui.dock.common.intern.station.CommonDockStationFactory;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationSettingConverter;
import bibliothek.gui.dock.frontend.DockFrontendPerspective;
import bibliothek.gui.dock.frontend.FrontendPerspectiveCache;
import bibliothek.gui.dock.frontend.RegisteringDockFactory;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.layout.DockLayout;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.perspective.PredefinedPerspective;
import bibliothek.gui.dock.support.mode.ModeSettings;
import bibliothek.gui.dock.support.mode.ModeSettingsConverter;
import bibliothek.util.ClientOnly;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A {@link CControlPerspective} is a wrapper around a {@link CControl} allowing
 * access to various {@link CPerspective}s.
 * @author Benjamin Sigg
 */
@ClientOnly
public class CControlPerspective {
	private CControlAccess control;
	
	/**
	 * Creates a new wrapper
	 * @param control the control whose perspectives are modified
	 */
	public CControlPerspective( CControlAccess control ){
		if( control == null ){
			throw new IllegalArgumentException( "control must not be null" );
		}
		
		this.control = control;
	}
	
	/**
	 * Gets the {@link CControl} in whose realm this {@link CControlPerspective} operates.
	 * @return the owner of this perspective
	 */
	public CControl getControl(){
		return control.getOwner();
	}
    
    /**
     * Creates a new {@link CPerspective} that is set up with all the stations of the {@link CControl}. 
     * There are no {@link Dockable}s stored in the new perspective.
     * @return the new perspective
     */
    public CPerspective createEmptyPerspective(){
    	CPerspective perspective = new CPerspective( control );
    	for( CStation<?> station : control.getOwner().getStations() ){
    		perspective.addStation( station.createPerspective() );
    	}
    	return perspective;
    }
    
    /**
     * Gets a perspective that matches the current layout of the application.
     * @param includeWorkingAreas whether {@link Dockable}s that are managed by a working-area should be
     * included in the layout or not
     * @return the current perspective
     */
    public CPerspective getPerspective( boolean includeWorkingAreas ){
    	Setting setting = control.getOwner().intern().getSetting( !includeWorkingAreas );
    	return convert( (CSetting)setting, includeWorkingAreas );
    }

    /**
     * Gets the names of all the perspectives that are available.
     * @return all the names
     */
    public String[] getNames(){
    	return control.getOwner().layouts();
    }
    
    /**
     * Gets the perspective which represents a layout that was stored using {@link CControl#save(String)}.
     * @param name the name of the stored layout
     * @return the perspective or <code>null</code> if <code>name</code> was not found
     */
    public CPerspective getPerspective( String name ){
    	return getPerspective( name, false );
    }

    /**
     * Gets the perspective which represents a layout that was stored using {@link CControl#save(String)}.
     * @param name the name of the stored layout
     * @param includeWorkingAreas whether the content of working areas should be included (requires that
     * the layout was saved in the first place)
     * @return the perspective or <code>null</code> if <code>name</code> was not found
     */
    public CPerspective getPerspective( String name, boolean includeWorkingAreas ){
    	Setting setting = control.getOwner().intern().getSetting( name );
    	if( setting == null ){
    		return null;
    	}
    	return convert( (CSetting)setting, includeWorkingAreas );
    }
    
    /**
     * Changes the layout of the associated {@link CControl} such that it matches <code>perspective</code>. 
     * @param perspective the perspective to apply, not <code>null</code>
     * @param includeWorkingAreas whether {@link Dockable}s that are managed by a working-area should be
     * included in the layout or not
     */
    public void setPerspective( CPerspective perspective, boolean includeWorkingAreas ){
    	control.getOwner().intern().setSetting( convert( perspective, includeWorkingAreas ), !includeWorkingAreas );
    }
    
    /**
     * Stores <code>perspective</code> as a layout that can be selected by the user by calling
     * {@link CControl#load(String)}.<br>
     * The contents of working areas are ignored by this method.
     * @param name the name of the layout
     * @param perspective the new layout, not <code>null</code>
     */
    public void setPerspective( String name, CPerspective perspective ){
    	setPerspective( name, perspective, false );
    }

    /**
     * Stores <code>perspective</code> as a layout that can be selected by the user by calling
     * {@link CControl#load(String)}.
     * @param name the name of the layout
     * @param perspective the new layout, not <code>null</code>
     * @param includeWorkingAreas whether the contents of working areas should be stored as well
     */
    public void setPerspective( String name, CPerspective perspective, boolean includeWorkingAreas ){
    	control.getOwner().intern().setSetting( name, convert( perspective, includeWorkingAreas ) );
    }
    
    /**
     * Deletes the perspective with name <code>name</code>.
     * @param name the name of the perspective
     */
    public void removePerspective( String name ){
    	control.getOwner().delete( name );
    }
    
    /**
     * Renames the perspective <code>source</code> to <code>destination</code>. If there is already a 
     * layout with name <code>destination</code> it will be overridden. This operation works directly on the
     * {@link CControl}, already existing {@link CPerspective}s will not be affected by invoking this method.
     * @param source the name of the source
     * @param destination the name of the destination
     * @throws IllegalArgumentException if <code>source</code> does not point to an existing layout
     * @throws IllegalArgumentException if either <code>source</code> or <code>destination</code> are <code>null</code>
     */
    public void renamePerspective( String source, String destination ){
    	if( source == null ){
    		throw new IllegalArgumentException( "source is null" );
    	}
    	if( destination == null ){
    		throw new IllegalArgumentException( "destination is null" );
    	}
    	
    	CDockFrontend frontend = control.getOwner().intern();
    	Setting layout = frontend.getSetting( source );
    	if( layout == null ){
    		throw new IllegalArgumentException( "no perspective registered with name '" + source + "'" );
    	}
    	frontend.setSetting( destination, layout );
    	frontend.delete( source );
    	
    	if( source.equals( frontend.getCurrentSetting() )){
    		frontend.setCurrentSettingName( destination );
    	}
    }
    
    /**
     * Writes the contents of <code>perspective</code> into <code>root</code> using the factories provided
     * by this {@link CControlPerspective}.
     * @param root the element to write into, not <code>null</code>
     * @param perspective the perspective to write, not <code>null</code>
     */
    public void writeXML( XElement root, CPerspective perspective ){
    	writeXML( root, perspective, true );
    }
    
    /**
     * Writes the contents of <code>perspective</code> into <code>root</code> using the factories provided
     * by this {@link CControlPerspective}.
     * @param root the element to write into, not <code>null</code>
     * @param perspective the perspective to write, not <code>null</code>
     * @param includeWorkingAreas whether the output contains information about children of {@link CStation#isWorkingArea() working areas} 
     * (<code>includeWorkingAreas = true</code>) or not (<code>includeWorkingAreas = false</code>)
     */
    public void writeXML( XElement root, CPerspective perspective, boolean includeWorkingAreas ){
    	perspective.storeLocations();
    	
    	DockFrontendPerspective frontend = conversion( perspective, includeWorkingAreas );
    	Perspective conversion = frontend.getPerspective();
    	
    	Map<String, DockLayoutComposition> stations = new HashMap<String, DockLayoutComposition>();
    	for( String key : perspective.getStationKeys() ){
    		CStationPerspective station = perspective.getStation( key );
    		if( station.asDockable() == null || station.asDockable().getParent() == null ){
    			stations.put( key, conversion.convert( station.intern() ));
    		}
    	}
    	
    	conversion.getSituation().writeCompositionsXML( stations, root.addElement( "stations" ) );
    	
    	// Store the last location of all known elements
    	XElement xinvisible = root.addElement( "invisible" );
    	PropertyTransformer transformer = frontend.getPropertyTransformer();
    	for( String key : perspective.getDockableKeys() ){
    		CDockablePerspective dockable = perspective.getDockable( key );
    		Location location = getInvisibleLocation( dockable );
    		
    		if( location != null ){
	    		XElement xdockable = xinvisible.addElement( "dockable" );
				xdockable.addString( "key", key );
				
	    		if( dockable.getParent() == null ){
		    		conversion.getSituation().writeCompositionXML( conversion.convert( dockable.intern() ), xdockable.addElement( "content" ) );
	    		}
	    		
	    		XElement xlocation = xdockable.addElement( "location" );
	    		xlocation.addString( "root", location.getRoot() );
	    		xlocation.addString( "mode", dockable.getLocationHistory().getLastMode().getModeIdentifier().toString() );
	    		xlocation.addBoolean( "applicationDefined", location.isApplicationDefined() );
	    		transformer.writeXML( location.getLocation(), xlocation );
    		}
    	}
    	
    	// store more location information
    	ModeSettings<Location, ?> settings = perspective.getLocationManager().writeModes( control );
    	
    	settings.writeXML( root.addElement( "modes" ) );
    }
    
    /**
     * Writes the contents of <code>perspective</code> into <code>out</code> using the factories provided
     * by this {@link CControlPerspective}.
     * @param out the stream to write into, not <code>null</code>
     * @param perspective the perspective to write, not <code>null</code>
     * @throws IOException if <code>out</code> is not writeable
     */
    public void write( DataOutputStream out, CPerspective perspective ) throws IOException{
    	write( out, perspective, true );
    }
    
    /**
     * Writes the contents of <code>perspective</code> into <code>out</code> using the factories provided
     * by this {@link CControlPerspective}.
     * @param out the stream to write into, not <code>null</code>
     * @param perspective the perspective to write, not <code>null</code>
     * @param includeWorkingAreas whether the output contains information about children of {@link CStation#isWorkingArea() working areas} 
     * (<code>includeWorkingAreas = true</code>) or not (<code>includeWorkingAreas = false</code>)
     * @throws IOException if <code>out</code> is not writeable
     */
    public void write( DataOutputStream out, CPerspective perspective, boolean includeWorkingAreas ) throws IOException{
    	perspective.storeLocations();
    	Version.write( out, Version.VERSION_1_1_2 );
    	
    	DockFrontendPerspective frontend = conversion( perspective, includeWorkingAreas );
    	Perspective conversion = frontend.getPerspective();
    	
    	Map<String, DockLayoutComposition> stations = new HashMap<String, DockLayoutComposition>();
    	for( String key : perspective.getStationKeys() ){
    		CStationPerspective station = perspective.getStation( key );
    		stations.put( key, conversion.convert( station.intern() ));
    	}
    	
    	conversion.getSituation().writeCompositions( stations, out );
    	
    	// Store the last location of all known elements
    	String[] keys = perspective.getDockableKeys();
    	out.writeInt( keys.length );
    	PropertyTransformer transformer = frontend.getPropertyTransformer();
    	
    	for( String key : keys ){
    		CDockablePerspective dockable = perspective.getDockable( key );
    		Location location = getInvisibleLocation( dockable );
    		
    		if( location != null ){
    			out.writeBoolean( true );
    			out.writeUTF( key );
    			
	    		if( dockable.getParent() == null ){
	    			out.writeBoolean( true );
	    			conversion.getSituation().writeComposition( conversion.convert( dockable.intern() ), out );
	    		}
	    		else{
	    			out.writeBoolean( false );
	    		}
	    		
	    		out.writeUTF( location.getRoot() );
	    		out.writeUTF( dockable.getLocationHistory().getLastMode().getModeIdentifier().toString() );
	    		out.writeBoolean( location.isApplicationDefined() );
	    		transformer.write( location.getLocation(), out );
    		}
    		else{
    			out.writeBoolean( false );
    		}
    	}
    	
    	
    	// write more location information
    	ModeSettings<Location, ?> settings = perspective.getLocationManager().writeModes( control );
    	
    	settings.write( out );
    }
    
    /**
     * Converts <code>perspective</code> into a {@link CSetting}.
     * @param perspective the perspective to convert
     * @param includeWorkingAreas whether the children of {@link CWorkingArea}s should be stored as well
     * @return the converted perspective
     */
    public CSetting write( CPerspective perspective, boolean includeWorkingAreas ){
    	return convert( perspective, includeWorkingAreas );
    }
    
    /**
     * Emulates a call to {@link CControl#readXML(XElement)} and returns all the layouts that are stored
     * within <code>root</code>.
     * @param root the root xml element of a file
     * @return all the layouts and settings stored in <code>root</code>
     * @throws XException if <code>root</code> is not well formed
     */
    public CControlPerspectiveBlop readAllXML( XElement root ) throws XException{
    	CControlPerspectiveBlop blop = new CControlPerspectiveBlop( this );
    	blop.readXML( root );
    	return blop;
    }
    
    /**
     * Creates a new {@link CPerspective} using the information stored in <code>root</code>. While this method
     * uses the factories provided by this {@link CControlPerspective}, the new {@link CPerspective} is not registered
     * anywhere. It is the clients responsibility to call {@link #setPerspective(String, CPerspective)} or
     * {@link #setPerspective(CPerspective, boolean)} to actually use the result of this method.
     * @param root the element which contains information about a perspective
     * @return the new perspective
     * @throws XException if the structure of <code>root</code> is not as expected
     */
    public CPerspective readXML( XElement root ) throws XException{
    	return readXML( root, true );
    }
    
    /**
     * Creates a new {@link CPerspective} using the information stored in <code>root</code>. While this method
     * uses the factories provided by this {@link CControlPerspective}, the new {@link CPerspective} is not registered
     * anywhere. It is the clients responsibility to call {@link #setPerspective(String, CPerspective)} or
     * {@link #setPerspective(CPerspective, boolean)} to actually use the result of this method.
     * @param root the element which contains information about a perspective
     * @return the new perspective
     * @param includeWorkingAreas whether the perspective contains information about children of {@link CStation#isWorkingArea() working areas} 
     * (<code>includeWorkingAreas = true</code>) or not (<code>includeWorkingAreas = false</code>). This parameter should have the same value as was used
     * when calling {@link #write(DataOutputStream, CPerspective, boolean)}. 
     * @throws XException if the structure of <code>root</code> is not as expected
     */
    public CPerspective readXML( XElement root, boolean includeWorkingAreas ) throws XException{
    	CPerspective perspective = createEmptyPerspective();
    	
    	PerspectiveElementFactory factory = new PerspectiveElementFactory( perspective );
    	DockFrontendPerspective frontend = wrap( perspective, includeWorkingAreas, factory );
    	Perspective conversion = frontend.getPerspective();
    	
    	for( Map.Entry<String, MultipleCDockableFactory<?, ?>> item : control.getRegister().getFactories().entrySet() ){
    		conversion.getSituation().add( new CommonMultipleDockableFactory( item.getKey(), item.getValue(), control, perspective ) );
    	}
    	
    	XElement xstations = root.getElement( "stations" );
    	if( xstations == null ){
    		throw new XException( "missing element 'stations'" );
    	}
    	
    	Map<String, DockLayoutComposition> stations = conversion.getSituation().readCompositionsXML( xstations );
    	factory.setStations( stations );
    	
    	for( DockLayoutComposition composition : stations.values() ){
    		PerspectiveElement station = conversion.convert( composition );
    		if( station instanceof CommonElementPerspective ){
    			CStationPerspective stationPerspective = ((CommonElementPerspective)station).getElement().asStation();
    			if( stationPerspective != null ){
    				perspective.addStation( stationPerspective );
    			}
    		}
    	}
    	
    	perspective.storeLocations();
    	
    	// read the last known location of all elements
    	XElement xinvisible = root.getElement( "invisible" );
    	if( xinvisible != null ){
    		PropertyTransformer transformer = frontend.getPropertyTransformer();
    		for( XElement xdockable : xinvisible.getElements( "dockable" )){
    			String key = xdockable.getString( "key" );
    			CDockablePerspective dockable = perspective.getDockable( key );
    			if( dockable == null ){
    				XElement xcontent = xdockable.getElement( "content" );
    				if( xcontent != null ){
    					PerspectiveElement element = conversion.convert( conversion.getSituation().readCompositionXML( xcontent ) );
    	    			if( element instanceof CommonElementPerspective ){
    	    				dockable = ((CommonElementPerspective)element).getElement().asDockable();
    	    				if( dockable != null ){
    	    					perspective.putDockable( dockable );
    	    				}
    	    			}		
    				}
    			}
    			if( dockable != null ){
    				XElement xlocation = xdockable.getElement( "location" );
    				String locationRoot = xlocation.getString( "root" );
    				DockableProperty location = transformer.readXML( xlocation );
    				Path mode = new Path( xlocation.getString( "mode" ));
    				
    				boolean applicationDefined = false;
    				if( xlocation.attributeExists( "applicationDefined" )){
    					applicationDefined = xlocation.getBoolean( "applicationDefined" );
    				}
    				
    				ExtendedMode extendedMode = perspective.getLocationManager().getMode( mode );
    				if( extendedMode != null ){
    					dockable.getLocationHistory().add( extendedMode, new Location( mode, locationRoot, location, applicationDefined ) );
    				}
    			}
    		}
    	}
    	
    	
    	XElement xmodes = root.getElement( "modes" );
    	if( xmodes == null ){
    		throw new XException( "missing element 'modes'" );
    	}
    	
    	ModeSettingsConverter<Location, Location> converter = new LocationSettingConverter( control.getOwner().getController() );
    	ModeSettings<Location, Location> modes = control.getOwner().getLocationManager().createModeSettings( converter );
    	modes.readXML( xmodes );
    	
    	perspective.getLocationManager().readModes( modes, perspective, control );
    	
    	return perspective;
    }
    
    /**
     * Emulates a call to {@link CControl#read(DataInputStream)} and returns all the layouts that are stored
     * in the stream <code>in</code>.
     * @param in the bytes of some layout file
     * @return all the layouts and settings that can be read from <code>in</code>
     * @throws IOException if there is a problem reading <code>in</code> or if <code>in</code> is not well formed
     */
    public CControlPerspectiveBlop readAll( DataInputStream in ) throws IOException{
    	CControlPerspectiveBlop blop = new CControlPerspectiveBlop( this );
    	blop.read( in );
    	return blop;
    }
    
    /**
     * Creates a new {@link CPerspective} using the information stored in <code>in</code>. While this method
     * uses the factories provided by this {@link CControlPerspective}, the new {@link CPerspective} is not registered
     * anywhere. It is the clients responsibility to call {@link #setPerspective(String, CPerspective)} or
     * {@link #setPerspective(CPerspective, boolean)} to actually use the result of this method.
     * @param in the stream to read data from
     * @return the new perspective
     * @throws IOException if <code>in</code> is not readable or in the wrong format
     */
    public CPerspective read( DataInputStream in ) throws IOException{
    	return read( in, true );
    }
    
    /**
     * Creates a new {@link CPerspective} using the information stored in <code>in</code>. While this method
     * uses the factories provided by this {@link CControlPerspective}, the new {@link CPerspective} is not registered
     * anywhere. It is the clients responsibility to call {@link #setPerspective(String, CPerspective)} or
     * {@link #setPerspective(CPerspective, boolean)} to actually use the result of this method.
     * @param in the stream to read data from
     * @param includeWorkingAreas whether the perspective contains information about children of {@link CStation#isWorkingArea() working areas} 
     * (<code>includeWorkingAreas = true</code>) or not (<code>includeWorkingAreas = false</code>). This parameter should have the same value as was used
     * when calling {@link #write(DataOutputStream, CPerspective, boolean)}. 
     * @return the new perspective
     * @throws IOException if <code>in</code> is not readable or in the wrong format
     */
    public CPerspective read( DataInputStream in, boolean includeWorkingAreas ) throws IOException{
    	Version version = Version.read( in );
    	
    	boolean version111 = version.equals( Version.VERSION_1_1_1 );
    	boolean version111a = version.equals( Version.VERSION_1_1_1a );
    	boolean version112 = version.equals( Version.VERSION_1_1_2 );
    	
    	if( !version111 && !version111a && !version112 ){
    		throw new IOException( "unknown version: " + version );
    	}
    	
    	CPerspective perspective = createEmptyPerspective();
    	
    	PerspectiveElementFactory factory = new PerspectiveElementFactory( perspective );
    	DockFrontendPerspective frontend = wrap( perspective, includeWorkingAreas, factory );
    	Perspective conversion = frontend.getPerspective();
    	
    	for( Map.Entry<String, MultipleCDockableFactory<?, ?>> item : control.getRegister().getFactories().entrySet() ){
    		conversion.getSituation().add( new CommonMultipleDockableFactory( item.getKey(), item.getValue(), control, perspective ) );
    	}
    	
    	Map<String, DockLayoutComposition> stations = conversion.getSituation().readCompositions( in );
    	factory.setStations( stations );
    	
    	for( DockLayoutComposition composition : stations.values() ){
    		PerspectiveElement station = conversion.convert( composition );
    		if( station instanceof CommonElementPerspective ){
    			CStationPerspective stationPerspective = ((CommonElementPerspective)station).getElement().asStation();
    			if( stationPerspective != null ){
    				perspective.addStation( stationPerspective );
    			}
    		}
    	}
    	
    	if( version111a || version112 ){
    		perspective.storeLocations();
    		PropertyTransformer transformer = frontend.getPropertyTransformer();
    		for( int i = 0, n = in.readInt(); i<n; i++ ){
    			if( in.readBoolean() ){
    				String key = in.readUTF();
    				DockLayoutComposition composition = null;
    				if( in.readBoolean() ){
    					 composition = conversion.getSituation().readComposition( in );
    				}
    				
	    			CDockablePerspective dockable = perspective.getDockable( key );
	    			if( dockable == null && composition != null ){
	    				PerspectiveElement element = conversion.convert( composition );
	    	    		if( element instanceof CommonElementPerspective ){
	    	    			dockable = ((CommonElementPerspective)element).getElement().asDockable();
	    	    			if( dockable != null ){
	    	    				perspective.putDockable( dockable );
	    	    			}
	    	    		}		
	    			}
	    			
	    			String locationRoot = in.readUTF();
	    			String modeId = in.readUTF();
	    			
	    			boolean applicationDefined = false;
	    			if( version112 ){
	    				applicationDefined = in.readBoolean();
	    			}
	    			
	    			DockableProperty location = transformer.read( in );
	    			
	    			if( dockable != null ){
	    				Path mode = new Path( modeId );
	    				
	    				ExtendedMode extendedMode = perspective.getLocationManager().getMode( mode );
	    				if( extendedMode != null ){
	    					dockable.getLocationHistory().add( extendedMode, new Location( mode, locationRoot, location, applicationDefined ) );
	    				}
	    			}
	    		}
    		}
    	}
    	
    	ModeSettingsConverter<Location, Location> converter = new LocationSettingConverter( control.getOwner().getController() );
    	ModeSettings<Location, Location> modes = control.getOwner().getLocationManager().createModeSettings( converter );
    	modes.read( in );
    	
    	perspective.getLocationManager().readModes( modes, perspective, control );
    	
    	return perspective;
    }
    
    /**
     * Creates a new {@link CPerspective} and fills it using the information from <code>setting</code>.
     * @param setting the layout to convert
     * @param includeWorkingAreas whether the layout contains information about children of {@link CWorkingArea}s 
     * @return the layout of <code>setting</code>
     */
    public CPerspective read( CSetting setting, boolean includeWorkingAreas ){
    	return convert( setting, includeWorkingAreas );
    }
    
    private CSetting convert( CPerspective perspective, boolean includeWorkingAreas ){
    	perspective.storeLocations();
    	
    	DockFrontendPerspective frontend = conversion( perspective, includeWorkingAreas );
    	Perspective conversion = frontend.getPerspective();
    	CSetting setting = new CSetting();
    	
    	// layout
    	for( String key : perspective.getStationKeys() ){
    		CStationPerspective station = perspective.getStation( key );
    		if( station.asDockable() == null || station.asDockable().getParent() == null ){
    			setting.putRoot( key, conversion.convert( station.intern() ) );
    		}
    	}
    	
    	// invisible items (storing location of visible items as well)
    	for( String key : perspective.getDockableKeys() ){
    		CDockablePerspective dockable = perspective.getDockable( key );
    		Location location = getInvisibleLocation( dockable );
	    	if( location != null ){
	    		setting.addInvisible( key, location.getRoot(), null, location.getLocation() );
	    	}
    	}
    	
    	ModeSettings<Location, Location> settings = perspective.getLocationManager().writeModes( control );
    	setting.setModes( settings );
    	return setting;
    }
    
    private CPerspective convert( CSetting setting, boolean includeWorkingAreas ){
    	CPerspective cperspective = createEmptyPerspective();
    	
    	PerspectiveElementFactory factory = new PerspectiveElementFactory( cperspective );
    	DockFrontendPerspective frontend = wrap( cperspective, includeWorkingAreas, factory );
    	Perspective conversion = frontend.getPerspective();
    	
    	for( Map.Entry<String, MultipleCDockableFactory<?, ?>> item : control.getRegister().getFactories().entrySet() ){
    		conversion.getSituation().add( new CommonMultipleDockableFactory( item.getKey(), item.getValue(), control, cperspective ) );
    	}
    	
    	// registered dockables
    	Map<String, DockLayoutComposition> stations = new HashMap<String, DockLayoutComposition>();
    	for( String root : setting.getRootKeys() ){
    		stations.put( root, setting.getRoot( root ) );
    	}
    	
    	factory.setStations( stations );
    	
    	for( DockLayoutComposition composition : stations.values() ){
    		PerspectiveElement station = conversion.convert( composition );
    		if( station instanceof CommonElementPerspective ){
    			CStationPerspective stationPerspective = ((CommonElementPerspective)station).getElement().asStation();
    			if( stationPerspective != null ){
    				cperspective.addStation( stationPerspective );
    			}
    		}
    	}
    	
    	// invisible dockables
    	for( int i = 0, n = setting.getInvisibleCount(); i < n; i++ ){
    		DockLayoutComposition composition = setting.getInvisibleLayout( i );
    		if( composition != null ){
    			PerspectiveElement element = conversion.convert( composition );
    			if( element instanceof CommonElementPerspective ){
    				CDockablePerspective dockable = ((CommonElementPerspective)element).getElement().asDockable();
    				if( dockable != null ){
	    				DockableProperty location = setting.getInvisibleLocation( i );
			    		String root = setting.getInvisibleRoot( i );
			    		
			    		ExtendedMode mode = cperspective.getLocationManager().getMode( root, location );
			    		if( mode != null ){
			    			dockable.getLocationHistory().add( mode, new Location( mode.getModeIdentifier(), root, location, false ) );
			    		}
			    		
			    		cperspective.putDockable( dockable );
    				}
    			}
    		}
    	}
    	
    	// location information 
    	ModeSettings<Location, Location> modes = setting.getModes();
    	
    	cperspective.getLocationManager().readModes( modes, cperspective, control );
    	
    	return cperspective;    	
    }
    
    private Location getInvisibleLocation( CDockablePerspective dockable ){
		LocationHistory history = dockable.getLocationHistory();
		List<Path> order = history.getOrder();
		if( !order.isEmpty() ){
			Path mode = order.get( order.size()-1 );
			Location location = history.getLocations().get( mode );
			return location;
		}
		return null;
    }

    /**
     * Creates a new {@link DockFrontendPerspective} which uses the settings from <code>perspective</code> to read
     * and write layouts. This method adds {@link CommonSingleDockableFactory}, {@link CommonMultipleDockableFactory} and
     * {@link CommonDockStationFactory} to the perspective.<br>
     * Clients usually have no need to call this method.
     * @param perspective the perspective whose settings should be used for reading or writing a layout
     * @param includeWorkingAreas whether the contents of {@link CStation#isWorkingArea() working areas}
     * should be included in the layout or not
     * @return the new builder
     */
    @FrameworkOnly
    public DockFrontendPerspective conversion( CPerspective perspective, boolean includeWorkingAreas ){
    	DockFrontendPerspective conversion = wrap( perspective, includeWorkingAreas );
    	DockSituation situation = conversion.getPerspective().getSituation();
    	
    	for( Map.Entry<String, MultipleCDockableFactory<?, ?>> item : control.getRegister().getFactories().entrySet() ){
    		situation.add( new CommonMultipleDockableFactory( item.getKey(), item.getValue(), control, perspective ) );
    	}
    	
    	return conversion;
    }
    
    private DockFrontendPerspective wrap( CPerspective perspective, boolean includeWorkingAreas ){
    	PerspectiveElementFactory factory = new PerspectiveElementFactory( perspective );
    	return wrap( perspective, includeWorkingAreas, factory );
    }
    
    private DockFrontendPerspective wrap( CPerspective perspective, boolean includeWorkingAreas, PerspectiveElementFactory factory ){
    	DockFrontendPerspective frontend = control.getOwner().intern().getPerspective( !includeWorkingAreas, factory );
    	PredefinedPerspective inner = frontend.getPerspective();
    	
    	factory.setBasePerspective( inner );
    	
    	CommonSingleDockableFactory singleDockableFactory = new CommonSingleDockableFactory( control.getOwner(), perspective );

    	inner.getSituation().add( singleDockableFactory );
    	inner.getSituation().addBackup( new RegisteringDockFactory<CommonDockable, CommonElementPerspective, CommonSingleDockableLayout>( control.getOwner().intern(), singleDockableFactory ) );
    	inner.getSituation().add( new CommonDockStationFactory( control.getOwner(), factory, singleDockableFactory ) );
    	
    	return frontend;
    }
    
    /**
     * Helper class for converting {@link DockElement}s to {@link PerspectiveElement}s.
     * @author Benjamin Sigg
     */
    private class PerspectiveElementFactory implements FrontendPerspectiveCache{
    	private CPerspective perspective;
    	private Perspective basePerspective;
    	private Map<String, SingleCDockablePerspective> dockables = new HashMap<String, SingleCDockablePerspective>();
    	private Map<String, DockLayoutComposition> stations;
    	
    	/**
    	 * Creates a new factory.
    	 * @param perspective the perspective for which items are required
    	 */
    	public PerspectiveElementFactory( CPerspective perspective ){
    		this.perspective = perspective;
    		Iterator<PerspectiveElement> elements = perspective.elements();
    		while( elements.hasNext() ){
    			PerspectiveElement element = elements.next();
    			if( element instanceof SingleCDockablePerspective ){
    				SingleCDockablePerspective dockable = (SingleCDockablePerspective) element;
    				dockables.put( dockable.getUniqueId(), dockable );
    			}
    		}
    	}
    	
    	public void setStations( Map<String, DockLayoutComposition> stations ){
			this.stations = stations;
		}
    	
    	/**
    	 * Sets the {@link Perspective} which is using this cache.
    	 * @param basePerspective the perspective using this cache, not <code>null</code>
    	 */
    	public void setBasePerspective( Perspective basePerspective ){
			this.basePerspective = basePerspective;
		}
    	
		public PerspectiveElement get( String id, DockElement element, boolean isRootStation ){
			if( isRootStation ){
				return perspective.getStation( id ).intern();
			}
			else if( element instanceof CommonDockable ){
				CDockable dockable = ((CommonDockable)element).getDockable();
				if( dockable.asStation() != null ){
					CStationPerspective station = perspective.getStation( dockable.asStation().getUniqueId() );
					if( station == null ){
						throw new IllegalArgumentException( "Found a non-root CStation that is not registered: " + dockable.asStation().getUniqueId() );
					}
					return station.intern();
				}
				
				if( dockable instanceof SingleCDockable ){
					String key = ((SingleCDockable)dockable).getUniqueId();
					SingleCDockablePerspective result = dockables.get( key );
					if( result == null ){
						result = new SingleCDockablePerspective( key );
						dockables.put( key, result );
					}
					return result.intern();
				}
				
				if( dockable instanceof MultipleCDockable ){
					return null;
				}
			}
			
			throw new IllegalArgumentException( "The intern DockFrontend of the CControl has elements registered that are not SingleCDockables: " + id + "=" + element );
		}
		
		@SuppressWarnings("unchecked")
		public PerspectiveElement get( String id, boolean rootStation ){
			String key = id;
			if( !rootStation && control.getRegister().isSingleId( id )){
				key = control.getRegister().singleToNormalId( id );
			}
			
			// maybe a station
			DockLayoutComposition root = null;
			if( stations != null ){
				root = stations.get( key );
			}
			if( root == null ){
				root = getPredefinedStation( key, basePerspective.getSituation() );
			}
			Path stationType = null;
			
			if( root != null ){
				// really a station
				DockLayout<Path> layout = (DockLayout<Path>)root.getAdjacent( RootStationAdjacentFactory.FACTORY_ID );
				if( layout != null){
					stationType = layout.getData();
				}
			
				CStationPerspective station = perspective.getStation( key );
				if( station == null ){
					station = control.getOwner().getMissingPerspectiveStrategy().createStation( key, stationType );
					if( station != null ){
						perspective.addStation( station );
						station.setRoot( rootStation );
					}
				}
				if( station == null ){
					return null;
				}
				return station.intern();
			}
			else if( control.getRegister().isSingleId( id )){
				// maybe a dockable
				SingleCDockablePerspective result = dockables.get( key );
				if( result == null ){
					result = new SingleCDockablePerspective( key );
					dockables.put( key, result );
				}
				return result.intern();
			}
			return null;
		}

	    /**
	     * Tries to find the layout of a {@link DockStation} which was predefined with the unique
	     * identifier <code>id</code>. This method recursively searches through the entire tree of elements and
	     * also finds stations that are registered as {@link SingleCDockable} or {@link MultipleCDockable}.
	     * @param id the identifier to search
	     * @param situation algorithms used to extract information from {@link DockLayoutComposition}s
	     * @return the layout or <code>null</code> if not found
	     */
	    protected DockLayoutComposition getPredefinedStation( String id, DockSituation situation ){
	    	if( stations != null ){
	    		for( DockLayoutComposition station : stations.values() ){
	    			DockLayoutComposition result = getPredefinedStation( id, station, situation );
	    			if( result != null ){
		    			return result;
		    		}
	    		}
	    	}
	    	
	    	return null;
	    }
	    
	    private DockLayoutComposition getPredefinedStation( String id, DockLayoutComposition current, DockSituation situation ){
	    	// check self
	    	String currentId = situation.getIdentifier( current );
	    	if( currentId != null ){
	    		if( id.length() == DockFrontend.ROOT_KEY_PREFIX.length()+id.length() && currentId.startsWith( DockFrontend.ROOT_KEY_PREFIX ) && currentId.endsWith( id )){
	    			return current;
	    		}
	    		if( currentId.startsWith( DockFrontend.DOCKABLE_KEY_PREFIX )){
	    			currentId = currentId.substring( DockFrontend.DOCKABLE_KEY_PREFIX.length() );
	    			if( control.getRegister().isSingleId( currentId )){
	    				currentId = control.getRegister().singleToNormalId( currentId );
	    			}
	    			else if( control.getRegister().isMultiId( currentId )){
	    				currentId = control.getRegister().multiToNormalId( currentId );
	    			}
	    			if( currentId.equals( id )){
	    				return current;
	    			}
	    		}
	    	}
	    	
	    	// check children
	    	List<DockLayoutComposition> children = current.getChildren();
	    	if( children != null ){
	    		for( DockLayoutComposition child : children ){
	    			DockLayoutComposition result = getPredefinedStation( id, child, situation );
	    			if( result != null ){
	    				return result;
	    			}
	    		}
	    	}
	    	return null;
	    }
		
		public String get( PerspectiveElement element ){
			for( String key : perspective.getStationKeys() ){
				CStationPerspective station = perspective.getStation( key );
				if( station.intern() == element ){
					return key;
				}
			}
			
			if( element instanceof CommonElementPerspective ){
				CElementPerspective celement = ((CommonElementPerspective)element).getElement();
				if( celement instanceof SingleCDockablePerspective ){
					return control.getRegister().toSingleId( ((SingleCDockablePerspective)celement).getUniqueId() );
				}
			}
			
			return null;
		}
		
		public boolean isRootStation( PerspectiveStation element ){
			for( String key : perspective.getStationKeys() ){
				CStationPerspective station = perspective.getStation( key );
				if( station.intern() == element ){
					return true;
				}
			}
			return false;
		}
    }
}
