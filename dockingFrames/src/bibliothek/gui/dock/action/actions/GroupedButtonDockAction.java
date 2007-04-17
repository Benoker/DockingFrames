package bibliothek.gui.dock.action.actions;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.views.ActionViewConverter;
import bibliothek.gui.dock.action.views.ViewTarget;

/**
 * A {@link GroupedDockAction} that provides the functionality of
 * a {@link ButtonDockAction}.
 * @author Benjamin Sigg
 * @param <K> the type of key used to distinguish groups
 */
public abstract class GroupedButtonDockAction<K> extends GroupedDropDownItemAction<K, SimpleButtonAction> implements ButtonDockAction{
	/**
	 * Creates a new action.
	 * @param generator the generator creating keys for {@link Dockable Dockables}
	 * which are not yet in a group 
	 */
	public GroupedButtonDockAction( GroupKeyGenerator<? extends K> generator ){
		super( generator );
	}

	@Override
	protected SimpleButtonAction createGroup(){
		return new SimpleButtonAction();
	}

	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.BUTTON, this, target, dockable );
	}
}
