package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.Graphics;

import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BorderedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * A {@link RectEclipseBorder} painting round edges at the side at which tabs are 
 * painted. 
 * @author Benjamin Sigg
 */
public class OwnedRectEclipseBorder extends RectEclipseBorder{
	private BorderedComponent owner;

	/**
	 * Creates a new border.
	 * @param owner the component which paints this border
	 * @param controller the controller in whose realm this border works
	 * @param fillEdges whether to paint the edges opaque
	 */
	public OwnedRectEclipseBorder( BorderedComponent owner, DockController controller, boolean fillEdges ){
		super( controller, fillEdges, 0 );
		if( owner == null )
			throw new IllegalArgumentException( "owner must not be null" );
		this.owner = owner;
	}
	
	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ){
		TabPlacement placement = owner.getDockTabPlacement();
		if( placement != null ){
			switch( placement ){
				case TOP_OF_DOCKABLE:
					setRoundEdges( TOP_LEFT | TOP_RIGHT );
					break;
				case BOTTOM_OF_DOCKABLE:
					setRoundEdges( BOTTOM_LEFT | BOTTOM_RIGHT );
					break;
				case LEFT_OF_DOCKABLE:
					setRoundEdges( BOTTOM_LEFT | TOP_LEFT );
					break;
				case RIGHT_OF_DOCKABLE:
					setRoundEdges( BOTTOM_RIGHT | TOP_RIGHT );
					break;
			}
		}
		
		super.paintBorder( c, g, x, y, width, height );
	}
}
