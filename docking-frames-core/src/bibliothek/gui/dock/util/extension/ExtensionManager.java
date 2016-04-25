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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	/** all shared extensions */
	private Map<ExtensionName<?>, Share<?>> shared = new HashMap<ExtensionName<?>, Share<?>>();
	
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
		String[] list = { 
				"glass.eclipse.GlassExtension",
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
	 * Creates a new {@link SharedExtension} object which uses <code>name</code> as key to read 
	 * extensions. The {@link SharedExtension} object can be {@link SharedExtension#bind() bound} 
	 * and {@link SharedExtension#unbind() unbound} at any time, it can be reused.
	 * @param name the name of the extension to share
	 * @return the shared extensions
	 */
	public <E> SharedExtension<E> share( final ExtensionName<E> name ){
		return new SharedExtension<E>(){
			private int bound = 0;
			
			@SuppressWarnings("unchecked")
			public void bind(){
				if( bound == 0 ){
					Share<E> share = (Share<E>)shared.get( name );
					if( share == null ){
						share = new Share<E>( name );
					}
					share.bind();
				}
				bound++;
			}
			
			@SuppressWarnings("unchecked")
			public void unbind(){
				if( bound == 0 ){
					throw new IllegalStateException( "cannot unbind, counter is already 0" );
				}
				bound--;
				if( bound == 0 ){
					Share<E> share = (Share<E>)shared.get( name );
					if( share != null ){
						share.unbind();
					}
				}
			}
			
			@SuppressWarnings("unchecked")
			public List<E> get(){
				if( bound == 0 ){
					throw new IllegalStateException( "SharedExtension is not bound" );
				}
				Share<E> share = (Share<E>)shared.get( name );
				return share.get();
			}
			
			public Iterator<E> iterator(){
				return get().iterator();
			}
			
			public ExtensionName<E> getName(){
				return name;
			}
		};
	}
	
	/**
	 * Represents a shared set of extensions.
	 * @author Benjamin Sigg
	 * @param <T> the type of object that is shared
	 */
	private class Share<T>{
		private int bound = 0;
		private List<T> extensions;
		private ExtensionName<T> name;
		
		/**
		 * Creates a new cache.
		 * @param name the key of the extensions
		 */
		public Share( ExtensionName<T> name ){
			this.name = name;
		}
		
		/**
		 * Connects this cache.
		 */
		public void bind(){
			if( bound == 0 ){
				shared.put( name, this );
			}
			bound++;
		}
		
		/**
		 * Disconnects this cache.
		 */
		public void unbind(){
			bound--;
			if( bound == 0 ){
				shared.remove( name );
			}
		}
		
		/**
		 * Gets the content of this cache.
		 * @return the content
		 */
		public List<T> get(){
			if( extensions == null ){
				extensions = Collections.unmodifiableList( load( name ) );
			}
			return extensions;
		}
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
