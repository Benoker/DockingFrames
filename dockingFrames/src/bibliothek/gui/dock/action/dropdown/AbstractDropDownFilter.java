package bibliothek.gui.dock.action.dropdown;

import javax.swing.Icon;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DropDownAction;

/**
 * A {@link DropDownFilter} which stores all properties. The properties can
 * be read by subclasses.
 * @author Benjamin Sigg
 *
 */
public abstract class AbstractDropDownFilter extends DropDownFilter {
	/** the icon provided by the selected element */
	protected Icon icon;
	
	/** the disabled icon provided by the selected element */
	protected Icon disabledIcon;
	
	/** whether the selected element is enabled */
	protected boolean enabled;
	
	/** whether the selected element is selected */
	protected boolean selected;
	
	/** the text of the selected element */
	protected String text;
	
	/** the tooltip of the selected element */
	protected String tooltip;

	/**
	 * Creates a new filter.
	 * @param action the action to filter
	 * @param dockable the owner of <code>action</code>.
	 * @param view the view in which this action will write its properties
	 */
	public AbstractDropDownFilter( DropDownAction action, Dockable dockable, DropDownView view ){
		super( dockable, action, view );
	}
	
	public void setDisabledIcon( Icon icon ){
		this.disabledIcon = icon;
	}
	
	public void setEnabled( boolean enabled ){
		this.enabled = enabled;
	}
	
	public void setIcon( Icon icon ){
		this.icon = icon;
	}

	public void setSelected( boolean selected ){
		this.selected = selected;
	}
	
	public void setText( String text ){
		this.text = text;
	}

	public void setTooltip( String tooltip ){
		this.tooltip = tooltip;
	}
}
