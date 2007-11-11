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

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.facile.intern.FControlAccess;

public class FControl {
	private DockFrontend frontend;
	
	/**
	 * Access to the internal methods of this control
	 */
	private FControlAccess access = new Access();
	
	public FControl(){
		frontend = new DockFrontend();

	}
	
	public void add( FSingleDockable dockable ){
		dockable.setControl( access );
		frontend.add( dockable.getDockable(), "single " + dockable.getId() );
	}
	
	public void add( FMultipleDockable dockable ){
		dockable.setControl( access );
		frontend.add( dockable.getDockable(), "multi " +  );
	}
	
	public void remove( FDockable dockable ){
		frontend.remove( dockable.getDockable() );
	}
	
	public void add( String id, FDockableFactory factory ){
		
	}
	
	/**
	 * Gets the representation of the layer beneath the facile-layer.
	 * @return the entry point to DockingFrames
	 */
	public DockFrontend getFrontend(){
		return frontend;
	}
	
	public void write( File file ) throws IOException{
		DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file )));
		write( out );
		out.close();
	}
	
	public void write( DataOutputStream out ) throws IOException{
		
	}
	
	public void read( File file ) throws IOException{
		DataInputStream in = new DataInputStream( new BufferedInputStream( new FileInputStream( file )));
		read( in );
		in.close();
	}
	
	public void read( DataInputStream in ) throws IOException{
		
	}
	
	public void save( String name ){
		
	}
	
	public void load( String name ){
		
	}
	
	public void delete( String name ){
		
	}
	
	public String[] layouts(){
		
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
	}
}
