package bibliothek.gui.dock.action.dropdown;

import javax.swing.Icon;

/**
 * A connection between an drop-down-item and a view. Clients should use
 * instances of this interface as if they access a button.
 * @author Benjamin Sigg
 */
public interface DropDownView {
	/**
	 * Sets the text of the button.
	 * @param text the text
	 */
	public void setText( String text );
	
	/**
	 * Sets the tooltip of the button.
	 * @param tooltip the tooltip
	 */
	public void setTooltip( String tooltip );
		
	/**
	 * Sets the icon of the button.
	 * @param icon the icon
	 */
	public void setIcon( Icon icon );
	
	/**
	 * Sets the disabled icon of the button.
	 * @param icon the disabled icon
	 */
	public void setDisabledIcon( Icon icon );	
	
	/**
	 * Sets the enabled-state of the button.
	 * @param enabled the state
	 */
	public void setEnabled( boolean enabled );
		
	/**
	 * Sets the selected-state of the button.
	 * @param selected the state
	 */
	public void setSelected( boolean selected );
}
