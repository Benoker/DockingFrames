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
package bibliothek.gui.dock.common.intern.color;

import java.awt.Color;
import java.util.*;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.event.CControlListener;
import bibliothek.gui.dock.common.event.ColorMapListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * A <code>ColorTransmitter</code> observes some {@link ColorMap}s
 * and transmits some {@link Color}s of these maps to a set of {@link DockColor}s.
 * @author Benjamin Sigg
 * @param <D> the kind of {@link DockColor} used in this transmitter
 */
public abstract class ColorTransmitter<D extends DockColor> implements ColorBridge {
    private CControl control;
    private Set<String> keys = new HashSet<String>();
    private Map<String, List<D>> colors = new HashMap<String, List<D>>();
    private Listener listener = new Listener();
    
    /**
     * Creates a new {@link ColorTransmitter}.
     * @param keys the keys which should be monitored by this transmitter
     */
    public ColorTransmitter( String... keys ){
        for( String key : keys )
            this.keys.add( key );
    }
    
    @SuppressWarnings( "unchecked" )
    public void add( String id, DockColor color ) {
        if( keys.contains( id )){
            boolean empty = colors.isEmpty();
            
            List<D> list = colors.get( id );
            if( list == null ){
                list = new LinkedList<D>();
                colors.put( id, list );
            }
            list.add( (D)color );            
            if( empty )
                setListening( true );
        }
    }
    
    public void remove( String id, DockColor color ) {
        if( keys.contains( id )){
            boolean empty = colors.isEmpty();
            
            List<D> list = colors.get( id );
            list.remove( color );
            if( list.isEmpty() ){
                colors.remove( id );
            }            
            if( !empty && colors.isEmpty() )
                setListening( false );
        }
    }
    
    /**
     * Sets the {@link CControl} which should be observed for new {@link CDockable}s
     * by this transmitter.
     * @param control the observed control, can be <code>null</code>
     */
    public void setControl( CControl control ) {
        if( !colors.isEmpty() )
            setListening( false );
        
        this.control = control;
        
        if( !colors.isEmpty() )
            setListening( true );
    }
    
    /**
     * Adds or removes all listeners from the {@link CControl}.
     * @param listening <code>true</code> if the listeners are to be
     * added, <code>false</code> if they have to be removed
     */
    private void setListening( boolean listening ){
        if( this.control != null ){
            if( listening ){
                control.addControlListener( listener );
                for( int i = 0, n = control.getCDockableCount(); i<n; i++ )
                    control.getCDockable( i ).getColors().addListener( listener );
            }
            else{
                this.control.removeControlListener( listener );
                for( int i = 0, n = this.control.getCDockableCount(); i<n; i++ )
                    this.control.getCDockable( i ).getColors().removeListener( listener );
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void set( String id, Color color, DockColor observer ) {
        if( keys.contains( id )){
            color = get( color, id, (D)observer );
        }
        observer.set( color );
    }
    
    /**
     * Called when a color needs to be set whose key has been registered at
     * this {@link ColorTransmitter}.
     * @param color the original color
     * @param id the key of the color
     * @param observer the destination for the color
     * @return the color that should be set to <code>observer</code>
     */
    protected abstract Color get( Color color, String id, D observer );
    
    /**
     * Called when a color in a {@link ColorMap} has changed.
     * @param dockable the owner of the map
     * @param key the name of the changed color
     * @param color the new value of the color in the map, can be <code>null</code>
     */
    protected abstract void update( CDockable dockable, String key, Color color );
    
    /**
     * Gets the {@link CDockable} which is associated with <code>observer</code>.
     * @param observer some observer
     * @return the associated dockable or <code>null</code>
     */
    protected abstract CDockable getDockable( D observer );
    
    /**
     * Gets the first non- <code>null</code> color of <code>map</code> that
     * matches a given key.
     * @param map a map of colors
     * @param keys some keys that will be read from index 0 upward.
     * @return the first {@link Color} that is not <code>null</code> or <code>null</code>
     */
    protected Color getFirstNonNull( ColorMap map, String...keys ){
        for( String key : keys ){
            Color color = map.getColor( key );
            if( color != null )
                return color;
        }
        return null;
    }
    
    /**
     * Transmits <code>color</code> to all {@link DockColor}s which
     * listen to the given id and which are associated with <code>dockable</code>.
     * @param id the id of the changed color
     * @param color the new color, might be <code>null</code>
     * @param dockable the associated dockable, might be <code>null</code>
     */
    protected void set( String id, Color color, CDockable dockable ){
        List<D> list = colors.get( id );
        if( list != null ){
            for( D observer : list ){
                if( dockable == getDockable( observer )){
                    observer.set( color );
                }
            }
        }
    }
    
    /**
     * A listener that gets informed when new maps join or some color in a map
     * changes.
     * @author Benjamin Sigg
     */
    private class Listener implements CControlListener, ColorMapListener{
        public void added( CControl control, CDockable dockable ) {
            // ignore
        }

        public void removed( CControl control, CDockable dockable ) {
            // ignore
        }

        public void closed( CControl control, CDockable dockable ) {
            dockable.getColors().removeListener( this );
        }

        public void opened( CControl control, CDockable dockable ) {
            dockable.getColors().addListener( this );
        }

        public void colorChanged( ColorMap map, String key, Color color ) {
            update( map.getDockable(), key, color );
        }
    }
}
