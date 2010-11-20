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
package bibliothek.gui.dock.themes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.TypedPropertyUIScheme;
import bibliothek.gui.dock.util.TypedUIProperties;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.FrameworkOnly;

/**
 * The {@link ThemeManager} is responsible for collecting properties of the current {@link DockTheme} and redistribute them. The
 * {@link ThemeManager} provides facilities for clients to modify and override properties of a theme without the need
 * to access or change the {@link DockTheme} itself.
 * @author Benjamin Sigg
 */
public class ThemeManager extends TypedUIProperties{
	/** Identifier for a factory that creates {@link StationPaint}s. */
	public static final Type<StationPaint> STATION_PAINT_TYPE = new Type<StationPaint>( "StationPaint" );
	
	/** unique identifier for the basic {@link StationPaint} */
	public static final String STATION_PAINT = "paint";
	
	/** Identifier for the type {@link Combiner} */
	public static final Type<Combiner> COMBINER_TYPE = new Type<Combiner>( "Combiner" );
	
	/** unique identifier for the basic {@link Combiner} */
	public static final String COMBINER = "combiner";
	
	/** Identifier for the type {@link DisplayerFactory} */
	public static final Type<DisplayerFactory> DISPLAYER_FACTORY_TYPE = new Type<DisplayerFactory>( "DisplayerFactory" );
	
	/** unique identifier for the basic {@link DisplayerFactory} */
	public static final String DISPLAYER_FACTORY = "displayer";
	
	/** the controller owning the manager */
	private DockController controller;
	
	/** the current theme */
	private DockTheme theme;
	
	/** Listeners observing the ui */
    private List<UIListener> uiListeners = new ArrayList<UIListener>();

    /** a listener that is added to the {@link UIManager} and gets notified when the {@link LookAndFeel} changes */
    private PropertyChangeListener lookAndFeelObserver = new PropertyChangeListener(){
        public void propertyChange( PropertyChangeEvent evt ) {
            if( "lookAndFeel".equals( evt.getPropertyName() )){
                updateUI();
            }
        }
    };
    
    /** items to transfer directly from {@link DockProperties} to <code>this</code> */
    private TypedPropertyUIScheme transfers;
    
    /**
     * Creates a new object
     * @param controller the owner of this manager, not <code>null</code>
     */
    public ThemeManager( DockController controller ){
    	super( controller );
    	
    	if( controller == null ){
    		throw new IllegalArgumentException( "controller must not be null" );
    	}
    	this.controller = controller;
    	
    	UIManager.addPropertyChangeListener( lookAndFeelObserver );
    	
    	transfers = new TypedPropertyUIScheme( controller.getProperties() );
    	setScheme( Priority.THEME, transfers );
    }
    
    /**
     * Initializes this managere, must be called exactly once.
     */
    public void init(){
    	registerTypes();
    	link();
    }
    
    private void registerTypes(){
    	registerType( STATION_PAINT_TYPE );
    	registerType( COMBINER_TYPE );
    	registerType( DISPLAYER_FACTORY_TYPE );
    }
    
    private void link(){
    	link( DockTheme.STATION_PAINT, STATION_PAINT_TYPE, STATION_PAINT );
    	link( DockTheme.COMBINER, COMBINER_TYPE, COMBINER );
    	link( DockTheme.DISPLAYER_FACTORY, DISPLAYER_FACTORY_TYPE, DISPLAYER_FACTORY );
    }
    
    /**
     * Destroys this manager and releases resources.
     */
    @FrameworkOnly
    public void kill(){
    	theme.uninstall( controller );
    	UIManager.removePropertyChangeListener( lookAndFeelObserver );
    }
    
    /**
     * Creates a link between the property <code>source</code> and the entry <code>id</code> on the
     * level {@link Priority#THEME}.
     * @param <V> the kind of property to read
     * @param <A> the kind of entry to write
     * @param source the key of the property to read
     * @param type the type of the entry to write
     * @param id the identifier of the entry to write
     */
    public <V, A extends V> void link( PropertyKey<A> source, Type<V> type, String id ){
    	transfers.link( source, type, id );
    }
    
    /**
     * Disables a link between a property and the entry <code>id</code>.
     * @param <V> the <code>type</code>
     * @param type the type of the entry
     * @param id the identifier of the entry to unlink
     * @see #link(PropertyKey, Type, String)
     */
    public <V> void unlink( Type<V> type, String id ){
    	transfers.unlink( type, id );
    }
    
    /**
     * Adds an {@link UIListener} to this manager, the listener gets
     * notified when the graphical user interface needs an update because
     * the {@link LookAndFeel} changed.
     * @param listener the new listener
     */
    public void addUIListener( UIListener listener ){
        uiListeners.add( listener );
    }
    
    /**
     * Removes a listener from this manager.
     * @param listener the listener to remove
     */
    public void removeUIListener( UIListener listener ){
        uiListeners.remove( listener );
    }
    
    /**
     * Gets all the available {@link UIListener}s.
     * @return the list of listeners
     */
    protected UIListener[] uiListeners(){
    	return uiListeners.toArray( new UIListener[ uiListeners.size() ]);
    }
    
    /**
     * Informs all registered {@link UIListener}s that the user interface
     * needs an update because the {@link LookAndFeel} changed.
     * @see #addUIListener(UIListener)
     * @see #removeUIListener(UIListener)
     */
    public void updateUI(){
        for( UIListener listener : uiListeners() )
            listener.updateUI( controller );
    }
    
    /**
     * Gets the current theme
     * @return the theme
     */
    public DockTheme getTheme() {
		return theme;
	}
    
    /**
     * Sets the theme of this manager. This method fires events on registered {@link UIListener}s
     * and ensures that all {@link DockStation}s receive the update
     * @param theme the new theme
     */
    public void setTheme( DockTheme theme ){
    	if( theme == null )
    		throw new IllegalArgumentException( "Theme must not be null" );
    	
    	if( this.theme != theme ){
    		for( UIListener listener : uiListeners() )
    			listener.themeWillChange( controller, this.theme, theme );
    		
    		DockRegister register = controller.getRegister();
    		DockTheme oldTheme = this.theme;
    		Dockable focused = null;
    		try{
    			register.setStalled( true );
    			focused = controller.getFocusedDockable();
    			
	    		if( this.theme != null )
	    			this.theme.uninstall( controller );
	    		
	    		this.theme = theme;
	    		
	    		ExtensionName<DockThemeExtension> name = new ExtensionName<DockThemeExtension>( 
	    				DockThemeExtension.DOCK_THEME_EXTENSION, DockThemeExtension.class, DockThemeExtension.THEME_PARAMETER, theme );
	    		List<DockThemeExtension> extensions = controller.getExtensions().load( name );
	    		
	    		theme.install( controller, extensions.toArray( new DockThemeExtension[ extensions.size() ] ) );
	    		controller.getDockTitleManager().registerTheme( DockTitleManager.THEME_FACTORY_ID, theme.getTitleFactory( controller ) );
	    		
	    		// update only those station which are registered to this controller
	    		for( DockStation station : register.listDockStations() ){
	    			if( station.getController() == controller ){
	    				station.updateTheme();
	    			}
	    		}
    		}
    		finally{
    			register.setStalled( false );
    		}
	    		
    		controller.setFocusedDockable( focused, true );
    		
    		for( UIListener listener : uiListeners() )
    			listener.themeChanged( controller, oldTheme, theme );
    	}
	}
}
