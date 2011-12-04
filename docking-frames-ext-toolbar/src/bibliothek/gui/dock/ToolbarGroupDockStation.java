package bibliothek.gui.dock;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Iterator;
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
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarComplexDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationFactory;
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
	private DockablePlaceholderToolbarGrid<StationChildHandle> dockables = new DockablePlaceholderToolbarGrid<StationChildHandle>();

	/** The {@link PlaceholderStrategy} that is used by {@link #dockables} */
	private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>( PlaceholderStrategy.PLACEHOLDER_STRATEGY ){
		protected void valueChanged( PlaceholderStrategy oldValue, PlaceholderStrategy newValue ){
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
	private int borderSideSnapSize = 1;
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
		this.mainPanel.getContentPane().setBackground( Color.YELLOW );
		this.mainPanel.getBasePane().setBackground( Color.ORANGE );
	}

	@Override
	protected void init(){
		mainPanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue( ThemeManager.STATION_PAINT + ".toolbar", this );
		setOrientation( this.getOrientation() );
		displayerFactory = createDisplayerFactory();
		displayers = new DisplayerCollection( this, displayerFactory, getDisplayerId() );
		displayers.addDockableDisplayerListener( new DockableDisplayerListener(){
			@Override
			public void discard( DockableDisplayer displayer ){
				ToolbarGroupDockStation.this.discard( displayer );
			}
		} );

		setTitleIcon( null );
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
		return dockables.getColumn( dockable );
		//		if (dockables != null){
		//			int column = 0;
		//			for (DockablePlaceholderList<StationChildHandle> dockablePlaceHolderList : dockables){
		//				for (StationChildHandle handle : dockablePlaceHolderList
		//						.dockables()){
		//					if (handle.getDockable() == dockable){
		//						return column;
		//					}
		//				}
		//				column++;
		//			}
		//		}
		//		return -1;
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
		//		if (dockables != null){
		//			for (DockablePlaceholderList<StationChildHandle> dockablePlaceHolderList : dockables){
		//				int line = 0;
		//				for (StationChildHandle handle : dockablePlaceHolderList
		//						.dockables()){
		//					if (handle.getDockable() == dockable){
		//						return line;
		//					}
		//					line++;
		//				}
		//			}
		//		}
		//		return -1;
	}

	// ########################################################
	// ############ General DockStation Managing ##############
	// ########################################################

	@Override
	public Component getComponent(){
		return mainPanel;
	}

	@Override
	public int getDockableCount(){
		return dockables.size();
		//		int count = 0;
		//		if (dockables != null){
		//			for (DockablePlaceholderList<StationChildHandle> dockablePlaceHolderList : dockables){
		//				count += dockablePlaceHolderList.dockables().size();
		//			}
		//		} else{
		//			return 0;
		//		}
		//		return count;
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.get( index ).asDockable();

		//		int count = -1;
		//		for (DockablePlaceholderList<StationChildHandle> dockablePlaceHolderList : dockables){
		//			for (StationChildHandle handle : dockablePlaceHolderList
		//					.dockables()){
		//				count++;
		//				if (count >= index){
		//					return handle.getDockable();
		//				}
		//			}
		//		}
		//		return null;
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
		if( borderSideSnapSize < 0 )
			throw new IllegalArgumentException( "borderSideSnapeSize must not be less than 0" );

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
			}
			else {
				title = registerTitle( controller );
			}
			paint.setController( controller );
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
		for( int i = 0; i < getDockableCount(); i++ ) {
			Dockable d = getDockable( i );
			if( d instanceof OrientedDockStation ) {
				OrientedDockStation group = (OrientedDockStation) d;
				group.setOrientation( this.getOrientation() );
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
		return new DockStationDropLayer[]{ new DefaultDropLayer( this ), new SideSnapDropLayer( this ), };
	}

	@Override
	public boolean accept( Dockable child ){
		return getToolbarStrategy().isToolbarGroupPart( child );
	}

	@Override
	public boolean accept( DockStation station ){
		return getToolbarStrategy().isToolbarGroupPartParent( station, this );
	}

	@Override
	public StationDropOperation prepareDrop( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
		System.out.println( this.toString() + "## prepareDrop(...) ##" );
		DockController controller = getController();

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
			return new ToolbarComplexDropInfo( dockable, this, mouseX, mouseY ){
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
					ToolbarGroupDockStation.this.mainPanel.repaint();
				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					ToolbarGroupDockStation.this.indexBeneathMouse = indexOf( getDockableBeneathMouse() );
					ToolbarGroupDockStation.this.prepareDropDraw = true;
					ToolbarGroupDockStation.this.sideBeneathMouse = this.getSideDockableBeneathMouse();
					// without this line, line is displayed only on the first
					// component met
					ToolbarGroupDockStation.this.mainPanel.repaint();
				}
			};
		}
		else {
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
		if( dropInfoGroup.getItemPositionVSBeneathDockable() != Position.CENTER ) {
			// Note: Computation of index to insert drag dockable is not the
			// same between a move() and a drop(), because with a move() it is
			// as if the drag dockable were remove first then added again in the
			// list -> so the list is shrunk and the index are shifted behind
			// the remove dockable. (Note: It's weird because indeed drag() is
			// called after move()...)
			if( dropInfoGroup.isMove() ) {
				int line, column, shift = 0;
				if( getOrientation() == Orientation.VERTICAL ) {
					column = column( dropInfoGroup.getDockableBeneathMouse() );
					if( dropInfoGroup.getItemPositionVSBeneathDockable() == Position.NORTH ) {
						// index shifted because the drag dockable is above the
						// dockable beneath mouse
						shift = -1;
					}
					if( dropInfoGroup.getSideDockableBeneathMouse() == Position.SOUTH ) {
						// the drag dockable is put below the dockable beneath
						// mouse
						line = indexBeneathMouse + 1 + shift;
					}
					else {
						// the drag dockable is put above the dockable beneath
						// mouse
						line = indexBeneathMouse + shift;
					}
					drop( dropInfoGroup.getItem(), column, line );
				}
				else {
					if( dropInfoGroup.getItemPositionVSBeneathDockable() == Position.WEST ) {
						// index shifted because the drag dockable is at the
						// left of the dockable beneath mouse
						shift = -1;
					}
					if( dropInfoGroup.getSideDockableBeneathMouse() == Position.EAST ) {
						// the drag dockable is put at the right of the dockable
						// beneath mouse
						column = indexBeneathMouse + 1 + shift;
					}
					else {
						// the drag dockable is put at the left of the dockable
						// beneath mouse
						column = indexBeneathMouse + shift;
					}
					drop( dropInfoGroup.getItem(), column );
				}
			}
			else {
				if( getOrientation() == Orientation.VERTICAL ) {
					if( dropInfoGroup.getSideDockableBeneathMouse() == Position.SOUTH ) {
						// the drag dockable is put below the dockable beneath
						// mouse
						drop( dropInfoGroup.getItem(), column( dropInfoGroup.getDockableBeneathMouse() ), line( dropInfoGroup.getDockableBeneathMouse() ) + 1 );
					}
					else {
						// the drag dockable is put above the dockable beneath
						// mouse
						drop( dropInfoGroup.getItem(), column( dropInfoGroup.getDockableBeneathMouse() ), line( dropInfoGroup.getDockableBeneathMouse() ) );
					}
				}
				else {
					if( dropInfoGroup.getSideDockableBeneathMouse() == Position.EAST ) {
						// the drag dockable is put at the right of the dockable
						// beneath mouse
						drop( dropInfoGroup.getItem(), column( dropInfoGroup.getDockableBeneathMouse() ) + 1 );
					}
					else {
						// the drag dockable is put at the left of the dockable
						// beneath mouse
						drop( dropInfoGroup.getItem(), column( dropInfoGroup.getDockableBeneathMouse() ) );
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
				dockable = getToolbarStrategy().ensureToolbarLayer( this, dockable );
				if( dockable == null ) {
					return false;
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
		dockable = getToolbarStrategy().ensureToolbarLayer( this, dockable );
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
		try {
			listeners.fireDockableAdding( dockable );

			dockable.setDockParent( this );
			StationChildHandle handle = createHandle( dockable );
			// add in the list of dockable
			dockables.insert( column, line, handle );

			// add in the main panel
			addComponent( handle );
			listeners.fireDockableAdded( dockable );
			fireDockablesRepositioned( indexOf( dockable ) + 1 );
		}
		finally {
			token.release();
		}
	}

	/**
	 * Creates a new {@link StationChildHandle} that wrapps around <code>dockable</code>. This method does
	 * not add the handle to any list or fire any events, that is the callers responsibility. Callers should
	 * also call {@link #addComponent(StationChildHandle)} with the new handle.
	 * @param dockable the element that is to be wrapped
	 * @return a new {@link StationChildHandle} for the element
	 */
	private StationChildHandle createHandle( Dockable dockable ){
		StationChildHandle handle = new StationChildHandle( this, displayers, dockable, title );
		handle.updateDisplayer();
		return handle;
	}

	/**
	 * Adds <code>handle</code> to the {@link #mainPanel} of this station. Note that this method only
	 * cares about the {@link Component}-{@link Container} relationship, it does not store <code>handle</code>
	 * in the {@link #dockables} list.
	 * @param handle the handle to add
	 */
	private void addComponent( StationChildHandle handle ){
		mainPanel.getContentPane().add( handle.getDisplayer().getComponent() );
		mainPanel.getContentPane().revalidate();

		// TODO
		// don't forget to add an intermediate column panel

		// Dockable dockable = handle.getDockable();
		//
		// dockable.setDockParent(this);
		// if (dockable instanceof OrientedDockStation){
		// if (getOrientation() != null){
		// // it would be possible that this station was not already
		// // oriented. This is the case when this station is
		// // instantiated but not drop in any station which could give it
		// // an orientation
		// ((OrientedDockStation) dockable)
		// .setOrientation(getOrientation());
		// }
		// }
		// mainPanel.getContentPane().add(handle.getDisplayer().getComponent(),
		// index);
		// mainPanel.getContentPane().invalidate();
		// mainPanel.revalidate();
		// mainPanel.getContentPane().repaint();
	}

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
		// TODO
		return false;
	}

	public boolean drop( Dockable dockable, int column, boolean force ){
		// TODO
		return false;
	}

	private void add( Dockable dockable, int column ){
		// TODO
	}

	@Override
	public void drag( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		if( dockable.getDockParent() != this ) {
			throw new IllegalArgumentException( "not a child of this station: " + dockable );
		}
		StationChildHandle handle = dockables.get( dockable );
		DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
		try {
			listeners.fireDockableRemoving( dockable );

			dockables.remove( handle );
			removeComponent( handle );
			dockable.setDockParent( null );

			listeners.fireDockableRemoved( dockable );
		}
		finally {
			token.release();
		}
		fireDockablesRepositioned( 0 ); // TODO not every dockable got repositioned
	}

	@Override
	protected void remove( Dockable dockable ){
		// TODO Auto-generated method stub
		// don't forget to remove the intermediate column panel
	}

	@Override
	protected void remove( int index ){
		// TODO Auto-generated method stub
		// don't forget to remove the intermediate column panel
	}

	@Override
	public void replace( Dockable old, Dockable next ){
		// TODO Auto-generated method stub

	}

	// ########################################################
	// ###################### UI Managing #####################
	// ########################################################

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme( this, new ToolbarGroupDockStationFactory() );
	}

	@Override
	protected DefaultDisplayerFactoryValue createDisplayerFactory(){
		return new DefaultDisplayerFactoryValue( ThemeManager.DISPLAYER_FACTORY + ".toolbar.group", this );
	}

	@Override
	protected DockTitleVersion registerTitle( DockController controller ){
		return controller.getDockTitleManager().getVersion( TITLE_ID, BasicDockTitleFactory.FACTORY );
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

		//		int index = indexOf(dockable);
		//		if (index < 0){
		//			throw new IllegalArgumentException(
		//					"displayer is not a child of this station: " + displayer);
		//		}

		StationChildHandle handle = dockables.get( dockable );
		if( handle == null ) {
			throw new IllegalArgumentException( "displayer is not child of this station: " + displayer );
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
				Dimension pref = super.getPreferredSize();
				// Insets insets = getInsets();
				// pref.height += insets.top + insets.bottom;
				// pref.width += insets.left + insets.right;
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
			basePane.setBackground( Color.GREEN );
			dockablePane.setBackground( Color.RED );

			basePane.setLayout( new BorderLayout() );
			basePane.add( dockablePane, BorderLayout.CENTER );
			setBasePane( basePane );
			setContentPane( dockablePane );
			//			this.setSolid( false );
			//			dockablePane.setOpaque( false );
			//			basePane.setOpaque( false );
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
		 * {@linl ToolbarDockStation}
		 */
		public void updateAlignment(){
			Orientation orientation = ToolbarGroupDockStation.this.getOrientation();

			if( orientation != null ) {
				dockablePane.setLayout( new ToolbarGridLayoutManager<StationChildHandle>( orientation, dockables ){
					@Override
					protected Component toComponent( StationChildHandle item ){
						return item.getDisplayer().getComponent();
					}
				} );

				//				switch( orientation ){
				//					case HORIZONTAL:
				//						//					dockablePane.setLayout(new BoxLayout(dockablePane,
				//						//							BoxLayout.X_AXIS));
				//						basePane.setLayout( new BoxLayout( basePane, BoxLayout.X_AXIS ) );
				//						dockablePane.setAlignmentY( Component.CENTER_ALIGNMENT );
				//						basePane.setAlignmentY( Component.CENTER_ALIGNMENT );
				//						dockablePane.setAlignmentX( Component.LEFT_ALIGNMENT );
				//						basePane.setAlignmentX( Component.LEFT_ALIGNMENT );
				//						break;
				//					case VERTICAL:
				//						//					dockablePane.setLayout(new BoxLayout(dockablePane,
				//						//							BoxLayout.Y_AXIS));
				//						basePane.setLayout( new BoxLayout( basePane, BoxLayout.Y_AXIS ) );
				//						dockablePane.setAlignmentY( Component.TOP_ALIGNMENT );
				//						basePane.setAlignmentY( Component.TOP_ALIGNMENT );
				//						dockablePane.setAlignmentX( Component.CENTER_ALIGNMENT );
				//						basePane.setAlignmentX( Component.CENTER_ALIGNMENT );
				//						break;
				//					default:
				//						throw new IllegalArgumentException();
				//				}
			}
		}

		@Override
		protected void paintOverlay( Graphics g ){
			Graphics2D g2D = (Graphics2D) g;
			DefaultStationPaintValue paint = getPaint();
			if( prepareDropDraw ) {
				if( indexBeneathMouse != -1 ) {
					Component componentBeneathMouse = dockables.get( indexBeneathMouse ).getDisplayer().getComponent();
					if( componentBeneathMouse != null ) {
						// WARNING:
						// 1. This rectangle stands for the component beneath
						// mouse. His coordinates are in the frame of reference
						// his
						// direct parent.
						// 2. g is in the frame of reference of the overlayPanel
						// 3. So we need to translate this rectangle in the
						// frame of
						// reference of the overlay panel, which is the same
						// that
						// the base pane
						Rectangle rectBeneathMouse = componentBeneathMouse.getBounds();
						Rectangle2D rect = new Rectangle2D.Double( rectBeneathMouse.x, rectBeneathMouse.y, rectBeneathMouse.width, rectBeneathMouse.height );
						g2D.setColor( Color.RED );
						g2D.setStroke( new BasicStroke( 3 ) );
						g2D.draw( rect );
						Point pBeneath = rectBeneathMouse.getLocation();
						SwingUtilities.convertPointToScreen( pBeneath, componentBeneathMouse.getParent() );
						SwingUtilities.convertPointFromScreen( pBeneath, this.getBasePane() );
						Rectangle rectangleTranslated = new Rectangle( pBeneath.x, pBeneath.y, rectBeneathMouse.width, rectBeneathMouse.height );
						switch( ToolbarGroupDockStation.this.getOrientation() ){
							case VERTICAL:
								int y;
								if( sideBeneathMouse == Position.NORTH ) {
									y = rectangleTranslated.y;
								}
								else {
									y = rectangleTranslated.y + rectangleTranslated.height;
								}
								paint.drawInsertionLine( g, rectangleTranslated.x, y, rectangleTranslated.x + rectangleTranslated.width, y );
								break;
							case HORIZONTAL:
								int x;
								if( sideBeneathMouse == Position.WEST ) {
									x = rectangleTranslated.x;
								}
								else {
									x = rectangleTranslated.x + rectangleTranslated.width;
								}
								paint.drawInsertionLine( g, x, rectangleTranslated.y, x, rectangleTranslated.y + rectangleTranslated.height );
						}
					}
				}
			}
		}

		@Override
		public String toString(){
			return this.getClass().getSimpleName() + '@' + Integer.toHexString( this.hashCode() );
		}

	}

	// ########################################################
	// ############### PlaceHolder Managing ###################
	// ########################################################
	//
	//	/**
	//	 * Grants direct access to the list of {@link Dockable}s, subclasses should
	//	 * not modify the list unless the fire the appropriate events.
	//	 * 
	//	 * @return the list of dockables
	//	 */
	//	protected PlaceholderList.Filter<StationChildHandle> getDockables(){
	//		return dockables.dockables();
	//	}

	@Override
	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}

	/**
	 * Converst this station into a {@link PlaceholderMap} using <code>identifiers</code> to
	 * remember which {@link Dockable} was at which location.
	 * @param identifiers the identifiers to apply
	 * @return <code>this</code> as map
	 */
	public PlaceholderMap getPlaceholders( Map<Dockable, Integer> identifiers ){
		return dockables.toMap( identifiers );
	}

	@Override
	public void setPlaceholders( PlaceholderMap placeholders ){
		dockables.fromMap( placeholders );
	}

	public void setPlaceholders( PlaceholderMap placeholders, Map<Integer, Dockable> children ){
		DockUtilities.checkLayoutLocked();
		if( getDockableCount() > 0 ) {
			throw new IllegalStateException( "this station still has children" );
		}

		DockController controller = getController();

		try {
			if( controller != null ) {
				controller.freezeLayout();
			}

			dockables.setStrategy( null );
			dockables.unbind();

			dockables.fromMap( placeholders, children, new PlaceholderToolbarGridConverter<Dockable, StationChildHandle>(){
				@Override
				public StationChildHandle convert( Dockable dockable, ConvertedPlaceholderListItem item ){
					listeners.fireDockableAdding( dockable );

					StationChildHandle handle = createHandle( dockable );
					addComponent( handle );

					return handle;
				}

				@Override
				public void added( StationChildHandle item ){
					listeners.fireDockableAdded( item.getDockable() );
				}
			} );

			if( controller != null ) {
				dockables.bind();
			}
			dockables.setStrategy( placeholderStrategy.getValue() );
		}
		finally {
			if( controller != null ) {
				controller.meltLayout();
			}
		}
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		int column = column( child );
		int line = line( child );

		if( target == null ) {
			target = child;
		}

		PlaceholderStrategy strategy = placeholderStrategy.getValue();
		Path placeholder = null;
		if( strategy != null ) {
			placeholder = strategy.getPlaceholderFor( target );
			if( placeholder != null && column >= 0 && line >= 0 ) {
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
	 * @param dockable the element to drop
	 * @param property the location of <code>dockable</code>
	 * @return <code>true</code> if dropping was successfull, <code>false</code> otherwise
	 */
	public boolean drop( Dockable dockable, ToolbarGroupProperty property ){
		Path placeholder = property.getPlaceholder();
		
		int column = property.getColumn();
		int line = property.getLine();
		
		if( placeholder != null ) {
			if( dockables.hasPlaceholder( placeholder ) ) {
				StationChildHandle child = dockables.get( placeholder );
				if( child == null ) {
					if( acceptable( dockable ) ){
						DockUtilities.checkLayoutLocked();
						DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
						try {
							DockUtilities.ensureTreeValidity( this, dockable );
							listeners.fireDockableAdding( dockable );
	
							dockable.setDockParent( this );
							StationChildHandle handle = createHandle( dockable );
							dockables.put( placeholder, handle );
							addComponent( handle );
	
							listeners.fireDockableAdded( dockable );
						}
						finally {
							token.release();
						}
						return true;
					}
				}
				else {
					if( drop( child, dockable, property )){
						return true;
					}
					
					column = dockables.getColumn( child.getDockable() );
					line = dockables.getLine( column, child.getDockable() ) + 1;
				}
			}
		}
		
		if( !acceptable( dockable )){
			return false;
		}
		
		return drop( dockable, column, line );
	}
	
	private boolean drop( StationChildHandle parent, Dockable child, ToolbarGroupProperty property ){
		if( property.getSuccessor() == null ){
			return false;
		}
		
		DockStation station = parent.getDockable().asDockStation();
		if( station == null ){
			return false;
		}
		
		return station.drop( child, property.getSuccessor() );
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO Auto-generated method stub

	}
}
