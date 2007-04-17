package bibliothek.gui.dock.action.actions;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.StandardDropDownItemAction;

/**
 * An action that can be shown as child of a {@link DropDownAction}.
 * @author Benjamin Sigg
 *
 * @param <K> the type of the keys used for groups
 * @param <D> the type of model used as group
 */
public abstract class GroupedDropDownItemAction<K, D extends SimpleDropDownItemAction> 
		extends GroupedDockAction<K, D> 
		implements StandardDropDownItemAction{
    
    /**
     * Creates a new action.
     * @param generator the generator that will be used to get a key for 
     * Dockables which do not yet have a key. The generator can be <code>null</code>
     * and set later through the method {@link #setGenerator(GroupKeyGenerator)}
     */
    public GroupedDropDownItemAction( GroupKeyGenerator<? extends K> generator ){
    	super( generator );
    }
	
	public boolean isDropDownSelectable( Dockable dockable ){
		return getGroup( dockable ).isDropDownSelectable();
	}
	
	/**
	 * Tells whether the group <code>key</code> can be selected by a 
	 * {@link DropDownAction}.
	 * @param key the name of the group
	 * @return <code>true</code> if the group can be selected
	 */
	public boolean isDropDownSelectable( Object key ){
		return getGroup( key ).isDropDownSelectable();
	}
	
	/**
	 * Sets whether the group <code>key</code> can be selected by a 
	 * {@link DropDownAction}.
	 * @param key the name of the group
	 * @param selectable <code>true</code> if the group can be selected
	 */
	public void setDropDownSelectable( K key, boolean selectable ){
		ensureGroup( key ).setDropDownSelectable( selectable );
	}
	
	public boolean isDropDownTriggerable( Dockable dockable, boolean selected ){
		return getGroup( dockable ).isDropDownTriggerable( dockable, selected );
	}
	
	/**
	 * Tells whether the group <code>key</code> can be triggered by a 
	 * {@link DropDownAction}.
	 * @param key the name of the group
	 * @param selected <code>true</code> if the event will be on a 
	 * drop-down-button itself, <code>false</code> if the event is
	 * in the menu.
	 * @return <code>true</code> if the group can be triggered
	 */
	public boolean isDropDownTriggerable( Object key, boolean selected ){
		if( selected )
			return getGroup( key ).isDropDownTriggerableSelected();
		else
			return getGroup( key ).isDropDownTriggerableNotSelected();
	}

	/**
	 * Sets whether the group <code>key</code> can be triggered if the event
	 * occurs on a drop-down-button.
	 * @param key the name of the group
	 * @param triggerable <code>true</code> if the group can be triggered
	 */
	public void setDropDownTriggerableSelected( K key, boolean triggerable ){
		ensureGroup( key ).setDropDownTriggerableSelected( triggerable );
	}

	/**
	 * Sets whether the group <code>key</code> can be triggered if the event
	 * occurs in a menu.
	 * @param key the name of the group
	 * @param triggerable <code>true</code> if the group can be triggered
	 */
	public void setDropDownTriggerableNotSelected( K key, boolean triggerable ){
		ensureGroup( key ).setDropDownTriggerableNotSelected( triggerable );
	}
}
