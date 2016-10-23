package bibliothek.notes.view.actions;

import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.view.NoteViewManager;
import bibliothek.notes.view.panels.NoteView;

/**
 * An action that removes {@link NoteView}s from the dock-tree.
 * @author Benjamin Sigg
 *
 */
@EclipseTabDockAction
public class Hide extends SimpleButtonAction implements ActionGuard{
    /** the result of {@link #getSource(Dockable)} */
	private DefaultDockActionSource source;
	/** the manager of the graphical representations of the Notes */
	private NoteViewManager manager;
	
	/** the icon of this action */
	private DockActionIcon icon;
	
	/**
	 * Creates a new action and {@link ActionGuard}
	 * @param frontend the frontend used to retrieve icons
	 * @param manager the manager of the graphical representations of the Notes,
	 * used to hide a {@link NoteView}
	 */
	public Hide( DockFrontend frontend, NoteViewManager manager ){
		this.manager = manager;
		setText( "Hide" );
		
		icon = new DockActionIcon( "close", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( newValue );
			}
		};
		icon.setManager( frontend.getController().getIcons() );
		
		source = new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ));
		source.add( this );
	}
	
	public DockActionSource getSource( Dockable dockable ){
		return source;
	}

	public boolean react( Dockable dockable ){
		return dockable instanceof NoteView;
	}
	
	@Override
	public void action( Dockable dockable ){
		super.action( dockable );
		
		manager.hide( ((NoteView)dockable).getNote() );
	}
}
