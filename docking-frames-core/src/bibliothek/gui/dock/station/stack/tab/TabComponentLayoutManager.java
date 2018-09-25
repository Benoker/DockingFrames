/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.swing.OrientedLabel;

/**
 * A {@link LayoutManager} that can be used by {@link Component}s that show an
 * {@link OrientedLabel} and a {@link ButtonPanel}.
 * @author Benjamin Sigg
 */
public class TabComponentLayoutManager implements LayoutManager{
	/** the free space around the content to the semi open sides */
	private int freeSpaceToSideBorder;
	/** the free space around the content to the closed side */
	private int freeSpaceToParallelBorder;
	/** free space between {@link #label} and {@link #actions} */
	private int freeSpaceBetweenLabelAndActions;
	/** the free space around the content to the open side */
	private int freeSpaceToOpenSide;
	/** insets to be added to the label */
	private Insets labelInsets = new Insets( 0, 0, 0, 0 );
	/** insets to be added to the actions */
	private Insets actionInsets = new Insets( 0, 0, 0, 0 );
	
	/** the current layout */
	private TabPlacement orientation;
	
	private OrientedLabel label;
	private ButtonPanel actions;
	
	/** details about the layout */
	private TabConfiguration configuration;
	
	/**
	 * Creates a new layout manager.
	 * @param label the label shown on the tab
	 * @param panel the actions shown on the tab
	 * @param configuration the exact look and behavior of the tab
	 */
	public TabComponentLayoutManager( OrientedLabel label, ButtonPanel panel, TabConfiguration configuration ){
		this.label = label;
		this.actions = panel;
		setOrientation( TabPlacement.TOP_OF_DOCKABLE );
		setConfiguration( configuration );
	}
	
	/**
	 * Gets the panels showing the actions
	 * @return the actions, not <code>null</code>
	 */
	public ButtonPanel getActions(){
		return actions;
	}
	
	/**
	 * Gets the label showing icon and text
	 * @return the label, not <code>null</code>
	 */
	public OrientedLabel getLabel(){
		return label;
	}
	
	/**
	 * Gets the current configuration of the tab
	 * @return the configuration, not <code>null</code>
	 */
	public TabConfiguration getConfiguration(){
		return configuration;
	}
	
	/**
	 * Changes the look and behavior of the tab.
	 * @param configuration the new configuration to use, not <code>null</code>
	 */
	public void setConfiguration( TabConfiguration configuration ){
		if( configuration == null ){
			throw new IllegalArgumentException( "configuration must not be null" );
		}
		this.configuration = configuration;
		label.revalidate();
	}
	
	/**
	 * Sets the size of the free space between content and the open side.
	 * @param freeSpaceToOpenSide the size
	 */
	public void setFreeSpaceToOpenSide( int freeSpaceToOpenSide ){
		this.freeSpaceToOpenSide = freeSpaceToOpenSide;
	}
	
	/**
	 * Gets the size of the open side.
	 * @return the size
	 * @see #setFreeSpaceToOpenSide(int)
	 */
	public int getFreeSpaceToOpenSide(){
		return freeSpaceToOpenSide;
	}
	
	/**
	 * Sets the size of the gap that is between the label (icon and text) and the
	 * {@link DockAction}s (if there are any).
	 * @param freeSpaceBetweenLabelAndActions the size
	 */
	public void setFreeSpaceBetweenLabelAndActions( int freeSpaceBetweenLabelAndActions ){
		this.freeSpaceBetweenLabelAndActions = freeSpaceBetweenLabelAndActions;
	}
	
	/**
	 * Gets the size of the gap between text/icon and actions.
	 * @return the size
	 * @see #setFreeSpaceBetweenLabelAndActions(int)
	 */
	public int getFreeSpaceBetweenLabelAndActions(){
		return freeSpaceBetweenLabelAndActions;
	}
	
	/**
	 * Sets the size of the free space between content and the border at the side
	 * of the {@link Dockable}.
	 * @param freeSpaceToParallelBorder the size
	 */
	public void setFreeSpaceToParallelBorder( int freeSpaceToParallelBorder ){
		this.freeSpaceToParallelBorder = freeSpaceToParallelBorder;
	}
	
	/**
	 * Gets the size of the border at the side of the {@link Dockable}
	 * @return the size
	 * @see #setFreeSpaceToParallelBorder(int) 
	 */
	public int getFreeSpaceToParallelBorder(){
		return freeSpaceToParallelBorder;
	}
	
	/**
	 * Sets the size of the free space between the borders that are on the same line as
	 * the text/icon and the actions.
	 * @param freeSpaceToSideBorder the size
	 */
	public void setFreeSpaceToSideBorder( int freeSpaceToSideBorder ){
		this.freeSpaceToSideBorder = freeSpaceToSideBorder;
	}
	
	/**
	 * Gets the size of the free space between the borders that are on the same line as
	 * the text/icon and the actions.
	 * @return the size
	 * @see #setFreeSpaceToSideBorder(int)
	 */
	public int getFreeSpaceToSideBorder(){
		return freeSpaceToSideBorder;
	}
	
	/**
	 * Sets the space that should be left empty around the label.
	 * @param labelInsets the empty space, not <code>null</code>
	 */
	public void setLabelInsets( Insets labelInsets ){
		if( labelInsets == null ){
			throw new IllegalArgumentException( "insets must not be null" );
		}
		this.labelInsets = new Insets( labelInsets.top, labelInsets.left, labelInsets.bottom, labelInsets.right );
	}
	
	/**
	 * Gets the empty space around the label.
	 * @return the empty space
	 */
	public Insets getLabelInsets(){
		return labelInsets;
	}
	
	/**
	 * Sets the empty space around the actions.
	 * @param actionInsets the empty space, not <code>null</code>
	 */
	public void setActionInsets( Insets actionInsets ){
		if( actionInsets == null ){
			throw new IllegalArgumentException( "insets must not be null" );
		}
		this.actionInsets = new Insets( actionInsets.top, actionInsets.left, actionInsets.bottom, actionInsets.right );
	}
	
	/**
	 * Gets the empty space around the actions.
	 * @return the empty space
	 */
	public Insets getActionInsets(){
		return actionInsets;
	}
	
	public void setOrientation( TabPlacement orientation ){
		if( orientation == null )
			throw new IllegalArgumentException( "orientation must not be null" );

		if( this.orientation != orientation ){	
			this.orientation = orientation;
			label.setHorizontal( orientation.isHorizontal() );
			switch( orientation ){
				case BOTTOM_OF_DOCKABLE:
					actions.setOrientation( DockTitle.Orientation.SOUTH_SIDED );
					break;
				case LEFT_OF_DOCKABLE:
					actions.setOrientation( DockTitle.Orientation.EAST_SIDED );
					break;
				case RIGHT_OF_DOCKABLE:
					actions.setOrientation( DockTitle.Orientation.WEST_SIDED );
					break;
				case TOP_OF_DOCKABLE:
					actions.setOrientation( DockTitle.Orientation.NORTH_SIDED );
					break;
			}
		}
	}
	
	/**
	 * Gets the current position of the tab in relation to the {@link Dockable}s.
	 * @return the location of the tab
	 */
	public TabPlacement getOrientation(){
		return orientation;
	}
	
	public void addLayoutComponent( String name, Component comp ){
		if( comp != label && comp != actions ){
			throw new IllegalArgumentException( "must add either label or panel" );
		}
	}

	public Dimension preferredLayoutSize( Container parent ){
		Dimension size = label.getPreferredSize();
		Dimension result;
		
		if( orientation.isHorizontal() ){
			result = new Dimension( 
					size.width+2*freeSpaceToSideBorder+labelInsets.left+labelInsets.right,
					size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder+labelInsets.top+labelInsets.bottom );
			
			if( actions.hasActions() ){
				result.width += freeSpaceBetweenLabelAndActions;
				size = actions.getPreferredSize();
				result.width += size.width+actionInsets.left+actionInsets.right;
				result.height = Math.max( result.height, size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder+actionInsets.top+actionInsets.bottom );
			}
			else{
				result.width += actionInsets.right;
				result.height = Math.max( result.height, size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder+actionInsets.bottom );
			}
		}
		else{
			result = new Dimension( 
					size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder+labelInsets.left+labelInsets.right,
					size.height+2*freeSpaceToSideBorder+labelInsets.top+labelInsets.bottom );
			if( actions.hasActions() ){
				result.height += freeSpaceBetweenLabelAndActions;
				size = actions.getPreferredSize();
				result.height += size.height+actionInsets.top+actionInsets.bottom;
				result.width = Math.max( result.width, size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder+actionInsets.left+actionInsets.right );
			}
			else{
				result.height += actionInsets.bottom;
				result.width = Math.max( result.width, size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder+actionInsets.right );
			}
		}
		return result;
	}
	
	public Dimension minimumLayoutSize( Container parent ){
		Dimension size = label.getMinimumSize();
		Dimension result;
		
		if( orientation.isHorizontal() ){
			result = new Dimension( 
					size.width+2*freeSpaceToSideBorder+labelInsets.left+labelInsets.right,
					size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder+labelInsets.top+labelInsets.bottom );
			
			if( actions.hasActions() ){
				result.width += freeSpaceBetweenLabelAndActions;
				size = actions.getMinimumSize();
				result.width += size.width+actionInsets.left+actionInsets.right;
				result.height = Math.max( result.height, size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder+actionInsets.top+actionInsets.bottom );
			}
			else{
				result.width += actionInsets.right;
				result.height = Math.max( result.height, size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder+actionInsets.bottom );
			}
		}
		else{
			result = new Dimension( 
					size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder+labelInsets.left+labelInsets.right,
					size.height+2*freeSpaceToSideBorder+labelInsets.top+labelInsets.bottom );
			if( actions.hasActions() ){
				result.height += freeSpaceBetweenLabelAndActions;
				size = actions.getMinimumSize();
				result.height += size.height+actionInsets.top+actionInsets.bottom;
				result.width = Math.max( result.width, size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder+actionInsets.left+actionInsets.right );
			}
			else{
				result.height += actionInsets.bottom;
				result.width = Math.max( result.width, size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder+actionInsets.right );
			}
		}
		return result;
	}
	
	public void layoutContainer( Container parent ){
		int width = parent.getWidth();
		int height = parent.getHeight();
		Dimension actionsSize;
		
		boolean showActions = shouldShowActions( parent );
		
		if( showActions ){
			actions.setVisible( true );
			actionsSize = actions.getPreferredSize();
			actionsSize = new Dimension( 
					actionsSize.width+actionInsets.left+actionInsets.right,
					actionsSize.height+actionInsets.top+actionInsets.bottom );
			
		}
		else{
			actions.setVisible( false );
			if( configuration.isHiddenActionUsingSpace() ){
				actionsSize = new Dimension( 
						actionInsets.right,
						actionInsets.bottom );
			}
			else{
				actionsSize = new Dimension( 0, 0 );
			}
		}
		
		
		switch( orientation ){
			case TOP_OF_DOCKABLE:
				label.setBounds(
						freeSpaceToSideBorder+labelInsets.left, 
						freeSpaceToOpenSide+labelInsets.top, 
						labelSize( parent, width-2*freeSpaceToSideBorder - actionsSize.width - labelInsets.left - labelInsets.right, freeSpaceToSideBorder+labelInsets.left, showActions ),
						height-freeSpaceToOpenSide-freeSpaceToParallelBorder - labelInsets.top - labelInsets.bottom );
				if( showActions ){
					int actionsHeight = Math.min( actionsSize.height, height - freeSpaceToOpenSide - freeSpaceToParallelBorder );
					int delta = height-freeSpaceToOpenSide-freeSpaceToParallelBorder-actionsHeight-actionInsets.top-actionInsets.bottom;
					
					actions.setBounds( 
							Math.max( 0, width-freeSpaceToOpenSide-actionsSize.width + actionInsets.left ),
							height-actionsHeight-freeSpaceToParallelBorder-delta/2 + actionInsets.top, 
							actionsSize.width - actionInsets.left - actionInsets.right, 
							actionsHeight - actionInsets.top - actionInsets.bottom );
				}
				break;
			case BOTTOM_OF_DOCKABLE:
				label.setBounds(
						freeSpaceToSideBorder + labelInsets.left, 
						freeSpaceToParallelBorder + labelInsets.top, 
						labelSize( parent, width-2*freeSpaceToSideBorder-actionsSize.width - labelInsets.left - labelInsets.right, freeSpaceToSideBorder + labelInsets.left, showActions ),
						height-freeSpaceToOpenSide-freeSpaceToParallelBorder - labelInsets.top - labelInsets.bottom );
				if( showActions ){
					int actionsHeight = Math.min( actionsSize.height, height-freeSpaceToOpenSide-freeSpaceToParallelBorder );
					int delta = height-freeSpaceToOpenSide-freeSpaceToParallelBorder-actionsHeight-actionInsets.top-actionInsets.bottom;
					
					actions.setBounds(
							Math.max( 0, width-freeSpaceToOpenSide-actionsSize.width + actionInsets.left ),
							freeSpaceToParallelBorder+delta/2 + actionInsets.top,
							actionsSize.width - actionInsets.left - actionInsets.right,
							actionsHeight - actionInsets.top - actionInsets.bottom );
				}
				break;
			case RIGHT_OF_DOCKABLE:
				label.setBounds(
						freeSpaceToParallelBorder + labelInsets.left, 
						freeSpaceToSideBorder + labelInsets.top,
						width-freeSpaceToOpenSide-freeSpaceToParallelBorder - labelInsets.left - labelInsets.right,
						labelSize( parent, height-2*freeSpaceToSideBorder-actionsSize.height - labelInsets.top - labelInsets.bottom, freeSpaceToSideBorder + labelInsets.top, showActions ) );
				if( showActions ){
					int actionsWidth = Math.min( actionsSize.width, width-freeSpaceToOpenSide-freeSpaceToParallelBorder );
					int delta = width-freeSpaceToOpenSide-freeSpaceToParallelBorder-actionsWidth-actionInsets.left-actionInsets.right;
					
					actions.setBounds(
							freeSpaceToParallelBorder+delta/2 + actionInsets.left,
							Math.max( 0, height-freeSpaceToOpenSide-actionsSize.height + actionInsets.top ),
							actionsWidth - actionInsets.left - actionInsets.right,
							actionsSize.height - actionInsets.top - actionInsets.bottom );
				}
				break;
			case LEFT_OF_DOCKABLE:
				label.setBounds(
						freeSpaceToOpenSide + labelInsets.left, 
						freeSpaceToSideBorder + labelInsets.top,
						width-freeSpaceToOpenSide-freeSpaceToParallelBorder - labelInsets.left - labelInsets.right,
						labelSize( parent, height-2*freeSpaceToSideBorder - actionsSize.height - labelInsets.top - labelInsets.bottom, freeSpaceToSideBorder + labelInsets.top, showActions ) );
				if( showActions ){
					int actionsWidth = Math.min( actionsSize.width, width-freeSpaceToOpenSide-freeSpaceToParallelBorder );
					int delta = width-freeSpaceToOpenSide-freeSpaceToParallelBorder-actionsWidth-actionInsets.left-actionInsets.right;
					
					actions.setBounds(
							width-actionsWidth-freeSpaceToParallelBorder-delta/2 + actionInsets.left,
							Math.max( 0, height-freeSpaceToOpenSide-actionsSize.height + actionInsets.top ),
							actionsWidth - actionInsets.left - actionInsets.right,
							actionsSize.height - actionInsets.top - actionInsets.bottom );
				}
			break;
		}
		label.setIconHidden( !shouldShowIcon() );
	}
	
	private int labelSize( Container parent, int suggested, int start, boolean showActions ){
		if( showActions ){
			return suggested;
		}
		if( configuration.isKeepLabelBig() && label.getIcon() != null ){
			if( orientation.isHorizontal() ){
				return Math.min( Math.max( suggested, label.getIconOffset() + label.getIcon().getIconWidth() ), parent.getWidth() - start );
			}
			else{
				return Math.min( Math.max( suggested, label.getIconOffset() + label.getIcon().getIconHeight() ), parent.getHeight() - start );
			}
		}
		return suggested;
	}
	
	/**
	 * Using the current {@link TabConfiguration}, this method decides whether there is enough space to show
	 * the actions or not.
	 * @param parent the parent {@link Container} of the label and the actions
	 * @return wether the actions should be shown
	 */
	protected boolean shouldShowActions( Container parent ){
		if( !actions.hasActions() ){
			return false;
		}
		
		if( orientation.isHorizontal() ){
			int minSize = -1;
			int actionSize = actions.getPreferredSize().width;
			int labelDelta = freeSpaceToSideBorder + freeSpaceBetweenLabelAndActions + freeSpaceToOpenSide + labelInsets.left + labelInsets.right + actionInsets.left + actionInsets.right;
			
			switch( getConfiguration().getActionHiding() ){
				case NEVER:
					return true;
				case NO_SPACE_LEFT:
					minSize = actionSize;
					break;
				case TEXT_DISAPPEARING:
					minSize = labelDelta + label.getPreferredSize().width + actionSize;
					break;
				case ICON_DISAPPEARING:
					if( label.getIcon() == null ){
						minSize = -1;
					}
					else{
						minSize = labelDelta + label.getIconOffset() + label.getIcon().getIconWidth() + actionSize;
					}
					break;
			}
			return minSize <= parent.getWidth();
		}
		else{
			int minSize = -1;
			int actionSize = actions.getPreferredSize().height;
			int labelDelta = freeSpaceToSideBorder + freeSpaceBetweenLabelAndActions + freeSpaceToOpenSide + labelInsets.top + labelInsets.bottom + actionInsets.top + actionInsets.bottom;
			switch( getConfiguration().getActionHiding() ){
				case NEVER:
					return true;
				case NO_SPACE_LEFT:
					minSize = actionSize;
					break;
				case TEXT_DISAPPEARING:
					minSize = labelDelta + label.getPreferredSize().height + actionSize;
					break;
				case ICON_DISAPPEARING:
					if( label.getIcon() == null ){
						minSize = -1;
					}
					else{
						minSize = labelDelta + label.getIconOffset() + label.getIcon().getIconHeight() + actionSize;
					}
					break;
			}
			return minSize <= parent.getHeight();
		}
	}
	
	/**
	 * Tells whether the icon should be shown for the current size of {@link #getLabel() the label}.
	 * @return whether to show the icon
	 */
	protected boolean shouldShowIcon(){
		if( label.getIcon() == null ){
			return true;
		}
		
		if( orientation.isHorizontal() ){
			switch( getConfiguration().getIconHiding() ){
				case NEVER:
					return true;
				case NO_SPACE_LEFT:
					return label.getWidth() >= label.getIconOffset() + label.getIcon().getIconWidth();
				case TEXT_DISAPPEARING:
					return label.getWidth() >= label.getPreferredSize().getWidth();
			}
		}
		else{
			switch( getConfiguration().getIconHiding() ){
				case NEVER:
					return true;
				case NO_SPACE_LEFT:
					return label.getHeight() >= label.getIconOffset() + label.getIcon().getIconHeight();
				case TEXT_DISAPPEARING:
					return label.getHeight() >= label.getPreferredSize().getHeight();
			}
		}
		return true;
	}
		
	public void removeLayoutComponent( Component comp ){
		throw new IllegalArgumentException( "must not remove any components" );
	}
}
