package bibliothek.gui.dock.action.views.dropdown;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.action.views.menu.MenuViewItem;

/**
 * An item that is shown in the menu of a drop-down-button and can be
 * selected by the button.
 * @author Benjamin Sigg
 */
public interface DropDownViewItem extends MenuViewItem<JComponent> {	
	/**
	 * Invoked if the item is triggered from outside. The item should
	 * call the method of its action, that causes the action to execute
	 * its natural code (for example: a checkbox may change their selected-state).
	 */
	public void triggered();
	
	/**
	 * Sends the current settings of this item to the view. The values
	 * can be changed as long as the view is registered.
	 * @param view the view, might be <code>null</code>
	 */
	public void setView( DropDownView view );
	
	/**
	 * Tells whether this item can be selected by the button. The selected
	 * item is shown directly on the button. Special items like a separator
	 * should return <code>false</code>.
	 * @return whether the item can be selected
	 */
	public boolean isSelectable();
	
	/**
	 * Tells whether the item can be triggered if it is on the button or
	 * in the menu.
	 * @param selected whether the item is selected or in the menu
	 * @return <code>true</code> if the item can be triggered
	 */
	public boolean isTriggerable( boolean selected );
}
