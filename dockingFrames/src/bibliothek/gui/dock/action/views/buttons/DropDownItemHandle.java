package bibliothek.gui.dock.action.views.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.views.dropdown.DropDownViewItem;

/**
 * Represents an action and a view which are children of a {@link DropDownAction}
 * @author Benjamin Sigg
 */
public class DropDownItemHandle implements ActionListener{
	/** the action */
	private DockAction item;
	/** the view of {@link #item} */
	private DropDownViewItem view;
	/** the dockable for which this item is used */
    private Dockable dockable;
    /** the menu which is the owner of the action */
    private DropDownAction action;
    
	/**
	 * Creates a new item.
	 * @param item the action
	 * @param view the view of <code>item</code>
     * @param dockable the dockable for which the item is used
     * @param action the owner of the item
	 */
	public DropDownItemHandle( DockAction item, DropDownViewItem view, Dockable dockable, DropDownAction action ){
		this.item = item;
		this.view = view;
        this.dockable = dockable;
        this.action = action;
	}
	
    /**
     * Gets the view of the action.
     * @return the view
     */
    public DropDownViewItem getView() {
        return view;
    }
    
	/**
	 * Connects the view.
	 */
	public void bind(){
		view.bind();
		view.addActionListener( this );
	}
	
	/**
	 * Disconnects the view
	 */
	public void unbind(){
		view.removeActionListener( this );
		view.unbind();
	}
	
	public void actionPerformed( ActionEvent e ){
		if( view.isSelectable() )
			action.setSelection( dockable, item );
	}
}