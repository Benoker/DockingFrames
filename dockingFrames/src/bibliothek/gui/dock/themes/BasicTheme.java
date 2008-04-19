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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import bibliothek.gui.*;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.themes.basic.*;
import bibliothek.gui.dock.themes.color.*;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.ColorProvider;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;
import bibliothek.gui.dock.util.laf.LookAndFeelColorsListener;

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
    private Combiner combiner;
    
    /** paints on stations */
    private StationPaint paint;
    
    /** creates panels for Dockables */
    private DisplayerFactory displayerFactory;
    
    /** creates titles Dockables */
    private DockTitleFactory titleFactory;
    
    /** selects the image which should be displayed when moving a dockable*/
    private DockableMovingImageFactory movingImage;
    
    /** the factory used to create components for {@link StackDockStation} */
    private StackDockComponentFactory stackDockComponentFactory;
    
    /** the colors of this theme */
    private ColorScheme colorScheme;
    
    /** how to select a new focused dockable */
    private DockableSelection selection;
    
    /** the controllers which are installed */
    private List<DockController> controllers = new ArrayList<DockController>();
    
    /** a listener waiting for changed {@link LookAndFeel}s */
    private UIListener uiListener = new UIListener(){
        public void updateUI( DockController controller ){
            BasicTheme.this.updateUI();
        }
    };
    
    /** a listener waiting for changes in the {@link LookAndFeelColors} */
    private LookAndFeelColorsListener colorListener = new LookAndFeelColorsListener(){
        public void colorChanged( String key ) {
            colorsChanged();
        }
        public void colorsChanged() {
            if( colorScheme.updateUI() )
                updateColors( getControllers() );
        }
    };
    
    /**
     * Creates a new <code>BasicTheme</code>.
     */
    public BasicTheme() {
        setCombiner( new BasicCombiner() );
        setPaint( new BasicStationPaint() );
        setDisplayerFactory( new BasicDisplayerFactory() );
        setTitleFactory( new BasicDockTitleFactory() );
        setMovingImageFactory( new BasicMovingImageFactory() );
        setStackDockComponentFactory( new StackDockComponentFactory(){
            public StackDockComponent create( StackDockStation station ) {
                return new BasicStackDockComponent( station );
            }
        });
        setDockableSelection( new BasicDockableSelection() );
        setColorScheme( new BasicColorScheme() );
    }
    
    public void install( DockController controller ) {
        if( controllers.isEmpty() ){
            controller.addUIListener( uiListener );
            DockUI.getDefaultDockUI().addLookAndFeelColorsListener( colorListener );
            updateUI();
        }
        controllers.add( controller );
        controller.getProperties().set( StackDockStation.COMPONENT_FACTORY, stackDockComponentFactory );
        updateColors( new DockController[]{ controller } );
    }
    
    public void uninstall(DockController controller) {
        controller.getProperties().set( StackDockStation.COMPONENT_FACTORY, null );
        controller.getColors().clear( Priority.THEME );
        
        if( controllers.get( 0 ) == controller ){
            controller.removeUIListener( uiListener );
            controllers.remove( 0 );
            if( !controllers.isEmpty() )
                controllers.get( 0 ).addUIListener( uiListener );
        }
        else{
            controllers.remove( controller );
        }
        
        if( controllers.isEmpty() ){
            DockUI.getDefaultDockUI().removeLookAndFeelColorsListener( colorListener );
        }
    }
    
    /**
     * Called when the {@link LookAndFeel} changed, should update colors, fonts, ...
     */
    public void updateUI(){
        if( colorScheme.updateUI() ){
            updateColors( getControllers() );
        }
        if( selection != null ){
            SwingUtilities.updateComponentTreeUI( selection.getComponent() );
        }
    }
    
    /**
     * Called when the the colors of the {@link ColorManager} have to be updated.
     * Subclasses should override this method and explicitly call
     * {@link #updateColor(DockController[], String, Color) updateColor} and
     * {@link #updateColorProvider(DockController[], Class) updateColorProvider}
     * for all {@link Color}s and {@link ColorProvider}s that will be used by
     * this theme. Since {@link ColorScheme}s can create new colors and providers 
     * lazily, just reading out all colors will ensure that all colors 
     * and providers exists and are registered at the {@link ColorManager}s.
     * @param controllers the set of controllers whose colors must be updated.
     */
    protected void updateColors( DockController[] controllers ){
        for( DockController controller : controllers ){
            controller.getColors().lockUpdate();
            controller.getColors().clear( Priority.THEME );
        }
        
        for( DockController controller : controllers )
            colorScheme.transmitAll( Priority.THEME, controller.getColors() );
        
        updateColor( controllers, "title.active.left", null );
        updateColor( controllers, "title.inactive.left", null );
        updateColor( controllers, "title.active.right", null );
        updateColor( controllers, "title.inactive.right", null );
        updateColor( controllers, "title.active.text", null );
        updateColor( controllers, "title.inactive.text", null );
        
        updateColor( controllers, "stack.tab.foreground", null );
        updateColor( controllers, "stack.tab.foreground.selected", null );
        updateColor( controllers, "stack.tab.foreground.focused", null );
        updateColor( controllers, "stack.tab.background", null );
        updateColor( controllers, "stack.tab.background.selected", null );
        updateColor( controllers, "stack.tab.background.focused", null );
        
        updateColor( controllers, "paint.line", null );
        updateColor( controllers, "paint.divider", null );
        updateColor( controllers, "paint.insertion", null );
        
        updateColorProvider( controllers, DockColor.class );
        updateColorProvider( controllers, TabColor.class );
        updateColorProvider( controllers, TitleColor.class );
        updateColorProvider( controllers, ActionColor.class );
        updateColorProvider( controllers, DisplayerColor.class );
        updateColorProvider( controllers, StationPaintColor.class );
        updateColorProvider( controllers, DockableSelectionColor.class );
        
        for( DockController controller : controllers )
            controller.getColors().unlockUpdate();
    }
    
    /**
     * Changes the color of all {@link ColorManager}s to the color obtained
     * through the {@link ColorScheme} or to <code>backup</code> if the scheme
     * returns a <code>null</code> value.
     * @param controllers the set of affected controllers
     * @param id the id of the new color
     * @param backup backup color in case that the scheme does not
     * know what to use
     */
    protected void updateColor( DockController[] controllers, String id, Color backup ){
        Color color = colorScheme.getColor( id );
        if( color == null )
            color = backup;
        
        for( DockController controller : controllers ){
            controller.getColors().put( Priority.THEME, id, color );
        }
    }
    
    /**
     * Publishes to {@link ColorProvider} for <code>kind</code> on all <code>controllers</code>.
     * @param controllers the set of affected controllers
     * @param kind the kind of provider that should be published
     */
    @SuppressWarnings("unchecked")
    protected void updateColorProvider( DockController[] controllers, Class<? extends DockColor> kind ){
        ColorProvider<DockColor> provider = (ColorProvider<DockColor>)colorScheme.getProvider( kind );
        if( provider != null ){
            for( DockController controller : controllers )
                controller.getColors().publish( Priority.THEME, kind, provider );
        }
    }
    
    /**
     * Gets a list of all {@link DockController} which are currently installed
     * with this theme.
     * @return the list of controllers
     */
    public DockController[] getControllers(){
        return controllers.toArray( new DockController[ controllers.size() ] );
    }
    
    /**
     * Sets the currently used set of colors. The colors of all {@link DockController}s
     * will change immediately.
     * @param colorScheme the new scheme
     */
    public void setColorScheme( ColorScheme colorScheme ) {
        if( colorScheme == null )
            throw new IllegalArgumentException( "The scheme must not be null" );
        
        this.colorScheme = colorScheme;
        updateColors( getControllers() );
    }
    
    /**
     * Gets the currently used color scheme
     * @return the scheme
     */
    public ColorScheme getColorScheme() {
        return colorScheme;
    }
    
    /**
     * Sets the factory which will be used to create components for 
     * {@link StackDockStation}. Note that this property has to be set
     * before the theme is installed. Otherwise it will take no effect.
     * @param stackDockComponentFactory the factory or <code>null</code>
     */
    public void setStackDockComponentFactory(
            StackDockComponentFactory stackDockComponentFactory ) {
        this.stackDockComponentFactory = stackDockComponentFactory;
    }
    
    /**
     * Sets the movingImage-property. The movignImage is needed to show an
     * image when the user grabs a {@link Dockable}
     * @param movingImage the new factory, not <code>null</code>
     */
    public void setMovingImageFactory( DockableMovingImageFactory movingImage ) {
        if( movingImage == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.movingImage = movingImage;
    }
    
    /**
     * Sets the {@link Combiner} of this theme. The combiner is used to
     * merge two Dockables.
     * @param combiner the combiner, not <code>null</code>
     */
    public void setCombiner( Combiner combiner ) {
        if( combiner == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.combiner = combiner;
    }
    
    /**
     * Sets the {@link StationPaint} of this theme. The paint is used to
     * draw markings on stations.
     * @param paint the paint, not <code>null</code>
     */
    public void setPaint( StationPaint paint ) {
        if( paint == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.paint = paint;
    }
    
    /**
     * Sets the {@link DisplayerFactory} of this theme. The factory is needed
     * to create {@link DockableDisplayer}.
     * @param factory the factory, not <code>null</code>
     */
    public void setDisplayerFactory( DisplayerFactory factory ) {
        if( factory == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        displayerFactory = factory;
    }
    
    /**
     * Sets the {@link DockTitleFactory} of this station. The factory is
     * used to create {@link DockTitle DockTitles} for some Dockables.
     * @param titleFactory the factory, not <code>null</code>
     */
    public void setTitleFactory( DockTitleFactory titleFactory ) {
        if( titleFactory == null )
            throw new IllegalArgumentException( "argument must not be null" );
        
        this.titleFactory = titleFactory;
    }
    
    /**
     * Sets how the user can select the focused {@link Dockable}.
     * @param selection the new selector, not <code>null</code>
     */
    public void setDockableSelection( DockableSelection selection ){
        if( selection == null )
            throw new IllegalArgumentException( "selection must not be null" );
        
        this.selection = selection;
    }
    
    public DockableMovingImageFactory getMovingImageFactory( DockController controller ) {
        return movingImage;
    }
    
    public Combiner getCombiner( DockStation station ) {
        return combiner;
    }

    public StationPaint getPaint( DockStation station ) {
        return paint;
    }

    public DisplayerFactory getDisplayFactory( DockStation station ) {
        return displayerFactory;
    }
    
    public DockTitleFactory getTitleFactory( DockController controller ) {
        return titleFactory;
    }
    
    public DockableSelection getDockableSelection( DockController controller ) {
        return selection;
    }
}
