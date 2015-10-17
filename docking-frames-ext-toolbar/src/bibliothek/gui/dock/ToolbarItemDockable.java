/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.component.DockComponentRootHandler;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.dockable.DockableBackgroundComponent;
import bibliothek.gui.dock.dockable.DockableIcon;
import bibliothek.gui.dock.station.OrientationObserver;
import bibliothek.gui.dock.station.toolbar.ToolbarItemDockableFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.toolbar.expand.ExpandableStateController;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItem;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.item.ComponentItem;
import bibliothek.gui.dock.toolbar.item.DockActionItem;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPanel;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.icon.DockIcon;

/**
 * A {@link ToolbarItemDockable} is a {@link Dockable} that can be shown as child of a {@link ToolbarDockStation}. This
 * class acts as wrapper around a {@link ToolbarItem} which can just be any kind of {@link Component}.<br>
 * This class supports {@link ExpandableToolbarItem}, clients can call {@link #setItem(ToolbarItem, ExpandedState)} to 
 * fill up the different positions. 
 * @author Benjamin Sigg
 */
public class ToolbarItemDockable extends AbstractDockable implements ExpandableToolbarItem {
	/** the component */
	private BackgroundPanel content;

	/** the layout of {@link #content} */
	private CardLayout contentLayout;

	/** all the {@link ExpandableToolbarItemListener}s */
	private final List<ExpandableToolbarItemListener> expandableListeners = new ArrayList<ExpandableToolbarItemListener>();

	/** the current state of this {@link ExpandableToolbarItem} */
	private ExpandedState state = ExpandedState.SHRUNK;

	/** the {@link Component}s to show in different states */
	private final ToolbarItem[] items = new ToolbarItem[ExpandedState.values().length];

	/** all registered {@link MouseInputListener}s */
	private final List<MouseInputListener> mouseListeners = new ArrayList<MouseInputListener>();

	/** the current orientation of the toolbar */
	private Orientation orientation = Orientation.HORIZONTAL;
	
	/** the background of this dockable */
	private Background background = new Background();
	
	/**
	 * Creates a new dockable
	 */
	public ToolbarItemDockable(){
		this( (ToolbarItem)null, null, null );
	}

	/**
	 * Creates a new dockable
	 * @param icon the icon of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( Icon icon ){
		this( (ToolbarItem)null, null, icon );
	}

	/**
	 * Creates a new dockable
	 * @param title the title of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( String title ){
		this( (ToolbarItem)null, title, null );
	}

	/**
	 * Creates a new dockable
	 * @param action the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 */
	public ToolbarItemDockable( DockAction action ){
		this( action, null, null );
	}
	
	/**
	 * Creates a new dockable
	 * @param component the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 */
	public ToolbarItemDockable( Component component ){
		this( component, null, null );
	}
	
	/**
	 * Creates a new dockable
	 * @param item the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 */
	public ToolbarItemDockable( ToolbarItem item ){
		this( item, null, null );
	}
	
	/**
	 * Creates a new dockable
	 * @param action the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param icon the icon of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( DockAction action, Icon icon ){
		this( action, null, icon );
	}
	
	/**
	 * Creates a new dockable
	 * @param component the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param icon the icon of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( Component component, Icon icon ){
		this( component, null, icon );
	}
	
	/**
	 * Creates a new dockable
	 * @param item the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param icon the icon of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( ToolbarItem item, Icon icon ){
		this( item, null, icon );
	}

	/**
	 * Creates a new dockable
	 * @param action the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param title the title of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( DockAction action, String title ){
		this( action, title, null );
	}
	
	/**
	 * Creates a new dockable
	 * @param component the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param title the title of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( Component component, String title ){
		this( component, title, null );
	}
	
	/**
	 * Creates a new dockable
	 * @param item the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param title the title of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( ToolbarItem item, String title ){
		this( item, title, null );
	}

	/**
	 * Creates a new dockable
	 * @param action the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param title the title of this dockable, can be <code>null</code>
	 * @param icon the icon of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( DockAction action, String title, Icon icon ){
		super( PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP );
		init( title, icon );
		setAction( action, ExpandedState.SHRUNK );
	}
	
	/**
	 * Creates a new dockable
	 * @param component the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param title the title of this dockable, can be <code>null</code>
	 * @param icon the icon of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( Component component, String title, Icon icon ){
		super( PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP );
		init( title, icon );
		setComponent( component, ExpandedState.SHRUNK );
	}
	
	/**
	 * Creates a new dockable
	 * @param item the item to show in the {@link ExpandedState#SHRUNK}, can be <code>null</code>
	 * @param title the title of this dockable, can be <code>null</code>
	 * @param icon the icon of this dockable, can be <code>null</code>
	 */
	public ToolbarItemDockable( ToolbarItem item, String title, Icon icon ){
		super( PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP );
		init( title, icon );
		setItem( item, ExpandedState.SHRUNK );
	}
	
	private void init( String title, Icon icon ){
		contentLayout = new CardLayout(){
			@Override
			public Dimension preferredLayoutSize( Container parent ){
				synchronized( parent.getTreeLock() ) {
					ToolbarItem current = getNearestComponent( state );
					if( current == null ) {
						return new Dimension( 10, 10 );
					}
					return current.getComponent().getPreferredSize();
				}
			}

			@Override
			public Dimension minimumLayoutSize( Container parent ){
				synchronized( parent.getTreeLock() ) {
					ToolbarItem current = getNearestComponent( state );
					if( current == null ) {
						return new Dimension( 10, 10 );
					}
					return current.getComponent().getMinimumSize();
				}
			}

			@Override
			public Dimension maximumLayoutSize( Container parent ){
				synchronized( parent.getTreeLock() ) {
					ToolbarItem current = getNearestComponent( state );
					if( current == null ) {
						return new Dimension( 10, 10 );
					}
					return current.getComponent().getMaximumSize();
				}
			}
		};

		content = new ConfiguredBackgroundPanel( contentLayout, Transparency.SOLID );
		content.setFocusable( false );
		content.setFocusTraversalPolicyProvider( true );
    	content.setFocusTraversalPolicy( new LayoutFocusTraversalPolicy() );
    	content.setBackground( background );

		new ExpandableStateController( this );

		new OrientationObserver( this ){
			@Override
			protected void orientationChanged( Orientation current ){
				orientation = current;
				for( ToolbarItem item : items ){
					if( item != null ){
						item.setOrientation( current );
					}
				}
			}
		};

		setTitleIcon( icon );
		setTitleText( title );
	}

	@Override
	protected DockComponentRootHandler createRootHandler() {
		return new DockComponentRootHandler( this ){
			@Override
			protected TraverseResult shouldTraverse( Component component ) {
				if( component == content ){
					return TraverseResult.EXCLUDE_CHILDREN;
				}
				return TraverseResult.INCLUDE_CHILDREN;
			}
		};
	}
	
	/**
	 * Gets the component associated with the nearest {@link ExpandedState} with
	 * regards to the <code>state</code> parameter. If two states are equally
	 * close, the state with minor ordinal value is returned.
	 * 
	 * @param state
	 *            the state
	 * @return the component in the nearest state.
	 */
	private ToolbarItem getNearestComponent( ExpandedState state ){
		if( getController() == null ) {
			return null;
		}

		int index = state.ordinal();
		while( index >= 0 ) {
			if( items[index] != null ) {
				return items[index];
			}
			index--;
		}

		index = state.ordinal() + 1;
		while( index < items.length ) {
			if( items[index] != null ) {
				return items[index];
			}
			index++;
		}
		return null;
	}

	/**
	 * Gets the nearest value of {@link ExpandedState} with regards to the
	 * <code>state</code> parameter.
	 * @param state the state
	 * @return the nearest state
	 */
	private ExpandedState getNearestState( ExpandedState state ){
		ToolbarItem nearest = getNearestComponent( state );
		if( nearest == null ) {
			return null;
		}
		for( final ExpandedState next : ExpandedState.values() ) {
			if( items[next.ordinal()] == nearest ) {
				return next;
			}
		}
		return null;
	}

	@Override
	public void addMouseInputListener( MouseInputListener listener ){
		super.addMouseInputListener( listener );
		mouseListeners.add( listener );

		ToolbarItem item = getCurrentItem();
		if( item != null ) {
			item.addMouseInputListener( listener );
		}
	}

	@Override
	public void removeMouseInputListener( MouseInputListener listener ){
		super.removeMouseInputListener( listener );
		mouseListeners.remove( listener );

		ToolbarItem item = getCurrentItem();
		if( item != null ) {
			item.removeMouseInputListener( listener );
		}
	}

	/**
	 * Sets the {@link DockAction} which should be shown if in state <code>state</code>.
	 * Please note that the same {@link DockAction} cannot be used for more than one state.
	 * 
	 * @param action the item to set
	 * @param state the state in which to show <code>action</code>
	 */	
	public void setAction( DockAction action, ExpandedState state ){
		if( action == null ){
			setItem( null, state );
		}
		else{
			setItem( new DockActionItem( action ), state );
		}
	}

	/**
	 * Sets the {@link Component} which should be shown if in state <code>state</code>.
	 * Please note that the same {@link Component} cannot be used for more than one state.
	 * 
	 * @param component the item to set
	 * @param state the state in which to show <code>component</code>
	 */
	public void setComponent( Component component, ExpandedState state ){
		if( component == null ){
			setItem( null, state );
		}
		else{
			setItem( new ComponentItem( component ), state );
		}
	}
	
	/**
	 * Sets the {@link ToolbarItem} which should be shown if in state
	 * <code>state</code>. Please note that the same {@link ToolbarItem} cannot be
	 * used for more than one state.
	 * 
	 * @param item the item to set
	 * @param state the state in which to show <code>item</code>
	 */
	public void setItem( ToolbarItem item, ExpandedState state ){
		if( item != null ){
			item.setOrientation( orientation );
		}
		
		ToolbarItem previous = items[state.ordinal()];
		boolean enabled = isEnabled( state );
		
		if( previous != item ){
			if( item != null ){
				item.setDockable( this );
			}
			
			if( getController() == null ) {
				items[state.ordinal()] = item;
			}
			else {
				ToolbarItem current = getCurrentItem();
				if( current != null && current == previous ) {
					current.setSelected( false );
					for( MouseInputListener listener : mouseListeners ) {
						current.removeMouseInputListener( listener );
					}
				}

				if( previous != null ) {
					content.remove( previous.getComponent() );
					previous.unbind();
					previous.setController( null );
				}

				items[state.ordinal()] = item;
				if( item != null ) {
					item.setController( getController() );
					item.bind();
					content.add( item.getComponent(), state.toString() );
					current = getCurrentItem();
					if( current != null && current == item ) {
						current.setSelected( true );
						for( MouseInputListener listener : mouseListeners ) {
							current.addMouseInputListener( listener );
						}
					}
					else{
						item.setSelected( false );
					}
				}

				ExpandedState nearest = getNearestState( this.state );
				if( nearest != null ) {
					contentLayout.show( content, nearest.toString() );
					content.revalidate();
				}
			}
			if( previous != null ){
				previous.setDockable( null );
			}
		}
		
		boolean newEnabled = isEnabled( state );
		if( newEnabled != enabled ){
			for( ExpandableToolbarItemListener listener : expandableListeners.toArray( new ExpandableToolbarItemListener[expandableListeners.size()] ) ) {
				listener.enablementChanged( this, state, newEnabled );
			}
		}
	}

	@Override
	public void setController( DockController controller ){
		if( getController() != controller ) {
			if( getController() != null ) {
				ToolbarItem current = getCurrentItem();
				if( current != null ) {
					for( MouseInputListener listener : mouseListeners ) {
						current.removeMouseInputListener( listener );
					}
				}

				for( ToolbarItem item : items ) {
					if( item != null ) {
						content.remove( item.getComponent() );
						item.unbind();
						item.setController( null );
					}
				}
			}

			super.setController( controller );
			background.setController( controller );

			if( controller != null ) {
				for( ExpandedState state : ExpandedState.values() ) {
					ToolbarItem item = items[state.ordinal()];
					if( item != null ) {
						item.setController( controller );
						item.bind();
						content.add( item.getComponent(), state.toString() );
					}
				}

				ToolbarItem current = getCurrentItem();
				if( current != null ) {
					for( MouseInputListener listener : mouseListeners ) {
						current.addMouseInputListener( listener );
					}
				}
			}
			
			forceState( state );
		}
	}

	@Override
	public void setExpandedState( ExpandedState state ){
		if( this.state != state ) {
			forceState( state );
		}
	}

	private void forceState( ExpandedState state ){
		ExpandedState oldState = this.state;
		ToolbarItem oldItem = getCurrentItem();
		this.state = state;
		ToolbarItem newItem = getCurrentItem();

		if( oldItem != newItem ) {
			if( oldItem != null ) {
				oldItem.setSelected( false );
				for( MouseInputListener listener : mouseListeners ) {
					oldItem.removeMouseInputListener( listener );
				}
			}
			if( newItem != null ) {
				newItem.setSelected( true );
				for( MouseInputListener listener : mouseListeners ) {
					newItem.addMouseInputListener( listener );
				}
			}
		}

		ExpandedState nearest = getNearestState( state );
		if( nearest != null ) {
			contentLayout.show( content, nearest.toString() );
		}
		content.revalidate();
		if( oldState != state ){
			for( ExpandableToolbarItemListener listener : expandableListeners.toArray( new ExpandableToolbarItemListener[expandableListeners.size()] ) ) {
				listener.changed( this, oldState, state );
			}
		}
	}

	/**
	 * Gets the {@link ComponentItem} that is currently shown on this dockable.
	 * @return the item that is currently shown, can be <code>null</code>
	 */
	private ToolbarItem getCurrentItem(){
		return getNearestComponent( state );
	}

	@Override
	public boolean isEnabled( ExpandedState state ){
		return items[ state.ordinal() ] != null;
	}
	
	@Override
	public ExpandedState getExpandedState(){
		return state;
	}
	
	/**
	 * Gets the latest known orientation of this dockable
	 * @return the orientation, or <code>null</code> if unknown
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public Component getComponent(){
		return content;
	}

	@Override
	public void addExpandableListener( ExpandableToolbarItemListener listener ){
		if( listener == null ) {
			throw new IllegalArgumentException( "listener must not be null" );
		}
		expandableListeners.add( listener );
	}

	@Override
	public void removeExpandableListener( ExpandableToolbarItemListener listener ){
		expandableListeners.remove( listener );
	}

	@Override
	public DockStation asDockStation(){
		return null;
	}

	@Override
	public String getFactoryID(){
		return ToolbarItemDockableFactory.ID;
	}

	@Override
	protected DockIcon createTitleIcon(){
		return new DockableIcon( "dockable.default", this ){
			@Override
			protected void changed( Icon oldValue, Icon newValue ){
				fireTitleIconChanged( oldValue, newValue );
			}
		};
	}

	@Override
	public boolean accept( DockStation station ){
		// as this method is called during drag&drop operations a DockController
		// is available
		final SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>( ToolbarStrategy.STRATEGY, getController() );
		final ToolbarStrategy strategy = value.getValue();
		value.setProperties( (DockController) null );

		return strategy.isToolbarGroupPartParent( station, this, false );
	}

	@Override
	public boolean accept( DockStation base, Dockable neighbour ){
		return false;
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@' + Integer.toHexString( hashCode() );
	}
	
    /**
     * A representation of the background of this {@link Dockable}.
     * @author Benjamin Sigg
     */
	private class Background extends BackgroundAlgorithm implements DockableBackgroundComponent{
		public Background(){
			super( DockableBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".dockable.toolbar" );
		}
		
		@Override
		public Component getComponent(){
			return getDockable().getComponent(); 
		}
		
		@Override
		public Dockable getDockable(){
			return ToolbarItemDockable.this;
		}
	}
}
