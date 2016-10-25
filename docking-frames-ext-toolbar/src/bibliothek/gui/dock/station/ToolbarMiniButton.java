/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.action.BasicResourceInitializer;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;
import bibliothek.gui.dock.themes.basic.action.buttons.MiniButtonContent;
import bibliothek.gui.dock.themes.border.BorderModifier;

/**
 * A {@link BasicMiniButton} with custom borders, to be shown on the titlebar of
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
	protected MiniButtonContent createButtonContent(){
		return new MiniButtonContent(){
			@Override
			protected Dimension getMinimumIconSize(){
				return new Dimension( 3, 3 );		
			}
		};
	}
	
	@Override
	protected void paintFocus( Graphics g ){
		// do not paint the focus indicators
	}

	@Override
	public Dimension getPreferredSize(){
		Dimension result = super.getPreferredSize();
		result.width -= 2;
		result.height -= 2;
		return result;
	}
}
