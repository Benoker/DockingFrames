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

package bibliothek.gui.dock.station.stack;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorCodes;


/**
 * The standard-implementation of {@link StackDockComponent}. This implementation
 * uses a {@link JTabbedPane} to display its children.
 * 
 * @author Janni Kovacs
 * @author Benjamin Sigg
 * @see StackDockComponent
 * @see JTabbedPane
 */
@ColorCodes( {"stack.tab.foreground", "stack.tab.background" } )
public class DefaultStackDockComponent extends JTabbedPane implements StackDockComponent {
	/** The Dockables shown on this component and their RemoteRelocators to control drag&drop operations */
	private List<Tab> dockables = new ArrayList<Tab>();
	
	/** The controller for which this component is shown */
	private DockController controller;
	
	/** the currently used remote */
	private RemoteRelocator relocator;
	
	/** the station for which this component is used */
	private StackDockStation station;
	
	/**
	 * Constructs the component, sets the location of the tabs to bottom.
	 */
    public DefaultStackDockComponent( StackDockStation station ){
        super(BOTTOM);
    
        this.station = station;
        
        Listener listener = new Listener();
        addMouseListener( listener );
        addMouseMotionListener( listener );
    }

    public void insertTab(String title, Icon icon, Component comp, Dockable dockable, int index) {
        insertTab(title, icon, comp, (String)null, index);
        Tab tab = new Tab(  dockable );
        dockables.add( index, tab );
        tab.connect( controller );
    }

	public void addTab( String title, Icon icon, Component comp, Dockable dockable ){
		addTab( title, icon, comp );
		Tab tab = new Tab(  dockable );
        dockables.add( tab );
        tab.connect( controller );
	}
    
    @Override
    public void removeAll(){
    	for( Tab tab : dockables )
    	    tab.connect( null );
        super.removeAll();
    	dockables.clear();
    }
    
    @Override
    public void remove( int index ){
        Tab tab = dockables.remove( index );
        tab.connect( null );
        super.remove( index );
    }

    public Component getComponent() {
        return this;
    }

	public void setController( DockController controller ){
		if( this.controller != controller ){
			if( relocator != null ){
				relocator.cancel();
				relocator = null;
			}
			
			this.controller = controller;
			if( controller == null ){
			    for( Tab tab : dockables ){
			        tab.relocator = null;
			        tab.connect( null );
			    }
			}
			else{
			    for( Tab tab : dockables ){
			        tab.relocator = controller.getRelocator().createRemote( tab.dockable );
			        tab.connect( controller );
			    }
			}
		}
	}
	
	/**
	 * Representation of a single tab of this {@link StackDockComponent}.
	 * @author Benjamin Sigg
	 *
	 */
	private class Tab{
	    /** the element on the tab */
	    public Dockable dockable;
	    /** used to drag and drop the tab */
	    public RemoteRelocator relocator;
	    
	    /** the foreground color */
	    public TabColor foreground;
	    /** the background color */
	    public TabColor background;
	    
	    /**
	     * Creates a new Tab
	     * @param dockable the element on the tab
	     */
	    public Tab( Dockable dockable ){
	        this.dockable = dockable;
	        
	        if( controller != null )
	            relocator = controller.getRelocator().createRemote( dockable );
	        
	        foreground = new TabColor( "stack.tab.foreground", TabColor.class, station, dockable, null ){
	            @Override
	            protected void changed( Color oldColor, Color newColor ) {
	                int index = station.indexOf( Tab.this.dockable );
	                if( index >= 0 )
	                    setForegroundAt( index, newColor );
	            }
	        };
	        background = new TabColor( "stack.tab.background", TabColor.class, station, dockable, null ){
                @Override
                protected void changed( Color oldColor, Color newColor ) {
                    int index = station.indexOf( Tab.this.dockable );
                    if( index >= 0 )
                        setBackgroundAt( index, newColor );
                }
            };
	    }
	    
	    /**
	     * Updates the colors of this tab
	     * @param controller the new source of information, can be <code>null</code>
	     */
	    public void connect( DockController controller ){
	        foreground.connect( controller );
	        background.connect( controller );
	    }
	}
	
	/**
	 * A listener to the enclosing component, using some {@link RemoteRelocator}
	 * to do drag & drop operations.
	 * @author Benjamin Sigg
	 */
	private class Listener extends MouseInputAdapter{
		/**
		 * Updates the value of {@link DefaultStackDockComponent#relocator relocator}
		 * @param x the x-coordinate of the mouse
		 * @param y the y-coordinate of the mouse
		 */
		private void updateRelocator( int x, int y ){
			if( relocator != null )
				return;
			
			for( int i = 0, n = getTabCount(); i<n; i++ ){
				Rectangle bounds = getBoundsAt( i );
				if( bounds != null && bounds.contains( x, y )){
					relocator = dockables.get( i ).relocator;
				}
			}
		}
		
		@Override
		public void mousePressed( MouseEvent e ){
			if( e.isConsumed() )
				return;
			
			updateRelocator( e.getX(), e.getY() );
			if( relocator != null ){
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
				Reaction reaction = relocator.init( mouse.x, mouse.y, 0, 0, e.getModifiersEx() );
				switch( reaction ){
					case BREAK_CONSUMED:
						e.consume();
					case BREAK:
						relocator = null;
						break;
					case CONTINUE_CONSUMED:
						e.consume();
						break;
				}
			}
		}
		
		@Override
		public void mouseReleased( MouseEvent e ){
			if( e.isConsumed() )
				return;
			
			updateRelocator( e.getX(), e.getY() );
			if( relocator != null ){
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
				Reaction reaction = relocator.drop( mouse.x, mouse.y, e.getModifiersEx() );
				switch( reaction ){
					case BREAK_CONSUMED:
						e.consume();
					case BREAK:
						relocator = null;
						break;
					case CONTINUE_CONSUMED:
						e.consume();
						break;
				}
			}
		}
		
		@Override
		public void mouseDragged( MouseEvent e ){
			if( e.isConsumed() )
				return;
			
			updateRelocator( e.getX(), e.getY() );
			if( relocator != null ){
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
				Reaction reaction = relocator.drag( mouse.x, mouse.y, e.getModifiersEx() );
				switch( reaction ){
					case BREAK_CONSUMED:
						e.consume();
					case BREAK:
						relocator = null;
						break;
					case CONTINUE_CONSUMED:
						e.consume();
						break;
				}
			}
		}
	}
}