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
package bibliothek.gui.dock.common.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.font.ButtonFontTransmitter;
import bibliothek.gui.dock.common.intern.font.FontBridgeFactory;
import bibliothek.gui.dock.common.intern.font.TabFontTransmitter;
import bibliothek.gui.dock.common.intern.font.TitleFontTransmitter;
import bibliothek.gui.dock.common.theme.color.CColorBridge;
import bibliothek.gui.dock.common.theme.color.CColorBridgeExtension;
import bibliothek.gui.dock.common.theme.color.ExtendedColorBridge;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.themes.DockThemeExtension;
import bibliothek.gui.dock.themes.font.TabFont;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.gui.dock.util.font.FontBridge;
import bibliothek.gui.dock.util.font.FontManager;
import bibliothek.util.Path;

/**
 * A {@link DockTheme} that wraps another theme and works within
 * the special environment the common-project provides.
 * @author Benjamin Sigg
 * @param <D> the kind of theme that is wrapped by this {@link CDockTheme}
 */
public class CDockTheme<D extends DockTheme> implements DockTheme {
    /** the original theme */
    private D theme;
    /** the theme to which all work is delegated */
    private DockTheme delegate;
    
    /** the factories for colors used in this theme */
    private Map<Path, ColorBridgeFactory> colorBridgeFactories =
        new HashMap<Path, ColorBridgeFactory>();
    
    /** the factories for fonts used in this theme */
    private Map<Path, FontBridgeFactory> fontBridgeFactories =
        new HashMap<Path, FontBridgeFactory>();
    
    /** the settings of all {@link DockController}s */
    private List<Controller> controllers = new ArrayList<Controller>();
    
    /** extensions associated with this theme */
    private DockThemeExtension[] extensions;
    
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
     * Adds the default {@link FontBridgeFactory}s to this theme.
     * @param control the owner of this theme
     */
    protected void initDefaultFontBridges( final CControl control ){        
        putFontBridgeFactory( TitleFont.KIND_TITLE_FONT, new FontBridgeFactory(){
            public FontBridge create( FontManager manager ) {
                TitleFontTransmitter transmitter = new TitleFontTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
        putFontBridgeFactory( TitleFont.KIND_FLAP_BUTTON_FONT, new FontBridgeFactory(){
            public FontBridge create( FontManager manager ) {
                ButtonFontTransmitter transmitter = new ButtonFontTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
        putFontBridgeFactory( TabFont.KIND_TAB_FONT, new FontBridgeFactory(){
            public FontBridge create( FontManager manager ) {
                TabFontTransmitter transmitter = new TabFontTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
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
     * @param kind the kind of {@link DockColor} the bridges will handle
     * @param factory the factory for new bridges, can be <code>null</code>
     */
    public void putColorBridgeFactory( Path kind, ColorBridgeFactory factory ){
        colorBridgeFactories.put( kind, factory );
        for( Controller setting : controllers ){
            ColorManager colors = setting.controller.getColors();
            
            ColorBridge oldBridge = setting.colors.remove( kind );
            ColorBridge newBridge = factory == null ? null : factory.create( colors );
            
            if( newBridge == null ){
                setting.colors.remove( kind );
                
                if( oldBridge != null )
                    colors.unpublish( Priority.DEFAULT, kind );
            }
            else{
                setting.colors.put( kind, newBridge );
                colors.publish( Priority.DEFAULT, kind, newBridge );
            }
        }
    }
    
    /**
     * Sets the {@link FontBridge} which should be used for a certain kind
     * of {@link DockFont}s. The bridges will be installed with priority
     * {@link Priority#DEFAULT} at all {@link FontManager}s.
     * @param kind the kind of {@link DockFont} the bridges will handle
     * @param factory the factory for new bridges, can be <code>null</code>
     */
    public void putFontBridgeFactory( Path kind, FontBridgeFactory factory ){
        fontBridgeFactories.put( kind, factory );
        for( Controller setting : controllers ){
            FontManager fonts = setting.controller.getFonts();
            
            FontBridge oldBridge = setting.fonts.remove( kind );
            FontBridge newBridge = factory == null ? null : factory.create( fonts );
            
            if( newBridge == null ){
                setting.fonts.remove( kind );
                
                if( oldBridge != null ){
                    fonts.unpublish( Priority.DEFAULT, kind );
                }
            }
            else{
                setting.fonts.put( kind, newBridge );
                fonts.publish( Priority.DEFAULT, kind, newBridge );
            }
        }
    }

    public void install( DockController controller, DockThemeExtension[] extensions ){
    	if( this.extensions != null ){
    		throw new IllegalStateException( "theme is already in use" );
    	}
    	
    	this.extensions = extensions;
    	
    	for( DockThemeExtension extension : extensions ){
    		extension.install( controller, this );
    	}
    	
        delegate.install( controller, extensions );
        install( controller );
        
        for( DockThemeExtension extension : extensions ){
    		extension.installed( controller, this );
    	}
    }
    
    /**
     * Installs this theme at <code>controller</code>.
     * @param controller the new owner of this theme
     */
    protected void install( DockController controller ){    
        Controller settings = new Controller();
        settings.controller = controller;
        
        ColorManager colors = controller.getColors();
        CControl control = controller.getProperties().get( CControl.CCONTROL );
        
        try{
            colors.lockUpdate();
            
            ExtensionName<CColorBridgeExtension> name = new ExtensionName<CColorBridgeExtension>( 
            		CColorBridgeExtension.EXTENSION_NAME, CColorBridgeExtension.class, CColorBridgeExtension.PARAMETER_NAME, this );
            List<CColorBridgeExtension> extensions = controller.getExtensions().load( name );
            
            for( Map.Entry<Path, ColorBridgeFactory> entry : colorBridgeFactories.entrySet() ){
                ColorBridge bridge = entry.getValue().create( colors );
                Path key = entry.getKey();
                
                List<CColorBridgeExtension> filtered = new ArrayList<CColorBridgeExtension>();
                for( CColorBridgeExtension extension : extensions ){
                	if( key.equals( extension.getKey() )){
                		filtered.add( extension );
                	}
                }

                if( !filtered.isEmpty() ){
                	CColorBridge[] extending = new CColorBridge[ filtered.size() ];
                	for( int i = 0; i < extending.length; i++ ){
                		extending[i] = filtered.get( i ).create( control, colors );
                	}
                	bridge = new ExtendedColorBridge( bridge, extending );
                }
                
                colors.publish( 
                        Priority.DEFAULT, 
                        entry.getKey(), 
                        bridge );
                settings.colors.put( entry.getKey(), bridge );
            }
        }
        finally{
            colors.unlockUpdate();
        }
        
        FontManager fonts = controller.getFonts();
        try{
            fonts.lockUpdate();
            for( Map.Entry<Path, FontBridgeFactory> entry : fontBridgeFactories.entrySet() ){
                FontBridge bridge = entry.getValue().create( fonts );
                fonts.publish( Priority.DEFAULT, entry.getKey(), bridge );
                settings.fonts.put( entry.getKey(), bridge );
            }
        }
        finally{
            fonts.unlockUpdate();
        }
        
        controllers.add( settings );
    }

    public void uninstall( DockController controller ) {
        delegate.uninstall( controller );
        
        for( int i = 0, n = controllers.size(); i<n; i++ ){
            Controller settings = controllers.get( i );
            if( settings.controller == controller ){
                controllers.remove( i-- );
                n--;
                
                ColorManager colors = controller.getColors();
                for( ColorBridge bridge : settings.colors.values() ){
                    colors.unpublish( Priority.DEFAULT, bridge );
                }
                
                FontManager fonts = controller.getFonts();
                for( FontBridge bridge : settings.fonts.values() ){
                    fonts.unpublish( Priority.DEFAULT, bridge );
                }
            }
        }
        
        for( DockThemeExtension extension : extensions ){
        	extension.uninstall( controller, this );
        }
        
        this.extensions = null;
    }
    
    /**
     * A structure containing settings for a specific {@link DockController}.
     * @author Benjamin Sigg
     */
    private class Controller{
        /** the controller which is represented by this element */
        public DockController controller;
        /** the list of {@link ColorBridge}s which is used in that controller */
        public Map<Path, ColorBridge> colors = new HashMap<Path, ColorBridge>();
        /** the list of {@link FontBridge}s which are used in that controller */
        public Map<Path, FontBridge> fonts = new HashMap<Path, FontBridge>();
    }
}