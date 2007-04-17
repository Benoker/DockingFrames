package bibliothek.gui.dock.action;

import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.views.ActionViewConverter;

/**
 * The ActionType manly defines, how a {@link DockAction} is to be used. The
 * ActionType is needed by the {@link ActionViewConverter} to create a view
 * for a particular {@link DockAction}. Client code may create new ActionTypes,
 * but must ensure that the {@link ActionViewConverter} knows these new types.
 * @param <D> the specialized type of {@link DockAction} that uses this type
 * @author Benjamin Sigg
 */
public class ActionType<D extends DockAction> {
    /** 
     * The action behaves like a button: it can be triggered, some
     * action happens, and the original state is reestablished.
     */
    public static final ActionType<ButtonDockAction> BUTTON =
    	new ActionType<ButtonDockAction>( "action type BUTTON" );
    /**
     * The action behaves like a checkbox: when it is triggered,
     * it changes it's state from selected to unselected, or vice versa.
     */
    public static final ActionType<SelectableDockAction> CHECK =
    	new ActionType<SelectableDockAction>( "action type CHECK" ); 
    /**
     * The action behaves like a radiobutton: when it is triggered,
     * it changes to the selected-state, but some other actions
     * may change to the unselected-state 
     */
    public static final ActionType<SelectableDockAction> RADIO =
    	new ActionType<SelectableDockAction>( "action type RADIO" );
    
    /**
     * The action is a group of other actions which are shown as soon
     * as someone triggers the action.
     */
    public static final ActionType<MenuDockAction> MENU =
    	new ActionType<MenuDockAction>( "action type MENU" );
	
    /**
     * Represents a separator.
     */
    public static final ActionType<SeparatorAction> SEPARATOR =
    	new ActionType<SeparatorAction>( "action type SEPARATOR" );
    
    /**
     * Represents a drop down action.
     */
    public static final ActionType<DropDownAction> DROP_DOWN =
    	new ActionType<DropDownAction>( "action type DROP DOWN" );
    
    /**
     * Internal identifier for this type
     */
	private String id;
	
	/**
	 * Creates a new ActionType.
	 * @param id a unique identifier
	 */
	public ActionType( String id ){
		if( id == null )
			throw new IllegalArgumentException( "Id must not be null" );
		this.id = id;
	}
	
	@Override
	public String toString(){
		return id;
	}

	@Override
	public int hashCode(){
		return id.hashCode();
	}

	@Override
	public boolean equals( Object obj ){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if( !(obj instanceof ActionType))
			return false;
		
		return ((ActionType)obj).id.equals( id );
	}
}
