package bibliothek.gui.dock.action.views.menu;

import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComponent;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.actions.SimpleMenuAction;
import bibliothek.gui.dock.event.StandardDockActionListener;

/**
 * A handler that shows a {@link DropDownAction} in a menu. The action is 
 * shown as if it were an ordinary menu.
 * @author Benjamin Sigg
 *
 */
public class DropDownMenuHandler implements MenuViewItem<JComponent>{
	/** an action that represents the {@link DropDownAction} as a menu */
	private SimpleMenuAction menuAction;
	/** a handler that sets up the view for {@link #menuAction} */
	private MenuMenuHandler handler;
	
	/** the action observed by this handler */
	private DropDownAction action;
	/** a listener to the action */
	private Listener listener = new Listener();
	/** the {@link Dockable} for which this view was created */
	private Dockable dockable;
	
	/**
	 * Creates a new handler
	 * @param action the action to show
	 * @param dockable the Dockable for which the action is shown
	 */
	public DropDownMenuHandler( DropDownAction action, Dockable dockable ){
		this.action = action;
		this.dockable = dockable;
	}

	public void addActionListener( ActionListener listener ){
		handler.addActionListener( listener );
	}

	public void removeActionListener( ActionListener listener ){
		handler.removeActionListener( listener );
	}

	public void bind(){
		action.bind( dockable );
		DockActionSource source = action.getSubActions( dockable );
		menuAction = new SimpleMenuAction( source );
		handler = new MenuMenuHandler( menuAction, dockable );
		
		menuAction.setText( action.getText( dockable ) );
		menuAction.setTooltipText( action.getTooltipText( dockable ) );
		menuAction.setEnabled( action.isEnabled( dockable ) );
		menuAction.setIcon( action.getIcon( dockable ) );
		
		handler.bind();
		action.addDockActionListener( listener );
	}

	public void unbind(){
		handler.unbind();
		action.removeDockActionListener( listener );
		action.unbind( dockable );
		
		menuAction = null;
		handler = null;
	}	
	
	public DockAction getAction(){
		return action;
	}

	public JComponent getItem(){
		return handler.getItem();
	}
	
	/**
	 * A listener to the action. Changes of icon, text, etc.. are forwarded
	 * to {@link DropDownMenuHandler#menuAction}.
	 * @author Benjamin Sigg
	 */
	private class Listener implements StandardDockActionListener{
		public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ))
				menuAction.setEnabled( action.isEnabled( dockable ) );
		}

		public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ))
				menuAction.setIcon( action.getIcon( dockable ) );
		}
		
		public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ))
				menuAction.setDisabledIcon( action.getDisabledIcon( dockable ) );
		}

		public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ))
				menuAction.setText( action.getText( dockable ) );
		}

		public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( dockable ))
				menuAction.setTooltipText( action.getTooltipText( dockable ) );
		}
	}
}
