package bibliothek.gui.dock.action.views.buttons;

import javax.swing.JComponent;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;

/**
 * A connection between a {@link StandardDockAction} and a {@link MiniButton}.
 * @author Benjamin Sigg
 *
 * @param <D> the type of action supported by this handler
 * @param <T> the type of button supported by this handler
 */
public interface MiniButtonHandler<D extends DockAction, T extends MiniButton> extends TitleViewItem<JComponent> {
	/**
	 * Called by <code>button</code> when the mouse is released.
	 */
	public abstract void triggered();
	
	/**
	 * Gets the button which shows the contents of this model.
	 * @return the button
	 */
	public T getButton();
	
	/**
	 * Gets the Dockable which owns the action of this model.
	 * @return the owner of {@link #getAction() the action}
	 */
	public Dockable getDockable();
	
	/**
	 * Gets the action that is observed by this model.
	 * @return the action
	 */
	public D getAction();
}
