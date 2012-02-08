package bibliothek.gui.dock;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.crypto.KeySelector.Purpose;

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
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumn;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModel;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupExpander;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.station.toolbar.layer.DefaultDropLayerComplex;
import bibliothek.gui.dock.station.toolbar.layer.SideSnapDropLayerComplex;
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.gui.dock.station.toolbar.layout.PlaceholderToolbarGridConverter;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockActionSource;
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
public class ToolbarGroupDockStation extends AbstractToolbarDockStation {

	/** the id of the {@link DockTitleFactory} which is used by this station */
	public static final String TITLE_ID = "toolbar.group";
	/**
	 * This id is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	public static final String DISPLAYER_ID = "toolbar.group";

	/** A list of all children organized in columns and lines */
	private final DockablePlaceholderToolbarGrid<StationChildHandle> dockables = new DockablePlaceholderToolbarGrid<StationChildHandle>();

	/** Responsible for managing the {@link ExpandedState} of the children */
	private ToolbarGroupExpander expander;

	/** The {@link PlaceholderStrategy} that is used by {@link #dockables} */
	private final PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>(
			PlaceholderStrategy.PLACEHOLDER_STRATEGY){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue,
				PlaceholderStrategy newValue ){
			dockables.setStrategy( newValue );
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

	/**
	 * The {@link LayoutManager} that is currently used to set the location and size of all children. Can be
	 * <code>null</code> if no {@link #setOrientation(Orientation) orientation} is set.
	 */
	private ToolbarGridLayoutManager<StationChildHandle> layoutManager;
	
	// ########################################################
	// ############ Initialization Managing ###################
	// ########################################################

	/**
	 * Creates a new {@link ToolbarGroupDockStation}.
	 */
	public ToolbarGroupDockStation(){
		init();
	}

	@Override
	protected void init(){
		mainPanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT
				+ ".toolbar", this);
		setOrientation( getOrientation() );
		displayerFactory = createDisplayerFactory();
		displayers = new DisplayerCollection(this, displayerFactory,
				getDisplayerId());
		displayers
				.addDockableDisplayerListener(new DockableDisplayerListener(){
			@Override
			public void discard( DockableDisplayer displayer ){
				ToolbarGroupDockStation.this.discard( displayer );
			}
		} );

		setTitleIcon( null );
		expander = new ToolbarGroupExpander( this );
	}

	// ########################################################
	// ################### Class Utilities ####################
	// ########################################################

	/**
	 * Gets access to a simplified view of the contents of this station.
	 * 
	 * @return a model describing all the columns that are shown on this station
	 */
	public ToolbarColumnModel<StationChildHandle> getColumnModel(){
		return dockables.getModel();
	}

	/**
	 * Gets the column location of the <code>dockable</code>.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the column location or -1 if the child was not found
	 */
	public int column( Dockable dockable ){
		return dockables.getColumn( dockable );
	}

	/**
	 * Gets the line location of the <code>dockable</code>.
	 * 
	 * @param dockable
	 *            the {@link Dockable} to search
	 * @return the line location or -1 if the child was not found
	 */
	public int line( Dockable dockable ){
		return dockables.getLine( dockable );
	}

	/**
	 * Gets the number of column of <code>this</code>.
	 * 
	 * @return the number of column
	 */
	public int columnCount(){
		return dockables.getColumnCount(); // column(getDockable(getDockableCount()
											// - 1)) + 1;
	}

	/**
	 * Gets the number of lines in <code>column</code>.
	 * 
	 * @param column
	 *            the column
	 * @return the number of lines in <code>column</code>
	 */
	public int lineCount( int column ){
		return dockables.getLineCount( column );
	}

	/**
	 * Gets the dockable at the specified <code>column</code> and
	 * <code>line</code>.
	 * 
	 * @param columnIndex
	 *            the column index
	 * @param line
	 *            the line index
	 * @return the dockable or <code>null</code> if there's no dockable at the
	 *         specified indexes.
	 */
	public Dockable getDockable( int columnIndex, int line ){
		StationChildHandle handle = getHandle( columnIndex, line );
		if( handle == null ) {
			return null;
		}
		return handle.asDockable();
	}

	/**
	 * Gets the {@link StationChildHandle} which is used at the given position.
	 * 
	 * @param columnIndex
	 *            the column in which to search
	 * @param line
	 *            the line in the column
	 * @return the item or <code>null</code> if the indices are out of bounds
	 */
	public StationChildHandle getHandle( int columnIndex, int line ){
		ToolbarColumnModel<StationChildHandle> model = getColumnModel();
		if( columnIndex < 0 || columnIndex >= model.getColumnCount() ) {
			return null;
		}
		ToolbarColumn<StationChildHandle> column = model.getColumn( columnIndex );
		if( line < 0 || line >= column.getDockableCount() ) {
			return null;
		}
		return column.getItem( line );
	}

	/**
	 * Tells if <code>dockable</code> is the last dockable in its column.
	 * 
	 * @param dockable
	 *            the dockable
	 * @return true if the dockable it's the last in its column, false if it's
	 *         not or if it doesn't belong to <code>this</code> dockstation.
	 */
	public boolean isLastOfColumn( Dockable dockable ){
		int index = indexOf( dockable );
		int column = column( dockable );
		if( index == (getDockableCount() - 1) ) {
			return true;
		} else if (column(getDockable(index + 1)) != column){
			return true;
		} else{
			return false;
		}
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
		final SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(
				ToolbarStrategy.STRATEGY, getController());
		final ToolbarStrategy result = value.getValue();
		value.setProperties( (DockController) null );
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
		return dockables.get( index ).asDockable();
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
		if( borderSideSnapSize < 0 ) {
			throw new IllegalArgumentException(
					"borderSideSnapeSize must not be less than 0");
		}

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
		if( getController() != controller ) {
			if( getController() != null ) {
				dockables.unbind();
			}
			Iterator<StationChildHandle> iter = dockables.items();
			while( iter.hasNext() ) {
				iter.next().setTitleRequest( null );
			}

			super.setController( controller );
			// if not set controller of the DefaultStationPaintValue, call to
			// DefaultStationPaintValue do nothing

			if( controller == null ) {
				title = null;
			} else{
				title = registerTitle( controller );
			}

			paint.setController( controller );
			expander.setController( controller );
			placeholderStrategy.setProperties( controller );
			displayerFactory.setController( controller );
			displayers.setController( controller );
			mainPanel.setController( controller );

			if( getController() != null ) {
				dockables.bind();
			}

			iter = dockables.items();
			while( iter.hasNext() ) {
				iter.next().setTitleRequest( title, true );
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
		fireOrientingEvent();
		mainPanel.updateAlignment();
		mainPanel.revalidate();
	}

	// ########################################################
	// ############### Drop/Move Managing #####################
	// ########################################################

	@Override
	public DockStationDropLayer[] getLayers(){
		return new DockStationDropLayer[]{ new DefaultDropLayerComplex( this ), new SideSnapDropLayerComplex( this ) };
		// return new DockStationDropLayer[] { new DefaultDropLayer(this),
		// new SideSnapDropLayer(this) };
	}

	@Override
	public boolean accept( Dockable child ){
		return getToolbarStrategy().isToolbarGroupPart( child );
	}

	@Override
	public boolean accept( DockStation station ){
		return getToolbarStrategy().isToolbarGroupPartParent(station, this,
				false);
	}

	public boolean accept( DockStation base, Dockable neighbor ){
		return false;
	}

	@Override
	public StationDropOperation prepareDrop( int mouseX, int mouseY,
			int titleX, int titleY, Dockable dockable ){
		// System.out.println(this.toString() + "## prepareDrop(...) ##");
		final DockController controller = getController();

		if( getExpandedState() == ExpandedState.EXPANDED ) {
			return null;
		}

		// check if the dockable and the station accept each other
		if( this.accept( dockable ) & dockable.accept( this ) ) {
			// check if controller exists and if the controller accepts that
			// the dockable becomes a child of this station
			if( controller != null ) {
				if( !controller.getAcceptance().accept( this, dockable ) ) {
					return null;
				}
			}
			return new ToolbarGroupDropInfo( dockable, this, mouseX, mouseY ){
				@Override
				public void execute(){
					drop( this );
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
					mainPanel.repaint();
				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					ToolbarGroupDockStation.this.indexBeneathMouse = indexOf( getDockableBeneathMouse() );
					ToolbarGroupDockStation.this.prepareDropDraw = true;
					ToolbarGroupDockStation.this.sideBeneathMouse = getSideDockableBeneathMouse();
					// without this line, line is displayed only on the first
					// component met
					mainPanel.repaint();
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
		final ToolbarGroupDropInfo dropInfoGroup = (ToolbarGroupDropInfo) dropInfo;
		// System.out
		// .println("Summarize Info: " + dropInfoGroup.toSummaryString());
		if( dropInfoGroup.getItemPositionVSBeneathDockable() != Position.CENTER ) {
			// Note: Computation of index to insert drag dockable is not the
			// same between a move() and a drop(), because with a move() it is
			// as if the drag dockable were remove first then added again in the
			// list -> so the list is shrunk and the index are shifted behind
			// the remove dockable. (Note: It's weird because indeed drag() is
			// called after move()...)
			if( dropInfoGroup.isMove() ) {
				int column, topShift = 0, lateralShift = 0;
				if( getOrientation() == Orientation.VERTICAL ) {
					column = column( dropInfoGroup.getDockableBeneathMouse() );
					if( dropInfoGroup.getItemPositionVSBeneathDockable() == Position.NORTH ) {
						// index shifted because the drag dockable is above the
						// dockable beneath mouse
						topShift = -1;
					}
					if( dropInfoGroup.getItemPositionVSBeneathDockable() == Position.WEST ) {
						// index shifted because the drag dockable is at the
						// left of the dockable beneath mouse
						lateralShift = -1;
					}
					switch( dropInfoGroup.getSideDockableBeneathMouse() ){

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
					column = column( dropInfoGroup.getDockableBeneathMouse() );
					if( dropInfoGroup.getItemPositionVSBeneathDockable() == Position.NORTH ) {
						// index shifted because the drag dockable is above the
						// dockable beneath mouse
						topShift = -1;
					}
					if( dropInfoGroup.getItemPositionVSBeneathDockable() == Position.WEST ) {
						// index shifted because the drag dockable is at the
						// left of the dockable beneath mouse
						lateralShift = -1;
					}
					switch( dropInfoGroup.getSideDockableBeneathMouse() ){

						case NORTH:
							// the drag dockable is put above the dockable beneath
							// mouse
						drop(dropInfoGroup.getItem(), indexBeneathMouse
								+ lateralShift);
							break;
						case EAST:
							// the drag dockable is put at the right of the dockable
							// beneath mouse
						drop(dropInfoGroup.getItem(), column, indexBeneathMouse
								+ 1 + topShift);
							break;
						case SOUTH:
							// the drag dockable is put below the dockable beneath
							// mouse
						drop(dropInfoGroup.getItem(), indexBeneathMouse + 1
								+ lateralShift);
							break;
						case WEST:
							// the drag dockable is put at the left of the dockable
							// beneath mouse
						drop(dropInfoGroup.getItem(), column, indexBeneathMouse
								+ topShift);
							break;
					}
				}

			} else{

				if( getOrientation() == Orientation.VERTICAL ) {
					switch( dropInfoGroup.getSideDockableBeneathMouse() ){
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
					switch( dropInfoGroup.getSideDockableBeneathMouse() ){
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
		drop( dockable, 0, 0 );
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
		return drop( dockable, column, line, false );
	}

	public boolean drop( Dockable dockable, int column, int line, boolean force ){
		if( force || this.accept( dockable ) ) {
			if( !force ) {
				Dockable replacement = getToolbarStrategy().ensureToolbarLayer(
						this, dockable);
				if( replacement == null ) {
					return false;
				}
				if( replacement != dockable ){
					replacement.asDockStation().drop( dockable );
					dockable = replacement;
				}
			}
			add( dockable, column, line );
			return true;
		}
		return false;
	}

	private void add( Dockable dockable, int column, int line ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		Dockable replacement = getToolbarStrategy().ensureToolbarLayer(this,
				dockable);
		if( replacement != dockable ){
			replacement.asDockStation().drop( dockable );
			dockable = replacement;
		}
		
		final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(
				this, dockable);
		try {
			listeners.fireDockableAdding( dockable );

			dockable.setDockParent( this );
			final StationChildHandle handle = createHandle( dockable );
			// add in the list of dockable
			final int before = dockables.getColumnCount();
			dockables.insert( column, line, handle );
			// add in the main panel
			addComponent( handle );
			listeners.fireDockableAdded( dockable );
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
		final StationChildHandle handle = new StationChildHandle(this,
				displayers, dockable, title);
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
		mainPanel.getContentPane().add( handle.getDisplayer().getComponent() );
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
		mainPanel.getContentPane().remove( handle.getDisplayer().getComponent() );
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
		return drop( dockable, column, false );
	}

	public boolean drop( Dockable dockable, int column, boolean force ){
		if( force || this.accept( dockable ) ) {
			if( !force ) {
				Dockable replacement = getToolbarStrategy().ensureToolbarLayer(
						this, dockable);
				if( replacement == null ) {
					return false;
				}
				if( replacement != dockable ){
					replacement.asDockStation().drop( dockable );
					dockable = replacement;
				}
			}
			add( dockable, column );
			return true;
		}
		return false;
	}

	private void add( Dockable dockable, int column ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		Dockable replacement = getToolbarStrategy().ensureToolbarLayer(this,
				dockable);
		if( replacement != dockable ){
			replacement.asDockStation().drop( dockable );
			dockable = replacement;
		}
		final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(
				this, dockable);
		try {
			listeners.fireDockableAdding( dockable );
			dockable.setDockParent( this );
			final StationChildHandle handle = createHandle( dockable );
			// add in the list of dockable
			final int before = dockables.getColumnCount();
			dockables.insert( column, handle, false );
			// add in the main panel
			addComponent( handle );
			listeners.fireDockableAdded( dockable );
			fireDockablesRepositioned(dockable,
					before != dockables.getColumnCount());
		} finally{
			token.release();
		}
	}

	@Override
	public void drag( Dockable dockable ){
		if( dockable.getDockParent() != this ) {
			throw new IllegalArgumentException("not a child of this station: "
					+ dockable);
		}
		remove( dockable );
	}

	@Override
	protected void remove( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		final int column = column( dockable );
		final int before = dockables.getColumnCount();

		final StationChildHandle handle = dockables.get( dockable );
		final DockHierarchyLock.Token token = DockHierarchyLock
				.acquireUnlinking(this, dockable);
		try {
			listeners.fireDockableRemoving( dockable );

			dockables.remove( handle );
			removeComponent( handle );
			handle.destroy();
			dockable.setDockParent( null );

			listeners.fireDockableRemoved( dockable );
			fireColumnRepositioned( column, before != dockables.getColumnCount() );
			mainPanel.repaint(); // if we don't call repaint, the station is
			// not repaint when we remove a ToolbarDockStation in a column and
			// that
			// it doesn't affect the number of column
		} finally{
			token.release();
		}
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		// TODO Auto-generated method stub
		DockUtilities.checkLayoutLocked();
		final DockController controller = getController();
		if( controller != null ) {
			controller.freezeLayout();
		}
		final int column = dockables.getColumn( old );
		final int line = dockables.getLine( old );
		final int beforeCount = dockables.getColumnCount();
		remove( old );
		// if remove the old dockable delete a column we have to recreate it
		if( beforeCount != dockables.getColumnCount() ) {
			add( next, column );
		} else{
			add( next, column, line );
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
		fireDockablesRepositioned( dockable, false );
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
		fireColumnRepositioned( column( dockable ), all );
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
		final List<Dockable> list = new ArrayList<Dockable>();
		final int end = all ? dockables.getColumnCount() : column + 1;

		for( int i = column; i < end; i++ ) {
			final Iterator<StationChildHandle> items = dockables
					.getColumnContent(i);
			while( items.hasNext() ) {
				list.add( items.next().getDockable() );
			}
		}

		if( list.size() > 0 ) {
			listeners.fireDockablesRepositioned(list.toArray(new Dockable[list
					.size()]));
		}
	}

	// ########################################################
	// ###################### UI Managing #####################
	// ########################################################

	/**
	 * Gets a {@link ColumnDockActionSource} which allows to modify the
	 * {@link ExpandedState} of the children of this station.
	 * 
	 * @return the source, not <code>null</code>
	 */
	public ColumnDockActionSource getExpandActionSource(){
		return expander.getActions();
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme( this, new ToolbarGroupDockStationFactory() );
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
		final Dockable dockable = displayer.getDockable();

		final StationChildHandle handle = dockables.get( dockable );
		if( handle == null ) {
			throw new IllegalArgumentException(
					"displayer is not child of this station: " + displayer);
		}

		removeComponent( handle );
		handle.updateDisplayer();
		addComponent( handle );
	}

	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too. It allows to draw arbitrary figures over
	 * the base panel
	 * 
	 * @author Herve Guillaume
	 */
	protected class OverpaintablePanelBase extends SecureContainer {

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
		private class SizeFixedPanel extends JPanel {
			@Override
			public Dimension getPreferredSize(){
				final Dimension pref = super.getPreferredSize();
				final Insets insets = getInsets();
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
		 * The content Pane of this {@link OverpaintablePanel}.
		 */
		private final JPanel dockablePane = new SizeFixedPanel();
		/**
		 * This pane is the base of this OverpaintablePanel.
		 */
		private final JPanel basePane = new SizeFixedPanel(); // {

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			basePane.setLayout( new BorderLayout() );
			basePane.add( dockablePane, BorderLayout.CENTER );
			setBasePane( basePane );
			setContentPane( dockablePane );
			setSolid( false );
			dockablePane.setOpaque( false );
			basePane.setOpaque( false );
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
			final Orientation orientation = getOrientation();

			if( orientation != null ) {
				dockablePane.setLayout( new ToolbarGridLayoutManager<StationChildHandle>( orientation, dockables ){
				dockablePane
						.setLayout(new ToolbarGridLayoutManager<StationChildHandle>(
								orientation, dockables){
				layoutManager = new ToolbarGridLayoutManager<StationChildHandle>( dockablePane, orientation, dockables ){
					@Override
							protected Component toComponent(
									StationChildHandle item ){
						return item.getDisplayer().getComponent();
					}
				};
				dockablePane.setLayout( layoutManager );
			}
			mainPanel.revalidate();
		}

		@Override
		protected void paintOverlay( Graphics g ){
			final Graphics2D g2D = (Graphics2D) g;
			g2D.setStroke( new BasicStroke( 2 ) );
			final int localIndexBeneathMouse = indexBeneathMouse;
			final Position localSideBeneathMouse = sideBeneathMouse;
			final DefaultStationPaintValue paint = getPaint();
			if( prepareDropDraw ) {
				if( localIndexBeneathMouse != -1 ) {
					final Dockable dockableBeneathMouse = getDockable( localIndexBeneathMouse );
					final Component componentBeneathMouse = dockableBeneathMouse
							.getComponent();
					if( componentBeneathMouse != null ) {
						final Rectangle rectToolbar = basePane.getBounds();
						final Rectangle rectBeneathMouse = componentBeneathMouse
								.getBounds();
						final Point pBeneath = rectBeneathMouse.getLocation();
						SwingUtilities.convertPointToScreen(pBeneath,
								componentBeneathMouse.getParent());
						SwingUtilities.convertPointFromScreen(pBeneath,
								getBasePane());
						final Rectangle rectangleBeneathMouseTranslated = new Rectangle(
								pBeneath.x, pBeneath.y, rectBeneathMouse.width,
								rectBeneathMouse.height);
						int x1 = 0, y1 = 0, x2 = 0, y2 = 0;

						switch( getOrientation() ){
							case VERTICAL:
								switch( localSideBeneathMouse ){
									case NORTH:
										x1 = rectangleBeneathMouseTranslated.x;
								x2 = rectangleBeneathMouseTranslated.x
										+ rectangleBeneathMouseTranslated.width;
										y1 = y2 = rectangleBeneathMouseTranslated.y;
										// the y value is slightly modified to allow to
										// draw the insertion lines with a proper larger
										// (otherwise, part of the insertion line falls
										// outside of the overlay pane and can't be
										// drawn)
										if( line( dockableBeneathMouse ) == 0 ) {
											y1 = y2 = y1 + 1;
										}
										break;
									case SOUTH:
										x1 = rectangleBeneathMouseTranslated.x;
								x2 = rectangleBeneathMouseTranslated.x
										+ rectangleBeneathMouseTranslated.width;
								y1 = y2 = rectangleBeneathMouseTranslated.y
										+ rectangleBeneathMouseTranslated.height;
										// the y value is slightly modified to allow to
										// draw the insertion lines with a proper larger
										// (otherwise, part of the insertion line falls
										// outside of the overlay pane and can't be
										// drawn)
										if( isLastOfColumn( dockableBeneathMouse ) ) {
											y1 = y2 = y1 - 2;
										}
										break;
									case EAST:
								x1 = x2 = rectangleBeneathMouseTranslated.x
										+ rectangleBeneathMouseTranslated.width;
										// the x value is slightly modified to allow to
										// draw the insertion lines with a proper larger
										// (otherwise, part of the insertion line falls
										// outside of the overlay pane and can't be
										// drawn)

								// System.out
								// .println("EAST: "
								// + "indexBeneathMouse :"
								// + localIndexBeneathMouse
								// + "Column :"
								// +
								// column(getDockable(localIndexBeneathMouse)));
										if( column( dockableBeneathMouse ) == (columnCount() - 1) ) {
											x1 = x2 = x1 - 2;
										}

										// we look at the longest column near the
										// insertion lines to decide what length the
										// lines should have
										y1 = rectToolbar.y;
										int column = column( dockableBeneathMouse );
										if( column == (columnCount() - 1) ) {
											// if column is the last, we take into
											// account the last dockable
									Component lastComponent = getDockable(
											getDockableCount() - 1)
											.getComponent();
									Point bottomRightPoint = new Point(
											(int) lastComponent.getBounds()
													.getMaxX(),
											(int) lastComponent.getBounds()
													.getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomRightPoint,
											lastComponent.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomRightPoint, getBasePane());
											y2 = bottomRightPoint.y;
								} else{
									Component lastComponentLeft = getDockable(
											column, lineCount(column) - 1)
											.getComponent();
									Point bottomPointLeft = new Point(
											(int) lastComponentLeft.getBounds()
													.getMaxX(),
											(int) lastComponentLeft.getBounds()
													.getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomPointLeft,
											lastComponentLeft.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomPointLeft, getBasePane());

									Component lastComponentRight = getDockable(
											column + 1,
											lineCount(column + 1) - 1)
											.getComponent();
									Point bottomPointRight = new Point(
											(int) lastComponentRight
													.getBounds().getMaxX(),
											(int) lastComponentRight
													.getBounds().getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomPointRight,
											lastComponentRight.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomPointRight, getBasePane());

											if( bottomPointLeft.y > bottomPointRight.y ) {
												y2 = bottomPointLeft.y;
									} else{
												y2 = bottomPointRight.y;
											}
										}
										break;
									case WEST:
										x1 = x2 = rectangleBeneathMouseTranslated.x;
										// the x value is slightly modified to allow to
										// draw the insertion lines with a proper larger
										// (otherwise, part of the insertion line falls
										// outside of the overlay pane and can't be
										// drawn)
										if( column( dockableBeneathMouse ) == 0 ) {
											x1 = x2 = x1 + 2;
										}

								// System.out
								// .println("WEST: "
								// + "indexBeneathMouse :"
								// + localIndexBeneathMouse
								// + "Column :"
								// +
								// column(getDockable(localIndexBeneathMouse)));

										// we look at the longest column near the
										// insertion lines to decide what length the
										// lines should have
										y1 = rectToolbar.y;
										column = column( dockableBeneathMouse );
										if( column == 0 ) {
											// if column is the first, we take into
											// account the last dockable of the first
											// column
									Component lastComponent = getDockable(0,
											lineCount(0) - 1).getComponent();
									Point bottomRightPoint = new Point(
											(int) lastComponent.getBounds()
													.getMaxX(),
											(int) lastComponent.getBounds()
													.getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomRightPoint,
											lastComponent.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomRightPoint, getBasePane());
											y2 = bottomRightPoint.y;
								} else{
									Component lastComponentLeft = getDockable(
											column, lineCount(column) - 1)
											.getComponent();
									Point bottomPointLeft = new Point(
											(int) lastComponentLeft.getBounds()
													.getMaxX(),
											(int) lastComponentLeft.getBounds()
													.getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomPointLeft,
											lastComponentLeft.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomPointLeft, getBasePane());

									Component lastComponentRight = getDockable(
											column + 1,
											lineCount(column + 1) - 1)
											.getComponent();
									Point bottomPointRight = new Point(
											(int) lastComponentRight
													.getBounds().getMaxX(),
											(int) lastComponentRight
													.getBounds().getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomPointRight,
											lastComponentRight.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomPointRight, getBasePane());

											if( bottomPointLeft.y > bottomPointRight.y ) {
												y2 = bottomPointLeft.y;
									} else{
												y2 = bottomPointRight.y;
											}
										}
										break;
									default:
										x1 = x2 = y1 = y2 = 0;
										break;
								}
								break;
							case HORIZONTAL:
							// System.out.println("HORIZONTAL");
								switch( localSideBeneathMouse ){
									case NORTH:
								// System.out.println("NORTH");
										y1 = y2 = rectangleBeneathMouseTranslated.y;
										// the y value is slightly modified to allow to
										// draw the insertion lines with a proper larger
										// (otherwise, part of the insertion line falls
										// outside of the overlay pane and can't be
										// drawn)
										if( column( getDockable( localIndexBeneathMouse ) ) == 0 ) {
											y1 = y2 = y1 + 1;
										}

										// we look at the longest column near the
										// insertion lines to decide what length the
										// lines should have
										x1 = rectToolbar.x;
										int column = column( dockableBeneathMouse );
										if( column == 0 ) {
											// if column is the first, we take into
											// account the last dockable of the first
											// column
									Component lastComponent = getDockable(0,
											lineCount(0) - 1).getComponent();
									Point bottomRightPoint = new Point(
											(int) lastComponent.getBounds()
													.getMaxX(),
											(int) lastComponent.getBounds()
													.getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomRightPoint,
											lastComponent.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomRightPoint, getBasePane());
											x2 = bottomRightPoint.x;
								} else{
											// otherwise we take into account the
											// tallest of the two side columns
									Component lastComponentLeft = getDockable(
											column, lineCount(column) - 1)
											.getComponent();
									Point bottomPointLeft = new Point(
											(int) lastComponentLeft.getBounds()
													.getMaxX(),
											(int) lastComponentLeft.getBounds()
													.getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomPointLeft,
											lastComponentLeft.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomPointLeft, getBasePane());

									Component lastComponentRight = getDockable(
											column + 1,
											lineCount(column + 1) - 1)
											.getComponent();
									Point bottomPointRight = new Point(
											(int) lastComponentRight
													.getBounds().getMaxX(),
											(int) lastComponentRight
													.getBounds().getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomPointRight,
											lastComponentRight.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomPointRight, getBasePane());

											if( bottomPointLeft.x > bottomPointRight.x ) {
												x2 = bottomPointLeft.x;
									} else{
												x2 = bottomPointRight.x;
											}
										}
										break;
									case EAST:
								// System.out.println("EAST");
								x1 = x2 = rectangleBeneathMouseTranslated.x
										+ rectangleBeneathMouseTranslated.width;
										y1 = rectangleBeneathMouseTranslated.y;
								y2 = rectangleBeneathMouseTranslated.y
										+ rectangleBeneathMouseTranslated.height;
										// the x value is slightly modified to allow to
										// draw the insertion lines with a proper larger
										// (otherwise, part of the insertion line falls
										// outside of the overlay pane and can't be
										// drawn)
										if( isLastOfColumn( dockableBeneathMouse ) ) {
											x1 = x2 = x1 - 2;
										}
										break;
									case SOUTH:
								// System.out.println("SOUTH");
								y1 = y2 = rectangleBeneathMouseTranslated.y
										+ rectangleBeneathMouseTranslated.height;
										// the y value is slightly modified to allow to
										// draw the insertion lines with a proper larger
										// (otherwise, part of the insertion line falls
										// outside of the overlay pane and can't be
										// drawn)
										if( column( dockableBeneathMouse ) == (columnCount() - 1) ) {
											y1 = y2 = y1 - 2;
										}

										// we look at the longest column near the
										// insertion lines to decide what length the
										// lines should have
										x1 = rectToolbar.x;
										column = column( dockableBeneathMouse );
										if( column == (columnCount() - 1) ) {
											// if column is the last, we take into
											// account the last dockable
									Component lastComponent = getDockable(
											getDockableCount() - 1)
											.getComponent();
									Point bottomRightPoint = new Point(
											(int) lastComponent.getBounds()
													.getMaxX(),
											(int) lastComponent.getBounds()
													.getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomRightPoint,
											lastComponent.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomRightPoint, getBasePane());
											x2 = bottomRightPoint.x;
								} else{
											// debug
									// System.out.println("Column: " + column);
									// System.out.println("Line count: "
									// + lineCount(column));
									// Dockable d = getDockable(column,
									// lineCount(column) - 1);
									// Component c = d.getComponent();
									// Rectangle r = c.getBounds();
											// end debug
									Component lastComponentLeft = getDockable(
											column, lineCount(column) - 1)
											.getComponent();
									Point bottomPointLeft = new Point(
											(int) lastComponentLeft.getBounds()
													.getMaxX(),
											(int) lastComponentLeft.getBounds()
													.getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomPointLeft,
											lastComponentLeft.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomPointLeft, getBasePane());
											// debug
									// System.out.println("Column + 1: "
									// + (column + 1));
									// System.out.println("Line count: "
									// + lineCount(column + 1));
									// d = getDockable(column + 1,
									// lineCount(column + 1) - 1);
									// c = d.getComponent();
									// r = c.getBounds();
											// end debug
									Component lastComponentRight = getDockable(
											column + 1,
											lineCount(column + 1) - 1)
											.getComponent();
									Point bottomPointRight = new Point(
											(int) lastComponentRight
													.getBounds().getMaxX(),
											(int) lastComponentRight
													.getBounds().getMaxY());
									SwingUtilities.convertPointToScreen(
											bottomPointRight,
											lastComponentRight.getParent());
									SwingUtilities.convertPointFromScreen(
											bottomPointRight, getBasePane());

											if( bottomPointLeft.x > bottomPointRight.x ) {
												x2 = bottomPointLeft.x;
									} else{
												x2 = bottomPointRight.x;
											}
										}
										break;
									case WEST:
								// System.out.println("WEST");
										x1 = x2 = rectangleBeneathMouseTranslated.x;
										y1 = rectangleBeneathMouseTranslated.y;
								y2 = rectangleBeneathMouseTranslated.y
										+ rectangleBeneathMouseTranslated.height;
										// the x value is slightly modified to allow to
										// draw the insertion lines with a proper larger
										// (otherwise, part of the insertion line falls
										// outside of the overlay pane and can't be
										// drawn)
										// if (line(dockableBeneathMouse) == 0){
										// x1 = x2 = x1 + 1;
										// }
										break;
									default:
										x1 = x2 = y1 = y2 = 0;
										break;
								}
								break;
						}
						paint.drawInsertionLine( g, x1, y1, x2, y2 );
					}
				}
			}
		}

		@Override
		public String toString(){
			return this.getClass().getSimpleName() + '@'
					+ Integer.toHexString(hashCode());
		}

	}

	// ########################################################
	// ############### PlaceHolder Managing ###################
	// ########################################################

	@Override
	public PlaceholderMap getPlaceholders(){
		PlaceholderMap map = dockables.toMap();
		writeLayoutArguments( map );
		return map;
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
		PlaceholderMap map = dockables.toMap( identifiers );
		writeLayoutArguments( map );
		return map;
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		dockables.fromMap( placeholders );
		readLayoutArguments( placeholders );
	}

	public void setPlaceholders( PlaceholderMap placeholders,
			Map<Integer, Dockable> children ){
		DockUtilities.checkLayoutLocked();
		if( getDockableCount() > 0 ) {
			throw new IllegalStateException( "this station still has children" );
		}

		final DockController controller = getController();
		readLayoutArguments( placeholders );
		
		try {
			if( controller != null ) {
				controller.freezeLayout();
			}

			dockables.setStrategy( null );
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
					listeners.fireDockableAdding( dockable );

					dockable.setDockParent( ToolbarGroupDockStation.this );
					final StationChildHandle handle = createHandle( dockable );
					addComponent( handle );

					return handle;
				}

				@Override
				public void added( StationChildHandle item ){
									listeners.fireDockableAdded(item
											.getDockable());
				}
			} );

			if( controller != null ) {
				dockables.bind();
			}
			dockables.setStrategy( placeholderStrategy.getValue() );
		} finally{
			if( controller != null ) {
				controller.meltLayout();
			}
		}
	}
	
	private void writeLayoutArguments( PlaceholderMap map ){
		PlaceholderMap.Key key = map.newKey( "group" );
		switch( getOrientation() ){
			case HORIZONTAL:
				map.putString( key, "orientation", "horizontal" );
				break;
			case VERTICAL:
				map.putString( key, "orientation", "vertical" );
				break;
		}
	}
	
	private void readLayoutArguments( PlaceholderMap map ){
		PlaceholderMap.Key key = map.newKey( "group" );
		String orientation = map.getString( key, "orientation" );
		if( "horizontal".equals( orientation )){
			setOrientation( Orientation.HORIZONTAL );
		} else if ("vertical".equals(orientation)){
			setOrientation( Orientation.VERTICAL );
		}
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		final int column = column( child );
		final int line = line( child );

		if( target == null ) {
			target = child;
		}

		final PlaceholderStrategy strategy = placeholderStrategy.getValue();
		Path placeholder = null;
		if( strategy != null ) {
			placeholder = strategy.getPlaceholderFor( target );
			if( (placeholder != null) && (column >= 0) && (line >= 0) ) {
				dockables.insertPlaceholder( column, line, placeholder );
			}
		}

		return new ToolbarGroupProperty( column, line, placeholder );
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		if( property instanceof ToolbarGroupProperty ) {
			return drop( dockable, (ToolbarGroupProperty) property );
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
		final Path placeholder = property.getPlaceholder();

		int column = property.getColumn();
		int line = property.getLine();

		if( placeholder != null ) {
			if( dockables.hasPlaceholder( placeholder ) ) {
				final StationChildHandle child = dockables.get( placeholder );
				if( child == null ) {
					if( acceptable( dockable ) ) {
						Dockable replacement = getToolbarStrategy()
								.ensureToolbarLayer(this, dockable);
						if( replacement == null ){
							return false;
						}
						
						DockController controller = getController();
						if( controller != null ){
							controller.freezeLayout();
						}
						try{
							dropAt( replacement, placeholder );

							if( replacement != dockable ){
								if( property.getSuccessor() != null ){
									if (!replacement.asDockStation().drop(
											dockable, property.getSuccessor())){
										replacement.asDockStation().drop(
												dockable);
									}
								} else{
									replacement.asDockStation().drop( dockable );
								}
							}
						} finally{
							if( controller != null ){
								controller.meltLayout();
							}
						}
						
						return true;
					}
				} else{
					if( drop( child, dockable, property ) ) {
						return true;
					}

					column = dockables.getColumn( child.getDockable() );
					line = dockables.getLine( column, child.getDockable() ) + 1;
				}
			}
		} else{
			if( column >= 0 && column < columnCount() ){
				if( line >= 0 && line < lineCount( column )){
					DockStation child = getDockable(column, line)
							.asDockStation();
					if (child != null
							&& child.drop(dockable, property.getSuccessor())){
						return true;
					}
				}
			}
		}

		if( !acceptable( dockable ) ) {
			return false;
		}

		line = Math.max( 0, line );
		//column = Math.max( 0, column );
		return drop( dockable, column, line );
	}
	
	/**
	 * Drops <code>dockable</code> at the location described by
	 * <code>placeholder</code>. This method must only be called if
	 * <code>placeholder</code> points to a valid location.
	 * 
	 * @param dockable
	 *            the dockable to drop
	 * @param placeholder
	 *            the location of <code>dockable</code>, must be valid
	 */
	private void dropAt( Dockable dockable, Path placeholder ){

		DockUtilities.checkLayoutLocked();
		final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(
				this, dockable);
		try {
			DockUtilities.ensureTreeValidity( this, dockable );
			listeners.fireDockableAdding( dockable );
			final int before = dockables.getColumnCount();

			dockable.setDockParent( this );
			final StationChildHandle handle = createHandle( dockable );
			dockables.put( placeholder, handle );
			addComponent( handle );

			listeners.fireDockableAdded( dockable );
			fireDockablesRepositioned(dockable,
					before != dockables.getColumnCount());
		} finally{
			token.release();
		}
	}

	@SuppressWarnings("static-method")
	private boolean drop( StationChildHandle parent, Dockable child,
			ToolbarGroupProperty property ){
		if( property.getSuccessor() == null ) {
			return false;
		}

		final DockStation station = parent.getDockable().asDockStation();
		if( station == null ) {
			return false;
		}

		return station.drop( child, property.getSuccessor() );
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		if( property instanceof ToolbarGroupProperty ) {
			move( dockable, (ToolbarGroupProperty) property );
		}
	}

	private void move( Dockable dockable, ToolbarGroupProperty property ){
		final int sourceColumn = column( dockable );
		final int sourceLine = line( dockable );

		boolean empty = false;
		int destinationColumn = property.getColumn();
		int destinationLine = property.getLine();

		final Path placeholder = property.getPlaceholder();
		if( placeholder != null ) {
			final int column = dockables.getColumn( placeholder );
			if( column != -1 ) {
				final int line = dockables.getLine( column, placeholder );
				if( line != -1 ) {
					empty = true;
					destinationColumn = column;
					destinationLine = line;
				}
			}
		}

		if( !empty ) {
			// ensure destination valid
			destinationColumn = Math.min(destinationColumn,
					dockables.getColumnCount());
			if ((destinationColumn == dockables.getColumnCount())
					|| (destinationColumn == -1)){
				destinationLine = 0;
			} else{
				destinationLine = Math.min(destinationLine,
						dockables.getLineCount(destinationColumn));
			}
		}

		Level level;
		if( empty ) {
			level = Level.BASE;
		} else{
			level = Level.DOCKABLE;
		}
		dockables.move(sourceColumn, sourceLine, destinationColumn,
				destinationLine, level);
		mainPanel.getContentPane().revalidate();
	}
}
