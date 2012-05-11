/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack.tab;


/**
 * A set of configurations for defining the look of tabs. New configurations are
 * created by the {@link TabConfigurations} factory.
 * @author Benjamin Sigg
 */
public class TabConfiguration {
	/** Tells at which moment actions on a tab should be made invisible */
	public static enum ActionHiding{
		/** They just stay */
		NEVER,
		/** Actions are made invisible when the tab itself is smaller then an action-button */
		NO_SPACE_LEFT,
		/** 
		 * Actions are made invisible right before the icon on the tab would disappear. Please note that
		 * if using {@link IconHiding#TEXT_DISAPPEARING}, the icon may still disappear. In such cases it
		 * is better to use <code>TEXT_DISAPPEARING</code> for the actions as well.
	     */
		ICON_DISAPPEARING,
		/** Actions are made invisible right before the text on the tab would disappear */
		TEXT_DISAPPEARING
	}
	
	/** Tells at which moment the icon on a tab is made invisible */
	public static enum IconHiding{
		/** The icon is never made invisible */
		NEVER,
		/** If there is not enough space to paint the full icon, then it is not painted at all */
		NO_SPACE_LEFT,
		/** If there is not enough space to paint text and icon at the same time, then the icon is not painted at all */
		TEXT_DISAPPEARING
	}
	
	/** if and when to hide the actions */
	private ActionHiding actionHiding = ActionHiding.ICON_DISAPPEARING;

	/** if and when to hide the icon */
	private IconHiding iconHiding = IconHiding.NO_SPACE_LEFT;
	
	/** whether the actions still use up space even if hidden */
	private boolean hiddenActionUsingSpace = true;
	
	/** allow the label to remain as big as the icon even if there is not enough space */
	private boolean keepLabelBig = true;
	
	/**
	 * Tells if and when to make buttons on the tab invisible.
	 * @param actionHiding hiding strategy, not <code>null</code>
	 */
	public void setActionHiding( ActionHiding actionHiding ){
		if( actionHiding == null ){
			throw new IllegalArgumentException( "actionHiding must not be null" );
		}
		this.actionHiding = actionHiding;
	}
	
	/**
	 * Gets if and when buttons on the tab are made invisible.
	 * @return the strategy, not <code>null</code>
	 */
	public ActionHiding getActionHiding(){
		return actionHiding;
	}
	
	/**
	 * Tells if and when to make the icon invisible.
	 * @param iconHiding hiding strategy, not <code>null</code>
	 */
	public void setIconHiding( IconHiding iconHiding ){
		if( iconHiding == null ){
			throw new IllegalArgumentException( "iconHiding must not be null" );
		}
		this.iconHiding = iconHiding;
	}
	
	/**
	 * Gets if and when the icon is made invisible.
	 * @return the strategy, not <code>null</code>
	 */
	public IconHiding getIconHiding(){
		return iconHiding;
	}
	
	/**
	 * Sets whether the label should be kept big enough to show the icon even if the tab itself
	 * demands a size that is smaller. This option can lead to some graphical errors as the icon
	 * appears to be outside of the tab. The default value of this option is <code>true</code>.
	 * @param keepLabelBig whether to keep the label big
	 */
	public void setKeepLabelBig( boolean keepLabelBig ){
		this.keepLabelBig = keepLabelBig;
	}
	
	/**
	 * Tells whether the label should be kept big enough to show the icon even if the tab itself
	 * demands a size that is smaller.
	 * @return whether to keep the label big
	 * @see #setKeepLabelBig(boolean)
	 */
	public boolean isKeepLabelBig(){
		return keepLabelBig;
	}
	
	/**
	 * Tells whether hidden actions still can influence the layout by using up some space. If <code>true</code> hidden
	 * actions are assumed to require <code>0</code> pixels <i>but still have a border</i>, otherwise the actions are
	 * treated as if they would not exist. The default value of this property is <code>true</code>.
	 * @param hiddenActionUsingSpace whether hidden actions have an influence on the layout or not
	 */
	public void setHiddenActionUsingSpace( boolean hiddenActionUsingSpace ){
		this.hiddenActionUsingSpace = hiddenActionUsingSpace;
	}
	
	/**
	 * Tells whether hidden actions still use up some space.
	 * @return wether hidden actions use space
	 * @see #setHiddenActionUsingSpace(boolean)
	 */
	public boolean isHiddenActionUsingSpace(){
		return hiddenActionUsingSpace;
	}
}
