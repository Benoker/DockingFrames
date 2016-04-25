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
package bibliothek.gui.dock.frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.DockFrontend.DockInfo;
import bibliothek.gui.DockFrontend.RootInfo;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.layout.AdjacentDockFactory;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.DockSituationIgnore;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;
import bibliothek.gui.dock.layout.PredefinedDockSituation;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.perspective.PredefinedMap;
import bibliothek.gui.dock.perspective.PredefinedPerspective;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.xml.XException;

/**
 * This default implementation of a {@link LayoutChangeStrategy} heavily depends on the methods of {@link PredefinedDockSituation}. It
 * also offers a set of methods that may be interesting for subclasses which do not use a {@link PredefinedDockSituation}.
 * @author Benjamin Sigg
 */
public class DefaultLayoutChangeStrategy implements LayoutChangeStrategy{
	private boolean updatingFullLayout = false;
	
	public boolean setLayout( DockFrontendInternals frontend, Setting setting, boolean entry ) throws IOException, XException{
		try{
			updatingFullLayout = true;
			return updateLayout( frontend, setting, entry );
		}
		finally{
			updatingFullLayout = false;
		}
	}
	
	public boolean shouldUpdateLayoutOnAdd( Dockable dockable ) {
		return !updatingFullLayout;
	}
	
	private boolean updateLayout( DockFrontendInternals frontend, Setting setting, boolean entry ) throws IOException, XException{
		DockSituation situation = createSituation( frontend, entry, true );
        
        DockSituationIgnore ignore = situation.getIgnore();
        if( ignore == null ){
            ignore = new DockSituationIgnore(){
                public boolean ignoreChildren( DockStation station ) {
                    return false;
                }
                public boolean ignoreElement( DockElement element ) {
                    return false;
                }
                public boolean ignoreChildren( PerspectiveStation station ){
                	return false;
                }
                public boolean ignoreElement( PerspectiveElement element ){
                	return false;
                }
            };
        }
        
        SettingAccess access = createAccess( frontend, setting );
        
        // maybe cancel the operation
        if( approveClosing( frontend, situation, access ) == null ){
        	return false;
        }
        
        // split up all child parent relations
        frontend.clean( ignore );
        
        // apply the new layout
        applyLayout( frontend, situation, access, entry );
        applyInvisibleLayout( frontend, situation, access );
        
        return true;
	}
	
	/**
	 * Creates a wrapper around <code>setting</code> that allows the algorithm of this 
	 * {@link LayoutChangeStrategy} to access the setting.
	 * @param frontend the caller of this method
	 * @param setting the setting to hide
	 * @return the wrapper
	 */
	protected SettingAccess createAccess( DockFrontendInternals frontend, Setting setting ){
		return new SettingAccess( setting );
	}
	
	/**
	 * Forwards to {@link #createSituation(DockFrontendInternals, boolean, boolean)} with the
	 * last argument set to <code>false</code>.
	 */
	public PredefinedDockSituation createSituation( DockFrontendInternals frontend, boolean entry ){
        return createSituation( frontend, entry, false );
    }
    
    /**
     * Creates a {@link DockSituation} which represents all the knowledge
     * <code>frontend</code> currently has.
     * @param frontend the frontend for which the situation is required
     * @param entry <code>true</code> if the situation is used for a regular setting,
     * <code>false</code> if the situation is used as the final setting which will
     * be loaded the next time the application starts.
     * @param onSetLayout whether this method is called by {@link #setLayout(DockFrontendInternals, Setting, boolean)} or not. If
     * <code>true</code> then the situation is used to apply some layout, otherwise it is used to store or read a layout
     * @return the situation, the default implementation always returns a {@link PredefinedDockSituation}, subclasses
     * may override and return other situations.
     */
    @SuppressWarnings("unchecked")
    protected PredefinedDockSituation createSituation( final DockFrontendInternals frontend, final boolean entry, boolean onSetLayout ){
        PredefinedDockSituation situation = new PredefinedDockSituation( frontend.getFrontend().getController() ){
            @Override
            protected boolean shouldLayout( DockElement element ) {
                if( entry ){
                    Dockable dockable = element.asDockable();
                    if( dockable != null ){
                        DockInfo info = frontend.getInfo( dockable );
                        if( info != null ){
                            return info.isEntryLayout();
                        }
                    }
                }
                return true;
            }
            
            @Override
            protected boolean shouldLayout( PerspectiveElement element, PredefinedPerspective perspective ) {
            	if( entry ){
            		String key = perspective.get( element );
            		if( key != null ){
            			DockInfo info = frontend.getInfo( key );
            			if( info != null ){
            				return info.isEntryLayout();
            			}
            		}
            	}
            	return true;
            }
        };
        
        for( RootInfo info : frontend.getRoots() ){
            situation.put( DockFrontend.ROOT_KEY_PREFIX + info.getName(), info.getStation() );
        }
        
        for( DockInfo info : frontend.getDockables() ){
            if( info.getDockable() != null && shouldPredefine( info.getDockable() )){
                situation.put( DockFrontend.DOCKABLE_KEY_PREFIX + info.getKey(), info.getDockable() );
            }
        }
        
        for( DockFactory<?,?,?> factory : frontend.getDockFactories() ){
            situation.add( factory );
        }
        
        for( DockFactory<?,?,?> backup : frontend.getBackupDockFactories() ){
            situation.addBackup( new RegisteringDockFactory( frontend.getFrontend(), backup ) );
        }
        
        for( AdjacentDockFactory<?> factory : frontend.getAdjacentDockFactories() ){
            situation.addAdjacent( factory );
        }
        
        if( entry )
        	situation.setIgnore( frontend.getFrontend().getIgnoreForEntry() );
        else
        	situation.setIgnore( frontend.getFrontend().getIgnoreForFinal() );
        
        return situation;
    }
    
    /**
     * Tells whether the element <code>dockable</code> should be added as predefined element to the {@link PredefinedDockSituation}
     * that is created by {@link #createSituation(DockFrontendInternals, boolean, boolean)}.
     * @param dockable the element which may need to be predefined
     * @return <code>true</code> if <code>dockable</code> is to be predefined
     */
    protected boolean shouldPredefine( Dockable dockable ){
    	return true;
    }
    
    public DockFrontendPerspective createPerspective( DockFrontendInternals frontend, boolean entry, final FrontendPerspectiveCache cache ){
        PredefinedDockSituation situation = createSituation( frontend, entry );
	    PredefinedPerspective perspective = situation.createPerspective();

        for( DockInfo info : frontend.getDockables() ){
            if( info.getDockable() != null ){
            	PerspectiveElement element = cache.get( info.getKey(), info.getDockable(), false );
            	if( element != null ){
            		perspective.put( DockFrontend.DOCKABLE_KEY_PREFIX + info.getKey(), element );
            	}
            }
        }
        
        for( RootInfo info : frontend.getRoots() ){
        	PerspectiveElement element = cache.get( info.getName(), info.getStation(), true );
        	perspective.put( DockFrontend.ROOT_KEY_PREFIX + info.getName(), element );
        }
        
        perspective.put( new PredefinedMap(){
        	public PerspectiveElement get( String id ){
        		if( id.startsWith( DockFrontend.DOCKABLE_KEY_PREFIX )){
					return cache.get( id.substring( DockFrontend.DOCKABLE_KEY_PREFIX.length() ), false );
				}
				else if( id.startsWith( DockFrontend.ROOT_KEY_PREFIX )){
					return cache.get( id.substring( DockFrontend.ROOT_KEY_PREFIX.length() ), true );
				}
				else{
					return null;
				}
			}
			
			public String get( PerspectiveElement element ){
				String id = cache.get( element );
				if( id == null ){
					return null;
				}
				
				if( element.asStation() != null && cache.isRootStation( element.asStation() )){
					return DockFrontend.ROOT_KEY_PREFIX + id;
				}
				else{
					return DockFrontend.DOCKABLE_KEY_PREFIX + id;
				}
			}
		});

        return new DefaultDockFrontendPerspective( frontend.getFrontend(), perspective, entry );
    }
    
    public PropertyTransformer createTransformer( DockFrontendInternals frontend ){
        PropertyTransformer transformer = new PropertyTransformer(frontend.getFrontend().getController());
        for( DockablePropertyFactory factory : frontend.getPropertyFactories() )
            transformer.addFactory( factory );
        return transformer;
    }
    
    /**
     * Applies the layout described in <code>setting</code> to the visible elements. 
     * This implementation tries to estimate the location of missing dockables using
     * {@link #listEstimateLocations(DockSituation, DockLayoutComposition)}. 
     * @param frontend the caller of this method
     * @param situation used to convert the layout
     * @param setting the new layout
     * @param entry whether the layout is a full or regular layout
     * @throws IOException if the layout cannot be converted
     * @throws XException if the layout cannot be converted 
     */
    protected void applyLayout( DockFrontendInternals frontend, DockSituation situation, SettingAccess setting, boolean entry ) throws IOException, XException{
    	DockFrontend dockFrontend = frontend.getFrontend();
    	MissingDockableStrategy missingDockable = frontend.getMissingDockableStrategy();
    	
    	for( RootInfo info : frontend.getRoots() ){
            DockLayoutComposition layout = setting.getRoot( info.getName() );
            if( layout != null ){
                layout = situation.fillMissing( layout );
                
                Map<String, DockableProperty> missingLocations =  listEstimateLocations( situation, layout );
                if( missingLocations != null ){
                    for( Map.Entry<String, DockableProperty> missing : missingLocations.entrySet() ){
                        String key = missing.getKey();
                        DockInfo dockInfo = frontend.getInfo( key );
                        
                        if( dockInfo == null && missingDockable.shouldStoreShown( key )){
                            dockFrontend.addEmpty( key );
                            dockInfo = frontend.getInfo( key );
                        }
                        
                        if( dockInfo != null ){
                            dockInfo.setLocation( info.getName(), missing.getValue() );
                            dockInfo.setShown( true );
                        }
                    }
                }
                
                Map<String, DockLayoutComposition> missingLayouts = listLayouts( situation, layout );
                
                if( missingLayouts != null ){
                    for( Map.Entry<String, DockLayoutComposition> missing : missingLayouts.entrySet() ){
                        String key = missing.getKey();
                        DockInfo dockInfo = frontend.getInfo( key );
                        
                        if( dockInfo == null && missingDockable.shouldStoreShown( key )){
                            dockFrontend.addEmpty( key );
                            dockInfo = frontend.getInfo( key );
                        }
                        
                        if( dockInfo != null ){
                            dockInfo.setShown( true );
                            if( !entry || dockInfo.isEntryLayout() ){
                                dockInfo.setLayout( missing.getValue() );
                            }
                        }
                    }
                    
                }
                
                situation.convert( layout );
            }
        }
    }
    
    /**
     * Applies <code>setting</code> to the invisible elements.
     * @param frontend the caller of this method
     * @param situation to convert the layout
     * @param setting the new layout
     * @throws IOException if the layout cannot be converted
     * @throws XException if the layout cannot be converted
     */
    protected void applyInvisibleLayout( DockFrontendInternals frontend, DockSituation situation, SettingAccess setting ) throws IOException, XException{
    	DockFrontend dockFrontend = frontend.getFrontend();
    	MissingDockableStrategy missingDockable = frontend.getMissingDockableStrategy();
    	
        for( int i = 0, n = setting.getInvisibleCount(); i<n; i++ ){
            String key = setting.getInvisibleKey( i );
            DockInfo info = frontend.getInfo( key );
            
            if( info == null && missingDockable.shouldStoreHidden( key )){
                dockFrontend.addEmpty( key );
                info = frontend.getInfo( key );
            }
            
            if( info != null ){
                info.setShown( false );
                info.setLocation( 
                        setting.getInvisibleRoot( i ), 
                        setting.getInvisibleLocation( i ) );
                
                DockLayoutComposition layout = setting.getInvisibleLayout( i );
                if( layout != null ){
                    layout = situation.fillMissing( layout );
                    if( info.getDockable() != null ){
                        situation.convert( layout );
                        layout = null;
                    }
                    info.setLayout( layout );
                }
            }
        }
    }
    
    /**
     * Tries to estimate the layouts of missing {@link Dockable}s. The
     * default implementation works with any {@link PredefinedDockSituation}.
     * @param situation the situation to use for transforming information
     * @param layout the layout to analyze
     * @return a map with <code>Dockable</code>-names as key or <code>null</code>
     */
    protected Map<String, DockLayoutComposition> listLayouts( DockSituation situation, DockLayoutComposition layout ){
        if( situation instanceof PredefinedDockSituation ){
            Map<String, DockLayoutComposition> map = ((PredefinedDockSituation)situation).listLayouts( layout, true );
            Map<String, DockLayoutComposition> result = new HashMap<String, DockLayoutComposition>();
            
            for( Map.Entry<String, DockLayoutComposition> entry : map.entrySet() ){
                String key = entry.getKey();
                if( key.startsWith( DockFrontend.DOCKABLE_KEY_PREFIX ))
                    result.put( key.substring( DockFrontend.DOCKABLE_KEY_PREFIX.length() ), entry.getValue() );
                else if( key.startsWith( DockFrontend.ROOT_KEY_PREFIX ))
                    result.put( key.substring( DockFrontend.ROOT_KEY_PREFIX.length() ), entry.getValue() );
                else
                    result.put( key, entry.getValue() );
            }
            
            return result;
        }
        return null;
    }

    /**
     * Tries to estimate the location of missing {@link Dockable}s. The
     * default implementation works with any {@link PredefinedDockSituation}.
     * @param situation the situation to use for transforming information
     * @param layout the layout to analyze
     * @return a map with <code>Dockable</code>-names as key or <code>null</code>
     */
    protected Map<String, DockableProperty> listEstimateLocations( DockSituation situation, DockLayoutComposition layout ){
        if( situation instanceof PredefinedDockSituation ){
            Map<String, DockableProperty> map = ((PredefinedDockSituation)situation).listEstimatedLocations( layout, true );
            Map<String, DockableProperty> result = new HashMap<String, DockableProperty>();
            
            for( Map.Entry<String, DockableProperty> entry : map.entrySet() ){
                String key = entry.getKey();
                if( key.startsWith( DockFrontend.DOCKABLE_KEY_PREFIX ))
                    result.put( key.substring( DockFrontend.DOCKABLE_KEY_PREFIX.length() ), entry.getValue() );
                else if( key.startsWith( DockFrontend.ROOT_KEY_PREFIX ))
                    result.put( key.substring( DockFrontend.ROOT_KEY_PREFIX.length() ), entry.getValue() );
                else
                    result.put( key, entry.getValue() );
            }
            
            return result;
        }
        return null;
    }
    
    public void estimateLocations( DockFrontendInternals frontend, DockSituation situation, DockLayoutComposition layout ){
        if( situation instanceof PredefinedDockSituation ){
            ((PredefinedDockSituation)situation).estimateLocations( layout );
        }
    }
    

    /**
     * Asks the {@link VetoManager} whether some {@link Dockable}s can be hidden. Only {@link Dockable}s that
     * are returned by {@link DockFrontendInternals#getDockables()} are checked by this method. 
     * @param frontend the caller of this method
     * @param situation the situation that will convert the layout
     * @param setting the new layout
     * @return the set of elements for which closing was explicitly approved
     * or <code>null</code> if the operation should be canceled
     */
    protected Collection<Dockable> approveClosing( DockFrontendInternals frontend, DockSituation situation, SettingAccess setting ){
    	// check whether some elements really should be closed
        Set<Dockable> remainingVisible = new HashSet<Dockable>();
        for( RootInfo info : frontend.getRoots() ){
            DockLayoutComposition layout = setting.getRoot( info.getName() );
            if( layout != null ){
                Set<Dockable> visible = estimateVisible( frontend, situation, layout );
                if( visible != null ){
                    remainingVisible.addAll( visible );
                }
            }
        }
        
        Collection<Dockable> closing = getClosingDockables( frontend, remainingVisible );
        
        if( !closing.isEmpty() ){
            if( !frontend.getVetos().expectToHide( closing, true ) ){
                // cancel the operation
                return null;
            }
        }
        return closing;
    }
    
    /**
     * Creates a collection of all the {@link Dockable}s that are about to be closed. Subclasses
     * may override this method, they should at least check all the {@link Dockable}s that are
     * registered at <code>frontend</code>. Subclasses may need to override
     * {@link #estimateVisible(DockFrontendInternals, DockSituation, DockLayoutComposition) estimateVisible} as
     * well to get the correct set of <code>visible</code> elements.
     * @param frontend the caller of this method
     * @param visible the elements that remain visible as told by {@link #estimateVisible(DockFrontendInternals, DockSituation, DockLayoutComposition)}.
     * @return the set of dockables that is about to be closed, not <code>null</code>, may be empty
     */
    protected Collection<Dockable> getClosingDockables( DockFrontendInternals frontend, Set<Dockable> visible ){
    	List<Dockable> closing = new ArrayList<Dockable>();
        for( DockInfo info : frontend.getDockables() ){
        	Dockable dockable = info.getDockable();
            if( dockable != null && info.isHideable() ){
            	if( !visible.contains( dockable )){
            		closing.add( info.getDockable() );
            	}
            }
        }
        return closing;
    }

    /**
     * Tries to estimate which of the currently visible {@link Dockable}s will
     * still be visible if <code>layout</code> is applied to <code>frontend</code>. The
     * default implementation assumes that <code>situation</code> is a {@link PredefinedDockSituation}.<br>
     * Subclasses may override this method and modify the result in any way they like
     * @param frontend the caller of this method
     * @param situation algorithm used to convert <code>layout</code>
     * @param layout the layout that will be applied
     * @return an estimation of the elements that will be made invisible or <code>null</code>
     */
    protected Set<Dockable> estimateVisible( DockFrontendInternals frontend, DockSituation situation, DockLayoutComposition layout ){
        if( situation instanceof PredefinedDockSituation ){
            Set<Dockable> allDockables = new HashSet<Dockable>();
            for( DockInfo info : frontend.getDockables() ){
            	Dockable dockable = info.getDockable();
            	if( dockable != null ){
            		allDockables.add( dockable );
            	}
            }
            
            PredefinedDockSituation predefined = (PredefinedDockSituation)situation;
            Set<Dockable> visible = predefined.listVisible( allDockables, layout );
            return visible;
        }
        
        return null;
    }
    
    public PlaceholderStrategy getPlaceholderStrategy( DockFrontendInternals frontend ){
    	return null;
    }
    
    /**
     * A wrapper around a {@link Setting}, allows algorithms access to the settings
     * but also allows to modify the data without changing the setting.
     * @author Benjamin Sigg
     */
    protected class SettingAccess{
    	private Setting setting;
    	
    	/**
    	 * Creates a new wrapper.
    	 * @param setting the source for all data, not <code>null</code>
    	 */
    	public SettingAccess( Setting setting ){
    		this.setting = setting;
    	}
    	
    	/**
    	 * Gets the setting that is hidden by this wrapper.
    	 * @return the source of all data, not <code>null</code>
    	 */
    	public Setting getSetting(){
			return setting;
		}
    	
        /**
         * Gets the layout of a root.
         * @param root the root
         * @return the layout or <code>null</code>
         */
        public DockLayoutComposition getRoot( String root ){
            return setting.getRoot( root );
        }
        
        /**
         * Gets the keys of all known roots.
         * @return the keys of the roots
         */
        public String[] getRootKeys(){
        	return setting.getRootKeys();
        }
        
        /**
         * Gets the number of stored invisible elements.
         * @return the number of elements
         */
        public int getInvisibleCount(){
            return setting.getInvisibleCount();
        }
        
        /**
         * Gets the key of the index'th invisible element.
         * @param index the index of the element
         * @return the key
         */
        public String getInvisibleKey( int index ){
            return setting.getInvisibleKey( index );
        }

        /**
         * Gets the preferred root of the index'th invisible element.
         * @param index the index of the element
         * @return the root
         */
        public String getInvisibleRoot( int index ){
         	return setting.getInvisibleRoot( index );
        }
        
        /**
         * Gets the location of the index'th invisible element.
         * @param index the index of the element
         * @return the location
         */
        public DockableProperty getInvisibleLocation( int index ){
            return setting.getInvisibleLocation( index );
        }
        
        /**
         * Gets the layout of the index'th invisible element.
         * @param index the index of the layout
         * @return associated information, may be <code>null</code>
         */
        public DockLayoutComposition getInvisibleLayout( int index ){
            return setting.getInvisibleLayout( index );
        }
        
        /**
         * Using the factories given by <code>situation</code>, this method tries
         * to fill any gaps in the layout.
         * @param situation a set of factories to use
         * @throws IllegalArgumentException if <code>situation</code> cannot handle
         * the content of this setting
         */
        public void fillMissing( DockSituation situation ){
            setting.fillMissing( situation );
        }
    }
}
