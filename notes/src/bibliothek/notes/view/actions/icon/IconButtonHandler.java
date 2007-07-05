package bibliothek.notes.view.actions.icon;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.action.BasicHandler;
import bibliothek.notes.view.actions.IconAction;

public class IconButtonHandler extends BasicHandler<IconAction> {
	public IconButtonHandler( IconAction action, Dockable dockable ){
		super( action, dockable );
	}
	
	@Override
	public void triggered(){
		JComponent owner = getModel().getOwner();
		IconGrid.GRID.changeIcon( getAction().getNote(), owner, 0, owner.getHeight() );
	}
}
