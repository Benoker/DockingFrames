package bibliothek.gui.dock.action.views.dropdown;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ButtonDockAction;

/**
 * A connection between a {@link ButtonDockAction} and a drop-down-button.
 * @author Benjamin Sigg
 */
public class ButtonDropDownHandler extends AbstractDropDownHandler<ButtonDockAction> {
	/** a listener to the menuitem of this handler */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new handler.
	 * @param action the action to observe
	 * @param dockable the Dockable for which the action is shown
	 * @param item the item that represents the action
	 */
	public ButtonDropDownHandler( ButtonDockAction action, Dockable dockable, JMenuItem item ){
		super( action, dockable, item );
	}

	public void triggered(){
		action.action( dockable );
	}
	
	@Override
	public void bind(){
		super.bind();
		item.addActionListener( listener );
	}
	
	@Override
	public void unbind(){
		item.removeActionListener( listener );
		super.unbind();
	}
	
	/**
	 * A listener added to the menuitem. The listener calls
	 * {@link ButtonDropDownHandler#triggered()} whenever the menuitem
	 * is clicked.
	 * @author Benjamin Sigg
	 */
	private class Listener implements ActionListener{
		public void actionPerformed( ActionEvent e ){
			if( action.isDropDownTriggerable( dockable, false ))
				triggered();
		}
	}
}
