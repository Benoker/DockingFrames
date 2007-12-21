package bibliothek.paint.view.action;

import bibliothek.gui.dock.facile.action.FButton;
import bibliothek.paint.model.Picture;
import bibliothek.paint.util.Resources;
import bibliothek.paint.view.Page;

/**
 * Removes the last drawn shape from a picture.
 * @author Benjamin Sigg
 *
 */
public class EraseLastShape extends FButton{
	/** the page this actions belongs to */
	private Page page;
	
	/**
	 * Creates a new action.
	 * @param page the page this action belongs to
	 */
	public EraseLastShape( Page page ){
		this.page = page;
		
		setText( "Erase" );
		setTooltip( "Erases the newest shape of the picture" );
		setIcon( Resources.getIcon( "shape.remove" ) );
	}
	
	@Override
	protected void action(){
		Picture picture = page.getPicture();
		if( picture != null )
			picture.removeLast();
	}
}
