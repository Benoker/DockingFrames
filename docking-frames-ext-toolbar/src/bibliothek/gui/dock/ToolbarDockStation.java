package bibliothek.gui.dock;

import java.awt.Color;
import java.io.IOException;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.layer.SideSnapDropLayer;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarSlimDropLayer;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
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
	
	protected abstract StationDropOperation createStationDropOperation() {
		return 
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

}
