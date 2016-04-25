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
package bibliothek.gui.dock.themes.basic;

import java.awt.Color;

import javax.swing.JTabbedPane;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.station.stack.DefaultStackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * The {@link BasicStackDockComponent} is an implementation of {@link StackDockComponent}
 * using a {@link JTabbedPane}. This <code>StackDockComponent</code> can put
 * different colors on its tabs.
 * @author Benjamin Sigg
 *
 */
@ColorCodes( {
    "stack.tab.foreground",
    "stack.tab.foreground.selected",
    "stack.tab.foreground.focused",
    "stack.tab.background",
    "stack.tab.background.selected",
    "stack.tab.background.focused" } )
public class BasicStackDockComponent extends DefaultStackDockComponent {
    private StackDockComponentParent station;
    
    public BasicStackDockComponent( StackDockComponentParent station ) {
        this.station = station;
    }

    @Override
    protected Tab createTab( Dockable dockable ) {
        return new BasicTab( dockable );
    }
    
    @Override
    public boolean hasBorder() {
        return false;
    }
    
    /**
     * A basic tab that listens to the {@link DockController} to recognize
     * when it is focused.
     * @author Benjamin Sigg
     *
     */
    protected class BasicTab extends DefaultStackDockComponent.Tab implements DockableFocusListener{
        private TabColor colorForeground;
        private TabColor colorForegroundSelected;
        private TabColor colorForegroundFocused;
        private TabColor colorBackground;
        private TabColor colorBackgroundSelected;
        private TabColor colorBackgroundFocused;
        
        private TabColor[] colors;
        private DockController controller;
        
        public BasicTab( Dockable dockable ) {
            super( dockable );

            colors = new TabColor[]{
                    colorForeground = new BasicTabColor( "stack.tab.foreground" ),
                    colorForegroundSelected = new BasicTabColor( "stack.tab.foreground.selected" ),
                    colorForegroundFocused = new BasicTabColor( "stack.tab.foreground.focused" ),
                    colorBackground = new BasicTabColor( "stack.tab.background" ),
                    colorBackgroundSelected = new BasicTabColor( "stack.tab.background.selected" ),
                    colorBackgroundFocused = new BasicTabColor( "stack.tab.background.focused" )
            };
        }
        
        @Override
        public void setController( DockController controller ) {
            if( this.controller != null )
                this.controller.removeDockableFocusListener( this );
            
            super.setController( controller );
            this.controller = controller;
            
            for( TabColor color : colors )
                color.connect( controller );
            
            if( controller != null )
                controller.addDockableFocusListener( this );
            
            updateColors();
        }
        
        private void updateColors(){
            int index = station.indexOf( BasicTab.this.getDockable() );
            if( index >= 0 && index < getTabCount() ){
                boolean focused = controller == null ? false : controller.getFocusedDockable() == getDockable();
                boolean selected = index == getSelectedIndex();
            
                Color foreground = null;
                Color background = null;
                
                if( focused ){
                    foreground = colorForegroundFocused.value();
                    background = colorBackgroundFocused.value();
                }
                else if( selected ){
                    foreground = colorForegroundSelected.value();
                    background = colorBackgroundSelected.value();
                }
                else{
                    foreground = colorForeground.value();
                    background = colorBackground.value();
                }
                
                setForegroundAt( index, foreground );
                setBackgroundAt( index, background );
            }
        }
        
        public void dockableFocused( DockableFocusEvent event ) {
            updateColors();
        }
        
        /**
         * A color used on this tab.
         * @author Benjamin Sigg
         */
        private class BasicTabColor extends TabColor{
            /**
             * Creates a new color
             * @param id the name of this color
             */
            public BasicTabColor( String id ){
                super( id, station.getStackDockParent(), dockable, null );
            }
            
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                updateColors();
            }
        }
    }
}
