package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.title.DockTitle;

/**
 * An item in a menu used to display the visibility-state of a {@link Dockable}.
 * @author Benjamin Sigg
 *
 */
public class DockableItem extends UpdateableCheckBoxMenuItem implements DockableListener{
    /** the element whose visibility-state is shown */
	private Dockable dockable;
	/** the manager used to show and hide {@link #dockable} */
	private DockFrontend frontend;
	
	/**
	 * Creates a new item.
	 * @param frontend used to show and hide <code>dockable</code>
	 * @param dockable the element whose visibility-state is shown
	 */
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
	
	public void titleToolTipChanged( Dockable dockable, String oldToolTip, String newToolTip ) {
	    setToolTipText( newToolTip );
	}

	public void titleBound( Dockable dockable, DockTitle title ){
		// ignore
	}
	
	public void titleUnbound( Dockable dockable, DockTitle title ){
		// ignore
	}

	public void titleExchanged( Dockable dockable, DockTitle title ) {
	    // ignore
	}
}
