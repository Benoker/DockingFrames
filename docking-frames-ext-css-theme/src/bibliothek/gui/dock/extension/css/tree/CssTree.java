/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.tree;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.extension.css.CssNode;
import bibliothek.gui.dock.extension.css.CssPath;
import bibliothek.gui.dock.extension.css.doc.CssDocKey;
import bibliothek.gui.dock.extension.css.doc.CssDocPath;
import bibliothek.gui.dock.extension.css.doc.CssDocPathNode;
import bibliothek.gui.dock.extension.css.doc.CssDocText;
import bibliothek.gui.dock.extension.css.path.FlapDockStationNode;
import bibliothek.gui.dock.extension.css.path.NamedCssNode;
import bibliothek.gui.dock.extension.css.path.ScreenDockStationNode;
import bibliothek.gui.dock.extension.css.path.SplitDockStationNode;
import bibliothek.gui.dock.extension.css.path.StackDockStationNode;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * {@link CssTree} provides a {@link CssPath} for each {@link DockElement}, the paths
 * share {@link CssNode}s whenever possible. Each path is automatically updated whenever
 * a property associated with a {@link DockElement} (e.g. its location) changes.
 * @author Benjamin Sigg
 */
public class CssTree {
	/** the controller in whose realm this tree works */
	private DockController controller;
	
	/** factories for creating relation {@link CssNode}s */
	private Map<Class<?>, CssRelationNodeFactory<?>> relationFactories = new HashMap<Class<?>, CssRelationNodeFactory<?>>(); 
	
	/** factories for creating {@link CssNode}s */
	private Map<Class<?>, CssNodeFactory<?>> nodeFactories = new HashMap<Class<?>, CssNodeFactory<?>>();
	
	/** cached paths for the known existing {@link DockElement}s */
	private Map<DockElement, CssPath> pathCache = new HashMap<DockElement, CssPath>();
	
	/** cached nodes for the known existing {@link DockElement}s */
	private Map<DockElement, CssNode> selfCache = new HashMap<DockElement, CssNode>();
	
	private boolean bound = false;
	
	private DockRegisterListener registerListener = new DockRegisterAdapter(){
		@Override
		public void dockableUnregistered( DockController controller, Dockable dockable ){
			pathCache.remove( dockable );
			selfCache.remove( dockable );
		}
		
		@Override
		public void dockStationUnregistered( DockController controller, DockStation station ){
			pathCache.remove( station );
			selfCache.remove( station );
		}
	};
	
	/**
	 * Creates a new tree.
	 * @param controller the controller in whose realm this tree has to work
	 */
	public CssTree( DockController controller ){
		if( controller == null ){
			throw new IllegalArgumentException( "controller must not be null" );
		}
		this.controller = controller;
		initRelationFactories();
		initNodeFactories();
	}
	
	/**
	 * Informs this tree that it is in use and can acquire resources.
	 */
	public void bind(){
		bound = true;
		controller.getRegister().addDockRegisterListener( registerListener );
	}
	
	/**
	 * Informs this tree that it is no longer in use and should release all resources.
	 */
	public void unbind(){
		bound = false;
		controller.getRegister().removeDockRegisterListener( registerListener );
		pathCache.clear();
		selfCache.clear();
	}
	
	private void initRelationFactories(){
		putRelationFactory( StackDockStation.class, new CssRelationNodeFactory<StackDockStation>(){
			@Override
			public CssNode createRelation( StackDockStation parent, Dockable child ){
				return new StackDockStationNode( parent, child );
			}
		});
		putRelationFactory( ScreenDockStation.class, new CssRelationNodeFactory<ScreenDockStation>(){
			@Override
			public CssNode createRelation( ScreenDockStation parent, Dockable child ){
				return new ScreenDockStationNode( parent, child );
			}
		});
		putRelationFactory( FlapDockStation.class, new CssRelationNodeFactory<FlapDockStation>(){
			@Override
			public CssNode createRelation( FlapDockStation parent, Dockable child ){
				return new FlapDockStationNode( parent, child );
			}
		});
		putRelationFactory( SplitDockStation.class, new CssRelationNodeFactory<SplitDockStation>(){
			@Override
			public CssNode createRelation( SplitDockStation parent, Dockable child ){
				return new SplitDockStationNode( parent, child ); 
			}
		});
	}
	
	@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.MAJOR,
			target=Version.VERSION_1_1_2, description="have better self nodes, e.g. a dockable could have a property for its title text")
	private void initNodeFactories(){
		putFactory( StackDockStation.class, new CssNodeFactory<StackDockStation>(){
			@Override
			public CssNode create( StackDockStation object ){
				return new NamedCssNode( "stack" );
			}
		});
		putFactory( FlapDockStation.class, new CssNodeFactory<FlapDockStation>(){
			@Override
			public CssNode create( FlapDockStation object ){
				return new NamedCssNode( "flap" );
			}
		});
		putFactory( ScreenDockStation.class, new CssNodeFactory<ScreenDockStation>(){
			@Override
			public CssNode create( ScreenDockStation object ){
				return new NamedCssNode( "screen" );
			}
		});
		putFactory( SplitDockStation.class, new CssNodeFactory<SplitDockStation>(){
			@Override
			public CssNode create( SplitDockStation object ){
				return new NamedCssNode( "split" );
			}
		});
		putFactory( Dockable.class, new CssNodeFactory<Dockable>(){
			public CssNode create( Dockable element ){
				return new NamedCssNode( "dockable" );
			}
		});
		putFactory( DockElement.class, new CssNodeFactory<DockElement>(){
			public CssNode create( DockElement element ){
				return new NamedCssNode( "element" );
			}
		});
	}
	
	/**
	 * Adds a factory to this tree. The factory will be used by the method {@link #getRelationNode(DockElement)}.
	 * @param clazz the type of {@link DockStation} that can be managed by <code>factory</code>
	 * @param factory the new factory
	 */
	public <S extends DockStation> void putRelationFactory( Class<S> clazz, CssRelationNodeFactory<S> factory ){
		relationFactories.put( clazz, factory );
	}

	/**
	 * Adds a factory to this tree. The factory will be used to create {@link CssNode}s describing various 
	 * {@link Object}s.
	 * @param clazz the clazz to convert
	 * @param factory the factory for creating new {@link CssNode}s from {@link Object}s of type <code>clazz</code>
	 */
	public <S> void putFactory( Class<S> clazz, CssNodeFactory<S> factory ){
		nodeFactories.put( clazz, factory );
	}
	
	/**
	 * Gets or creates a path pointing to <code>element</code>.
	 * @param element the element to which the {@link CssPath} should point
	 * @return the path, may be a new {@link CssPath} or may be shared with other modules
	 */
	@CssDocPath(
			id="getPathFor",
			description=@CssDocText(text="Generic path for a Dockable or a DockStation, the nodes are created by different CssNodeFactorys."),
			unordered={
				@CssDocPathNode(name=@CssDocKey(key="split", description=@CssDocText(text="Denotes a SplitDockStation when using the default CssNodeFactories"))),
				@CssDocPathNode(name=@CssDocKey(key="flap", description=@CssDocText(text="Denotes a FlapDockStation when using the default CssNodeFactories"))),
				@CssDocPathNode(name=@CssDocKey(key="stack", description=@CssDocText(text="Denotes a StackDockStation when using the default CssNodeFactories"))),
				@CssDocPathNode(name=@CssDocKey(key="screen", description=@CssDocText(text="Denotes a ScreenDockStation when using the default CssNodeFactories"))),
				@CssDocPathNode(name=@CssDocKey(key="dockable", description=@CssDocText(text="Denotes a Dockable when using the default CssNodeFactories"))),
				@CssDocPathNode(name=@CssDocKey(key="element", description=@CssDocText(text="Denotes a generic DockElement when using the default CssNodeFactories"))),
				@CssDocPathNode(reference=StackDockStationNode.class),
				@CssDocPathNode(reference=ScreenDockStationNode.class),
				@CssDocPathNode(reference=SplitDockStationNode.class),
				@CssDocPathNode(reference=FlapDockStationNode.class),
			})
	public CssPath getPathFor( DockElement element ){
		CssPath path = pathCache.get( element );
		if( path == null ){
			path = new DockElementPath( this, element );
		}
		if( bound ){
			pathCache.put( element, path );
		}
		return path;
	}
	
	/**
	 * Gets the node that describes <code>element</code> itself.
	 * @param element the element whose description is searched
	 * @return the description to <code>element</code>
	 */
	public CssNode getSelfNode( DockElement element ){
		CssNode node = selfCache.get( element );
		if( node == null ){
			node = create( element.getClass(), element );
		}
		if( bound ){
			selfCache.put( element, node );
		}
		return node;
	}
	
	@SuppressWarnings("unchecked")
	private CssNode create( Class<?> type, Object element ){
		if( type == null ){
			return null;
		}
		
		CssNodeFactory<Object> factory = (CssNodeFactory<Object>)nodeFactories.get( type );
		if( factory != null ){
			return factory.create( element );
		}
		
		for( Class<?> interfaze : type.getInterfaces()){
			CssNode result = create( interfaze, element );
			if( result != null ){
				return result;
			}
		}
		
		return create( type.getSuperclass(), element );
	}
	
	/**
	 * Gets the node that describes the current relation between <code>element</code>
	 * and its parent {@link DockStation} (if there is any). This node is not updated
	 * if the relationship is broken.
	 * @param element the element whose relation is searched
	 * @return the relation to the parent station or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public CssNode getRelationNode( DockElement element ){
		Dockable dockable = element.asDockable();
		if( dockable == null ){
			return null;
		}
		DockStation parent = dockable.getDockParent();
		if( parent == null ){
			return null;
		}
		Class<?> clazz = parent.getClass();
		while( clazz != null ){
			CssRelationNodeFactory<DockStation> factory = (CssRelationNodeFactory<DockStation>)relationFactories.get( clazz );
			if( factory == null ){
				clazz = clazz.getSuperclass();
			}
			else{
				return factory.createRelation( parent, dockable );
			}
		}
		return null;
	}
}
