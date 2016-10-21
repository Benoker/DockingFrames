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

package bibliothek.gui.dock.themes;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.basic.BasicColorScheme;
import bibliothek.gui.dock.themes.basic.BasicCombiner;
import bibliothek.gui.dock.themes.basic.BasicDisplayerFactory;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.themes.basic.BasicDockableSelection;
import bibliothek.gui.dock.themes.basic.BasicMovingImageFactory;
import bibliothek.gui.dock.themes.basic.BasicSpanFactory;
import bibliothek.gui.dock.themes.basic.BasicStackDockComponent;
import bibliothek.gui.dock.themes.basic.BasicStationPaint;
import bibliothek.gui.dock.themes.color.ExtendingColorScheme;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.NullPriorityValue;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;

/**
 * A {@link DockTheme theme} that does not install anything and uses the
 * default-implementations off all factories. It is possible to replace 
 * any of the factories.
 * @author Benjamin Sigg
 */
@ThemeProperties(
        nameBundle="theme.basic", 
        descriptionBundle="theme.basic.description",
        authors={"Benjamin Sigg"},
        webpages={})
public class BasicTheme implements DockTheme{
    /** combines several Dockables */
    private NullPriorityValue<Combiner> combiner = new NullPriorityValue<Combiner>();

    /** paints on stations */
    private NullPriorityValue<StationPaint> paint = new NullPriorityValue<StationPaint>();

    /** creates panels for Dockables */
    private NullPriorityValue<DisplayerFactory> displayerFactory = new NullPriorityValue<DisplayerFactory>();

    /** creates titles Dockables */
    private NullPriorityValue<DockTitleFactory> titleFactory = new NullPriorityValue<DockTitleFactory>();

    /** selects the image which should be displayed when moving a dockable*/
    private NullPriorityValue<DockableMovingImageFactory> movingImage = new NullPriorityValue<DockableMovingImageFactory>();

    /** the factory used to create components for {@link StackDockStation} */
    private NullPriorityValue<StackDockComponentFactory> stackDockComponentFactory = new NullPriorityValue<StackDockComponentFactory>();
    
    /** the side at which tabs are normally shown */
    private NullPriorityValue<TabPlacement> tabPlacement = new NullPriorityValue<TabPlacement>();

    /** how spans are created */
    private NullPriorityValue<SpanFactory> spanFactory = new NullPriorityValue<SpanFactory>();
    
    /** extensions used by this theme */
    private DockThemeExtension[] extensions;
    
    /** the key to set the {@link ColorScheme} of this theme */
    public static final PropertyKey<ColorScheme> BASIC_COLOR_SCHEME = 
        new PropertyKey<ColorScheme>( "dock.ui.BasicTheme.ColorScheme",
        		new DynamicPropertyFactory<ColorScheme>(){
        			public ColorScheme getDefault( PropertyKey<ColorScheme> key, DockProperties properties ){
        				return new BasicColorScheme();
        			}
        		}, true );

    /** the color scheme used in this theme */
    private PropertyValue<ColorScheme> colorScheme = new PropertyValue<ColorScheme>( BASIC_COLOR_SCHEME ){
        @Override
        protected void valueChanged( ColorScheme oldValue, ColorScheme newValue ) {
            updateColors();
        }
    };

    /** how to select a new focused dockable */
    private NullPriorityValue< DockableSelection> selection = new NullPriorityValue<DockableSelection>();

    /** the controller which is installed */
    private DockController controller;
    
    /** a listener waiting for changed {@link LookAndFeel}s */
    private UIListener uiListener = new UIListener(){
        public void updateUI( DockController controller ){
            BasicTheme.this.updateUI();
        }
        public void themeChanged(DockController controller, DockTheme oldTheme, DockTheme newTheme) {
            // ignore
        }
        public void themeWillChange(DockController controller, DockTheme oldTheme, DockTheme newTheme) {
            // ignore
        }
    };

    /**
     * Creates a new <code>BasicTheme</code>.
     */
    public BasicTheme() {
        setCombiner( new BasicCombiner(), Priority.DEFAULT );
        setPaint( new BasicStationPaint(), Priority.DEFAULT );
        setDisplayerFactory( new BasicDisplayerFactory(), Priority.DEFAULT );
        setTitleFactory( new BasicDockTitleFactory(), Priority.DEFAULT );
        setMovingImageFactory( new BasicMovingImageFactory(), Priority.DEFAULT );
        setStackDockComponentFactory( new StackDockComponentFactory(){
            public StackDockComponent create( StackDockComponentParent station ) {
                return new BasicStackDockComponent( station );
            }
        }, Priority.DEFAULT );
        setDockableSelection( new BasicDockableSelection(), Priority.DEFAULT );
        setTabPlacement( TabPlacement.BOTTOM_OF_DOCKABLE, Priority.DEFAULT );
        setSpanFactory( new BasicSpanFactory( 250, 250 ), Priority.DEFAULT );
    }

    public void install( DockController controller, DockThemeExtension[] extensions ){
    	this.extensions = extensions;
    	for( DockThemeExtension extension : extensions ){
    		extension.install( controller, this );
    	}
    	install( controller );
    	for( DockThemeExtension extension : extensions ){
    		extension.installed( controller, this );
    	}
    }
    
    /**
     * Installs the basic items of this theme, ignoring any {@link DockThemeExtension}.
     * @param controller the new owner of this theme
     */
    protected void install( DockController controller ){
        if( this.controller != null )
            throw new IllegalStateException( "Theme is already in use" );

        this.controller = controller;

        controller.getThemeManager().addUIListener( uiListener );
        updateUI();

        controller.getProperties().set( StackDockStation.COMPONENT_FACTORY, stackDockComponentFactory.get(), Priority.THEME );
        controller.getProperties().set( StackDockStation.TAB_PLACEMENT, tabPlacement.get(), Priority.THEME );
        controller.getProperties().set( DockTheme.SPAN_FACTORY, spanFactory.get(), Priority.THEME );
        
        colorScheme.setProperties( controller );

        updateColors();
    }

    public void uninstall( DockController controller ){
        if( this.controller != controller )
            throw new IllegalArgumentException( "Trying to uninstall a controller which is not installed" );

        controller.getProperties().unset( StackDockStation.COMPONENT_FACTORY, Priority.THEME );
        controller.getProperties().unset( StackDockStation.TAB_PLACEMENT, Priority.THEME );
        controller.getProperties().unset( DockTheme.SPAN_FACTORY, Priority.THEME );
        controller.getColors().clear( Priority.THEME );
        controller.getThemeManager().removeUIListener( uiListener );

        colorScheme.setProperties( (DockProperties)null );
        
        for( DockThemeExtension extension : extensions ){
    		extension.uninstall( controller, this );
    	}
        
        this.controller = null;
    }

    /**
     * Called when the {@link LookAndFeel} changed, should update colors, fonts, ...
     */
    public void updateUI(){
    	if( selection != null ){
            SwingUtilities.updateComponentTreeUI( selection.get().getComponent() );
        }
    }

    /**
     * Called when the the colors of the {@link ColorManager} have to be updated. This method reads
     * the current {@link ColorScheme} and installs it using
     * {@link ColorManager#setScheme(Priority, bibliothek.gui.dock.util.UIScheme)} with a priority
     * of {@link Priority#CLIENT}.<br>
     * This method changed its behavior in version 1.1.0p3, subclasses no longer need to override it. All colors
     * can now be created lazily and automatically in exactly the moment when they are needed.
     */
    protected void updateColors(){
    	if( controller != null ){
    		ColorScheme scheme = colorScheme.getValue();
    		
	        if( scheme != null ){
	        	scheme = new ExtendingColorScheme( scheme, controller );
	        }
	        
	        controller.getColors().setScheme( Priority.THEME, scheme );
    	}
    }

    /**
     * Gets the currently installed controller
     * @return the controller
     */
    public DockController getController(){
        return controller;
    }

    /**
     * Sets the key that will be used to read the {@link ColorScheme} of this
     * theme from the {@link DockProperties}.
     * @param key the new key, not <code>null</code>
     * @see #setColorScheme(ColorScheme)
     */
    protected void setColorSchemeKey( PropertyKey<ColorScheme> key ){
        if( key == null )
            throw new IllegalArgumentException( "key must not be null" );

        colorScheme.setKey( key );
    }

    /**
     * Sets the currently used set of colors. The colors of all {@link DockController}s
     * will change immediately.
     * @param colorScheme the new scheme, <code>null</code> will 
     * activate the default color scheme.
     */
    public void setColorScheme( ColorScheme colorScheme ) {
        this.colorScheme.setValue( colorScheme );
    }

    /**
     * Gets the currently used color scheme
     * @return the scheme
     */
    public ColorScheme getColorScheme() {
        return colorScheme.getValue();
    }

    /**
     * Sets the factory which will be used to create components for 
     * {@link StackDockStation}. Note that this property has to be set
     * before the theme is installed. Otherwise it will take no effect.
     * @param stackDockComponentFactory the factory or <code>null</code>
     */
    public void setStackDockComponentFactory( StackDockComponentFactory stackDockComponentFactory ) {
    	setStackDockComponentFactory( stackDockComponentFactory, Priority.CLIENT );
    }
    
    /**
     * Sets the factory which will be used to create components for 
     * {@link StackDockStation}. Note that this property has to be set
     * before the theme is installed. Otherwise it will take no effect.
     * @param stackDockComponentFactory the factory or <code>null</code>
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setStackDockComponentFactory( StackDockComponentFactory stackDockComponentFactory, Priority priority ) {
        this.stackDockComponentFactory.set( priority, stackDockComponentFactory );
    }
    
    /**
     * Sets the factory which will be used to create new {@link Span}s. Note that this property
     * has to be set before the theme is installed, otherwise it will take not effect.
     * @param factory the new factory, can be <code>null</code>
     */
    public void setSpanFactory( SpanFactory factory ){
    	setSpanFactory( factory, Priority.CLIENT );
    }
    
    /**
     * Sets the factory which will be used to create new {@link Span}s. Note that this property
     * has to be set before the theme is installed. Otherwise it will take no effect.
     * @param factory the factory or <code>null</code>
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setSpanFactory( SpanFactory factory, Priority priority ){
    	this.spanFactory.set( priority, factory );
    }

    /**
     * Sets the movingImage-property. The movingImage is needed to show an
     * image when the user grabs a {@link Dockable}
     * @param movingImage the new factory
     */
    public void setMovingImageFactory( DockableMovingImageFactory movingImage ) {
    	setMovingImageFactory( movingImage, Priority.CLIENT );
    }
    
    /**
     * Sets the movingImage-property. The movingImage is needed to show an
     * image when the user grabs a {@link Dockable}
     * @param movingImage the new factory
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setMovingImageFactory( DockableMovingImageFactory movingImage, Priority priority ) {
        this.movingImage.set( priority, movingImage );
    }
    
    /**
     * Sets the {@link Combiner} of this theme. The combiner is used to
     * merge two Dockables.
     * @param combiner the combiner
     */
    public void setCombiner( Combiner combiner ) {
    	setCombiner( combiner, Priority.CLIENT );
    }
    
    /**
     * Sets the {@link Combiner} of this theme. The combiner is used to
     * merge two Dockables.
     * @param combiner the combiner
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setCombiner( Combiner combiner, Priority priority ) {
        this.combiner.set( priority, combiner );
    }
    
    /**
     * Sets the {@link StationPaint} of this theme. The paint is used to
     * draw markings on stations.
     * @param paint the paint
     */
    public void setPaint( StationPaint paint ) {
    	setPaint( paint, Priority.CLIENT );
    }
    
    /**
     * Sets the {@link StationPaint} of this theme. The paint is used to
     * draw markings on stations.
     * @param paint the paint
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setPaint( StationPaint paint, Priority priority ) {
        this.paint.set( priority, paint );
    }
    
    /**
     * Sets the {@link DisplayerFactory} of this theme. The factory is needed
     * to create {@link DockableDisplayer}.
     * @param factory the factory
     */
    public void setDisplayerFactory( DisplayerFactory factory ) {
    	setDisplayerFactory( factory, Priority.CLIENT );
    }
    
    /**
     * Sets the {@link DisplayerFactory} of this theme. The factory is needed
     * to create {@link DockableDisplayer}.
     * @param factory the factory
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setDisplayerFactory( DisplayerFactory factory, Priority priority ) {
        displayerFactory.set( priority, factory );
    }

    /**
     * Sets the {@link DockTitleFactory} of this theme. The factory is
     * used to create {@link DockTitle DockTitles} for some Dockables.
     * @param titleFactory the factory
     */
    public void setTitleFactory( DockTitleFactory titleFactory ) {
    	setTitleFactory( titleFactory, Priority.CLIENT );
    }
    
    /**
     * Sets the {@link DockTitleFactory} of this station. The factory is
     * used to create {@link DockTitle DockTitles} for some Dockables.
     * @param titleFactory the factory
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setTitleFactory( DockTitleFactory titleFactory, Priority priority ) {
        this.titleFactory.set( priority, titleFactory );
        
        if( controller != null ){
        	controller.getDockTitleManager().registerTheme( DockTitleManager.THEME_FACTORY_ID, this.titleFactory.get() );
        }
    }

    /**
     * Sets how the user can select the focused {@link Dockable}.
     * @param selection the new selector
     */
    public void setDockableSelection( DockableSelection selection ){
    	setDockableSelection( selection, Priority.CLIENT );
    }
    
    /**
     * Sets how the user can select the focused {@link Dockable}.
     * @param selection the new selector
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setDockableSelection( DockableSelection selection, Priority priority ){
        this.selection.set( priority, selection );
    }
    
    /**
     * Sets the side at which tabs are to be displayed. This method has to
     * be called before a {@link DockController} is installed, otherwise the
     * settings has no effect.
     * @param tabPlacement the side at which to show tabs, may be <code>null</code> to
     * use the default value
     */
    public void setTabPlacement( TabPlacement tabPlacement ){
    	setTabPlacement( tabPlacement, Priority.CLIENT );
    }
    
    /**
     * Sets the side at which tabs are to be displayed. This method has to
     * be called before a {@link DockController} is installed, otherwise the
     * settings has no effect.
     * @param tabPlacement the side at which to show tabs, may be <code>null</code> to
     * use the default value
     * @param priority the importance of the new setting (whether it should override existing settings or not).
     */
    public void setTabPlacement( TabPlacement tabPlacement, Priority priority ){
		this.tabPlacement.set( priority, tabPlacement );
	}
    
    /**
     * Gets the side at which tabs are displayed.
     * @return the side with the tabs, may be <code>null</code>
     */
    public TabPlacement getTabPlacement(){
		return tabPlacement.get();
	}

    public DockableMovingImageFactory getMovingImageFactory( DockController controller ) {
        return movingImage.get();
    }

    public Combiner getCombiner( DockStation station ) {
        return combiner.get();
    }

    public StationPaint getPaint( DockStation station ) {
        return paint.get();
    }

    public DisplayerFactory getDisplayFactory( DockStation station ) {
        return displayerFactory.get();
    }

    public DockTitleFactory getTitleFactory( DockController controller ) {
        return titleFactory.get();
    }

    public DockableSelection getDockableSelection( DockController controller ) {
        return selection.get();
    }
}
