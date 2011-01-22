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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.themes.ThemePropertyFactory;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A map of {@link ThemeFactory}s. This map can change the {@link DockTheme}
 * of its associated {@link CControl}. New factories can be added or removed
 * from the map.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class ThemeMap {
    /** standard key for the {@link CBasicTheme} */
    public static final String KEY_BASIC_THEME = "basic";
    /** standard key for the {@link CBubbleTheme} */
    public static final String KEY_BUBBLE_THEME = "bubble";
    /** standard key for the {@link CEclipseTheme} */
    public static final String KEY_ECLIPSE_THEME = "eclipse";
    /** standard key for the {@link CFlatTheme} */
    public static final String KEY_FLAT_THEME = "flat";
    /** standard key for the {@link CSmoothTheme} */
    public static final String KEY_SMOOTH_THEME = "smooth";
   
    /** modifies the themes created by this map */
    private DockThemeModifier modifier;
    
    /** the observers of this map */
    private List<ThemeMapListener> listeners = new ArrayList<ThemeMapListener>();
    
    /** the set of known factories to this map */
    private List<Entry> factories = new ArrayList<Entry>();
    
    /** the currently selected factory, can be <code>null</code> */
    private Entry selected;
    
    /**
     * Creates a new empty map.
     */
    public ThemeMap(){
        // nothing
    }
    
    /**
     * Creates a new map and wires this map to <code>control</code>. Ensures
     * that the standard themes are available.<br>
     * Every change of the selected factory will change the theme of <code>control</code>,
     * and this map will ensure that the name of the theme is stored in the
     * {@link ApplicationResourceManager}.
     * @param control the control to monitor
     */
    public ThemeMap( final CControl control ){
        init( control );
        
        addThemeMapListener( new ThemeMapListener(){
            public void changed( ThemeMap map, int index, String key, ThemeFactory oldFactory, ThemeFactory newFactory ) {
                // ignore
            }
            public void selectionChanged( ThemeMap map, String oldKey, String newKey ) {
                ThemeFactory factory = null;
                if( newKey != null )
                    factory = getFactory( newKey );
                
                DockTheme theme;
                
                if( factory == null ){
                    theme = new CBasicTheme( control );
                }
                else{
                    theme = factory.create( control.getController() );
                }
                
                if( modifier != null )
                    theme = modifier.modify( theme );
                
                control.intern().getController().setTheme( theme );
            }
        });
        
        try {
            control.getResources().put( "dock.ui.ThemeMap", new ApplicationResource(){
                public void read( DataInputStream in ) throws IOException {
                    Version.read( in ).checkCurrent();
                    if( in.readBoolean() ){
                        select( in.readUTF() );
                    }
                    else{
                        select( -1 );
                    }
                }

                public void readXML( XElement element ) {
                    String key = null;
                    XElement xkey = element.getElement( "key" );
                    if( xkey != null ){
                        key = xkey.getString();
                    }
                    select( key );
                }

                public void write( DataOutputStream out ) throws IOException {
                    Version.write( out, Version.VERSION_1_0_6 );
                    String key = getSelectedKey();
                    if( key == null ){
                        out.writeBoolean( false );
                    }
                    else{
                        out.writeBoolean( true );
                        out.writeUTF( key );
                    }
                }

                public void writeXML( XElement element ) {
                    String key = getSelectedKey();
                    if( key != null ){
                        element.addElement( "key" ).setString( key );
                    }
                }                
            });
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        
        if( getSelectedKey() == null ){
            select( KEY_BASIC_THEME );
        }
    }
    
    private void init( CControl control ){
        ThemeFactory flat = 
            new CDockThemeFactory<FlatTheme>( new ThemePropertyFactory<FlatTheme>( FlatTheme.class ), control ){
            @Override
            public DockTheme create( CControl control ) {
                return new CFlatTheme( control );
            }
        };

        ThemeFactory bubble = 
            new CDockThemeFactory<BubbleTheme>( new ThemePropertyFactory<BubbleTheme>( BubbleTheme.class ), control ){
            @Override
            public DockTheme create( CControl control ) {
                return new CBubbleTheme( control );
            }
        };

        ThemeFactory eclipse = new CDockThemeFactory<EclipseTheme>( new ThemePropertyFactory<EclipseTheme>( EclipseTheme.class ), control ){
            @Override
            public DockTheme create( CControl control ) {
                return new CEclipseTheme( control );
            }
        };

        ThemeFactory smooth = 
            new CDockThemeFactory<SmoothTheme>( new ThemePropertyFactory<SmoothTheme>( SmoothTheme.class ), control ){
            @Override
            public DockTheme create( CControl control ) {
                return new CSmoothTheme( control );
            }
        };

        ThemeFactory basic =
            new CDockThemeFactory<BasicTheme>( new ThemePropertyFactory<BasicTheme>( BasicTheme.class ), control ){
            @Override
            public DockTheme create( CControl control ) {
                return new CBasicTheme( control );
            }
        };

        add( KEY_BASIC_THEME, basic );
        add( KEY_SMOOTH_THEME, smooth );
        add( KEY_FLAT_THEME, flat );
        add( KEY_BUBBLE_THEME, bubble );
        add( KEY_ECLIPSE_THEME, eclipse );
    }
    
    /**
     * Adds a new listener to this map. The listener will be informed when a 
     * factory is changed or the theme changes.
     * @param listener the new listener
     */
    public void addThemeMapListener( ThemeMapListener listener ){
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        
        listeners.add( listener );
    }
    
    /**
     * Removes a listener from this map.
     * @param listener the listener to remove
     */
    public void removeThemeMapListener( ThemeMapListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets an array containing all {@link ThemeMapListener}s of this map.
     * @return the list of listeners
     */
    protected ThemeMapListener[] listeners(){
        return listeners.toArray( new ThemeMapListener[ listeners.size() ] );
    }
    
    /**
     * Sets the object which will modify the {@link DockTheme} before applying
     * the theme to the {@link DockController}.
     * @param modifier the new modifier, can be <code>null</code>
     */
    public void setModifier( DockThemeModifier modifier ) {
        if( this.modifier != modifier ){
            this.modifier = modifier;
            String key = getSelectedKey();
            select( key, true );
        }
    }
    
    /**
     * Gets the object which will modify the {@link DockTheme} before applying
     * the theme to the {@link DockController}.
     * @return the modifier, can be <code>null</code>
     */
    public DockThemeModifier getModifier() {
        return modifier;
    }
    
    /**
     * Changes the selected factory. If there is no factory with name
     * <code>key</code> or <code>key</code> is <code>null</code>, then the
     * <code>null</code>-factory is selected.
     * @param key the name of the newly selected factory, can be <code>null</code>
     */
    public void select( String key ){
        select( key, false );
    }
    
    /**
     * Changes the selected factory. If there is no factory with name
     * <code>key</code> or <code>key</code> is <code>null</code>, then the
     * <code>null</code>-factory is selected.
     * @param key the name of the newly selected factory, can be <code>null</code>
     * @param force <code>true</code> if the theme is to be loaded even
     * if it is already selected
     */
    public void select( String key, boolean force ){
        if( key == null )
            select( -1, force );
        else
            select( indexOf( key ), force );
    }
    
    /**
     * Changes the selected factory to <code>factory</code>.
     * @param factory the factory to select
     * @throws IllegalArgumentException if <code>factory</code> is not registered
     * in this map.
     */
    public void select( ThemeFactory factory ){
        int index = indexOf( factory );
        if( index < 0 )
            throw new IllegalArgumentException( "factory not known " + factory );
        
        select( index );
    }
    
    /**
     * Changes the selected factory.
     * @param index the index of the newly selected factory, -1 will deselect
     * any factory
     */
    public void select( int index ){
        select( index, false );
    }

    /**
     * Changes the selected factory.
     * @param index the index of the newly selected factory, -1 will deselect
     * any factory
     * @param force <code>true</code> if an update should be forced even if
     * there seems not to be a change
     */
    public void select( int index, boolean force ){
        Entry entry = null;
        if( index >= 0 )
            entry = factories.get( index );
        
        if( entry != selected || force ){
            String oldKey = selected == null ? null : selected.key;
            String newKey = entry == null ? null : entry.key;
            
            selected = entry;
            
            for( ThemeMapListener listener : listeners() ){
                listener.selectionChanged( this, oldKey, newKey );
            }
        }
    }
    
    /**
     * Gets the name of the currently selected factory.
     * @return the name or <code>null</code>
     */
    public String getSelectedKey(){
        return selected == null ? null : selected.key;
    }
    
    /**
     * Gets the currently selected factory.
     * @return the factory or <code>null</code>
     */
    public ThemeFactory getSelectedFactory(){
        return selected == null ? null : selected.factory;
    }
    
    /**
     * Gets the number of elements of this map.
     * @return the number of elements
     */
    public int size(){
        return factories.size();
    }
    
    /**
     * Searches for an entry named <code>key</code> and changes its factory.
     * If no such entry is found, then <code>factory</code> is added at the
     * end of this map.
     * @param key the name of the factory
     * @param factory the new factory
     */
    public void put( String key, ThemeFactory factory ){
        if( key == null )
            throw new IllegalArgumentException( "key must not be null" );
        
        if( factory == null )
            throw new IllegalArgumentException( "factory must not be null" );
        
        int index = indexOf( key );
        if( index < 0 ){
            add( key, factory );
        }
        else{
            Entry entry = factories.get( index );
            ThemeFactory old = entry.factory;
            entry.factory = factory;
            for( ThemeMapListener listener : listeners()){
                listener.changed( this, index, key, old, factory );
            }
        }
    }
    
    /**
     * Adds <code>factory</code> at the end of this map. If there is already
     * a factory named <code>key</code>, then that other factory is first removed.
     * @param key the key of the new factory
     * @param factory the new factory
     */
    public void add( String key, ThemeFactory factory ){
        insert( size(), key, factory );
    }
    
    /**
     * Inserts a new factory into this map. If there is already a factory
     * <code>key</code> in this map, then that other factory is removed.
     * @param index the index where to insert the new factory
     * @param key the key of the new factory
     * @param factory the new factory
     */
    public void insert( int index, String key, ThemeFactory factory ){
        if( key == null )
            throw new IllegalArgumentException( "key must not be null" );
        
        if( factory == null )
            throw new IllegalArgumentException( "factory must not be null" );
        
        if( index < 0 || index > factories.size() )
            throw new ArrayIndexOutOfBoundsException( index );
        
        int remove = indexOf( key );
        if( remove >= 0 ){
            remove( remove );
            if( index > remove )
                index--;
        }
        
        Entry entry = new Entry();
        entry.key = key;
        entry.factory = factory;
        
        factories.add( index, entry );
        for( ThemeMapListener listener : listeners() ){
            listener.changed( this, index, key, null, factory );
        }
    }
    
    /**
     * Removes the <code>index</code>'th entry of this map.
     * @param index the name of the element to remove
     */
    public void remove( int index ){
        Entry entry = factories.remove( index );
        
        for( ThemeMapListener listener : listeners() ){
            listener.changed( this, index, entry.key, entry.factory, null );
        }
    }
    
    /**
     * Deletes the factory associated with <code>key</code>.
     * @param key the name of the element to remove
     * @return <code>true</code> if the element was deleted, <code>false</code>
     * if no element named <code>key</code> was found
     */
    public boolean remove( String key ){
        int index = indexOf( key );
        if( index >= 0 ){
            remove( index );
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * Searches for <code>factory</code> and returns its index.
     * @param factory the factory to search
     * @return its index or -1
     */
    public int indexOf( ThemeFactory factory ){
        int index = 0;
        for( Entry entry : factories ){
            if( entry.factory == factory )
                return index;
            
            index++;
        }
        
        return -1;
    }
    
    /**
     * Searches for <code>key</code> and returns its location.
     * @param key the key to search
     * @return the index or -1
     */
    public int indexOf( String key ){
        int index = 0;
        for( Entry entry : factories ){
            if( entry.key.equals( key ))
                return index;
            
            index++;
        }
        
        return -1;
    }
    
    /**
     * Gets the key of the <code>index</code>'th element.
     * @param index the index of the element
     * @return the key to that element
     */
    public String getKey( int index ){
        return factories.get( index ).key;
    }
    
    /**
     * Gets the <code>index</code>'th factory.
     * @param index the index of the factory
     * @return the factory, not <code>null</code>
     */
    public ThemeFactory getFactory( int index ){
        return factories.get( index ).factory;
    }
    
    /**
     * Searches the factory which is associated with <code>key</code>.
     * @param key the unique name of a factory
     * @return the factory, may be <code>null</code>
     */
    public ThemeFactory getFactory( String key ){
        Entry entry = getEntry( key );
        if( entry == null )
            return null;
        
        return entry.factory;
    }
    
    private Entry getEntry( String key ){
        for( Entry entry : factories ){
            if( entry.key.equals( key ))
                return entry;
        }
        return null;
    }
    
    /**
     * An entry of this map.
     * @author Benjamin Sigg
     */
    private static class Entry{
        /** the unique name of the entry */
        public String key;
        /** the factory associated to {@link #key} */
        public ThemeFactory factory;
    }
}
