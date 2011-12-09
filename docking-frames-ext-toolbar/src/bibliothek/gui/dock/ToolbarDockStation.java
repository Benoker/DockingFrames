package bibliothek.gui.dock;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
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
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarSlimDropLayer;
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
 * A {@link Dockable} and a {@link DockStation} which stands for a group of
 * {@link ComponentDockable}. As dockable it can be put in {@link DockStation}
 * which implements marker interface {@link ToolbarInterface}. As DockStation it
 * accept a {@link ComponentDockable} or a {@link ToolbarDockStation}
 * 
 * @author Herve Guillaume
 */
public class ToolbarDockStation extends AbstractToolbarDockStation{

	/** the id of the {@link DockTitleFactory} which is used by this station */
	public static final String TITLE_ID = "toolbar";
	/**
	 * This id is forwarded to {@link Extension}s which load additional
	 * {@link DisplayerFactory}s
	 */
	public static final String DISPLAYER_ID = "toolbar";

	/** A list of all children */
	protected DockablePlaceholderList<StationChildHandle> dockables = new DockablePlaceholderList<StationChildHandle>();

	/**
	 * The graphical representation of this station: the pane which contains
	 * component
	 */
	private OverpaintablePanelBase mainPanel;

	/**
	 * Size of the lateral zone where no drop action can be done (Measured in
	 * pixel).
	 */
	private int lateralNodropZoneSize = 1;

	/** current {@link PlaceholderStrategy} */
	private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>(
			PlaceholderStrategy.PLACEHOLDER_STRATEGY){
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue,
				PlaceholderStrategy newValue ){
			dockables.setStrategy(newValue);
		}
	};

	// ########################################################
	// ############ Initialization Managing ###################
	// ########################################################

	/**
	 * Creates a new {@link ToolbarDockStation}.
	 */
	public ToolbarDockStation(){
		init();
		this.mainPanel.getContentPane().setBackground(Color.GREEN);
		this.mainPanel.getBasePane().setBackground(Color.CYAN);
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
						ToolbarDockStation.this.discard(displayer);
					}
				});

		setTitleIcon(null);
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
		return dockables.dockables().get(index).getDockable();
	}

	@Override
	public String getFactoryID(){
		return ToolbarDockStationFactory.ID;
	}

	/**
	 * Sets the size of the two lateral zones where no drop action can be done
	 * (Measured in pixel).
	 * 
	 * @param lateralNodropZoneSize
	 *            the size of the rectangular lateral zones (in pixel)
	 * @throws IllegalArgumentException
	 *             if the size is smaller than 0
	 */
	public void setLateralNodropZoneSize( int lateralNodropZoneSize ){
		if (lateralNodropZoneSize < 0)
			throw new IllegalArgumentException(
					"borderSideSnapeSize must not be less than 0");
		this.lateralNodropZoneSize = lateralNodropZoneSize;
	}

	/**
	 * Gets the size of the two lateral zones where no drop action can be done
	 * (Measured in pixel).
	 * 
	 * @return the size of the rectangular lateral zones (in pixel)
	 */
	public int getLateralNodropZoneSize(){
		return lateralNodropZoneSize;
	}

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
				OrientedDockStation element = (OrientedDockStation) d;
				element.setOrientation(this.getOrientation());
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
		return new DockStationDropLayer[] { new ToolbarSlimDropLayer(this) };
	}

	@Override
	public boolean accept( Dockable child ){
		return getToolbarStrategy().isToolbarPart(child);
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
			// check if controller exist and if the controller accept that
			// the dockable become a child of this station
			if (controller != null){
				if (!controller.getAcceptance().accept(this, dockable)){
					return null;
				}
			}
			return new ToolbarDropInfo<AbstractToolbarDockStation>(dockable,
					this, mouseX, mouseY){
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
					ToolbarDockStation.this.indexBeneathMouse = -1;
					ToolbarDockStation.this.sideBeneathMouse = null;
					ToolbarDockStation.this.prepareDropDraw = false;
					ToolbarDockStation.this.mainPanel.repaint();
				}

				@Override
				public void draw(){
					// without this line, nothing is displayed
					ToolbarDockStation.this.indexBeneathMouse = indexOf(getDockableBeneathMouse());
					ToolbarDockStation.this.prepareDropDraw = true;
					ToolbarDockStation.this.sideBeneathMouse = this
							.getSideDockableBeneathMouse();
					// without this line, line is displayed only on the first
					// component met
					ToolbarDockStation.this.mainPanel.repaint();
				}
			};
		} else{
			return null;
		}
	}

	/**
	 * Drop thanks to information collect by dropInfo
	 * 
	 * @param dropInfo
	 */
	@Override
	protected void drop( StationDropOperation dropInfo ){
		@SuppressWarnings("unchecked")
		ToolbarDropInfo<ToolbarDockStation> dropInfoToolbar = (ToolbarDropInfo<ToolbarDockStation>) dropInfo;
		if (dropInfoToolbar.getItemPositionVSBeneathDockable() != Position.CENTER){
			// Note: Computation of index to insert drag dockable is not the
			// same between a move() and a drop(), because with a move() it is
			// as if the drag dockable were remove first then added again in the
			// list -> so the list is shrunk and the index are shifted behind
			// the remove dockable. (Note: It's weird because indeed drag() is
			// called after move()...)
			int dropIndex;
			int indexBeneathMouse = indexOf(dropInfoToolbar
					.getDockableBeneathMouse());
			if (dropInfoToolbar.isMove()){
				int shift = 0;
				if (dropInfoToolbar.getItemPositionVSBeneathDockable() == Position.NORTH
						|| dropInfoToolbar.getItemPositionVSBeneathDockable() == Position.WEST){
					// index shifted because the drag dockable is above (or
					// at the left of) the dockable beneath mouse
					shift = -1;
				}
				if (dropInfoToolbar.getSideDockableBeneathMouse() == Position.SOUTH
						|| dropInfoToolbar.getSideDockableBeneathMouse() == Position.EAST){
					// the drag dockable is put below (or at the right of) the
					// dockable beneath mouse
					dropIndex = indexBeneathMouse + 1 + shift;
				} else{
					// the drag dockable is put above (or at the left of) the
					// dockable beneath mouse
					dropIndex = indexBeneathMouse + shift;
				}
				move(dropInfoToolbar.getItem(), dropIndex);
			} else{
				if (dropInfoToolbar.getSideDockableBeneathMouse() == Position.SOUTH
						|| dropInfoToolbar.getSideDockableBeneathMouse() == Position.EAST){
					// the drag dockable is put below (or at right of) the
					// dockable beneath mouse
					drop(dropInfoToolbar.getItem(), indexBeneathMouse + 1);
				} else{
					// the drag dockable is put above (or at left of) the
					// dockable beneath mouse
					drop(dropInfoToolbar.getItem(), indexBeneathMouse);
				}
			}
		}
	}

	@Override
	public void drop( Dockable dockable ){
		// System.out.println(this.toString() + "## drop(Dockable dockable)##");
		this.drop(dockable, getDockableCount(), true);
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

	protected boolean drop( Dockable dockable, int index, boolean force ){
		// note: merging of two ToolbarGroupDockStations is done by the
		// ToolbarGroupDockStationMerger
		// System.out.println(this.toString()
		// + "## drop(Dockable dockable, int index)##");
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

	/**
	 * Adds dockable at the specified index. This method should be called in
	 * case of move action (it means when the dockable to insert already belongs
	 * to this station), because in this case this dockable was removed first.
	 * 
	 * @param dockable
	 *            the dockable to insert
	 * @param index
	 *            the index where insert the dockable
	 */
	protected void move( Dockable dockable, int index ){
		DockController controller = getController();
		try{
			if (controller != null){
				controller.freezeLayout();
			}
			this.add(dockable, index);
		} finally{
			if (controller != null){
				controller.meltLayout();
			}
		}
	}

	protected void add( Dockable dockable, int index ){
		add(dockable, index, null);
	}

	protected void add( Dockable dockable, int index, Path placeholder ){
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		// Case where dockable is instance of ToolbarDockStation is handled by
		// the "ToolbarDockStationMerger"
		// Case where dockable is instance of ToolbarGroupDockStation is handled
		// by the "ToolbarStrategy.ensureToolbarLayer" method
		dockable = getToolbarStrategy().ensureToolbarLayer(this, dockable);
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this,
				dockable);
		try{
			listeners.fireDockableAdding(dockable);
			int inserted = -1;

			StationChildHandle handle = new StationChildHandle(this,
					displayers, dockable, title);
			handle.updateDisplayer();

			if (placeholder != null
					&& dockables.getDockableAt(placeholder) == null){
				inserted = dockables.put(placeholder, handle);
			} else if (placeholder != null){
				index = dockables.getDockableIndex(placeholder);
			}

			if (inserted == -1){
				getDockables().add(index, handle);
			} else{
				index = inserted;
			}

			insertAt(handle, index);
			listeners.fireDockableAdded(dockable);
			fireDockablesRepositioned(index + 1);
		} finally{
			token.release();
		}
	}

	protected void insertAt( StationChildHandle handle, int index ){
		Dockable dockable = handle.getDockable();

		dockable.setDockParent(this);
		if (dockable instanceof OrientedDockStation){
			if (getOrientation() != null){
				// it would be possible that this station was not already
				// oriented. This is the case when this station is
				// instantiated but not drop in any station which could give it
				// an orientation
				((OrientedDockStation) dockable)
						.setOrientation(getOrientation());
			}
		}
		mainPanel.getContentPane().add(handle.getDisplayer().getComponent(),
				index);
		mainPanel.getContentPane().invalidate();

		// mainPanel.getContentPane().setBounds( 0, 0,
		// mainPanel.getContentPane().getPreferredSize().width,
		// mainPanel.getContentPane().getPreferredSize().height );
		// mainPanel.setPreferredSize( new Dimension(
		// mainPanel.getContentPane().getPreferredSize().width,
		// mainPanel.getContentPane().getPreferredSize().height ) );
		// mainPanel.doLayout();
		mainPanel.revalidate();
		mainPanel.getContentPane().repaint();
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

	/**
	 * Removes <code>dockable</code> from this station.<br>
	 * Note: clients may need to invoke {@link DockController#freezeLayout()}
	 * and {@link DockController#meltLayout()} to ensure none else adds or
	 * removes <code>Dockable</code>s.
	 * 
	 * @param dockable
	 *            the child to remove
	 */
	@Override
	protected void remove( Dockable dockable ){
		DockUtilities.checkLayoutLocked();
		int index = this.indexOf(dockable);
		StationChildHandle handle = dockables.dockables().get(index);

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
			mainPanel.revalidate();
			mainPanel.repaint();
			handle.destroy();
			listeners.fireDockableRemoved(dockable);
			fireDockablesRepositioned(index);
		} finally{
			token.release();
		}
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
	protected void remove( int index ){
		DockUtilities.checkLayoutLocked();
		StationChildHandle handle = dockables.dockables().get(index);
		Dockable dockable = getDockable(index);

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
	public void replace( Dockable old, Dockable next ){
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

	// ########################################################
	// ###################### UI Managing #####################
	// ########################################################

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme(this, new ToolbarDockStationFactory());
	}

	@Override
	protected DefaultDisplayerFactoryValue createDisplayerFactory(){
		return new DefaultDisplayerFactoryValue(ThemeManager.DISPLAYER_FACTORY
				+ ".toolbar", this);
	}

	@Override
	protected String getDisplayerId(){
		return DISPLAYER_ID;
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

		private final int INSETS_SIZE = 1;

		/**
		 * A panel with a fixed size (minimum, maximum and preferred size have
		 * same values).
		 * 
		 * @author Herve Guillaume
		 * 
		 */
		@SuppressWarnings("serial")
		private class SizeFixedPanel extends JPanel{
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
			basePane.add(dockablePane);
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
			return getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize(){
			return getPreferredSize();
		}

		/**
		 * Update alignment with regards to the current orientation of this
		 * {@linl ToolbarDockStation}
		 */
		public void updateAlignment(){
			if (ToolbarDockStation.this.getOrientation() != null){
				switch (ToolbarDockStation.this.getOrientation()) {
				case HORIZONTAL:
					basePane.setBorder(new EmptyBorder(new Insets(0,
							INSETS_SIZE, 0, INSETS_SIZE + 1)));
					dockablePane.setLayout(new BoxLayout(dockablePane,
							BoxLayout.X_AXIS));
					basePane.setLayout(new BoxLayout(basePane, BoxLayout.X_AXIS));
					dockablePane.setAlignmentY(Component.CENTER_ALIGNMENT);
					basePane.setAlignmentY(Component.CENTER_ALIGNMENT);
					dockablePane.setAlignmentX(Component.LEFT_ALIGNMENT);
					basePane.setAlignmentX(Component.LEFT_ALIGNMENT);
					break;
				case VERTICAL:
					basePane.setBorder(new EmptyBorder(new Insets(INSETS_SIZE,
							0, INSETS_SIZE + 1, 0)));
					dockablePane.setLayout(new BoxLayout(dockablePane,
							BoxLayout.Y_AXIS));
					basePane.setLayout(new BoxLayout(basePane, BoxLayout.Y_AXIS));
					dockablePane.setAlignmentY(Component.TOP_ALIGNMENT);
					basePane.setAlignmentY(Component.TOP_ALIGNMENT);
					dockablePane.setAlignmentX(Component.CENTER_ALIGNMENT);
					basePane.setAlignmentX(Component.CENTER_ALIGNMENT);
					break;
				default:
					throw new IllegalArgumentException();
				}
			}
		}

		@Override
		protected void paintOverlay( Graphics g ){
			Graphics2D g2D = (Graphics2D) g;
			// DefaultStationPaintValue paint = getPaint();
			if (prepareDropDraw){
				if (indexBeneathMouse != -1){
					Component componentBeneathMouse = getDockables()
							.get(indexBeneathMouse).getDisplayer()
							.getComponent();
					if (componentBeneathMouse != null){
						Rectangle rectToolbar = basePane.getBounds();
						//Color color = this.getController().getColors().get("paint.line");
						Color color = new Color(16, 138, 230, 50);
						Rectangle2D rect = new Rectangle2D.Double(
								rectToolbar.x, rectToolbar.y,
								rectToolbar.width, rectToolbar.height);
						g2D.setColor(color);
						g2D.setStroke(new BasicStroke(2));
						g2D.fill(rect);
						g2D.setColor(Color.RED);

						// WARNING:
						// 1. This rectangle stands for the component beneath
						// mouse. His coordinates are in the frame of reference
						// his direct parent.
						// 2. g is in the frame of reference of the overlayPanel
						// 3. So we need to translate this rectangle in the
						// frame of reference of the overlay panel, which is the
						// same that the base pane
						Rectangle rectBeneathMouse = componentBeneathMouse
								.getBounds();
						Point pBeneath = rectBeneathMouse.getLocation();
						SwingUtilities.convertPointToScreen(pBeneath,
								componentBeneathMouse.getParent());
						SwingUtilities.convertPointFromScreen(pBeneath,
								this.getBasePane());
						Rectangle rectangleTranslated = new Rectangle(
								pBeneath.x, pBeneath.y, rectBeneathMouse.width,
								rectBeneathMouse.height);
						switch (ToolbarDockStation.this.getOrientation()) {
						case VERTICAL:
							int y;
							if (sideBeneathMouse == Position.NORTH){
								y = rectangleTranslated.y;
							} else{
								y = rectangleTranslated.y
										+ rectangleTranslated.height;
							}
							// g2D.drawLine(rectangleTranslated.x, y,
							// rectangleTranslated.x
							// + rectangleTranslated.width, y);
							paint.drawInsertionLine(g, rectangleTranslated.x,
									y, rectangleTranslated.x
											+ rectangleTranslated.width, y);
							break;
						case HORIZONTAL:
							int x;
							if (sideBeneathMouse == Position.WEST){
								x = rectangleTranslated.x;
							} else{
								x = rectangleTranslated.x
										+ rectangleTranslated.width;
							}

							// g2D.drawLine(x, rectangleTranslated.y, x,
							// rectangleTranslated.y
							// + rectangleTranslated.height);
							paint.drawInsertionLine(g, x,
									rectangleTranslated.y, x,
									rectangleTranslated.y
											+ rectangleTranslated.height);
						}
					}
				}
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

	// TODO don't use ToolbarProperty but a custom class, would be much safer
	// if the layout is screwed up

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
	protected DockableProperty getDockableProperty( Dockable child,
			Dockable target, int index, Path placeholder ){
		return new ToolbarProperty(index, placeholder);
	}

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
	protected boolean isValidProperty( DockableProperty property ){
		return property instanceof ToolbarProperty;
	}

	/**
	 * Gets the location of a {@link Dockable} on this station. Called only if
	 * <code>property</code> passed {@link #isValidProperty(DockableProperty)}.
	 * 
	 * @param property
	 *            some property created by
	 *            {@link #getDockableProperty(Dockable, Dockable, int, Path)}
	 * @return the index parameter
	 */
	protected int getIndex( DockableProperty property ){
		return ((ToolbarProperty) property).getIndex();
	}

	protected Path getPlaceholder( DockableProperty property ){
		return ((ToolbarProperty) property).getPlaceholder();
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
										ToolbarDockStation.this, dockable);
								token = DockHierarchyLock.acquireLinking(
										ToolbarDockStation.this, dockable);
								listeners.fireDockableAdding(dockable);
								return new StationChildHandle(
										ToolbarDockStation.this, displayers,
										dockable, title);
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

	@Override
	public void move( Dockable dockable, DockableProperty property ){
		// TODO pending
	}

}
