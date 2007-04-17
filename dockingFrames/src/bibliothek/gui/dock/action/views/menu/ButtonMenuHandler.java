package bibliothek.gui.dock.action.views.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ButtonDockAction;

/**
 * A handler that handles an ordinary {@link JMenuItem}.
 * @author Benjamin Sigg
 *
 */
public class ButtonMenuHandler extends AbstractMenuHandler<JMenuItem, ButtonDockAction> {
	
	/**
     * Creates a new handler.
     * @param action the action to observe
     * @param dockable the dockable for which actions are dispatched
     */
    public ButtonMenuHandler( ButtonDockAction action, Dockable dockable ){
    	super( action, dockable, new JMenuItem() );
    	
        item.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                ButtonMenuHandler.this.action.action( ButtonMenuHandler.this.dockable );
            }
        });
    }
    
    public void addActionListener( ActionListener listener ){
    	item.addActionListener( listener );
    }
    
    public void removeActionListener( ActionListener listener ){
    	item.removeActionListener( listener );
    }
}
