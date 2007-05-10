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

package bibliothek.gui.dock.station.flap;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.IconManager;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SimpleMenuAction;
import bibliothek.gui.dock.action.actions.SimpleSelectableAction;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.FlapDockStation.Direction;

/**
 * This {@link DockAction} is used as an action of a {@link FlapDockStation}.
 * The action itself changes the direction in which a <code>FlapDockStation</code>
 * opens. This is done by calling the {@link FlapDockStation#setAutoDirection(boolean) setAutoDirection}
 * and the {@link FlapDockStation#setDirection(Direction)}-methods.  
 * @author Benjamin Sigg
 */
public class FlapDockDirection extends SimpleMenuAction implements ListeningDockAction{
    private DirectedArrow north, south, east, west, center;
    private DockController controller;
    private Listener listener = new Listener();
    
    /**
     * Creates the icon of the action, and sets the text and tooltip of the action.
     * @param controller The controller for which this action will be used. The
     * controller is needed to retrieve the icons for this action.
     */
    public FlapDockDirection( DockController controller ){
        setText( DockUI.getDefaultDockUI().getString( "flap.direction" ) );
        setTooltipText( DockUI.getDefaultDockUI().getString( "flap.direction.tooltip" ) );
        
        center = new DirectedArrow( null );
        north = new DirectedArrow( Direction.NORTH );
        south = new DirectedArrow( Direction.SOUTH );
        east = new DirectedArrow( Direction.EAST );
        west = new DirectedArrow( Direction.WEST );
        
        north.setText( DockUI.getDefaultDockUI().getString( "flap.direction.north" ));
        north.setTooltipText( DockUI.getDefaultDockUI().getString( "flap.direction.north.tooltip" ));
        south.setText( DockUI.getDefaultDockUI().getString( "flap.direction.south" ));
        south.setTooltipText( DockUI.getDefaultDockUI().getString( "flap.direction.south.tooltip" ));
        east.setText( DockUI.getDefaultDockUI().getString( "flap.direction.east" ));
        east.setTooltipText( DockUI.getDefaultDockUI().getString( "flap.direction.east.tooltip" ));
        west.setText( DockUI.getDefaultDockUI().getString( "flap.direction.west" ));
        west.setTooltipText( DockUI.getDefaultDockUI().getString( "flap.direction.west.tooltip" ));
        center.setText( DockUI.getDefaultDockUI().getString( "flap.direction.center" ));
        center.setTooltipText( DockUI.getDefaultDockUI().getString( "flap.direction.center.tooltip" ));
        
        DefaultDockActionSource source = new DefaultDockActionSource();
        source.add( center );
        source.addSeparator();
        source.add( north, south, east, west );
        setMenu( source );
        
        setController( controller );
    }
    
    public void setController( DockController controller ) {
        if( this.controller != controller ){
            if( this.controller != null ){
                IconManager icons = this.controller.getIcons();
                icons.remove( "flap.direction", listener );
                icons.remove( "flap.south", listener );
                icons.remove( "flap.east", listener );
                icons.remove( "flap.west", listener );
                icons.remove( "flap.north", listener );
                icons.remove( "flap.auto", listener );
            }
            
            this.controller = controller;
            if( controller != null ){
                IconManager icons = controller.getIcons();
                icons.add( "flap.direction", listener );
                icons.add( "flap.south", listener );
                icons.add( "flap.east", listener );
                icons.add( "flap.west", listener );
                icons.add( "flap.north", listener );
                icons.add( "flap.auto", listener );
                setIcon( icons.getIcon( "flap.direction" ));
                north.setIcon( icons.getIcon( "flap.north" ));
                south.setIcon( icons.getIcon( "flap.south" ));
                east.setIcon( icons.getIcon( "flap.east" ));
                west.setIcon( icons.getIcon( "flap.west" ));
                center.setIcon( icons.getIcon( "flap.auto" ));
            }
        }
    }
    
    /**
     * Fires an event on all radio buttons.
     */
    private void fire(){
    	center.fireSelectedChanged();
    	north.fireSelectedChanged();
    	south.fireSelectedChanged();
    	east.fireSelectedChanged();
    	west.fireSelectedChanged();
    }
    
    /**
     * Gets the first FlapDockStation in the path from <code>dockable</code>
     * to the root of the tree.
     * @param dockable the first element to test.
     * @return the lowest FlapDockStation
     */
    private FlapDockStation getStation( Dockable dockable ){
    	if( dockable instanceof FlapDockStation )
    		return (FlapDockStation)dockable;
    	if( dockable.getDockParent() != null )
    		return getStation( dockable.getDockParent().asDockable() );
    	else
    		throw new IllegalArgumentException( "Dockable or parent is not a FlapDockStation" );
    }
    
    /**
     * A listener that can exchange the icons of this action
     * @author Benjamin Sigg
     */
    private class Listener implements IconManagerListener{
        public void iconChanged( String key, Icon icon ) {
            if( key.equals( "flap.direction" ))
                setIcon( icon );
            else if( key.equals( "flap.north" ))
                north.setIcon( icon );
            else if( key.equals( "flap.south" ))
                south.setIcon( icon );
            else if( key.equals( "flap.east" ))
                east.setIcon( icon );
            else if( key.equals( "flap.west" ))
                west.setIcon( icon );
            else if( key.equals( "flap.auto" ))
                center.setIcon( icon );
        }
    }
    
    /**
     * A button pointing in a direction.
     * @author Benjamin Sigg
     */
    private class DirectedArrow extends SimpleSelectableAction.Radio{
    	/** the direction in which this button points */
    	private Direction direction;
    	
    	/**
    	 * Creates a new button.
    	 * @param direction the direction, <code>null</code> for
    	 * automatic determination
    	 */
    	public DirectedArrow( Direction direction ){
    		this.direction = direction;
    	}
    	
    	@Override
    	public boolean isSelected( Dockable dockable ){
    		FlapDockStation station = getStation( dockable );
    		if( direction == null )
    			return station.isAutoDirection();
    		
    		return !station.isAutoDirection() && station.getDirection() == direction;
    	}
    	
    	@Override
    	public void setSelected( Dockable dockable, boolean selected ){
    		if( selected ){
    			FlapDockStation station = getStation( dockable );
    			if( direction == null )
    				station.setAutoDirection( true );
    			else{
	    			station.setAutoDirection( false );
	    			station.setDirection( direction );
    			}
    			fire();
    		}
    	}
    	
    	@Override
    	public void fireSelectedChanged(){
    		super.fireSelectedChanged();
    	}
    }
}
