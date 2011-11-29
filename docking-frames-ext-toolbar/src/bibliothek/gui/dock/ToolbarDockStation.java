package bibliothek.gui.dock;

import java.awt.Color;
import java.io.IOException;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarOverrideDropLayer;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.NullTitleFactory;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.util.Path;

/**
 * A {@link Dockable} and a {@link DockStation} which stands a group of
 * {@link ToolbarGroupDockStation}. As dockable it can be put in
 * {@link DockStation} which implements marker interface
 * {@link ToolbarInterface} or in {@link ScreenDockStation}, so that a
 * ToolbarDockStation can be floattable. As DockStation it accepts a
 * {@link ToolbarElementInterface}. All the ComponentDockable extracted from the
 * element are merged together and wrapped in a {@link ToolbarGroupDockStation}
 * before to be added
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
	 * Size of the insets on the two lateral sides of this station, which define
	 * two lateral areas where no drop action can be done (Measured in pixel).
	 */
	private int insetsSideOverrideSize = 25;

	/**
	 * Creates a new {@link ToolbarDockStation}.
	 */
	public ToolbarDockStation(){
		init();
		this.mainPanel.getContentPane().setBackground(Color.YELLOW);
		this.mainPanel.getBasePane().setBackground(Color.ORANGE);
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
				+ ".toolbar", this);
	}

	@Override
	protected DockTitleVersion registerTitle( DockController controller ){
		return controller.getDockTitleManager().getVersion(TITLE_ID,
				NullTitleFactory.INSTANCE);
	}

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

	@Override
	public DockStationDropLayer[] getLayers(){
		return new DockStationDropLayer[] { new DefaultDropLayer(this),
				new ToolbarOverrideDropLayer(this) };
	}

	@Override
	public boolean accept( Dockable child ){
		System.out.println(this.toString() + "## accept(Dockable child) ##");
		return getToolbarStrategy().isToolbarPart(child);
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println(this.toString()
				+ "## accept(DockStation station) ##");
		return getToolbarStrategy().isToolbarGroupPartParent(station, this);
	}

	/**
	 * There are insets on the two lateral sides of this station. These insets
	 * define two areas where no {@link Dockable} can be dropped (so, this is
	 * the parent station which will be notified to this drop action).
	 * 
	 * @param insetsSideOverrideSize
	 *            the size of the insets on the two lateral sides (in pixel)
	 * @throws IllegalArgumentException
	 *             if the size is smaller than 0
	 */
	public void setInsetsSideOverrideSize( int insetsSideOverrideSize ){
		if (insetsSideOverrideSize < 0)
			throw new IllegalArgumentException(
					"borderSideSnapeSize must not be less than 0");
		/**
		 * Size of the insets on the two lateral sides of this station, which
		 * define the area where a drop action is transfered to the parent
		 * station. Measured in pixel.
		 */
		this.insetsSideOverrideSize = insetsSideOverrideSize;
	}

	/**
	 * Gets the size of the insets on the two lateral sides which define the two
	 * lateral areas where dropped action can be done.
	 * 
	 * @return the size, in pixel, of the insets on the two lateral sides
	 */
	public int getInsetsSideOverrideSize(){
		return insetsSideOverrideSize;
	}

}
