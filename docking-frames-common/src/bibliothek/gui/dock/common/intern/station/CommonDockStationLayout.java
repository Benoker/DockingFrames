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
import java.io.DataInputStream;
import java.io.IOException;

import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.layout.DockLayout;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.xml.XElement;

/**
 * Information about the layout of a {@link CommonDockStation}, used by the {@link CommonDockStationFactory}
 * to store and load the layout.
 * @author Benjamin Sigg
 */
public class CommonDockStationLayout {
	/** The unique identifier of the {@link CommonDockStation} */
	private String id;
	
	/** Whether the {@link CStation} is a {@link CControl#isRootStation(CStation) root station}*/
	private boolean root;
	
	/** The unique identifier of the {@link DockFactory} that is used to read or apply the layout */
	private String factoryId;
	
	/** The actual layout information */
	private DockLayoutInfo layout = new DockLayoutInfo();
	
	/**
	 * Creates a new layout.
	 * @param id the unique identifier of the described {@link CommonDockStation}, might be <code>null</code>
	 * @param root whether the {@link CStation} is a root station
	 * @param factoryId the unique identifier of the {@link DockFactory} that is used to read or write the actual 
	 * layout
	 * @param layout the layout that was loaded by the factory <code>factoryId</code>
	 */
	public CommonDockStationLayout( String id, boolean root, String factoryId, DockLayout<?> layout ){
		if( factoryId == null ){
			throw new IllegalArgumentException( "factoryId must not be null" );
		}
		
		this.id = id;
		this.root = root;
		this.factoryId = factoryId;
		if( layout != null ){
			this.layout.setData( layout );
		}
	}
	
	
	/**
	 * Creates a new layout.
	 * @param id the unique identifier of the described {@link CommonDockStation}, might be <code>null</code>
	 * @param root whether the {@link CStation} is a root station
	 * @param factoryId the unique identifier of the {@link DockFactory} that is used to read or write the actual 
	 * layout
	 * @param layout the layout that might be loaded by the factory <code>factoryId</code>
	 */
	public CommonDockStationLayout( String id, boolean root, String factoryId, byte[] layout ){
		if( factoryId == null ){
			throw new IllegalArgumentException( "factoryId must not be null" );
		}
		
		this.id = id;
		this.root = root;
		this.factoryId = factoryId;
		if( layout != null ){
			this.layout.setData( layout );
		}
	}
	
	/**
	 * Creates a new layout.
	 * @param id the unique identifier of the described {@link CommonDockStation}, might be <code>null</code>
	 * @param root whether the {@link CStation} is a root station
	 * @param factoryId the unique identifier of the {@link DockFactory} that is used to read or write the actual 
	 * layout
	 * @param layout the layout that might be loaded by the factory <code>factoryId</code>
	 */
	public CommonDockStationLayout( String id, boolean root, String factoryId, XElement layout ){
		if( factoryId == null ){
			throw new IllegalArgumentException( "factoryId must not be null" );
		}
		
		this.id = id;
		this.root = root;
		this.factoryId = factoryId;
		if( layout != null ){
			this.layout.setData( layout );
		}
	}

	/**
	 * Updates the contents of the internal {@link DockLayoutInfo} using <code>factory</code> to read
	 * a byte array or an {@link XElement}.
	 * @param factory the factory used to read the layout
	 * @param placeholders the placeholders that may be used
	 */
	public void updateLayout( DockFactory<?, ?, Object> factory, PlaceholderStrategy placeholders ){
		try{
			Object data = null;
			
			switch( layout.getKind() ){
				case BYTE:
					data = factory.read( new DataInputStream( new ByteArrayInputStream( layout.getDataByte() ) ), placeholders );
					break;
				case XML:
					data = factory.read( layout.getDataXML(), placeholders );
					break;
			}
			
			if( data != null ){
				layout.setData( new DockLayout<Object>( factory.getID(), data ) );
			}
		}
		catch( IOException e ){
			// since a ByteArrayInputStream never throws an IOException this should never happen
			throw new IllegalStateException( e );
		}
	}
	
	/**
	 * Gets the unique id of the {@link CommonDockStation} which is described by this layout.
	 * @return the unique id, might be <code>null</code>
	 */
	public String getId(){
		return id;
	}

	/**
	 * Tells whether the {@link CStation} was {@link CControl#addStation(CStation, boolean) added} to the
	 * {@link CControl} with the <code>root</code> flag set to <code>true</code>.
	 * @return the root flag
	 */
	public boolean isRoot(){
		return root;
	}
	
	/**
	 * Gets the unique id of the {@link DockFactory} that is used to read and store the actual layout.
	 * @return the factory to be used, not <code>null</code>
	 */
	public String getFactoryId(){
		return factoryId;
	}

	/**
	 * Gets the layout information as byte array, assuming that the layout information is stored
	 * as byte array.
	 * @return the layout information or <code>null</code> if not stored in byte array format
	 */
	public byte[] getLayoutBytes(){
		return layout.getDataByte();
	}
	

	/**
	 * Gets the layout information as xml element, assuming that the layout information is stored
	 * in xml.
	 * @return the layout information or <code>null</code> if not stored in xml format
	 */
	public XElement getLayoutXML(){
		return layout.getDataXML();
	}
	
	/**
	 * Gets the layout information that was produced the {@link DockFactory} with id {@link #getFactoryId()}.
	 * @return the layout information or <code>null</code> if not present in object format
	 */
	public DockLayout<?> getLayout(){
		return layout.getDataLayout();
	}
}
