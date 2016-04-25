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

package bibliothek.gui.dock.facile.action;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionText;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.actions.GroupedButtonDockAction;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.util.ClientOnly;

/**
 * A {@link DockAction} that can replace a {@link DockStation} by it's only
 * child. This action is only enabled, if the associated {@link DockStation}
 * has exactly one or zero children. This action can handle only the one
 * station that was provided through the constructor.
 * @author Benjamin Sigg
 */
@ClientOnly
public class ReplaceAction extends GroupedButtonDockAction<Boolean> implements ListeningDockAction{
	/** the key uses for the {@link bibliothek.gui.dock.util.IconManager} to get the {@link Icon} of this action */
	public static final String KEY_ICON = "replace";
	
	/** A listener to the stations known to this action */
    private DockStationListener dockStationListener;
    
    private DockActionText text;
    
    private DockActionText tooltip;
    
    private DockActionIcon icon;
    
    /**
     * Sets up this action.
     * @param controller The controller for which this action is used. This
     * action will add some listeners to the controller. To remove those
     * listeners, call {@link #setController(DockController)} with a
     * <code>null</code> argument.
     */
    public ReplaceAction( DockController controller ){
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
        
        text = new DockActionText( "replace", this ){
			protected void changed( String oldValue, String newValue ){
				setText( true, newValue );
		        setText( false, newValue );			
			}
		};
        
        tooltip = new DockActionText( "replace.tooltip", this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( true, newValue );
				setTooltip( false, newValue );
			}
		};
        
        icon = new DockActionIcon( "replace", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( true, newValue );
				setIcon( false, newValue );
			}
		};
        
        setController( controller );
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
    public void bound( Dockable dockable ) {
    	DockStation station = dockable.asDockStation();
    	if( station == null )
    		throw new IllegalArgumentException( "dockable is not a station" );

    	station.addDockStationListener( dockStationListener );
    	super.bound( dockable );
    }
    @Override
    public void unbound( Dockable dockable ) {
    	DockStation station = dockable.asDockStation();
    	if( station == null )
    		throw new IllegalArgumentException( "dockable is not a station" );

    	station.removeDockStationListener( dockStationListener );
        super.unbound( dockable );
    }
    
    public void setController( DockController controller ) {
        icon.setController( controller );
        text.setController( controller );
        tooltip.setController( controller );
    }
}
