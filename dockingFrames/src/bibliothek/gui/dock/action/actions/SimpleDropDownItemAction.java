package bibliothek.gui.dock.action.actions;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.dropdown.DropDownItemAction;

/**
 * An action that can be shown as child of a {@link DropDownAction} and
 * which has advanced information for the {@link DropDownAction}.
 * @author Benjamin Sigg
 */
public abstract class SimpleDropDownItemAction extends SimpleDockAction implements DropDownItemAction{

    /** Whether this action can be selected in a {@link DropDownAction} or not. */
    private boolean dropDownSelectable = true;
    
    /** Whether this action can be triggered when shown on a {@link DropDownAction} or not if it is selected */
    private boolean dropDownTriggerableSelected = true;
    
    /** Whether this action can be triggered when shown on a {@link DropDownAction} or not if it is not selected */
    private boolean dropDownTriggerableNotSelected = true;

    /**
     * Sets whether this action can be selected if it is a child of a
     * {@link DropDownAction} or not.
     * @param dropDownSelectable <code>true</code> if this action can
     * be selected
     */
    public void setDropDownSelectable( boolean dropDownSelectable ){
		this.dropDownSelectable = dropDownSelectable;
	}
    
    public boolean isDropDownSelectable( Dockable dockable ){
    	return dropDownSelectable;
    }
    
    /**
     * Tells whether this action can be selected.
     * @return <code>true</code> if it can be selected
     * @see #setDropDownSelectable(boolean)
     */
    public boolean isDropDownSelectable(){
    	return dropDownSelectable;
    }
    
    /**
     * Sets whether this action can be triggered when shown on, and selected by, a
     * {@link DropDownAction} or not.
     * @param dropDownTriggerableSelected <code>true</code> if this action
     * can be triggered
     */
    public void setDropDownTriggerableSelected( boolean dropDownTriggerableSelected ){
		this.dropDownTriggerableSelected = dropDownTriggerableSelected;
	}
    
    /**
     * Tells whether this action can be triggered when shown on and selected by a
     * {@link DropDownAction} or not.
     * @return <code>true</code> if the action can be triggered
     */
    public boolean isDropDownTriggerableSelected(){
		return dropDownTriggerableSelected;
	}
    /**
     * Sets whether this action can be triggered when shown on, but not selected 
     * by, a {@link DropDownAction} or not.
     * @param dropDownTriggerableNotSelected <code>true</code> if this action
     * can be triggered
     */
    public void setDropDownTriggerableNotSelected( boolean dropDownTriggerableNotSelected ){
		this.dropDownTriggerableNotSelected = dropDownTriggerableNotSelected;
	}
    
    /**
     * Tells whether this action can be triggered when shown on, but not selected 
     * by, a {@link DropDownAction} or not.
     * @return <code>true</code> if the action can be triggered
     */
    public boolean isDropDownTriggerableNotSelected(){
		return dropDownTriggerableNotSelected;
	}
    
    public boolean isDropDownTriggerable( Dockable dockable, boolean selected ){
    	if( selected )
    		return dropDownTriggerableSelected;
    	else
    		return dropDownTriggerableNotSelected;
    }
}
