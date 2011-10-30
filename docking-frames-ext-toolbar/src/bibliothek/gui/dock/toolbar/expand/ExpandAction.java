package bibliothek.gui.dock.toolbar.expand;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionText;
import bibliothek.gui.dock.action.actions.GroupKeyGenerator;
import bibliothek.gui.dock.action.actions.GroupedButtonDockAction;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This action is always associated with a {@link ExpandableToolbarItemStrategy}, it can make the element larger or smaller.
 * @author Benjamin Sigg
 */
public class ExpandAction extends GroupedButtonDockAction<Boolean>{
	/** the strategy telling which dockables are expanded and which are not */
	private PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			if( oldValue != null ){
				oldValue.removeExpandedListener( listener );
			}
			if( newValue != null ){
				newValue.addExpandedListener( listener );
			}
			for( Dockable dockable : getBoundDockables() ){
				update( dockable );
			}
		}
	};
	
	/** this listener is added to the current {@link ExpandableToolbarItemStrategy} */
	private ExpandableToolbarItemStrategyListener listener = new ExpandableToolbarItemStrategyListener(){
		
		@Override
		public void shrunk( Dockable item ){
			update( item );
		}
		
		@Override
		public void expanded( Dockable item ){
			update( item );
		}
		
		@Override
		public void expandableChanged( Dockable item, boolean expandable ){
			// ignore
		}
	};
	
	private DockActionIcon iconExpand;
	private DockActionIcon iconShrink;
	
	private DockActionText textExpand;
	private DockActionText textExpandTooltip;
	private DockActionText textShrink;
	private DockActionText textShrinkTooltip;
	
	/**
	 * Creates a new {@link ExpandAction}.
	 * @param controller the controller in whose realm this action will be used
	 */
	public ExpandAction( DockController controller ){
		super( null );
		
		setGenerator( new GroupKeyGenerator<Boolean>(){
			@Override
			public Boolean generateKey( Dockable dockable ){
				return getStrategy().isExpanded( dockable );
			}
		});
	
		strategy.setProperties( controller );
		
		setRemoveEmptyGroups( false );
		
		textExpand = new DockActionText( "toolbar.item.expand", this ){
			@Override
			protected void changed( String oldValue, String newValue ){
				setText( Boolean.TRUE, newValue );	
			}
		};
		textExpandTooltip = new DockActionText( "toolbar.item.expand.tooltip", this ){
			@Override
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.TRUE, newValue );	
			}
		};
		
		textShrink = new DockActionText( "toolbar.item.shrink", this ){
			@Override
			protected void changed( String oldValue, String newValue ){
				setText( Boolean.FALSE, newValue );	
			}
		};
		textShrinkTooltip = new DockActionText( "toolbar.item.shrink.tooltip", this ){
			@Override
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.FALSE, newValue );	
			}
		};
		
		iconExpand = new DockActionIcon( "toolbar.item.expand", this ){
			@Override
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( Boolean.TRUE, newValue );
			}
		};
		iconShrink = new DockActionIcon( "toolbar.item.shrink", this ){
			@Override
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( Boolean.FALSE, newValue );
			}
		};
		
		textExpand.setController( controller );
		textExpandTooltip.setController( controller );
		textShrink.setController( controller );
		textShrinkTooltip.setController( controller );
		iconExpand.setController( controller );
		iconShrink.setController( controller );
	}
	
	private void update( Dockable dockable ){
		setGroup( createGroupKey( dockable ), dockable );
	}
	
	/**
	 * Gets the currently used strategy.
	 * @return the strategy, not <code>null</code>
	 */
	private ExpandableToolbarItemStrategy getStrategy(){
		return strategy.getValue();
	}

	@Override
	public void action( Dockable dockable ){
		ExpandableToolbarItemStrategy strategy = getStrategy();
		strategy.setExpanded( dockable, !strategy.isExpanded( dockable ) );
	}
	
	@Override
	public void bound( Dockable dockable ){
		super.bound( dockable );
	}
	
	
	@Override
	public void unbound( Dockable dockable ){
		super.unbound( dockable );
	}
}
