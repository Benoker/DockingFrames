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
package bibliothek.gui.dock.frontend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.layout.*;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;


/**
 * The set of properties that describe one setting of a {@link DockFrontend}.
 * @author Benjamin Sigg
 */
public class Setting{
    /** the layouts of the roots */
    private Map<String, DockLayoutComposition> roots = new HashMap<String, DockLayoutComposition>();
    
    /** the list of element that are not visible */
    private List<Invisible> dockables = new ArrayList<Invisible>();
    
    /**
     * Stores the layout of a root.
     * @param root the name of the root
     * @param layout the layout
     */
    public void putRoot( String root, DockLayoutComposition layout ){
        roots.put( root, layout );
    }
    
    /**
     * Gets the layout of a root.
     * @param root the root
     * @return the layout or <code>null</code>
     */
    public DockLayoutComposition getRoot( String root ){
        return roots.get( root );
    }
    
    /**
     * Gets the keys of all known roots.
     * @return the keys of the roots
     */
    public String[] getRootKeys(){
        Set<String> keys = roots.keySet();
        return keys.toArray( new String[ keys.size() ] );
    }
    
    /**
     * Stores the location of an invisible element.
     * @param key the key of the element
     * @param root the preferred root of the element
     * @param location the location of the element on <code>root</code>
     */
    public void addInvisible( String key, String root, DockableProperty location ){
        Invisible invisible = new Invisible();
        invisible.key = key;
        invisible.root = root;
        invisible.location = location;
        dockables.add( invisible );
    }
    
    /**
     * Gets the number of stored invisible elements.
     * @return the number of elements
     */
    public int getInvisibleCount(){
        return dockables.size();
    }
    
    /**
     * Gets the key of the index'th invisible element.
     * @param index the index of the element
     * @return the key
     */
    public String getInvisibleKey( int index ){
        return dockables.get( index ).key;
    }

    /**
     * Gets the preferred root of the index'th invisible element.
     * @param index the index of the element
     * @return the root
     */
    public String getInvisibleRoot( int index ){
        return dockables.get( index ).root;
    }
    
    /**
     * Gets the location of the index'th invisible element.
     * @param index the index of the element
     * @return the location
     */
    public DockableProperty getInvisibleLocation( int index ){
        return dockables.get( index ).location;
    }
    
    /**
     * Writes the properties of this setting into <code>out</code>.
     * @param situation can be used to write {@link DockLayout}s
     * @param transformer can be used to write {@link DockableProperty}s
     * @param entry if <code>true</code>, then this setting is used as one of
     * the settings a user can choose. If <code>false</code> then this setting
     * is used as the final setting that is written when the application
     * shuts down.
     * @param out a stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public void write( DockSituation situation, PropertyTransformer transformer, boolean entry, DataOutputStream out ) throws IOException{
        Version.write( out, Version.VERSION_1_0_4 );
        
        String[] roots = getRootKeys();
        out.writeInt( roots.length );
        for( String root : roots ){
            out.writeUTF( root );
            situation.writeComposition( getRoot( root ), out );
        }
        
        out.writeInt( getInvisibleCount() );
        for( int i = 0, n = getInvisibleCount(); i<n; i++ ){
            out.writeUTF( getInvisibleKey( i ) );
            out.writeUTF( getInvisibleRoot( i ) );
            transformer.write( getInvisibleLocation( i ), out );
        }
    }
    
    /**
     * Writes the properties of this setting in xml format.
     * @param situation can be used to write {@link DockLayout}s
     * @param transformer can be used to write {@link DockableProperty}s
     * @param entry if <code>true</code>, then this setting is used as one of
     * the settings a user can choose. If <code>false</code> then this setting
     * is used as the final setting that is written when the application
     * shuts down.
     * @param element the element into which this setting writes, the attributes
     * of <code>element</code> are not changed.
     */
    public void writeXML( DockSituation situation, PropertyTransformer transformer, boolean entry,XElement element ){
        XElement xroots = element.addElement( "roots" );
        String[] roots = getRootKeys();
        for( String root : roots ){
            XElement xroot = xroots.addElement( "root" );
            xroot.addString( "name", root );
            situation.writeCompositionXML( getRoot( root ), xroot );
        }
        
        XElement xchildren = element.addElement( "children" );
        for( int i = 0, n = getInvisibleCount(); i<n; i++ ){
            XElement xchild = xchildren.addElement( "child" );
            xchild.addString( "key", getInvisibleKey( i ) );
            xchild.addString( "root", getInvisibleRoot( i ) );
            transformer.writeXML( getInvisibleLocation( i ), xchild );
        }
    }
    
    /**
     * Reads the properties of this setting. Old properties are deleted without
     * further notice.
     * @param situation can be used to read {@link DockLayout}s
     * @param transformer can be used to read {@link DockableProperty}s
     * @param entry if <code>true</code>, then this setting is used as one of
     * the settings a user can choose. If <code>false</code> then this setting
     * is used as the first setting that is read when the application
     * starts up.
     * @param in the stream to read from
     * @throws IOException if an I/O-error occurs
     */
    public void read( DockSituation situation, PropertyTransformer transformer, boolean entry,DataInputStream in ) throws IOException{
        Version version = Version.read( in );
        version.checkCurrent();
        
        roots.clear();
        dockables.clear();
        
        int count = in.readInt();
        for( int i = 0; i < count; i++ ){
            String root = in.readUTF();
            DockLayoutComposition layout = situation.readComposition( in );
            if( layout != null ){
                putRoot( root, layout );
            }
        }
        
        count = in.readInt();
        for( int i = 0; i < count; i++ ){
            String key = in.readUTF();
            String root = in.readUTF();
            DockableProperty location = transformer.read( in );
            addInvisible( key, root, location );
        }
        
    }
    
    /**
     * Reads the properties of this setting. Old properties are deleted without
     * further notice.
     * @param situation can be used to read {@link DockLayout}s
     * @param transformer can be used to read {@link DockableProperty}s
     * @param entry if <code>true</code>, then this setting is used as one of
     * the settings a user can choose. If <code>false</code> then this setting
     * is used as the first setting that is read when the application
     * starts up.
     * @param element the element which should be read
     */
    public void readXML( DockSituation situation, PropertyTransformer transformer, boolean entry, XElement element ){
        roots.clear();
        dockables.clear();
        
        // roots
        XElement xroots = element.getElement( "roots" );
        if( xroots != null ){
            for( XElement xroot : xroots.getElements( "root" )){
                String name = xroot.getString( "name" );
                DockLayoutComposition composition = situation.readCompositionXML( xroot );
                if( composition != null ){
                    putRoot( name, composition );
                }
            }
        }
        
        // children
        XElement xchildren = element.getElement( "children" );
        if( xchildren != null ){
            for( XElement xchild : xchildren.getElements( "child" )){
                String key = xchild.getString( "key" );
                String root = xchild.getString( "root" );
                DockableProperty location = transformer.readXML( xchild );
                if( location != null ){
                    addInvisible( key, root, location );
                }
            } 
        }
        
    }
    
    /**
     * Describes the location of an invisible element.
     * @author Benjamin Sigg
     */
    private static class Invisible{
        /** the key of the element */
        public String key;
        /** the preferred root of the element */
        public String root;
        /** the location of the element on <code>root</code> */
        public DockableProperty location;
    }
}