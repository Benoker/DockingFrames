package bibliothek.gui.dock.station.toolbar;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.OrientedDockStation.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation.Position;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * Information where to insert a {@link Dockable} into a
 * {@link ToolbarGroupDockStation} or a Toolbar  {@link ToolbarContainerDockStation}
 * 
 * @author Herve Guillaume
 */
public class ToolbarContainerDropInfo implements StationDropOperation {
	/** The {@link Dockable} which is inserted */
	private Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to the original dock parent of the dockable!)
	 */
	private ToolbarContainerDockStation stationHost;
	/** the drag dockable will be insert inside this {@link Dockable}s */
	private ArrayList<Dockable> associateToolbars;
	/** Store temporary the position beneath the mouse */
	private Position position;
	/** Location of the mouse */
	public int mouseX, mouseY;
	/**
	 * index computed with reference take on upper left corner on the underneath
	 * dockables
	 */
	private int indexUpperLeft = -1;
	/**
	 * index computed with reference take on bottom right corner on the
	 * underneath dockables
	 */
	private int indexBottomRight = -1;

	/**
	 * Constructs a new info.
	 * 
	 * @param station
	 *            the owner of this info
	 * @param dockable
	 *            the {@link Dockable} which will be inserted
	 */
	public ToolbarContainerDropInfo( Dockable dockable, ToolbarContainerDockStation stationHost, ArrayList<Dockable> associateToolbars,
			Position position, int mouseX, int mouseY ){
		this.dragDockable = dockable;
		this.stationHost = stationHost;
		this.associateToolbars = associateToolbars;
		this.position = position;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	public Dockable getItem(){
		return dragDockable;
	}
	
	public DockStation getTarget(){
		return stationHost;
	}

	public void destroy(){
		// nothing to do
	}
	
	public void draw(){
		// TODO	
	}
	
	public boolean isMove(){
		return getItem().getDockParent() == getTarget();
	}
	
	public void execute(){
		stationHost.drop( this );
	}
	
	public CombinerTarget getCombination(){
		// not supported
		return null;
	}
	
	public DisplayerCombinerTarget getDisplayerCombination(){
		// not supported
		return null;
	}
	
	/**
	 * Gets the {@link Position} which will be dropped or moved on the station.
	 * 
	 * @return
	 */
	public Position getPosition(){
		return this.position;
	}

	/**
	 * Gets the <code>index</code> of the component beneath the mouse
	 * 
	 * @return the index
	 */
	public int getIndex( ReferencePoint reference ){
		switch( reference ){
			case UPPERLEFT:
				if( indexUpperLeft == -1 ) {
					indexUpperLeft = computeIndex( associateToolbars, mouseX, mouseY, stationHost.getOrientation( position ), reference );
				}
				return indexUpperLeft;
			case BOTTOMRIGHT:
				if( indexBottomRight == -1 ) {
					indexBottomRight = computeIndex( associateToolbars, mouseX, mouseY, stationHost.getOrientation( position ), reference );
				}
				return indexBottomRight;
			default:
				return 0;
		}
	}

	/**
	 * compute the <code>index</code> of the component beneath the mouse
	 * 
	 * @param list list of the dockables in the middle of which the drag dockable will be inserted
	 * @param mouseX position X of the mouse
	 * @param mouseY position Y of the mouse
	 * @param orientation orientation of the dockables
	 * @param reference reference point used to compute relative position of the dockables
	 * @return the index
	 */
	public int computeIndex( ArrayList<Dockable> list, int mouseX, int mouseY, Orientation orientation, ReferencePoint reference ){
		if( position != null ) {
			int dockableCount = list.size();
			Point mousePoint = new Point( this.mouseX, this.mouseY );
			
			SwingUtilities.convertPointFromScreen( mousePoint, stationHost.getComponent() );
			switch( reference ){
				case UPPERLEFT:
					for( int i = dockableCount - 1; i > -1; i-- ) {
						Point componentPoint = new Point( (int) stationHost.getDockable( i ).getComponent().getBounds().getMinX(), (int) stationHost
								.getDockable( i ).getComponent().getBounds().getMinY() );
						switch( orientation ){
							case VERTICAL:
								if( mousePoint.getY() > componentPoint.getY() ) {
									return i + 1;
								}
								break;
							case HORIZONTAL:
								if( mousePoint.getX() > componentPoint.getX() ) {
									return i + 1;
								}
								break;
						}
					}
				case BOTTOMRIGHT:
					for( int i = dockableCount - 1; i > -1; i-- ) {
						Point componentPoint = new Point( (int) stationHost.getDockable( i ).getComponent().getBounds().getMaxX(), (int) stationHost
								.getDockable( i ).getComponent().getBounds().getMaxY() );
						switch( orientation ){
							case VERTICAL:
								if( mousePoint.getY() > componentPoint.getY() ) {
									return i + 1;
								}
								break;
							case HORIZONTAL:
								if( mousePoint.getX() > componentPoint.getX() ) {
									return i + 1;
								}
								break;
						}
					}
			}
			return 0;
		}
		else {
			return -1;
		}
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@' + Integer.toHexString( this.hashCode() );
	}

}
