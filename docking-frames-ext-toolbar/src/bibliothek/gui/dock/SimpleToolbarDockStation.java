package bibliothek.gui.dock;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Map;

import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.CombinerTarget;
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
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.NullTitleFactory;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.util.Path;

/**
 * A very simple {@link DockStation} to {@link ToolbarElementInterface}. The
 * dockable are layouted in with a FlowLayout. An optional title and border may be shown.
 * 
 * @author Herve Guillaume
 */
public class SimpleToolbarDockStation extends AbstractDockableStation implements ToolbarInterface, ToolbarElementInterface {

	/** the id of the {@link DockTitleFactory} which is used by this station */
	public static final String TITLE_ID = "simple.toolbar";
	/**
	 * This id is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	public static final String DISPLAYER_ID = "simple.toolbar";

	/** A list of all children */
	protected DockablePlaceholderList<StationChildHandle> dockables = new DockablePlaceholderList<StationChildHandle>();

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	private OverpaintablePanelBase mainPanel;

	/** current {@link PlaceholderStrategy} */
	private final PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>( PlaceholderStrategy.PLACEHOLDER_STRATEGY ){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue, PlaceholderStrategy newValue ){
			dockables.setStrategy( newValue );
		}
	};
	
	/** Strategy used to shrink the children of this station */
	private PropertyValue<ExpandableToolbarItemStrategy> expandableStrategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			shrinkAll();
		}
	};

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

	/**
	 * Tells if this station is in prepareDrop state and should draw something
	 * accordingly
	 */
	protected boolean prepareDropDraw = false;

	// ########################################################
	// ############ Initialization Managing ###################
	// ########################################################

	/**
	 * Constructs a new ToolbarDockStation. Subclasses must call {@link #init()}
	 * once the constructor has been executed.
	 */
	public SimpleToolbarDockStation(){
		init();
	}

	protected void init(){
		mainPanel = new OverpaintablePanelBase();
		paint = new DefaultStationPaintValue( ThemeManager.STATION_PAINT + ".simple.toolbar", this );
		displayerFactory = createDisplayerFactory();
		displayers = new DisplayerCollection( this, displayerFactory, getDisplayerId() );
		displayers.addDockableDisplayerListener( new DockableDisplayerListener(){
			@Override
			public void discard( DockableDisplayer displayer ){
				SimpleToolbarDockStation.this.discard( displayer );
			}
		} );

		setTitleIcon( null );
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
		return dockables.dockables().size();
	}

	@Override
	public Dockable getDockable( int index ){
		return dockables.dockables().get( index ).getDockable();
	}

	@Override
	public String getFactoryID(){
		// TODO
		return null;
//		return SimpleToolbarDockStationFactory.ID;
	}

	@Override
	public void setController( DockController controller ){
		if( getController() != controller ) {
			if( getController() != null ) {
				dockables.unbind();
			}
			for( final StationChildHandle handle : dockables.dockables() ) {
				handle.setTitleRequest( null );
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
			expandableStrategy.setProperties( controller );
			displayerFactory.setController( controller );
			displayers.setController( controller );
			mainPanel.setController( controller );

			if( controller != null ) {
				dockables.bind();
			}
			for( final StationChildHandle handle : dockables.dockables() ) {
				handle.setTitleRequest( title, true );
			}
		}
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
	public String toString(){
		return this.getClass().getSimpleName() + '@' + Integer.toHexString( hashCode() );
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
		for( int i = 0; i < getDockableCount(); i++ ) {
			if( getDockable( i ) == dockable ) {
				return i;
			}
		}
		return -1;
	}

	// ########################################################
	// ############ Expanded State Managing ###################
	// ########################################################

	private void shrink( Dockable dockable ){
		ExpandableToolbarItemStrategy strategy = expandableStrategy.getValue();
		if( strategy != null ){
			if( strategy.isEnabled( dockable, ExpandedState.SHRUNK )){
				strategy.setState( dockable, ExpandedState.SHRUNK );
			}
			else if( strategy.isEnabled( dockable, ExpandedState.STRETCHED )){
				strategy.setState( dockable, ExpandedState.STRETCHED );
			}
		}
	}
	
	private void shrinkAll(){
		for( int i = 0, n = getDockableCount(); i<n; i++ ){
			shrink( getDockable( i ) );
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
		final SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>( ToolbarStrategy.STRATEGY, getController() );
		final ToolbarStrategy result = value.getValue();
		value.setProperties( (DockController) null );
		return result;
	}

	@Override
	public StationDropOperation prepareDrop( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
		System.out.println(this.toString() + "## prepareDrop(...) ##");
		final DockController controller = getController();

		// check if the dockable and the station accept each other
		if( this.accept( dockable ) & dockable.accept( this ) ) {
			// check if controller exist and if the controller accept that
			// the dockable become a child of this station
			if( controller != null ) {
				if( !controller.getAcceptance().accept( this, dockable ) ) {
					return null;
				}
			}
			return new SimpleToolbarDropInfo( dockable, this, mouseX, mouseY ){
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
					SimpleToolbarDockStation.this.prepareDropDraw = false;
					mainPanel.repaint();
				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					if( !isMove() ) {
						// a dockable already inside the station can't be moved inside
						SimpleToolbarDockStation.this.prepareDropDraw = true;
					}
					else {
						SimpleToolbarDockStation.this.prepareDropDraw = false;
					}
					// without this line, line is displayed only on the first
					// component met
					mainPanel.repaint();
				}
			};
		}
		else {
			return null;
		}
	}

	@Override
	public void drop( Dockable dockable ){
		this.drop( dockable, getDockableCount(), true );
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
		return drop( dockable, index, false );
	}

	protected boolean drop( Dockable dockable, int index, boolean force ){
		if( force || this.accept( dockable ) ) {
			if( !force ) {
				Dockable replacement = getToolbarStrategy().ensureToolbarLayer( this, dockable );
				if( dockable == null ) {
					return false;
				}
				if( replacement != dockable ){
					replacement.asDockStation().drop( dockable );
					dockable = replacement;
				}
			}
			add( dockable, index );
			return true;
		}
		return false;
	}

	protected void add( Dockable dockable, int index ){
		add( dockable, index, null );
	}

	protected void add( Dockable dockable, int index, Path placeholder ){
		DockUtilities.ensureTreeValidity( this, dockable );
		DockUtilities.checkLayoutLocked();
		Dockable replacement = getToolbarStrategy().ensureToolbarLayer( this, dockable );
		if( replacement != dockable ){
			if( replacement != null ){
				replacement.asDockStation().drop( dockable );
			}
			dockable = replacement;
		}
		final DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking( this, dockable );
		try {
			listeners.fireDockableAdding( dockable );
			shrink( dockable );
			int inserted = -1;

			final StationChildHandle handle = new StationChildHandle( this, displayers, dockable, title );
			handle.updateDisplayer();

			if( (placeholder != null) && (dockables.getDockableAt( placeholder ) == null) ) {
				inserted = dockables.put( placeholder, handle );
			}
			else if( placeholder != null ) {
				index = dockables.getDockableIndex( placeholder );
			}

			if( inserted == -1 ) {
				getDockables().add( index, handle );
			}
			else {
				index = inserted;
			}

			insertAt( handle, index );
			listeners.fireDockableAdded( dockable );
			fireDockablesRepositioned( index + 1 );
		}
		finally {
			token.release();
		}
	}

	protected void insertAt( StationChildHandle handle, int index ){
		final Dockable dockable = handle.getDockable();
		dockable.setDockParent( this );
		mainPanel.getContentPane().add( handle.getDisplayer().getComponent(), index );
		mainPanel.getContentPane().invalidate();
		mainPanel.revalidate();
		mainPanel.getContentPane().repaint();
	}

	@Override
	public void drag( Dockable dockable ){
		if( dockable.getDockParent() != this ) {
			throw new IllegalArgumentException( "The dockable cannot be dragged, it is not child of this station." );
		}
		this.remove( dockable );

	}

	@Override
	public void replace( Dockable old, Dockable next ){
		DockUtilities.checkLayoutLocked();
		final DockController controller = getController();
		if( controller != null ) {
			controller.freezeLayout();
		}
		final int index = indexOf( old );
		remove( old );
		add( next, index );
		controller.meltLayout();
	}

	/**
	 * Drops thanks to information collect by dropInfo
	 * 
	 * @param dropInfo
	 */
	protected void drop( StationDropOperation dropInfo ){
		final SimpleToolbarDropInfo dropInfoToolbar = (SimpleToolbarDropInfo) dropInfo;
		if( !dropInfoToolbar.isMove() ) {
			drop( dropInfoToolbar.getItem(), getDockableCount() );
		}
	}

	@Override
	public boolean canDrag( Dockable dockable ){
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
	protected void remove( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		final int index = indexOf( dockable );
		final StationChildHandle handle = dockables.dockables().get( index );

		if( getFrontDockable() == dockable ) {
			setFrontDockable( null );
		}

		final DockHierarchyLock.Token token = DockHierarchyLock.acquireUnlinking( this, dockable );
		try {
			listeners.fireDockableRemoving( dockable );
			dockable.setDockParent( null );

			dockables.remove( index );
			mainPanel.getContentPane().remove( handle.getDisplayer().getComponent() );
			mainPanel.doLayout();
			mainPanel.revalidate();
			mainPanel.repaint();
			handle.destroy();
			listeners.fireDockableRemoved( dockable );
			fireDockablesRepositioned( index );
		}
		finally {
			token.release();
		}
	}

	@Override
	public boolean canReplace( Dockable old, Dockable next ){
		if( old.getClass() == next.getClass() ) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void replace( DockStation old, Dockable next ){
		replace( old.asDockable(), next );
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
	protected DefaultDisplayerFactoryValue createDisplayerFactory(){
		return new DefaultDisplayerFactoryValue( ThemeManager.DISPLAYER_FACTORY + ".toolbar.simple", this );
	}

	/**
	 * Gets an id that is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	protected String getDisplayerId(){
		return DISPLAYER_ID;
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
	protected DockTitleVersion registerTitle( DockController controller ){
		return controller.getDockTitleManager().getVersion( TITLE_ID, NullTitleFactory.INSTANCE );
	}

	/**
	 * Replaces <code>displayer</code> with a new {@link DockableDisplayer}.
	 * 
	 * @param displayer
	 *            the displayer to replace
	 * @throws IllegalArgumentException
	 *             if <code>displayer</code> is not a child of this station
	 */
	protected void discard( DockableDisplayer displayer ){
		final Dockable dockable = displayer.getDockable();

		final int index = indexOf( dockable );
		if( index < 0 ) {
			throw new IllegalArgumentException( "displayer is not a child of this station: " + displayer );
		}

		final StationChildHandle handle = dockables.dockables().get( index );

		mainPanel.getContentPane().remove( handle.getDisplayer().getComponent() );
		handle.updateDisplayer();
		insertAt( handle, index );
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

		private final int INSETS_SIZE = 0;

		/**
		 * The content Pane of this {@link OverpaintablePanel} (with a
		 * BoxLayout)
		 */
		private final JPanel dockablePane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		/**
		 * This pane is the base of this OverpaintablePanel and contains both
		 * title and content panes (with a BoxLayout)
		 */
		private final JPanel basePane = new JPanel(new GridLayout( 1, 1 ));

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBase(){
			basePane.setBackground(Color.GREEN);
			dockablePane.setBackground(Color.RED);
			basePane.add( dockablePane );
			setBasePane( basePane );
			setContentPane( dockablePane );
		}

		@Override
		protected void paintOverlay( Graphics g ){
			final Graphics2D g2D = (Graphics2D) g;
			// DefaultStationPaintValue paint = getPaint();
			if( prepareDropDraw ) {
				final Rectangle rectToolbar = basePane.getBounds();
				paint.drawInsertion( g2D, rectToolbar, rectToolbar );
			}
		}

		@Override
		public String toString(){
			return this.getClass().getSimpleName() + '@' + Integer.toHexString( hashCode() );
		}

	}

	// ########################################################
	// ############### PlaceHolder Managing ###################
	// ########################################################

//	/**
//	 * Creates a new {@link DockableProperty} describing the location of
//	 * <code>child</code> on this station. This method is called by
//	 * {@link #getDockableProperty(Dockable, Dockable)} once the location and
//	 * placeholder of <code>child</code> or <code>target</code> have been
//	 * calculated
//	 * 
//	 * @param child
//	 *            a child of this station
//	 * @param target
//	 *            the item whose position is searched
//	 * @param index
//	 *            the location of <code>child</code>
//	 * @param placeholder
//	 *            the placeholder for <code>target</code> or <code>child</code>
//	 * @return a new {@link DockableProperty} that stores <code>index</code>,
//	 *         <code>placeholder</code> and any other information a subclass
//	 *         deems necessary to store
//	 */
//	protected DockableProperty getDockableProperty( Dockable child, Dockable target, int index, Path placeholder ){
//		return new SimpleToolbarProperty( index, placeholder );
//	}
//
//	/**
//	 * Tells whether the subclass knows how to handle <code>property</code>.
//	 * This means that the type of <code>property</code> is the same type as the
//	 * result of {@link #getDockableProperty(Dockable, Dockable, int, Path)}
//	 * 
//	 * @param property
//	 *            the property to check
//	 * @return <code>true</code> if this sublcass knows how to handle the type
//	 *         of <code>property</code>
//	 */
//	protected boolean isValidProperty( DockableProperty property ){
//		return property instanceof SimpleToolbarProperty;
//	}
//
//	/**
//	 * Gets the location of a {@link Dockable} on this station. Called only if
//	 * <code>property</code> passed {@link #isValidProperty(DockableProperty)}.
//	 * 
//	 * @param property
//	 *            some property created by
//	 *            {@link #getDockableProperty(Dockable, Dockable, int, Path)}
//	 * @return the index parameter
//	 */
//	protected int getIndex( DockableProperty property ){
//		return ((SimpleToolbarProperty) property).getIndex();
//	}
//
//	protected Path getPlaceholder( DockableProperty property ){
//		return ((SimpleToolbarProperty) property).getPlaceholder();
//	}

	/**
	 * Grants direct access to the list of {@link Dockable}s, subclasses should
	 * not modify the list unless the fire the appropriate events.
	 * 
	 * @return the list of dockables
	 */
	protected PlaceholderList.Filter<StationChildHandle> getDockables(){
		return dockables.dockables();
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

		return dockables.toMap( new PlaceholderListItemAdapter<Dockable, StationChildHandle>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, StationChildHandle handle ){
				final Dockable dockable = handle.getDockable();

				final Integer id = children.get( dockable );
				if( id == null ) {
					return null;
				}

				final ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				item.putInt( "id", id );
				item.putInt( "index", index );

				if( strategy != null ) {
					final Path placeholder = strategy.getPlaceholderFor( dockable );
					if( placeholder != null ) {
						item.putString( "placeholder", placeholder.toString() );
						item.setPlaceholder( placeholder );
					}
				}

				return item;
			}
		} );
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
	public void setPlaceholders( PlaceholderMap map, final Map<Integer, Dockable> children ){
		DockUtilities.checkLayoutLocked();
		if( getDockableCount() > 0 ) {
			throw new IllegalStateException( "must not have any children" );
		}
		final DockController controller = getController();

		try {
			if( controller != null ) {
				controller.freezeLayout();
			}

			final DockablePlaceholderList<StationChildHandle> next = new DockablePlaceholderList<StationChildHandle>();

			if( getController() != null ) {
				dockables.setStrategy( null );
				dockables.unbind();
				dockables = next;
			}
			else {
				dockables = next;
			}

			next.read( map, new PlaceholderListItemAdapter<Dockable, StationChildHandle>(){
				private DockHierarchyLock.Token token;
				private int index = 0;

				@Override
				public StationChildHandle convert( ConvertedPlaceholderListItem item ){
					final int id = item.getInt( "id" );
					final Dockable dockable = children.get( id );
					if( dockable != null ) {
						DockUtilities.ensureTreeValidity( SimpleToolbarDockStation.this, dockable );
						token = DockHierarchyLock.acquireLinking( SimpleToolbarDockStation.this, dockable );
						listeners.fireDockableAdding( dockable );
						return new StationChildHandle( SimpleToolbarDockStation.this, displayers, dockable, title );
					}
					return null;
				}

				@Override
				public void added( StationChildHandle handle ){
					try {
						handle.updateDisplayer();
						insertAt( handle, index++ );
						listeners.fireDockableAdded( handle.getDockable() );
					}
					finally {
						token.release();
					}
				}
			} );

			if( getController() != null ) {
				dockables.bind();
				dockables.setStrategy( getPlaceholderStrategy() );
			}
		}
		finally {
			if( controller != null ) {
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
		if( getDockableCount() > 0 ) {
			throw new IllegalStateException( "only allowed if there are not children present" );
		}

		try {
			final DockablePlaceholderList<StationChildHandle> next = new DockablePlaceholderList<StationChildHandle>( placeholders );
			if( getController() != null ) {
				dockables.setStrategy( null );
				dockables.unbind();
				dockables = next;
				dockables.bind();
				dockables.setStrategy( getPlaceholderStrategy() );
			}
			else {
				dockables = next;
			}
		}
		catch( final IllegalArgumentException ex ) {
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
		placeholderStrategy.setValue( strategy );
	}

	@Override
	public DockableProperty getDockableProperty( Dockable child, Dockable target ){
		// TODO
//		final int index = indexOf( child );
//		Path placeholder = null;
//		final PlaceholderStrategy strategy = getPlaceholderStrategy();
//		if( strategy != null ) {
//			placeholder = strategy.getPlaceholderFor( target == null ? child : target );
//			if( placeholder != null ) {
//				dockables.dockables().addPlaceholder( index, placeholder );
//			}
//		}
//		return getDockableProperty( child, target, index, placeholder );
		return null;
	}

	@Override
	public boolean drop( Dockable dockable, DockableProperty property ){
		// TODO
//		if( isValidProperty( property ) ) {
//			final boolean acceptable = acceptable( dockable );
//			boolean result = false;
//			final int index = Math.min( getDockableCount(), getIndex( property ) );
//
//			final Path placeholder = getPlaceholder( property );
//			if( (placeholder != null) && (property.getSuccessor() != null) ) {
//				final StationChildHandle preset = dockables.getDockableAt( placeholder );
//				if( preset != null ) {
//					final DockStation station = preset.getDockable().asDockStation();
//					if( station != null ) {
//						if( station.drop( dockable, property.getSuccessor() ) ) {
//							dockables.removeAll( placeholder );
//							result = true;
//						}
//					}
//				}
//			}
//
//			if( !result && (placeholder != null) ) {
//				if( acceptable && dockables.hasPlaceholder( placeholder ) ) {
//					add( dockable, index, placeholder );
//					result = true;
//				}
//			}
//
//			if( !result && (dockables.dockables().size() == 0) ) {
//				if( acceptable ) {
//					drop( dockable );
//					result = true;
//				}
//			}
//
//			if( !result ) {
//				if( (index < dockables.dockables().size()) && (property.getSuccessor() != null) ) {
//					final DockStation child = getDockable( index ).asDockStation();
//					if( child != null ) {
//						result = child.drop( dockable, property.getSuccessor() );
//					}
//				}
//			}
//
//			if( !result && acceptable ) {
//				result = drop( dockable, index );
//			}
//
//			return result;
//		}
//		return false;
		
		
		return false;
	}

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO pending
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		// TODO Auto-generated method stub

	}

}

abstract class SimpleToolbarDropInfo implements StationDropOperation {

	/** The {@link Dockable} which is inserted */
	private final Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to his original dock parent!)
	 */
	private final SimpleToolbarDockStation stationHost;
	/** Location of the mouse */
	private final int mouseX, mouseY;

	/**
	 * Constructs a new info.
	 * 
	 * @param dockable
	 *            the dockable to drop
	 * @param stationHost
	 *            the station where drop the dockable
	 * @param mouseX
	 *            the mouse position on X axis
	 * @param mouseY
	 *            the mouse position on Y axis
	 */
	public SimpleToolbarDropInfo( Dockable dockable, SimpleToolbarDockStation stationHost, int mouseX, int mouseY ){
		this.dragDockable = dockable;
		this.stationHost = stationHost;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	@Override
	public Dockable getItem(){
		return dragDockable;
	}

	@Override
	public SimpleToolbarDockStation getTarget(){
		return stationHost;
	}

	@Override
	public abstract void destroy();

	@Override
	public abstract void draw();

	@Override
	public abstract void execute();

	@Override
	public CombinerTarget getCombination(){
		// not supported by this kind of station
		return null;
	}

	@Override
	public DisplayerCombinerTarget getDisplayerCombination(){
		// not supported by this kind of station
		return null;
	}

	@Override
	public boolean isMove(){
		return getItem().getDockParent() == getTarget();
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@' + Integer.toHexString( hashCode() );
	}

}
