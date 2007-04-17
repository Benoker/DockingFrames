package bibliothek.gui.dock.action.actions;

import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.views.ActionViewConverter;
import bibliothek.gui.dock.action.views.ViewTarget;

/**
 * A separator represents a space between actions. A separator has no state,
 * he is purely graphical.
 * @author Benjamin Sigg
 */
public class SeparatorAction implements DockAction {
	/**
	 * A separator that is visible on menus and on titles
	 */
	public static final SeparatorAction SEPARATOR = 
		new SeparatorAction( ViewTarget.MENU, ViewTarget.TITLE, ViewTarget.DROP_DOWN );
	
	/**
	 * A separator that is only visible on menus
	 */
	public static final SeparatorAction MENU_SEPARATOR =
		new SeparatorAction( ViewTarget.MENU, ViewTarget.DROP_DOWN );
	
	/**
	 * A separator which is only visible on titles
	 */
	public static final SeparatorAction TITLE_SEPARATOR = 
		new SeparatorAction( ViewTarget.TITLE );
	
	/**
	 * The targets on which this separator should be shown
	 */
	private Set<ViewTarget<?>> targets = new HashSet<ViewTarget<?>>();
	
	/**
	 * Creates a new separator.
	 * @param targets the targets on which this separator should be visible
	 */
	public SeparatorAction( ViewTarget<?>... targets ){
		for( ViewTarget<?> target : targets )
			this.targets.add( target );
	}
	
	/**
	 * Tells whether the separator should be shown or not.
	 * @param target the target on which the separator might be made visible
	 * @return <code>true</code> if the separator should be shown, <code>false</code>
	 * otherwise.
	 */
	public boolean shouldDisplay( ViewTarget<?> target ){
		return targets.contains( target );
	}
	
	public void bind( Dockable dockable ){
		// do nothing
	}

	public void unbind( Dockable dockable ){
		// do nothing
	}
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.SEPARATOR, this, target, dockable );
	}
}
