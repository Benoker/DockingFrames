package bibliothek.gui.dock.action.views.dropdown;

import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JMenuItem;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.StandardDropDownItemAction;
import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.action.views.menu.AbstractMenuHandler;
import bibliothek.gui.dock.event.StandardDockActionListener;

/**
 * A handler that connects a {@link StandardDropDownItemAction} with a
 * drop-down-button.
 * @author Benjamin Sigg
 *
 * @param <S> the type of action handled by this handler
 */
public abstract class AbstractDropDownHandler<S extends StandardDropDownItemAction>
		extends AbstractMenuHandler<JMenuItem, S> 
		implements DropDownViewItem{

	/** a view where the handler may write some properties */
	private DropDownView view;
	/** a listener to the action */
	private Listener listener = new Listener();
	
	/**
	 * Creates an new handler.
	 * @param action the action to observe
	 * @param dockable the {@link Dockable} for which the action is shown.
	 * @param item the item that represents the action
	 */
	public AbstractDropDownHandler( S action, Dockable dockable, JMenuItem item ){
		super( action, dockable, item );
	}
	
	/**
	 * Gets the view that can be used to send properties directly to the drop-down-button.
	 * @return the view, can be <code>null</code>
	 */
	public DropDownView getView(){
		return view;
	}

	public void addActionListener( ActionListener listener ){
		item.addActionListener( listener );
	}

	public boolean isSelectable(){
		return action.isDropDownSelectable( dockable );
	}

	public boolean isTriggerable( boolean selected ){
		return action.isDropDownTriggerable( dockable, selected );
	}

	public void removeActionListener( ActionListener listener ){
		item.removeActionListener( listener );
	}

	public void setView( DropDownView view ){
		this.view = view;
		if( view != null ){
			view.setText( action.getText( dockable ) );
			view.setTooltip( action.getTooltipText( dockable ) );
			view.setIcon( action.getIcon( dockable ) );
			view.setDisabledIcon( action.getDisabledIcon( dockable ) );
			view.setEnabled( action.isEnabled( dockable ) );
		}
	}
	
	@Override
	public void bind(){
		super.bind();
		action.addDockActionListener( listener );
		
		if( view != null ){
			view.setText( action.getText( dockable ) );
			view.setTooltip( action.getTooltipText( dockable ) );
			view.setIcon( action.getIcon( dockable ) );
			view.setEnabled( action.isEnabled( dockable ) );
		}
	}
	
	@Override
	public void unbind(){
		action.removeDockActionListener( listener );
		super.unbind();
	}
	
	/**
	 * A listener to the action of this handler. Forwards changes to the
	 * {@link AbstractDropDownHandler#getView() view}.
	 * @author Benjamin Sigg
	 */
	private class Listener implements StandardDockActionListener{
		public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable ))
				view.setEnabled( action.isEnabled( dockable ) );
		}

		public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable ))
				view.setIcon( action.getIcon( dockable ) );
		}
		
		public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable ))
				view.setDisabledIcon( action.getDisabledIcon( dockable ) );
		}

		public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable ))
				view.setText( action.getText( dockable ) );
		}

		public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ){
			if( view != null && dockables.contains( dockable ))
				view.setTooltip( action.getTooltipText( dockable ) );
		}
		
	}
}
