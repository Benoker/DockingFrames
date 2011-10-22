package bibliothek.gui.dock;

import java.io.IOException;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.util.Path;

/**
 * A {@link Dockable} and a {@link Dockstation} which stands for a group of
 * {@link ComponentDockable}. As dockable it can be put in {@link DockStation}
 * which implements marker interface {@link ToolbarInterface}. As DockStation it
 * accept a {@link ComponentDockable} or a {@link ToolbarGroupDockStation}
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupDockStation extends AbstractToolbarDockStation {
	/** the id of the {@link DockTitleFactory} which is used by this station */
	public static final String TITLE_ID = "toolbar.group";
	
	/**
	 * Creates a new {@link ToolbarGroupDockStation}.
	 */
	public ToolbarGroupDockStation(){
		init();
	}
	
	@Override
	public String getFactoryID(){
		return ToolbarGroupDockStationFactory.ID;
	}
	
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
	
	@Override
	public boolean accept( Dockable child ){
		System.out.println(this.toString() + "## accept(Dockable child) ##");
		return getToolbarStrategy().isToolbarGroupPart(child);
	}
	
//	@Override
//	public boolean accept( DockStation station ){
//		System.out.println( this.toString() + "## accept(DockStation station) ##" );
//		return getToolbarStrategy().isToolbarGroupPartParent( station, this );
//	}

	// TODO don't use ToolbarProperty but a custom class, would be much safer if the layout is screwed up
	
	@Override
	protected DockableProperty getDockableProperty( Dockable child, Dockable target, int index, Path placeholder ){
		return new ToolbarProperty( index, placeholder );
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
	
	

}
