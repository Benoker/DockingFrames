package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.GroupKeyGenerator;

/**
 * This {@link AbstractGroupedExpandAction} is used for items that can switch only between two
 * of the {@link ExpandedState}s.
 * 
 * @author Benjamin Sigg
 */
public class SwitchExpandAction extends AbstractGroupedExpandAction{
	public SwitchExpandAction( DockController controller ){
		super(controller, Action.SMALLER, Action.SMALLEST, Action.LARGER,
				Action.LARGEST);

		setGenerator(new GroupKeyGenerator<Action>(){
			@Override
			public Action generateKey( Dockable dockable ){
				final boolean shrunk = getStrategy().isEnabled(dockable,
						ExpandedState.SHRUNK);
				final boolean expanded = getStrategy().isEnabled(dockable,
						ExpandedState.EXPANDED);

				switch (getStrategy().getState(dockable)) {
				case EXPANDED:
					if (shrunk){
						return Action.SMALLEST;
					} else{
						return Action.SMALLER;
					}
				case SHRUNK:
					if (expanded){
						return Action.LARGEST;
					} else{
						return Action.LARGER;
					}
				case STRETCHED:
					if (shrunk){
						return Action.SMALLER;
					} else{
						return Action.LARGER;
					}
				default:
					return null;
				}
			}
		});
	}

	@Override
	public void action( Dockable dockable ){
		final boolean shrunk = getStrategy().isEnabled(dockable,
				ExpandedState.SHRUNK);
		final boolean expanded = getStrategy().isEnabled(dockable,
				ExpandedState.EXPANDED);

		switch (getStrategy().getState(dockable)) {
		case EXPANDED:
			if (shrunk){
				getStrategy().setState(dockable, ExpandedState.SHRUNK);
			} else{
				getStrategy().setState(dockable, ExpandedState.STRETCHED);
			}
			break;
		case SHRUNK:
			if (expanded){
				getStrategy().setState(dockable, ExpandedState.EXPANDED);
			} else{
				getStrategy().setState(dockable, ExpandedState.STRETCHED);
			}
			break;
		case STRETCHED:
			if (shrunk){
				getStrategy().setState(dockable, ExpandedState.SHRUNK);
			} else if (expanded){
				getStrategy().setState(dockable, ExpandedState.EXPANDED);
			}
			break;
		}
	}
}
