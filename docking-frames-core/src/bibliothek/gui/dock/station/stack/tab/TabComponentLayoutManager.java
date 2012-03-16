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

	/** the current layout */
	private TabPlacement orientation;
	
	private OrientedLabel label;
	private ButtonPanel actions;
	
	/**
	 * Creates a new layout manager.
	 * @param label the label shown on the tab
	 * @param panel the actions shown on the tab
	 */
	public TabComponentLayoutManager( OrientedLabel label, ButtonPanel panel ){
		this.label = label;
		this.actions = panel;
		setOrientation( TabPlacement.TOP_OF_DOCKABLE );
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
					size.width+2*freeSpaceToSideBorder,
					size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder );
			
			if( actions.hasActions() ){
				result.width += freeSpaceBetweenLabelAndActions;
				size = actions.getPreferredSize();
				result.width += size.width;
				result.height = Math.max( result.height, size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder );
			}
		}
		else{
			result = new Dimension( 
					size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder,
					size.height+2*freeSpaceToSideBorder );
			if( actions.hasActions() ){
				result.height += freeSpaceBetweenLabelAndActions;
				size = actions.getPreferredSize();
				result.height += size.height;
				result.width = Math.max( result.width, size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder );
			}
		}
		return result;
	}
	
	public Dimension minimumLayoutSize( Container parent ){
		Dimension size = label.getMinimumSize();
		Dimension result;
		
		if( orientation.isHorizontal() ){
			result = new Dimension( 
					size.width+2*freeSpaceToSideBorder,
					size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder );
			
			if( actions.hasActions() ){
				result.width += freeSpaceBetweenLabelAndActions;
				size = actions.getMinimumSize();
				result.width += size.width;
				result.height = Math.max( result.height, size.height+freeSpaceToOpenSide+freeSpaceToParallelBorder );
			}
		}
		else{
			result = new Dimension( 
					size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder,
					size.height+2*freeSpaceToSideBorder );
			if( actions.hasActions() ){
				result.height += freeSpaceBetweenLabelAndActions;
				size = actions.getMinimumSize();
				result.height += size.height;
				result.width = Math.max( result.width, size.width+freeSpaceToOpenSide+freeSpaceToParallelBorder );
			}
		}
		return result;
	}
	
	public void layoutContainer( Container parent ){
		int width = parent.getWidth();
		int height = parent.getHeight();
		Dimension actionsSize;
		
		if( actions.hasActions() ){
			actions.setVisible( true );
			actionsSize = actions.getPreferredSize();
		}
		else{
			actions.setVisible( false );
			actionsSize = new Dimension( 0, 0 );
		}
		
		
		switch( orientation ){
			case TOP_OF_DOCKABLE:
				label.setBounds(
						freeSpaceToSideBorder, 
						freeSpaceToOpenSide, 
						width-2*freeSpaceToSideBorder - actionsSize.width,
						height-freeSpaceToOpenSide-freeSpaceToParallelBorder );
				if( actions.hasActions() ){
					int actionsHeight = Math.min( actionsSize.height, height - freeSpaceToOpenSide - freeSpaceToParallelBorder );
					int delta = height-freeSpaceToOpenSide-freeSpaceToParallelBorder-actionsHeight;
					
					actions.setBounds( 
							width-freeSpaceToOpenSide-actionsSize.width,
							height-actionsHeight-freeSpaceToParallelBorder-delta/2, 
							actionsSize.width, 
							actionsHeight );
				}
				break;
			case BOTTOM_OF_DOCKABLE:
				label.setBounds(
						freeSpaceToSideBorder, 
						freeSpaceToParallelBorder, 
						width-2*freeSpaceToSideBorder-actionsSize.width,
						height-freeSpaceToOpenSide-freeSpaceToParallelBorder );
				if( actions.hasActions() ){
					int actionsHeight = Math.min( actionsSize.height, height-freeSpaceToOpenSide-freeSpaceToParallelBorder );
					int delta = height-freeSpaceToOpenSide-freeSpaceToParallelBorder-actionsHeight;
					
					actions.setBounds(
							width-freeSpaceToOpenSide-actionsSize.width,
							freeSpaceToParallelBorder+delta/2,
							actionsSize.width,
							actionsHeight );
				}
				break;
			case RIGHT_OF_DOCKABLE:
				label.setBounds(
						freeSpaceToParallelBorder, 
						freeSpaceToSideBorder,
						width-freeSpaceToOpenSide-freeSpaceToParallelBorder,
						height-2*freeSpaceToSideBorder-actionsSize.height );
				if( actions.hasActions() ){
					int actionsWidth = Math.min( actionsSize.width, width-freeSpaceToOpenSide-freeSpaceToParallelBorder );
					int delta = width-freeSpaceToOpenSide-freeSpaceToParallelBorder-actionsWidth;
					
					actions.setBounds(
							freeSpaceToParallelBorder+delta/2,
							height-freeSpaceToOpenSide-actionsSize.height,
							actionsWidth,
							actionsSize.height );
				}
				break;
			case LEFT_OF_DOCKABLE:
				label.setBounds(
						freeSpaceToOpenSide, 
						freeSpaceToSideBorder,
						width-freeSpaceToOpenSide-freeSpaceToParallelBorder,
						height-2*freeSpaceToSideBorder - actionsSize.height );
				if( actions.hasActions() ){
					int actionsWidth = Math.min( actionsSize.width, width-freeSpaceToOpenSide-freeSpaceToParallelBorder );
					int delta = width-freeSpaceToOpenSide-freeSpaceToParallelBorder-actionsWidth;
					
					actions.setBounds(
							width-actionsWidth-freeSpaceToParallelBorder-delta/2,
							height-freeSpaceToOpenSide-actionsSize.height,
							actionsWidth,
							actionsSize.height );
				}
			break;
		}
	}
		
	public void removeLayoutComponent( Component comp ){
		throw new IllegalArgumentException( "must not remove any components" );
	}
}
