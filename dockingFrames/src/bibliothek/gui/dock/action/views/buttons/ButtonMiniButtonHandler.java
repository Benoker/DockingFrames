package bibliothek.gui.dock.action.views.buttons;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ButtonDockAction;

/**
 * A Connection between a {@link ButtonDockAction} and a {@link MiniButton}.
 * The handler calls the {@link ButtonDockAction#action(Dockable)} whenever
 * triggered.
 * @author Benjamin Sigg
 *
 */
public class ButtonMiniButtonHandler extends AbstractMiniButtonHandler<ButtonDockAction, MiniButton> {
	/**
	 * Creates a new handler.
	 * @param action the action to handle
	 * @param dockable the owner of the action
	 * @param button the button to manage
	 */
	public ButtonMiniButtonHandler( ButtonDockAction action, Dockable dockable, MiniButton button ){
		super( action, dockable, button );
	}

	public void triggered(){
		getAction().action( getDockable() );
	}
}
