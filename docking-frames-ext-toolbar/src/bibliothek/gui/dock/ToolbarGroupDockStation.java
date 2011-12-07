package bibliothek.gui.dock;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarComplexDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.station.toolbar.layer.SideSnapDropLayer;
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.gui.dock.station.toolbar.layout.PlaceholderToolbarGridConverter;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.util.Path;

/**
 * A {@link Dockable} and a {@link DockStation} which stands a group of
 * {@link ToolbarDockStation}. As <code>Dockable</code> it can be put in
 * <code>DockStation</code> which implements marker interface
 * {@link ToolbarInterface} or in {@link ScreenDockStation}, so that a
 * <code>ToolbarDockStation</code> can be floattable. As
 * <code>DockStation</code>, it accepts a {@link ToolbarElementInterface}. If
 * the element is not a <code>ToolbarElementInterface</code>, it is wrapped in a
 * <code>ToolbarDockStation</code> before to be added.
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupDockStation extends AbstractToolbarDockStation{

	/** the id of the {@link DockTitleFactory} which is used by this station */
	public static final String TITLE_ID = "toolbar.group";
	/**
	 * This id is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	public static final String DISPLAYER_ID = "toolbar.group";

	/** A list of all children organized in columns and lines */
	private DockablePlaceholderToolbarGrid<StationChildHandle> dockables = new DockablePlaceholderToolbarGrid<StationChildHandle>();

	/** The {@link PlaceholderStrategy} that is used by {@link #dockables} */
	private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>(
			PlaceholderStrategy.PLACEHOLDER_STRATEGY){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue,
				PlaceholderStrategy newValue ){
			dockables.setStrategy(newValue);
		}
	};

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	private OverpaintablePanelBase mainPanel;

	/**
	 * Size of the border outside this station where a {@link Dockable} will
	 * still be considered to be dropped onto this station. Measured in pixel.
	 */
	private int borderSideSnapSize = 10;
	/**
	 * Whether the bounds of this station are slightly bigger than the station
	 * itself. Used together with {@link #borderSideSnapSize} to grab Dockables
	 * "out of the sky". The default is <code>true</code>.
	 */
	private boolean allowSideSnap = true;

	// ########################################################
	// ############ Initialization Managing ###################
	// ########################################################

	/**
	 * Creates a new {@link ToolbarGroupDockStation}.
	 */
	public ToolbarGroupDockStation(){
		init();
		this.mainPanel.getContentPane().setBackground(Color.YELLOW);
		this.mainPanel.getBasePane().setBackground(Color.ORANGE);
	}

	@Override
	protected void init(){
		mainPanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT
				+ ".toolbar", this);
		setOrientation(this.getOrientation());
		displayerFactory = createDisplayerFactory();
		displayers = new DisplayerCollection(this, displayerFactory,
				getDisplayerId());
		displayers
				.addDockableDisplayerListener(new DockableDisplayerListener(){
					@Override
					public void discard( DockableDisplayer displayer ){
						ToolbarGroupDockStation.this.discard(displayer);
					}
				});

		setTitleIcon(null);
	}

	// ########################################################
	// ################### Class Utilities ####################
	// ########################################################

	/**
	 * Gets the column location of the <code>dockable</code>.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the column location or -1 if the child was not found
	 */
	public int column( Dockable dockable ){
		return dockables.getColumn(dockable);
	}

	/**
	 * Gets the line location of the <code>dockable</code>.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the line location or -1 if the child was not found
	 */
	public int line( Dockable dockable ){
		return dockables.getLine(dockable);
	}

	// ########################################################
	// ############ General DockStation Managing ##############
	// ########################################################

	/**
	 * Gets the {@link ToolbarStrategy} that is currently used by this station.
	 * 
	 * @return the strategy, never <code>null</code>
	 */
	@Override
	public ToolbarStrategy getToolbarStrategy(){
		SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(
				ToolbarStrategy.STRATEGY, getController());
		ToolbarStrategy result = value.getValue();
		value.setProperties((DockController) null);
		return result;
	}

	@Override
	public Component getComponent(){
		return mainPanel;
	}

	@Override
	public int getDockableCount(){
		return dockables.size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.get(index).asDockable();
	}

	@Override
	public String getFactoryID(){
		return ToolbarDockStationFactory.ID;
	}

	@Override
	protected String getDisplayerId(){
		return DISPLAYER_ID;
	}

	/**
	 * Sets whether {@link Dockable Dockables} which are dragged near the
	 * station are captured and added to this station.
	 * 
	 * @param allowSideSnap
	 *            <code>true</code> if the station can snap Dockables which are
	 *            near.
	 * @see #setBorderSideSnapSize(int)
	 */
	public void setAllowSideSnap( boolean allowSideSnap ){
		this.allowSideSnap = allowSideSnap;
	}

	/**
	 * Tells whether the station can grab Dockables which are dragged near the
	 * station.
	 * 
	 * @return <code>true</code> if grabbing is allowed
	 * @see #setAllowSideSnap(boolean)
	 */
	public boolean isAllowSideSnap(){
		return allowSideSnap;
	}

	/**
	 * There is an invisible border around the station. If a {@link Dockable} is
	 * dragged inside this border, its considered to be on the station and will
	 * be dropped into.
	 * 
	 * @param borderSideSnapSize
	 *            the size of the border in pixel
	 * @throws IllegalArgumentException
	 *             if the size is smaller than 0
	 */
	public void setBorderSideSnapSize( int borderSideSnapSize ){
		if (borderSideSnapSize < 0)
			throw new IllegalArgumentException(
					"borderSideSnapeSize must not be less than 0");

		this.borderSideSnapSize = borderSideSnapSize;
	}

	/**
	 * Gets the size of the invisible border around the station where a dockable
	 * can be dropped.
	 * 
	 * @return the size in pixel
	 * @see #setBorderSideSnapSize(int)
	 */
	public int getBorderSideSnapSize(){
		return borderSideSnapSize;
	}

	@Override
	public void setController( DockController controller ){
		if (getController() != controller){
			if (getController() != null){
				dockables.unbind();
			}
			Iterator<StationChildHandle> iter = dockables.items();
			while (iter.hasNext()){
				iter.next().setTitleRequest(null);
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

			if (getController() != null){
				dockables.bind();
			}

			iter = dockables.items();
			while (iter.hasNext()){
				iter.next().setTitleRequest(title, true);
			}
		}
	}

	// ########################################################
	// ############ Orientation Managing ######################
	// ########################################################

	@Override
	public void setOrientation( Orientation orientation ){
		// it's very important to change position and orientation of inside
		// dockables first, else doLayout() is done on wrong inside information
		this.orientation = orientation;
		for (int i = 0; i < getDockableCount(); i++){
			Dockable d = getDockable(i);
			if (d instanceof OrientedDockStation){
				OrientedDockStation group = (OrientedDockStation) d;
				group.setOrientation(this.getOrientation());
			}
		}
		mainPanel.updateAlignment();
		mainPanel.revalidate();
		fireOrientingEvent();
	}

	// ########################################################
	// ############### Drop/Move Managing #####################
	// ########################################################

	@Override
	public DockStationDropLayer[] getLayers(){
		return new DockStationDropLayer[] { new DefaultDropLayer(this),
				new SideSnapDropLayer(this), };
	}

	@Override
	public boolean accept( Dockable child ){
		return getToolbarStrategy().isToolbarGroupPart(child);
	}

	@Override
	public boolean accept( DockStation station ){
		return getToolbarStrategy().isToolbarGroupPartParent(station, this,
				false);
	}

	@Override
	public StationDropOperation prepareDrop( int mouseX, int mouseY,
			int titleX, int titleY, Dockable dockable ){
		// System.out.println(this.toString() + "## prepareDrop(...) ##");
		DockController controller = getController();

		if (getExpandedState() == ExpandedState.EXPANDED){
			return null;
		}

		// check if the dockable and the station accept each other
		if (this.accept(dockable) & dockable.accept(this)){
			// check if controller exists and if the controller accepts that
			// the dockable becomes a child of this station
			if (controller != null){
				if (!controller.getAcceptance().accept(this, dockable)){
					return null;
				}
			}
			return new ToolbarComplexDropInfo(dockable, this, mouseX, mouseY){
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
					ToolbarGroupDockStation.this.indexBeneathMouse = -1;
					ToolbarGroupDockStation.this.sideBeneathMouse = null;
					ToolbarGroupDockStation.this.prepareDropDraw = false;
					ToolbarGroupDockStation.this.mainPanel.repaint();
				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					ToolbarGroupDockStation.this.indexBeneathMouse = indexOf(getDockableBeneathMouse());
					ToolbarGroupDockStation.this.prepareDropDraw = true;
					ToolbarGroupDockStation.this.sideBeneathMouse = this
							.getSideDockableBeneathMouse();
					// without this line, line is displayed only on the first
					// component met
					ToolbarGroupDockStation.this.mainPanel.repaint();
				}
			};
		} else{
			return null;
		}
	}

	/**
	 * Drops thanks to information collect by dropInfo.
	 * 
	 * @param dropInfo
	 */
	@Override
	protected void drop( StationDropOperation dropInfo ){
		ToolbarComplexDropInfo dropInfoGroup = (ToolbarComplexDropInfo) dropInfo;
		System.out
				.println("Summarize Info: " + dropInfoGroup.toSummaryString());
		if (dropInfoGroup.getItemPositionVSBeneathDockable() != Position.CENTER){
			// Note: Computation of index to insert drag dockable is not the
			// same between a move() and a drop(), because with a move() it is
			// as if the drag dockable were remove first then added again in the
			// list -> so the list is shrunk and the index are shifted behind
			// the remove dockable. (Note: It's weird because indeed drag() is
			// called after move()...)
			if (dropInfoGroup.isMove()){
				int column, topShift = 0, lateralShift = 0;
				if (getOrientation() == Orientation.VERTICAL){
					column = column(dropInfoGroup.getDockableBeneathMouse());
					if (dropInfoGroup.getItemPositionVSBeneathDockable() == Position.NORTH){
						// index shifted because the drag dockable is above the
						// dockable beneath mouse
						topShift = -1;
					}
					if (dropInfoGroup.getItemPositionVSBeneathDockable() == Position.WEST){
						// index shifted because the drag dockable is at the
						// left of the dockable beneath mouse
						lateralShift = -1;
					}
					switch (dropInfoGroup.getSideDockableBeneathMouse()) {

					case NORTH:
						// the drag dockable is put above the dockable beneath
						// mouse
						drop(dropInfoGroup.getItem(), column, indexBeneathMouse
								+ topShift);
						break;
					case EAST:
						// the drag dockable is put at the right of the dockable
						// beneath mouse
						drop(dropInfoGroup.getItem(), indexBeneathMouse + 1
								+ lateralShift);
						break;
					case SOUTH:
						// the drag dockable is put below the dockable beneath
						// mouse
						drop(dropInfoGroup.getItem(), column, indexBeneathMouse
								+ 1 + topShift);
						break;
					case WEST:
						// the drag dockable is put at the left of the dockable
						// beneath mouse
						drop(dropInfoGroup.getItem(), indexBeneathMouse
								+ lateralShift);
						break;
					}

				} else{

				}

			} else{

				if (getOrientation() == Orientation.VERTICAL){
					switch (dropInfoGroup.getSideDockableBeneathMouse()) {
					case NORTH:
						// the drag dockable is put above the dockable beneath
						// mouse
						drop(dropInfoGroup.getItem(),
								column(dropInfoGroup.getDockableBeneathMouse()),
								line(dropInfoGroup.getDockableBeneathMouse()));
						break;
					case EAST:
						// the drag dockable is put at the right of the dockable
						// beneath mouse
						System.out.println(column(dropInfoGroup
								.getDockableBeneathMouse()));
						drop(dropInfoGroup.getItem(),
								column(dropInfoGroup.getDockableBeneathMouse()) + 1);
						break;
					case SOUTH:
						// the drag dockable is put below the dockable beneath
						// mouse
						drop(dropInfoGroup.getItem(),
								column(dropInfoGroup.getDockableBeneathMouse()),
								line(dropInfoGroup.getDockableBeneathMouse()) + 1);
						break;
					case WEST:
						// the drag dockable is put at the left of the dockable
						// beneath mouse
						drop(dropInfoGroup.getItem(),
								column(dropInfoGroup.getDockableBeneathMouse()));
						break;
					}
				} else{
					switch (dropInfoGroup.getSideDockableBeneathMouse()) {
					case NORTH:
						// the drag dockable is put above the dockable beneath
						// mouse
						drop(dropInfoGroup.getItem(),
								column(dropInfoGroup.getDockableBeneathMouse()));
						break;
					case EAST:
						// the drag dockable is put at the right of the dockable
						// beneath mouse
						drop(dropInfoGroup.getItem(),
								column(dropInfoGroup.getDockableBeneathMouse()),
								line(dropInfoGroup.getDockableBeneathMouse()) + 1);
						break;
					case SOUTH:
						// the drag dockable is put below the dockable beneath
						// mouse
						System.out.println(column(dropInfoGroup
								.getDockableBeneathMouse()));
						drop(dropInfoGroup.getItem(),
								column(dropInfoGroup.getDockableBeneathMouse()) + 1);
						break;
					case WEST:
						// the drag dockable is put at the left of the dockable
						// beneath mouse
						drop(dropInfoGroup.getItem(),
								column(dropInfoGroup.getDockableBeneathMouse()),
								line(dropInfoGroup.getDockableBeneathMouse()));
						break;
					}
				}
			}
		}
	}

	@Override
	public void drop( Dockable dockable ){
		drop(dockable, 0, 0);
	}

	/**
	 * Drops the <code>dockable</code> at the specified line and column.
	 * 
	 * @param dockable
	 *            the dockable to insert
	 * @param column
	 *            the column where insert
	 * @param line
	 *            the line where insert
	 * @return true if the dockable has been inserted, false otherwise
	 */
	public boolean drop( Dockable dockable, int column, int line ){
		return drop(dockable, column, line, false);
	}

	public boolean drop( Dockable dockable, int column, int line, boolean force ){
		if (force || this.accept(dockable)){
			if (!force){
				dockable = getToolbarStrategy().ensureToolbarLayer(this,
						dockable);
				if (dockable == null){
					return false;
				}
			}
			add(dockable, column, line);
			return true;
		}
		return false;
	}

	private void add( Dockable dockable, int column, int line ){
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		dockable = getToolbarStrategy().ensureToolbarLayer(this, dockable);
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this,
				dockable);
		try{
			listeners.fireDockableAdding(dockable);

			dockable.setDockParent(this);
			StationChildHandle handle = createHandle(dockable);
			// add in the list of dockable
			int before = dockables.getColumnCount();
			dockables.insert(column, line, handle);

			if (dockable instanceof OrientedDockStation){
				((OrientedDockStation) dockable)
						.setOrientation(getOrientation());
			}

			// add in the main panel
			addComponent(handle);
			listeners.fireDockableAdded(dockable);
			fireDockablesRepositioned(dockable,
					before != dockables.getColumnCount());
		} finally{
			token.release();
		}
	}

	/**
	 * Creates a new {@link StationChildHandle} that wrapps around
	 * <code>dockable</code>. This method does not add the handle to any list or
	 * fire any events, that is the callers responsibility. Callers should also
	 * call {@link #addComponent(StationChildHandle)} with the new handle.
	 * 
	 * @param dockable
	 *            the element that is to be wrapped
	 * @return a new {@link StationChildHandle} for the element
	 */
	private StationChildHandle createHandle( Dockable dockable ){
		StationChildHandle handle = new StationChildHandle(this, displayers,
				dockable, title);
		handle.updateDisplayer();
		return handle;
	}

	/**
	 * Adds <code>handle</code> to the {@link #mainPanel} of this station. Note
	 * that this method only cares about the {@link Component}-{@link Container}
	 * relationship, it does not store <code>handle</code> in the
	 * {@link #dockables} list.
	 * 
	 * @param handle
	 *            the handle to add
	 */
	private void addComponent( StationChildHandle handle ){
		mainPanel.getContentPane().add(handle.getDisplayer().getComponent());
		mainPanel.getContentPane().revalidate();
	}

	/**
	 * Removes <code>handle</code> of the {@link #mainPanel} of this station.
	 * Note that this method only cares about the {@link Component}-
	 * {@link Container} relationship, it does not remove <code>handle</code> of
	 * the {@link #dockables} list.
	 * 
	 * @param handle
	 *            the handle to remove
	 */
	private void removeComponent( StationChildHandle handle ){
		mainPanel.getContentPane().remove(handle.getDisplayer().getComponent());
		mainPanel.getContentPane().revalidate();
	}

	/**
	 * Drops the <code>dockable</code> in a new column.
	 * 
	 * @param dockable
	 *            the dockable to insert
	 * @param column
	 *            the column index to create
	 * @return true if the dockable has been inserted, false otherwise
	 */
	public boolean drop( Dockable dockable, int column ){
		return drop(dockable, column, false);
	}

	public boolean drop( Dockable dockable, int column, boolean force ){
		if (force || this.accept(dockable)){
			if (!force){
				dockable = getToolbarStrategy().ensureToolbarLayer(this,
						dockable);
				if (dockable == null){
					return false;
				}
			}
			add(dockable, column);
			return true;
		}
		return false;
	}

	private void add( Dockable dockable, int column ){
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		dockable = getToolbarStrategy().ensureToolbarLayer(this, dockable);
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this,
				dockable);
		try{
			listeners.fireDockableAdding(dockable);
			dockable.setDockParent(this);
			StationChildHandle handle = createHandle(dockable);
			// add in the list of dockable
			int before = dockables.getColumnCount();
			dockables.insert(column, handle, false);

			if (dockable instanceof OrientedDockStation){
				((OrientedDockStation) dockable)
						.setOrientation(getOrientation());
			}

			// add in the main panel
			addComponent(handle);
			listeners.fireDockableAdded(dockable);
			fireDockablesRepositioned(dockable,
					before != dockables.getColumnCount());
		} finally{
			token.release();
		}
	}

	@Override
	public void drag( Dockable dockable ){
		if (dockable.getDockParent() != this){
			throw new IllegalArgumentException("not a child of this station: "
					+ dockable);
		}
		remove(dockable);
	}

	@Override
	protected void remove( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		int column = column(dockable);
		int before = dockables.getColumnCount();

		StationChildHandle handle = dockables.get(dockable);
		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking(
				this, dockable);
		try{
			listeners.fireDockableRemoving(dockable);

			dockables.remove(handle);
			removeComponent(handle);
			dockable.setDockParent(null);

			listeners.fireDockableRemoved(dockable);
			fireColumnRepositioned(column, before != dockables.getColumnCount());
		} finally{
			token.release();
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		// TODO Auto-generated method stub
		DockUtilities.checkLayoutLocked();
		DockController controller = getController();
		if (controller != null)
			controller.freezeLayout();
		int column = dockables.getColumn(old);
		int line = dockables.getLine(old);
		int beforeCount = dockables.getColumnCount();
		remove(old);
		// if remove the old dockable delete a column we have to recreate it
		if (beforeCount != dockables.getColumnCount()){
			add(next, column);
		} else{
			add(next, column, line);
		}
		controller.meltLayout();
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		return acceptable(next)
				&& getToolbarStrategy().isToolbarGroupPartParent(this, next,
						true);
	}

	/**
	 * Fires
	 * {@link DockStationListener#dockablesRepositioned(DockStation, Dockable[])}
	 * for all {@link Dockable}s that are in the same column as
	 * <code>dockable</code>, including <code>dockable</code>.
	 * 
	 * @param dockable
	 *            some item from a column that changed
	 * @param all
	 *            whether there should be an event for all the columns after
	 *            <code>dockable</code> as well
	 */
	protected void fireDockablesRepositioned( Dockable dockable ){
		fireDockablesRepositioned(dockable, false);
	}

	/**
	 * Fires
	 * {@link DockStationListener#dockablesRepositioned(DockStation, Dockable[])}
	 * for all {@link Dockable}s that are in the same column as
	 * <code>dockable</code>, including <code>dockable</code>.
	 * 
	 * @param dockable
	 *            some item from a column that changed
	 * @param all
	 *            whether there should be an event for all the columns after the
	 *            column of the dockable as well
	 */
	protected void fireDockablesRepositioned( Dockable dockable, boolean all ){
		fireColumnRepositioned(column(dockable), all);
	}

	/**
	 * Fires
	 * {@link DockStationListener#dockablesRepositioned(DockStation, Dockable[])}
	 * for all {@link Dockable}s in the given <code>column</code>.
	 * 
	 * @param column
	 *            the column for which to fire an event
	 * @param all
	 *            whether there should be an event for all the columns after
	 *            <code>column</code> as well
	 */
	protected void fireColumnRepositioned( int column, boolean all ){
		List<Dockable> list = new ArrayList<Dockable>();
		int end = all ? dockables.getColumnCount() : column + 1;

		for (int i = column; i < end; i++){
			Iterator<StationChildHandle> items = dockables.getColumnContent(i);
			while (items.hasNext()){
				list.add(items.next().getDockable());
			}
		}

		if (list.size() > 0){
			listeners.fireDockablesRepositioned(list.toArray(new Dockable[list
					.size()]));
		}
	}

	// ########################################################
	// ###################### UI Managing #####################
	// ########################################################

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme(this, new ToolbarGroupDockStationFactory());
	}

	@Override
	protected DefaultDisplayerFactoryValue createDisplayerFactory(){
		return new DefaultDisplayerFactoryValue(ThemeManager.DISPLAYER_FACTORY
				+ ".toolbar.group", this);
	}

	@Override
	protected DockTitleVersion registerTitle( DockController controller ){
		return controller.getDockTitleManager().getVersion(TITLE_ID,
				BasicDockTitleFactory.FACTORY);
	}

	/**
	 * Replaces <code>displayer</code> with a new {@link DockableDisplayer}.
	 * 
	 * @param displayer
	 *            the displayer to replace
	 * @throws IllegalArgumentException
	 *             if <code>displayer</code> is not a child of this station
	 */
	@Override
	protected void discard( DockableDisplayer displayer ){
		Dockable dockable = displayer.getDockable();

		StationChildHandle handle = dockables.get(dockable);
		if (handle == null){
			throw new IllegalArgumentException(
					"displayer is not child of this station: " + displayer);
		}

		removeComponent(handle);
		handle.updateDisplayer();
		addComponent(handle);
	}

	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too. It allows to draw arbitrary figures over
	 * the base panel
	 * 
	 * @author Herve Guillaume
	 */
	protected class OverpaintablePanelBase extends SecureContainer{

		/**
		 * Generated serial number
		 */
		private static final long serialVersionUID = -4399008463139189130L;

		/**
		 * A panel with a fixed size (minimum, maximum and preferred size have
		 * same values). Computation of the size takes insets into account.
		 * 
		 * @author Herve Guillaume
		 * 
		 */
		@SuppressWarnings("serial")
		private class SizeFixedPanel extends JPanel{
			@Override
			public Dimension getPreferredSize(){
				Dimension pref = super.getPreferredSize();
				Insets insets = getInsets();
				pref.height += insets.top + insets.bottom;
				pref.width += insets.left + insets.right;
				return pref;
			}

			@Override
			public Dimension getMaximumSize(){
				return getPreferredSize();
			}

			@Override
			public Dimension getMinimumSize(){
				return getPreferredSize();
			}
		}

		/**
		 * The content Pane of this {@link OverpaintablePanel} (with a
		 * BoxLayout)
		 */
		private JPanel dockablePane = new SizeFixedPanel();
		/**
		 * This pane is the base of this OverpaintablePanel and contains both
		 * title and content panes (with a BoxLayout)
		 */
		private JPanel basePane = new SizeFixedPanel(); // {

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			// basePane.setBackground( Color.GREEN );
			// dockablePane.setBackground( Color.RED );

			basePane.setLayout(new BorderLayout());
			basePane.add(dockablePane, BorderLayout.CENTER);
			setBasePane(basePane);
			setContentPane(dockablePane);
			this.setSolid(false);
			dockablePane.setOpaque(false);
			basePane.setOpaque(false);
		}

		@Override
		public Dimension getPreferredSize(){
			return getBasePane().getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize(){
			return getBasePane().getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize(){
			return getBasePane().getPreferredSize();
		}

		/**
		 * Update alignment with regards to the current orientation of this
		 * {@link ToolbarGroupDockStation}
		 */
		public void updateAlignment(){
			Orientation orientation = ToolbarGroupDockStation.this
					.getOrientation();

			if (orientation != null){
				dockablePane
						.setLayout(new ToolbarGridLayoutManager<StationChildHandle>(
								orientation, dockables){
							@Override
							protected Component toComponent(
									StationChildHandle item ){
								return item.getDisplayer().getComponent();
							}
						});
			}
			mainPanel.revalidate();
		}

		@Override
		protected void paintOverlay( Graphics g ){
			Graphics2D g2D = (Graphics2D) g;
			g2D.setStroke(new BasicStroke(2));
			DefaultStationPaintValue paint = getPaint();
			if (prepareDropDraw){
				if (indexBeneathMouse != -1){
					Component componentBeneathMouse = dockables
							.get(indexBeneathMouse).getDisplayer()
							.getComponent();
					if (componentBeneathMouse != null){
						Rectangle rectToolbar = basePane.getBounds();
						// Point pToolbar = rectToolbar.getLocation();
						// SwingUtilities.convertPointToScreen(pToolbar,
						// componentBeneathMouse.getParent());
						// SwingUtilities.convertPointFromScreen(pToolbar,
						// this.getBasePane());
						// Rectangle rectangleToolbarTranslated = new Rectangle(
						// pToolbar.x, pToolbar.y, rectToolbar.width,
						// rectToolbar.height);
						// Color color = new Color(0, 0, 255, 50);
						// Rectangle2D rect = new Rectangle2D.Double(
						// rectToolbar.x, rectToolbar.y,
						// rectToolbar.width, rectToolbar.height);
						// g2D.setColor(color);
						// g2D.fill(rect);
						g2D.setColor(Color.BLUE);

						Rectangle rectBeneathMouse = componentBeneathMouse
								.getBounds();
						Point pBeneath = rectBeneathMouse.getLocation();
						SwingUtilities.convertPointToScreen(pBeneath,
								componentBeneathMouse.getParent());
						SwingUtilities.convertPointFromScreen(pBeneath,
								this.getBasePane());
						Rectangle rectangleBeneathMouseTranslated = new Rectangle(
								pBeneath.x, pBeneath.y, rectBeneathMouse.width,
								rectBeneathMouse.height);
						int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
						switch (ToolbarGroupDockStation.this.getOrientation()) {
						case VERTICAL:
							switch (sideBeneathMouse) {
							case NORTH:
								x1 = rectangleBeneathMouseTranslated.x;
								x2 = rectangleBeneathMouseTranslated.x
										+ rectangleBeneathMouseTranslated.width;
								y1 = y2 = rectangleBeneathMouseTranslated.y;
								break;
							case EAST:
								x1 = x2 = rectangleBeneathMouseTranslated.x
										+ rectangleBeneathMouseTranslated.width;
								y1 = rectToolbar.y;
								y2 = rectToolbar.y + rectToolbar.height;
								break;
							case SOUTH:
								x1 = rectangleBeneathMouseTranslated.x;
								x2 = rectangleBeneathMouseTranslated.x
										+ rectangleBeneathMouseTranslated.width;
								y1 = y2 = rectangleBeneathMouseTranslated.y
										+ rectangleBeneathMouseTranslated.height;
								break;
							case WEST:
								x1 = x2 = rectangleBeneathMouseTranslated.x;
								y1 = rectToolbar.y;
								y2 = rectToolbar.y + rectToolbar.height;
								break;
							default:
								x1 = x2 = y1 = y2 = 0;
							}
							break;
						case HORIZONTAL:
							switch (sideBeneathMouse) {
							case NORTH:
								x1 = rectToolbar.x;
								x2 = rectToolbar.x + rectToolbar.width;
								y1 = y2 = rectangleBeneathMouseTranslated.y;
								break;
							case EAST:
								x1 = x2 = rectangleBeneathMouseTranslated.x
										+ rectangleBeneathMouseTranslated.width;
								y1 = rectangleBeneathMouseTranslated.y;
								y2 = rectangleBeneathMouseTranslated.y
										+ rectangleBeneathMouseTranslated.height;
								break;
							case SOUTH:
								x1 = rectToolbar.x;
								x2 = rectToolbar.x + rectToolbar.width;
								y1 = y2 = rectangleBeneathMouseTranslated.y
										+ rectangleBeneathMouseTranslated.height;
								break;
							case WEST:
								x1 = x2 = rectangleBeneathMouseTranslated.x;
								y1 = rectangleBeneathMouseTranslated.y;
								y2 = rectangleBeneathMouseTranslated.y
										+ rectangleBeneathMouseTranslated.height;
								break;
							default:
								x1 = x2 = y1 = y2 = 0;
							}

							break;
						// paint.drawInsertionLine(g, x,
						// rectangleTranslated.y, x,
						// rectangleTranslated.y
						// + rectangleTranslated.height);
						}
						g2D.drawLine(x1, y1, x2, y2);
					}
				}
			} else {
				System.out.println("DONT PAINT");
			}
		}

		@Override
		public String toString(){
			return this.getClass().getSimpleName() + '@'
					+ Integer.toHexString(this.hashCode());
		}

	}

	// ########################################################
	// ############### PlaceHolder Managing ###################
	// ########################################################

	@Override
	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}

	/**
	 * Converts this station into a {@link PlaceholderMap} using
	 * <code>identifiers</code> to remember which {@link Dockable} was at which
	 * location.
	 * 
	 * @param identifiers
	 *            the identifiers to apply
	 * @return <code>this</code> as map
	 */
	public PlaceholderMap getPlaceholders( Map<Dockable, Integer> identifiers ){
		return dockables.toMap(identifiers);
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		dockables.fromMap(placeholders);
	}

	public void setPlaceholders( PlaceholderMap placeholders,
			Map<Integer, Dockable> children ){
		DockUtilities.checkLayoutLocked();
		if (getDockableCount() > 0){
			throw new IllegalStateException("this station still has children");
		}

		DockController controller = getController();

		try{
			if (controller != null){
				controller.freezeLayout();
			}

			dockables.setStrategy(null);
			dockables.unbind();

			dockables
					.fromMap(
							placeholders,
							children,
							new PlaceholderToolbarGridConverter<Dockable, StationChildHandle>(){
								@Override
								public StationChildHandle convert(
										Dockable dockable,
										ConvertedPlaceholderListItem item ){
									listeners.fireDockableAdding(dockable);

									dockable.setDockParent(ToolbarGroupDockStation.this);
									StationChildHandle handle = createHandle(dockable);
									addComponent(handle);

									return handle;
								}

								@Override
								public void added( StationChildHandle item ){
									listeners.fireDockableAdded(item
											.getDockable());
								}
							});

			if (controller != null){
				dockables.bind();
			}
			dockables.setStrategy(placeholderStrategy.getValue());
		} finally{
			if (controller != null){
				controller.meltLayout();
			}
		}
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		int column = column(child);
		int line = line(child);

		if (target == null){
			target = child;
		}

		PlaceholderStrategy strategy = placeholderStrategy.getValue();
		Path placeholder = null;
		if (strategy != null){
			placeholder = strategy.getPlaceholderFor(target);
			if (placeholder != null && column >= 0 && line >= 0){
				dockables.insertPlaceholder(column, line, placeholder);
			}
		}

		return new ToolbarGroupProperty(column, line, placeholder);
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if (property instanceof ToolbarGroupProperty){
			return drop(dockable, (ToolbarGroupProperty) property);
		}
		return false;
	}

	/**
	 * Tries to drop <code>dockable</code> at <code>property</code>.
	 * 
	 * @param dockable
	 *            the element to drop
	 * @param property
	 *            the location of <code>dockable</code>
	 * @return <code>true</code> if dropping was successfull, <code>false</code>
	 *         otherwise
	 */
	public boolean drop( Dockable dockable, ToolbarGroupProperty property ){
		Path placeholder = property.getPlaceholder();

		int column = property.getColumn();
		int line = property.getLine();

		if (placeholder != null){
			if (dockables.hasPlaceholder(placeholder)){
				StationChildHandle child = dockables.get(placeholder);
				if (child == null){
					if (acceptable(dockable)){
						DockUtilities.checkLayoutLocked();
						DockHierarchyLock.Token token = DockHierarchyLock
								.acquireLinking(this, dockable);
						try{
							DockUtilities.ensureTreeValidity(this, dockable);
							listeners.fireDockableAdding(dockable);
							int before = dockables.getColumnCount();

							dockable.setDockParent(this);
							StationChildHandle handle = createHandle(dockable);
							dockables.put(placeholder, handle);
							addComponent(handle);

							listeners.fireDockableAdded(dockable);
							fireDockablesRepositioned(dockable,
									before != dockables.getColumnCount());
						} finally{
							token.release();
						}
						return true;
					}
				} else{
					if (drop(child, dockable, property)){
						return true;
					}

					column = dockables.getColumn(child.getDockable());
					line = dockables.getLine(column, child.getDockable()) + 1;
				}
			}
		}

		if (!acceptable(dockable)){
			return false;
		}

		return drop(dockable, column, line);
	}

	@SuppressWarnings("static-method")
	private boolean drop( StationChildHandle parent, Dockable child,
			ToolbarGroupProperty property ){
		if (property.getSuccessor() == null){
			return false;
		}

		DockStation station = parent.getDockable().asDockStation();
		if (station == null){
			return false;
		}

		return station.drop(child, property.getSuccessor());
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		if (property instanceof ToolbarGroupProperty){
			move(dockable, (ToolbarGroupProperty) property);
		}
	}

	private void move( Dockable dockable, ToolbarGroupProperty property ){
		int sourceColumn = column(dockable);
		int sourceLine = line(dockable);

		boolean empty = false;
		int destinationColumn = property.getColumn();
		int destinationLine = property.getLine();

		Path placeholder = property.getPlaceholder();
		if (placeholder != null){
			int column = dockables.getColumn(placeholder);
			if (column != -1){
				int line = dockables.getLine(column, placeholder);
				if (line != -1){
					empty = true;
					destinationColumn = column;
					destinationLine = line;
				}
			}
		}

		if (!empty){
			// ensure destination valid
			destinationColumn = Math.min(destinationColumn,
					dockables.getColumnCount());
			if (destinationColumn == dockables.getColumnCount()
					|| destinationColumn == -1){
				destinationLine = 0;
			} else{
				destinationLine = Math.min(destinationLine,
						dockables.getLineCount(destinationColumn));
			}
		}

		Level level;
		if (empty){
			level = Level.BASE;
		} else{
			level = Level.DOCKABLE;
		}
		dockables.move(sourceColumn, sourceLine, destinationColumn,
				destinationLine, level);
		mainPanel.getContentPane().revalidate();
	}
}
