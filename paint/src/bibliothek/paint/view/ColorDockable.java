package bibliothek.paint.view;

import bibliothek.gui.dock.facile.FSingleDockable;

/**
 * A Dockable that lets the user choose the color for new
 * {@link bibliothek.paint.model.Shape}s.
 * @author Benjamin Sigg
 *
 */
public class ColorDockable extends FSingleDockable{
	private ViewManager manager;
	
	public ColorDockable( ViewManager manager ){
		super( "ColorDockable" );
		this.manager = manager;
		
		setCloseable( true );
		setMinimizable( true );
		setExternalizable( true );
		setMaximizable( false );
	}
}
