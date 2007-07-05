package bibliothek.notes.view.actions;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.actions.SimpleDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.notes.model.Note;
import bibliothek.notes.util.ResourceSet;

public class IconAction extends SimpleDockAction {
	public static final ActionType<IconAction> ICON = new ActionType<IconAction>( "icon" );
	private Note note;
	
	public IconAction( Note note ){
		this.note = note;
		
		setIcon( ResourceSet.APPLICATION_ICONS.get( "icon" ) );
	}
	
	public Note getNote(){
		return note;
	}
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ICON, this, target, dockable );
	}
}
