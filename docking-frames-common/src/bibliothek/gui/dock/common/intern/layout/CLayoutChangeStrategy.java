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
package bibliothek.gui.dock.common.intern.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.DockFrontend.DockInfo;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.CommonMultipleDockableLayout;
import bibliothek.gui.dock.frontend.DefaultLayoutChangeStrategy;
import bibliothek.gui.dock.frontend.DockFrontendInternals;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.layout.DockLayout;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.PredefinedDockSituation;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.FrameworkOnly;

/**
 * Strategy that pays attention to {@link MultipleCDockableFactory#match(bibliothek.gui.dock.common.MultipleCDockable, bibliothek.gui.dock.common.MultipleCDockableLayout)}
 * and that fires {@link CVetoClosingEvent}s for {@link MultipleCDockable}s as well.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class CLayoutChangeStrategy extends DefaultLayoutChangeStrategy {
	/** the control in whose realm this strategy is used */
	private CControl control;
	
	/** the internal name of the {@link ReplacementDockFactory} */
	private static final String REPLACEMENT_FACTORY_ID = PredefinedDockSituation.convertFactoryID( ReplacementDockFactory.REPLACEMENT_FACTORY_ID );
	
	/**
	 * Creates a new strategy.
	 * @param control the control in whose realm this strategy will be used
	 */
	public CLayoutChangeStrategy( CControl control ){
		this.control = control;
	}
	
	@Override
	protected PredefinedDockSituation createSituation( DockFrontendInternals frontend, boolean entry, boolean onSetLayout ){
		PredefinedDockSituation situation = super.createSituation( frontend, entry, onSetLayout );
		if( onSetLayout ){
			situation.add( new ReplacementDockFactory() );
		}
		situation.setPlaceholderStrategy( control.getProperty( PlaceholderStrategy.PLACEHOLDER_STRATEGY ) );
		return situation;
	}
	
	@Override
	protected boolean shouldPredefine( Dockable dockable ){
		if( dockable instanceof CommonDockable ){
			return ((CommonDockable)dockable).getDockable() instanceof SingleCDockable; 
		}
		else{
			return true;
		}
	}
	
	@Override
	protected Set<Dockable> estimateVisible( DockFrontendInternals frontend, DockSituation situation, DockLayoutComposition layout ){
        if( situation instanceof PredefinedDockSituation ){
            Set<Dockable> allDockables = new HashSet<Dockable>();
            for( DockInfo info : frontend.getDockables() ){
            	Dockable dockable = info.getDockable();
            	if( dockable != null ){
            		allDockables.add( dockable );
            	}
            }
            for( MultipleCDockable dockable : control.getRegister().getMultipleDockables() ){
            	allDockables.add( dockable.intern() );
            }
            
            PredefinedDockSituation predefined = (PredefinedDockSituation)situation;
            Set<Dockable> visible = predefined.listVisible( allDockables, layout );
            
            findVisible( visible, layout );
            
            return visible;
        }
        
        return null;
	}
	
	@Override
	protected Collection<Dockable> getClosingDockables( DockFrontendInternals frontend, Set<Dockable> visible ){
		Collection<Dockable> result = super.getClosingDockables( frontend, visible );
		
		for( MultipleCDockable dockable : control.getRegister().getMultipleDockables() ){
			CommonDockable intern = dockable.intern();
			if( !visible.contains( intern )){
				result.add( intern );
			}
		}
		
		return result;
	}
	
	private void findVisible( Set<Dockable> visible, DockLayoutComposition layout ){
		DockLayoutInfo info = layout.getLayout();
		if( info != null ){
			DockLayout<?> data = info.getDataLayout();
			if( data != null ){
				if( REPLACEMENT_FACTORY_ID.equals( data.getFactoryID() )){
					CDockable dockable = (CDockable)data.getData();
					visible.add( dockable.intern() );
				}
			}
		}
		
		for( DockLayoutComposition child : layout.getChildren() ){
			findVisible( visible, child );
		}
	}
	
	/**
	 * Checks the {@link DockLayout} that is associated with <code>composition</code> and may replace 
	 * the layout by another layout if a {@link MultipleCDockableFactory} finds a match between an existing
	 * item and meta-data about an item. 
	 * @param frontend the caller of this method
	 * @param setting the layout that is about to be applied
	 * @param composition the composition to modify, may be <code>null</code>
	 * @return the replacement composition or <code>null</code>
	 */
	protected DockLayoutComposition replaceMultipleDockables( DockFrontendInternals frontend, CSettingAccess setting, DockLayoutComposition composition ){
		if( composition == null ){
			return null;
		}
		
		DockLayoutInfo info = composition.getLayout();
		if( info != null ){
			DockLayout<?> layout = info.getDataLayout();
			if( layout != null ){
				MultipleCDockable match = setting.findMatch( layout );
				if( match != null ){
					DockLayout<?> newLayout = new DockLayout<MultipleCDockable>( REPLACEMENT_FACTORY_ID, match );
					DockLayoutInfo newInfo = new DockLayoutInfo( newLayout );
					newInfo.setLocation( info.getLocation() );
					info = newInfo;
				}
			}
		}
	
		List<DockLayoutComposition> oldChildren = composition.getChildren();
		List<DockLayoutComposition> newChildren = new ArrayList<DockLayoutComposition>( oldChildren.size() );
		
		for( DockLayoutComposition child : oldChildren ){
			newChildren.add( replaceMultipleDockables( frontend, setting, child ) );
		}
		
		return new DockLayoutComposition( info, composition.getAdjacent(), newChildren, composition.isIgnoreChildren() );
	}
	
	@Override
	protected SettingAccess createAccess( DockFrontendInternals frontend, Setting setting ){
		return new CSettingAccess( frontend, setting );
	}
	
	/**
	 * A {@link bibliothek.gui.dock.frontend.DefaultLayoutChangeStrategy.SettingAccess} that modifies the roots by calling
	 * {@link CLayoutChangeStrategy#replaceMultipleDockables(DockFrontendInternals, CSettingAccess, DockLayoutComposition)}.
	 * @author Benjamin Sigg
	 */
	protected class CSettingAccess extends SettingAccess{
		private DockFrontendInternals frontend;
		private Map<String, DockLayoutComposition> modifiedRoots = new HashMap<String, DockLayoutComposition>();
		
		/** all the factories that might be used to create a match */
		private Map<String, MultipleCDockableFactory<?, ?>> factories;
		
		/** the dockables which have not yet been paired off ordered by their factories */
		private Map<String, List<MultipleCDockable>> remainingDockables;
		
		public CSettingAccess( DockFrontendInternals frontend, Setting setting ){
			super( setting );
			this.frontend = frontend;
			
			Map<String, MultipleCDockableFactory<?, ?>> factories = control.getRegister().getFactories();
			this.factories = new HashMap<String, MultipleCDockableFactory<?,?>>();
			for( Map.Entry<String, MultipleCDockableFactory<?, ?>> entry : factories.entrySet() ){
				this.factories.put( PredefinedDockSituation.convertFactoryID( entry.getKey() ), entry.getValue() );
			}
			
			remainingDockables = new HashMap<String, List<MultipleCDockable>>();
			
			for( MultipleCDockable dockable : control.getRegister().getMultipleDockables() ){
				for( Map.Entry<String, MultipleCDockableFactory<?, ?>> entry : factories.entrySet() ){
					if( entry.getValue() == dockable.getFactory() ){
						String key = PredefinedDockSituation.convertFactoryID( entry.getKey() );
						List<MultipleCDockable> list  = remainingDockables.get( key );
						if( list == null ){
							list = new LinkedList<MultipleCDockable>();
							remainingDockables.put( key, list );
						}
						list.add( dockable );
						break;
					}
				}
			}
		}
		
		/**
		 * Searches a match for the meta-data <code>layout</code>. The result of this method will
		 * never be result again for this {@link CSettingAccess}.
		 * @param layout the element whose match is searched
		 * @return the match or <code>null</code> if none was found
		 */
		@SuppressWarnings("unchecked")
		public MultipleCDockable findMatch( DockLayout<?> layout ){
			String factoryId = layout.getFactoryID();
			Object data = layout.getData();
			
			if( data instanceof CommonMultipleDockableLayout ){
				MultipleCDockableLayout multipleLayout = ((CommonMultipleDockableLayout) data).getLayout();
				MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout> factory = (MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout>) factories.get( factoryId );
				if( factory != null ){
					List<MultipleCDockable> list = remainingDockables.get( factoryId );
					if( list != null ){
						Iterator<MultipleCDockable> iterator = list.iterator();
						while( iterator.hasNext() ){
							MultipleCDockable next = iterator.next();
							if( factory.match( next, multipleLayout )){
								iterator.remove();
								if( list.isEmpty() ){
									remainingDockables.remove( factoryId );
								}
								return next;
							}
						}
					}
				}
			}
			
			return null;
		}
		
		@Override
		public DockLayoutComposition getRoot( String root ){
			DockLayoutComposition result = modifiedRoots.get( root );
			if( result == null ){
				result = replaceMultipleDockables( frontend, this, super.getRoot( root ) );
				modifiedRoots.put( root, result );
			}
			return result;
		}
	}
}
