package bibliothek.gui.dock.action.dropdown;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.views.dropdown.DropDownViewItem;

/**
 * An action that can be child of a {@link DropDownAction}. The properties
 * of this action are read by the view, for example if this action is wrapped into
 * a {@link DropDownViewItem}.
 * @author Benjamin Sigg
 */
public interface DropDownItemAction {
	/**
	 * Tells whether this action can be selected by a {@link DropDownAction},
	 * if it is shown for <code>dockable</code>.
	 * @param dockable the Dockable for which the action is shown
	 * @return <code>true</code> if the action can be selected
	 */
	public boolean isDropDownSelectable( Dockable dockable );

	/**
	 * Tells whether this action can be triggered if it is shown as child of
	 * a {@link DropDownAction}.
	 * @param dockable the Dockable for which the action is shown
	 * @param selected <code>true</code> if the action is selected (the action
	 * is triggered because the user clicks onto the {@link DropDownAction}),
	 * or <code>false</code> if this action is just in a menu.
	 * @return <code>true</code> if the action can be triggered
	 */
	public boolean isDropDownTriggerable( Dockable dockable, boolean selected );
}
