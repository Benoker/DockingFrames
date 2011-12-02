package bibliothek.gui.dock.station.toolbar.title;
//package bibliothek.gui.dock.station.toolbar;
//
//import bibliothek.gui.DockStation;
//import bibliothek.gui.Dockable;
//import bibliothek.gui.Position;
//import bibliothek.gui.dock.ToolbarContainerDockStation;
//import bibliothek.gui.dock.ToolbarDockStation;
//import bibliothek.gui.dock.title.DockTitle;
//import bibliothek.gui.dock.title.DockTitleRequest;
//import bibliothek.gui.dock.title.DockTitleVersion;
//
//public class ToolbarDockTitleRequest extends DockTitleRequest{
//
//	/**
//	 * Inform this DockTitleRequest on the position of the associated Toolbar
//	 */
//	private Position position = Position.NORTH;
//
//	public ToolbarDockTitleRequest( DockStation parent, Dockable target,
//			DockTitleVersion version ){
//		super(parent, target, version);
//	}
//
//	@Override
//	protected void answer( DockTitle previous, DockTitle title ){
//		ToolbarContainerDockStation station = (ToolbarContainerDockStation) this
//				.getParent();
//		ToolbarDockStation dockable = (ToolbarDockStation) this.getTarget();
//		if (previous != null){
//			// Dockables know which DockTitle is associated with them. On
//			// removal you need to call "unbind"
//			this.getTarget().unbind(previous);
//			dockable.getTitlePane().remove(previous.getComponent());
//		}
//		if (title != null){
//			// here you can configure the title, e.g. whether it is painted
//			// horizontal or vertical
//			title.setOrientation(toDockTitleOrientation(station
//					.getOrientation(getPosition())));
//			// you need to tell the Dockable about the DockTitle. Otherwise the
//			// title does not update its text or icon
//			this.getTarget().bind(title);
//			// and you need to show the Component
//			// targetparent.add(title.getComponent());
//			dockable.getTitlePane().add(title.getComponent());
//
//		}
//	}
//
//	public Position getPosition(){
//		return position;
//	}
//
//	public void setPosition( Position position ){
//		this.position = position;
//	}
//
//	private DockTitle.Orientation toDockTitleOrientation(
//			bibliothek.gui.Orientation stationOrientation ){
//		switch (stationOrientation) {
//		case VERTICAL:
//			return DockTitle.Orientation.FREE_HORIZONTAL;
//		case HORIZONTAL:
//			return DockTitle.Orientation.FREE_VERTICAL;
//		default:
//			throw new IllegalArgumentException();
//		}
//	}
//
//}
