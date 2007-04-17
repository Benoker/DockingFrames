package bibliothek.gui.dock.action.views.buttons;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MenuDockAction;

/**
 * A handler that will show a popup-menu whenever the user triggers the 
 * handler.
 * @author Benjamin Sigg
 */
public class MenuMiniButtonHandler extends AbstractMiniButtonHandler<MenuDockAction, MiniButton> {

	/**
	 * Creates the new handler.
	 * @param action the action for which a menu will be shown
	 * @param dockable the owner of the action
	 * @param button the button that must be pressed in order to show the menu
	 */
	public MenuMiniButtonHandler( MenuDockAction action, Dockable dockable, MiniButton button ){
		super( action, dockable, button );
	}

	public void triggered(){
		final DockActionSource source = getAction().getMenu( getDockable() );
        if( source != null && source.getDockActionCount() > 0 ){
            ActionPopup popup = new ActionPopup( false ){
                @Override
                protected Dockable getDockable() {
                    return MenuMiniButtonHandler.this.getDockable();
                }

                @Override
                protected DockActionSource getSource() {
                    return source;
                }

                @Override
                protected boolean isEnabled() {
                    return true;
                }
            };
            
            MiniButton button = getButton();
            popup.popup( button, 0, button.getHeight() );
        }
	}
}
