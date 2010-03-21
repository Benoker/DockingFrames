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
package bibliothek.gui.dock.common.intern;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableBackupFactory;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * This factory is used to create new {@link SingleCDockable}s using various
 * {@link SingleCDockableBackupFactory}s. This factory is accessed when a single
 * dockable is missing in the cache of its owning {@link CControl}.
 * @author Benjamin Sigg
 */
public class CommonSingleDockableFactory implements DockFactory<CommonDockable, String>{
    public static final String BACKUP_FACTORY_ID = "ccontrol backup factory id";
    
    private Map<String, SingleCDockableBackupFactory> backups = new HashMap<String, SingleCDockableBackupFactory>();
    private CControl control;
    
    /**
     * Creates a new factory.
     * @param control the owner of the factory, the factory will add {@link SingleCDockable}
     * to this control
     */
    public CommonSingleDockableFactory( CControl control ){
        this.control = control;
    }
    
    /**
     * Registers a new factory that will load {@link SingleCDockable}s with
     * the unique id <code>id</code>.
     * @param id the unique id of the dockables which <code>factory</code> will create
     * @param factory the new factory
     */
    public void add( String id, SingleCDockableBackupFactory factory ){
        backups.put( id, factory );
    }
    
    /**
     * Removes a factory from this.
     * @param id the name of the factory to remove
     */
    public void remove( String id ){
        backups.remove( id );
    }
    
    /**
     * Searches the factory which was registered for <code>id</code>.
     * @param id the name of a factory
     * @return the factory or <code>null</code>
     */
    public SingleCDockableBackupFactory getFactory( String id ){
        return backups.get( id );
    }
    
    public String getID() {
        return BACKUP_FACTORY_ID;
    }
    
    /**
     * Gets a list of keys for all factories known to this.
     * @return the list of keys
     */
    public String[] listFactories(){
        Set<String> keys = backups.keySet();
        return keys.toArray( new String[ keys.size() ] );
    }
    
    /**
     * Gets the set of keys for all factories known to this.
     * @return the set of keys
     */
    public Set<String> getFactoryIds(){
        return Collections.unmodifiableSet( backups.keySet() );
    }
    
    public void estimateLocations(String layout, Map<Integer, DockLayoutInfo> children) {
    	// currently not supported
    }

    public String getLayout( CommonDockable element, Map<Dockable, Integer> children ) {
        CDockable dockable = element.getDockable();
        if( dockable instanceof SingleCDockable ){
            SingleCDockable single = (SingleCDockable)dockable;
            return single.getUniqueId();
        }
        else
            throw new IllegalArgumentException( "A CommonSingleDockableFactory works only with Dockables of type SingleCDockable, but this is not a single dockable: " + element );
    }

    public CommonDockable layout( String layout, Map<Integer, Dockable> children ) {
        return layout( layout );
    }

    public CommonDockable layout( String layout ) {
        SingleCDockableBackupFactory backup = backups.get( layout );
        if( backup == null )
            return null;
        
        SingleCDockable dockable = backup.createBackup( layout );
        if( dockable == null )
            return null;
        
        control.add( dockable );
        return dockable.intern();
    }

    public String read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException {
        Version version = Version.read( in );
        if( !version.equals( Version.VERSION_1_0_4 ))
            throw new IOException( "Data from the future - unknown version: " + version );
        
        return in.readUTF();
    }

    public String read( XElement element, PlaceholderStrategy placeholders ) {
        return element.getElement( "id" ).getString();
    }

    public void setLayout( CommonDockable element, String layout, Map<Integer, Dockable> children ) {
        // can't do anything
    }

    public void setLayout( CommonDockable element, String layout ) {
        // can't do anything
    }

    public void write( String layout, DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_0_4 );
        out.writeUTF( layout );
    }

    public void write( String layout, XElement element ) {
        element.addElement( "id" ).setString( layout );
    }
}
