/**
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A {@link DockSituation} that does not load or store all {@link DockElement DockElements}.
 * All elements which are registered by {@link #put(DockElement)} are stored in an
 * internal list. On writing, just a unique id is written to the stream. 
 * A {@link DockFactory} is still necessary for these elements, but the factory may
 * just do nothing.
 * @author Benjamin Sigg
 */
public class PredefinedDockSituation extends DockSituation {
	/** A mapping from ids to a list of elements which must not be created by a factory */
	private Map<String, DockElement> stringToElement = new HashMap<String, DockElement>();
	/** A mapping from a list of elements to their ids */
	private Map<DockElement, String> elementToString = new HashMap<DockElement, String>();
	
	private static final String KNOWN = "predefined";
	private static final String UNKNOWN = "delegate_";
	
	private final PreloadFactory factory = new PreloadFactory();
	
	/**
	 * Registers an element at this situation. When a stream is read, this
	 * element will be returned instead a newly created element (assuming
	 * that the element was written into the stream). The key for
	 * the element is generated automatically
	 * @param element the element
	 */
	public void put( DockElement element ){
		put( String.valueOf( stringToElement.size() ), element );
	}
	
	/**
	 * Registers an element at this situation. When a stream is read, this
	 * element will be returned instead a newly created element (assuming
	 * that the element was written into the stream).
	 * @param key the key of the element
	 * @param element the element
	 * @throws IllegalArgumentException if the key is already used
	 */
	public void put( String key, DockElement element ){
		if( stringToElement.containsKey( key ))
			throw new IllegalArgumentException( "Key does already exist: " + key );
		
		stringToElement.put( key, element );
		elementToString.put( element, key );
	}

	@Override
	protected String getID(DockElement dockable) {
		String key = elementToString.get( dockable );
		if( key == null )
			return UNKNOWN + super.getID( dockable );
		else
			return KNOWN;
	}
	
	@Override
	protected String getID(DockFactory<?,?> factory) {
		if( factory == this.factory )
			return KNOWN;
		else
			return UNKNOWN + super.getID(factory);
	}
	
	@Override
	protected DockFactory<? extends DockElement,? extends DockLayout> getFactory(String id) {
		if( KNOWN.equals( id ))
			return factory;
		else
			return super.getFactory( id );
	}
	
	/**
	 * A layout that stores another layout and maybe also an identifier
	 * for a preloaded element.
	 * @author Benjamin Sigg
	 */
	private static class PreloadedLayout implements DockLayout{
	    /** the id of the factory that created this layout */
	    private String factory;
	    
	    /** the layout that stores the content */
	    private DockLayout delegate;
	    /** the id of the element which was predefined */
	    private String preload;
	    
	    /**
	     * Creates a new layout.
	     * @param preload the element which was preloaded
	     * @param delegate the delegate which stores the content
	     */
	    public PreloadedLayout( String preload, DockLayout delegate ){
	        this.preload = preload;
	        this.delegate = delegate;
	    }
	    
	    public String getFactoryID() {
	        return factory;
	    }
	    
	    public void setFactoryID( String id ) {
	        factory = id;
	    }
	    
	    /**
	     * Gets the id of the element which was predefined.
	     * @return the identifier
	     */
	    public String getPreload() {
            return preload;
        }
	    
	    /**
	     * Gets the layout which stores the contents of the predefined element.
	     * @return the content
	     */
	    public DockLayout getDelegate() {
            return delegate;
        }
	}
	
	/**
	 * A factory which uses other factories as delegate. This factory does
	 * not always use the delegates, sometimes it does just read an element
	 * which was predefined in {@link PredefinedDockSituation}.
	 * @author Benjamin Sigg
	 */
	private class PreloadFactory implements DockFactory<DockElement, PreloadedLayout>{
		public String getID() {
			return KNOWN;
		}
		
		@SuppressWarnings("unchecked")
        public PreloadedLayout getLayout( DockElement element, Map<Dockable, Integer> children ) {
		    String factoryId = UNKNOWN + PredefinedDockSituation.super.getID( element );
		    DockFactory<DockElement, DockLayout> factory = (DockFactory<DockElement, DockLayout>)getFactory( factoryId );
		    if( factory == null )
		        throw new IllegalStateException( "Missing factory: " + factoryId );
		    
		    DockLayout layout = factory.getLayout( element, children );
		    layout.setFactoryID( factoryId );
		    
		    return new PreloadedLayout( elementToString.get( element ), layout );
		}
		
		@SuppressWarnings("unchecked")
        public void setLayout( DockElement element, PreloadedLayout layout,
		        Map<Integer, Dockable> children ) {
		 
		    String factoryId = layout.getDelegate().getFactoryID();
		    DockFactory<DockElement, DockLayout> factory = (DockFactory<DockElement, DockLayout>)getFactory( factoryId );
            if( factory != null ){
                factory.setLayout( element, layout.getDelegate(), children );
            }
		}
		
		@SuppressWarnings("unchecked")
        public void setLayout( DockElement element, PreloadedLayout layout ) {
		    String factoryId = layout.getDelegate().getFactoryID();
            DockFactory<DockElement, DockLayout> factory = (DockFactory<DockElement, DockLayout>)getFactory( factoryId );
            if( factory != null ){
                factory.setLayout( element, layout.getDelegate() );
            }
		}
		
		@SuppressWarnings("unchecked")
        public DockElement layout( PreloadedLayout layout, Map<Integer, Dockable> children ) {
		    DockElement element = stringToElement.get( layout.getPreload() );
		    if( element == null )
		        return null;
		    
		    setLayout( element, layout, children );
		    return element;
		}
		
		@SuppressWarnings("unchecked")
        public DockElement layout( PreloadedLayout layout ) {
            DockElement element = stringToElement.get( layout.getPreload() );
            if( element == null )
                return null;
            
            setLayout( element, layout );
            return element;
		}
		
		@SuppressWarnings("unchecked")
        public void write( PreloadedLayout layout, DataOutputStream out ) throws IOException {
		    out.writeUTF( layout.getPreload() );
		    
		    DockLayout delegate = layout.getDelegate();
		    String factoryId = delegate.getFactoryID();
		    DockFactory<DockElement, DockLayout> factory = (DockFactory<DockElement, DockLayout>)getFactory( factoryId );
		    if( factory == null )
		        throw new IOException( "Missing factory: " + factoryId );
		    
		    out.writeUTF( factoryId );
		    factory.write( delegate, out );
		}
		
		@SuppressWarnings("unchecked")
        public PreloadedLayout read( DataInputStream in ) throws IOException {
		    String preloaded = in.readUTF();
		    String factoryId = in.readUTF();
		    DockFactory<DockElement, DockLayout> factory = (DockFactory<DockElement, DockLayout>)getFactory( factoryId );
            if( factory == null )
                return null;
            
            DockLayout delegate = factory.read( in );
            if( delegate == null )
                return null;
            
            delegate.setFactoryID( factoryId );
            return new PreloadedLayout( preloaded, delegate );
		}
		
		@SuppressWarnings("unchecked")
        public void write( PreloadedLayout layout, XElement element ) {
		    DockLayout delegate = layout.getDelegate();
		    String factoryId = delegate.getFactoryID();
            DockFactory<DockElement, DockLayout> factory = (DockFactory<DockElement, DockLayout>)getFactory( factoryId );
            if( factory == null )
                throw new XException( "Missing factory: " + factoryId );
            
		    element.addElement( "replacement" ).addString( "id", layout.getPreload() );
		    XElement xdelegate = element.addElement( "delegate" );
		    xdelegate.addString( "id", factoryId );
		    factory.write( delegate, xdelegate );
		}
		
		@SuppressWarnings("unchecked")
        public PreloadedLayout read( XElement element ) {
		    String preload = element.getElement( "replacement" ).getString( "id" );
		    
		    XElement xdelegate = element.getElement( "delegate" );
		    String factoryId = xdelegate.getString( "id" );
            DockFactory<DockElement, DockLayout> factory = (DockFactory<DockElement, DockLayout>)getFactory( factoryId );
            if( factory == null )
                return null;
            
            DockLayout delegate = factory.read( xdelegate );
            if( delegate == null )
                return null;
            
            delegate.setFactoryID( factoryId );
            return new PreloadedLayout( preload, delegate );
		}
	}
}
