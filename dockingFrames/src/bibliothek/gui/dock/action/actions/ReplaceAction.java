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

package bibliothek.gui.dock.action.actions;

import javax.swing.Icon;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;

/**
 * A {@link DockAction} that can replace a {@link DockStation} by it's only
 * child. This action is only enabled, if the associated {@link DockStation}
 * has exactly one or zero children. This action can handle only the one
 * station that was provided through the constructor.
 * @author Benjamin Sigg
 */
public class ReplaceAction extends GroupedButtonDockAction<Boolean> {
	/** A listener to the stations known to this action */
    private DockStationListener dockStationListener;
    
    /**
     * Sets up this action.
     */
    public ReplaceAction(){
        super( null );
        
        dockStationListener = new DockStationAdapter(){
            @Override
            public void dockableAdded( DockStation station, Dockable dockable ) {
            	setGroup( createGroupKey( station.asDockable() ), station.asDockable() );
            }
            @Override
            public void dockableRemoved( DockStation station, Dockable dockable ) {
            	setGroup( createGroupKey( station.asDockable() ), station.asDockable() );
            }
        };
        
        setRemoveEmptyGroups( false );
        
        setEnabled( true, true );
        setEnabled( false, false );

        Icon icon = createIcon();
        setIcon( true, icon );
        setIcon( false, icon );
        
        setText( true, DockUI.getDefaultDockUI().getString( "replace" ) );
        setText( false, DockUI.getDefaultDockUI().getString( "replace" ) );
        setTooltipText( true, DockUI.getDefaultDockUI().getString( "replace.tooltip" ));
        setTooltipText( false, DockUI.getDefaultDockUI().getString( "replace.tooltip" ));
    }
    
    @Override
    protected Boolean createGroupKey( Dockable dockable ){
    	DockStation station = dockable.asDockStation();
    	if( station == null )
    		throw new IllegalArgumentException( "Only dockables which are also a DockStation can be used for a ReplaceAction" );
    	
    	DockStation parent = dockable.getDockParent();
    	if( parent == null )
    		return false;
    	
    	int count = station.getDockableCount();
    	if( count == 0 )
    		return parent.canDrag( dockable );
    	if( count == 1 ){
    		return parent.canReplace( dockable, station.getDockable( 0 ) ) &&
            	parent.accept( station.getDockable( 0 )) &&
            	station.getDockable( 0 ).accept( parent ) &&
            	station.canDrag( station.getDockable( 0 ));
    	}
    	return false;
    }
    
    public void action( Dockable dockable ) {
        DockStation station = dockable.asDockStation();
        if( station == null )
        	throw new IllegalArgumentException( "dockable is not a station" );
        
        DockStation parent = dockable.getDockParent();
        if( parent != null ){
	        if( station.getDockableCount() == 0 ){
	            if( parent.canDrag( station.asDockable() ))
	                parent.drag( station.asDockable());
	        }
	        else{
	            if( parent.canReplace( station.asDockable(), station.getDockable( 0 ) ) &&
	                    parent.accept( station.getDockable( 0 )) &&
	                    station.getDockable( 0 ).accept( parent ) &&
	                    station.canDrag( station.getDockable( 0 ))){
	                
	                dockable = station.getDockable( 0 );
	                
	                station.drag( dockable );
	                parent.replace( station.asDockable(), dockable );
	            }
	        }
        }
    }
    
    @Override
    public void binded( Dockable dockable ) {
    	DockStation station = dockable.asDockStation();
    	if( station == null )
    		throw new IllegalArgumentException( "dockable is not a station" );

    	station.addDockStationListener( dockStationListener );
    	super.binded( dockable );
    }
    @Override
    public void unbinded( Dockable dockable ) {
    	DockStation station = dockable.asDockStation();
    	if( station == null )
    		throw new IllegalArgumentException( "dockable is not a station" );

    	station.removeDockStationListener( dockStationListener );
        super.unbinded( dockable );
    }
    
    /**
     * Creates the icon that is shown for this action.
     * @return The icon of this action
     */
    protected Icon createIcon(){
        return DockUI.getDefaultDockUI().getIcon( "replace" );
    }
}
