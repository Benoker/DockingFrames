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
package bibliothek.gui.dock.station.split;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionText;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.actions.GroupedButtonDockAction;
import bibliothek.gui.dock.event.SplitDockListener;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This {@link DockAction} is mainly used by the {@link SplitDockStation}
 * to allow it's children to get in fullscreen-mode.
 * @author Benjamin Sigg
 */
public class SplitFullScreenAction extends GroupedButtonDockAction<Boolean> implements ListeningDockAction {
    private SplitDockStation split;
    private DockController controller;
    
    private DockActionIcon iconNormalize;
    private DockActionIcon iconMaximize;
    
    private DockActionText textNormalize;
    private DockActionText textMaximize;
    private DockActionText textNormalizeTooltip;
    private DockActionText textMaximizeTooltip;

    private PropertyValue<KeyStroke> accelerator = new PropertyValue<KeyStroke>( SplitDockStation.MAXIMIZE_ACCELERATOR ){
    	@Override
    	protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ){
    		setAccelerator( Boolean.TRUE, newValue );
    		setAccelerator( Boolean.FALSE, newValue );
    	}
    };
    
    /**
     * Constructs the action and sets the <code>station</code> on
     * which the {@link Dockable Dockables} will be made fullscreen.
     * @param station the station
     */
    public SplitFullScreenAction( SplitDockStation station ){
        super( null );
        
        split = station;
        setRemoveEmptyGroups( false );
        
        station.addSplitDockStationListener( new SplitDockListener(){
            public void fullScreenDockableChanged( SplitDockStation station, Dockable oldFullScreen, Dockable newFullScreen ) {
                if( oldFullScreen != null ){
                    change( oldFullScreen, Boolean.FALSE );
                }
                
                if( newFullScreen != null ){
                    change( newFullScreen, Boolean.TRUE );
                }
            }
        });
        
        textNormalize = new DockActionText( "split.normalize", this ){
			protected void changed( String oldValue, String newValue ){
				setText( Boolean.TRUE, newValue );	
			}
		};
		textMaximize = new DockActionText( "split.maximize", this ){
			protected void changed( String oldValue, String newValue ){
				setText( Boolean.FALSE, newValue );	
			}
		};
        
        textNormalizeTooltip = new DockActionText( "split.normalize.tooltip", this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.TRUE, newValue );	
			}
		};
		
		textMaximizeTooltip = new DockActionText( "split.maximize.tooltip", this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.FALSE, newValue );
			}
		};
        
        iconNormalize = new DockActionIcon( "split.normalize", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( Boolean.TRUE, newValue );	
			}
		};
		iconMaximize = new DockActionIcon( "split.maximize", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( Boolean.FALSE, newValue );	
			}
		};
    }
    
    public void setController( DockController controller ) {
        if( this.controller != controller ){
            this.controller = controller;
            accelerator.setProperties( controller );
            
            if( controller == null ){
            	iconNormalize.setManager( null );
            	iconMaximize.setManager( null );
            	
                textNormalize.setManager( null );
                textMaximize.setManager( null );
                textNormalizeTooltip.setManager( null );
                textMaximizeTooltip.setManager( null );
            }
            else{
            	iconNormalize.setManager( controller.getIcons() );
            	iconMaximize.setManager( controller.getIcons() );
            	
            	textNormalize.setManager( controller.getTexts() );
                textMaximize.setManager( controller.getTexts() );
                textNormalizeTooltip.setManager( controller.getTexts() );
                textMaximizeTooltip.setManager( controller.getTexts() );
            }
        }
    }
    
    public void action( Dockable dockable ) {
        while( dockable.getDockParent() != split ){
            DockStation station = dockable.getDockParent();
            if( station == null )
                return;
            
            dockable = station.asDockable();
            if( dockable == null )
                return;
        }
        
        Dockable fullscreen = split.getFullScreen();
        
        if( fullscreen == dockable )
            split.setFullScreen( null );
        else
            split.setFullScreen( dockable );
    }
    
    private void change( Dockable dockable, Boolean value ){
        if( isKnown( dockable ))
            setGroup( value, dockable );
        
        DockStation station = dockable.asDockStation();
        if( station != null ){
            for( int i = 0, n = station.getDockableCount(); i<n; i++ )
                change( station.getDockable(i), value );
        }
    }
    
    @Override
    protected Boolean createGroupKey( Dockable dockable ){
        while( dockable.getDockParent() != split ){
            DockStation station = dockable.getDockParent();
            if( station == null )
                return Boolean.FALSE;
            
            dockable = station.asDockable();
            if( dockable == null )
                return Boolean.FALSE;
        }
        
        if( dockable == split.getFullScreen() )
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }
}
