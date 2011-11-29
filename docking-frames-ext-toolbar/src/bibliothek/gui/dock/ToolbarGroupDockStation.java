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
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.layer.SideSnapDropLayer;
import bibliothek.gui.dock.station.toolbar.layer.ToolbarSlimDropLayer;
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
 * {@link ToolbarDockStation}. As dockable it can be put in
 * {@link DockStation} which implements marker interface
 * {@link ToolbarInterface} or in {@link ScreenDockStation}, so that a
 * ToolbarDockStation can be floattable. As DockStation it accepts a
 * {@link ToolbarElementInterface}. All the ComponentDockable extracted from the
 * element are merged together and wrapped in a {@link ToolbarDockStation}
 * before to be added
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
	public static final String DISPLAYER_ID = "toolbar.groupk";

	/** 
	 *  Size of the border outside this station where a {@link Dockable} will still
	 *  be considered to be dropped onto this station. Measured in pixel.
	 */
	private int borderSideSnapSize = 0;
	/** 
	 * Whether the bounds of this station are slightly bigger than the station itself.
	 * Used together with {@link #borderSideSnapSize} to grab Dockables "out of the sky".
	 * The default is <code>true</code>. 
	 */
	private boolean allowSideSnap = true;

	/**
	 * Creates a new {@link ToolbarGroupDockStation}.
	 */
	public ToolbarGroupDockStation(){
		init();
		this.mainPanel.getContentPane().setBackground(Color.YELLOW);
		this.mainPanel.getBasePane().setBackground(Color.ORANGE);
	}

	@Override
	public String getFactoryID(){
		return ToolbarGroupDockStationFactory.ID;
	}

	@Override
	protected String getDisplayerId(){
		return DISPLAYER_ID;
	}

	@Override
	protected void callDockUiUpdateTheme() throws IOException{
		DockUI.updateTheme(this, new ToolbarGroupDockStationFactory());
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
		return new DockStationDropLayer[]{
				new DefaultDropLayer( this ),
				new SideSnapDropLayer( this ),
		};
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
	
	/**
	 * Sets whether {@link Dockable Dockables} which are dragged near
	 * the station are captured and added to this station.
	 * @param allowSideSnap <code>true</code> if the station can
	 * snap Dockables which are near.
	 * @see #setBorderSideSnapSize(int)
	 */
	public void setAllowSideSnap( boolean allowSideSnap ){
		this.allowSideSnap = allowSideSnap;
	}

	/**
	 * Tells whether the station can grab Dockables which are dragged
	 * near the station.
	 * @return <code>true</code> if grabbing is allowed
	 * @see #setAllowSideSnap(boolean)
	 */
	public boolean isAllowSideSnap(){
		return allowSideSnap;
	}
	
}
