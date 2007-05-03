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

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SimpleMenuAction;
import bibliothek.gui.dock.action.actions.SimpleSelectableAction;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.FlapDockStation.Direction;

/**
 * This {@link DockAction} is used as an action of a {@link FlapDockStation}.
 * The action itself changes the direction in which a <code>FlapDockStation</code>
 * opens. This is done by calling the {@link FlapDockStation#setAutoDirection(boolean) setAutoDirection}
 * and the {@link FlapDockStation#setDirection(Direction)}-methods.  
 * @author Benjamin Sigg
 */
public class FlapDockDirection extends SimpleMenuAction {
    private FlapDockStation station;
    
    private DirectedArrow north, south, east, west, center;

    /**
     * Creates the icon of the action, and sets the text and tooltip of the action.
     */
    public FlapDockDirection(){
        Icon icon = createIcon();
        setIcon( icon );
        setText( DockUI.getDefaultDockUI().getString( "flap.direction" ) );
        setTooltipText( DockUI.getDefaultDockUI().getString( "flap.direction.tooltip" ) );
        
        center = new DirectedArrow( null );
        north = new DirectedArrow( Direction.NORTH );
        south = new DirectedArrow( Direction.SOUTH );
        east = new DirectedArrow( Direction.EAST );
        west = new DirectedArrow( Direction.WEST );
        
        north.setIcon( createSideIcon( Direction.NORTH ));
        south.setIcon( createSideIcon( Direction.SOUTH ));
        east.setIcon( createSideIcon( Direction.EAST ));
        west.setIcon( createSideIcon( Direction.WEST ));
        center.setIcon( createCenterIcon() );
     
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
    }
     
    /**
     * Creates the icon that is shown as the icon of the action.
     * @return The icon of the action
     */
    protected Icon createIcon(){
        return DockUI.getDefaultDockUI().getIcon( "flap.direction" );
    }
    
    /**
     * Creates an icon that represents a direction.
     * @param direction The direction in which the icon should point
     * @return The icon
     */
    protected Icon createSideIcon( Direction direction ){
        return DockUI.getDefaultDockUI().getIcon( "flap." + direction.name().toLowerCase() );
    }
    
    /**
     * Creates an icon that represents the automatic direction.
     * @return The center icon
     */
    protected Icon createCenterIcon(){
        return DockUI.getDefaultDockUI().getIcon( "flap.auto" );
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
    	protected void fireSelectedChanged(){
    		super.fireSelectedChanged();
    	}
    }
}
