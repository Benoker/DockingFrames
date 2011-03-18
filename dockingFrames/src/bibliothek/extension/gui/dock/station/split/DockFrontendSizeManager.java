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
package bibliothek.extension.gui.dock.station.split;


import java.awt.Insets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.event.DockFrontendAdapter;
import bibliothek.gui.dock.event.DockFrontendListener;
import bibliothek.gui.dock.event.DockRelocatorAdapter;
import bibliothek.gui.dock.event.DockRelocatorListener;
import bibliothek.gui.dock.layout.AdjacentDockFactory;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;
import bibliothek.util.xml.XElement;

/**
 * Observes a {@link DockFrontend} in order to find out which {@link Dockable}s
 * are available, and when a drag &amp; drop operation starts.<br> 
 * Clients should call {@link #setFrontend(DockFrontend)} in order to connect
 * this manager with a {@link DockFrontend}. They should call the method
 * {@link #setFrontend(DockFrontend)} again with a <code>null</code> argument
 * to disconnect this manager.
 * 
 * @author Parag Shah
 * @author Benjamin Sigg
 * 
 * @deprecated Due to the new placeholder mechanism this class/interface has become obsolete, it is no longer used
 * anywhere. Clients should now use a {@link PlaceholderStrategy} to assign identifiers to the {@link Dockable}s, with
 * these identifiers the location and size of a {@link Dockable} is stored in a much more consistent way than using the
 * {@link LbSplitLayoutManager}. This class/interface will be removed in a future release.
 */
@Deprecated
@Todo(compatibility=Compatibility.BREAK_MINOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_1,
		description="remove this class")
public class DockFrontendSizeManager implements SizeManager{
    /** the known sizes of several {@link Dockable}s */
    private Map<Dockable, Double> sizes = new HashMap<Dockable, Double>();

    /** the frontend for which this manager works */
    private DockFrontend frontend;

    /** this listener registers the start of any drag and drop operation and stores the size of the involved {@link Dockable} */
    private DockRelocatorListener relocatorListener = new DockRelocatorAdapter(){
        @Override
        public void init(DockController controller, Dockable dockable){
            //we are dragging out of a split dock station
            if( needToTrackChange(dockable) ){
                double dockableSize = computeRelativeSizeOfDockable( dockable );
                if( dockableSize >= 0 ){
                    put( dockable, dockableSize );
                }
            }        
        }   
    };

    /** this listener ensures that there are no dangling references */
    private DockFrontendListener frontendListener = new DockFrontendAdapter(){
        @Override
        public void removed( DockFrontend frontend, Dockable dockable ) {
            sizes.remove( dockable );
        }
    };

    /**
     * Used to store the contents of this manager together with the layout
     * of the whole {@link #frontend}
     */
    private AdjacentDockFactory<Double> sizeConverter = new AdjacentDockFactory<Double>(){
        public boolean interested( DockElement element ) {
            return sizes.containsKey( element );
        }
        
        public boolean interested( PerspectiveElement element ){
        	return false;
        }
        
        public String getID() {
            return "dock.extension.DockFrontendSizeManager.sizes";
        }

        public Double getLayout( DockElement element, Map<Dockable, Integer> children ) {
            return sizes.get( element );
        }

        public Double getPerspectiveLayout( PerspectiveElement element, Map<PerspectiveDockable, Integer> children ){
        	return null;
        }
        
        public Double read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException {
            return in.readDouble();
        }

        public Double read( XElement element, PlaceholderStrategy placeholders ) {
            return element.getDouble();
        }

        public void setLayout( DockElement element, Double layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
            setLayout( element, layout, placeholders );
        }

        public void setLayout( DockElement element, Double layout, PlaceholderStrategy placeholders ) {
            Dockable dockable = element.asDockable();
            if( dockable != null ){
                sizes.put( dockable, layout );
            }
        }

        public void write( Double layout, DataOutputStream out ) throws IOException {
            out.writeDouble( layout );
        }

        public void write( Double layout, XElement element ) {
            element.setDouble( layout );
        }
    };
    
    /**
     * Returns the old size of <code>dockable</code> as a percentage of the size 
     * of the parent component. This size will be used for the 
     * <code>dockable</code> before dropping it onto a {@link SplitDockStation}.
     * @param dockable the element for which the size is requested
     * @return The size that should be used for dropping <code>dockable</code>,
     * -1 if not specified
     */
    public double getSize( Dockable dockable ){
        Double size = sizes.get( dockable );
        if( size == null )
            return -1;

        return size;
    }

    /**
     * Sets the frontend for which this manager works. This method will clear
     * any stored information.
     * @param frontend the new frontend, can be <code>null</code>
     */
    public void setFrontend( DockFrontend frontend ){
        if( this.frontend != frontend ){
            if( this.frontend != null ){
                this.frontend.getController().getRelocator().removeDockRelocatorListener( relocatorListener );
                this.frontend.removeFrontendListener( frontendListener );
                this.frontend.unregisterAdjacentFactory( sizeConverter );
                sizes.clear();
            }

            this.frontend = frontend;

            if( this.frontend != null ){
                this.frontend.getController().getRelocator().addDockRelocatorListener( relocatorListener );
                this.frontend.addFrontendListener( frontendListener );
                this.frontend.registerAdjacentFactory( sizeConverter );
            }
        }
    }

    /**
     * Tells whether there is any need to track the size of <code>dockable</code>.<br>
     * The default behavior of this method is to check whether <code>dockable</code>
     * has a {@link SplitDockStation} as parent, and to ensure that this station
     * has more than just one child (otherwise the child has size 1.0, 
     * which does not really help later).
     * @param dockable the element to check
     * @return <code>true</code> if the size of <code>dockable</code> is to
     * be stored, <code>false</code> otherwise
     */
    protected boolean needToTrackChange( Dockable dockable ){
        SplitDockStation parentStation = getFirstAncestorSplitDockStation(dockable);
        boolean needToTrack = false;
        if( parentStation != null ){
            Root root = parentStation.getRoot();
            SplitNode child = root.getChild();
            if( child instanceof Node ){
                needToTrack = true;
            }
        }
        return needToTrack;        
    }

    /**
     * Computes the size of <code>dockable</code> in relation to it's parent/ancestor
     * {@link SplitDockStation}.
     * @param dockable the element whose size is to be calculated
     * @return the size
     */
    private double computeRelativeSizeOfDockable( Dockable dockable ){
        double size = -1;
        SplitDockStation station = getFirstAncestorSplitDockStation( dockable );
        
        if( station != null ){
            SplitNode splitNode = station.getRoot().getChild();
            double stationSize = 0.0;
            double dockableSize = 0.0;
            SplitDockStation.Orientation orientation = 
                SplitDockStation.Orientation.HORIZONTAL; 
            if( splitNode instanceof Node ){
                orientation = ((Node)splitNode).getOrientation();
            }
            
            /*
            else
            {
                System.out.println("The Node object representing this " + 
                        "SplitDockStation does not have an orientation. Using " +
                        "default orientation of HORIZONTAL"    + station);
            }
            */
            
            Insets insets = station.getBasePane().getInsets();
            
            if( orientation == SplitDockStation.Orientation.HORIZONTAL ){
                stationSize = station.getWidth() - insets.left - insets.right;
                dockableSize = dockable.getComponent().getWidth();
            }
            else{
                stationSize = station.getHeight() - insets.top - insets.bottom;
                dockableSize = dockable.getComponent().getHeight();
            }
            
            size = dockableSize / stationSize;
        }
        
        if( Double.isNaN( size ))
            size = -1;
        
        return size;
    }

    private SplitDockStation getFirstAncestorSplitDockStation( Dockable dockable ){
        Dockable currentDockable = dockable;
        DockStation parent;
        while( (parent = currentDockable.getDockParent()) != null ){
            if( parent instanceof SplitDockStation ){
                return (SplitDockStation)parent;
            }

            currentDockable = parent.asDockable();
            if( currentDockable == null )
                return null;
        }

        return null;
    }

    private void put( Dockable dockable, double size ){
        sizes.put( dockable, size );
    }
}
