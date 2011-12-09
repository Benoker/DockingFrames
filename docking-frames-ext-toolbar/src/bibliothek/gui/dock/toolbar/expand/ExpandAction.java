package bibliothek.gui.dock.toolbar.expand;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionText;
import bibliothek.gui.dock.action.actions.GroupedButtonDockAction;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This action is always associated with a {@link ExpandableToolbarItemStrategy}
 * , it can make the element larger or smaller.
 * 
 * @author Benjamin Sigg
 */
public abstract class ExpandAction extends
		GroupedButtonDockAction<ExpandAction.Action>{
	/** describes the various states this action can have */
	public enum Action{
		/** fully expand an item */
		LARGEST,
		/** just make an item larger */
		LARGER,
		/** just make an item smaller */
		SMALLER,
		/** fully shrink an item */
		SMALLEST
	}

	/** the strategy telling which dockables are expanded and which are not */
	private final PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>(
			ExpandableToolbarItemStrategy.STRATEGY){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue,
				ExpandableToolbarItemStrategy newValue ){
			if (oldValue != null){
				oldValue.removeExpandedListener(listener);
			}
			if (newValue != null){
				newValue.addExpandedListener(listener);
			}
			for (final Dockable dockable : getBoundDockables()){
				update(dockable);
			}
		}
	};

	/**
	 * this listener is added to the current
	 * {@link ExpandableToolbarItemStrategy}
	 */
	private final ExpandableToolbarItemStrategyListener listener = new ExpandableToolbarItemStrategyListener(){

		@Override
		public void shrunk( Dockable item ){
			update(item);
		}

		@Override
		public void expanded( Dockable item ){
			update(item);
		}

		@Override
		public void stretched( Dockable item ){
			update(item);
		}

		@Override
		public void enablementChanged( Dockable item, ExpandedState state,
				boolean enabled ){
			update(item);
		}
	};

	private final DockController controller;
	private int bound = 0;

	private final List<DockActionIcon> icons = new ArrayList<DockActionIcon>();
	private final List<DockActionText> texts = new ArrayList<DockActionText>();

	/**
	 * Creates a new {@link ExpandAction}.
	 * 
	 * @param controller
	 *            the controller in whose realm this action will be used
	 * @param actions
	 *            the actions that are going to be used by this
	 *            {@link ExpandAction}, only icons and text for these actions
	 *            will be available.
	 */
	public ExpandAction( DockController controller, Action ... actions ){
		super(null);

		this.controller = controller;
		strategy.setProperties(controller);

		setRemoveEmptyGroups(false);

		for (final Action action : actions){
			setup(action);
		}
	}

	private void setup( final Action action ){
		final String name = name(action);

		icons.add(new DockActionIcon("toolbar.item." + name, this){
			@Override
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon(action, newValue);
			}
		});
		texts.add(new DockActionText("toolbar.item." + name, this){
			@Override
			protected void changed( String oldValue, String newValue ){
				setText(action, newValue);
			}
		});

		texts.add(new DockActionText("toolbar.item." + name + ".tooltip", this){
			@Override
			protected void changed( String oldValue, String newValue ){
				setTooltip(action, newValue);
			}
		});
	}

	private String name( Action action ){
		switch (action) {
		case LARGER:
			return "larger";
		case LARGEST:
			return "expand";
		case SMALLER:
			return "smaller";
		case SMALLEST:
			return "shrink";
		default:
			throw new IllegalStateException("unknown action: " + action);
		}
	}

	private void update( Dockable dockable ){
		if (isBound(dockable)){
			setGroup(createGroupKey(dockable), dockable);
		}
	}

	/**
	 * Gets the currently used strategy.
	 * 
	 * @return the strategy, not <code>null</code>
	 */
	protected ExpandableToolbarItemStrategy getStrategy(){
		return strategy.getValue();
	}

	@Override
	public void bound( Dockable dockable ){
		if (bound == 0){
			for (final DockActionIcon icon : icons){
				icon.setController(controller);
			}
			for (final DockActionText text : texts){
				text.setController(controller);
			}
		}
		super.bound(dockable);
		bound++;
	}

	@Override
	public void unbound( Dockable dockable ){
		super.unbound(dockable);
		bound--;
		if (bound == 0){
			for (final DockActionIcon icon : icons){
				icon.setController(null);
			}
			for (final DockActionText text : texts){
				text.setController(null);
			}
		}
	}
}
