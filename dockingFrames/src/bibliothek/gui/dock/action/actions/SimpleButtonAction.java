package bibliothek.gui.dock.action.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.views.ActionViewConverter;
import bibliothek.gui.dock.action.views.ViewTarget;

/**
 * A {@link ButtonDockAction} that has the same properties for all 
 * {@link Dockable Dockables} which use the action. <br>
 * @author Benjamin Sigg
 *
 */
public class SimpleButtonAction extends SimpleDropDownItemAction implements ButtonDockAction{
	/** A set of listeners observing this action */
	private List<ActionListener> listeners = new ArrayList<ActionListener>();
	
	/** A command delivered in each ActionEvent created by this action */
	private String command;
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.BUTTON, this, target, dockable );
	}
	
	/**
	 * Sets the command of this action. The <code>command</code> will be 
	 * set in each {@link ActionEvent} that is created and fired by
	 * this action.
	 * @param command the command, might be <code>null</code>
	 */
	public void setCommand( String command ){
		this.command = command;
	}
	
	/**
	 * Gets the command of this action.
	 * @return the command, might be <code>null</code>
	 * @see #setCommand(String)
	 */
	public String getCommand(){
		return command;
	}
	
	/**
	 * Adds a listener to this action. The listener will be notified whenever
	 * this action is triggered.
	 * @param listener the new listener
	 */
	public void addActionListener( ActionListener listener ){
		listeners.add( listener );
	}
	
	/**
	 * Removes a listener from this action.
	 * @param listener the listener to remove
	 */
	public void removeActionListener( ActionListener listener ){
		listeners.remove( listener );
	}
	
	public void action( Dockable dockable ){
		ActionEvent event = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, command );
		
		for( ActionListener listener : listeners.toArray( new ActionListener[ listeners.size() ]))
			listener.actionPerformed( event );
	}
}
