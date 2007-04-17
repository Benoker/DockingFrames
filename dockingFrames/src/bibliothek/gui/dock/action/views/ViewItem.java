package bibliothek.gui.dock.action.views;

import bibliothek.gui.dock.action.DockAction;

/**
 * A wrapper for an item that will be shown somewhere.
 * @author Benjamin Sigg
 *
 * @param <A> the type of item that is wrapped
 */
public interface ViewItem<A> {
	/**
	 * Binds this item to its action
	 */
	public void bind();
	
	/**
	 * Unbinds this item from its action
	 */
	public void unbind();
	
	/**
	 * Gets this item as component.
	 * @return this item
	 */
	public A getItem();
	
	/**
	 * Gets the action that is represented by this target.
	 * @return the action, might be <code>null</code>
	 */
	public DockAction getAction();
}
