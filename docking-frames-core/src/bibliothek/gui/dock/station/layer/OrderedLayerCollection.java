/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.layer;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * The {@link OrderedLayerCollection} is a helper class that allows order a set
 * {@link DockStation}s according to the rules defined in {@link DockStationDropLayer}.
 * @author Benjamin Sigg
 */
public class OrderedLayerCollection {
	/** all the currently known stations */
	private Set<DockStation> stations = new HashSet<DockStation>();
	
	/** factory applied to all {@link DockStation}s to find their {@link DockStationDropLayer}s */
	private DockStationDropLayerFactory factory;
	
	/**
	 * Creates a new collection
	 * @param factory applied to all {@link DockStation}s in order to find their {@link DockStationDropLayer}s.
	 */
	public OrderedLayerCollection( DockStationDropLayerFactory factory ){
		this.factory = factory;
	}
	
	/**
	 * Adds an additional station which is to be ordered.
	 * @param station the additional station
	 */
	public void add( DockStation station ){
		stations.add( station );
	}
	
	/**
	 * Sorts the current set of {@link DockStation}s currently known to this
	 * collection.
	 * @param x the x-coordinate of the mouse on the screen
	 * @param y the y-coordinate of the mouse on the screen
	 * @return the ordered stations, where the first station is the station with the highest priority 
	 */
	public List<DockStation> sort( int x, int y ){
		Map<DockStation, Node> nodes = new HashMap<DockStation, OrderedLayerCollection.Node>();
		for( DockStation station : stations ){
			nodes.put( station, new Node( station ) );
		}
		
		List<Node> roots = new ArrayList<Node>();
		for( Node node : nodes.values() ){
			if( !node.register( nodes )){
				roots.add( node );
			}
		}
		
		for( Node root : roots ){
			root.modify();
		}
		
		List<DockStationDropLayer> layers = new ArrayList<DockStationDropLayer>();
		for( Node root : roots ){
			root.collect( layers, x, y );
		}
		
		layers = sort( layers );
		
		List<DockStation> result = new ArrayList<DockStation>();
		for( DockStationDropLayer layer : layers ){
			if( nodes.remove( layer.getStation() ) != null ){
				result.add( layer.getStation() );
			}
		}
		
		return result;
	}
	
	/**
	 * Creates a new ordered list containing all items of <code>layer</code>, the new list
	 * is built by an algorithm that is resistent against inconstant ordering.
	 * @param layers the layers to order
	 * @return the ordered layers
	 */
	protected List<DockStationDropLayer> sort( List<DockStationDropLayer> layers ){
		List<DockStationDropLayer> result = new LinkedList<DockStationDropLayer>();
		
		for( DockStationDropLayer layer : layers ){
			int index = 0;
			
	        // insertion sort
	        for( DockStationDropLayer resultLayer : result ){
	            int compare = compare( resultLayer, layer );
	            if( compare > 0 )
	                break;
	            else
	                index++;
	        }
	        
	        result.add( index, layer );
		}
		
		return result;
	}
	
	/**
	 * Works like {@link Comparator#compare(Object, Object)}, compares <code>a</code> to <code>b</code>.
	 * @param a the first object to compare
	 * @param b the second object to compare
	 * @return a value less/equal/greater to 0 depending on whether <code>a</code> is less/equal/greater than <code>b</code>.
	 */
	protected int compare( DockStationDropLayer a, DockStationDropLayer b ){
		if( a == b ){
			return 0;
		}
		
		// direct comparison
		boolean compareA = a.canCompare( b );
		boolean compareB = b.canCompare( a );
		
		if( compareA && compareB ){
			int resultA = -a.compare( b );
			int resultB = b.compare( a );
			
			if( resultA == 0 ){
				return resultB;
			}
			if( resultB == 0 ){
				return resultA;
			}
			
			if( resultA < 0 == resultB < 0 ){
				return resultA;
			}
			// contradiction
		}
		
		// Priority
		LayerPriority pa = a.getPriority();
		LayerPriority pb = b.getPriority();
		
		int priority = pa.compareTo( pb );
		if( priority != 0 ){
			return priority;
		}
		
		// same priority
		int reverse;
		if( pa.isReverse() ){
			reverse = -1;
		}
		else{
			reverse = 1;
		}
		
		// by relationship
		DockStation sa = a.getStation();
		DockStation sb = b.getStation();
		
		if( DockUtilities.isAncestor( sa, sb )){
			return 1 * reverse;
		}
		if( DockUtilities.isAncestor( sb, sa )){
			return -1 * reverse;
		}
		
		// by components
		Component compA = a.getComponent();
        Component compB = b.getComponent();
        
        if( compA != null && compB != null ){
	        Window windowA = SwingUtilities.getWindowAncestor( compA );
	        Window windowB = SwingUtilities.getWindowAncestor( compB );
	
	        if( windowA != null && windowB != null ){
	            if( windowA == windowB ){
	            	if( DockUI.isOverlapping( compA, compB )){
	            		return -1 * reverse;
	            	}
	            	if( DockUI.isOverlapping( compB, compA )){
	            		return 1  * reverse;
	            	}
	            }
	            else{
	            	if( isParent( windowA, windowB ))
	                    return 1 * reverse;
	                
	                if( isParent( windowB, windowA ))
	                    return -1 * reverse;
	                
	                boolean mouseOverA = windowA.getMousePosition() != null;
	                boolean mouseOverB = windowB.getMousePosition() != null;
	                
	                if( mouseOverA && !mouseOverB ){
	                	return -1 * reverse;
	                }
	                if( !mouseOverA && mouseOverB ){
	                	return 1 * reverse;
	                }
	            }
	        }
        }
		
		return 0;
	}
	
    /**
     * Tells whether <code>parent</code> is really a parent of <code>child</code>
     * or not.
     * @param parent a window which may be an ancestor of <code>child</code>
     * @param child a window which may be child of <code>parent</code>
     * @return <code>true</code> if <code>parent</code> is an
     * ancestor of <code>child</code>
     */
    private boolean isParent( Window parent, Window child ){
        Window temp = child.getOwner();
        while( temp != null ){
            if( temp == parent )
                return true;
            
            temp = temp.getOwner();
        }
        
        return false;
    }
	
	/**
	 * Describes one {@link DockStation} and its {@link DockStationDropLayer}s.
	 * @author Benjamin Sigg
	 */
	private class Node{
		private Node parent;
		private List<Node> children;
		
		private DockStation station;
		private DockStationDropLayer[] layers;
		
		/**
		 * Creates a new node and stores the {@link DockStationDropLayer}s
		 * @param station the station represented by this node.
		 */
		public Node( DockStation station ){
			this.station = station;
			if( station.getDockableCount() > 0 ){
				children = new ArrayList<Node>( station.getDockableCount() );
			}
			layers = factory.getLayers( station );
		}
		
		/**
		 * Searches the parent of <code>this</code> in <code>nodes</code> and
		 * registers itself as child.
		 * @param nodes all the known stations
		 * @return <code>true</code> if the parent was found, <code>false</code>
		 * if not (in which case this is a root node).
		 */
		public boolean register( Map<DockStation, Node> nodes ){
			Dockable child = station.asDockable();
			while( child != null ){
				DockStation station = child.getDockParent();
				if( station == null ){
					return false;
				}
				
				Node node = nodes.get( station );
				if( node != null ){
					node.children.add( this );
					this.parent = node;
					return true;
				}
				
				child = station.asDockable();
			}
			return false;
		}
		
		/**
		 * Gets all the layers of this node.
		 * @return all the layers
		 */
		public DockStationDropLayer[] getLayers(){
			return layers;
		}
		
		/**
		 * Calls {@link #modify()} on all children nodes, then 
		 * calls {@link DockStationDropLayer#modify(DockStationDropLayer)} on all layers of
		 * all parent nodes.
		 */
		public void modify(){
			if( children != null ){
				for( Node child : children ){
					child.modify();
				}
			}
			
			Node parent = this.parent;
			while( parent != null ){
				for( int i = 0; i < layers.length; i++ ){
					for( DockStationDropLayer layer : parent.getLayers() ){
						layers[i] = layer.modify( layers[i] );
					}
				}
				parent = parent.parent;
			}
		}
		
		/**
		 * Collects all layers of this node and of its children. Layers which do not contain
		 * the point <code>x/y</code> are ignored.
		 * @param layers the list to fill
		 * @param x the x-coordinate of the mouse on the screen
		 * @param y the y-coordinate of the mouse on the screen 
		 */
		public void collect( List<DockStationDropLayer> layers, int x, int y ){
			if( children != null ){
				for( Node child : children ){
					child.collect( layers, x, y );
				}
			}
			for( DockStationDropLayer layer : this.layers ){
				if( layer.contains( x, y )){
					layers.add( layer );
				}
			}
		}
	}
}
