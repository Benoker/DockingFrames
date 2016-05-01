/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.split;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Path;
import bibliothek.util.xml.XElement;

/**
 * This property references a placeholder that is already present within the
 * tree of a {@link SplitDockStation}. 
 * @author Benjamin Sigg
 */
public class SplitDockPlaceholderProperty extends AbstractDockableProperty {
	/** the referenced placeholder */
	private Path placeholder;
	/** backup location should the placeholder not be found */
	private DockableProperty backup;
	
	/**
	 * Creates a new empty property
	 */
	public SplitDockPlaceholderProperty(){
		// nothing
	}
	
	/**
	 * Creates a new property.
	 * @param placeholder the referenced placeholder
	 */
	public SplitDockPlaceholderProperty( Path placeholder ){
		this( placeholder, (DockableProperty)null );
	}

	/**
	 * Creates a new property.
	 * @param placeholder the referenced placeholder
	 * @param backup backup location to be used if <code>placeholder</code> is not found
	 */
	public SplitDockPlaceholderProperty( Path placeholder, SplitDockProperty backup ){
		this( placeholder, (DockableProperty)backup );
	}

	/**
	 * Creates a new property.
	 * @param placeholder the referenced placeholder
	 * @param backup backup location to be used if <code>placeholder</code> is not found
	 */
	public SplitDockPlaceholderProperty( Path placeholder, SplitDockPathProperty backup ){
		this( placeholder, (DockableProperty)backup );
	}
	
	/**
	 * Creates a new property.
	 * @param placeholder the referenced placeholder
	 * @param backup backup location to be used if <code>placeholder</code> is not found
	 */
	private SplitDockPlaceholderProperty( Path placeholder, DockableProperty backup ){
		if( placeholder == null )
			throw new IllegalArgumentException( "placeholder must not be null" );
		this.placeholder = placeholder;
		this.backup = backup;
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + "[placeholder=" + getPlaceholder() + ", backup=" + getBackup() + ", successor=" + getSuccessor() + "]";
	}
	
	/**
	 * Gets the placeholder this property references to.
	 * @return the placeholder, not <code>null</code>
	 */
	public Path getPlaceholder(){
		return placeholder;
	}
	
	/**
	 * Gets some {@link DockableProperty} that works as backup property
	 * if the referenced placeholder is not found.
	 * @param target the node that asks for this location
	 * @return the location, not <code>null</code>
	 */
	public DockableProperty toLocation( SplitNode target ){
		if( backup == null ){
			return toSplitLocation( target );
		}
		return backup;
	}
	
	/**
	 * Gets a {@link SplitDockProperty} that works as backup property
	 * if the referenced placeholder is not found.
	 * @param target the node that asks for this location
	 * @return the location, not <code>null</code>
	 */
	public SplitDockProperty toSplitLocation( SplitNode target ){
		if( backup instanceof SplitDockProperty )
			return (SplitDockProperty)backup;
		
		if( backup instanceof SplitDockPathProperty )
			return ((SplitDockPathProperty)backup).toLocation( target );
		
		SplitDockProperty result = new SplitDockProperty( target.getX(), target.getY(), target.getWidth(), target.getHeight() );
		result.setSuccessor( getSuccessor() );
		return result;
	}
	
	@Override
	public void setSuccessor( DockableProperty successor ){
		super.setSuccessor( successor );
		if( backup != null ){
			backup.setSuccessor( successor );
		}
	}
	
	/**
	 * Gets the backup location that was set when this {@link SplitDockPlaceholderProperty} 
	 * was created.
	 * @return the backup location, might be <code>null</code>
	 */
	public DockableProperty getBackup(){
		return backup;
	}

	public SplitDockPlaceholderProperty copy(){
		SplitDockPlaceholderProperty copy = new SplitDockPlaceholderProperty( placeholder, backup == null ? null : backup.copy() );
		copy( copy );
		return copy;
	}

	public String getFactoryID(){
		return SplitDockPlaceholderPropertyFactory.ID;
	}

	public void store( DataOutputStream out ) throws IOException{
		out.writeUTF( placeholder.toString() );
		if( backup == null ){
			out.writeByte( 0 );
		}
		else if( backup instanceof SplitDockProperty ){
			out.writeByte( 1 );
			backup.store( out );
		}
		else if( backup instanceof SplitDockPathProperty ){
			out.writeByte( 2 );
			backup.store( out );
		}
		else{
			throw new IllegalStateException( "never happens" );
		}
	}
	
	public void load( DataInputStream in ) throws IOException{
		placeholder = new Path( in.readUTF() );
		switch( in.readByte() ){
			case 0:
				backup = null;
				break;
			case 1:
				backup = new SplitDockProperty();
				backup.load( in );
				break;
			case 2:
				backup = new SplitDockPathProperty();
				backup.load( in );
				break;
			default:
				throw new IllegalArgumentException( "unknown type of backup property" );
		}
	}
	
	public void store( XElement element ){
		element.addElement( "placeholder" ).setString( placeholder.toString() );
		if( backup instanceof SplitDockProperty ){
			backup.store( element.addElement( "backup-location" ) );
		}
		else if( backup instanceof SplitDockPathProperty ){
			backup.store( element.addElement( "backup-path" ) );
		}
	}
	
	public void load( XElement element ){
		placeholder = new Path( element.getElement( "placeholder" ).getString() );
		backup = null;
		XElement xchild = element.getElement( "backup-path" );
		if( xchild != null ){
			backup = new SplitDockPathProperty();
			backup.load( xchild );
		}
		else{
			xchild = element.getElement( "backup-location" );;
			if( xchild != null ){
				backup = new SplitDockProperty();
				backup.load( xchild );
			}
		}
	}
}
