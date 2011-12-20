package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.GroupKeyGenerator;

/**
 * An {@link AbstractGroupedExpandAction} used for items that can switch between all
 * {@link ExpandedState}s. This action makes items as small as possible
 * 
 * @author Benjamin Sigg
 */
public class SmallExpandAction extends AbstractGroupedExpandAction{
	public SmallExpandAction( DockController controller ){
		super(controller, Action.LARGER, Action.SMALLEST, Action.SMALLER);

		setGenerator(new GroupKeyGenerator<Action>(){
			@Override
			public Action generateKey( Dockable dockable ){
				switch (getStrategy().getState(dockable)) {
				case EXPANDED:
					return Action.SMALLEST;
				case SHRUNK:
					return Action.LARGER;
				case STRETCHED:
					return Action.SMALLER;
				default:
					return null;
				}
			}
		});
	}

	@Override
	public void action( Dockable dockable ){
		switch (getStrategy().getState(dockable)) {
		case SHRUNK:
			getStrategy().setState(dockable, ExpandedState.STRETCHED);
			return;
		default:
			getStrategy().setState(dockable, ExpandedState.SHRUNK);
			return;
		}
	}
}