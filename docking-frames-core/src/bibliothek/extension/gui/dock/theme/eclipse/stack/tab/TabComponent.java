/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabStateInfo;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTab;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.station.stack.tab.TabConfiguration;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * A {@link TabComponent} paints the content of an {@link EclipseTab}. This
 * component is informed about the change of properties that are often needed
 * to paint properly.
 */
public interface TabComponent extends DockElementRepresentative{
	/**
	 * Gets the internal representation of this {@link TabComponent}, this
	 * method must always return the same value.
	 * @return the internal representation, not <code>null</code>
	 */
	public Component getComponent();
	
	/**
	 * Gets information about the state of this tab.
	 * @return information about the state, updates itself.
	 */
	public EclipseTabStateInfo getEclipseTabStateInfo();
	
	/**
	 * Tells this component whether it has to be painted selected or not.
	 * @param selected the selection state of this tab
	 */
	public void setSelected( boolean selected );
	
	/**
	 * Tells this component that its {@link Dockable} gained or lost focus.
	 * @param focused the state of its dockable.
	 */
	public void setFocused( boolean focused );
	
	/**
	 * Enables or disables this component. A disabled component should be visually distinct from an enabled one. Note
	 * that {@link EclipseTab} automatically removes all {@link MouseInputListener}s from a {@link TabComponent} that
	 * is not enabled.
	 * @param enabled whether this component is enabled or not
	 */
	public void setEnabled( boolean enabled );
	
	/**
	 * Tells this component whether to paint an {@link Icon} if not selected.
	 * @param paint <code>true</code> if an icon is always to be painted,
	 * <code>false</code> if icons should only appear on selected tabs.
	 */
	public void setPaintIconWhenInactive( boolean paint );
	
	/**
	 * Sets the placement of the tabs and as a result the orientation of this 
	 * component.
	 * @param orientation the orientation, not <code>null</code>
	 */
	public void setOrientation( TabPlacement orientation );
	
	/**
	 * Informs this {@link TabComponent} by which tab is is used.
	 * @param tab the owner of this {@link TabComponent}
	 */
	public void setTab( EclipseTab tab );
	
	/**
	 * Fine tunes the look and behavior of this tab.
	 * @param configuration the new configuration to use, not <code>null</code>
	 */
	public void setConfiguration( TabConfiguration configuration );
	
	/**
	 * Gets the number of pixels which should be covered at the sides
	 * of this component by other tabs.
	 * @param other the component which may overlap this component
	 * @return the number of overlapped pixels
	 */
	public Insets getOverlap( TabComponent other );
	
	/** Informs this tab that it will be shown soon */
	public void bind();
	
	/** Informs this tab that it is invisible and must not have any connections to other resources */
	public void unbind();

	/**
	 * Gets the minimum size of this tab under the assumption that 
	 * this tab is displayed together with <code>tabs</code>.
	 * @param tabs the displayed tabs, exactly one entry is <code>this</code>
	 * and may contain <code>null</code> entries.
	 * @return the minimum size of this tab
	 */
	public Dimension getMinimumSize( TabComponent[] tabs );
	
	/**
	 * Gets the preferred size of this tab under the assumption that 
	 * this tab is displayed together with <code>tabs</code>.
	 * @param tabs the displayed tabs, exactly one entry is <code>this</code>
	 * and may contain <code>null</code> entries.
	 * @return the preferred size of this tab
	 */
	public Dimension getPreferredSize( TabComponent[] tabs );
	
	/**
	 * Sets the text that should be displayed on this tab.
	 * @param text the new text
	 */
	public void setText( String text );
	
	/**
	 * Sets the tooltip that should be displayed on this tab.
	 * @param tooltip the new tooltip, can be <code>null</code>
	 */
	public void setTooltip( String tooltip );
	
	/**
	 * Sets the icon that should be painted on this tab.
	 * @param icon the icon, can be <code>null</code>
	 */
	public void setIcon( Icon icon );
}
