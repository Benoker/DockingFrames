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
import java.util.List;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.gui.dock.util.UISchemeEvent;
import bibliothek.gui.dock.util.UISchemeListener;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.gui.dock.util.laf.LookAndFeelColorsListener;

/**
 * This abstract {@link ColorScheme} stores listeners and {@link UIProperties}. This scheme
 * also calls {@link #updateUI()} when the look and feel changed.
 * @author Benjamin Sigg
 */
public abstract class AbstractColorScheme implements ColorScheme{
	/** all the listeners of this scheme */
    private List<UISchemeListener<Color, DockColor, ColorBridge>> listeners = new ArrayList<UISchemeListener<Color,DockColor,ColorBridge>>();
    
    /** all the managers using this scheme */
    private List<ColorManager> managers = new ArrayList<ColorManager>(); 
    
    /** calls {@link #updateUI()} if the look and feel changed */
    private LookAndFeelColorsListener updateUiListener = new LookAndFeelColorsListener(){
		public void colorsChanged(){
			try{
				for( ColorManager manager : managers ){
					manager.lockUpdate();
				}
				updateUI();
			}
			finally{
				for( ColorManager manager : managers ){
					manager.unlockUpdate();
				}
			}
		}
		
		public void colorChanged( String key ){
			colorsChanged();
		}
	};
    
    public void addListener( UISchemeListener<Color, DockColor, ColorBridge> listener ){
    	if( listener == null ){
    		throw new IllegalArgumentException( "listener must not be null" );
    	}
    	boolean ui = shouldListenUI();
    	listeners.add( listener );
    	if( !ui && shouldListenUI() ){
    		DockUI.getDefaultDockUI().addLookAndFeelColorsListener( updateUiListener );
    		updateUI();
    	}
    }
    
    public void removeListener( UISchemeListener<Color, DockColor, ColorBridge> listener ){
    	boolean ui = shouldListenUI();
    	listeners.remove( listener );
    	if( ui && !shouldListenUI() ){
    		DockUI.getDefaultDockUI().removeLookAndFeelColorsListener( updateUiListener );
    	}
    }
    
    /**
     * Gets all the listeners that are currently known to this scheme.
     * @return all the listeners
     */
    @SuppressWarnings("unchecked")
	protected UISchemeListener<Color, DockColor, ColorBridge>[] listeners(){
    	return listeners.toArray( new UISchemeListener[ listeners.size() ] );
    }
    
    /**
     * Tells whether this scheme has listeners attached or not.
     * @return <code>true</code> if there is at least one listener attached
     */
    protected boolean hasListeners(){
    	return listeners.size() > 0;
    }
    
    /**
     * Calls {@link UISchemeListener#changed(UISchemeEvent)} on all currently registered listeners.
     * @param event the event to fire
     */
    protected void fire( UISchemeEvent<Color, DockColor, ColorBridge> event ){
    	for( UISchemeListener<Color, DockColor, ColorBridge> listener : listeners() ){
    		listener.changed( event );
    	}
    }
    
    /**
     * Gets all the {@link ColorManager}s that are currently installed on this scheme.
     * @return all the managers
     */
    protected ColorManager[] managers(){
		return managers.toArray( new ColorManager[ managers.size() ] );
	}
    
    public void install( UIProperties<Color, DockColor, ColorBridge> properties ){
    	boolean ui = shouldListenUI();
    	managers.add( (ColorManager)properties );
    	if( !ui && shouldListenUI() ){
    		DockUI.getDefaultDockUI().addLookAndFeelColorsListener( updateUiListener );
    		updateUI();
    	}
    }
    
    public void uninstall( UIProperties<Color, DockColor, ColorBridge> properties ){
    	boolean ui = shouldListenUI();
    	managers.remove( (ColorManager)properties );
    	if( ui && !shouldListenUI() ){
    		DockUI.getDefaultDockUI().removeLookAndFeelColorsListener( updateUiListener );
    	}
    }
    
    private boolean shouldListenUI(){
    	return managers.size() > 0 || listeners.size() > 0;
    }
    
    /**
     * Called when the look and feel changed. Subclasses may override this method and update
     * colors if necessary.
     */
    protected abstract void updateUI();
}
