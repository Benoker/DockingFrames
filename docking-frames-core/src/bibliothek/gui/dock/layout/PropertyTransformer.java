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

package bibliothek.gui.dock.layout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.station.flap.FlapDockPropertyFactory;
import bibliothek.gui.dock.station.screen.ScreenDockPropertyFactory;
import bibliothek.gui.dock.station.split.SplitDockFullScreenPropertyFactory;
import bibliothek.gui.dock.station.split.SplitDockPathPropertyFactory;
import bibliothek.gui.dock.station.split.SplitDockPlaceholderPropertyFactory;
import bibliothek.gui.dock.station.split.SplitDockPropertyFactory;
import bibliothek.gui.dock.station.stack.StackDockPropertyFactory;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A PropertyTransformer can read and write instances of {@link DockableProperty}
 * , assuming that a factory is installed for the property.
 * 
 * @author Benjamin Sigg
 * 
 */
public class PropertyTransformer {
	/** Name of the {@link ExtensionName} that allows to load additional {@link DockablePropertyFactory}s */
	public static final Path FACTORY_EXTENSION = new Path( "dock.DockablePropertyFactory" );
	
	/** Name of the only property of an {@link ExtensionName} that points to <code>this</code> */
	public static final String FACTORY_EXTENSION_PARAMETER = "transformer";
	
	private Map<String, DockablePropertyFactory> factories = new HashMap<String, DockablePropertyFactory>();

	/**
	 * Creates a new transformer, the factories for all {@link DockableProperty}s implemented 
	 * by this framework are installed.
	 * @param controller the controller in whose realm this transformer is used
	 */
	public PropertyTransformer(DockController controller){
		this( controller,
				SplitDockPropertyFactory.FACTORY,
				SplitDockPathPropertyFactory.FACTORY,
				SplitDockPlaceholderPropertyFactory.FACTORY,
				SplitDockFullScreenPropertyFactory.FACTORY,
				StackDockPropertyFactory.FACTORY,
				FlapDockPropertyFactory.FACTORY,
				ScreenDockPropertyFactory.FACTORY );
	}

	/**
	 * Creates a new transformer and installs <code>factories</code>.
	 * @param factories a list of factories to install
	 * @param controller the controller in whose realm this transformer is used
	 */
	public PropertyTransformer( DockController controller, DockablePropertyFactory... factories ){
		for (DockablePropertyFactory factory : factories){
			this.factories.put( factory.getID(), factory );
		}
		
		List<DockablePropertyFactory> extensions = controller.getExtensions().load( new ExtensionName<DockablePropertyFactory>( FACTORY_EXTENSION, DockablePropertyFactory.class, FACTORY_EXTENSION_PARAMETER, this ) );
		for( DockablePropertyFactory factory : extensions ){
			this.factories.put( factory.getID(), factory );
		}
	}

	/**
	 * Installs a factory 
	 * @param factory the new factory
	 */
	public void addFactory( DockablePropertyFactory factory ){
		factories.put( factory.getID(), factory );
	}

	/**
	 * Writes <code>property</code> and all its successors into <code>out</code>.
	 * @param property the property to write
	 * @param out a stream to write into
	 * @throws IOException if the stream throws an exception
	 */
	public void write( DockableProperty property, DataOutputStream out ) throws IOException{
		Version.write( out, Version.VERSION_1_0_4 );

		int count = 0;
		DockableProperty successor = property;
		while( successor != null ) {
			count++;
			successor = successor.getSuccessor();
		}

		out.writeInt( count );
		while( property != null ) {
			out.writeUTF( property.getFactoryID() );
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream datas = new DataOutputStream( bytes );
			property.store( datas );
			datas.close();
			byte[] written = bytes.toByteArray();
			out.writeInt( written.length );
			out.write( written );

			property = property.getSuccessor();
		}
	}

	/**
	 * Reads a property which was earlier stored. If the property had any
	 * successors, then they are read as well.
	 * @param in a stream to read from
	 * @return the properties
	 * @throws IOException if the property can't be read
	 */
	public DockableProperty read( DataInputStream in ) throws IOException{
		Version version = Version.read( in );
		version.checkCurrent();

		int count = in.readInt();

		DockableProperty property = null;
		DockableProperty base = null;

		for (int i = 0; i < count; i++) {
			String id = in.readUTF();
			DockablePropertyFactory factory = factories.get( id );
			if (factory == null)
				throw new IOException( "Unknown factory-id: " + id );

			DockableProperty temp = factory.createProperty();

			int length = in.readInt();
			byte[] data = new byte[length];
			int index = 0;

			while( index < length ) {
				int read = in.read( data, index, length - index );
				if (read < 0)
					throw new EOFException();
				index += read;
			}

			DataInputStream datas = new DataInputStream(
					new ByteArrayInputStream( data ) );
			temp.load( datas );
			datas.close();

			if (base == null) {
				base = temp;
				property = temp;
			} else {
				property.setSuccessor( temp );
				property = temp;
			}
		}

		return base;
	}

	/**
	 * Writes <code>property</code> and all its successors into
	 * <code>element</code>.
	 * @param property the property to write
	 * @param element an xml element to which this method will add some children
	 */
	public void writeXML( DockableProperty property, XElement element ){
		while( property != null ) {
			XElement xnode = element.addElement( "property" );
			xnode.addString( "factory", property.getFactoryID() );
			property.store( xnode );
			property = property.getSuccessor();
		}
	}

	/**
	 * Reads a {@link DockableProperty} and its successors from an xml element.
	 * @param element the element to read from
	 * @return the property or <code>null</code> if <code>element</code> is empty
	 * @throws IllegalArgumentException if a {@link DockablePropertyFactory} is missing.
	 */
	public DockableProperty readXML( XElement element ){
		DockableProperty base = null;
		DockableProperty property = null;

		for (XElement xnode : element.getElements( "property" )) {
			DockablePropertyFactory factory = factories.get( xnode.getString( "factory" ) );
			if (factory == null)
				throw new IllegalArgumentException( "Missing factory: " + xnode.getString( "factory" ) );

			DockableProperty next = factory.createProperty();
			next.load( xnode );

			if (property == null) {
				property = next;
				base = next;
			} else {
				property.setSuccessor( next );
				property = next;
			}
		}

		return base;
	}
}
