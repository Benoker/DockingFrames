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

import bibliothek.gui.dock.dockable.DefaultDockableMovingImageFactory;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.span.DefaultSpanFactory;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.DockThemeExtension;
import bibliothek.gui.dock.themes.StationCombinerValue;
import bibliothek.gui.dock.themes.ThemeCombiner;
import bibliothek.gui.dock.themes.ThemeDisplayerFactory;
import bibliothek.gui.dock.themes.ThemeDockableSelection;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.ThemeStationPaint;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;
import bibliothek.util.FrameworkOnly;

/**
 * A theme describes how a {@link DockStation} looks like, which {@link DockTitle} are selected, and other behavior. 
 * A theme needs only to support one {@link DockController} at a time.<br>
 * Most of the methods of this interface should not be called by the client. Instead the {@link ThemeManager} and
 * the {@link DockProperties} should be used. To request a value from the {@link ThemeManager} the method
 * {@link ThemeManager#add(String, bibliothek.util.Path, bibliothek.gui.dock.util.TypedUIProperties.Type, UIValue) add} must
 * be used to install an observer (of type {@link UIValue}). The {@link PropertyKey}s specified in this interface may
 * be used directly, but clients should bear in mind that the {@link ThemeManager} can override resources that are
 * stored in the {@link DockProperties}.
 * @author Benjamin Sigg
 */
public interface DockTheme {
	/**
	 * A unique identifier for the {@link DockProperties} to access the current {@link BackgroundPaint}.
	 */
	public static final PropertyKey<BackgroundPaint> BACKGROUND_PAINT = new PropertyKey<BackgroundPaint>( "dock.background" );
	
	/**
	 * A unique identifier for the {@link DockProperties} to access the current {@link BorderModifier}.
	 */
	public static final PropertyKey<BorderModifier> BORDER_MODIFIER = new PropertyKey<BorderModifier>( "dock.borderModifier" );
	
	/** The factory creating {@link Span}s and ultimately deciding how the animation looks like when dropping a {@link Dockable} */
	public static final PropertyKey<SpanFactory> SPAN_FACTORY = new PropertyKey<SpanFactory>( "span factory", new ConstantPropertyFactory<SpanFactory>( new DefaultSpanFactory() ), true );
	
	/**
     * Install this theme at <code>controller</code>. The theme
     * may change any properties it likes.
     * @param controller the controller
     * @param extensions a set of extensions specifically for this theme
     */
    public void install( DockController controller, DockThemeExtension[] extensions );
    
    /**
     * Uninstalls this theme from <code>controller</code>. The theme
     * has to remove all listeners it added. 
     * @param controller the controller
     */
    public void uninstall( DockController controller );
    
    /** 
     * A unique identifier for the {@link DockProperties} to access the current {@link Combiner}. The default
     * value will be derived from the current {@link DockTheme}. 
     */
    public static final PropertyKey<Combiner> COMBINER = new PropertyKey<Combiner>( "dock.combiner",
    		new DynamicPropertyFactory<Combiner>(){
    			public Combiner getDefault( PropertyKey<Combiner> key, DockProperties properties ){
    				return new ThemeCombiner( properties.getController() );
    			}
			}, true );
    
    /**
     * Gets the Combiner for <code>station</code>.<br>
     * This method should not be used directly, instead an {@link UIValue} of type {@link StationCombinerValue} should
     * be installed at the local {@link ThemeManager} to retrieve the value.
     * @param station the station whose combiner is searched
     * @return a combiner for <code>station</code>
     */
    @FrameworkOnly
    public Combiner getCombiner( DockStation station );

    /**
     * A unique identifier for the {@link DockProperties} to access the current {@link StationPaint}. The default
     * value will be derived from the current {@link DockTheme}.
     */
    public static final PropertyKey<StationPaint> STATION_PAINT = new PropertyKey<StationPaint>( "dock.paint", 
    		new DynamicPropertyFactory<StationPaint>(){
    			public StationPaint getDefault( PropertyKey<StationPaint> key, DockProperties properties ){
    				return new ThemeStationPaint( properties.getController() );
    			}
			}, true );
    
    /**
     * Gets the paint which is used to draw things onto <code>station</code>.<br>
     * This method should not be used directly, instead an {@link UIValue} of type {@link DefaultStationPaintValue} should
     * be installed at the local {@link ThemeManager} to retrieve the value.
     * @param station the station to paint on
     * @return the paint for <code>station</code>
     */
    @FrameworkOnly
    public StationPaint getPaint( DockStation station );
    
    /**
     * A unique identifier for the {@link DockProperties} to access the current {@link DisplayerFactory}. The default
     * value will be derived from the current {@link DockTheme}.
     */
    public static final PropertyKey<DisplayerFactory> DISPLAYER_FACTORY = new PropertyKey<DisplayerFactory>( "dock.displayerFactory",
    		new DynamicPropertyFactory<DisplayerFactory>(){
    			public DisplayerFactory getDefault( PropertyKey<DisplayerFactory> key, DockProperties properties ){
    				return new ThemeDisplayerFactory( properties.getController() );
    			}
			}, true );
    
    /**
     * Gets a displayer factory for <code>station</code>.<br>
     * This method should not be used directly, instead an {@link UIValue} of type {@link DefaultDisplayerFactoryValue} should
     * be installed at the local {@link ThemeManager} to retrieve the value.
     * @param station the station on which the created {@link DockableDisplayer}
     * is shown
     * @return the factory to create displayer
     */
    @FrameworkOnly
    public DisplayerFactory getDisplayFactory( DockStation station );
    
    /**
     * Gets the default {@link DockTitleFactory} which is used if no other factory is set.<br>
     * The result of this method is installed in the {@link DockTitleManager} using
     * the key {@link DockTitleManager#THEME_FACTORY_ID} and priority {@link Priority#THEME}. A
     * theme may use the manager to change the factory at any time.
     * @param controller the controller using this theme
     * @return the factory
     */
    public DockTitleFactory getTitleFactory( DockController controller );
    
    /**
     * Identifier for the {@link DockableMovingImageFactory} that is used to show an image during 
     * drag and drop operations.
     */
    public static final PropertyKey<DockableMovingImageFactory> DOCKABLE_MOVING_IMAGE_FACTORY = new PropertyKey<DockableMovingImageFactory>( "dock.movingImageFactory", 
    		new DynamicPropertyFactory<DockableMovingImageFactory>(){
		    	public DockableMovingImageFactory getDefault( PropertyKey<DockableMovingImageFactory> key, DockProperties properties ){
			    	return new DefaultDockableMovingImageFactory( properties.getController() );
		    	}
			} , true );
    
    /**
     * Gets a factory for images which are moved around by the user.<br>
     * This method should not be invoked directly, instead the property key {@link #DOCKABLE_MOVING_IMAGE_FACTORY}
     * should be used.
     * @param controller the controller for which the factory is needed
     * @return a factory
     */
    @FrameworkOnly
    public DockableMovingImageFactory getMovingImageFactory( DockController controller );
 
    /**
     * Identifier for the {@link DockableSelection}, a panel that is shown to select a {@link Dockable}
     * using only the keyboard.
     */
    public static final PropertyKey<DockableSelection> DOCKABLE_SELECTION = new PropertyKey<DockableSelection>( "dock.dockableSelection",
    		new DynamicPropertyFactory<DockableSelection>(){
    			public DockableSelection getDefault( PropertyKey<DockableSelection> key, DockProperties properties ){
    				return new ThemeDockableSelection( properties.getController() );
    			}
			}, true );
    
    /**
     * Gets a selector for {@link Dockable}s. This method should not be invoked directly, instead
     * the property key {@link #DOCKABLE_SELECTION} should be used.
     * @param controller the controller for which the selector will be used
     * @return the selector
     */
    @FrameworkOnly
    public DockableSelection getDockableSelection( DockController controller );
}
