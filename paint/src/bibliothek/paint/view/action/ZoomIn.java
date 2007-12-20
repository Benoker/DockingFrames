package bibliothek.paint.view.action;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import bibliothek.gui.dock.facile.action.FButton;
import bibliothek.paint.util.Resources;
import bibliothek.paint.view.Page;

/**
 * A button that enlarges a {@link Page} when clicked.
 * @author Benjamin Sigg
 *
 */
public class ZoomIn extends FButton {
	/** the page whose zoomfactor will be changed */
	private Page page;
	
	/**
	 * Creates a new button
	 * @param page the page whose zoomfactor will be changed
	 */
	public ZoomIn( Page page ){
		this.page = page;
		setText( "Zoom in" );
		setIcon( Resources.getIcon( "zoom.in" ) );
		setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PLUS, InputEvent.CTRL_MASK ) );
	}
	
	@Override
	protected void action(){
		double zoom = 2*page.getZoom();
		if( zoom <= 32.0 ){
			page.setZoom( zoom );
		}
	}
}
