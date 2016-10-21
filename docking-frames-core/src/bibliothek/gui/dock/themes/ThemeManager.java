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
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.themes.basic.action.buttons.MiniButton;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.TypedPropertyUIScheme;
import bibliothek.gui.dock.util.TypedUIProperties;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.ClientOnly;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Path;

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
	public static final String STATION_PAINT = "dock.paint";
	
	/** Identifier for the type {@link Combiner} */
	public static final Type<Combiner> COMBINER_TYPE = new Type<Combiner>( "Combiner" );
	
	/** unique identifier for the basic {@link Combiner} */
	public static final String COMBINER = "dock.combiner";
	
	/** Identifier for the type {@link DisplayerFactory} */
	public static final Type<DisplayerFactory> DISPLAYER_FACTORY_TYPE = new Type<DisplayerFactory>( "DisplayerFactory" );
	
	/** unique identifier for the basic {@link DisplayerFactory} */
	public static final String DISPLAYER_FACTORY = "dock.displayer";
	
	/** Identifier for the type {@link BackgroundPaint} */
	public static final Type<BackgroundPaint> BACKGROUND_PAINT_TYPE = new Type<BackgroundPaint>( "BackgroundPaint" );
	
	/** unique identifier for the basic {@link BackgroundPaint} */
	public static final String BACKGROUND_PAINT = "dock.background";
	
	/** Identifier for the type {@link BorderModifier} */
	public static final Type<BorderModifier> BORDER_MODIFIER_TYPE = new Type<BorderModifier>( "BorderModifier" );
	
	/** unique identifier for the basic {@link BorderModifier} */
	public static final String BORDER_MODIFIER = "dock.border";
	
	/** Identifier for the type {@link SpanFactory} */
	public static final Type<SpanFactory> SPAN_FACTORY_TYPE = new Type<SpanFactory>( "SpanFactory" );
	
	/** unique identifier for the basic {@link SpanFactory} */
	public static final String SPAN_FACTORY = "dock.spanFactory";
	
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
     * Initializes this manager, must be called exactly once.
     */
    public void init(){
    	registerTypes();
    	link();
    }
    
    private void registerTypes(){
    	registerType( STATION_PAINT_TYPE );
    	registerType( COMBINER_TYPE );
    	registerType( DISPLAYER_FACTORY_TYPE );
    	registerType( BACKGROUND_PAINT_TYPE );
    	registerType( BORDER_MODIFIER_TYPE );
    	registerType( SPAN_FACTORY_TYPE );
    }
    
    private void link(){
    	link( DockTheme.STATION_PAINT, STATION_PAINT_TYPE, STATION_PAINT );
    	link( DockTheme.COMBINER, COMBINER_TYPE, COMBINER );
    	link( DockTheme.DISPLAYER_FACTORY, DISPLAYER_FACTORY_TYPE, DISPLAYER_FACTORY );
    	link( DockTheme.BACKGROUND_PAINT, BACKGROUND_PAINT_TYPE, BACKGROUND_PAINT );
    	link( DockTheme.BORDER_MODIFIER, BORDER_MODIFIER_TYPE, BORDER_MODIFIER );
    	link( DockTheme.SPAN_FACTORY, SPAN_FACTORY_TYPE, SPAN_FACTORY );
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
     * @see #link(PropertyKey, TypedUIProperties.Type, String)
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
	    		
    		controller.setFocusedDockable( new DefaultFocusRequest( focused, null, true ));
    		
    		for( UIListener listener : uiListeners() )
    			listener.themeChanged( controller, oldTheme, theme );
    	}
	}
    
    /**
     * Sets an algorithm to paint in the overlay panel of {@link DockStation}s. Possible
     * identifiers can be, but are not restricted to:
     * <ul>
     * 	<li>{@value #STATION_PAINT}.flap</li>
     * 	<li>{@value #STATION_PAINT}.screen</li>
     * 	<li>{@value #STATION_PAINT}.split</li>
     * 	<li>{@value #STATION_PAINT}.stack</li>
     * </ul>
     * @param id the identifier of the stations that should use <code>value</code>
     * @param value the new algorithm or <code>null</code>
     */
    public void setStationPaint( String id, StationPaint value ){
    	put( Priority.CLIENT, id, STATION_PAINT_TYPE, value );
    }
    
    /**
     * Sets the {@link UIBridge} that will transfer properties to those {@link UIValue}s whose kind is either
     * <code>kind</code> or a child of <code>kind</code>.
     * @param kind the kind of {@link UIValue} <code>bridge</code> will handle
     * @param bridge the new bridge or <code>null</code>
     */
    public void setStationPaintBridge( Path kind, UIBridge<StationPaint, UIValue<StationPaint>> bridge ){
    	if( bridge == null ){
    		unpublish( Priority.CLIENT, kind, STATION_PAINT_TYPE );
    	}
    	else{
    		publish( Priority.CLIENT, kind, STATION_PAINT_TYPE, bridge );
    	}
    }
    
    /**
     * Sets a strategy how two {@link Dockable}s can be merged into a new {@link Dockable}.
     * Valid identifiers may be, but are not restricted to:
     * <ul>
     * 	<li>{@value #COMBINER}.flap</li>
     * 	<li>{@value #COMBINER}.screen</li>
     * 	<li>{@value #COMBINER}.split</li>
     * 	<li>{@value #COMBINER}.stack</li>
     * </ul>
     * @param id the identifier of the item that uses <code>value</code>
     * @param value the new strategy, can be <code>null</code>
     */
    public void setCombiner( String id, Combiner value ){
    	put( Priority.CLIENT, id, COMBINER_TYPE, value );
    }
    
    /**
     * Sets the {@link UIBridge} that will transfer properties to those {@link UIValue}s whose kind is either
     * <code>kind</code> or a child of <code>kind</code>.
     * @param kind the kind of {@link UIValue} <code>bridge</code> will handle
     * @param bridge the new bridge or <code>null</code>
     */
    public void setCombinerBridge( Path kind, UIBridge<Combiner, UIValue<Combiner>> bridge ){
    	if( bridge == null ){
    		unpublish( Priority.CLIENT, kind, COMBINER_TYPE );
    	}
    	else{
    		publish( Priority.CLIENT, kind, COMBINER_TYPE, bridge );
    	}
    }
    
    /**
     * Sets a strategy how to display {@link Dockable}s on a {@link DockStation}. Valid
     * identifiers can be, but are not restricted to:
     * <ul>
     * 	<li>{@value #DISPLAYER_FACTORY}.flap</li>
     * 	<li>{@value #DISPLAYER_FACTORY}.screen</li>
     * 	<li>{@value #DISPLAYER_FACTORY}.split</li>
     * 	<li>{@value #DISPLAYER_FACTORY}.stack</li>
     * </ul>
     * @param id the identifier of the item that uses <code>value</code>
     * @param value the new strategy, can be <code>null</code>
     */
    public void setDisplayerFactory( String id, DisplayerFactory value ){
    	put( Priority.CLIENT, id, DISPLAYER_FACTORY_TYPE, value );
    }
    
    /**
     * Sets the {@link UIBridge} that will transfer properties to those {@link UIValue}s whose kind is either
     * <code>kind</code> or a child of <code>kind</code>.
     * @param kind the kind of {@link UIValue} <code>bridge</code> will handle
     * @param bridge the new bridge or <code>null</code>
     */
    public void setDisplayerFactoryBridge( Path kind, UIBridge<DisplayerFactory, UIValue<DisplayerFactory>> bridge ){
    	if( bridge == null ){
    		unpublish( Priority.CLIENT, kind, DISPLAYER_FACTORY_TYPE );
    	}
    	else{
    		publish( Priority.CLIENT, kind, DISPLAYER_FACTORY_TYPE, bridge );
    	}
    }
    
    /**
     * Sets a strategy to tell how to animate empty spaces when drag and dropping a {@link Dockable}.
     * Valid identifiers can be, but are not restricted to:
     * <ul>
     * 	<li>{@value #DISPLAYER_FACTORY}.flap</li>
     *  <li>{@value #DISPLAYER_FACTORY}.split</li>
     *  <li>{@value #DISPLAYER_FACTORY}.stack (currently not used)</li>
     *  <li>{@value #DISPLAYER_FACTORY}.screen (currently not used)</li>
     * </ul>
     * @param id the identifier of the item that uses <code>value</code>
     * @param value the new strategy, can be <code>null</code>
     */
    public void setSpanFactory( String id, SpanFactory value ){
    	put( Priority.CLIENT, id, SPAN_FACTORY_TYPE, value );
    }
    
    /**
     * Sets the {@link UIBridge} that will transfer properties to those {@link UIValue}s whose kind is either
     * <code>kind</code> or a child of <code>kind</code>.
     * @param kind the kind of {@link UIValue} <code>bridge</code> will handle
     * @param bridge the new bridge or <code>null</code>
     */
    public void setSpanFactoryBridge( Path kind, UIBridge<SpanFactory, UIValue<SpanFactory>> bridge ){
    	if( bridge == null ){
    		unpublish( Priority.CLIENT, kind, SPAN_FACTORY_TYPE );
    	}
    	else{
    		publish( Priority.CLIENT, kind, SPAN_FACTORY_TYPE, bridge );
    	}
    }
    
    /**
     * Sets an algorithm that is used to paint the background of items which register an {@link UIValue} with
     * an identifier of <code>id</code>. Valid identifier can be, but are not restricted to:
     * <ul>
     * 	<li>{@value #BACKGROUND_PAINT}.action</li>
     *  <li>{@value #BACKGROUND_PAINT}.displayer</li>
     *  <li>{@value #BACKGROUND_PAINT}.dockable</li>
     *  <li>{@value #BACKGROUND_PAINT}.station.flap</li>
     *  <li>{@value #BACKGROUND_PAINT}.station.flap.window</li>
     *  <li>{@value #BACKGROUND_PAINT}.station.screen</li>
     *  <li>{@value #BACKGROUND_PAINT}.station.split</li>
     *  <li>{@value #BACKGROUND_PAINT}.station.stack</li>
     *  <li>{@value #BACKGROUND_PAINT}.tabPane</li>
     *  <li>{@value #BACKGROUND_PAINT}.tabPane.child.menu</li>
     *  <li>{@value #BACKGROUND_PAINT}.tabPane.child.tab</li>
     *  <li>{@value #BACKGROUND_PAINT}.title</li>
     * </ul>
     * @param id the identifier of the items that should use <code>value</code>
     * @param value the new background algorithm, can be <code>null</code>
     */
    @ClientOnly
    public void setBackgroundPaint( String id, BackgroundPaint value ){
    	put( Priority.CLIENT, id, BACKGROUND_PAINT_TYPE, value );
    }
    
    /**
     * Sets the {@link UIBridge} that will transfer properties to those {@link UIValue}s whose kind is either
     * <code>kind</code> or a child of <code>kind</code>.
     * @param kind the kind of {@link UIValue} <code>bridge</code> will handle
     * @param bridge the new bridge or <code>null</code>
     */
    public void setBackgroundPaintBridge( Path kind, UIBridge<BackgroundPaint, UIValue<BackgroundPaint>> bridge ){
    	if( bridge == null ){
    		unpublish( Priority.CLIENT, kind, BACKGROUND_PAINT_TYPE );
    	}
    	else{
    		publish( Priority.CLIENT, kind, BACKGROUND_PAINT_TYPE, bridge );
    	}
    }
    
    /**
     * Sets a strategy that is used to modify the border of various components.<br>
     * Valid identifiers can be, but are not restricted to:
     * <ul>
     *  <li>{@link MiniButton#BORDER_KEY_NORMAL}</li>
     * 	<li>{@link MiniButton#BORDER_KEY_NORMAL_SELECTED}</li>
     * 	<li>{@link MiniButton#BORDER_KEY_MOUSE_OVER}</li>
     * 	<li>{@link MiniButton#BORDER_KEY_MOUSE_OVER_SELECTED}</li>
     * 	<li>{@link MiniButton#BORDER_KEY_MOUSE_PRESSED}</li>
     * 	<li>{@link MiniButton#BORDER_KEY_MOUSE_PRESSED_SELECTED}</li>
     * 	<li>{@value #BORDER_MODIFIER}.displayer.basic.base</li>
     * 	<li>{@value #BORDER_MODIFIER}.displayer.basic.content</li>	
     * 	<li>{@value #BORDER_MODIFIER}.displayer.bubble</li>
     * 	<li>{@value #BORDER_MODIFIER}.displayer.eclipse.no_title.out</li>
     * 	<li>{@value #BORDER_MODIFIER}.displayer.eclipse.no_title.in</li>
     * 	<li>{@value #BORDER_MODIFIER}.displayer.eclipse</li>
     * 	<li>{@value #BORDER_MODIFIER}.displayer.eclipse.content</li>
     * 	<li>{@value #BORDER_MODIFIER}.screen.window</li>
     * 	<li>{@value #BORDER_MODIFIER}.stack.eclipse</li>
     * 	<li>{@value #BORDER_MODIFIER}.stack.eclipse.content</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button.flat</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button.flat.hover</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button.flat.pressed</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button.flat.selected</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button.flat.selected.hover</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button.flat.selected.pressed</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button.selected</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.button.pressed</li>	
     * 	<li>{@value #BORDER_MODIFIER}.title.button.selected.pressed</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.eclipse.button.flat</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.flat</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.station.basic</li>
     * 	<li>{@value #BORDER_MODIFIER}.title.tab</li>
     * </ul>
     * @param id the identifier of the items that should use <code>modifier</code>
     * @param modifier the new strategy, can be <code>null</code>
     */
    public void setBorderModifier( String id, BorderModifier modifier ){
    	put( Priority.CLIENT, id, BORDER_MODIFIER_TYPE, modifier );
    }
    /**
     * Sets the {@link UIBridge} that will transfer properties to those {@link UIValue}s whose kind is either
     * <code>kind</code> or a child of <code>kind</code>.
     * @param kind the kind of {@link UIValue} <code>bridge</code> will handle
     * @param bridge the new bridge or <code>null</code>
     */
    public void setBorderModifierBridge( Path kind, UIBridge<BorderModifier, UIValue<BorderModifier>> bridge ){
    	if( bridge == null ){
    		unpublish( Priority.CLIENT, kind, BORDER_MODIFIER_TYPE );
    	}
    	else{
    		publish( Priority.CLIENT, kind, BORDER_MODIFIER_TYPE, bridge );
    	}
    }
}
