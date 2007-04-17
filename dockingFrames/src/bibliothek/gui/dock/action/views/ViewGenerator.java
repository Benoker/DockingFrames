package bibliothek.gui.dock.action.views;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;


/**
 * A single entry for a {@link ActionViewConverter}. A generator
 * can convert one or more types of {@link DockAction} into one or more types
 * of {@link ViewTarget}.
 * @author Benjamin Sigg
 * 
 * @param <D> The type of DockAction converted by this generator
 * @param <A> The type of view created by this generator
 */
public interface ViewGenerator<D extends DockAction, A> {
	/**
	 * Converts <code>action</code> into a view. The result of this method
	 * can be <code>null</code> if no view should be shown for the given action.
	 * @param converter the converter that invoked this method
	 * @param action the action to convert
	 * @param dockable the Dockable for which the action will be used
	 * @return the view of the action or <code>null</code> if nothing should
	 * be displayed
	 */
	public A create( ActionViewConverter converter, D action, Dockable dockable );
}
