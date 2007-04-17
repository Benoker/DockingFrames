package bibliothek.gui.dock.action.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * This class ensures that from a group of {@link SelectableDockAction} only one
 * is selected.
 * @author Benjamin Sigg
 */
public class SelectableDockActionGroup {
	/** The actions to observe */
	private List<SelectableDockAction> actions = new ArrayList<SelectableDockAction>();
	
	/** A listener to all actions */
	private SelectableDockActionListener listener = new SelectableDockActionListener(){
		public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
			for( Dockable dockable : dockables ){
				if( action.isSelected( dockable )){
					for( SelectableDockAction change : actions ){
						if( change != action ){
							change.setSelected( dockable, false );
						}
					}
				}
			}
		}
	};
	
	/**
	 * Adds an action that has to be observed
	 * @param action the new action
	 */
	public void addAction( SelectableDockAction action ){
		actions.add( action );
		action.addSelectableListener( listener );
	}
	
	/**
	 * Removes an action. That action will no longer be observed.
	 * @param action the action to remove
	 */
	public void removeAction( SelectableDockAction action ){
		action.removeSelectableListener( listener );
		actions.remove( action );
	}
}
