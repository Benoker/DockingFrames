package bibliothek.paint.view.action;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import bibliothek.gui.dock.facile.action.FButton;
import bibliothek.paint.util.Resources;
import bibliothek.paint.view.Page;

/**
 * A button that shrinks a {@link Page} when clicked.
 * @author Benjamin Sigg
 *
 */
public class ZoomOut extends FButton {
	/** the page whose zoomfactor will be changed */
	private Page page;
	
	/**
	 * Creates a new button
	 * @param page the page whose zoomfactor will be changed
	 */
	public ZoomOut( Page page ){
		this.page = page;
		setText( "Zoom out" );
		setIcon( Resources.getIcon( "zoom.out" ) );
		setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_MINUS, InputEvent.CTRL_MASK ) );
	}
	
	@Override
	protected void action(){
		double zoom = 0.5*page.getZoom();
		if( zoom >= 1.0 / 32.0 ){
			page.setZoom( zoom );
		}
	}
}