package bibliothek.gui.dock.action.dropdown;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.views.dropdown.DropDownViewItem;

/**
 * A filter is used by views which want to display a {@link DropDownAction}, but
 * have to show elements of the selected action as well.<br>
 * A filter is a {@link DropDownView} which forwards their properties to another
 * DropDownView. The filter may cache some values, but has to forward them if the
 * {@link #update(DropDownViewItem) update}-method is invoked.
 * @author Benjamin Sigg
 */
public abstract class DropDownFilter implements DropDownView{
	/** The Dockable for which this filter is used */
	private Dockable dockable;
	
	/** the action which is filtered */
	private DropDownAction action;
	
	/** a view to write into */
	private DropDownView view;
	
	/**
	 * Creates a new filter
	 * @param dockable the owner of the view
	 * @param action the action that is filtered
	 * @param view a view where this filter should write into
	 */
	public DropDownFilter( Dockable dockable, DropDownAction action, DropDownView view ){
		this.dockable = dockable;
		this.action = action;
		this.view = view;
	}
	
	/**
	 * Gets the {@link Dockable} for which the {@link #getAction() action} is
	 * shown.
	 * @return the owner of the action
	 */
	public Dockable getDockable(){
		return dockable;
	}
	
	/**
	 * Gets the action that is filtered.
	 * @return the action
	 */
	public DropDownAction getAction(){
		return action;
	}
	
	/**
	 * Gets a {@link DropDownView} in which this filter has to write its
	 * properties when {@link #update(DropDownViewItem)} is invoked. The view
	 * should be seen as a thing like a button or a menu item.
	 * @return the view to write into
	 */
	public DropDownView getView(){
		return view;
	}
	
	/**
	 * Invoked before this filter is used
	 */
	public void bind(){
		// do nothing
	}
	
	/**
	 * Invoked when this filter is no longer used
	 */
	public void unbind(){
		// do nothing
	}
	
	/**
	 * Updates all properties using the current selection of a button.
	 * @param selection the selection
	 */
	public abstract void update( DropDownViewItem selection );
}
