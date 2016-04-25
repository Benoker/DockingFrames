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
package bibliothek.gui.dock.control.focus;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.util.Stack;

import javax.swing.FocusManager;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Ensures that either a {@link DockTitle} or child {@link Component} of the currently focused
 * {@link Dockable} is focused. This request does nothing if the focus already is
 * correctly set, but will request the focus if not.<br>
 * The request will be executed several times, this is necessary because on some systems - like
 * Linux - Java does not handle focus requests very well.
 * @author Benjamin Sigg
 */
public class EnsuringFocusRequest implements FocusRequest{
	/** the element which is focused */
	private Dockable dockable;
	/** whether the focused {@link Component} should be a child of {@link #dockable} */
	private boolean dockableOnly;
	/** the preferred component to gain the focus */
	private Component mouseClicked;
	
	/**
	 * Creates a new request.
	 * @param dockable the element which is focused
	 * @param dockableOnly whether a child of <code>dockable</code> should gain the focus. If <code>false</code>, then also
	 * a {@link DockTitle} can gain the focus.
	 */
	public EnsuringFocusRequest( Dockable dockable, boolean dockableOnly ){
		this( dockable, dockableOnly, null );
	}
	
	/**
	 * Creates a new request.
	 * @param dockable the element which is focused
	 * @param dockableOnly whether a child of <code>dockable</code> should gain the focus. If <code>false</code>, then also
	 * a {@link DockTitle} can gain the focus.
	 * @param mouseClicked the {@link Component} the used clicked on, may be <code>null</code>
	 */
	public EnsuringFocusRequest( Dockable dockable, boolean dockableOnly, Component mouseClicked ){
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		
		this.dockable = dockable;
		this.dockableOnly = dockableOnly;
		this.mouseClicked = mouseClicked;
	}
	
	public DockElementRepresentative getSource(){
		return dockable;
	}
	
	public Component getComponent(){
		return mouseClicked;
	}
	
	public int getDelay(){
		return 0;
	}
	
	public boolean isHardRequest(){
		return false;
	}
	
	public void veto( FocusVeto veto ){
		// ignore
	}
	
	public boolean acceptable( Component component ){
		if( dockableOnly ){
			return SwingUtilities.isDescendingFrom( component, dockable.getComponent() );
		}
		else{
			return true;
		}
	}
	
	public boolean validate( FocusController controller ){
		if( controller.getFocusedDockable() != dockable ){
			return false;
		}
		
        Stack<Dockable> front = new Stack<Dockable>();            
        
        Dockable temp = dockable;
        
        while( temp != null ){
            DockStation parent = temp.getDockParent();
            if( parent != null )
                front.push( temp );
            
            temp = parent == null ? null : parent.asDockable();
        }
        
        while( !front.isEmpty() ){
            Dockable element = front.pop();
            element.getDockParent().setFrontDockable( element );
        }
    
        if( !dockableOnly ){
            DockTitle[] titles = dockable.listBoundTitles();
            Component focused = FocusManager.getCurrentManager().getFocusOwner();
            if( focused != null ){
                if( SwingUtilities.isDescendingFrom( focused, dockable.getComponent() ) ){
                    return false;
                }
                
                for( DockTitle title : titles ){
                    if( SwingUtilities.isDescendingFrom( focused, title.getComponent() )){
                        return false;
                    }
                }
            }
        }
        return true;
	}
	
	public FocusRequest grant( Component component ){
        if( component.isFocusable() ){
            component.requestFocus();
            component.requestFocusInWindow();
            return new RepeatingFocusRequest( dockable, component, isHardRequest() );
        }
        else if( mouseClicked == null || mouseClicked == dockable.getComponent() ){
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent( component );
        }
        else{
        	mouseClicked.requestFocus();
        	mouseClicked.requestFocusInWindow();
        	return new RepeatingFocusRequest( dockable, mouseClicked, isHardRequest() );
        }
        return null;
	}
}
