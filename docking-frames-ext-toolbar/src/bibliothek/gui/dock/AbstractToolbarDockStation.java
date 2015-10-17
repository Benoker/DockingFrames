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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OrientationObserver;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OrientingDockStationEvent;
import bibliothek.gui.dock.station.OrientingDockStationListener;
import bibliothek.gui.dock.station.StationBackgroundComponent;
import bibliothek.gui.dock.station.StationDragOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.ToolbarTabDockStation;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItem;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemListener;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemStrategyListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.FrameworkOnly;

/**
 * Base class of a {@link DockStation} behaving like a typical toolbar: the
 * children are ordered in a list, an optional title and border may be shown.
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public abstract class AbstractToolbarDockStation extends
		AbstractDockableStation implements OrientedDockStation, ExpandableToolbarItem{
	
	/**
	 * If it is not clear whether an {@link ExpandedState} is enabled, because the involved {@link Dockable}s offer different
	 * enabled-states, then the value of this <code>boolean</code> is the result of the operation.
	 */
	public static final PropertyKey<Boolean> ON_CONFLICT_ENABLE = new PropertyKey<Boolean>( "ExpandableToolbarGroupActions.on_conflict_enable", 
			new ConstantPropertyFactory<Boolean>( Boolean.TRUE ), true );

	/**
	 * a helper class ensuring that all properties of the
	 * {@link DockableDisplayer}s are set correctly
	 */
	protected DisplayerCollection displayers;
	/**
	 * a factory used by {@link #displayers} to create new
	 * {@link DockableDisplayer}s
	 */
	protected DefaultDisplayerFactoryValue displayerFactory;
	/** a factory creating new {@link DockTitle}s */
	protected DockTitleVersion title;
	/** A paint to draw lines */
	protected DefaultStationPaintValue paint;

	/** Alignment of the content of this station */
	protected Orientation orientation = Orientation.HORIZONTAL;

	/** all registered {@link OrientingDockStationListener}s. */
	private final List<OrientingDockStationListener> orientingListeners = new ArrayList<OrientingDockStationListener>();

	/** all registered {@link ExpandableToolbarItemListener}s */
	private final List<ExpandableToolbarItemListener> expandableListeners = new ArrayList<ExpandableToolbarItemListener>();
	/** the current behavior of this station */
	private ExpandedState state = ExpandedState.SHRUNK;

	/** added to the current {@link #expandableStategy} */
	private ExpandableListener expandableListener = new ExpandableListener();
	
	/** the current strategy to handle {@link ExpandableToolbarItem}s  */ 
	private PropertyValue<ExpandableToolbarItemStrategy> expandableStategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			if( oldValue != null ){
				oldValue.removeExpandedListener( expandableListener );
			}
			if( newValue != null ){
				newValue.addExpandedListener( expandableListener );
			}
			fireEnablementChanged();
		}
	};
	
	/** tells what happens when there are conflicts in the enabled state of {@link ExpandedState} */
	private PropertyValue<Boolean> onConflictEnable = new PropertyValue<Boolean>( ON_CONFLICT_ENABLE ){
		@Override
		protected void valueChanged( Boolean oldValue, Boolean newValue ){
			fireEnablementChanged();
		}
	};


	private boolean[] expandedEnablementStateCache = new boolean[ ExpandedState.values().length ];
	
	/** the Dockable that is currently removed */
	private Dockable removal;
	
	private Background background;
	
	// ########################################################
	// ############ Initialization Managing ###################
	// ########################################################

	/**
	 * Constructs a new ToolbarDockStation. Subclasses must call {@link #init()}
	 * once the constructor has been executed.
	 */
	public AbstractToolbarDockStation(){
		new OrientationObserver( this ){
			@Override
			protected void orientationChanged( Orientation current ){
				if( current != null ){
					setOrientation( current );
				}
			}
		};
		addDockStationListener( new DockStationAdapter(){
			@Override
			public void dockableAdded( DockStation station, Dockable dockable ){
				fireEnablementChanged();
			}
			
			@Override
			public void dockableRemoved( DockStation station, Dockable dockable ){
				fireEnablementChanged();
			}
		});
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		expandableStategy.setProperties( controller );
		onConflictEnable.setProperties( controller );
		background.setController( controller );
	}

	/**
	 * Initializes the properties that depend on the subclasses
	 * @param backgroundId the identifier used for registering a {@link BackgroundComponent}
	 */
	protected void init( String backgroundId ){
		background = new Background( backgroundId );
	}
	
	/**
	 * Creates a new {@link JPanel} which uses the {@link #getBackgroundAlgorithm() background algorithm} to
	 * paint its content.
	 * @return the new panel
	 */
	protected JPanel createBackgroundPanel(){
		ConfiguredBackgroundPanel panel = new ConfiguredBackgroundPanel( Transparency.DEFAULT );
		panel.setBackground( background );
		return panel;
	}
	
	/**
	 * Gets the algorithm which should be used to paint this station.
	 * @return the background algorithm, <code>null</code> until {@link #init(String)} was called
	 */
	protected BackgroundAlgorithm getBackgroundAlgorithm(){
		return background;
	}

	// ########################################################
	// ############ General DockStation Managing ##############
	// ########################################################

	@Override
	public Dockable getFrontDockable(){
		// there's no child which is more important than another
		return null;
	}

	@Override
	public void setFrontDockable( Dockable dockable ){
		// there's no child which is more important than another
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(hashCode());
	}

	// ########################################################
	// ################### Class Utilities ####################
	// ########################################################

	/**
	 * Gets the location of <code>dockable</code> in the component-panel.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the location or -1 if the child was not found
	 */
	public int indexOf( Dockable dockable ){
		for (int i = 0; i < getDockableCount(); i++){
			if (getDockable(i) == dockable){
				return i;
			}
		}
		return -1;
	}

	// ########################################################
	// ############ Orientation Managing ######################
	// ########################################################

	@Override
	public Orientation getOrientation(){
		return orientation;
	}

	@Override
	public void addOrientingDockStationListener(
			OrientingDockStationListener listener ){
		orientingListeners.add(listener);
	}

	@Override
	public void removeOrientingDockStationListener(
			OrientingDockStationListener listener ){
		orientingListeners.remove(listener);
	}

	@Override
	public Orientation getOrientationOf( Dockable child ){
		return getOrientation();
	}

	/**
	 * Fires an {@link OrientingDockStationEvent}.
	 */
	protected void fireOrientingEvent(){
		final OrientingDockStationEvent event = new OrientingDockStationEvent(
				this);
		for (final OrientingDockStationListener listener : orientingListeners
				.toArray(new OrientingDockStationListener[orientingListeners
						.size()])){
			listener.changed(event);
		}
	}

	// ########################################################
	// ############ Expanded State Managing ###################
	// ########################################################

	@Override
	public ExpandedState getExpandedState(){
		return state;
	}

	/**
	 * Sets the {@link ExpandedState} of this station.
	 * 
	 * @param state
	 *            the new state, not <code>null</code>
	 * @param action
	 *            if <code>true</code>, then
	 *            {@link #setExpandedState(ExpandedState)} is called. Otherwise
	 *            the property is changed without actually performing any
	 *            actions. The later option should only be used while loading a
	 *            layout.
	 */
	@FrameworkOnly
	public void setExpandedState( ExpandedState state, boolean action ){
		if (action){
			setExpandedState(state);
		} else{
			this.state = state;
		}
	}

	@Override
	public void setExpandedState( ExpandedState state ){
		if (this.state != state){
			final DockController controller = getController();
			if (controller != null){
				controller.freezeLayout();
			}
			try{
				final ExpandedState oldState = this.state;
				this.state = state;

				if (oldState != ExpandedState.SHRUNK){
					shrink(oldState);
				}
				if (state == ExpandedState.EXPANDED){
					expand();
				} else if (state == ExpandedState.STRETCHED){
					stretch();
				}

				for (final ExpandableToolbarItemListener listener : expandableListeners){
					listener.changed(this, oldState, state);
				}
			} finally{
				if (controller != null){
					controller.meltLayout();
				}
			}
		}
	}

	@Override
	public boolean isEnabled( ExpandedState state ){
		ExpandableToolbarItemStrategy strategy = expandableStategy.getValue();
		if( strategy == null ){
			return false;
		}
		
		boolean hasEnabled = false;
		boolean hasDisabled = false;
		
		DockStation station = null;
		if( getExpandedState() == ExpandedState.EXPANDED && getDockableCount() == 1 ){
			station = getDockable( 0 ).asDockStation();
		}
		if( station == null ){
			station = this;
		}
		
		for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
			if( strategy.isEnabled( station.getDockable( i ), state )){
				hasEnabled = true;
			}
			else{
				hasDisabled = true;
			}
		}
		if( hasEnabled && hasDisabled ){
			onConflictEnable.getValue();
		}
		return hasEnabled;
	}
	
	private void expand(){
		// state is "shrunk"

		final DockController controller = getController();
		Dockable focused = null;

		final Dockable[] children = new Dockable[getDockableCount()];
		for (int i = 0; i < children.length; i++){
			children[i] = getDockable(i);
			if ((controller != null) && controller.isFocused(children[i])){
				focused = children[i];
			}
		}

		for (int i = children.length - 1; i >= 0; i--){
			remove(getDockable(i));
		}

		final ToolbarTabDockStation station = new ToolbarTabDockStation();
		for (final Dockable child : children){
			station.drop(child);
		}

		drop(station);
		if (focused != null){
			station.setFrontDockable(focused);
			controller.setFocusedDockable(focused, true);
		}
	}

	public void stretch(){
		// state is "shrunk"
	}

	public void shrink(ExpandedState state){
		if (state == ExpandedState.EXPANDED){
			final DockController controller = getController();

			final DockStation child = getDockable(0).asDockStation();
			final Dockable focused = child.getFrontDockable();
			remove(getDockable(0));

			final Dockable[] children = new Dockable[child.getDockableCount()];
			for (int i = 0; i < children.length; i++){
				children[i] = child.getDockable(i);
			}
			for (int i = children.length - 1; i >= 0; i--){
				child.drag(children[i]);
			}

			for (final Dockable next : children){
				drop(next);
			}
			if ((focused != null) && (controller != null)){
				controller.setFocusedDockable(focused, true);
			}
		}
	}

	@Override
	public void addExpandableListener( ExpandableToolbarItemListener listener ){
		if (listener == null){
			throw new IllegalArgumentException("listener must not be null");
		}
		expandableListeners.add(listener);
	}

	@Override
	public void removeExpandableListener( ExpandableToolbarItemListener listener ){
		expandableListeners.remove(listener);
	}

	/**
	 * Gets all the {@link ExpandableToolbarItemListener}s that are currently
	 * registered.
	 * 
	 * @return all the listeners
	 */
	protected ExpandableToolbarItemListener[] expandableListeners(){
		return expandableListeners
				.toArray(new ExpandableToolbarItemListener[expandableListeners
						.size()]);
	}
	
	private void fireEnablementChanged(){
		for( ExpandedState state : ExpandedState.values() ){
			fireEnablementChanged( state );
		}
	}
	
	private void fireEnablementChanged( ExpandedState state ){
		boolean enabled = isEnabled( state );
		if( enabled != expandedEnablementStateCache[ state.ordinal() ]){
			expandedEnablementStateCache[ state.ordinal() ] = enabled;
			for( ExpandableToolbarItemListener listener : expandableListeners() ){
				listener.enablementChanged( this, state, enabled );
			}
		}
	}

	// ########################################################
	// ############### Drop/Move Managing #####################
	// ########################################################

	/**
	 * Gets the {@link ToolbarStrategy} that is currently used by this station.
	 * 
	 * @return the strategy, never <code>null</code>
	 */
	public ToolbarStrategy getToolbarStrategy(){
		final SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(
				ToolbarStrategy.STRATEGY, getController());
		final ToolbarStrategy result = value.getValue();
		value.setProperties((DockController) null);
		return result;
	}

	@Override
	public boolean canDrag( Dockable dockable ){
		if (getExpandedState() == ExpandedState.EXPANDED){
			DockStation child = dockable.asDockStation();
			return child != null && child.getDockableCount() == 0;
		}
		return true;
	}

	/**
	 * Removes <code>dockable</code> from this station.<br>
	 * Note: clients may need to invoke {@link DockController#freezeLayout()}
	 * and {@link DockController#meltLayout()} to ensure none else adds or
	 * removes <code>Dockable</code>s.
	 * 
	 * @param dockable
	 *            the child to remove
	 */
	protected abstract void remove( Dockable dockable );

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		if (old.getClass() == next.getClass()){
			return true;
		} else{
			return false;
		}
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		replace(old.asDockable(), next);
	}

	@Override
	public StationDragOperation prepareDrag( Dockable dockable ){
		removal = dockable;
		getComponent().repaint();
		return new StationDragOperation(){
			@Override
			public void succeeded(){
				removal = null;
				getComponent().repaint();
			}
			
			@Override
			public void canceled(){
				removal = null;
				getComponent().repaint();
			}
		};
	}
	
	/**
	 * Gets the child of this station that is about to be removed.
	 * @return the child that is involved in a drag and drop operation, can be <code>null</code>
	 */
	protected Dockable getRemoval(){
		return removal;
	}
	
	// ########################################################
	// ###################### UI Managing #####################
	// ########################################################

	/**
	 * Creates a new {@link DefaultDisplayerFactoryValue}, a factory used to
	 * create new {@link DockableDisplayer}s.
	 * 
	 * @return the new factory, must not be <code>null</code>
	 */
	protected abstract DefaultDisplayerFactoryValue createDisplayerFactory();

	/**
	 * Gets a {@link StationPaint} which is used to paint some lines onto this
	 * station. Use a {@link DefaultStationPaintValue#setDelegate(StationPaint)
	 * delegate} to exchange the paint.
	 * 
	 * @return the paint
	 */
	public DefaultStationPaintValue getPaint(){
		return paint;
	}

	/**
	 * Registers the default {@link DockTitleFactory} of this station at
	 * <code>controller</code> and returns the associated
	 * {@link DockTitleVersion}.
	 * 
	 * @param controller
	 *            the controller at which the default title factory has to be
	 *            registered
	 * @return the version of the title
	 */
	protected abstract DockTitleVersion registerTitle( DockController controller );

	/**
	 * Replaces <code>displayer</code> with a new {@link DockableDisplayer}.
	 * 
	 * @param displayer
	 *            the displayer to replace
	 * @throws IllegalArgumentException
	 *             if <code>displayer</code> is not a child of this station
	 */
	protected abstract void discard( DockableDisplayer displayer );
	
	/**
	 * The background algorithm of this {@link ToolbarContainerDockStation}.
	 * @author Benjamin Sigg
	 */
	private class Background  extends BackgroundAlgorithm implements StationBackgroundComponent{
		public Background( String backgroundId ){
			super( StationBackgroundComponent.KIND, backgroundId );
		}
		
		@Override
		public Component getComponent(){
			return AbstractToolbarDockStation.this.getComponent();
		}
		
		@Override
		public DockStation getStation(){
			return AbstractToolbarDockStation.this;
		}
	} 

	private class ExpandableListener implements ExpandableToolbarItemStrategyListener{
		@Override
		public void expanded( Dockable item ){
			// ignore
		}

		@Override
		public void stretched( Dockable item ){
			// ignore
		}

		@Override
		public void shrunk( Dockable item ){
			// ignore
		}

		@Override
		public void enablementChanged( Dockable item, ExpandedState state, boolean enabled ){
			if( item.getDockParent() == AbstractToolbarDockStation.this ){
				fireEnablementChanged( state );
			}
		}
	}
}
