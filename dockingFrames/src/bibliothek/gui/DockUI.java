/**
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

package bibliothek.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.*;
import bibliothek.gui.dock.themes.basic.BasicCombiner;
import bibliothek.gui.dock.themes.basic.BasicDisplayerFactory;
import bibliothek.gui.dock.themes.basic.BasicStationPaint;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.Priority;

/**
 * A list of icons, text and methods used by the framework. 
 * @author Benjamin Sigg
 */
public class DockUI {
    /** An instance of DockUI */
	private static DockUI ui = new DockUI();
	
	/** The resource bundle for some text shown in this framework */
	private ResourceBundle bundle;
	
    /** The icons used in this framework */
    private Map<String, Icon> icons;
    
    /** A list of all available themes */
    private List<ThemeFactory> themes = new ArrayList<ThemeFactory>();
    
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
        ClassLoader loader = DockUI.class.getClassLoader();
        icons = new HashMap<String, Icon>();
        for( Map.Entry<String, String> entry : map.entrySet() ){
            try{
                ImageIcon icon = new ImageIcon( ImageIO.read( loader.getResource( entry.getValue()) ));
                icons.put( entry.getKey(), icon );
            }
            catch( IOException ex ){
                ex.printStackTrace();
            }
        }
        
        // special icons
        icons.put( "ButtonPanel.overflow.menu", new Icon(){
			public int getIconHeight(){
				return 7;
			}

			public int getIconWidth(){
				return 9;
			}

			public void paintIcon( Component c, Graphics g, int x, int y ){
				g = g.create();
				((Graphics2D)g).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g.setColor( c.getForeground() );
				
				g.fillPolygon( 
						new int[]{ x + 1, x + 8, x + 4 },
						new int[]{ y + 1, y + 1, y + 6 }, 3 );
				g.dispose();
			}
        });
        
        setBundle( Locale.getDefault() );
        
        registerThemes();
    }
    
    private void registerThemes(){
        registerTheme( BasicTheme.class, null );
        registerTheme( FlatTheme.class, null );
        registerTheme( SmoothTheme.class, null );
        registerTheme( BubbleTheme.class, null );
        registerTheme( EclipseTheme.class, null );
        registerTheme( NoStackTheme.getFactory( BasicTheme.class, null, this ));
        registerTheme( NoStackTheme.getFactory( FlatTheme.class, null, this ));
        registerTheme( NoStackTheme.getFactory( SmoothTheme.class, null, this ));
        registerTheme( NoStackTheme.getFactory( BubbleTheme.class, null, this ));
    }
    
    /**
     * Gets the default-theme to be used by all {@link DockController}s when
     * nothing else is specified.
     * @return the default-theme
     */
    public ThemeFactory getDefaultTheme(){
        return themes.get( 0 );
    }
    
    /**
     * Gets the list of all available themes.
     * @return the themes
     */
    public ThemeFactory[] getThemes(){
        return themes.toArray( new ThemeFactory[ themes.size() ] );
    }
    
    /**
     * Registers a factory for <code>theme</code>.
     * @param <T> the type of the {@link DockTheme}.
     * @param theme A class which must have the annotation 
     * {@link ThemeProperties}
     * @param bundle The {@link ResourceBundle} that should be used to read
     * name and description. This argument can be <code>null</code>, in that
     * case the bundle of this DockUI will be used.
     */
    public <T extends DockTheme> void registerTheme( Class<T> theme, ResourceBundle bundle ){
        registerTheme( new ThemePropertyFactory<T>( theme, bundle, this ));
    }
    
    /**
     * Stores a new theme.
     * @param factory the new theme
     */
    public void registerTheme( ThemeFactory factory ){
        if( factory == null )
            throw new IllegalArgumentException( "Theme must not be null" );
        
        themes.add( factory );
    }
    
    /**
     * Removes an earlier added factory from the set of theme-factories.
     * @param factory the factory to remove
     */
    public void unregisterTheme( ThemeFactory factory ){
        themes.remove( factory );
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
     * Replaces the bundle of this DockUI using the given Locale
     * @param locale the language of the DockUI
     */
    public void setBundle( Locale locale ){
        setBundle( ResourceBundle.getBundle( "data.locale.text", locale, this.getClass().getClassLoader() ));
    }
    
    /**
     * Gets the icon stored under <code>key</code>. The keys are stored in
     * a file "icons.ini" in the directory "data".
     * @param key the key for the icon
     * @return the icon or <code>null</code>
     */
    public Icon getIcon( String key ){
        return icons.get( key );
    }
    
    /**
     * Sets the icon that is used for a certain key.
     * @param key the key 
     * @param icon the icon to return if {@link #getIcon(String)} is invoked
     */
    public void setIcon( String key, Icon icon ){
        icons.put( key, icon );
    }
    
    /**
     * Gets a map containing keys and path for icon.
     * @return the icons
     */
    @SuppressWarnings("unchecked")
    protected Map<String, String> loadKeyPathMapping(){
        try{
            Properties properties = new Properties();
            InputStream in = DockUI.class.getResourceAsStream( "/data/icons.ini" );
            properties.load( in );
            in.close();
            
            //Properties properties = ResourceManager.getDefault().ini( "DockUI.mapping", "data/icons.ini", getClass().getClassLoader() ).get();
            Map<String, String> result = new HashMap<String, String>();
            Enumeration e = properties.keys();
            while( e.hasMoreElements() ){
                String key = (String)e.nextElement();
                result.put( key, properties.getProperty( key ));
            }
            return result;
        }
        catch( IOException ex ){
            ex.printStackTrace();
            return new HashMap<String, String>();
        }
    }
    
    /**
     * Fills all known icons as default-icons into the given manager.
     * @param manager the manager to fill
     */
    public void fillIcons( IconManager manager ){
        for( Map.Entry<String, Icon> icon : icons.entrySet() )
            manager.setIcon( icon.getKey(), Priority.DEFAULT, icon.getValue() );
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
        	return new BasicStationPaint();
        
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
        	return new BasicDisplayerFactory();
        
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
        	return new BasicCombiner();
        
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
