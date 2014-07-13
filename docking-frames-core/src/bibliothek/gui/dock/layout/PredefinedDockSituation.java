/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PredefinedPerspective;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.extension.ExtensionManager;
import bibliothek.util.Version;
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

    /** backup factories for elements that should be in the cache, but are missing */
    private Map<String, DockFactory<? extends DockElement,?,? extends BackupFactoryData<?>>> backups =
        new HashMap<String, DockFactory<? extends DockElement,?,? extends BackupFactoryData<?>>>();

    private final PreloadFactory factory = new PreloadFactory();
    
    /**
     * Creates a new {@link DockSituation}, uses <code>controller</code> to access the
     * {@link ExtensionManager} and install additional {@link DockFactory}s.
     * @param controller the controller in whose realm this situation will be used
     */
    public PredefinedDockSituation( DockController controller ){
    	super( controller );
    }
    
    /**
     * Creates a new {@link PredefinedPerspective}. This perspective will write the layout in
     * such a way that a {@link PredefinedDockSituation} can read it again.<br>
     * Please note that clients need to register {@link DockElement}s using {@link PredefinedPerspective#put(String, PerspectiveElement)}
     * before the perspective can be used. The {@link DockElement}s registered to <code>this</code> by using {@link #put(String, DockElement)} 
     * are ignored by the perspective.  
     */
    @Override
    public PredefinedPerspective createPerspective(){
	    return new PredefinedPerspective( this ){
	    	private PreloadFactory preload;
	    	
	    	{
	    		preload = new PreloadFactory(this);
	    	}
	    	
			protected String getID( PerspectiveElement element ){
				return PredefinedDockSituation.this.getID( element, this );
			}
			
			protected DockFactory<?, ?, ?> getFactory( String id ){
				DockFactory<?, ?, ?> factory = PredefinedDockSituation.this.getFactory( id );
				if( factory == PredefinedDockSituation.this.factory ){
					return preload;
				}
				return factory;
			}
		};
    }
    
    /**
     * Adds a backup factory to this situation. A backup factory is used when
     * an element should be in the cache, but is missing. The backup factory
     * receives a {@link BackupFactoryData} object, the identifier of that
     * object does not have to be stored by <code>factory</code>. The
     * factory has only to look at the {@link BackupFactoryData#getData() data}-property.
     * This {@link PredefinedDockSituation} will set the identifier whenever
     * a method of <code>factory</code> is called, that has a {@link BackupFactoryData}
     * as parameter.
     * @param factory a backup factory
     */
    public void addBackup( DockFactory<? extends DockElement, ?, ? extends BackupFactoryData<?>> factory ){
        backups.put( UNKNOWN + factory.getID(), factory );
    }

    /**
     * Removes the backup factory with the name <code>id</code>.
     * @param id the id of the factory which should be removed
     */
    public void removeBackup( String id ){
        backups.remove( UNKNOWN + id );
    }

    /**
     * Registers an element at this situation. When a stream is read, this
     * element will be returned instead of a newly created element (assuming
     * that the element was written into the stream). The key for
     * the element is generated automatically.<br>
     * <b>Note:</b> the order in which elements are added is important as the
     * unique key depends on the order.
     * @param element the element
     * @deprecated use {@link #put(String, DockElement)} instead
     */
    @Deprecated
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

    /**
     * Tells whether the layout of <code>element</code> itself should be stored
     * or loaded, that will not prevent the <code>element</code> from showing
     * up but from changing its content. The default implementation returns
     * always <code>true</code>. This method is intended to be overridden by
     * subclasses.
     * @param element the element whose contents might or might not be stored
     * or loaded
     * @return <code>true</code> if the contents should be handled, <code>false</code>
     * if they should be discarded
     */
    protected boolean shouldLayout( DockElement element ){
        return true;
    }

    /**
     * Tells whether the layout of <code>element</code> itself should be stored
     * or loaded, that will not prevent the <code>element</code> from showing
     * up but from changing its content. The default implementation uses the maps
     * of <code>perspective</code> and this {@link DockSituation} to convert <code>element</code>
     * into a preset {@link DockElement} and calls {@link #shouldLayout(DockElement)}. If the
     * element is not found, <code>true</code> is returned.
     * @param element the element whose contents might or might not be stored
     * or loaded
     * @param perspective the perspective for which the question has to be answered
     * @return <code>true</code> if the contents should be handled, <code>false</code>
     * if they should be discarded
     */
    protected boolean shouldLayout( PerspectiveElement element, PredefinedPerspective perspective ){
    	String key = perspective.get( element );
    	if( key != null ){
    		DockElement dock = stringToElement.get( key );
    		if( dock != null ){
    			return shouldLayout( dock );
    		}
    	}
    	return true;
    }
    
    @Override
    protected DockLayoutInfo fillMissing( DockLayoutInfo info ) {
        DockLayout<?> layout = info.getDataLayout();
        if( KNOWN.equals( layout.getFactoryID() )){
            PredefinedLayout preloaded = (PredefinedLayout)layout.getData();
            DockLayoutInfo delegate = preloaded.getDelegate();
            DockLayoutInfo newDelegate = null;

            if( delegate.getKind() == DockLayoutInfo.Data.BYTE ){
                newDelegate = fillMissingStream( preloaded );
            }
            else if( delegate.getKind() == DockLayoutInfo.Data.XML ){
                newDelegate = fillMissingXML( preloaded );
            }

            if( newDelegate != null ){
                info = new DockLayoutInfo( new DockLayout<PredefinedLayout>( 
                        KNOWN, new PredefinedLayout( preloaded.getPredefined(), newDelegate )));
            }
        }
        return info;
    }
    
    /**
     * Given a set of <code>Dockable</code>s this method
     * estimates which of them will be visible once <code>composition</code>
     * is applied.
     * @param <D> the kind of elements to check
     * @param base a collection of <code>Dockable</code>s in no specific
     * order and with no restrictions
     * @param composition location information for various elements
     * @return A subset of <code>base</code> with those elements which will
     * be visible once this situation converts <code>composition</code>
     */
    public <D extends DockElement> Set<D> listVisible( Collection<D> base, DockLayoutComposition composition ){
        Set<D> result = new HashSet<D>();
        listVisible( base, composition, result );
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private <D extends DockElement> void listVisible( final Collection<D> base, DockLayoutComposition composition, final Set<D> result ){
        DockLayoutInfo info = composition.getLayout();
        if( info.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT ){
            DockLayout<?> layout = info.getDataLayout();
            
            // preloaded element with key
            if( KNOWN.equals( layout.getFactoryID() )){
                PredefinedLayout preload = (PredefinedLayout)layout.getData();
                String key = preload.getPredefined();
                DockElement element = stringToElement.get( key );
                if( element != null ){
                    if( base.contains( element )){
                        result.add( (D)element );
                    }
                    if( composition.isIgnoreChildren() ){
                        DockUtilities.visit( element, new DockUtilities.DockVisitor(){
                            @Override
                            public void handleDockable( Dockable dockable ) {
                                if( base.contains( dockable )){
                                    result.add( (D)dockable );
                                }
                            }
                            
                            @Override
                            public void handleDockStation( DockStation station ) {
                                if( base.contains( station )){
                                    result.add( (D)station );
                                }
                            }
                        });
                    }
                }
            }
        }
        
        // check all children
        for( DockLayoutComposition child : composition.getChildren() ){
            listVisible( base, child, result );
        }
    }
    
    /**
     * Lists for all keys that can be found in <code>composition</code> its
     * estimated location.<br>
     * Note: This method will call {@link #estimateLocations(DockLayoutComposition)}
     * to get the most recent locations 
     * @param composition some composition to search for keys and locations
     * @param missingOnly if set, then only locations of keys for which 
     * no {@link DockLayout} is set are reported. This are the keys which most
     * likely will be ignored when calling {@link #convert(DockLayoutComposition)}
     * @return the map of keys and positions, might be empty
     */
    public Map<String, DockableProperty> listEstimatedLocations( DockLayoutComposition composition, boolean missingOnly ){
        return listEstimatedLocations( composition, composition.getLayout().getLocation(), missingOnly );
    }
    
    /**
     * Lists for all keys that can be found in <code>composition</code> its
     * estimated location.<br>
     * Note: This method will call {@link #estimateLocations(DockLayoutComposition)}
     * to get the most recent locations 
     * @param composition some composition to search for keys and locations
     * @param location the location of <code>composition</code> itself
     * @param missingOnly if set, then only locations of keys for which 
     * no {@link DockLayout} is set are reported. This are the keys which most
     * likely will be ignored when calling {@link #convert(DockLayoutComposition)}
     * @return the map of keys and positions, might be empty
     */
    public Map<String, DockableProperty> listEstimatedLocations( DockLayoutComposition composition, DockableProperty location, boolean missingOnly ){
        estimateLocations( composition, location );
        
        Map<String, DockableProperty> map = new HashMap<String, DockableProperty>();
        listEstimatedLocations( composition, missingOnly, map );
        
        if( location != null ){
            String key = getKey( composition, missingOnly );
            if( key != null ){
                map.put( key, location );
            }
        }
        
        return map;
    }
    
    private void listEstimatedLocations( DockLayoutComposition composition, boolean missingOnly, Map<String, DockableProperty> map ){
        DockableProperty location = composition.getLayout().getLocation();
        if( location != null ){
            String key = getKey( composition, missingOnly );
            if( key != null){
                map.put( key, location );
            }
        }
        
        List<DockLayoutComposition> children = composition.getChildren();
        if( children != null ){
            for( DockLayoutComposition child : children ){
                listEstimatedLocations( child, missingOnly, map );
            }
        }
    }
    
    /**
     * Gets a map containing some or all of the named layouts.
     * @param composition some composition to analyze
     * @param missingOnly if set, then only locations of keys for which 
     * no {@link DockLayout} is set are reported. This are the keys which most
     * likely will be ignored when calling {@link #convert(DockLayoutComposition)}
     * @return the map of keys and layouts, might be empty
     */
    public Map<String, DockLayoutComposition> listLayouts( DockLayoutComposition composition, boolean missingOnly ){
        Map<String, DockLayoutComposition> map = new HashMap<String, DockLayoutComposition>();
        listLayouts( composition, missingOnly, map );
        return map;
    }
    
    private void listLayouts( DockLayoutComposition composition, boolean missingOnly, Map<String, DockLayoutComposition> map ){
        String key = getKey( composition, missingOnly );
        if( key != null){
            map.put( key, composition );
        }
        
        List<DockLayoutComposition> children = composition.getChildren();
        if( children != null ){
            for( DockLayoutComposition child : children ){
                listLayouts( child, missingOnly, map );
            }
        }
    }
    
    /**
     * Gets the name of element which is represented by <code>composition</code>.
     * @param composition the composition whose element key is searched
     * @param missingOnly if set, then the key will only be returned if <code>composition</code>
     * is not fully loaded
     * @return the key or <code>null</code>
     */
    private String getKey( DockLayoutComposition composition, boolean missingOnly ){
        DockLayoutInfo layout = composition.getLayout();
        if( layout.getKind() != DockLayoutInfo.Data.DOCK_LAYOUT )
            return null;
        
        if( !KNOWN.equals( layout.getDataLayout().getFactoryID() ))
            return null;
        
        PredefinedLayout preloaded = (PredefinedLayout)layout.getDataLayout().getData();
        if( missingOnly && preloaded.getDelegate().getKind() == DockLayoutInfo.Data.DOCK_LAYOUT ){
            // if there is such a Dockable registered then it is not missing...
            if( stringToElement.containsKey( preloaded.getPredefined() )){
                return null;
            }
        }
        
        String key = preloaded.getPredefined();
        return key;
    }

    /**
     * Tries to read the byte data in <code>layout</code>.
     * @param layout the layout to read
     * @return either a new info or <code>null</code> if the data could
     * not be read
     */
    @SuppressWarnings("unchecked")
    private DockLayoutInfo fillMissingStream( PredefinedLayout layout ){
        byte[] bytes = layout.getDelegate().getDataByte();

        try{
            DataInputStream in = new DataInputStream( new ByteArrayInputStream( bytes ));
            String factoryId = in.readUTF();

            DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
            DockLayoutInfo info = null;
            
            if( factory == null ){
                DockFactory<?,?,BackupFactoryData<?>> backup = getBackup( factoryId );
                if( backup != null ){
                    BackupFactoryData<Object> data = (BackupFactoryData<Object>)backup.read( in, getPlaceholderStrategy() );
                    if( data != null && data.getData() != null ){
                        info = new DockLayoutInfo( new DockLayout<Object>( factoryId, data.getData() ));
                    }
                }
            }
            else{
                Object delegate = factory.read( in, getPlaceholderStrategy() );
                if( delegate != null ){
                    info = new DockLayoutInfo( new DockLayout<Object>( factoryId, delegate ));
                }
            }


            in.close();
            return info;
        }
        catch( IOException ex ){
            throw new IllegalArgumentException( "Cannot read stream", ex );
        }
    }

    /**
     * Tries to read the xml data in <code>layout</code>.
     * @param layout the layout to read
     * @return either a new info or <code>null</code> if the data could
     * not be read
     */
    @SuppressWarnings("unchecked")
    private DockLayoutInfo fillMissingXML( PredefinedLayout layout ){
        XElement xdelegate = layout.getDelegate().getDataXML();
        String factoryId = xdelegate.getString( "id" );

        Object delegate = null;

        DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
        if( factory == null ){
            DockFactory<?,?,BackupFactoryData<?>> backup = getBackup( factoryId );
            if( backup != null ){
                BackupFactoryData<Object> data = (BackupFactoryData<Object>)backup.read( xdelegate, getPlaceholderStrategy() );
                if( data != null )
                    delegate = data.getData();
            }
        }
        else{
            delegate = factory.read( xdelegate, getPlaceholderStrategy() );
        }

        if( delegate == null ){
            return null;
        }
        
        return new DockLayoutInfo( new DockLayout<Object>( factoryId, delegate ) );
    }
    
    /**
     * Gets the identifier that matches <code>element</code> which is part of <code>perspective</code>.
     * @param element some element whose identifier is searched
     * @param perspective the owner of <code>element</code>
     * @return the identifier
     */
    protected String getID( PerspectiveElement element, PredefinedPerspective perspective ){
    	String key = perspective.get( element );
        if( key == null )
            return UNKNOWN + super.getID( element );
        else
            return KNOWN;
    }

    @Override
    public String getID( DockElement dockable ){
        String key = elementToString.get( dockable );
        if( key == null )
            return UNKNOWN + super.getID( dockable );
        else
            return KNOWN;
    }

    @Override
    protected String getID( DockFactory<?,?,?> factory ) {
        if( factory == this.factory )
            return KNOWN;
        else
            return UNKNOWN + super.getID( factory );
    }

    @Override
    protected String getFactoryID( String id ) {
        if( KNOWN.equals( id ))
            return factory.getID();
        else
            return id.substring( UNKNOWN.length() );
    }
    
    /**
     * Given the unique identifier of a factory tells what identifier will be used
     * internally. This method may be useful if creating a {@link DockLayout} manually.
     * @param id the id of some factory
     * @return the id that will be used internally
     */
    public static String convertFactoryID( String id ){
    	return UNKNOWN + id;
    }

    @Override
    public DockFactory<? extends DockElement,?,?> getFactory( String id ){
        if( KNOWN.equals( id ))
            return factory;
        else
            return super.getFactory( id );
    }

    /**
     * Searches a backup factory with the name <code>id</code>.
     * @param id the name of the factory
     * @return the factory or <code>null</code>
     */
    @SuppressWarnings( "unchecked" )
	protected DockFactory<? extends DockElement,?,BackupFactoryData<?>> getBackup( String id ){
        return (DockFactory<? extends DockElement,?,BackupFactoryData<?>>)backups.get( id );
    }

    @Override
    public String getIdentifier( DockLayoutComposition composition ){
    	DockLayout<?> layout = composition.getLayout().getDataLayout();
    	if( layout != null && layout.getFactoryID().equals( KNOWN )){
    		PredefinedLayout predefined = (PredefinedLayout) layout.getData();
    		return predefined.getPredefined();
    	}
    	return null;
    }
    
    /**
     * A factory which uses other factories as delegate. This factory does
     * not always use the delegates, sometimes it does just read an element
     * which was predefined in {@link PredefinedDockSituation}.
     * @author Benjamin Sigg
     */
    private class PreloadFactory implements DockFactory<DockElement,PerspectiveElement,PredefinedLayout>{
    	private PredefinedPerspective perspective;
    	
    	/**
    	 * Creates a new factory
    	 */
    	public PreloadFactory(){
    		// nothing
    	}
    	
    	/**
    	 * Creates a new factory
    	 * @param perspective the perspective whose contents should be used for handling {@link PerspectiveElement}
    	 */
    	public PreloadFactory( PredefinedPerspective perspective ){
    		this.perspective = perspective;
    	}
    	
        public String getID() {
            return KNOWN;
        }

        @SuppressWarnings("unchecked")
        public void estimateLocations( PredefinedLayout layout, LocationEstimationMap children ){
            DockLayoutInfo delegate = layout.getDelegate();
            if( delegate.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT ){
                String factoryId = delegate.getDataLayout().getFactoryID();
                DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
                if( factory != null ){
                    factory.estimateLocations( delegate.getDataLayout().getData(), children );
                }
            }
        }

        @SuppressWarnings("unchecked")
        public PredefinedLayout getLayout( DockElement element, Map<Dockable, Integer> children ) {
            if( shouldLayout( element )){
                String factoryId = UNKNOWN + PredefinedDockSituation.super.getID( element );
                DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
                if( factory == null )
                    throw new IllegalStateException( "Missing factory: " + factoryId );

                Object data = factory.getLayout( element, children );
                DockLayout<Object> layout = new DockLayout<Object>( factoryId, data );
                return new PredefinedLayout( elementToString.get( element ), new DockLayoutInfo( layout ));    
            }
            else{
                return new PredefinedLayout( elementToString.get( element ), new DockLayoutInfo() );
            }
        }

        @SuppressWarnings("unchecked")
        public void setLayout( DockElement element, PredefinedLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
            DockLayoutInfo delegate = layout.getDelegate();
            if( delegate.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT && shouldLayout( element )){
                String factoryId = delegate.getDataLayout().getFactoryID();
                DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
                if( factory != null ){
                	DockController controller = element.getController();
                	try{
                		if( controller != null )
                			controller.freezeLayout();
                		
                		factory.setLayout( element, delegate.getDataLayout().getData(), children, placeholders );
                	}
                	finally{
                		if( controller != null )
                			controller.meltLayout();
                	}
                }
            }
        }

        @SuppressWarnings("unchecked")
        public void setLayout( DockElement element, PredefinedLayout layout, PlaceholderStrategy placeholders ) {
            DockLayoutInfo delegate = layout.getDelegate();
            if( delegate.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT && shouldLayout( element )){
            	String factoryId = delegate.getDataLayout().getFactoryID();
                DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
                if( factory != null ){
                	DockController controller = element.getController();
                	try{
                		if( controller != null )
                			controller.freezeLayout();
                		
                		factory.setLayout( element, delegate.getDataLayout().getData(), placeholders );
                	}
                	finally{
                		if( controller != null )
                			controller.meltLayout();
                	}
                }
            }
        }

        public DockElement layout( PredefinedLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
            DockLayoutInfo delegate = layout.getDelegate();
            boolean isLayout = delegate.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT;
            boolean isNull =  delegate.getKind() == DockLayoutInfo.Data.NULL;

            if( !isLayout && !isNull ){
                return null;
            }
            
            DockElement element = stringToElement.get( layout.getPredefined() );
            if( element == null && isLayout ){
                String factoryId = delegate.getDataLayout().getFactoryID();
                DockFactory<?, ?, BackupFactoryData<?>> factory = getBackup( factoryId );
                if( factory != null ){
                    return factory.layout( new BackupFactoryData<Object>(
                            layout.getPredefined(), 
                            delegate.getDataLayout().getData()), children,
                            placeholders );
                }
                return null;
            }

            setLayout( element, layout, children, placeholders );
            return element;
        }

        public DockElement layout( PredefinedLayout layout, PlaceholderStrategy placeholders ) {
            DockLayoutInfo delegate = layout.getDelegate();
            
            boolean isLayout = delegate.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT;
            boolean isNull =  delegate.getKind() == DockLayoutInfo.Data.NULL;

            if( !isLayout && !isNull ){
                return null;
            }

            DockElement element = stringToElement.get( layout.getPredefined() );
            if( element == null && isLayout ){
                if( layout.getDelegate() == null )
                    return null;

                String factoryId = delegate.getDataLayout().getFactoryID();
                DockFactory<?, ?, BackupFactoryData<?>> factory = getBackup( factoryId );
                if( factory != null ){
                    return factory.layout( new BackupFactoryData<Object>( 
                            layout.getPredefined(),
                            delegate.getDataLayout().getData()),
                            placeholders);
                }

                return null;
            }

            setLayout( element, layout, placeholders );
            return element;
        }
        
        @SuppressWarnings("unchecked")
		public PerspectiveElement layoutPerspective( PredefinedLayout layout, Map<Integer, PerspectiveDockable> children ){
        	if( perspective == null ){
        		throw new IllegalStateException( "the perspective of this factory is not set, meaning this factory cannot be used handling perspective dependent tasks" );
        	}

        	DockLayoutInfo delegate = layout.getDelegate();
            boolean isLayout = delegate.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT;
            boolean isNull =  delegate.getKind() == DockLayoutInfo.Data.NULL;

            if( !isLayout && !isNull ){
                return null;
            }
            
            PerspectiveElement element = perspective.get( layout.getPredefined() );
            if( element == null && isLayout ){
                String factoryId = delegate.getDataLayout().getFactoryID();
                DockFactory factory = getBackup( factoryId );
                if( factory != null ){
                	return factory.layoutPerspective(new BackupFactoryData<Object>( layout.getPredefined(), delegate.getDataLayout().getData()), children );
                }
                return null;
            }

            layoutPerspective( element, layout, children );
            return element;
        }
        
        @SuppressWarnings("unchecked")
        public void layoutPerspective( PerspectiveElement element, PredefinedLayout layout, Map<Integer, PerspectiveDockable> children ){
        	DockLayoutInfo delegate = layout.getDelegate();
        	if( delegate.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT && shouldLayout( element, perspective )){
        		String factoryId = delegate.getDataLayout().getFactoryID();
        		DockFactory factory = getFactory( factoryId );
        		if( factory != null ){
        			factory.layoutPerspective( element, delegate.getDataLayout().getData(), children );
        		}
        	}
        }
        
        @SuppressWarnings("unchecked")
		public PredefinedLayout getPerspectiveLayout( PerspectiveElement element, Map<PerspectiveDockable, Integer> children ){
        	if( perspective == null ){
        		throw new IllegalStateException( "the perspective of this factory is not set, meaning this factory cannot be used handling perspective dependent tasks" );
        	}
        	
        	DockLayoutInfo info;
        	
        	if( shouldLayout( element, perspective )){
                String factoryId = UNKNOWN + PredefinedDockSituation.super.getID( element );
                DockFactory factory = getFactory( factoryId );
                if( factory == null )
                    throw new IllegalStateException( "Missing factory: " + factoryId );

                Object data = factory.getPerspectiveLayout( element, children );
                DockLayout<Object> layout = new DockLayout<Object>( factoryId, data );
                info = new DockLayoutInfo( layout );
            }
            else{
            	info = new DockLayoutInfo();
            }
        	
        	String key = perspective.get( element );
        	if( key == null ){
        		throw new IllegalStateException( "Expected a key for an element, the element should be known to the perspective, otherwise this method would not have been called: '" + element +"'" );
        	}
        	
        	return new PredefinedLayout( key, info );
        }

        @SuppressWarnings("unchecked")
        public void write( PredefinedLayout layout, DataOutputStream out ) throws IOException {
            Version.write( out, Version.VERSION_1_0_7 );

            DockLayoutInfo info = layout.getDelegate();
            out.writeUTF( layout.getPredefined() );

            if( info.getKind() == DockLayoutInfo.Data.BYTE ){
                out.writeBoolean( true );
                out.write( info.getDataByte() );
            }
            else if( info.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT ){
                out.writeBoolean( true );
                DockLayout delegate = info.getDataLayout();
                String factoryId = delegate.getFactoryID();
                DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
                if( factory == null )
                    throw new IOException( "Missing factory: " + factoryId );

                out.writeUTF( factoryId );
                factory.write( delegate.getData(), out );    
            }
            else if( info.getKind() == DockLayoutInfo.Data.NULL ){
                out.writeBoolean( false );
            }
            else{
                throw new IllegalArgumentException( "Cannot store information as byte[], it is not present as raw byte[] or in an understandable format" );
            }
        }

        @SuppressWarnings("unchecked")
        public PredefinedLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException {
            Version version = Version.read( in );
            version.checkCurrent();

            boolean version7 = Version.VERSION_1_0_7.compareTo( version ) <= 0;
            
            String preloaded = in.readUTF();
            boolean nullValue = false;
            if( version7 ){
                nullValue = !in.readBoolean();
            }

            DockLayoutInfo info = null;
            
            if( nullValue ){
                info = new DockLayoutInfo();
            }
            else{
                String factoryId = in.readUTF();

                DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
                if( factory == null ){
                    DockFactory backup = getBackup( factoryId );
                    if( backup != null ){
                        BackupFactoryData<Object> data = (BackupFactoryData<Object>)backup.read( in, placeholders );
                        if( data != null && data.getData() != null ){
                            info = new DockLayoutInfo( new DockLayout<Object>( factoryId, data.getData() ));
                        }
                    }
                    else{
                        // store as byte[]
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        DataOutputStream dout = new DataOutputStream( out );
                        dout.writeUTF( factoryId );

                        int read;
                        while( (read = in.read()) != -1 ){
                            dout.write( read );
                        }

                        dout.close();
                        info = new DockLayoutInfo( out.toByteArray() );
                    }
                }
                else{
                    Object delegate = factory.read( in, placeholders );
                    if( delegate != null ){
                        info = new DockLayoutInfo( new DockLayout<Object>( factoryId, delegate ));
                    }
                }
            }

            if( info == null )
                return null;

            return new PredefinedLayout( preloaded, info );
        }

        @SuppressWarnings("unchecked")
        public void write( PredefinedLayout layout, XElement element ) {
            element.addElement( "replacement" ).addString( "id", layout.getPredefined() );

            DockLayoutInfo info = layout.getDelegate();
            if( info.getKind() == DockLayoutInfo.Data.XML ){
                element.addElement( info.getDataXML() );
            }
            else if( info.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT ){
                DockLayout<?> delegate = layout.getDelegate().getDataLayout();
                String factoryId = delegate.getFactoryID();
                DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
                if( factory == null )
                    throw new XException( "Missing factory: " + factoryId );

                XElement xdelegate = element.addElement( "delegate" );
                xdelegate.addString( "id", factoryId );
                factory.write( delegate.getData(), xdelegate );    
            }
            else if( info.getKind() == DockLayoutInfo.Data.NULL ){
                // nothing to store
            }
            else{
                throw new IllegalArgumentException( "Cannot store information as xml, it is neither present as raw xml nor in an understandable format" );
            }
        }

        @SuppressWarnings("unchecked")
        public PredefinedLayout read( XElement element, PlaceholderStrategy placeholders ) {
            String preload = element.getElement( "replacement" ).getString( "id" );

            XElement xdelegate = element.getElement( "delegate" );
            if( xdelegate == null ){
                return new PredefinedLayout( preload, new DockLayoutInfo() );
            }
            
            String factoryId = xdelegate.getString( "id" );

            Object delegate = null;

            DockFactory<DockElement,?,Object> factory = (DockFactory<DockElement,?,Object>)getFactory( factoryId );
            if( factory == null ){
                DockFactory backup = getBackup( factoryId );
                if( backup != null ){
                    BackupFactoryData<Object> data = (BackupFactoryData<Object>)backup.read( xdelegate, placeholders );
                    if( data != null )
                        delegate = data.getData();
                }
            }
            else{
                delegate = factory.read( xdelegate, placeholders );
            }

            if( delegate == null ){
                return new PredefinedLayout( preload, new DockLayoutInfo( xdelegate ));
            }
            else{
                DockLayout<Object> layout = new DockLayout<Object>( factoryId, delegate );
                return new PredefinedLayout( preload, new DockLayoutInfo( layout ) );
            }
        }
    }
}
