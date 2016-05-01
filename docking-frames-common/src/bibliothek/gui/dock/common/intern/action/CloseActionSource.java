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
package bibliothek.gui.dock.common.intern.action;

import java.util.Iterator;
import java.util.NoSuchElementException;

import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * An action source that observes one {@link CDockable} and either adds
 * or removes a close-action from itself.
 * @author Benjamin Sigg
 */
public class CloseActionSource extends AbstractDockActionSource{
    /** the element which is observed */
    private CDockable dockable;
    
    /** the access that is used to communicate with {@link CControl} */
    private CControlAccess control;
    
    /** the action that closes {@link #dockable} */
    private DockAction action;
    
    /**
     * Creates a new source
     * @param dockable the element which will be observed for changes
     */
    public CloseActionSource( CDockable dockable ){
        this.dockable = dockable;
        dockable.addCDockablePropertyListener( new CDockableAdapter(){
            @Override
            public void actionChanged( CDockable dockable, String key, CAction oldAction, CAction newAction ) {
                update();
            }
            @Override
            public void closeableChanged( CDockable dockable ) {
                update();
            }
        });
    }
    
    /**
     * Grants access to the {@link CControl} and allows to create the default
     * action.
     * @param control the access or <code>null</code>
     */
    public void setControl( CControlAccess control ){
        this.control = control;
        update();
    }
    
    /**
     * Called when the content of this source needs to be updated.
     */
    protected void update(){
        DockAction next = null;
        if( control != null ){
            if( dockable.isCloseable() ){
                CAction cnext = dockable.getAction( CDockable.ACTION_KEY_CLOSE );
                if( cnext != null )
                    next = cnext.intern();
                else
                    next = control.createCloseAction( dockable );
            }
        }
        
        if( next != action ){
            if( action != null ){
                action = null;
                fireRemoved( 0, 0 );
            }
            
            if( next != null ){
                action = next;
                fireAdded( 0, 0 );
            }
        }
    }

    public DockAction getDockAction( int index ) {
        if( index == 0 && action != null )
            return action;
        else
            throw new ArrayIndexOutOfBoundsException( index );
    }

    public int getDockActionCount() {
        if( action == null )
            return 0;
        else
            return 1;
    }

    public LocationHint getLocationHint() {
        return new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL );
    }

    public Iterator<DockAction> iterator() {
        return new Iterator<DockAction>(){
            private DockAction action = CloseActionSource.this.action;
            
            public boolean hasNext() {
                return action != null;
            }

            public DockAction next() {
                if( action == null )
                    throw new NoSuchElementException();
                
                DockAction result = action;
                action = null;
                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
