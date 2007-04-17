package bibliothek.gui.dock.action.views.menu;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.views.ViewItem;

/**
 * An item that is shown in a menu.
 * @author Benjamin Sigg
 *
 * @param <C> The type of component that represents this view
 */
public interface MenuViewItem<C extends JComponent> extends ViewItem<C> {
	/**
	 * Adds a listener which will be called if this view is triggered. The
	 * listener should only be called, if the user clicked directly onto
	 * this view.
	 * @param listener the new listener
	 */
	public void addActionListener( ActionListener listener );
	
	/**
	 * Removes a listener from this view.
	 * @param listener the listener to remove
	 */
	public void removeActionListener( ActionListener listener );
}
