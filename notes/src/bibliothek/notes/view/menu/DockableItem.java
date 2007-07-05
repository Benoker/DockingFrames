package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.title.DockTitle;

public class DockableItem extends UpdateableCheckBoxMenuItem implements DockableListener{
	private Dockable dockable;
	private DockFrontend frontend;
	
	public DockableItem( DockFrontend frontend, Dockable dockable ){
		this.frontend = frontend;
		this.dockable = dockable;
		
		dockable.addDockableListener( this );
		
		setText( dockable.getTitleText() );
		setIcon( dockable.getTitleIcon() );
	}

	public void actionPerformed( ActionEvent e ){
		boolean state = isSelected();
		if( state )
			frontend.show( dockable );
		else
			frontend.hide( dockable );
	}
	
	public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ){
		setIcon( newIcon );
	}

	public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ){
		setText( newTitle );
	}

	public void titleBinded( Dockable dockable, DockTitle title ){
		// ignore
	}
	
	public void titleUnbinded( Dockable dockable, DockTitle title ){
		// ignore
	}

}
