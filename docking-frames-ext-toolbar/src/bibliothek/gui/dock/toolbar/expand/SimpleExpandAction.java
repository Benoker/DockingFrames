package bibliothek.gui.dock.toolbar.expand;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionText;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;

/**
 * A {@link SimpleExpandAction} just shows the icon, text and tooltip of one 
 * {@link Action} that could be executed regarding the {@link ExpandedState}. This action
 * does not implement any logic, it just provides a good looking button.
 * @author Benjamin Sigg
 */
public class SimpleExpandAction extends SimpleButtonAction {
	/** describes to which state this action leads */
	public enum Action {
		/** fully expand an item */
		LARGEST,
		/** just make an item larger */
		LARGER,
		/** just make an item smaller */
		SMALLER,
		/** fully shrink an item */
		SMALLEST
	}

	private final DockController controller;
	private int bound = 0;

	private DockActionIcon icon;
	private DockActionText text;
	private DockActionText tooltip;

	/**
	 * Creates a new action.
	 * @param controller the controller in whose realm this action is used
	 * @param action the exact look of this action
	 */
	public SimpleExpandAction( DockController controller, Action action ){
		this.controller = controller;
		final String name = name( action );

		icon = new DockActionIcon( "toolbar.item." + name, this ){
			@Override
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( newValue );
			}
		};
		text = new DockActionText( "toolbar.item." + name, this ){
			@Override
			protected void changed( String oldValue, String newValue ){
				setText( newValue );
			}
		};

		tooltip = new DockActionText( "toolbar.item." + name + ".tooltip", this ){
			@Override
			protected void changed( String oldValue, String newValue ){
				setTooltip( newValue );
			}
		};
	}

	private String name( Action action ){
		switch( action ){
			case LARGER:
				return "larger";
			case LARGEST:
				return "expand";
			case SMALLER:
				return "smaller";
			case SMALLEST:
				return "shrink";
			default:
				throw new IllegalStateException( "unknown action: " + action );
		}
	}

	@Override
	public void bound( Dockable dockable ){
		if( bound == 0 ) {
			icon.setController( controller );
			text.setController( controller );
			tooltip.setController( controller );
		}
		super.bound( dockable );
		bound++;
	}

	@Override
	public void unbound( Dockable dockable ){
		super.unbound( dockable );
		bound--;
		if( bound == 0 ) {
			icon.setController( null );
			text.setController( null );
			tooltip.setController( null );
		}
	}
}
