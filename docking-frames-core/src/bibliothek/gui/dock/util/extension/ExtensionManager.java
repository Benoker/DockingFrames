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
package bibliothek.gui.dock.util.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bibliothek.gui.DockController;

/**
 * Manages a set of {@link Extension}s.
 * @author Benjamin Sigg
 */
public class ExtensionManager {
	/** the controller in whose realm this manager works */
	private DockController controller;
	
	/** all the extensions of this manager */
	private List<Extension> extensions = new ArrayList<Extension>();
	
	/** whether the extensions are installed or not */
	private boolean alive = false;
	
	/**
	 * Creates a new manager.
	 * @param controller the controller in whose realm this manager works
	 */
	public ExtensionManager( DockController controller ){
		this.controller = controller;
		tryLoadDefaultExtensions();
	}
	
	/**
	 * Tries to load the standard extensions that are developed alongside with the
	 * main-framework.
	 */
	protected void tryLoadDefaultExtensions(){
		String[] list = { "glass.eclipse.GlassExtension",
				"bibliothek.gui.ToolbarExtension" };
		for( String className : list ){
			try {
				tryLoadExtension( className );
			} catch( ClassNotFoundException e ) {
				// ignore
			} catch( InstantiationException e ) {
				e.printStackTrace();
			} catch( IllegalAccessException e ) {
				// ignore
			}
		}
	}
	
	private void tryLoadExtension( String className ) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<?> clazz = Class.forName( className );
		Object extension = clazz.newInstance();
		if( extension instanceof Extension ){
			add( (Extension)extension );
		}
	}
	
	/**
	 * Adds <code>extension</code> to the list of extensions.
	 * @param extension the new extension
	 */
	public void add( Extension extension ){
		extensions.add( extension );
		if( alive ){
			extension.install( controller );
		}
	}
	
	/**
	 * Removes <code>extension</code> from the list of extensions.
	 * @param extension the extension to remove
	 */
	public void remove( Extension extension ){
		if( extensions.remove( extension ) ){
			if( alive ){
				extension.uninstall( controller );
			}
		}
	}
	
	/**
	 * Gets a list of all extensions that are currently known to this manager.
	 * @return the list of extensions
	 */
	public Extension[] getExtensions(){
		return extensions.toArray( new Extension[ extensions.size() ] );
	}
	
	/**
	 * Loads all extensions matching <code>name</code>.
	 * @param <E> the type of extensions that is loaded
	 * @param name the name of the extensions
	 * @return a list containing all non-<code>null</code> extensions, may be empty
	 */
	public <E> List<E> load( ExtensionName<E> name ){
		List<E> result = new ArrayList<E>();
		for( Extension extension : extensions ){
			Collection<E> es = extension.load( controller, name );
			if( es != null ){
				result.addAll( es );
			}
		}
		return result;
	}
	
	/**
	 * Starts up all extensions.
	 */
	public void init(){
		alive = true;
		for( Extension extension : extensions ){
			extension.install( controller );
		}
	}
	
	/**
	 * Stops and removes all extensions.
	 */
	public void kill(){
		if( alive ){
			alive = false;
			for( Extension extension : extensions ){
				extension.uninstall( controller );
			}
			extensions.clear();
		}
	}
	
}
