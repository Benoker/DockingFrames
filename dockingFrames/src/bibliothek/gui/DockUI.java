/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.Icon;

import bibliothek.data.ResourceManager;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.DefaultCombiner;
import bibliothek.gui.dock.station.support.DefaultDisplayerFactory;
import bibliothek.gui.dock.station.support.DefaultStationPaint;

/**
 * A list of icons, text and methods used by the framework.<br>
 * The icons are read through the default {@link ResourceManager}. Clients
 * can change some icons by calling {@link ResourceManager#putValue(String, Object)}.
 * Icons which are already read will not be affected by such a call. 
 * @author Benjamin Sigg
 */
public class DockUI {
    /** An instance of DockUI */
	private static DockUI ui = new DockUI();
	
	/** The resource bundle for some text shown in this framework */
	private ResourceBundle bundle;
	
    /**
     * Gets the default instance of DockUI.
     * @return the instance
     */
	public static DockUI getDefaultDockUI(){
		return ui;
	}
	
    /**
     * Creates a new DockUI
     */
    protected DockUI(){
        Map<String, String> map = loadKeyPathMapping();
        for( Map.Entry<String, String> entry : map.entrySet() ){
            if( !ResourceManager.getDefault().exists( entry.getKey() ))
                ResourceManager.getDefault().icon( entry.getKey(), entry.getValue(), getClass().getClassLoader() );
        }
        setBundle( ResourceBundle.getBundle( "data.locale.text", Locale.getDefault(), this.getClass().getClassLoader() ));
    }
    
    /**
     * Gets the local resource bundle.
     * @return the bundle
     */
    public ResourceBundle getBundle(){
		return bundle;
	}
    
    /**
     * Gets a string of the current {@link #getBundle() bundle}.
     * @param key the key of the string
     * @return the string
     */
    public String getString( String key ){
    	return getBundle().getString( key );
    }
    
    /**
     * Sets the resource bundle which should be used.
     * @param bundle the bundle
     */
    public void setBundle( ResourceBundle bundle ){
		this.bundle = bundle;
	}
    
    /**
     * Gets the icon stored under <code>key</code>. The keys are stored in
     * a file "icons.ini" in the directory "data".
     * @param key the key for the icon
     * @return the icon or <code>null</code>
     */
    public Icon getIcon( String key ){
        return ResourceManager.getDefault().icon( key );
    }
    
    /**
     * Gets a map containing keys and path for icon.
     * @return the icons
     */
    @SuppressWarnings("unchecked")
    protected Map<String, String> loadKeyPathMapping(){
        Properties properties = ResourceManager.getDefault().ini( "DockUI.mapping", "data/icons.ini", getClass().getClassLoader() ).get();
        Map<String, String> result = new HashMap<String, String>();
        Enumeration e = properties.keys();
        while( e.hasMoreElements() ){
            String key = (String)e.nextElement();
            result.put( key, properties.getProperty( key ));
        }
        return result;
    }
    
    /**
     * Gets a {@link StationPaint} for <code>station</code>.
     * @param paint a default value, may be <code>null</code>
     * @param station the station for which a paint is searched
     * @return <code>paint</code> or another StationPaint, not <code>null</code>
     */
    public static StationPaint getPaint( StationPaint paint, DockStation station ){
        if( paint != null )
            return paint;
        
        DockTheme theme = station.getTheme();
        if( theme == null )
        	return new DefaultStationPaint();
        
        return theme.getPaint(station);
    }
    
    /**
     * Gets a {@link DisplayerFactory} for <code>station</code>.
     * @param factory a default value, may be <code>null</code>
     * @param station the station for which a factory is searched
     * @return <code>factory</code> or another DisplayerFactory, not <code>null</code>
     */
    public static DisplayerFactory getDisplayerFactory( DisplayerFactory factory, DockStation station ){
    	if( factory != null )
    		return factory;
    	
    	DockTheme theme = station.getTheme();
        if( theme == null )
        	return new DefaultDisplayerFactory();
        
        return theme.getDisplayFactory(station);
    }
    
    /**
     * Gets a {@link Combiner} for <code>station</code>.
     * @param combiner a default value, may be <code>null</code>
     * @param station the station for which a combiner is searched
     * @return <code>combiner</code> or another Combiner, not <code>null</code>
     */
    public static Combiner getCombiner( Combiner combiner, DockStation station ){
        if( combiner != null )
            return combiner;
        
        DockTheme theme = station.getTheme();
        if( theme == null )
        	return new DefaultCombiner();
        
        return theme.getCombiner(station);
    }
    
    /**
     * Removes all children of <code>station</code> and then adds
     * the children again. Reading the children ensures that all components are
     * build up again with the current theme of the station
     * @param <D> the type of the station
     * @param station the station to update
     * @param factory a factory used to remove and to add the elements
     * @throws IOException if the factory throws an exception
     */
    public static <D extends DockStation> void updateTheme( D station, DockFactory<? super D> factory ) throws IOException{
    	Map<Integer, Dockable> children = new HashMap<Integer, Dockable>();
    	Map<Dockable, Integer> ids = new HashMap<Dockable, Integer>();
    	
    	for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
    		Dockable child = station.getDockable(i);
    		children.put(i, child);
    		ids.put(child, i);
    	}
    	
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
    	DataOutputStream out = new DataOutputStream( bout );
    	factory.write( station, ids, out );
    	out.close();

    	for( int i = station.getDockableCount()-1; i >= 0; i-- ){
    		station.drag( station.getDockable( i ));
    	}
    	
    	ByteArrayInputStream bin = new ByteArrayInputStream( bout.toByteArray() );
    	DataInputStream in = new DataInputStream( bin );
    	factory.read( children, false, station, in );
    	in.close();
    }
}
