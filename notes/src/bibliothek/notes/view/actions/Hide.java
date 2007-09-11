package bibliothek.notes.view.actions;

import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.notes.view.NoteViewManager;
import bibliothek.notes.view.panels.NoteView;

@EclipseTabDockAction
public class Hide extends SimpleButtonAction implements ActionGuard{
	private DefaultDockActionSource source;
	private NoteViewManager manager; 
	
	public Hide( DockFrontend frontend, NoteViewManager manager ){
		this.manager = manager;
		setText( "Hide" );
		setIcon( frontend.getController().getIcons().getIcon( "close" ) );
		frontend.getController().getIcons().add( "close", new IconManagerListener(){
			public void iconChanged( String key, Icon icon ){
				setIcon( icon );
			}
		});
		
		source = new DefaultDockActionSource(
				new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ));
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
