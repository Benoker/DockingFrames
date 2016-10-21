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
package bibliothek.gui.dock.util.laf;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;

/**
 * The set of colors used when no specialized set has been specified.
 * @author Benjamin Sigg
 */
public class DefaultLookAndFeelColors extends AbstractLookAndFeelColors {
    private Map<String, String> translations = new HashMap<String, String>();
    
    private Listener listener = new Listener();
    
    /**
     * Creates a new object.
     */
    public DefaultLookAndFeelColors(){
        translations.put( TITLE_BACKGROUND, "MenuItem.background" );
        translations.put( TITLE_FOREGROUND, "MenuItem.foreground" );
        translations.put( TITLE_SELECTION_BACKGROUND, "MenuItem.selectionBackground" );
        translations.put( TITLE_SELECTION_FOREGROUND, "MenuItem.selectionForeground" );
        translations.put( SELECTION, "TextField.selectionBackground" );
        translations.put( PANEL_BACKGROUND, "Panel.background" );
        translations.put( PANEL_FOREGROUND, "Panel.foreground" );
        translations.put( CONTROL_SHADOW, "controlDkShadow" );
        translations.put( WINDOW_BORDER, "windowBorder" );
    }
    
    public Color getColor( String key ) {
        key = translations.get( key );
        if( key == null )
            return null;
        
        return UIManager.getColor( key );
    }
    
    public void bind() {
        UIManager.addPropertyChangeListener( listener );
    }
    
    public void unbind() {
        UIManager.removePropertyChangeListener( listener );
    }
    
    /**
     * A listener for the {@link UIManager}, gets informed when a color
     * changes.
     * @author Benjamin Sigg
     */
    private class Listener implements PropertyChangeListener{
        public void propertyChange( PropertyChangeEvent evt ) {
            String name = evt.getPropertyName();
            if( "lookAndFeel".equals( name )){
            	fireColorsChanged();
            }
            else{
	            String key = null;
	            
	            for( Map.Entry<String, String> entry : translations.entrySet() ){
	                if( entry.getValue().equals( name )){
	                    if( key == null ){
	                        key = entry.getKey();
	                    }
	                    else{
	                        fireColorsChanged();
	                        return;
	                    }
	                }
	            }
	            
	            if( key != null )
	                fireColorChanged( key );
            }
        }
    }
}
