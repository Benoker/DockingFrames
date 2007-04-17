package bibliothek.gui.dock.action.actions;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.views.ActionViewConverter;
import bibliothek.gui.dock.action.views.ViewTarget;

/**
 * An action that provides a menu which contains other actions.
 * @author Benjamin Sigg
 */
public class SimpleMenuAction extends SimpleDockAction implements MenuDockAction{
	/** the menu */
	private DockActionSource menu;
	
	/**
	 * Creates a new action. The menu has to be set later.
	 * @see #setMenu(DockActionSource)
	 */
	public SimpleMenuAction(){
		// do nothing
	}
	
	/**
	 * Creates a new action.
	 * @param menu the menu that is shown for this action
	 */
	public SimpleMenuAction( DockActionSource menu ){
		setMenu( menu );
	}

	/**
     * Sets a menu that will be displayed instead of this action. Note that
     * this call might not have an immediate effect.
     * @param menu the menu, may be <code>null</code>
     */
    public void setMenu( DockActionSource menu ) {
    	this.menu = menu;
    }
    
    public DockActionSource getMenu( Dockable dockable ){
    	return menu;
    }
    
    /**
     * Gets the menu of this action
     * @return the menu
     */
    public DockActionSource getMenu() {
        return menu;
    }
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.MENU, this, target, dockable );
	}
}
