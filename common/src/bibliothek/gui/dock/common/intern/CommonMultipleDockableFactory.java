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
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.common.*;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A factory used to create {@link CommonDockable}s. This factory is only
 * used to create {@link MultipleCDockable}s because {@link SingleCDockable}s
 * are never stored. 
 * @author Benjamin Sigg
 */
public class CommonMultipleDockableFactory implements DockFactory<CommonDockable, CommonDockableLayout> {
    /** the unique identifier of this factory */
    private String id;
    /** the factory used to read and write {@link MultipleCDockable}s */
    private MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout> delegate;
    /** access to private properties of {@link CControl} */
    private CControlAccess access;
    
    /**
     * Creates a new factory.
     * @param id the identifier of this factory
     * @param delegate the factory that should be used to load the contents of the
     * new {@link CommonDockable}
     * @param access access to the internal affairs of a {@link CControl}
     */
    @SuppressWarnings("unchecked")
    public CommonMultipleDockableFactory( String id, MultipleCDockableFactory<?,?> delegate, CControlAccess access ){
        this.id = id;
        this.delegate = (MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout>)delegate;
        this.access = access;
    }
    
    public String getID() {
        return id;
    }

    public CommonDockableLayout getLayout( CommonDockable element,
            Map<Dockable, Integer> children ) {
        
        MultipleCDockable dockable = (MultipleCDockable)element.getDockable();
        MultipleCDockableLayout layout = delegate.write( dockable );
        
        CommonDockableLayout flayout = new CommonDockableLayout();
        flayout.setLayout( layout );
        flayout.setId( access.access( element.getDockable() ).getUniqueId() );
        if( element.getDockable().getWorkingArea() != null )
            flayout.setArea( element.getDockable().getWorkingArea().getUniqueId() );
        
        return flayout;
    }

    public CommonDockable layout( CommonDockableLayout layout,
            Map<Integer, Dockable> children ) {
        
        return layout( layout );
    }

    public CommonDockable layout( CommonDockableLayout layout ) {
        // base
        MultipleCDockable dockable = delegate.read( layout.getLayout() );
        if( dockable == null )
            return null;
        
        // id
        String id = layout.getId();
        access.getOwner().add( dockable, id );
        
        // working area
        String areaId = layout.getArea();
        if( areaId != null ){
            for( int i = 0, n = access.getOwner().getCDockableCount(); i<n; i++ ){
                CDockable check = access.getOwner().getCDockable( i );
                if( check instanceof CWorkingArea ){
                    CWorkingArea checkArea = (CWorkingArea)check;
                    if( checkArea.getUniqueId().equals( areaId )){
                        // found
                        dockable.setWorkingArea( checkArea );
                        break;
                    }
                }
            }
        }
        
        return dockable.intern();
    }

    public void setLayout( CommonDockable element, CommonDockableLayout layout,
            Map<Integer, Dockable> children ) {
        
        // not supported
    }

    public void setLayout( CommonDockable element, CommonDockableLayout layout ) {
        // not supported
    }
    
    public CommonDockableLayout read( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        
        CommonDockableLayout layout = new CommonDockableLayout();
        layout.setLayout( delegate.create() );
        layout.getLayout().readStream( in );
        layout.setId( in.readUTF() );
        if( in.readBoolean() )
            layout.setArea( in.readUTF() );
        return layout;
    }

    public CommonDockableLayout read( XElement element ) {
        CommonDockableLayout layout = new CommonDockableLayout();
        layout.setLayout( delegate.create() );
        layout.getLayout().readXML( element.getElement( "multiple" ) );
        layout.setId( element.getElement( "id" ).getString() );
        XElement xarea = element.getElement( "area" );
        if( xarea != null )
            layout.setArea( xarea.getString() );
        return layout;
    }

    public void write( CommonDockableLayout layout, DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_0_4 );
        
        layout.getLayout().writeStream( out );
        out.writeUTF( layout.getId() );
        if( layout.getArea() == null ){
            out.writeBoolean( false );
        }
        else{
            out.writeBoolean( true );
            out.writeUTF( layout.getArea() );
        }
    }

    public void write( CommonDockableLayout layout, XElement element ) {
        element.addElement( "id" ).setString( layout.getId() );
        if( layout.getArea() != null )
            element.addElement( "area" ).setString( layout.getArea() );
        layout.getLayout().writeXML( element.addElement( "multiple" ) );
    }
}
