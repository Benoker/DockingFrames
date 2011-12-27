package bibliothek.gui.dock.station;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.action.BasicResourceInitializer;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;
import bibliothek.gui.dock.themes.border.BorderModifier;

/**
 * A {@link BasicMiniButton} with custom borders, to be shown on the titlbar of
 * a toolbar.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarMiniButton extends BasicMiniButton {
	/**
	 * Identifier for the {@link ThemeManager} of the {@link BorderModifier}
	 * which is used for the normal state.
	 */
	public static final String BORDER_KEY_NORMAL = ThemeManager.BORDER_MODIFIER + ".action.toolbar.miniButton.normal";
	/**
	 * Identifier for the {@link ThemeManager} of the {@link BorderModifier}
	 * which is used for the selected state.
	 */
	public static final String BORDER_KEY_NORMAL_SELECTED = ThemeManager.BORDER_MODIFIER + ".action.toolbar.miniButton.normal.selected";
	/**
	 * Identifier for the {@link ThemeManager} of the {@link BorderModifier}
	 * which is used for the mouse hover state.
	 */
	public static final String BORDER_KEY_MOUSE_OVER = ThemeManager.BORDER_MODIFIER + ".action.toolbar.miniButton.mouseOver";
	/**
	 * Identifier for the {@link ThemeManager} of the {@link BorderModifier}
	 * which is used for the selected mouse hover state.
	 */
	public static final String BORDER_KEY_MOUSE_OVER_SELECTED = ThemeManager.BORDER_MODIFIER + ".action.toolbar.miniButton.mouseOver.selected";
	/**
	 * Identifier for the {@link ThemeManager} of the {@link BorderModifier}
	 * which is used for the mouse pressed state.
	 */
	public static final String BORDER_KEY_MOUSE_PRESSED = ThemeManager.BORDER_MODIFIER + ".action.toolbar.miniButton.mousePressed";
	/**
	 * Identifier for the {@link ThemeManager} of the {@link BorderModifier}
	 * which is used for the selected mouse pressed state.
	 */
	public static final String BORDER_KEY_MOUSE_PRESSED_SELECTED = ThemeManager.BORDER_MODIFIER + ".action.toolbar.miniButton.mousePressed.selected";

	/**
	 * Creates the new button.
	 * 
	 * @param trigger
	 *            the callback that is invoked when the user clicks onto this
	 *            button
	 * @param initializer
	 *            a strategy to lazily initialize resources
	 */
	public ToolbarMiniButton( BasicTrigger trigger, BasicResourceInitializer initializer ){
		super( trigger, initializer );
		setBorderKeyNormal( BORDER_KEY_NORMAL );
		setBorderKeyNormalSelected( BORDER_KEY_NORMAL_SELECTED );
		setBorderKeyMouseOver( BORDER_KEY_MOUSE_OVER );
		setBorderKeyMouseOverSelected( BORDER_KEY_MOUSE_OVER_SELECTED );
		setBorderKeyMousePressed( BORDER_KEY_MOUSE_PRESSED );
		setBorderKeyMousePressedSelected( BORDER_KEY_MOUSE_PRESSED_SELECTED );

		Border border = BorderFactory.createEmptyBorder( 1, 1, 1, 1 );
		setNormalBorder( border );
		setMouseOverBorder( border );
		setMousePressedBorder( border );
	}
	
	@Override
	protected void paintFocus( Graphics g ){
		// do not paint the focus indicators
	}

	@Override
	protected Dimension getMinimumIconSize(){
		return new Dimension( 3, 3 );
	}
	
	@Override
	public Dimension getPreferredSize(){
		Dimension result = super.getPreferredSize();
		result.width -= 2;
		result.height -= 2;
		return result;
	}
}
