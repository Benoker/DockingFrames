package bibliothek.gui.dock;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.ToolbarElementInterface;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.dockable.DockableIcon;
import bibliothek.gui.dock.station.toolbar.ToolbarPartDockFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
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
public class ComponentDockable extends AbstractDockable implements ToolbarElementInterface {

	/** the component */
	private Component component;

	/**
	 * Constructs a new ComponentDockable
	 */
	public ComponentDockable(){
		this( null, null, null );
	}

	/**
	 * Constructs a new ComponentDockable and sets the icon.
	 * 
	 * @param icon
	 *            the icon, to be shown at various places
	 */
	public ComponentDockable( Icon icon ){
		this( null, null, icon );
	}

	/**
	 * Constructs a new ComponentDockable and sets the title.
	 * 
	 * @param title
	 *            the title, to be shown at various places
	 */
	public ComponentDockable( String title ){
		this( null, title, null );
	}

	/**
	 * Constructs a new ComponentDockable and places one component onto the
	 * content pane.
	 * 
	 * @param component
	 *            the only child of the content pane
	 */
	public ComponentDockable( Component component ){
		this( component, null, null );
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
		this( component, null, icon );
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
		this( component, title, null );
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
		super( PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP );
		if( component != null ) {
			this.component = component;
		}

		if( icon != null ) {
			setTitleIcon( icon );
		}
		setTitleText( title );
	}
	
	@Override
	public Component getComponent(){
		return component;
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
		// TODO to verify... simple recopy of the same method in DefaultDockable
		return new DockableIcon( "dockable.default", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				fireTitleIconChanged( oldValue, newValue );
			}
		};
	}

	@Override
	public boolean accept( DockStation station ){
		System.out.println( this.toString() + "## accept(DockStation station) ##" );
		
		// as this method is called during drag&drop operations a DockController is available
		
		SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>( ToolbarStrategy.STRATEGY, getController() );
		ToolbarStrategy strategy = value.getValue();
		value.setProperties( (DockController)null );
		
		return strategy.isToolbarGroupPartParent( station, this );
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@' + Integer.toHexString( this.hashCode() );
	}

}
