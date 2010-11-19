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

package bibliothek.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.StationPaintValue;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.ThemePropertyFactory;
import bibliothek.gui.dock.themes.basic.BasicCombiner;
import bibliothek.gui.dock.themes.basic.BasicDisplayerFactory;
import bibliothek.gui.dock.themes.basic.BasicStationPaint;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.laf.DefaultLookAndFeelColors;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;
import bibliothek.gui.dock.util.laf.LookAndFeelColorsListener;
import bibliothek.gui.dock.util.laf.Nimbus6u10;
import bibliothek.gui.dock.util.laf.Windows;
import bibliothek.gui.dock.util.local.LocaleListener;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;
import bibliothek.util.container.Tuple;

/**
 * A list of icons, text and methods used by the framework. 
 * @author Benjamin Sigg
 */
@Todo(compatibility=Compatibility.BREAK_MAJOR, target=Version.VERSION_1_1_0, priority=Todo.Priority.MAJOR,
		description="Use an UIManager instead of bundles to handle text, also applies to the Common library")
public class DockUI {
    /** An instance of DockUI */
	private static DockUI ui;
	
	/** Key for an {@link Icon} stored in the {@link IconManager} for an overflow-menu */
	@Todo(compatibility=Compatibility.BREAK_MINOR, priority=Todo.Priority.MINOR, target=Todo.Version.VERSION_1_1_0,
			description="Instead of just a simple icon allow clients more influence of what an overflow-menu can do. This key may remain, but its value may not be used all the time.")
	public static final String OVERFLOW_MENU_ICON = "overflow.menu";
	
	/** The resource bundle for some text shown in this framework */
	private ResourceBundle bundle;
	
	/** the local used to load the {@link ResourceBundle} */
	private Locale locale = Locale.getDefault();
	
    /** The icons used in this framework */
    private Map<String, Icon> icons;
    
    /** A list of all available themes */
    private List<ThemeFactory> themes = new ArrayList<ThemeFactory>();
    
    /** contains regex-LookAndFeelColor pairs */
    private List<Tuple<String, LookAndFeelColors>> lookAndFeelColors = new ArrayList<Tuple<String,LookAndFeelColors>>();
    
    /** the currently used {@link LookAndFeelColors} */
    private LookAndFeelColors lookAndFeelColor;
    
    /** a list of color listeners that is called from {@link #colorsListeners} */
    private List<LookAndFeelColorsListener> colorsListeners = new ArrayList<LookAndFeelColorsListener>();
    
    /** a list of listeners waiting for the language to change */
    private List<LocaleListener> localeListeners = new ArrayList<LocaleListener>();
    
    /** a listener added to {@link #lookAndFeelColor} */
    private LookAndFeelColorsListener colorsListener = new LookAndFeelColorsListener(){
        public void colorChanged( String key ) {
            for( LookAndFeelColorsListener listener : colorsListeners.toArray( new LookAndFeelColorsListener[ colorsListeners.size()] ))
                listener.colorChanged( key );
        }

        public void colorsChanged() {
            for( LookAndFeelColorsListener listener : colorsListeners.toArray( new LookAndFeelColorsListener[ colorsListeners.size()] ))
                listener.colorsChanged();
        }
    };
    
    /**
     * Gets the default instance of DockUI.
     * @return the instance
     */
	public static DockUI getDefaultDockUI(){
		if( ui == null ){
		    synchronized( DockUI.class ){
		        if( ui == null ){
		            ui = new DockUI();
		        }
		    }
		}
		return ui;
	}
	
    /**
     * Creates a new DockUI
     */
    protected DockUI(){
        icons = DockUtilities.loadIcons( "data/icons.ini", null, DockUI.class.getClassLoader() );
        
        // special icons
        icons.put( OVERFLOW_MENU_ICON, new Icon(){
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
        
        setLocale( Locale.getDefault() );
        
        registerThemes();
        
        registerColors();
        
        UIManager.addPropertyChangeListener( new PropertyChangeListener(){
            public void propertyChange( PropertyChangeEvent evt ) {
                if( "lookAndFeel".equals( evt.getPropertyName() )){
                    updateUI();
                }
            }            
        });
    }
    
    /**
     * Called when the {@link LookAndFeel} changed.
     */
    protected void updateUI(){
        updateLookAndFeelColors();
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
    
    private void registerColors(){
        registerColors( ".+", new DefaultLookAndFeelColors() );
        registerColors( "com\\.sun\\.java\\.swing\\.plaf\\.nimbus\\.NimbusLookAndFeel", new Nimbus6u10() );
        registerColors( "com\\.sun\\.java\\.swing\\.plaf\\.windows\\.WindowsLookAndFeel", new Windows() );
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
     * Registeres a new {@link LookAndFeelColors}. The <code>lookAndFeelClassNameRegex</code>
     * is a regular expression. If a {@link LookAndFeel} is active whose class name
     * {@link String#matches(String) matches} <code>lookAndFeelClassNameRegex</code>,
     * then <code>colors</code> becomes the selected source for colors. If more
     * then one regex matches, the last one that was added to this {@link DockUI}
     * is taken. So generally one would first add the most general regexes, and
     * the more detailed ones later.
     * @param lookAndFeelClassNameRegex a description of a class name
     * @param colors the new set of colors
     */
    public void registerColors( String lookAndFeelClassNameRegex, LookAndFeelColors colors ){
        if( lookAndFeelClassNameRegex == null )
            throw new IllegalArgumentException( "lookAndFeelClassNameRegex must not be null" );
            
        if( colors == null )
            throw new IllegalArgumentException( "colors must not be null" );
        
        lookAndFeelColors.add( new Tuple<String, LookAndFeelColors>( lookAndFeelClassNameRegex, colors ));
        updateLookAndFeelColors();
    }
    
    /**
     * Adds a listener which gets informed when a color of the current
     * {@link LookAndFeelColors} changes. This listener gets not informed
     * about any changes when the {@link LookAndFeel} itself gets replaced.
     * This listener will automatically be transfered when another 
     * {@link LookAndFeelColors} gets selected.
     * @param listener the new listener, not <code>null</code>
     */
    public void addLookAndFeelColorsListener( LookAndFeelColorsListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        
        colorsListeners.add( listener );
    }
    
    /**
     * Removes a listener from this {@link DockUI}.
     * @param listener the listener to remove
     */
    public void removeLookAndFeelColorsListener( LookAndFeelColorsListener listener ){
        colorsListeners.remove( listener );
    }
    
    /**
     * Updates the currently used {@link LookAndFeelColors} to the best
     * matching colors.
     */
    protected void updateLookAndFeelColors(){
        LookAndFeelColors next = selectBestMatchingColors();
        if( next != lookAndFeelColor ){
            if( lookAndFeelColor != null ){
                lookAndFeelColor.unbind();
                lookAndFeelColor.removeListener( colorsListener );
            }
            
            lookAndFeelColor = next;
            if( next != null ){
                next.bind();
                lookAndFeelColor.addListener( colorsListener );
            }
        }
    }
    
    /**
     * Gets the {@link LookAndFeelColors} which matches the current
     * {@link LookAndFeel} best.
     * @return the current set of colors
     */
    protected LookAndFeelColors selectBestMatchingColors(){
        String className = UIManager.getLookAndFeel().getClass().getName();
        for( int i = lookAndFeelColors.size()-1; i >= 0; i-- ){
            if( className.matches( lookAndFeelColors.get( i ).getA() ))
                return lookAndFeelColors.get( i ).getB();
        }
        
        return null;
    }
    
    /**
     * Gets the current source of colors that depend on the {@link LookAndFeel}.
     * @return the current source of colors
     */
    public LookAndFeelColors getColors(){
        return lookAndFeelColor;
    }
    
    /**
     * Gets the color <code>key</code> where <code>key</code> is one of
     * the keys specified in {@link LookAndFeelColors}.
     * @param key the name of the color
     * @return the color or <code>null</code>
     */
    public static Color getColor( String key ){
        return getDefaultDockUI().getColors().getColor( key );
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
     * Sets the locale for which a {@link #getBundle() ResourceBundle}
     * should be loaded.
     * @param locale the new locale, not <code>null</code>
     */
    public void setLocale( Locale locale ){
    	if( locale == null )
    		throw new IllegalArgumentException( "locale must not be null" );
    	setBundle( locale );
    }
    
    /**
     * Gets the {@link Locale} for which {@link #getBundle() the ResourceBundle}
     * was loaded.
     * @return the locale, not <code>null</code>
     */
    public Locale getLocale(){
		return locale;
	}
    
    /**
     * Adds a new {@link LocaleListener}.
     * @param listener the new listener, not <code>null</code>
     */
    public void addLocaleListener( LocaleListener listener ){
    	localeListeners.add( listener );
    }

    /**
     * Removes <code>listener</code> from this {@link DockUI}.
     * @param listener the listener to remove
     */
    public void removeLocaleListener( LocaleListener listener ){
    	localeListeners.remove( listener );
    }
    
    private LocaleListener[] localeListeners(){
    	return localeListeners.toArray( new LocaleListener[ localeListeners.size() ] );
    }
    
    /**
     * Sets the resource bundle which should be used.
     * @param bundle the bundle
     */
    public void setBundle( ResourceBundle bundle ){
		this.bundle = bundle;
		
		for( LocaleListener listener : localeListeners() )
			listener.bundleChanged( this );
	}
    
    /**
     * Replaces the bundle of this DockUI using the given Locale
     * @param locale the language of the DockUI
     * @deprecated replaced by {@link #setLocale(Locale)}
     */
    @Deprecated
    public void setBundle( Locale locale ){
    	this.locale = locale;
        setBundle( ResourceBundle.getBundle( "data.locale.text", locale, this.getClass().getClassLoader() ));
        
        for( LocaleListener listener : localeListeners() )
        	listener.localeChanged( this );
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
     * @deprecated since the {@link ThemeManager} exists, this method should no longer be used. Instead an
     * {@link UIValue} should be registered at the {@link ThemeManager}, see {@link StationPaintValue}.
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MINOR, priority=Todo.Priority.ENHANCEMENT, target=Version.VERSION_1_1_1,
    		description="remove this methode")
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
     * @param <L> the type of the layout needed to describe the contents
     * of the station
     * @param station the station to update
     * @param factory a factory used to remove and to add the elements
     * @throws IOException if the factory throws an exception
     */
    public static <D extends DockStation, L> void updateTheme( D station, DockFactory<D,?,L> factory ) throws IOException{
        Map<Integer, Dockable> children = new HashMap<Integer, Dockable>();
    	Map<Dockable, Integer> ids = new HashMap<Dockable, Integer>();
    	
    	for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
    		Dockable child = station.getDockable(i);
    		children.put(i, child);
    		ids.put(child, i);
    	}
    	
    	L layout = factory.getLayout( station, ids );
    	DockController controller = station.getController();
    	if( controller != null ){
    		controller.getRegister().setStalled( true );
    	}
    	try{
    	    for( int i = station.getDockableCount()-1; i >= 0; i-- ){
    		    station.drag( station.getDockable( i ));
    	    }
    	
    	    factory.setLayout( station, layout, children );
    	}
    	finally{
    		if( controller != null ){
    			controller.getRegister().setStalled( false );
    		}
    	}
    }
    
    /**
     * Searches the first {@link JDesktopPane} which either is <code>component</code>
     * or a parent of <code>component</code>.
     * @param component the component whose parent is searched
     * @return the parent {@link JDesktopPane} or <code>null</code> if not found
     */
    public static JDesktopPane getDesktopPane( Component component ){
		while( component != null ){
			if( component instanceof JDesktopPane ){
				return ((JDesktopPane)component);
			}
			component = component.getParent();
		}
		return null;
    }
    
    /**
     * Tells whether <code>above</code> overlaps <code>under</code>. This method
     * assumes that both components have a mutual parent. The method checks the location
     * and the z-order of both components.
     * @param above the component that is supposed to be above <code>under</code>
     * @param under the component that is supposed to be under <code>above</code>
     * @return <code>true</code> is <code>above</code> is overlapping <code>under</code>
     */
    public static boolean isOverlapping( Component above, Component under ){
    	if( SwingUtilities.isDescendingFrom( under, above )){
    		return false;
    	}
    	if( SwingUtilities.isDescendingFrom( above, under )){
    		return true;
    	}
    	if( above == under ){
    		return true;
    	}
    	
    	Container parent = above.getParent();
    	while( parent != null ){
    		if( SwingUtilities.isDescendingFrom( under, parent )){
    			// found mutual parent
    			
    			Point locationA = new Point( 0, 0 );
    			Point locationU = new Point( 0, 0 );
    			
    			locationA = SwingUtilities.convertPoint( above, locationA, parent );
    			locationU = SwingUtilities.convertPoint( under, locationU, parent );
    			
    			Rectangle boundsA = new Rectangle( locationA, above.getSize() );
    			Rectangle boundsU = new Rectangle( locationU, under.getSize() );
    			
    			if( !boundsA.intersects( boundsU )){
    				return false;
    			}
    			
    			Component pathA = firstOnPath( parent, above );
    			Component pathU = firstOnPath( parent, under );
    			
    			int zA = parent.getComponentZOrder( pathA );
    			int zU = parent.getComponentZOrder( pathU );
    			
    			return zA < zU;
    		}
    		parent = parent.getParent();
    	}
    	return false;
    }
    
    private static Component firstOnPath( Container parent, Component child ){
    	Component result = child;
    	while( result.getParent() != parent ){
    		result = result.getParent();
    	}
    	return result;
    }
}
