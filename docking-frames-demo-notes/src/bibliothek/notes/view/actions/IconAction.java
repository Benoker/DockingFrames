package bibliothek.notes.view.actions;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.actions.SimpleDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.notes.model.Note;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.actions.icon.IconButtonHandler;
import bibliothek.notes.view.actions.icon.IconGrid;

/**
 * A new kind of action, used to select one icon out of a whole set of icons.
 * This action has its own graphical representation, normally a button 
 * and a popup-panel with the icons.<br>
 * The action of opening the popup-panel is implemented by the {@link IconButtonHandler}.<br>
 * The popup-panel containing the icons is implemented as the {@link IconGrid}.<br>
 * All the {@link DockTheme}s used by this application will register a 
 * {@link ViewGenerator} under the new {@link ActionType} {@link #ICON} at 
 * the {@link ActionViewConverter} of the 
 * {@link DockController#getActionViewConverter() controller}.
 * @author Benjamin Sigg
 */
public class IconAction extends SimpleDockAction {
    /** The type of action that an {@link IconAction} is */
	public static final ActionType<IconAction> ICON = new ActionType<IconAction>( "icon" );
	/** The <code>Note</code> whose icon might be changed by this action */
	private Note note;
	
	/**
	 * Creates a new action.
	 * @param note the <code>Note</code> whose {@link Icon} might be changed.
	 */
	public IconAction( Note note ){
		super( true );
		this.note = note;
		
		setIcon( ResourceSet.APPLICATION_ICONS.get( "icon" ) );
	}
	
	/**
	 * Gets the owner of this action.
	 * @return the owner
	 */
	public Note getNote(){
		return note;
	}
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ICON, this, target, dockable );
	} 
	
	public boolean trigger( Dockable dockable ) {
	    return false;
	}
}
