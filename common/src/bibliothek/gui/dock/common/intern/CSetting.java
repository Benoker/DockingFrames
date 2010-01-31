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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.support.mode.ModeSettings;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A {@link Setting} that stores also the contents of a {@link CLocationModeManager}.
 * @author Benjamin Sigg
 */
public class CSetting extends Setting{
    /** a set of modes */
    private ModeSettings<Location, ?> modes;
    
    /** tells for some {@link MultipleCDockableFactory}s which {@link MultipleCDockable} existed */
    private Map<String, List<String>> multiFactoryDockables = new HashMap<String, List<String>>();
    
    /**
     * Sets the set of modes.
     * @param modes the modes
     */
    public void setModes( ModeSettings<Location, ?> modes ) {
        this.modes = modes;
    }
    
    /**
     * Gets the set of modes.
     * @return the modes
     */
    public ModeSettings<Location, ?> getModes() {
        return modes;
    }
    
    /**
     * Links the factory with identifier <code>factoryId</code> with the
     * elements <code>dockables</code>. This information can be useful when 
     * a factory is added to the {@link CControl} after a setting was loaded.
     * @param factoryId the id of the factory
     * @param dockables the list of elements, not <code>null</code>
     */
    public void putMultipleFactoryDockables( String factoryId, List<String> dockables ){
        if( dockables == null )
            throw new IllegalArgumentException( "dockables must not be null" );
        
        multiFactoryDockables.put( factoryId, dockables );
    }
    
    /**
     * Gets all the identifiers of dockables which were created and shown for <code>factoryId</code>.
     * @param factoryId the id of a factory
     * @return its dockables
     */
    public List<String> getMultipleFactoryDockables( String factoryId ){
        return multiFactoryDockables.get( factoryId );
    }
    
    @Override
    public void write( DockSituation situation,
            PropertyTransformer transformer, boolean entry, DataOutputStream out )
            throws IOException {
        
        Version.write( out, Version.VERSION_1_0_8 );
        
        super.write( situation, transformer, entry, out );
        modes.write( out );
        
        out.writeInt( multiFactoryDockables.size() );
        for( Map.Entry<String, List<String>> factory : multiFactoryDockables.entrySet() ){
            out.writeUTF( factory.getKey() );
            List<String> list = factory.getValue();
            out.writeInt( list.size() );
            for( String dockable : list ){
                out.writeUTF( dockable );
            }
        }
    }
    
    @Override
    public void writeXML( DockSituation situation,
            PropertyTransformer transformer, boolean entry, XElement element ) {
        
        super.writeXML( situation, transformer, entry, element.addElement( "base" ) );
        modes.writeXML( element.addElement( "modes" ) );
        
        if( !multiFactoryDockables.isEmpty() ){
            XElement xmultiFactories = element.addElement( "multi-factories" );
            for( Map.Entry<String, List<String>> factory : multiFactoryDockables.entrySet() ){
                XElement xfactory = xmultiFactories.addElement( "factory" );
                xfactory.addString( "id", factory.getKey() );
                for( String dockable : factory.getValue() ){
                    xfactory.addElement( "dockable" ).addString( "id", dockable );
                }
            }
        }
    }
    
    @Override
    public void read( DockSituation situation, PropertyTransformer transformer,
            boolean entry, DataInputStream in ) throws IOException {
        
        Version version = Version.read( in );
        version.checkCurrent();
        
        boolean version7 = version.compareTo( Version.VERSION_1_0_7 ) >= 0;
        
        super.read( situation, transformer, entry, in );
        
        // old settings will be converted automatically
        modes.read( in );
        
        if( version7 ){
            for( int i = 0, n = in.readInt(); i<n; i++ ){
                String id = in.readUTF();
                List<String> dockables = new ArrayList<String>();
                for( int j = 0, m = in.readInt(); j<m; j++ ){
                    dockables.add( in.readUTF() );
                }
                putMultipleFactoryDockables( id, dockables );
            }
        }
    }
    
    @Override
    public void readXML( DockSituation situation,
            PropertyTransformer transformer, boolean entry, XElement element ) {
        super.readXML( situation, transformer, entry, element.getElement( "base" ) );
        modes.readXML( element.getElement( "modes" ) );
        
        XElement xmultiFactories = element.getElement( "multi-factories" );
        if( xmultiFactories != null ){
            for( XElement xfactory : xmultiFactories.getElements( "factory" ) ){
                List<String> dockables = new ArrayList<String>();
                for( XElement xdockable : xfactory.getElements( "dockable" )){
                    String id = xdockable.getString( "id" );
                    if( id != null ){
                        dockables.add( id );
                    }
                }
                String id = xfactory.getString( "id" );
                if( id != null ){
                    putMultipleFactoryDockables( id, dockables );
                }
            }
        }
    }
}
