/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.themes.color;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.gui.dock.util.UIScheme;
import bibliothek.gui.dock.util.UISchemeEvent;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Path;

/**
 * The default implementation of {@link ColorScheme} just uses some maps
 * to store its colors.
 * <b>Warning:</b> this class will be replaced in version 1.1.0
 * @author Benjamin Sigg
 */
public class DefaultColorScheme extends AbstractColorScheme{
    private Map<String, Color> colors = new HashMap<String, Color>();
    private Map<Path, ColorBridgeFactory> bridges = new HashMap<Path, ColorBridgeFactory>();
 
    public ColorBridge getBridge( Path name, UIProperties<Color, DockColor, ColorBridge> properties ){
    	ColorBridgeFactory factory = getBridgeFactory( name );
    	if( factory == null ){
    		return null;
    	}
    	return factory.create( (ColorManager)properties );
    }
    
    @Override
    protected void updateUI(){
    	// ignore
    }
    
    /**
     * Sets how to modify some <code>kind</code> of {@link DockColor}s.
     * @param kind the kind of {@link DockColor}s the bridge works with
     * @param bridge the factory for modifications or <code>null</code>
     */
    public void setBridgeFactory( final Path kind, ColorBridgeFactory bridge ){
        if( bridge == null ){
            bridges.remove( kind );
        }
        else{
            bridges.put( kind, bridge );
        }
        

        UISchemeEvent<Color, DockColor, ColorBridge> event = new UISchemeEvent<Color, DockColor, ColorBridge>(){
			public UIScheme<Color, DockColor, ColorBridge> getScheme(){
				return DefaultColorScheme.this;
			}
			
			public Collection<String> changedResources( Set<String> names ){
				return Collections.emptySet();
			}
			
			public Collection<Path> changedBridges( Set<Path> names ){
				List<Path> result = new ArrayList<Path>();
				for( Path name : names ){
					if( name.startsWith( kind )){
						result.add( name );
					}
				}
				return result;
			}
		};
		fire( event );
    }
    
    /**
     * Sets the value of some color.
     * @param id the identifier of the color
     * @param color the color or <code>null</code>
     * @see #setNullColor(String)
     */
    public void setColor( final String id, Color color ){
    	synchronized( colors ){
	        if( color == null ){
	            colors.remove( id );
	        }
	        else{
	            colors.put( id, color );
	        }
    	}
    	fire( id );
    }
    
    /**
     * Sets the value of some color explicitly to <code>null</code>, this is not the same as calling
     * {@link #setColor(String, Color)}: <code>setColor</code> removes the entry, this method keeps the entry
     * but sets it to <code>null</code>.
     * @param id the identifier of the color to set explicitly to <code>null</code>
     */
    public void setNullColor( String id ){
    	synchronized( colors ){
    		colors.put( id, null );
    	}
    	fire( id );
    }
    
    private void fire( final String id ){
        UISchemeEvent<Color, DockColor, ColorBridge> event = new UISchemeEvent<Color, DockColor, ColorBridge>(){
			public UIScheme<Color, DockColor, ColorBridge> getScheme(){
				return DefaultColorScheme.this;
			}
			
			public Collection<String> changedResources( Set<String> names ){
				if( names == null ){
					return null;
				}
				
				List<String> result = new ArrayList<String>();
				for( String name : names ){
					if( name.startsWith( id )){
						result.add( id );
					}
				}
				
				return result;
			}
			
			public Collection<Path> changedBridges( Set<Path> names ){
				return Collections.emptySet();
			}
		};
		
		fire( event );
    }
 
    public Color getResource( String name, UIProperties<Color, DockColor, ColorBridge> properties ){
	    return getColor( name );
    }
    
    /**
     * Gets the color that best matches the identifier <code>id</code>.
     * @param id some identifier
     * @return a color that matches id or <code>null</code>, the color may not be stored with the exact identifier <code>id</code>
     */
    public Color getColor( String id ) {
    	Color color;
    	synchronized( colors ){
	        color = colors.get( id );
	        if( color != null || colors.containsKey( id ) )
	            return color;
	        
	        int best = -1;
	        for( Map.Entry<String, Color> entry : colors.entrySet() ){
	            if( id.startsWith( entry.getKey() )){
	                if( entry.getKey().length() > best ){
	                    best = entry.getKey().length();
	                    color = entry.getValue();
	                }
	            }
	        }
    	}
        
        return color;
    }

    /**
     * Gets the factory that creates bridges for <code>kind</code>.
     * @param kind some identifier for a type
     * @return the factory whose kind best matches <code>kind</code> or <code>null</code>
     */
    public ColorBridgeFactory getBridgeFactory( Path kind ) {
        while( kind != null ){
            ColorBridgeFactory factory = bridges.get( kind );
            if( factory != null )
                return factory;
            
            kind = kind.getParent();
        }
        
        return null;
    }
}
