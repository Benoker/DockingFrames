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
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;

/**
 * Access to the colors of the Nimbus LookAndFeel.
 * @author Benjamin Sigg
 */
public class Nimbus6u10 extends AbstractLookAndFeelColors{
    private Map<String, String> translations = new HashMap<String, String>();
    
    private Listener listener = new Listener();
    
    /**
     * Creates a new object.
     */
    public Nimbus6u10(){
        translations.put( TITLE_BACKGROUND, "menu" );
        translations.put( TITLE_FOREGROUND, "menuText" );
        translations.put( TITLE_SELECTION_BACKGROUND, "nimbusSelection" );
        translations.put( TITLE_SELECTION_FOREGROUND, "menu" );
        translations.put( SELECTION, "nimbusSelectionBackground" );
        
        translations.put( PANEL_BACKGROUND, "control" );
        translations.put( PANEL_FOREGROUND, "text" );
        
        translations.put( CONTROL_SHADOW, "controlDkShadow" );
        translations.put( WINDOW_BORDER, "windowBorder" );
    }
    
    /**
     * Tells this {@link Nimbus6u10} that the color <code>colorKey</code>
     * should be read from the {@link UIManager} using <code>lafKey</code> 
     * as key.
     * @param colorKey name of a color
     * @param lafKey key used by the {@link UIManager}
     */
    public void put( String colorKey, String lafKey ){
    	translations.put( colorKey, lafKey );
    }
    
    public Color getColor( String key ) {
        key = translations.get( key );
        if( key == null )
            return null;
        
        Color color = UIManager.getColor( key );
        
        if( color == null )
            return null;
        
        return new Color( color.getRGB() );
    }
    
    public void bind() {
        UIManager.addPropertyChangeListener( listener );
        
        EventQueue.invokeLater( new Runnable(){
           public void run() {
               // since Nimbus changes its colors *after* its initialization,
               // we need to wait as well.
               fireColorsChanged();
           } 
        });
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
            update( name );
        }
        
        public void update( String name ){
            for( Map.Entry<String, String> entry : translations.entrySet() ){
                if( entry.getValue().equals( name )){
                    // there are derived colors which might change as well...
                    // just fire an event that "colors changed", no details required. But
                	// we have to wait until Nimbus finished the update.
                    EventQueue.invokeLater( new Runnable(){
                        public void run() {
                            fireColorsChanged();     
                        }
                    });
                    return;
                }       
            }
        }
    }
}
