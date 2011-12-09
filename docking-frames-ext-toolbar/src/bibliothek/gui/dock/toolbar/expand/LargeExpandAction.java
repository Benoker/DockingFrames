package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.GroupKeyGenerator;

/**
 * An {@link ExpandAction} used for items that can switch between all
 * {@link ExpandedState}s. This action makes items as large as possible.
 * 
 * @author Benjamin Sigg
 */
public class LargeExpandAction extends ExpandAction{
	public LargeExpandAction( DockController controller ){
		super(controller, Action.LARGER, Action.LARGEST, Action.SMALLER);

		setGenerator(new GroupKeyGenerator<Action>(){
			@Override
			public Action generateKey( Dockable dockable ){
				switch (getStrategy().getState(dockable)) {
				case EXPANDED:
					return Action.SMALLER;
				case SHRUNK:
					return Action.LARGEST;
				case STRETCHED:
					return Action.LARGER;
				default:
					return null;
				}
			}
		});
	}

	@Override
	public void action( Dockable dockable ){
		switch (getStrategy().getState(dockable)) {
		case EXPANDED:
			getStrategy().setState(dockable, ExpandedState.STRETCHED);
			return;
		default:
			getStrategy().setState(dockable, ExpandedState.EXPANDED);
			return;
		}
	}
}
