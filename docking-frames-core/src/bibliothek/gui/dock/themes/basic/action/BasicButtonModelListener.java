/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.themes.basic.action;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.BackgroundPaint;

/**
 * A listener that can be added to a {@link BasicButtonModel} and will
 * be informed about changes in the model.
 * @author Benjamin Sigg
 *
 */
public interface BasicButtonModelListener {
	/**
	 * Called if the text of <code>model</code> changed.
	 * @param model the source of the event
	 * @param oldText the old value, can be <code>null</code>
	 * @param text the new value, can be <code>null</code>
	 */
	public void textChanged( BasicButtonModel model, String oldText, String text );
	
	/**
	 * Called if an icon of <code>model</code> changed.
	 * @param model the source of the event
	 * @param modifier the context in which the icon is used
	 * @param oldIcon the old value
	 * @param icon the new value
	 */
	public void iconChanged( BasicButtonModel model, ActionContentModifier modifier, Icon oldIcon, Icon icon );
	
	/**
	 * Called if the selection state of <code>model</code> changed.
	 * @param model the source of the event.
	 * @param selected the new state
	 */
	public void selectedStateChanged( BasicButtonModel model, boolean selected );

	/**
	 * Called if the action was enabled or disabled.
	 * @param model the source of the event.
	 * @param enabled the new enable state
	 */
	public void enabledStateChanged( BasicButtonModel model, boolean enabled );

	/**
	 * Called if the tooltip of the action changed.
	 * @param model the source of the event
	 * @param old the old value
	 * @param tooltip the new value
	 */
	public void tooltipChanged( BasicButtonModel model, String old, String tooltip );

	/**
	 * Called if the orientation of the view changed.
	 * @param model the source of the event
	 * @param old the old value
	 * @param orientation the new value
	 */
	public void orientationChanged( BasicButtonModel model, Orientation old, Orientation orientation );

	/**
	 * Called if the mouse enters or leaves the view.
	 * @param model the source of the event
	 * @param mouseInside whether the mouse is inside or not
	 */
	public void mouseInside( BasicButtonModel model, boolean mouseInside );

	/**
	 * Called if the mouse is pressed or released.
	 * @param model the source of the event
	 * @param mousePressed the state of the mouse
	 */
	public void mousePressed( BasicButtonModel model, boolean mousePressed );
	
	/**
	 * Called when the background algorithm has been exchanged. 
	 * @param model the source of this event
	 * @param oldBackground the old background algorithm, can be <code>null</code>
	 * @param newBackground the new background algorithm, can be <code>null</code>
	 */
	public void backgroundChanged( BasicButtonModel model, BackgroundPaint oldBackground, BackgroundPaint newBackground );

	/**
	 * Called when a border has changed.
	 * @param model the source of this event
	 * @param key the identifier of the border
	 * @param oldBorder the old border, can be <code>null</code>
	 * @param newBorder the new border, can be <code>null</code>
	 */
	public void borderChanged( BasicButtonModel model, String key, BorderModifier oldBorder, BorderModifier newBorder );
	
	/**
	 * Called after <code>controller</code> was installed on <code>model</code>.
	 * @param model the source of this event
	 * @param controller the controller that was installed
	 */
	public void bound( BasicButtonModel model, DockController controller );
	
	/**
	 * Called after <code>controller</code> was uninstalled from <code>model</code>.
	 * @param model the source of this event
	 * @param controller the controller that was removed
	 */
	public void unbound( BasicButtonModel model, DockController controller );
	
	/**
	 * Called if the user triggered the action.
	 */
	public void triggered();

}
