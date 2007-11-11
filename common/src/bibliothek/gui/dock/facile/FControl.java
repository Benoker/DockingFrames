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
package bibliothek.gui.dock.facile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.facile.intern.FControlAccess;
import bibliothek.gui.dock.facile.intern.FacileDockable;

public class FControl {
	private DockFrontend frontend;
	
	/** the set of known factories */
	private Map<String, FactoryProperties> factories = 
		new HashMap<String, FactoryProperties>();
	
	/** the set of {@link FMultipleDockable}s */
	private List<FMultipleDockable> multiDockables = 
		new ArrayList<FMultipleDockable>();
	
	/**
	 * Access to the internal methods of this control
	 */
	private FControlAccess access = new Access();
	
	/**
	 * Creates a new control
	 */
	public FControl(){
		frontend = new DockFrontend();
	}
	
	/**
	 * Adds a dockable to this control. The dockable can be made visible afterwards.
	 * @param dockable the new element to show
	 */
	public void add( FSingleDockable dockable ){
		if( dockable == null )
			throw new NullPointerException( "dockable must not be null" );
		
		if( dockable.getControl() != null )
			throw new IllegalStateException( "dockable is already part of a control" );

		dockable.setControl( access );
		frontend.add( dockable.getDockable(), "single " + dockable.getId() );
		frontend.setHideable( dockable.getDockable(), false );
	}
	
	/**
	 * Adds a dockable to this control. The dockable can be made visible afterwards.
	 * @param dockable the new element to show
	 */
	public void add( FMultipleDockable dockable ){
		if( dockable == null )
			throw new NullPointerException( "dockable must not be null" );
		
		if( dockable.getControl() != null )
			throw new IllegalStateException( "dockable is already part of a control" );
		
		String factory = access.getFactoryId( dockable.getFactory() );
		if( factory == null ){
			throw new IllegalStateException( "the factory for a MultipleDockable is not registered" );
		}
		
		dockable.setControl( access );
		multiDockables.add( dockable );
	}
	
	/**
	 * Removes a dockable from this control. The dockable is made invisible.
	 * @param dockable the element to remove
	 */
	public void remove( FSingleDockable dockable ){
		if( dockable == null )
			throw new NullPointerException( "dockable must not be null" );
		
		if( dockable.getControl() == access ){
			dockable.setVisible( false );
			frontend.remove( dockable.getDockable() );
			dockable.setControl( null );
		}
	}
	
	/**
	 * Removes a dockable from this control. The dockable is made invisible.
	 * @param dockable the element to remove
	 */
	public void remove( FMultipleDockable dockable ){
		if( dockable == null )
			throw new NullPointerException( "dockable must not be null" );
		
		if( dockable.getControl() == access ){
			dockable.setVisible( false );
			frontend.remove( dockable.getDockable() );
			multiDockables.remove( dockable );
			String factory = access.getFactoryId( dockable.getFactory() );
			
			if( factory == null ){
				throw new IllegalStateException( "the factory for a MultipleDockable is not registered" );
			}
			
			factories.get( factory ).count--;
			dockable.setControl( null );
		}
	}
	
	/**
	 * Adds a factory to this control. The factory will create {@link FMultipleDockable}s
	 * when a layout is loaded.
	 * @param id the unique id of the factory
	 * @param factory the new factory
	 */
	public void add( final String id, final FDockableFactory factory ){
		if( id == null )
			throw new NullPointerException( "id must not be null" );
		
		if( factory == null )
			throw new NullPointerException( "factory must not be null" );
		
		if( factories.containsKey( id )){
			throw new IllegalArgumentException( "there is already a factory named " + id );
		}
		
		FactoryProperties properties = new FactoryProperties();
		properties.factory = factory;
		
		factories.put( id, properties );
		
		frontend.registerFactory( new DockFactory<FacileDockable>(){
			public String getID(){
				return id;
			}

			public FacileDockable read( Map<Integer, Dockable> children, boolean ignoreChildren, DataInputStream in ) throws IOException{
				FMultipleDockable dockable = factory.read( in );
				add( dockable );
				return dockable.getDockable();
			}

			public void read( Map<Integer, Dockable> children, boolean ignoreChildren, FacileDockable preloaded, DataInputStream in ) throws IOException{
				// ignore
			}

			public void write( FacileDockable element, Map<Dockable, Integer> children, DataOutputStream out ) throws IOException{
				factory.write( (FMultipleDockable)element.getDockable(), out );
			}
		});
	}
	
	/**
	 * Gets the representation of the layer beneath the facile-layer.
	 * @return the entry point to DockingFrames
	 */
	public DockFrontend getFrontend(){
		return frontend;
	}
	
	/**
	 * Writes the current and all known layouts into <code>file</code>.
	 * @param file the file to override
	 * @throws IOException if the file can't be written
	 */
	public void write( File file ) throws IOException{
		DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file )));
		write( out );
		out.close();
	}
	
	/**
	 * Writes the current and all known layouts into <code>out</code>.
	 * @param out the stream to write into
	 * @throws IOException if the stream is not writable
	 */
	public void write( DataOutputStream out ) throws IOException{
		out.writeInt( 1 );
		frontend.write( out );
	}
	
	/**
	 * Reads the current and other known layouts from <code>file</code>.
	 * @param file the file to read from
	 * @throws IOException if the file can't be read
	 */
	public void read( File file ) throws IOException{
		DataInputStream in = new DataInputStream( new BufferedInputStream( new FileInputStream( file )));
		read( in );
		in.close();
	}
	
	/**
	 * Reads the current and other known layouts from <code>in</code>.
	 * @param in the stream to read from
	 * @throws IOException if the stream can't be read
	 */
	public void read( DataInputStream in ) throws IOException{
		int version = in.readInt();
		if( version != 1 )
			throw new IOException( "Version of stream unknown, expected 1 but found: " + version );
		
		frontend.read( in );
	}
	
	/**
	 * Stores the current layout with the given name.
	 * @param name the name of the current layout.
	 */
	public void save( String name ){
		frontend.save( name );
	}
	
	/**
	 * Loads an earlier stored layout.
	 * @param name the name of the layout.
	 */
	public void load( String name ){
		frontend.load( name );
	}
	
	/**
	 * Deletes a layout that has been stored earlier.
	 * @param name the name of the layout to delete
	 */
	public void delete( String name ){
		frontend.delete( name );
	}
	
	/**
	 * Gets a list of all layouts that are currently known.
	 * @return the list of layouts
	 */
	public String[] layouts(){
		Set<String> settings = frontend.getSettings();
		return settings.toArray( new String[ settings.size() ] );
	}
	
	/**
	 * Properties associated with one factory.
	 * @author Benjamin Sigg
	 *
	 */
	private class FactoryProperties{
		/** the associated factory */
		public FDockableFactory factory;
		/** the number of {@link FMultipleDockable} that belong to {@link #factory} */
		public int count = 0;
	}
	
	/**
	 * A class giving access to the internal methods of the enclosing
	 * {@link FControl}.
	 * @author Benjamin Sigg
	 */
	private class Access implements FControlAccess{
		public FControl getOwner(){
			return FControl.this;
		}
		
		public void hide( FDockable dockable ){
			frontend.hide( dockable.getDockable() );
		}
		
		public void show( FDockable dockable ){
			frontend.show( dockable.getDockable() );
		}
		
		public boolean isVisible( FDockable dockable ){
			return frontend.isShown( dockable.getDockable() );
		}
		
		public String getFactoryId( FDockableFactory factory ){
			for( Map.Entry<String, FactoryProperties> entry : factories.entrySet() ){
				if( entry.getValue().factory == factory )
					return entry.getKey();
			}
			
			return null;
		}
	}
}
