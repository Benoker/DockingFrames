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
package bibliothek.gui.dock.common.intern.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * A {@link DockTheme} that wraps another theme and works within
 * the special environment the common-project provides.
 * @author Benjamin Sigg
 */
public class CDockTheme<D extends DockTheme> implements DockTheme {
    /** the original theme */
    private D theme;
    /** the theme to which all work is delegated */
    private DockTheme delegate;
    
    /** the factories used in this theme */
    private Map<Path, ColorBridgeFactory> colorBridgeFactories =
        new HashMap<Path, ColorBridgeFactory>();
    
    /** the settings of all {@link DockController}s */
    private List<Controller> controllers = new ArrayList<Controller>();
    
    /**
     * Creates a new theme 
     * @param delegate the theme to which all work is delegated
     */
    public CDockTheme( D delegate ){
        this( delegate, delegate );
    }
    
    /**
     * Creates a new theme.
     * @param theme the theme which is represented by this {@link CDockTheme}.
     * @param delegate the theme to which all work is delegated
     */
    public CDockTheme( D theme, DockTheme delegate ){
        if( theme == null )
            throw new IllegalArgumentException( "theme must not be null" );
        if( delegate == null )
            throw new IllegalArgumentException( "delegate must not be null" );
        
        this.theme = theme;
        this.delegate = delegate;
    }
    
    /**
     * Gets the internal representation of this theme.
     * @return the internal representation
     */
    public D intern(){
        return theme;
    }
    
    public Combiner getCombiner( DockStation station ) {
        return delegate.getCombiner( station );
    }

    public DisplayerFactory getDisplayFactory( DockStation station ) {
        return delegate.getDisplayFactory( station );
    }

    public DockableMovingImageFactory getMovingImageFactory( DockController controller ) {
        return delegate.getMovingImageFactory( controller );
    }

    public StationPaint getPaint( DockStation station ) {
        return delegate.getPaint( station );
    }

    public DockTitleFactory getTitleFactory( DockController controller ) {
        return delegate.getTitleFactory( controller );
    }

    public DockableSelection getDockableSelection( DockController controller ) {
        return delegate.getDockableSelection( controller );
    }
    
    /**
     * Sets the {@link ColorBridge} which should be used for a certain kind
     * of {@link DockColor}s. The bridges will be installed with priority
     * {@link Priority#DEFAULT} at all {@link ColorManager}s.
     * @param kind the kind of {@link DockColor} the providers will handle
     * @param factory the factory for new providers
     */
    public <C extends DockColor> void putColorBridgeFactory( Path kind, ColorBridgeFactory factory ){
        colorBridgeFactories.put( kind, factory );
        for( Controller setting : controllers ){
            ColorManager colors = setting.controller.getColors();
            
            ColorBridge oldBridge = setting.bridges.remove( kind );
            ColorBridge newBridge = factory == null ? null : factory.create( colors );
            
            if( newBridge == null ){
                setting.bridges.remove( kind );
                
                if( oldBridge != null )
                    colors.unpublish( Priority.DEFAULT, kind );
            }
            else{
                setting.bridges.put( kind, newBridge );
                colors.publish( Priority.DEFAULT, kind, newBridge );
            }
        }
    }

    public void install( DockController controller ) {
        delegate.install( controller );
        
        Controller settings = new Controller();
        settings.controller = controller;
        
        ColorManager colors = controller.getColors();
        for( Map.Entry<Path, ColorBridgeFactory> entry : colorBridgeFactories.entrySet() ){
            ColorBridge bridge = entry.getValue().create( colors );
            colors.publish( 
                    Priority.DEFAULT, 
                    entry.getKey(), 
                    bridge );
            settings.bridges.put( entry.getKey(), bridge );
        }
        controllers.add( settings );
    }

    public void uninstall( DockController controller ) {
        delegate.uninstall( controller );
        
        for( int i = 0, n = controllers.size(); i<n; i++ ){
            Controller settings = controllers.get( i );
            if( settings.controller == controller ){
                controllers.remove( i );
                
                ColorManager colors = controller.getColors();
                for( ColorBridge bridge : settings.bridges.values() ){
                    colors.unpublish( Priority.DEFAULT, bridge );
                }
            }
        }
    }
    
    /**
     * A structure containing settings for a specific {@link DockController}.
     * @author Benjamin Sigg
     */
    private class Controller{
        /** the controller which is represented by this element */
        public DockController controller;
        /** the list of {@link ColorBridge}s which is used in that controller */
        public Map<Path, ColorBridge> bridges = new HashMap<Path, ColorBridge>();
    }
}