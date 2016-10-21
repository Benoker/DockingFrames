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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Component;
import java.awt.Dimension;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;

/**
 * Abstract implementation of {@link Tab} based on a real {@link Component}. Clients
 * should call {@link #bind()}, {@link #unbind()} and {@link #setController(DockController)} to
 * fully utilize this class.
 * @author Benjamin Sigg
 */
public abstract class AbstractTab extends AbstractTabPaneComponent implements Tab{
	private Dockable dockable;
	
	/** whether {@link #bind()} was called and {@link #unbind()} was not yet called */
	private boolean bound = false;
	
	/** the controller in whose realm this tab works */
	private DockController controller;
	
	/** whether this tab is currently selected */
	private boolean selected = false;
	
	/** whether this tab is currently focused */
	private boolean focused = false;
	
	/**
	 * Observes the owner of this tab and changes the selection state
	 * of this tab if the owners selection changes.
	 */
	private TabPaneListener selectionListener = new TabPaneListener(){
		public void added( TabPane pane, Dockable dockable ){
			// ignore
		}
		public void removed( TabPane pane, Dockable dockable ){
			// ignore
		}
		public void selectionChanged( TabPane pane ){
			boolean newSelected = pane.getSelectedDockable() == getDockable();
			if( newSelected != selected ){
				selected = newSelected;
				informSelectionChanged( selected );
			}
		}
		
		public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo ){
			// ignore
		}
		public void controllerChanged( TabPane pane, DockController controller ){
			// ignore
		}
	};
	
	/**
	 * Observers the {@link DockController} in order to find out whether this
	 * tab is currently focused or not.
	 */
	private DockableFocusListener focusListener = new DockableFocusListener(){
		public void dockableFocused( DockableFocusEvent event ){
			boolean newFocused = event.getNewFocusOwner() == getDockable();
			if( newFocused != focused ){
				focused = newFocused;
				informFocusChanged( focused );
			}
		}
	};
	
	/**
	 * Creates a new abstract tab.
	 * @param parent the owner of this tab
	 * @param dockable the element to show, not <code>null</code>
	 */
	public AbstractTab( TabPane parent, Dockable dockable ){
		super( parent );
		
		if( dockable == null )
			throw new IllegalArgumentException( "dockable must not be null" );
		
		this.dockable = dockable;
	}
	
	public Dockable getDockable(){
		return dockable;
	}
	
	public Dimension getMinimumSize( Tab[] tabs ){
		return getMinimumSize();
	}
	
	public Dimension getPreferredSize( Tab[] tabs ){
		return getPreferredSize();
	}
	
	/**
	 * Connects this tab with its parent and the {@link DockController}.
	 * @throws IllegalStateException if this method has already been invoked
	 */
	public void bind(){
		if( bound )
			throw new IllegalStateException( "this tab is already bound" );
		bound = true;
		
		if( controller != null ){
			controller.addDockableFocusListener( focusListener );
			focused = controller.getFocusedDockable() == getDockable();
			informFocusChanged( focused );
		}
		
		TabPane parent = getTabParent();
		parent.addTabPaneListener( selectionListener );
		selected = parent.getSelectedDockable() == getDockable();
		informSelectionChanged( selected );
	}
	
	/**
	 * Disconnects this tab from its parent and from the {@link DockController}.
	 * @throws IllegalStateException if this method has already been invoked.
	 */
	public void unbind(){
		if( !bound )
			throw new IllegalStateException( "this tab is not bound" );
		bound = false;
		
		if( controller != null ){
			controller.removeDockableFocusListener( focusListener );
		}
		
		getTabParent().removeTabPaneListener( selectionListener );
	}
	
	/**
	 * Tells whether this tab is selected. This property is updated a {@link TabPaneListener}
	 * and might not be the correct value while a selection changes.
	 * @return whether this tab is selected
	 */
	public boolean isSelected(){
		return selected;
	}
	
	/**
	 * Called when the selection state of this tab has been changed. Subclasses
	 * may choose to ignore the event.
	 * @param selected the new selection state
	 */
	protected abstract void informSelectionChanged( boolean selected );
	
	/**
	 * Tells whether this tab is focused. A tab is focused if its
	 * {@link Dockable} is {@link DockController#getFocusedDockable() focused}.
	 * This property is updated by a {@link DockableFocusListener}, hence it
	 * might not be accurate while the focus changes.
	 * @return whether this tab is focused
	 */
	public boolean isFocused(){
		return focused;
	}
	
	/**
	 * Called when the focus state of this tab has been changed. Subclasses
	 * may choose to ignore the event.
	 * @param focused the new focus state
	 */
	protected abstract void informFocusChanged( boolean focused );
	
	/**
	 * Sets the controller in whose realm this {@link AbstractTab} works.
	 * @param controller the controller
	 */
	public void setController( DockController controller ){
		if( bound ){
			if( this.controller != null ){
				this.controller.removeDockableFocusListener( focusListener );
			}
			
			this.controller = controller;	
			
			if( this.controller != null ){
				this.controller.addDockableFocusListener( focusListener );
				focused = this.controller.getFocusedDockable() == getDockable();
				informFocusChanged( focused );
			}
		}
		else{
			this.controller = controller;
		}
	}
}
