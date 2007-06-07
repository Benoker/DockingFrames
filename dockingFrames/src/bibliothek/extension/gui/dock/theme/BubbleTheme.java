/**
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

package bibliothek.extension.gui.dock.theme;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.bubble.BubbleDisplayerFactory;
import bibliothek.extension.gui.dock.theme.bubble.BubbleDockTitleFactory;
import bibliothek.extension.gui.dock.theme.bubble.BubbleFlapDockButtonTitleFactory;
import bibliothek.extension.gui.dock.theme.bubble.BubbleStackDockComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.stack.DefaultStackDockComponent;
import bibliothek.gui.dock.themes.DefaultTheme;
import bibliothek.gui.dock.themes.ThemeProperties;

/**
 * A theme using a lot of eye-candy.
 * @author Benjamin Sigg
 */
@ThemeProperties(
		authors = { "Ivan Seidl", "Benjamin Sigg" }, 
		descriptionBundle = "theme.bubble.description", 
		nameBundle = "theme.bubble", 
		webpages = { "" }  )
public class BubbleTheme extends DefaultTheme {
	/** The colors used by this theme */
    private Map<String, Color> colors = new HashMap<String, Color>();
    
    /** A listener to the {@link DockController} */
	private Listener listener = new Listener();
	
    public BubbleTheme(){
        colors.put( "tab.border.active",            new Color( 150, 0, 0 ) );
        colors.put( "tab.border.active.mouse",      new Color( 200, 100, 100 ) );
        colors.put( "tab.border.inactive",          new Color( 100, 100, 100 ) );
        colors.put( "tab.border.inactive.mouse",    new Color( 100, 175, 100 ) );
        colors.put( "tab.top.active",               new Color( 200, 0, 0 ) );
        colors.put( "tab.top.active.mouse",         new Color( 255, 100, 100 ) );
        colors.put( "tab.top.inactive",             new Color( 150, 150, 150 ) );
        colors.put( "tab.top.inactive.mouse",       new Color( 150, 255, 150 ) );
        colors.put( "tab.bottom.active",            new Color( 255, 100, 100 ) );
        colors.put( "tab.bottom.active.mouse",      new Color( 255, 200, 200 ) );
        colors.put( "tab.bottom.inactive",          new Color( 200, 200, 200 ) );
        colors.put( "tab.bottom.inactive.mouse",    new Color( 220, 255, 220 ) );
        colors.put( "tab.text.active",              new Color( 0, 0, 0 ));
        colors.put( "tab.text.active.mouse",        new Color( 0, 0, 0 ));
        colors.put( "tab.text.inactive",            new Color( 100, 100, 100 ));
        colors.put( "tab.text.inactive.mouse",      new Color( 25, 25, 25 ));
        
        colors.put( "title.top.active",               new Color( 200, 0, 0 ) );
        colors.put( "title.top.active.mouse",         new Color( 255, 100, 100 ) );
        colors.put( "title.top.inactive",             new Color( 150, 150, 150 ) );
        colors.put( "title.top.inactive.mouse",       new Color( 150, 255, 150 ) );
        colors.put( "title.bottom.active",            new Color( 255, 100, 100 ) );
        colors.put( "title.bottom.active.mouse",      new Color( 255, 200, 200 ) );
        colors.put( "title.bottom.inactive",          new Color( 200, 200, 200 ) );
        colors.put( "title.bottom.inactive.mouse",    new Color( 220, 255, 220 ) );
        colors.put( "title.text.active",              new Color( 0, 0, 0 ));
        colors.put( "title.text.active.mouse",        new Color( 0, 0, 0 ));
        colors.put( "title.text.inactive",            new Color( 100, 100, 100 ));
        colors.put( "title.text.inactive.mouse",      new Color( 25, 25, 25 ));
        
        colors.put( "border.high.active",           new Color( 255, 100, 100 ));
        colors.put( "border.high.inactive",         new Color( 200, 200, 200 ));
        colors.put( "border.low.active",            new Color( 200, 100, 100 ));
        colors.put( "border.low.inactive",          new Color( 100, 100, 100 ));
        
        setDisplayerFactory( new BubbleDisplayerFactory( this ));
        setTitleFactory( new BubbleDockTitleFactory( this ));
    }
    
    /**
     * Gets a color for a specified key.
     * @param key the key of the color
     * @return the color or <code>null</code>
     */
    public Color getColor( String key ){
        return colors.get( key );
    }
    
    /**
     * Stores a color which will be used in the theme.
     * @param key the key of the color
     * @param color the color to store
     */
    public void setColor( String key, Color color ){
    	colors.put( key, color );
    }
    
	@Override
	public void install( DockController controller ){
		super.install( controller );
        
		// Exchange the DockComponents
		for( int i = 0, n = controller.getStationCount(); i<n; i++ ){
        	DockStation station = controller.getStation(i);
        	if( station instanceof StackDockStation ){
        		StackDockStation stack = (StackDockStation)station;
        		if( !(stack.getStackComponent() instanceof BubbleStackDockComponent) )
        			stack.setStackComponent( new BubbleStackDockComponent( this ) );
        	}
        }
		
        // set new titles
        controller.getDockTitleManager().registerTheme( 
                FlapDockStation.BUTTON_TITLE_ID, 
                new BubbleFlapDockButtonTitleFactory( this ));
        
		controller.addDockControllerListener( listener );
        
        Map<String,Icon> icons = loadIcons();
        for( Map.Entry<String, Icon> icon : icons.entrySet() ){
            controller.getIcons().setIconTheme( icon.getKey(), icon.getValue() );
        }
	}
    
    protected Map<String, Icon> loadIcons(){
        return new HashMap<String, Icon>();
    }
	
	@Override
	public void uninstall( DockController controller ){
		super.uninstall( controller );
		
		controller.removeDockControllerListener( listener );
		
    	// Exchange the DockComponents
        for( int i = 0, n = controller.getStationCount(); i<n; i++ ){
        	DockStation station = controller.getStation(i);
        	if( station instanceof StackDockStation ){
        		StackDockStation stack = (StackDockStation)station;
        		if( stack.getStackComponent() instanceof BubbleStackDockComponent )
        			stack.setStackComponent( new DefaultStackDockComponent() );
        	}
        }
        
        controller.getDockTitleManager().clearThemeFactories();
	}
	
    /**
     * A listener to the Controller
     * @author Benjamin Sigg
     */
    private class Listener extends DockControllerAdapter{
		@Override
		public void dockableRegistered(DockController controller, Dockable dockable) {
			if( dockable instanceof StackDockStation ){
				StackDockStation stack = (StackDockStation)dockable;
				if( !(stack.getStackComponent() instanceof BubbleStackDockComponent) ){
					stack.setStackComponent( new BubbleStackDockComponent( BubbleTheme.this ) );
				}
			}
		}
    }
}
