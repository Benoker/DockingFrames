package bibliothek.gui.dock.action.views.buttons;

import java.util.Set;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A connection between a {@link SelectableDockAction} and a {@link MiniButton}.
 * The handler ensures that the selected-state of the action and of the button
 * are always the same.
 * @author Benjamin Sigg
 *
 */
public abstract class SelectableMiniButtonHandler extends AbstractMiniButtonHandler<SelectableDockAction, MiniButton> {
	/**
	 * A class handling a {@link SelectableDockAction} with a behavior
	 * of {@link ActionType#RADIO}: the action can only be selected, not deselected
	 * by this handler.
	 * @author Benjamin Sigg
	 */
	public static class Radio extends SelectableMiniButtonHandler{
		/**
		 * Creates a new radio-handler
		 * @param action the action to handle
		 * @param dockable the owner of the action
		 * @param button the button to manage
		 */
		public Radio( SelectableDockAction action, Dockable dockable, MiniButton button ){
			super( action, dockable, button );
		}

		public void triggered(){
			getAction().setSelected( getDockable(), true );
		}
	};
	
	/**
	 * A class handling a {@link SelectableDockAction} with a behavior of
	 * {@link ActionType#CHECK}: the action can be selected and deselected
	 * by this handler.
	 * @author Benjamin Sigg
	 */
	public static class Check extends SelectableMiniButtonHandler{
		/**
		 * Creates a new check-handler.
		 * @param action the action to handle
		 * @param dockable the owner of the action
		 * @param button the button to manage
		 */
		public Check( SelectableDockAction action, Dockable dockable, MiniButton button ){
			super( action, dockable, button );
		}

		public void triggered(){
			boolean current = getAction().isSelected( getDockable() );
			getAction().setSelected( getDockable(), !current );
		}
	}
	
	/** A listener added to the action of this handler */
	private SelectableDockActionListener listener = new SelectableDockActionListener(){
		public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( getDockable() )){
				boolean value = action.isSelected( getDockable() );
				getButton().setSelected( value );
			}
		}
	};
	
	/**
	 * Creates a new handler.
	 * @param action the action to handle
	 * @param dockable the owner of the action
	 * @param button the button to manage
	 */
	public SelectableMiniButtonHandler( SelectableDockAction action, Dockable dockable, MiniButton button ){
		super( action, dockable, button );
	}
	
	@Override
	public void bind(){
		super.bind();
		getAction().addSelectableListener( listener );
		getButton().setSelected( getAction().isSelected( getDockable() ) );
	}

	@Override
	public void unbind(){
		super.unbind();
		getAction().removeSelectableListener( listener );
	}
}
