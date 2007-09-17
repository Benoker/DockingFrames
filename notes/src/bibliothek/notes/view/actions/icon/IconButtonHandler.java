package bibliothek.notes.view.actions.icon;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.action.BasicHandler;
import bibliothek.notes.view.actions.IconAction;

/**
 * A connection between a {@link bibliothek.gui.dock.themes.basic.action.BasicButtonModel button model}
 * and the {@link IconGrid}.<br>
 * This handler is called whenever the user clicks on the graphical 
 * representation of an {@link IconAction}, and then opens a popup-panel
 * containing an {@link IconGrid}.
 * @author Benjamin Sigg
 * @see IconAction
 */
public class IconButtonHandler extends BasicHandler<IconAction> {
	/**
	 * Creates a new handler.
	 * @param action the action to monitor
	 * @param dockable the Dockable for which the action might be triggered
	 */
	public IconButtonHandler( IconAction action, Dockable dockable ){
		super( action, dockable );
	}
	
	@Override
	public void triggered(){
		JComponent owner = getModel().getOwner();
		IconGrid.GRID.changeIcon( getAction().getNote(), owner, 0, owner.getHeight() );
	}
}
