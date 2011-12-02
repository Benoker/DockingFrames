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
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CPerspectiveMultipleIdentifierCollection;
import bibliothek.gui.dock.common.perspective.CommonElementPerspective;
import bibliothek.gui.dock.common.perspective.MultipleCDockablePerspective;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A factory used to create {@link CommonDockable}s. This factory is only
 * used to create {@link MultipleCDockable}s because {@link SingleCDockable}s
 * are stored by the client. 
 * @author Benjamin Sigg
 */
public class CommonMultipleDockableFactory implements DockFactory<CommonDockable, CommonElementPerspective, CommonMultipleDockableLayout> {
    /** the unique identifier of this factory */
    private String id;
    /** the factory used to read and write {@link MultipleCDockable}s */
    private MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout> delegate;
    /** access to private properties of {@link CControl} */
    private CControlAccess controlAccess;
    /** access to private properties of {@link CPerspective} */
    private CPerspectiveMultipleIdentifierCollection perspectiveIdentifiers;
    
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
        this.controlAccess = access;
    }
    
    /**
     * Creates a new factory.
     * @param id the identifier of this factory
     * @param delegate the factory that should be used to load the contents of the
     * new {@link CommonDockable}
     * @param access access to the internal affairs of a {@link CControl}
     * @param perspective the {@link CPerspective} for which this factory is going to read and write elements
     */
    @SuppressWarnings("unchecked")
    public CommonMultipleDockableFactory( String id, MultipleCDockableFactory<?, ?> delegate, CControlAccess access, CPerspective perspective ){
    	this.id = id;
    	this.delegate = (MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout>)delegate;
    	this.controlAccess = access;
    	this.perspectiveIdentifiers = new CPerspectiveMultipleIdentifierCollection( id, perspective );
    }
    
    public String getID() {
        return id;
    }
    
    /**
     * Gets the delegate of this factory.
     * @return the delegate, not <code>null</code>
     */
    public MultipleCDockableFactory<?, ?> getFactory(){
        return delegate;
    }
    
    public void estimateLocations( CommonMultipleDockableLayout layout, LocationEstimationMap children ){
    	// currently not supported
    }

    public CommonMultipleDockableLayout getLayout( CommonDockable element, Map<Dockable, Integer> children ) {
        MultipleCDockable dockable = (MultipleCDockable)element.getDockable();
        MultipleCDockableLayout layout = delegate.write( dockable );
        
        CommonMultipleDockableLayout flayout = new CommonMultipleDockableLayout();
        flayout.setLayout( layout );
        String uniqueId = controlAccess.access( element.getDockable() ).getUniqueId();
        uniqueId = controlAccess.getRegister().multiToNormalId( uniqueId );
        flayout.setId( uniqueId );
        if( element.getDockable().getWorkingArea() != null )
            flayout.setArea( element.getDockable().getWorkingArea().getUniqueId() );
        
        return flayout;
    }

    public CommonMultipleDockableLayout getPerspectiveLayout( CommonElementPerspective element, Map<PerspectiveDockable, Integer> children ){
    	MultipleCDockablePerspective dockable = (MultipleCDockablePerspective)element.getElement();
    	
    	MultipleCDockableLayout layout = dockable.getLayout();
        
        CommonMultipleDockableLayout flayout = new CommonMultipleDockableLayout();
        flayout.setLayout( layout );
        String uniqueId = perspectiveIdentifiers.getUniqueId( dockable );
        flayout.setId( uniqueId );
        if( dockable.getWorkingArea() != null )
            flayout.setArea( dockable.getWorkingArea().getUniqueId() );
        
        return flayout;
    }
    
	public void layoutPerspective( CommonElementPerspective perspective, CommonMultipleDockableLayout layout, Map<Integer, PerspectiveDockable> children ){
    	MultipleCDockablePerspective multiple = (MultipleCDockablePerspective) perspective.getElement();
    	multiple.setLayout( layout.getLayout() );
        perspectiveIdentifiers.putDockable( layout.getId(), multiple );
        
        // working area
        String areaId = layout.getArea();
        if( areaId != null ){
        	multiple.setWorkingArea( perspectiveIdentifiers.getPerspective().getStation( areaId ) );
        }
    }
    
    public CommonElementPerspective layoutPerspective( CommonMultipleDockableLayout layout, Map<Integer, PerspectiveDockable> children ){
    	MultipleCDockablePerspective perspective = new MultipleCDockablePerspective( getID(), layout.getId(), layout.getLayout() );
    	layoutPerspective( perspective.intern(), layout, children );
    	return perspective.intern();
    }
    
    public CommonDockable layout( CommonMultipleDockableLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        return layout( layout, placeholders );
    }

    public CommonDockable layout( CommonMultipleDockableLayout layout, PlaceholderStrategy placeholders ) {
        // base
        MultipleCDockable dockable = delegate.read( layout.getLayout() );
        if( dockable == null )
            return null;
        
        // id
        String id = layout.getId();
        
        MultipleCDockable oldDockable = controlAccess.getOwner().getMultipleDockable( id );
        
        if( oldDockable != null ){
        	controlAccess.getOwner().replace( oldDockable, dockable );
        }
        else{
        	controlAccess.getOwner().addDockable( id, dockable );
        }
        
        // working area
        String areaId = layout.getArea();
        if( areaId != null ){
        	CStation<?> station = controlAccess.getOwner().getStation( areaId );
        	if( station != null ){
        		if( station.isWorkingArea() ){
        			dockable.setWorkingArea( station );
        		}
        	}
        	else{
	            for( int i = 0, n = controlAccess.getOwner().getCDockableCount(); i<n; i++ ){
	                CDockable check = controlAccess.getOwner().getCDockable( i );
	                CStation<?> checkStation = check.asStation();
	                
	                if( checkStation != null && checkStation.isWorkingArea() ){
	                    if( checkStation.getUniqueId().equals( areaId )){
	                        // found
	                        dockable.setWorkingArea( checkStation );
	                        break;
	                    }
	                }
	            }
        	}
        }
        
        return dockable.intern();
    }

    public void setLayout( CommonDockable element, CommonMultipleDockableLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        // not supported
    }

    public void setLayout( CommonDockable element, CommonMultipleDockableLayout layout, PlaceholderStrategy placeholders ) {
        // not supported
    }
    
    public CommonMultipleDockableLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        
        CommonMultipleDockableLayout layout = new CommonMultipleDockableLayout();
        layout.setLayout( delegate.create() );
        layout.getLayout().readStream( in );
        layout.setId( in.readUTF() );
        if( in.readBoolean() )
            layout.setArea( in.readUTF() );
        return layout;
    }

    public CommonMultipleDockableLayout read( XElement element, PlaceholderStrategy placeholders ) {
        CommonMultipleDockableLayout layout = new CommonMultipleDockableLayout();
        layout.setLayout( delegate.create() );
        layout.getLayout().readXML( element.getElement( "multiple" ) );
        layout.setId( element.getElement( "id" ).getString() );
        XElement xarea = element.getElement( "area" );
        if( xarea != null )
            layout.setArea( xarea.getString() );
        return layout;
    }

    public void write( CommonMultipleDockableLayout layout, DataOutputStream out ) throws IOException {
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

    public void write( CommonMultipleDockableLayout layout, XElement element ) {
        element.addElement( "id" ).setString( layout.getId() );
        if( layout.getArea() != null )
            element.addElement( "area" ).setString( layout.getArea() );
        layout.getLayout().writeXML( element.addElement( "multiple" ) );
    }
}
