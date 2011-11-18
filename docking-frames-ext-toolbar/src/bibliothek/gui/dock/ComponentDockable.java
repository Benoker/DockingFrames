package bibliothek.gui.dock;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.dockable.DockableIcon;
import bibliothek.gui.dock.station.toolbar.ToolbarPartDockFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandableStateController;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItem;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.SilentPropertyValue;
import bibliothek.gui.dock.util.icon.DockIcon;

/**
 * A {@link Dockable} which consist only of one {@link JComponent}. This
 * dockable can be put in {@link DockStation} which implements marker interface
 * {@link ToolbarInterface}
 * 
 * @author Herve Guillaume
 */
public class ComponentDockable extends AbstractDockable implements
		ToolbarElementInterface, ExpandableToolbarItem{

	/** the component */
	private JPanel content;

	/** the layout of {@link #content} */
	private CardLayout contentLayout;

	/** all the {@link ExpandableToolbarItemListener}s */
	private List<ExpandableToolbarItemListener> expandableListeners = new ArrayList<ExpandableToolbarItemListener>();

	/** the current state of this {@link ExpandableToolbarItem} */
	private ExpandedState state = ExpandedState.SHRUNK;

	/** the {@link Component}s to show in different states */
	private Component[] components = new Component[ExpandedState.values().length];

	/**
	 * Constructs a new ComponentDockable
	 */
	public ComponentDockable(){
		this(null, null, null);
	}

	/**
	 * Constructs a new ComponentDockable and sets the icon.
	 * 
	 * @param icon
	 *            the icon, to be shown at various places
	 */
	public ComponentDockable( Icon icon ){
		this(null, null, icon);
	}

	/**
	 * Constructs a new ComponentDockable and sets the title.
	 * 
	 * @param title
	 *            the title, to be shown at various places
	 */
	public ComponentDockable( String title ){
		this(null, title, null);
	}

	/**
	 * Constructs a new ComponentDockable and places one component onto the
	 * content pane.
	 * 
	 * @param component
	 *            the only child of the content pane
	 */
	public ComponentDockable( Component component ){
		this(component, null, null);
	}

	/**
	 * Constructs a new ComponentDockable, sets an icon and places one
	 * component.
	 * 
	 * @param component
	 *            the only child of the content pane
	 * @param icon
	 *            the icon, to be shown at various places
	 */
	public ComponentDockable( Component component, Icon icon ){
		this(component, null, icon);
	}

	/**
	 * Constructs a new ComponentDockable, sets the title and places one
	 * component.
	 * 
	 * @param component
	 *            the only child of the content pane
	 * @param title
	 *            the title, to be shown at various places
	 */
	public ComponentDockable( Component component, String title ){
		this(component, title, null);
	}

	/**
	 * Constructs a new ComponentDockable, sets the icon and the title, and
	 * places a component.
	 * 
	 * @param component
	 *            the only child of the content pane
	 * @param title
	 *            the title, to be shown at various places
	 * @param icon
	 *            the icon, to be shown at various places
	 */
	public ComponentDockable( Component component, String title, Icon icon ){
		super(PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP);

		contentLayout = new CardLayout(){
			@Override
			public Dimension preferredLayoutSize( Container parent ){
				synchronized (parent.getTreeLock()){
					Component current = getNearestComponent(state);
					if (current == null){
						return new Dimension(10, 10);
					}
					return current.getPreferredSize();
				}
			}

			@Override
			public Dimension minimumLayoutSize( Container parent ){
				synchronized (parent.getTreeLock()){
					Component current = getNearestComponent(state);
					if (current == null){
						return new Dimension(10, 10);
					}
					return current.getMinimumSize();
				}
			}

			@Override
			public Dimension maximumLayoutSize( Container parent ){
				synchronized (parent.getTreeLock()){
					Component current = getNearestComponent(state);
					if (current == null){
						return new Dimension(10, 10);
					}
					return current.getMaximumSize();
				}
			}
		};

		content = new JPanel(contentLayout);

		new ExpandableStateController(this);

		if (component != null){
			setComponent(component, ExpandedState.SHRUNK);
		}

		setTitleIcon(icon);
		setTitleText(title);
	}

	/**
	 * Gets the component associated with the nearest {@link ExpandedState} with
	 * regards to the <code>state</code> parameter. If two states are equally
	 * close, the state with minor ordinal value is returned.
	 * 
	 * @param state
	 *            the state
	 * @return the component in the nearest state.
	 */
	private Component getNearestComponent( ExpandedState state ){
		int index = state.ordinal();
		while (index >= 0){
			if (components[index] != null){
				return components[index];
			}
			index--;
		}

		index = state.ordinal() + 1;
		while (index < components.length){
			if (components[index] != null){
				return components[index];
			}
			index++;
		}
		return null;
	}

	/**
	 * Gets the nearest value of {@link ExpandedState} with regards to the
	 * <code>state</code> parameter.
	 * 
	 * @param state
	 *            the state.
	 * @return the nearest state.
	 */
	private ExpandedState getNearestState( ExpandedState state ){
		Component nearest = getNearestComponent(state);
		if (nearest == null){
			return null;
		}
		for (ExpandedState next : ExpandedState.values()){
			if (components[next.ordinal()] == nearest){
				return next;
			}
		}
		return null;
	}

	/**
	 * Sets the {@link Component} which should be shown if in state
	 * <code>state</code>. Please note that the same {@link Component} cannot be
	 * used for more than one state.
	 * 
	 * @param component
	 *            the component to add
	 * @param state
	 *            the state in which to show <code>component</code>
	 */
	public void setComponent( Component component, ExpandedState state ){
		Component previous = components[state.ordinal()];
		if (previous != component){
			if (previous != null){
				content.remove(previous);
			}
			components[state.ordinal()] = component;
			if (component != null){
				content.add(component, state.toString());
			}

			ExpandedState nearest = getNearestState(this.state);
			if (nearest != null){
				contentLayout.show(content, nearest.toString());
				content.revalidate();
			}
		}
	}
	
	@Override
	public void setExpandedState( ExpandedState state ){
		if (this.state != state){
			ExpandedState oldState = this.state;
			this.state = state;
			ExpandedState nearest = getNearestState(state);
			if (nearest != null){
				contentLayout.show(content, nearest.toString());
			}
			content.revalidate();
			for (ExpandableToolbarItemListener listener : expandableListeners
					.toArray(new ExpandableToolbarItemListener[expandableListeners
							.size()])){
				listener.changed(this, oldState, state);
			}
		}
	}

	@Override
	public ExpandedState getExpandedState(){
		return state;
	}

	@Override
	public Component getComponent(){
		return content;
	}

	@Override
	public void addExpandableListener( ExpandableToolbarItemListener listener ){
		if (listener == null){
			throw new IllegalArgumentException("listener must not be null");
		}
		expandableListeners.add(listener);
	}

	@Override
	public void removeExpandableListener( ExpandableToolbarItemListener listener ){
		expandableListeners.remove(listener);
	}

	@Override
	public DockStation asDockStation(){
		return null;
	}

	@Override
	public String getFactoryID(){
		return ToolbarPartDockFactory.ID;
	}

	@Override
	protected DockIcon createTitleIcon(){
		return new DockableIcon("dockable.default", this){
			protected void changed( Icon oldValue, Icon newValue ){
				fireTitleIconChanged(oldValue, newValue);
			}
		};
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println(this.toString()
				+ "## accept(DockStation station) ##");

		// as this method is called during drag&drop operations a DockController
		// is available

		SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(
				ToolbarStrategy.STRATEGY, getController());
		ToolbarStrategy strategy = value.getValue();
		value.setProperties((DockController) null);

		return strategy.isToolbarGroupPartParent(station, this);
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

}
