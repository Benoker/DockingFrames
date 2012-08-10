/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.station;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonSingleDockableFactory;
import bibliothek.gui.dock.common.perspective.CElementPerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.common.perspective.CommonDockStationPerspective;
import bibliothek.gui.dock.frontend.FrontendPerspectiveCache;
import bibliothek.gui.dock.layout.DockLayout;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A factory that is responsible for storing and loading the layout of the
 * {@link CommonDockStation}s. This factory actually forwards the work to
 * a real {@link DockFactory} and only stores meta-data (like "was the 
 * {@link CommonDockStation} as {@link SingleCDockable}?").
 * @author Benjamin Sigg
 */
public class CommonDockStationFactory implements DockFactory<CommonDockStation<?, ?>, CommonDockStationPerspective, CommonDockStationLayout>{
	/** The unique identifier of this factory */
	public static final String FACTORY_ID = "CommonDockStationFactory";
	
	/** The control in whose realm this factory is used */
	private CControl control;

	/** The factory used to create new {@link CommonDockStation}s that are also {@link SingleCDockable}s */
	private CommonSingleDockableFactory singleDockableFactory;
	
	/** Factory used to access missing {@link PerspectiveElement}s */
	private FrontendPerspectiveCache cache;
	
	/**
	 * Creates a new factory
	 * @param control the {@link CControl} in whose realm this factory works, not <code>null</code>
	 * @param cache used to create missing {@link PerspectiveElement}s, can be <code>null</code>
	 * @param singleDockableFactory the factory used to create new {@link CommonDockStation}s that are also {@link SingleCDockable}s, not <code>null</code>
	 */
	public CommonDockStationFactory( CControl control, FrontendPerspectiveCache cache, CommonSingleDockableFactory singleDockableFactory ){
		if( control == null ){
			throw new IllegalArgumentException( "control must not be null" );
		}
		if( singleDockableFactory == null ){
			throw new IllegalArgumentException( "singleDockableFactory must not be null" );
		}
		this.control = control;
		this.cache = cache;
		this.singleDockableFactory = singleDockableFactory;
	}
	
	/**
	 * Creates a new {@link CommonDockStation} whose {@link CStation} is also a {@link SingleCDockable} with
	 * unique identifier <code>id</code>.
	 * @param id the unique identifier of a {@link SingleCDockable}
	 * @return the new station or <code>null</code> if <code>id</code> is unknown
	 */
	protected CommonDockStation<?, ?> createStation( String id ){
    	SingleCDockableFactory factory = singleDockableFactory.getFactory( id );
    	if( factory == null ){
    		return null;
    	}
    	
    	SingleCDockable dockable = factory.createBackup( id );
    	if( dockable == null ){
    		return null;
    	}
    	
        String factoryId = dockable.intern().getFactoryID();
        if( !factoryId.equals( getID() )){
        	throw new IllegalArgumentException( "Wrong type of dockable for unique id '" + id + "': The backup factory created a dockable which expects a factory of type '" + factoryId + 
        			"',  but the call was done from a factory of type '" + getID() + "'" );
        }
        
    	CStation<?> station = dockable.asStation();
    	if( station == null ){
    		System.err.println( "unique identifier '" + id + "' was supposed to be a CStation, but factory created a dockable" );
    		return null;
    	}
    	
    	return (CommonDockStation<?, ?>)station.getStation();  
	}
	
	/**
	 * Register <code>station</code> at the {@link CControl} in whose realm this factory works.
	 * @param station the station to register
	 * @param root whether to set the root flag or not
	 */
	protected void registerStation( CStation<?> station, boolean root ){
		if( control.getStation( station.getUniqueId() ) != station ){
			control.addStation( station, root );
		}
		CDockable dockable = station.asDockable();
		if( dockable != null ){
			if( dockable instanceof SingleCDockable ){
				SingleCDockable single = (SingleCDockable)dockable;
				if( control.getSingleDockable( single.getUniqueId() ) != single ){
					control.addDockable( single );
				}
			}
		}
	}

	public String getID(){
		return FACTORY_ID;
	}
	
	@SuppressWarnings("unchecked")
	public CommonDockStationLayout getLayout( CommonDockStation<?, ?> element, Map<Dockable, Integer> children ){
		String factoryId = element.getConverterID();
		DockFactory<DockElement, ?, ?> factory = (DockFactory<DockElement, ?, ?>)control.intern().getDockFactory( factoryId );
		if( factory == null ){
			return null;
		}
		
		Object layout = factory.getLayout( element, children );
		if( layout == null ){
			return null;
		}
		
		CDockable dockable = element.getStation().asDockable();
		String id = null;
		if( dockable instanceof SingleCDockable ){
			id = ((SingleCDockable)dockable).getUniqueId();
		}
		
		boolean root = control.isRootStation( element.getStation() );
		return new CommonDockStationLayout( id, root, factoryId, new DockLayout<Object>( factoryId, layout ) );
	}

	public CommonDockStation<?, ?> layout( CommonDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		CommonDockStation<?, ?> station = createStation( layout.getId() );
		if( station == null ){
			return null;
		}
		
		registerStation( station.getStation(), layout.isRoot() );
		
		setLayout( station, layout, children, placeholders );
		
		return station;
	}

	public CommonDockStation<?, ?> layout( CommonDockStationLayout layout, PlaceholderStrategy placeholders ){
		return layout( layout, null, placeholders );
	}
	
	@SuppressWarnings("unchecked")
	public void setLayout( CommonDockStation<?, ?> element, CommonDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
		String factoryId = element.getConverterID();
		DockFactory<DockElement, ?, Object> factory = (DockFactory<DockElement, ?, Object>)control.intern().getDockFactory( factoryId );
		if( factory == null ){
			return;
		}
		
		layout.updateLayout( factory, placeholders );
		DockLayout<?> data = layout.getLayout();
		if( data == null ){
			return;
		}
		
		if( children == null ){
			factory.setLayout( element, data.getData(), placeholders );
		}
		else{
			factory.setLayout( element, data.getData(), children, placeholders );
		}
	}

	public void setLayout( CommonDockStation<?, ?> element, CommonDockStationLayout layout, PlaceholderStrategy placeholders ){
		setLayout( element, layout, null, placeholders );	
	}
	
	@SuppressWarnings("unchecked")
	public void estimateLocations( CommonDockStationLayout layout, LocationEstimationMap children ){
		String factoryId = null;
		if( layout != null ){
			factoryId = layout.getFactoryId();
		}
		if( factoryId == null ){
			return;
		}
		DockFactory<DockElement, ?, Object> factory = (DockFactory<DockElement, ?, Object>)control.intern().getDockFactory( factoryId );
		if( factory == null ){
			return;
		}
		layout.updateLayout( factory, null );
		DockLayout<?> data = layout.getLayout();
		if( data == null ){
			return;
		}
		factory.estimateLocations( data.getData(), children );
	}

	public CommonDockStationPerspective layoutPerspective( CommonDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		CommonDockStationPerspective element = null;
		if( cache != null ){
			element = (CommonDockStationPerspective)cache.get( layout.getId(), layout.isRoot() );
		}
		if( element == null ){
			return null;
		}
		
		layoutPerspective( element, layout, children );
		return element;
	}

	@SuppressWarnings("unchecked")
	public void layoutPerspective( CommonDockStationPerspective element, CommonDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
		CStationPerspective station = element.getElement().asStation();
		station.setRoot( layout.isRoot() );
		
		DockFactory<?, PerspectiveElement, Object> factory = (DockFactory<?, PerspectiveElement, Object>)control.intern().getDockFactory( layout.getFactoryId() );
		if( factory == null ){
			return;
		}
		
		layout.updateLayout( factory, null );
		DockLayout<?> data = layout.getLayout();
		if( data == null ){
			return;
		}
		
		factory.layoutPerspective( element, data.getData(), children );
	}
	
	@SuppressWarnings("unchecked")
	public CommonDockStationLayout getPerspectiveLayout( CommonDockStationPerspective element, Map<PerspectiveDockable, Integer> children ){
		String factoryId = element.getConverterID();
		DockFactory<?, PerspectiveElement, Object> dockFactory = (DockFactory<?, PerspectiveElement, Object>)control.intern().getDockFactory( factoryId );
		DockFactory<?, PerspectiveElement, Object> factory = dockFactory;
		if( factory == null ){
			return null;
		}
		Object data = factory.getPerspectiveLayout( element, children );
		if( data == null ){
			return null;
		}
		
		CElementPerspective celement = element.getElement();
		CStationPerspective station = celement.asStation();
		
		String id = station.getUniqueId();
				
		return new CommonDockStationLayout( id, station.isRoot(), factoryId, new DockLayout<Object>( factoryId, data ) );
	}

	@SuppressWarnings("unchecked")
	public void write( CommonDockStationLayout layout, XElement element ){
		String factoryId = layout.getFactoryId();
		DockFactory<DockElement, ?, Object> factory = (DockFactory<DockElement, ?, Object>)control.intern().getDockFactory( factoryId );
		XElement content = layout.getLayoutXML();
		if( content == null ){
			layout.updateLayout( factory, null );
			DockLayout<?> data = layout.getLayout();
			if( data == null ){
				throw new XException( "data are null, but data were just updated" );
			}
			content = new XElement("content");
			factory.write( data.getData(), content );
		}
		
		String id = layout.getId();
		if( id != null ){
			element.addElement( "id" ).setString( id );
		}
		element.addElement( "root" ).setBoolean( layout.isRoot() );
		
		content.addString( "delegate", factoryId );
		element.addElement( content );
	}
	
	@SuppressWarnings("unchecked")
	public CommonDockStationLayout read( XElement element, PlaceholderStrategy placeholders ){
		String id = null;
		XElement xid = element.getElement( "id" );
		if( xid != null ){
			id = xid.getString();
		}
		
		boolean root = element.getElement( "root" ).getBoolean();
		
		XElement xcontent = element.getElement( "content" );
		if( xcontent == null ){
			throw new XException( "missing content element" );
		}
		
		String factoryId = xcontent.getString( "delegate" );
		DockFactory<DockElement, ?, Object> factory = (DockFactory<DockElement, ?, Object>)control.intern().getDockFactory( factoryId );
		if( factory == null ){
			return new CommonDockStationLayout( id, root, factoryId, xcontent );
		}
		else{
			Object data = factory.read( xcontent, placeholders );
			if( data == null ){
				return null;
			}
			
			return new CommonDockStationLayout( id, root, factoryId, new DockLayout<Object>( factoryId, data ) );
		}
	}

	@SuppressWarnings("unchecked")
	public void write( CommonDockStationLayout layout, DataOutputStream out ) throws IOException{
		String factoryId = layout.getFactoryId();
		DockFactory<DockElement, ?, Object> factory = (DockFactory<DockElement, ?, Object>)control.intern().getDockFactory( factoryId );
		byte[] content = layout.getLayoutBytes();
		if( content == null ){
			layout.updateLayout( factory, null );
			DockLayout<?> data = layout.getLayout();
			if( data == null ){
				throw new IOException( "data are null, but data were just updated" );
			}
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			factory.write( data.getData(), new DataOutputStream( bout ) );
			content = bout.toByteArray();
		}
		if( content == null ){
			throw new IOException( "unable to write layout, it could not be converted into byte-array format" );
		}
		
		Version.write( out, Version.VERSION_1_1_1 );
		
		String id = layout.getId();
		if( id == null ){
			out.writeBoolean( false );
		}
		else{
			out.writeBoolean( true );
			out.writeUTF( id );
		}
		
		out.writeBoolean( layout.isRoot() );
		
		out.writeUTF( factoryId );
		
		out.writeInt( content.length );
		out.write( content );
	}
	
	@SuppressWarnings("unchecked")
	public CommonDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		Version.read( in ).checkCurrent();
		
		String id;
		if( in.readBoolean() ){
			id = in.readUTF();
		}
		else{
			id = null;
		}
		
		boolean root = in.readBoolean();
		
		String factoryId = in.readUTF();
		
		int length = in.readInt();
		byte[] content = new byte[length];
		int offset = 0;
		while( offset < length ){
			int delta = in.read( content, offset, length - offset );
			if( delta == -1 ){
				throw new EOFException();
			}
			offset += delta;
		}
		
		DockFactory<DockElement, ?, Object> factory = (DockFactory<DockElement, ?, Object>)control.intern().getDockFactory( factoryId );
		if( factory == null ){
			return new CommonDockStationLayout( id, root, factoryId, content );
		}
		else{
			Object data = factory.read( new DataInputStream( new ByteArrayInputStream( content ) ), placeholders );
			if( data == null ){
				return null;
			}
			return new CommonDockStationLayout( id, root, factoryId, new DockLayout<Object>( factoryId, data ) );
		}
	}
}
