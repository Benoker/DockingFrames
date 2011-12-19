package bibliothek.gui.dock;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OrientationObserver;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OrientingDockStationEvent;
import bibliothek.gui.dock.station.OrientingDockStationListener;
import bibliothek.gui.dock.station.StationDropOperation;
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
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.util.FrameworkOnly;

/**
 * Base class of a {@link DockStation} behaving like a typical toolbar: the
 * children are ordered in a list, an optional title and border may be shown.
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public abstract class AbstractToolbarDockStation extends
		AbstractDockableStation implements OrientedDockStation,
		ToolbarInterface, ToolbarElementInterface, ExpandableToolbarItem{

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
	/** the index of the closest dockable above the mouse */
	protected int indexBeneathMouse = -1;
	/** closest side of the the closest dockable above the mouse */
	protected Position sideBeneathMouse = null;
	/**
	 * Tells if this station is in prepareDrop state and should draw something
	 * accordingly
	 */
	protected boolean prepareDropDraw = false;

	/** Alignment of the content of this station */
	protected Orientation orientation = Orientation.HORIZONTAL;

	/** all registered {@link OrientingDockStationListener}s. */
	private final List<OrientingDockStationListener> orientingListeners = new ArrayList<OrientingDockStationListener>();

	/** all registered {@link ExpandableToolbarItemListener}s */
	private final List<ExpandableToolbarItemListener> expandableListeners = new ArrayList<ExpandableToolbarItemListener>();
	/** the current behavior of this station */
	private ExpandedState state = ExpandedState.SHRUNK;

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
	}

	/**
	 * Init the class and especially should ini the main panel.
	 */
	protected abstract void init();

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

				if (this.state != ExpandedState.SHRUNK){
					shrink();
				}
				if (state == ExpandedState.EXPANDED){
					expand();
				} else if (state == ExpandedState.STRETCHED){
					stretch();
				}
				this.state = state;

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

		final StackDockStation station = new ToolbarTabDockStation();
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

	public void shrink(){
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

	/**
	 * Drop thanks to information collect by dropInfo
	 * 
	 * @param dropInfo
	 */
	protected abstract void drop( StationDropOperation dropInfo );

	@Override
	public boolean canDrag( Dockable dockable ){
		if (getExpandedState() == ExpandedState.EXPANDED){
			return false;
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
	 * Gets an id that is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	protected abstract String getDisplayerId();

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

}
