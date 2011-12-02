package bibliothek.gui.dock;

import java.awt.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OrientingDockStationEvent;
import bibliothek.gui.dock.station.OrientingDockStationListener;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.ToolbarTabDockStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItem;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Path;

/**
 * Base class of a {@link DockStation} behaving like a typical toolbar: the
 * children are ordered in a list, an optional title and border may be shown.
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public abstract class AbstractToolbarDockStationOld extends
		AbstractDockableStation implements OrientedDockStation,
		ToolbarInterface, ToolbarElementInterface, ExpandableToolbarItem{

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	protected OverpaintablePanelBase mainPanel;

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

	/** A list of all children */
	protected DockablePlaceholderList<StationChildHandle> dockables = new DockablePlaceholderList<StationChildHandle>();
	/** Alignment of the content of this station */
	private Orientation orientation = Orientation.HORIZONTAL;
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

	/** all registered {@link OrientingDockStationListener}s. */
	private List<OrientingDockStationListener> orientingListeners = new ArrayList<OrientingDockStationListener>();

	/** all registered {@link ExpandableToolbarItemListener}s */
	private List<ExpandableToolbarItemListener> expandableListeners = new ArrayList<ExpandableToolbarItemListener>();

	/** the current behavior of this station */
	private ExpandedState state = ExpandedState.SHRUNK;

	/** current {@link PlaceholderStrategy} */
	private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>(
			PlaceholderStrategy.PLACEHOLDER_STRATEGY){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue,
				PlaceholderStrategy newValue ){
			dockables.setStrategy(newValue);
		}
	};

	/**
	 * Constructs a new ToolbarDockStation. Subclasses must call {@link #init()}
	 * once the constructor has been executed.
	 */
	public AbstractToolbarDockStationOld(){
		// nothing
	}

	/**
	 * Init the class and especially should ini the main panel.
	 */
	protected abstract void init();

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

	@Override
	public int getDockableCount(){
		return dockables.dockables().size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.dockables().get(index).getDockable();
	}

	/**
	 * Grants direct access to the list of {@link Dockable}s, subclasses should
	 * not modify the list unless the fire the appropriate events.
	 * 
	 * @return the list of dockables
	 */
	protected PlaceholderList.Filter<StationChildHandle> getDockables(){
		return dockables.dockables();
	}

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
			DockController controller = getController();
			if (controller != null){
				controller.freezeLayout();
			}
			try{
				ExpandedState oldState = this.state;

				if (this.state != ExpandedState.SHRUNK){
					shrink();
				}
				if (state == ExpandedState.EXPANDED){
					expand();
				} else if (state == ExpandedState.STRETCHED){
					stretch();
				}
				this.state = state;

				for (ExpandableToolbarItemListener listener : expandableListeners){
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

		DockController controller = getController();
		Dockable focused = null;

		Dockable[] children = new Dockable[getDockableCount()];
		for (int i = 0; i < children.length; i++){
			children[i] = getDockable(i);
			if (controller != null && controller.isFocused(children[i])){
				focused = children[i];
			}
		}

		for (int i = children.length - 1; i >= 0; i--){
			remove(i);
		}

		StackDockStation station = new ToolbarTabDockStation();
		for (Dockable child : children){
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
			DockController controller = getController();

			DockStation child = getDockable(0).asDockStation();
			Dockable focused = child.getFrontDockable();
			remove(0);

			Dockable[] children = new Dockable[child.getDockableCount()];
			for (int i = 0; i < children.length; i++){
				children[i] = child.getDockable(i);
			}
			for (int i = children.length - 1; i >= 0; i--){
				child.drag(children[i]);
			}

			for (Dockable next : children){
				drop(next);
			}
			if (focused != null && controller != null){
				controller.setFocusedDockable(focused, true);
			}
		}
	}

	/**
	 * Gets the placeholders of this station using a
	 * {@link PlaceholderListItemConverter} to encode the children. The
	 * converter puts the following parameters for each {@link Dockable} into
	 * the map:
	 * <ul>
	 * <li>id: the integer from <code>children</code></li>
	 * <li>index: the location of the element in the dockables-list</li>
	 * <li>placeholder: the placeholder of the element, might be missing</li>
	 * </ul>
	 * 
	 * @param children
	 *            a unique identifier for each child of this station
	 * @return the map
	 */
	public PlaceholderMap getPlaceholders( final Map<Dockable, Integer> children ){
		final PlaceholderStrategy strategy = getPlaceholderStrategy();

		return dockables
				.toMap(new PlaceholderListItemAdapter<Dockable, StationChildHandle>(){
					@Override
					public ConvertedPlaceholderListItem convert( int index,
							StationChildHandle handle ){
						Dockable dockable = handle.getDockable();

						Integer id = children.get(dockable);
						if (id == null){
							return null;
						}

						ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
						item.putInt("id", id);
						item.putInt("index", index);

						if (strategy != null){
							Path placeholder = strategy
									.getPlaceholderFor(dockable);
							if (placeholder != null){
								item.putString("placeholder",
										placeholder.toString());
								item.setPlaceholder(placeholder);
							}
						}

						return item;
					}
				});
	}

	/**
	 * Sets a new layout on this station, this method assumes that
	 * <code>map</code> was created by the method {@link #getPlaceholders(Map)}.
	 * 
	 * @param map
	 *            the map to read
	 * @param children
	 *            the new children of this station
	 * @throws IllegalStateException
	 *             if there are children left on this station
	 */
	public void setPlaceholders( PlaceholderMap map,
			final Map<Integer, Dockable> children ){
		DockUtilities.checkLayoutLocked();
		if (getDockableCount() > 0){
			throw new IllegalStateException("must not have any children");
		}
		DockController controller = getController();

		try{
			if (controller != null){
				controller.freezeLayout();
			}

			DockablePlaceholderList<StationChildHandle> next = new DockablePlaceholderList<StationChildHandle>();

			if (getController() != null){
				dockables.setStrategy(null);
				dockables.unbind();
				dockables = next;
			} else{
				dockables = next;
			}

			next.read(
					map,
					new PlaceholderListItemAdapter<Dockable, StationChildHandle>(){
						private DockHierarchyLock.Token token;
						private int index = 0;

						@Override
						public StationChildHandle convert(
								ConvertedPlaceholderListItem item ){
							int id = item.getInt("id");
							Dockable dockable = children.get(id);
							if (dockable != null){
								DockUtilities.ensureTreeValidity(
										AbstractToolbarDockStationOld.this,
										dockable);
								token = DockHierarchyLock.acquireLinking(
										AbstractToolbarDockStationOld.this,
										dockable);
								listeners.fireDockableAdding(dockable);
								return new StationChildHandle(
										AbstractToolbarDockStationOld.this,
										displayers, dockable, title);
							}
							return null;
						}

						@Override
						public void added( StationChildHandle handle ){
							try{
								handle.updateDisplayer();
								insertAt(handle, index++);
								listeners.fireDockableAdded(handle
										.getDockable());
							} finally{
								token.release();
							}
						}
					});

			if (getController() != null){
				dockables.bind();
				dockables.setStrategy(getPlaceholderStrategy());
			}
		} finally{
			if (controller != null){
				controller.meltLayout();
			}
		}
	}

	@Override
	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		if (getDockableCount() > 0){
			throw new IllegalStateException(
					"only allowed if there are not children present");
		}

		try{
			DockablePlaceholderList<StationChildHandle> next = new DockablePlaceholderList<StationChildHandle>(
					placeholders);
			if (getController() != null){
				dockables.setStrategy(null);
				dockables.unbind();
				dockables = next;
				dockables.bind();
				dockables.setStrategy(getPlaceholderStrategy());
			} else{
				dockables = next;
			}
		} catch (IllegalArgumentException ex){
			// silent
		}
	}

	/**
	 * Gets the {@link PlaceholderStrategy} that is currently in use.
	 * 
	 * @return the current strategy, may be <code>null</code>
	 */
	public PlaceholderStrategy getPlaceholderStrategy(){
		return placeholderStrategy.getValue();
	}

	/**
	 * Sets the {@link PlaceholderStrategy} to use, <code>null</code> will set
	 * the default strategy.
	 * 
	 * @param strategy
	 *            the new strategy, can be <code>null</code>
	 */
	public void setPlaceholderStrategy( PlaceholderStrategy strategy ){
		placeholderStrategy.setValue(strategy);
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		int index = indexOf(child);
		Path placeholder = null;
		PlaceholderStrategy strategy = getPlaceholderStrategy();
		if (strategy != null){
			placeholder = strategy.getPlaceholderFor(target == null ? child
					: target);
			if (placeholder != null){
				dockables.dockables().addPlaceholder(index, placeholder);
			}
		}
		return getDockableProperty(child, target, index, placeholder);
	}

	/**
	 * Creates a new {@link DockableProperty} describing the location of
	 * <code>child</code> on this station. This method is called by
	 * {@link #getDockableProperty(Dockable, Dockable)} once the location and
	 * placeholder of <code>child</code> or <code>target</code> have been
	 * calculated
	 * 
	 * @param child
	 *            a child of this station
	 * @param target
	 *            the item whose position is searched
	 * @param index
	 *            the location of <code>child</code>
	 * @param placeholder
	 *            the placeholder for <code>target</code> or <code>child</code>
	 * @return a new {@link DockableProperty} that stores <code>index</code>,
	 *         <code>placeholder</code> and any other information a subclass
	 *         deems necessary to store
	 */
	protected abstract DockableProperty getDockableProperty( Dockable child,
			Dockable target, int index, Path placeholder );
	
	/**
	 * Tells whether the subclass knows how to handle <code>property</code>.
	 * This means that the type of <code>property</code> is the same type as the
	 * result of {@link #getDockableProperty(Dockable, Dockable, int, Path)}
	 * 
	 * @param property
	 *            the property to check
	 * @return <code>true</code> if this sublcass knows how to handle the type
	 *         of <code>property</code>
	 */
	protected abstract boolean isValidProperty( DockableProperty property );
	
	/**
	 * Gets the location of a {@link Dockable} on this station. Called only if
	 * <code>property</code> passed {@link #isValidProperty(DockableProperty)}.
	 * 
	 * @param property
	 *            some property created by
	 *            {@link #getDockableProperty(Dockable, Dockable, int, Path)}
	 * @return the index parameter
	 */
	protected abstract int getIndex( DockableProperty property );

	/**
	 * Gets the placeholder of a {@link Dockable} on this station. Called only
	 * if <code>property</code> passed
	 * {@link #isValidProperty(DockableProperty)}.
	 * 
	 * @param property
	 *            some property created by
	 *            {@link #getDockableProperty(Dockable, Dockable, int, Path)}
	 * @return the placeholder parameter, may be <code>null</code>
	 */
	protected abstract Path getPlaceholder( DockableProperty property );

	/**
	 * Drop thanks to information collect by dropInfo
	 * 
	 * @param dropInfo
	 */
	protected abstract void drop( StationDropOperation dropInfo );

	@Override
	public void drop( Dockable dockable ){
		System.out.println(this.toString() + "## drop(Dockable dockable)##");
		this.drop(dockable, getDockableCount(), true);
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if (isValidProperty(property)){
			boolean acceptable = acceptable(dockable);
			boolean result = false;
			int index = Math.min(getDockableCount(), getIndex(property));

			Path placeholder = getPlaceholder(property);
			if (placeholder != null && property.getSuccessor() != null){
				StationChildHandle preset = dockables
						.getDockableAt(placeholder);
				if (preset != null){
					DockStation station = preset.getDockable().asDockStation();
					if (station != null){
						if (station.drop(dockable, property.getSuccessor())){
							dockables.removeAll(placeholder);
							result = true;
						}
					}
				}
			}

			if (!result && placeholder != null){
				if (acceptable && dockables.hasPlaceholder(placeholder)){
					add(dockable, index, placeholder);
					result = true;
				}
			}

			if (!result && dockables.dockables().size() == 0){
				if (acceptable){
					drop(dockable);
					result = true;
				}
			}

			if (!result){
				if (index < dockables.dockables().size()
						&& property.getSuccessor() != null){
					DockStation child = getDockable(index).asDockStation();
					if (child != null){
						result = child.drop(dockable, property.getSuccessor());
					}
				}
			}

			if (!result && acceptable){
				result = drop(dockable, index);
			}

			return result;
		}
		return false;
	}

	/**
	 * Drops <code>dockable</code> at location <code>index</code>.
	 * 
	 * @param dockable
	 *            the element to add
	 * @param index
	 *            the location of <code>dockable</code>
	 * @return whether the operation was succesfull or not
	 */
	public boolean drop( Dockable dockable, int index ){
		return drop(dockable, index, false);
	}

	private boolean drop( Dockable dockable, int index, boolean force ){
		// note: merging of two ToolbarGroupDockStations is done by the
		// ToolbarGroupDockStationMerger
		System.out.println(this.toString()
				+ "## drop(Dockable dockable, int index)##");
		if (force || this.accept(dockable)){
			if (!force){
				dockable = getToolbarStrategy().ensureToolbarLayer(this,
						dockable);
				if (dockable == null){
					return false;
				}
			}
			add(dockable, index);
			return true;
		}
		return false;
	}

	protected void move( Dockable dockable, int indexWhereInsert ){
		System.out.println(this.toString() + "## move() ## ==> Index: "
				+ indexWhereInsert);
		DockController controller = getController();
		try{
			if (controller != null){
				controller.freezeLayout();
			}
			this.add(dockable, indexWhereInsert);
		} finally{
			if (controller != null){
				controller.meltLayout();
			}
		}
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO pending
	}

	@Override
	public boolean canDrag( Dockable dockable ){
		if (getExpandedState() == ExpandedState.EXPANDED){
			return false;
		}

		System.out.println(this.toString()
				+ "## canDrag(Dockable dockable) ## ");
		return true;
	}

	@Override
	public void drag( Dockable dockable ){
		System.out.println(this.toString() + "## drag(Dockable dockable) ##");
		if (dockable.getDockParent() != this)
			throw new IllegalArgumentException(
					"The dockable cannot be dragged, it is not child of this station.");
		int index = this.indexOf(dockable);
		if (index >= 0){
			this.remove(index);
		}
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		System.out.println(this.toString()
				+ "## canReplace(Dockable old, Dockable next) ## ");
		if (old.getClass() == next.getClass()){
			return true;
		} else{
			return false;
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		System.out.println(this.toString()
				+ "## replace(Dockable old, Dockable next) ## ");
		DockUtilities.checkLayoutLocked();
		DockController controller = getController();
		if (controller != null)
			controller.freezeLayout();
		int index = indexOf(old);
		remove(old);
		// the child is a ToolbarGroupDockStation because canReplace()
		// ensure it
		add(next, index);
		controller.meltLayout();
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		System.out.println(this.toString()
				+ "## replace(DockStation old, Dockable next) ## ");
		replace(old.asDockable(), next);
	}

	@Override
	public Component getComponent(){
		return mainPanel;
	}

	/**
	 * Gets the {@link ToolbarStrategy} that is currently used by this station.
	 * 
	 * @return the strategy, never <code>null</code>
	 */
	public ToolbarStrategy getToolbarStrategy(){
		SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(
				ToolbarStrategy.STRATEGY, getController());
		ToolbarStrategy result = value.getValue();
		value.setProperties((DockController) null);
		return result;
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

	/**
	 * Gets the location of <code>dockable</code> in the component-panel.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the location or -1 if the child was not found
	 */
	public int indexOf( Dockable dockable ){
		for (int i = 0; i < dockables.dockables().size(); i++){
			if (dockables.dockables().get(i).getDockable() == dockable){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Insert one dockable at the index. The dockable can be a
	 * {@link ComponentDockable}, {@link ToolbarDockStation} or a
	 * {@link ToolbarGroupDockStation} (see method accept()). All the
	 * ComponentDockable extracted from the element are merged together and
	 * wrapped in a {@link ToolbarDockStation} before to be inserted at the
	 * index
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 */
	private void add( Dockable dockable, int index ){
		add(dockable, index, null);
	}

	protected abstract void add( Dockable dockable, int index, Path placeholder );
	
	protected abstract void insertAt( StationChildHandle handle, int index );
	
	/**
	 * Replaces <code>displayer</code> with a new {@link DockableDisplayer}.
	 * 
	 * @param displayer
	 *            the displayer to replace
	 * @throws IllegalArgumentException
	 *             if <code>displayer</code> is not a child of this station
	 */
	protected void discard( DockableDisplayer displayer ){
		Dockable dockable = displayer.getDockable();

		int index = indexOf(dockable);
		if (index < 0){
			throw new IllegalArgumentException(
					"displayer is not a child of this station: " + displayer);
		}

		StationChildHandle handle = dockables.dockables().get(index);

		mainPanel.getContentPane().remove(handle.getDisplayer().getComponent());
		handle.updateDisplayer();
		insertAt(handle, index);
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
	private void remove( Dockable dockable ){
		int index = this.indexOf(dockable);
		if (index >= 0)
			this.remove(index);
	}

	/**
	 * Removes the child with the given <code>index</code> from this station.<br>
	 * Note: clients may need to invoke {@link DockController#freezeLayout()}
	 * and {@link DockController#meltLayout()} to ensure noone else adds or
	 * removes <code>Dockable</code>s.
	 * 
	 * @param index
	 *            the index of the child that will be removed
	 */
	private void remove( int index ){
		DockUtilities.checkLayoutLocked();
		StationChildHandle handle = dockables.dockables().get(index);
		Dockable dockable = handle.getDockable();

		if (getFrontDockable() == dockable)
			setFrontDockable(null);

		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking(
				this, dockable);
		try{
			listeners.fireDockableRemoving(dockable);
			dockable.setDockParent(null);

			dockables.remove(index);
			mainPanel.getContentPane().remove(
					handle.getDisplayer().getComponent());
			mainPanel.doLayout();
			mainPanel.getContentPane().revalidate();
			mainPanel.getContentPane().repaint();
			handle.destroy();
			listeners.fireDockableRemoved(dockable);
			fireDockablesRepositioned(index);
		} finally{
			token.release();
		}
	}

	@Override
	public Orientation getOrientation(){
		return orientation;
	}

	@Override
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;

		// it's very important to change position and orientation of inside
		// dockables first, else doLayout() is done on wrong inside information
		for (StationChildHandle handle : dockables.dockables()){
			Dockable d = handle.getDockable();
			if (d instanceof OrientedDockStation){
				OrientedDockStation group = (OrientedDockStation) d;
				group.setOrientation(this.getOrientation());
			}
		}
		mainPanel.updateAlignment();
		mainPanel.revalidate();
		fireOrientingEvent();

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
		OrientingDockStationEvent event = new OrientingDockStationEvent(this);
		for (OrientingDockStationListener listener : orientingListeners
				.toArray(new OrientingDockStationListener[orientingListeners
						.size()])){
			listener.changed(event);
		}
	}

	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too. It allows to draw arbitrary figures over
	 * the base panel.
	 * 
	 * @author Herve Guillaume
	 */
	protected abstract class OverpaintablePanelBase extends SecureContainer{
		/**
		 * Update the alignment and the layout of child with regards to the
		 * current orientation.
		 */
		public abstract void updateAlignment();
	}

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

	@Override
	public void setController( DockController controller ){
		if (getController() != controller){
			if (getController() != null){
				dockables.unbind();
			}
			for (StationChildHandle handle : dockables.dockables()){
				handle.setTitleRequest(null);
			}

			super.setController(controller);
			// if not set controller of the DefaultStationPaintValue, call to
			// DefaultStationPaintValue do nothing

			if (controller == null){
				title = null;
			} else{
				title = registerTitle(controller);
			}
			paint.setController(controller);
			placeholderStrategy.setProperties(controller);
			displayerFactory.setController(controller);
			displayers.setController(controller);
			mainPanel.setController(controller);

			if (controller != null){
				dockables.bind();
			}
			for (StationChildHandle handle : dockables.dockables()){
				handle.setTitleRequest(title, true);
			}
		}
	}
}
