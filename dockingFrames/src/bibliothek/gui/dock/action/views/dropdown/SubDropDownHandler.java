package bibliothek.gui.dock.action.views.dropdown;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.action.views.menu.MenuViewItem;

/**
 * A handler that connects non-selectable, non-triggerable items with a 
 * drop-down-button.
 * @author Benjamin Sigg
 */
public class SubDropDownHandler implements DropDownViewItem {
	/** the view of the item in the menu */
	private MenuViewItem<JComponent> view;
	
	/**
	 * Creates a new handler.
	 * @param view the item as it will appear in the menu
	 */
	public SubDropDownHandler( MenuViewItem<JComponent> view ){
		this.view = view;
	}

	public void triggered(){
		// that will never happen
	}

	public void setView( DropDownView view ){
		// will never happen
	}
	
	public boolean isSelectable(){
		return false;
	}

	public boolean isTriggerable( boolean selected ){
		return false;
	}

	public void addActionListener( ActionListener listener ){
		view.addActionListener( listener );
	}

	public void removeActionListener( ActionListener listener ){
		view.removeActionListener( listener );
	}

	public void bind(){
		view.bind();
	}

	public DockAction getAction(){
		return view.getAction();
	}

	public JComponent getItem(){
		return view.getItem();
	}

	public void unbind(){
		view.unbind();
	}
}
