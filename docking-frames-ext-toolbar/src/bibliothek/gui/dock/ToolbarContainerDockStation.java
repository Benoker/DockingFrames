package bibliothek.gui.dock;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OrientingDockStation;
import bibliothek.gui.dock.station.OrientingDockStationEvent;
import bibliothek.gui.dock.station.OrientingDockStationListener;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.DefaultToolbarContainerConverter;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerConverter;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerConverterCallback;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.extension.Extension;

/**
 * A {@link Dockable} and a {@link DockStation} which stands for a group of
 * {@link ToolbarGroupDockStation}. As dockable it can be put in every
 * {@link DockStation}. As DockStation it accepts only
 * {@link ToolbarElementInterface}s. When ToolbarElement are added, all the
 * ComponentDockable extracted from the element are merged together and wrapped
 * in a {@link ToolbarGroupDockStation} before to be added.
 * 
 * @author Herve Guillaume
 */
public class ToolbarContainerDockStation extends AbstractDockableStation
		implements ToolbarInterface, OrientingDockStation, OrientedDockStation{

	/** the id of the {@link DockTitleFactory} used with this station */
	public static final String TITLE_ID = "toolbar.container";
	/**
	 * This id is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	public static final String DISPLAYER_ID = "toolbar.container";

	public static final Orientation DEFAULT_ORIENTATION = Orientation.VERTICAL;

	/** the orientation of the station */
	private Orientation orientation = Orientation.VERTICAL;

	private int dockablesMaxNumber = -1;

	/** The containerPane */
	private JPanel containerPanel;
	/**
	 * The graphical representation of this station: the pane which contains
	 * toolbars
	 */
	protected OverpaintablePanelBase mainPanel;

	/** dockables associate with the container pane */
	private DockablePlaceholderList<StationChildHandle> dockables = new DockablePlaceholderList<StationChildHandle>();
	/** the {@link DockableDisplayer} shown */
	private DisplayerCollection displayer;
	/** factory for {@link DockTitle}s used for the main panel */
	private DockTitleVersion title;
	/** factory for creating new {@link DockableDisplayer}s */
	private DefaultDisplayerFactoryValue displayerFactory;

	/** A paint to draw lines */
	private DefaultStationPaintValue paint;
	/** the index of the closest dockable above the mouse */
	private int indexBeneathMouse = -1;
	/** closest side of the the closest dockable above the mouse */
	private Position sideAboveMouse = null;
	/**
	 * Tells if this station is in prepareDrop state and should draw something
	 * accordingly
	 */
	boolean prepareDropDraw = false;

	/** all registered {@link OrientingDockStationListener}s. */
	private List<OrientingDockStationListener> orientingListeners = new ArrayList<OrientingDockStationListener>();

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
	 * Constructs a new ContainerLineStation
	 */
	public ToolbarContainerDockStation( Orientation orientation ){
		this.orientation = orientation;
		mainPanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT
				+ ".toolbar", this);

		displayerFactory = new DefaultDisplayerFactoryValue(
				ThemeManager.DISPLAYER_FACTORY + ".toolbar.container", this);

		displayer = new DisplayerCollection(this, displayerFactory,
				DISPLAYER_ID);

		DockableDisplayerListener listener = new DockableDisplayerListener(){
			@Override
			public void discard( DockableDisplayer displayer ){
				ToolbarContainerDockStation.this.discard(displayer);
			}
		};
		displayer.addDockableDisplayerListener(listener);
		setTitleIcon(null);
	}

	/**
	 * Create a pane for this dock station
	 */
	private JPanel createPanel(){
		JPanel panel = new JPanel();

		panel.setOpaque(false);

		switch (orientation) {
		case HORIZONTAL:
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.setAlignmentX(Component.LEFT_ALIGNMENT);
			break;
		case VERTICAL:
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setAlignmentY(Component.TOP_ALIGNMENT);
			break;
		}
		panel.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
		return panel;
	}

	@Override
	public int getDockableCount(){
		return dockables.dockables().size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.dockables().get(index).getDockable();
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
	public PlaceholderMap getPlaceholders(){
		return createConverter().getPlaceholders(this);
	}

	/**
	 * Gets the layout of this station encoded as {@link PlaceholderMap}.
	 * 
	 * @param children
	 *            identifiers for the children
	 * @return the encoded layout, not <code>null</code>
	 */
	public PlaceholderMap getPlaceholders( Map<Dockable, Integer> children ){
		return createConverter().getPlaceholders(this, children);
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		createConverter().setPlaceholders(this, placeholders);
	}

	/**
	 * Sets the layout of this station using the encoded layout from
	 * <code>placeholders</code>
	 * 
	 * @param placeholders
	 *            the placeholders to read
	 * @param children
	 *            the children to add
	 */
	public void setPlaceholders( PlaceholderMap placeholders,
			Map<Integer, Dockable> children ){
		createConverter().setPlaceholders(this,
				new ToolbarContainerConverterCallback(){
					int index = 0;

					@Override
					public StationChildHandle wrap( Dockable dockable ){
						return new StationChildHandle(
								ToolbarContainerDockStation.this, displayer,
								dockable, title);
					}

					@Override
					public void adding( StationChildHandle handle ){
						listeners.fireDockableAdding(handle.getDockable());
					}

					@Override
					public void added( StationChildHandle handle ){
						handle.updateDisplayer();

						insertAt(handle, index++);

						handle.getDockable().setDockParent(
								ToolbarContainerDockStation.this);
						listeners.fireDockableAdded(handle.getDockable());
					}

					@Override
					public void setDockables(
							DockablePlaceholderList<StationChildHandle> list ){
						ToolbarContainerDockStation.this.setDockables(list,
								false);
					}

					@Override
					public void finished(
							DockablePlaceholderList<StationChildHandle> list ){
						if (getController() != null){
							list.bind();
							list.setStrategy(getPlaceholderStrategy());
						}
					}

				}, placeholders, children);
	}

	/**
	 * Creates a {@link ToolbarContainerConverter} which will be used for one
	 * call.
	 * 
	 * @return the new converter, not <code>null</code>
	 */
	protected ToolbarContainerConverter createConverter(){
		return new DefaultToolbarContainerConverter();
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
		return new ToolbarContainerProperty(index, null);
	}

	@Override
	public StationDropOperation prepareDrop( int mouseX, int mouseY,
			int titleX, int titleY, Dockable dockable ){
		// System.out.println(this.toString() + "## prepareDrop(...) ##");
		DockController controller = getController();

		// check if the dockable and the station accept each other
		if (this.accept(dockable) & dockable.accept(this)){
			// check if controller exist and if the controller accept that
			// the dockable become a child of this station
			if (controller != null){
				if (!controller.getAcceptance().accept(this, dockable)){
					return null;
				}
			}
			Point mousePoint = new Point(mouseX, mouseY);
			SwingUtilities.convertPointFromScreen(mousePoint,
					mainPanel.getContentPane());

			if (!getToolbarStrategy().isToolbarPart(dockable)){
				// only ToolbarElementInterface can be drop or move into this
				return null;
			}

			ToolbarContainerDropInfo result = new ToolbarContainerDropInfo(
					dockable, this, dockables, mouseX, mouseY){
				@Override
				public void execute(){
					drop(this);
				}

				// Note: draw() is called first by the Controller. It seems
				// destroy() is called after, after a new StationDropOperation
				// is created

				@Override
				public void destroy(){
					// without this line, nothing is displayed except if you
					// drag another component
					ToolbarContainerDockStation.this.indexBeneathMouse = -1;
					ToolbarContainerDockStation.this.sideAboveMouse = null;
					ToolbarContainerDockStation.this.prepareDropDraw = false;
					ToolbarContainerDockStation.this.mainPanel.repaint();

				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					// Reminder: if dockable beneath mouse doesn't belong to
					// this, then indexOf return -1
					ToolbarContainerDockStation.this.indexBeneathMouse = indexOf(getDockableBeneathMouse());
					ToolbarContainerDockStation.this.prepareDropDraw = true;
					ToolbarContainerDockStation.this.sideAboveMouse = this
							.getSideDockableBeneathMouse();
					// without this line, line is displayed only on the first
					// component met
					ToolbarContainerDockStation.this.mainPanel.repaint();
				}
			};
			// System.out.println(result.toSummaryString());
			return result;

		} else{
			return null;
		}
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
		return this.orientation;
	}

	/**
	 * Sets the number of dockables that this station will accept (max = -1
	 * indicates that there's no limit).
	 * 
	 * @param max
	 *            the number of dockables accepted
	 */
	public void setDockablesMaxNumber( int max ){
		dockablesMaxNumber = max;
	}

	/**
	 * Gets the number of dockables that this station will accept.
	 * 
	 * @return the number of dockables accepted (-1 if there is no maximum
	 *         number)
	 */
	public int getDockablesMaxNumber(){
		return dockablesMaxNumber;
	}

	/**
	 * Fires an {@link OrientingDockStationEvent}.
	 * 
	 * @param dockables
	 *            the items whose orientation changed
	 */
	protected void fireOrientingEvent( Dockable ... dockables ){
		OrientingDockStationEvent event = new OrientingDockStationEvent(this,
				dockables);
		for (OrientingDockStationListener listener : orientingListeners
				.toArray(new OrientingDockStationListener[orientingListeners
						.size()])){
			listener.changed(event);
		}
	}

	private void drop( ToolbarContainerDropInfo dropInfo ){
		// Note: Computation of index to insert drag dockable is not the
		// same between a move() and a drop(), because with a move() it is
		// as if the drag dockable were remove first then added again in the
		// list (Note: It's wird because in fact drag() is called after
		// move()...)

		// we check if there's dockable in this station
		if (this.getDockables().dockables().size() == 0){
			// in this case, it's inevitably a drop() action
			drop(dropInfo.getItem(), 0);
		}
		// if the dockable has to be drop at the same place (centered with
		// regards to itself): nothing to be done
		if (dropInfo.getItemPositionVSBeneathDockable() != Position.CENTER){
			int indexBeneathMouse = indexOf(dropInfo.getDockableBeneathMouse());
			int dropIndex;

			if (dropInfo.isMove()){
				switch (this.getOrientation()) {
				case VERTICAL:
					if (dropInfo.getItemPositionVSBeneathDockable() == Position.SOUTH){
						if (dropInfo.getSideDockableBeneathMouse() == Position.SOUTH){
							dropIndex = indexBeneathMouse + 1;
						} else{
							dropIndex = indexBeneathMouse;
						}
					} else{
						if (dropInfo.getSideDockableBeneathMouse() == Position.SOUTH){
							dropIndex = indexBeneathMouse;
						} else{
							dropIndex = indexBeneathMouse - 1;
						}
					}
					move(dropInfo.getItem(), dropIndex);
					break;
				case HORIZONTAL:
					if (dropInfo.getItemPositionVSBeneathDockable() == Position.EAST){
						if (dropInfo.getSideDockableBeneathMouse() == Position.EAST){
							dropIndex = indexBeneathMouse + 1;
						} else{
							dropIndex = indexBeneathMouse;
						}
					} else{
						if (dropInfo.getSideDockableBeneathMouse() == Position.EAST){
							dropIndex = indexBeneathMouse;
						} else{
							dropIndex = indexBeneathMouse - 1;
						}
					}
					move(dropInfo.getItem(), dropIndex);
					break;
				}

			} else{
				int increment = 0;
				if (dropInfo.getSideDockableBeneathMouse() == Position.SOUTH
						|| dropInfo.getSideDockableBeneathMouse() == Position.EAST){
					increment++;
				}
				dropIndex = indexBeneathMouse + increment;
				drop(dropInfo.getItem(), dropIndex);
			}
		}

	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if (property instanceof ToolbarContainerProperty){
			ToolbarContainerProperty toolbar = (ToolbarContainerProperty) property;

			if (toolbar.getSuccessor() != null){
				Dockable preset = null;
				DockablePlaceholderList<StationChildHandle> list = getDockables();

				if (toolbar.getIndex() < list.dockables().size()){
					preset = list.dockables().get(toolbar.getIndex())
							.getDockable();
				}

				if (preset != null && preset.asDockStation() != null){
					return preset.asDockStation().drop(dockable,
							property.getSuccessor());
				}
			}

			int max = getDockables().dockables().size();
			return drop(dockable, Math.min(max, toolbar.getIndex()));

		}
		return false;
	}

	/**
	 * Adds <code>dockable</code> to this station. The dockable must be a
	 * {@link ToolbarElementInterface} : if not, do nothing. The dockable is
	 * added at the last position.
	 * 
	 * @param dockable
	 *            a new child
	 */
	@Override
	public void drop( Dockable dockable ){
		// System.out.println(this.toString() +
		// "## drop(Dockable dockable )##");
		this.drop(dockable, getDockables().dockables().size());
	}

	/**
	 * Inserts <code>dockable</code> to this station at the given index. The
	 * dockable must be a {@link ToolbarElementInterface}: if not, do nothing.
	 * 
	 * @param dockable
	 *            a new child
	 * @return <code>true</code> if dropping was successfull
	 */
	private boolean drop( Dockable dockable, int index ){
		// System.out.println(this.toString()
		// + "## drop(Dockable dockable, int index )##");
		return this.add(dockable, index);
	}

	private void move( Dockable dockable, int indexWhereInsert ){
		// System.out.println(this.toString() + "## move() ## ==> ");
		if (getToolbarStrategy().isToolbarPart(dockable)){
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

	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
	}

	@Override
	public boolean canDrag( Dockable dockable ){
		// System.out.println(this.toString()
		// + "## canDrag(Dockable dockable) ## " + this.toString());
		return true;
	}

	@Override
	public void drag( Dockable dockable ){
		// System.out.println(this.toString() +
		// "## drag(Dockable dockable) ##");
		if (dockable.getDockParent() != this)
			throw new IllegalArgumentException(
					"The dockable cannot be dragged, it is not child of this station.");
		this.remove(dockable);
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		if (old.getClass() == next.getClass()){
			return true;
		} else{
			return false;
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		// System.out.println(this.toString()
		// + "## replace(Dockable old, Dockable next) ## "
		// + this.toString());
		DockUtilities.checkLayoutLocked();
		DockController controller = getController();
		if (controller != null)
			controller.freezeLayout();
		int index = indexOf(old);
		this.remove(old);
		// the child is a TollbarGroupDockStation because canReplace()
		// ensure it
		add(next, index);
		controller.meltLayout();
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		// System.out.println(this.toString()
		// + "## replace(DockStation old, Dockable next) ## "
		// + this.toString());
		replace(old.asDockable(), next);
	}

	@Override
	public String getFactoryID(){
		return ToolbarContainerDockStationFactory.ID;
	}

	@Override
	public Component getComponent(){
		return mainPanel;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme(this, new ToolbarContainerDockStationFactory());
	}

	/**
	 * Gets the panel which contains dockables
	 * 
	 * @return the panel which contains dockables
	 */
	public JPanel getContainerPanel(){
		return containerPanel;
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
	public boolean accept( Dockable child ){
		if (dockables.dockables().size() == -1){
			return true;
		} else if (dockables.dockables().size() >= dockablesMaxNumber){
			return false;
		} else{
			return true;
		}
	}

	@Override
	public boolean accept( DockStation station ){
		return true;
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

	/**
	 * Updates one of the lists containing dockables.
	 * 
	 * @param list
	 *            the new list
	 * @param bind
	 *            whether the new list should be
	 *            {@link DockablePlaceholderList#bind() bound}
	 */
	private void setDockables(
			DockablePlaceholderList<StationChildHandle> list, boolean bind ){
		if (getController() != null){
			DockablePlaceholderList<StationChildHandle> oldList = getDockables();
			oldList.setStrategy(null);
			oldList.unbind();
		}

		dockables = list;

		if (getController() != null && bind){
			list.bind();
			list.setStrategy(getPlaceholderStrategy());
		}
	}

	/**
	 * Gets the dockables in the station
	 * 
	 * @return the dockables associated with the station
	 */
	public DockablePlaceholderList<StationChildHandle> getDockables(){
		return dockables;
	}

	/**
	 * Gets the orientation of dockables in the station
	 * 
	 * @return the orientation
	 */
	@Override
	public Orientation getOrientation(){
		return orientation;
	}

	/**
	 * Sets the orientation of dockables in the station
	 * 
	 * @param orientation
	 *            the orientation
	 */
	@Override
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
		ArrayList<Dockable> list = new ArrayList<Dockable>();
		for (int i = 0; i < dockables.dockables().size(); i++){
			if (dockables.dockables().get(i).getDockable() instanceof OrientedDockStation){
				((OrientedDockStation) dockables.dockables().get(i)
						.getDockable()).setOrientation(this.orientation);
				list.add((dockables.dockables().get(i).getDockable()));
			}
		}
		fireOrientingEvent(list.toArray(new Dockable[list.size()]));
	}

	/**
	 * Gets the index of a child.
	 * 
	 * @param dockable
	 *            the child which is searched
	 * @return the index of <code>dockable</code> or -1 if it was not found
	 */
	private int indexOf( Dockable dockable ){
		for (int i = 0; i < dockables.dockables().size(); i++){
			if (dockables.dockables().get(i).getDockable() == dockable){
				return i;
			}
		}
		return -1;
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
	private void remove( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking(
				this, dockable);
		try{
			int index = indexOf(dockable);
			DockablePlaceholderList.Filter<StationChildHandle> dockables = getDockables()
					.dockables();
			listeners.fireDockableRemoving(dockable);
			dockable.setDockParent(null);
			StationChildHandle childHandle = dockables.get(index);
			dockables.remove(index);
			getContainerPanel().remove(
					childHandle.getDisplayer().getComponent());
			childHandle.destroy();

			mainPanel.getContentPane().revalidate();
			mainPanel.getContentPane().repaint();
			listeners.fireDockableRemoved(dockable);
			fireDockablesRepositioned(index);

		} finally{
			token.release();
		}
	}

	/**
	 * Add one dockable at the index position. The dockable can be a
	 * {@link ComponentDockable}, {@link ToolbarDockStation} or a
	 * {@link ToolbarGroupDockStation} (see method accept()). All the
	 * ComponentDockable extracted from the element are merged together and
	 * wrapped in a {@link ToolbarDockStation} before to be added at index
	 * position
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 * @return <code>true</code> if dropping was successfull
	 */
	protected boolean add( Dockable dockable, int index ){
		// System.out.println(this.toString()
		// + "## add( Dockable dockable, int index ) ##");
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		ToolbarStrategy strategy = getToolbarStrategy();
		if (strategy.isToolbarPart(dockable)){
			dockable = strategy.ensureToolbarLayer(this, dockable);

			DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(
					this, dockable);
			try{
				listeners.fireDockableAdding(dockable);
				dockable.setDockParent(this);
				DockablePlaceholderList.Filter<StationChildHandle> dockables = getDockables()
						.dockables();
				if (dockable instanceof OrientedDockStation){
					((OrientedDockStation) dockable)
							.setOrientation(this.orientation);
				}
				StationChildHandle handle = new StationChildHandle(this,
						displayer, dockable, title);
				dockables.add(index, handle);
				handle.updateDisplayer();
				insertAt(handle, index);
				listeners.fireDockableAdded(dockable);
				fireDockablesRepositioned(index + 1);
			} finally{
				token.release();
			}
			fireOrientingEvent(dockable);
			mainPanel.revalidate();
			mainPanel.repaint();
			return true;
		}
		return false;
	}

	private void insertAt( StationChildHandle handle, int index ){
		Dockable dockable = handle.getDockable();
		if (dockable instanceof OrientedDockStation){
			((OrientedDockStation) dockable).setOrientation(this.orientation);
		}
		dockable.setDockParent(this);
		getContainerPanel().add(handle.getDisplayer().getComponent(), index);
		mainPanel.getContentPane().revalidate();
		mainPanel.getContentPane().repaint();
	}

	/**
	 * Replaces <code>displayer</code> with a new {@link DockableDisplayer}.
	 * 
	 * @param displayer
	 *            the displayer to replace, must actually be shown on this
	 *            station
	 */
	protected void discard( DockableDisplayer displayer ){
		int index = indexOf(displayer.getDockable());
		StationChildHandle handle = getDockables().dockables().get(index);

		getContainerPanel().remove(displayer.getComponent());
		handle.updateDisplayer();
		insertAt(handle, index);
	}

	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too. It allows to draw arbitrary figures over
	 * the base panel
	 * 
	 * @author Herve Guillaume
	 */
	protected class OverpaintablePanelBase extends OverpaintablePanel{

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			containerPanel = createPanel();
			// content.setBounds( 0, 0, content.getPreferredSize().width,
			// content.getPreferredSize().height );
			// this.setPreferredSize( new Dimension(
			// content.getPreferredSize().width,
			// content.getPreferredSize().height ) );
			this.setBasePane(containerPanel);
			this.setContentPane(containerPanel);
			this.setSolid(false);
			this.getContentPane().revalidate();
			this.getContentPane().repaint();
		}

		@Override
		protected void paintOverlay( Graphics g ){
			DefaultStationPaintValue paint = getPaint();
			Rectangle rectangleAreaBeneathMouse;
			if (prepareDropDraw){
				if (indexBeneathMouse != -1){
					// WARNING: This rectangle stands for the component beneath
					// mouse. His coordinates are in the frame of reference his
					// direct parent: getPanel(areaBeneathMouse).
					// So we need to translate this rectangle in the frame of
					// reference of the overlay panel, which is the same that
					// the base pane
					Rectangle rectComponentBeneathMouse = getDockables()
							.dockables().get(indexBeneathMouse).getDisplayer()
							.getComponent().getBounds();
					// this rectangle stands for the panel which holds the
					// mouse.
					// The return rectangle is in the frame of reference of his
					// direct parent which is the content of the overlay pane
					rectangleAreaBeneathMouse = getContainerPanel().getBounds();
					// Translation
					rectComponentBeneathMouse.translate(
							rectangleAreaBeneathMouse.x,
							rectangleAreaBeneathMouse.y);
					switch (ToolbarContainerDockStation.this.getOrientation()) {
					case VERTICAL:
						int y;
						if (sideAboveMouse == Position.NORTH){
							y = rectComponentBeneathMouse.y;
						} else{
							y = rectComponentBeneathMouse.y
									+ rectComponentBeneathMouse.height;
						}
						paint.drawInsertionLine(g, rectComponentBeneathMouse.x,
								y, rectComponentBeneathMouse.x
										+ rectComponentBeneathMouse.width, y);
						break;
					case HORIZONTAL:
						int x;
						if (sideAboveMouse == Position.WEST){
							x = rectComponentBeneathMouse.x;
						} else{
							x = rectComponentBeneathMouse.x
									+ rectComponentBeneathMouse.width;
						}
						paint.drawInsertionLine(g, x,
								rectComponentBeneathMouse.y, x,
								rectComponentBeneathMouse.y
										+ rectComponentBeneathMouse.height);
					}

				} else{
					// the container pane is empty
					paint.drawDivider(g, getContainerPanel().getBounds());
				}
			}

		}

		@Override
		public String toString(){
			return this.getClass().getSimpleName() + '@'
					+ Integer.toHexString(this.hashCode());
		}

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

	@Override
	public void setController( DockController controller ){
		if (getController() != controller){
			if (getController() != null){
				unbind(dockables);
			}

			super.setController(controller);
			paint.setController(controller);
			// we catch the DockTitleManager (one by controller)
			// effect of getVersion(...): we catch the DockTitleVersion
			// associated
			// with TITLE_ID. If none exist: a new one is created and registered
			// in DockTitleManager. If no default factory is registered in the
			// version TITLE_ID, so an new one is registered (in our case a
			// BasicDockTitle.FACTORY).

			displayerFactory.setController(controller);

			if (controller == null){
				this.title = null;
			} else{
				this.title = controller.getDockTitleManager().getVersion(
						TITLE_ID, BasicDockTitleFactory.FACTORY);
			}

			displayer.setController(controller);
			placeholderStrategy.setProperties(controller);

			if (controller != null){
				bind(dockables, title);
			}
		}
	}

	private void unbind( DockablePlaceholderList<StationChildHandle> list ){
		list.unbind();
		for (StationChildHandle handle : list.dockables()){
			handle.setTitleRequest(null);
		}
	}

	private void bind( DockablePlaceholderList<StationChildHandle> list,
			DockTitleVersion title ){
		list.bind();
		for (StationChildHandle handle : list.dockables()){
			handle.setTitleRequest(title, true);
		}
	}
}