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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.ThemePropertyFactory;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.laf.DefaultLookAndFeelColors;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;
import bibliothek.gui.dock.util.laf.LookAndFeelColorsListener;
import bibliothek.gui.dock.util.laf.Nimbus6u10;
import bibliothek.gui.dock.util.laf.Windows;
import bibliothek.util.container.Tuple;

/**
 * A list of icons, text and methods used by the framework. 
 * @author Benjamin Sigg
 */
public class DockUI {
    /** An instance of DockUI */
	private static DockUI ui;
	
	/** 
	 * Key for an {@link Icon} stored in the {@link IconManager} for the action-overflow-menu. This menu is shown if there
	 * are too many {@link DockAction}s to show. 
	 */
	public static final String OVERFLOW_MENU_ICON = "overflow.menu";
	
    /** A list of all available themes */
    private List<ThemeFactory> themes = new ArrayList<ThemeFactory>();
    
    /** contains regex-LookAndFeelColor pairs */
    private List<Tuple<String, LookAndFeelColors>> lookAndFeelColors = new ArrayList<Tuple<String,LookAndFeelColors>>();
    
    /** the currently used {@link LookAndFeelColors} */
    private LookAndFeelColors lookAndFeelColor;
    
    /** a list of color listeners that is called from {@link #colorsListeners} */
    private List<LookAndFeelColorsListener> colorsListeners = new ArrayList<LookAndFeelColorsListener>();
    
    /** whether this is a secure environment where global {@link AWTEventListener}s are not allowed */
    private Boolean secureEnvironment = null;
    
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
        registerTheme( BasicTheme.class );
        registerTheme( FlatTheme.class );
        registerTheme( SmoothTheme.class );
        registerTheme( BubbleTheme.class );
        registerTheme( EclipseTheme.class );
        registerTheme( NoStackTheme.getFactory( BasicTheme.class ));
        registerTheme( NoStackTheme.getFactory( FlatTheme.class ));
        registerTheme( NoStackTheme.getFactory( SmoothTheme.class ));
        registerTheme( NoStackTheme.getFactory( BubbleTheme.class ));
    }
    
    private void registerColors(){
        registerColors( ".+", new DefaultLookAndFeelColors() );
        
        String jvmVersionString = System.getProperty("java.specification.version");
        int verIndex = jvmVersionString.indexOf("1.");
        if (verIndex >= 0) { // handle Java 9
            jvmVersionString = jvmVersionString.substring(verIndex + 2);
        }
        int major = Integer.parseInt(jvmVersionString);
        if( major >= 7 ){
        	registerColors( "javax\\.swing\\.plaf\\.nimbus\\.NimbusLookAndFeel", new Nimbus6u10() );
        }
        else{
        	registerColors( "com\\.sun\\.java\\.swing\\.plaf\\.nimbus\\.NimbusLookAndFeel", new Nimbus6u10() );
        }
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
     */
    public <T extends DockTheme> void registerTheme( Class<T> theme ){
        registerTheme( new ThemePropertyFactory<T>( theme ));
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
     * Registers a new {@link LookAndFeelColors}. The <code>lookAndFeelClassNameRegex</code>
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
     * This listener will automatically be transferred when another
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
            colorsListener.colorsChanged();
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
    		controller.getHierarchyLock().setConcurrent( true );
    	}
    	try{
    	    for( int i = station.getDockableCount()-1; i >= 0; i-- ){
    		    station.drag( station.getDockable( i ));
    	    }
    	
    	    factory.setLayout( station, layout, children, null );
    	}
    	finally{
    		if( controller != null ){
    			controller.getRegister().setStalled( false );
    			controller.getHierarchyLock().setConcurrent( false );
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
    
    /**
     * Tells whether this application runs in a restricted environment or not. This method
     * only makes a guess and may return a false result.
     * @return whether this is a restricted environment
     */
    public boolean isSecureEnvironment(){
    	if( secureEnvironment != null ){
    		return secureEnvironment;
    	}
    	
        try{
        	Toolkit toolkit = Toolkit.getDefaultToolkit();
        	AWTEventListener listener = new AWTEventListener(){
				public void eventDispatched( AWTEvent event ){
					// ignore	
				}
			}; 
        	toolkit.addAWTEventListener( listener, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK );
        	toolkit.removeAWTEventListener( listener );
			
//            SecurityManager security = System.getSecurityManager();
//            if( security != null ){
//                security.checkPermission(SecurityConstants.ALL_AWT_EVENTS_PERMISSION);
//            }
        }
        catch( SecurityException ex ){
        	secureEnvironment = true;
            return true;
        }
        
        secureEnvironment = false;
        return false;
    }
    
    /**
     * Overrides the result of {@link #isSecureEnvironment()}, any future call of that method will
     * return <code>secureEnvironment</code>.
     * @param secureEnvironment Whether global {@link AWTEventListener}s are allowed or not, a value of <code>true</code> 
     * indicates that listeners are not allowed
     */
    public void setSecureEnvironment( boolean secureEnvironment ){
		this.secureEnvironment = secureEnvironment;
	}
    
    private static Component firstOnPath( Container parent, Component child ){
    	Component result = child;
    	while( result.getParent() != parent ){
    		result = result.getParent();
    	}
    	return result;
    }
}
