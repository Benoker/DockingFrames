/*
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.disable.TabDisablingStrategyObserver;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.util.SimpleDockElementRepresentative;


/**
 * The standard-implementation of {@link StackDockComponent}. This implementation
 * uses a {@link JTabbedPane} to display its children.
 * 
 * @author Janni Kovacs
 * @author Benjamin Sigg
 * @see StackDockComponent
 * @see JTabbedPane
 */
public class DefaultStackDockComponent extends JTabbedPane implements StackDockComponent {
	/** The Dockables shown on this component and their RemoteRelocators to control {@literal drag & drop} operations */
	private List<Tab> dockables = new ArrayList<Tab>();
	
	/** The controller for which this component is shown */
	private DockController controller;
	
	/** the tab to which mouse-events are currently redirected */
	private Tab mouseTarget;
	
	/** listeners to be informed if the selection changes */
	private List<StackDockComponentListener> listeners = new ArrayList<StackDockComponentListener>();
	
	/** keeps track of all tabs that need to be disabled */
	private TabDisablingStrategyObserver tabDisabling = new TabDisablingStrategyObserver(){
		@Override
		public void setDisabled( Dockable dockable, boolean disabled ){
			int index = indexOf( dockable );
			if( index >= 0 ){
				setEnabledAt( index, !disabled );
			}
		}
	};
	
	/**
	 * Constructs the component, sets the location of the tabs to bottom.
	 */
    public DefaultStackDockComponent(){
        super(BOTTOM);
        
        Listener listener = new Listener();
        addMouseListener( listener );
        addMouseMotionListener( listener );
        addChangeListener( listener );
        
        setOpaque( false );
    }
    
    public void addStackDockComponentListener( StackDockComponentListener listener ){
    	listeners.add( listener );
    }
    
    public void removeStackDockComponentListener( StackDockComponentListener listener ){
    	listeners.remove( listener );
    }
    
    public void setDockTabPlacement( TabPlacement tabSide ){
	    switch( tabSide ){
	    	case BOTTOM_OF_DOCKABLE:
	    		setTabPlacement( BOTTOM );
	    		break;
	    	case LEFT_OF_DOCKABLE:
	    		setTabPlacement( LEFT );
	    		break;
	    	case TOP_OF_DOCKABLE:
	    		setTabPlacement( TOP );
	    		break;
	    	case RIGHT_OF_DOCKABLE:
	    		setTabPlacement( RIGHT );
	    		break;
	    }
    }
    
    public TabPlacement getDockTabPlacement(){
    	switch( getTabPlacement() ){
    		case BOTTOM:
    			return TabPlacement.BOTTOM_OF_DOCKABLE;
    		case LEFT:
    			return TabPlacement.LEFT_OF_DOCKABLE;
    		case RIGHT:
    			return TabPlacement.RIGHT_OF_DOCKABLE;
    		case TOP:
    			return TabPlacement.TOP_OF_DOCKABLE;
    	}
    	
    	throw new IllegalStateException( "unknown position: " + getTabPlacement() );
    }
    
    public void insertTab(String title, Icon icon, Component comp, Dockable dockable, int index) {
        insertTab(title, icon, comp, (String)null, index);
        Tab tab = createTab( dockable );
        dockables.add( index, tab );
        tab.setController( controller );
        tabDisabling.add( dockable );
    }
    
    /**
     * Creates a new representation of a tab on this component.
     * @param dockable the element which is represented by the tab
     * @return the new tab
     */
    protected Tab createTab( Dockable dockable ){
        return new Tab( dockable );
    }

	public void addTab( String title, Icon icon, Component comp, Dockable dockable ){
		addTab( title, icon, comp );
		Tab tab = createTab( dockable );
        dockables.add( tab );
        tab.setController( controller );
        tabDisabling.add( dockable );
	}
	
	public Dockable getDockableAt( int index ){
		return dockables.get( index ).getDockable();
	}
	
	/**
	 * The structure of the {@link JTabbedPane} does not allow its tabs to recognized as {@link DockElementRepresentative}, 
	 * hence this method always returns <code>null</code>.
	 * @param index ignored
	 * @return <code>null</code>
	 */
	public DockElementRepresentative getTabAt( int index ){
		return null;
	}
	
	public void moveTab( int source, int destination ){
		if( source == destination ){
			return;
		}
		if( destination < 0 || destination >= getTabCount() ){
			throw new ArrayIndexOutOfBoundsException();
		}
		int selected = getSelectedIndex();
		
		String title = getTitleAt( source );
		String tooltip = getToolTipTextAt( source );
		Icon icon = getIconAt( source );
		Component comp = getComponentAt( source );
		Dockable dockable = dockables.get( source ).getDockable();
		
		remove( source );
		insertTab( title, icon, comp, dockable, destination );
		setTooltipAt( destination, tooltip );
		
		if( selected == source ){
			selected = destination;
		}
		else if( selected > source && selected <= destination ){
			selected++;
		}
		setSelectedIndex( selected );
	}
    
    @Override
    public void removeAll(){
    	for( Tab tab : dockables ){
    	    tab.setController( null );
    	    tabDisabling.remove( tab.getDockable() );
    	}
        super.removeAll();
    	dockables.clear();
    }
    
    @Override
    public void remove( int index ){
    	Tab tab = dockables.remove( index );
        tab.setController( null );
        tabDisabling.remove( tab.getDockable() );
        super.remove( index );
    }

    public Component getComponent() {
        return this;
    }
    
    @Override
    public void setTitleAt( int index, String title ){
    	super.setTitleAt( index, title == null ? "" : title );
    }
    
    public void setTooltipAt( int index, String newTooltip ) {
        setToolTipTextAt( index, newTooltip );
    }

	public void setController( DockController controller ){
		if( this.controller != controller ){
			if( mouseTarget != null ){
				if( mouseTarget.relocator != null ){
					mouseTarget.relocator.cancel();
				}
				mouseTarget = null;
			}
			
			this.controller = controller;
			tabDisabling.setController( controller );
			
		    for( Tab tab : dockables ){
		        tab.setController( controller );
		    }
		}
	}
	
	public boolean hasBorder() {
	    return true;
	}
	
	public boolean isSingleTabComponent(){
		return false;
	}
	
	public DockElementRepresentative createDefaultRepresentation( DockElement target ){
		return new SimpleDockElementRepresentative( target, this );
	}
	
	public int getIndexOfTabAt( Point mouseLocation ){
		for( int i = 0, n = getTabCount(); i<n; i++ ){
			Rectangle tab = getBoundsAt( i );
			if( tab != null && tab.contains( mouseLocation )){
				return i;
			}
		}
		return -1;
	}
	
	public int indexOf( Dockable dockable ){
		int index = 0;
		for( Tab tab : dockables ){
			if( tab.getDockable() == dockable ){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	@Override
	public Dimension getMinimumSize(){
		Dimension result = new Dimension( 1, 1 );
		for( int i = 0, n = getTabCount(); i<n; i++ ){
    		Dimension size = getComponentAt( i ).getMinimumSize();
    		result.width = Math.max( result.width, size.width );
    		result.height = Math.max( result.height, size.height );
    	}
		return result;
	}
	
	/**
	 * Representation of a single tab of this {@link StackDockComponent}.
	 * @author Benjamin Sigg
	 */
	public class Tab extends ActionPopup{
	    /** the element on the tab */
	    protected Dockable dockable;
	    /** used to drag and drop the tab */
	    private RemoteRelocator relocator;
	    
	    /**
	     * Creates a new Tab
	     * @param dockable the element on the tab
	     */
	    public Tab( Dockable dockable ){
	    	super( true );
	        this.dockable = dockable;
	    }

	    public Dockable getDockable() {
            return dockable;
        }
	    
	    /**
	     * Tells this tab which controller is currently used. Set to <code>null</code>
	     * if this tab is no longer used, or when the connection to a 
	     * {@link DockController} is lost.
	     * @param controller the new source of information, can be <code>null</code>
	     */
	    public void setController( DockController controller ){
	        if( controller == null )
	            relocator = null;
	        else
	            relocator = controller.getRelocator().createRemote( dockable );
	    }

	    public void popup( MouseEvent event ){
	    	if( !event.isConsumed() && event.isPopupTrigger() ){
	    		super.popup( event );
	    	}
	    }
	    
		protected DockActionSource getActions(){
			return dockable.getGlobalActionOffers();
		}

		@Override
		protected Object getSource(){
			return this;
		}
		
		protected boolean isEnabled(){
			return true;
		}
	}
	
	/**
	 * A listener to the enclosing component, using some {@link RemoteRelocator}
	 * to do drag & drop operations.
	 * @author Benjamin Sigg
	 */
	private class Listener extends MouseInputAdapter implements ChangeListener{
		public void stateChanged( ChangeEvent e ){
			for( StackDockComponentListener listener : listeners.toArray( new StackDockComponentListener[ listeners.size() ] )){
				listener.selectionChanged( DefaultStackDockComponent.this );
			}
		}
		
		/**
		 * Updates the value of {@link DefaultStackDockComponent#mouseTarget relocator}
		 * @param x the x-coordinate of the mouse
		 * @param y the y-coordinate of the mouse
		 * @param searchDockable if <code>true</code>, then a new current relocator can be
		 * selected, otherwise the relocator may only be canceled by not exchanged
		 * @param forceSearch if <code>true</code> then a search is always made, even if the user is
		 * not allowed to move a tab anyways
		 */
		private void updateRelocator( int x, int y, boolean searchDockable, boolean forceSearch ){
		    boolean allowed = controller == null || !controller.getRelocator().isDragOnlyTitle();
		    
			if( mouseTarget != null ){
			    if( !allowed ){
			        mouseTarget.relocator.cancel();
			        if( !forceSearch ){
			        	mouseTarget = null;
			        }
			    }
			    
				return;
			}
			
			if( !allowed && !forceSearch ){
			    return;
			}
			
			if( searchDockable ){
				for( int i = 0, n = getTabCount(); i<n; i++ ){
					Rectangle bounds = getBoundsAt( i );
					if( bounds != null && bounds.contains( x, y )){
						mouseTarget = dockables.get( i );
					}
				}
			}
		}
		
		@Override
		public void mousePressed( MouseEvent e ){
			if( e.isConsumed() )
				return;
			
			updateRelocator( e.getX(), e.getY(), true, false );
			if( mouseTarget != null && mouseTarget.relocator != null ){
				mouseTarget.popup( e );
				if( e.isConsumed() ){
					return;
				}
				
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
				Reaction reaction = mouseTarget.relocator.init( mouse.x, mouse.y, 0, 0, e.getModifiersEx() );
				switch( reaction ){
					case BREAK_CONSUMED:
						e.consume();
						// fall through
					case BREAK:
						mouseTarget = null;
						break;
					case CONTINUE:
					case CONTINUE_CONSUMED:
						// consume MouseEvent in all cases: there will never be any component below the tab that should start
						// a drag and drop operation
						e.consume();
				}
			}
			else{
				updateRelocator( e.getX(), e.getY(), true, true );
				if( mouseTarget != null ){
					mouseTarget.popup( e );
					mouseTarget = null;
				}
			}
		}
		
		@Override
		public void mouseReleased( MouseEvent e ){
			if( e.isConsumed() )
				return;
			
			updateRelocator( e.getX(), e.getY(), false, false );
			if( mouseTarget != null && mouseTarget.relocator != null ){
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
				Reaction reaction = mouseTarget.relocator.drop( mouse.x, mouse.y, e.getModifiersEx() );
				switch( reaction ){
					case BREAK_CONSUMED:
						e.consume();
						mouseTarget = null;
						break;
					case BREAK:
						mouseTarget.popup( e );
						mouseTarget = null;
						break;
					case CONTINUE_CONSUMED:
						e.consume();
						break;
				}
			}
			else{
				updateRelocator( e.getX(), e.getY(), true, true );
				if( mouseTarget != null ){
					mouseTarget.popup( e );
					mouseTarget = null;
				}
			}
		}
		
		@Override
		public void mouseDragged( MouseEvent e ){
			if( e.isConsumed() )
				return;
			
			updateRelocator( e.getX(), e.getY(), false, false );
			if( mouseTarget != null && mouseTarget.relocator != null ){
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
				Reaction reaction = mouseTarget.relocator.drag( mouse.x, mouse.y, e.getModifiersEx() );
				switch( reaction ){
					case BREAK_CONSUMED:
						e.consume();
						// fall through
					case BREAK:
						mouseTarget = null;
						break;
					case CONTINUE_CONSUMED:
						e.consume();
						break;
				}
			}
		}
	}
}