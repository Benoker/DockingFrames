package bibliothek.gui.dock;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.Position;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarDropInfo;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.layer.SideSnapDropLayer;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarSlimDropLayer;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
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

	/**
	 * Size of the lateral zone where no drop action can be done (Measured in
	 * pixel).
	 */
	private int lateralNodropZoneSize = 4;

	/**
	 * Creates a new {@link ToolbarDockStation}.
	 */
	public ToolbarDockStation(){
		init();
		this.mainPanel.getContentPane().setBackground(Color.GREEN);
		this.mainPanel.getBasePane().setBackground(Color.CYAN);
	}

	protected void init(){
		mainPanel = new OverpaintablePanelBaseToolbar();
		paint = new DefaultStationPaintValue(ThemeManager.STATION_PAINT
				+ ".toolbar", this);
		setOrientation(this.getOrientation());
		displayerFactory = createDisplayerFactory();
		displayers = new DisplayerCollection(this, displayerFactory,
				getDisplayerId());
		displayers
				.addDockableDisplayerListener(new DockableDisplayerListener(){
					public void discard( DockableDisplayer displayer ){
						ToolbarDockStation.this.discard(displayer);
					}
				});

		setTitleIcon(null);
	}

	@Override
	public String getFactoryID(){
		return ToolbarDockStationFactory.ID;
	}

	@Override
	protected String getDisplayerId(){
		return DISPLAYER_ID;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme(this, new ToolbarDockStationFactory());
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

	@Override
	public DockStationDropLayer[] getLayers(){
		return new DockStationDropLayer[] { new ToolbarSlimDropLayer(this) };
	}

	@Override
	public boolean accept( Dockable child ){
		System.out.println(this.toString() + "## accept(Dockable child) ##");
		return getToolbarStrategy().isToolbarGroupPart(child);
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println(this.toString()
				+ "## accept(DockStation station) ##");
		return getToolbarStrategy().isToolbarGroupPartParent(station, this);
	}

	// TODO don't use ToolbarProperty but a custom class, would be much safer if
	// the layout is screwed up

	@Override
	protected DockableProperty getDockableProperty( Dockable child,
			Dockable target, int index, Path placeholder ){
		return new ToolbarProperty(index, placeholder);
	}

	@Override
	protected boolean isValidProperty( DockableProperty property ){
		return property instanceof ToolbarProperty;
	}

	@Override
	protected int getIndex( DockableProperty property ){
		return ((ToolbarProperty) property).getIndex();
	}

	@Override
	protected Path getPlaceholder( DockableProperty property ){
		return ((ToolbarProperty) property).getPlaceholder();
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
	public StationDropOperation prepareDrop( int mouseX, int mouseY,
			int titleX, int titleY, Dockable dockable ){

		System.out.println(this.toString() + "## prepareDrop(...) ##");
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

	// /////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////

	/**
	 * This panel is used as base of the station. All children of the station
	 * have this panel as parent too. It allows to draw arbitrary figures over
	 * the base panel
	 * 
	 * @author Herve Guillaume
	 */
	protected class OverpaintablePanelBaseToolbar extends
			OverpaintablePanelBase{

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

		// @Override
		// public Dimension getPreferredSize(){
		// Dimension contentPreferredSize =
		// super.getPreferredSize();//dockablePane.getPreferredSize();
		// Dimension basePreferredSize = new Dimension( contentPreferredSize );
		// Insets insets = getInsets();
		// basePreferredSize.height += insets.top + insets.bottom;
		// basePreferredSize.width += insets.left + insets.right;
		// return basePreferredSize;
		// };
		// };

		/**
		 * Creates a new panel
		 */
		public OverpaintablePanelBaseToolbar(){
			// basePane.setBackground(Color.GREEN);
			// dockablePane.setBackground(Color.RED);

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
			if (ToolbarDockStation.this.getOrientation() != null){
				switch (ToolbarDockStation.this.getOrientation()) {
				case HORIZONTAL:
					dockablePane.setLayout(new BoxLayout(dockablePane,
							BoxLayout.X_AXIS));
					basePane.setLayout(new BoxLayout(basePane, BoxLayout.X_AXIS));
					dockablePane.setAlignmentY(Component.CENTER_ALIGNMENT);
					basePane.setAlignmentY(Component.CENTER_ALIGNMENT);
					dockablePane.setAlignmentX(Component.LEFT_ALIGNMENT);
					basePane.setAlignmentX(Component.LEFT_ALIGNMENT);
					break;
				case VERTICAL:
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
			DefaultStationPaintValue paint = getPaint();
			if (prepareDropDraw){
				if (indexBeneathMouse != -1){
					Component componentBeneathMouse = getDockables()
							.get(indexBeneathMouse).getDisplayer()
							.getComponent();
					if (componentBeneathMouse != null){
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
						Rectangle rectBeneathMouse = componentBeneathMouse
								.getBounds();
						Rectangle2D rect = new Rectangle2D.Double(
								rectBeneathMouse.x, rectBeneathMouse.y,
								rectBeneathMouse.width, rectBeneathMouse.height);
						g2D.setColor(Color.RED);
						g2D.setStroke(new BasicStroke(3));
						g2D.draw(rect);
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

}
