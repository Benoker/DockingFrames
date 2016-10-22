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
package bibliothek.gui.dock.common;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.common.event.FontMapListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.util.font.ConstantFontModifier;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.gui.dock.util.font.GenericFontModifier;

/**
 * A map containing {@link FontModifier}s. Each <code>FontMap</code> is associated
 * with exactly one {@link CDockable}. This map is used to set the font
 * of various elements like titles or tabs. Changes in the map will
 * immediately be forwarded and applied.
 * @author Benjamin Sigg
 */
public class FontMap {
    /** key for font used in titles */
    public static final String FONT_KEY_TITLE = "dock.title";
    
    /** key for font used in titles if the title is focused */
    public static final String FONT_KEY_TITLE_FOCUSED = "dock.title.focused";
    
    /** key for font used on the button for a minimized dockable */
    public static final String FONT_KEY_MINIMIZED_BUTTON = "dock.minimized";
    
    /** key for font used on the focused button for a minimized dockable */
    public static final String FONT_KEY_MINIMIZED_BUTTON_FOCUSED = "dock.minimized.focused";
    
    /** key for font used on a tab */
    public static final String FONT_KEY_TAB = "dock.tab";
    
    /** key for font used on a selected tab */
    public static final String FONT_KEY_TAB_SELECTED = "dock.tab.selected";
    
    /** key for font used on a focused tab */
    public static final String FONT_KEY_TAB_FOCUSED = "dock.tab.focused";
    
    /** the map of fonts associated with {@link #dockable} */
    private Map<String, FontModifier> fonts = new HashMap<String, FontModifier>();
    
    /** listeners to be informed when {@link #fonts} changes */
    private List<FontMapListener> listeners = new ArrayList<FontMapListener>();
    
    /** the element for which this map is used */
    private CDockable dockable;
    
    /**
     * Creates a new map
     * @param dockable the owner of this map
     */
    public FontMap( CDockable dockable ){
        if( dockable == null )
            throw new IllegalArgumentException( "Dockable must not be null" );
        this.dockable = dockable;
    }
    
    /**
     * Gets the owner of this map.
     * @return the owner
     */
    public CDockable getDockable() {
        return dockable;
    }
    
    /**
     * Adds a listener to this map.
     * @param listener the new listener
     */
    public void addListener( FontMapListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        listeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this map.
     * @param listener the listener to remove
     */
    public void removeListener( FontMapListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets the font which is associated with <code>key</code>.
     * @param key the key of the font
     * @return the font or <code>null</code>
     */
    public FontModifier getFont( String key ){
        return fonts.get( key );
    }
    
    /**
     * Sets the font which should be used for <code>key</code>.
     * @param key the key of the font
     * @param font the new font, can be <code>null</code>
     */
    public void setFont( String key, Font font ){
        if( font == null )
            setFont( key, (FontModifier)null );
        else
            setFont( key, new ConstantFontModifier( font ) );
    }
    
    /**
     * Tells to use a font that is derived from the original font of 
     * <code>key</code>. There are different modifications possible, all
     * have to be supplied in the same form: <code>key=value</code>.<br>
     * Example: <code>setFont( x, "i=!", "b=+", s=14" );</code> would
     * create a modification that reverses the italic flag, sets any font
     * to bold and creates only fonts of size 14
     * <ul>
     *      <li>'i': italic
     *          <ul>
     *              <li>'+': make the font italic</li>
     *              <li>'-': make the font not italic</li>
     *              <li>'!': reverse the italic property of the font</li>
     *          </ul>
     *      </li>
     *      <li>'b': bold
     *          <ul>
     *              <li>'+': make the font bold</li>
     *              <li>'-': make the font not bold</li>
     *              <li>'!': reverse the bold property of the font</li>
     *          </ul>
     *      </li>
     *      <li>'s': size
     *          <ul>
     *              <li>'+number': increase the size by <code>number</code></li>
     *              <li>'-number': decrease the size by <code>number</code></li>
     *              <li>'number': set the size to <code>number</code></li>
     *          </ul>
     *      </li>
     * </ul>
     * @param key the key for the font
     * @param modifications a set of modifications
     */
    public void setFont( String key, String... modifications ){
        if( modifications.length == 0 ){
            setFont( key, (FontModifier)null );
        }
        else{
            GenericFontModifier modifier = new GenericFontModifier();
            for( String modification : modifications ){
                String[] entry = split( modification );
                String entryKey = entry[0];
                String entryValue = entry[1];
                
                boolean italic = "i".equals( entryKey );
                boolean bold = "b".equals( entryKey );
                boolean size = "s".equals( entryKey );
                
                if( italic || bold ){
                    GenericFontModifier.Modify modify;
                    
                    if( "+".equals( entryValue )){
                        modify = GenericFontModifier.Modify.ON;
                    }
                    else if( "-".equals( entryValue )){
                        modify = GenericFontModifier.Modify.OFF;
                    }
                    else if( "!".equals( entryValue )){
                        modify = GenericFontModifier.Modify.REVERSE;
                    }
                    else{
                        throw new IllegalArgumentException( "illegal value, must be one of '+', '-' or '!': " + modification );
                    }
                    
                    if( italic )
                        modifier.setItalic( modify );
                    else
                        modifier.setBold( modify );
                }
                else if( size ){
                    String number;
                    
                    if( entryValue.startsWith( "+" )){
                        modifier.setSizeDelta( true );
                        number = entryValue.substring( 1 ).trim();
                    }
                    else if( entryValue.startsWith( "-" )){
                        modifier.setSizeDelta( true );
                        number = entryValue.substring( 1 ).trim();
                    }
                    else{
                        modifier.setSizeDelta( false );
                        number = entryValue;
                    }
                    
                    int parsed = Integer.parseInt( number );
                    if( entryValue.startsWith( "-" )){
                        parsed = -parsed;
                    }
                    
                    modifier.setSize( parsed );
                }
                else{
                    throw new IllegalArgumentException( "unknown key: " + modification );
                }
            }
            
            setFont( key, modifier );
        }
    }
    
    private String[] split( String modification ){
        int index = modification.indexOf( '=' );
        if( index < 0 )
            throw new IllegalArgumentException( "not in the form 'key'='value': " + modification );
        
        String key = modification.substring( 0, index );
        String value = modification.substring( index+1 );
        
        key = key.trim();
        value = value.trim();
        
        if( key.length() == 0 )
            throw new IllegalArgumentException( "missing key in: " + modification );
        
        if( value.length() == 0 )
            throw new IllegalArgumentException( "missing value in: " + modification );
        
        return new String[]{ key, value };
    }
    
    /**
     * Ensures that the original font is used for <code>key</code>
     * @param key the key which should no longer use a modified font
     */
    public void removeFont( String key ){
        setFont( key, (FontModifier)null );
    }
    
    /**
     * Sets the font for <code>key</code>. 
     * @param key the key of the font
     * @param font the new value or <code>null</code> to set
     * the default value
     */
    public void setFont( String key, FontModifier font ){
        FontModifier old;
        if( font == null )
            old = fonts.remove( key );
        else
            old = fonts.put( key, font );
        
        if( (old == null && font != null) || (old != null && !old.equals( font )) ){
            for( FontMapListener listener : listeners.toArray( new FontMapListener[ listeners.size() ] ))
                listener.fontChanged( this, key, font );
        }
    }
}
