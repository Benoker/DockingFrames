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
import java.util.Map;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.layout.BackupFactoryData;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockFactory} that wraps around another factory and adds all elements
 * that are read through {@link #layout(BackupFactoryData, PlaceholderStrategy)} or {@link #layout(BackupFactoryData, Map, PlaceholderStrategy)}
 * to a {@link DockFrontend}.
 * @author Benjamin Sigg
 * @param <D> the kind of elements read by this factory
 * @param <P> the kind of perspective read by this factory
 * @param <L> the kind of data the wrapped factory needs
 */
public class RegisteringDockFactory<D extends Dockable, P extends PerspectiveElement, L> implements DockFactory<D, P, BackupFactoryData<L>> {
    /** the {@link DockFrontend} to which this factory will add new elements */
    private DockFrontend frontend;
    /** delegate used to read new elements */
    private DockFactory<D, P, L> factory;
    
    /**
     * Creates a new factory
     * @param frontend the frontend to which this factory will add new elements
     * @param factory delegated used to read and create new elements
     */
    public RegisteringDockFactory( DockFrontend frontend, DockFactory<D, P, L> factory ){
        this.frontend = frontend;
        this.factory = factory;
    }

    public String getID() {
        return factory.getID();
    }

    public void estimateLocations( BackupFactoryData<L> layout, LocationEstimationMap children ){
    	factory.estimateLocations( layout.getData(), children );
    }
    
    public BackupFactoryData<L> getLayout( D element, Map<Dockable, Integer> children ) {
        return new BackupFactoryData<L>( null, factory.getLayout( element, children ));
    }

    public D layout( BackupFactoryData<L> layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        D element = factory.layout( layout.getData(), children, placeholders );
        if( element != null ){
            String id = layout.getIdentifier();
            if( id.startsWith( "dockable" )){
                id = id.substring( "dockable".length() );
                if( frontend.getDockable( id ) == null ){
                    frontend.addDockable( id, element );
                }
            }
        }
        return element;
    }

    public D layout( BackupFactoryData<L> layout, PlaceholderStrategy placeholders ) {
        D element = factory.layout( layout.getData(), placeholders );
        if( element != null ){
            String id = layout.getIdentifier();
            if( id.startsWith( "dockable" )){
                id = id.substring( "dockable".length() );
                if( frontend.getDockable( id ) == null ){
                    frontend.addDockable( id, element );
                }
            }
        }
        return element;
    }

    public BackupFactoryData<L> read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException {
        return new BackupFactoryData<L>( null, factory.read( in, placeholders ));
    }

    public BackupFactoryData<L> read( XElement element, PlaceholderStrategy placeholders ) {
        return new BackupFactoryData<L>( null, factory.read( element, placeholders ));
    }

    public void setLayout( D element, BackupFactoryData<L> layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        factory.setLayout( element, layout.getData(), children, placeholders );
    }

    public void setLayout( D element, BackupFactoryData<L> layout, PlaceholderStrategy placeholders ) {
        factory.setLayout( element, layout.getData(), placeholders );
    }

    public void write( BackupFactoryData<L> layout, DataOutputStream out ) throws IOException {
        factory.write( layout.getData(), out );
    }

    public void write( BackupFactoryData<L> layout, XElement element ) {
        factory.write( layout.getData(), element );
    }

	public BackupFactoryData<L> getPerspectiveLayout( P element, Map<PerspectiveDockable, Integer> children ){
		return new BackupFactoryData<L>( null, factory.getPerspectiveLayout( element, children ) );
	}

	public P layoutPerspective( BackupFactoryData<L> layout, Map<Integer, PerspectiveDockable> children ){
		if( layout.getData() == null ){
			return null;
		}
		return factory.layoutPerspective( layout.getData(), children );
	}
	
	public void layoutPerspective( P perspective, BackupFactoryData<L> layout, Map<Integer,PerspectiveDockable> children ){
		if( layout.getData() != null ){
			factory.layoutPerspective( perspective, layout.getData(), children );
		}
	}
}
