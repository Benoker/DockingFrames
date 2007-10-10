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
package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;

public class TabStripLayoutManager implements LayoutManager {
    private RexTabbedComponent tabbed;
    
    public TabStripLayoutManager( RexTabbedComponent tabbed ){
        this.tabbed = tabbed;
    }
    
	public void addLayoutComponent( String name, Component comp ){
		// ignore
	}

	public void layoutContainer( Container parent ){
		synchronized( parent.getTreeLock() ){
			int componentCount = parent.getComponentCount();
			Dimension[] preferreds = new Dimension[ componentCount ];
			int preferredWidthSum = 0;
			
			for( int i = 0; i < componentCount; i++ ){
				preferreds[ i ] = parent.getComponent( i ).getPreferredSize();
				
				int overlap = tabbed.getTabComponent( i ).getOverlap();
				preferredWidthSum = Math.max( 0, preferredWidthSum-overlap );
				
				preferredWidthSum += preferreds[ i ].width;
			}
			
			// can't do anything
			if( preferredWidthSum <= 0 )
				return;
			
			double ratio = Math.min( 1.0, parent.getWidth() / (double)preferredWidthSum );
			
			int x = 0;
			int parentHeight = parent.getHeight();
			
			for( int i = 0; i < componentCount; i++ ){
				Component child = parent.getComponent( i );
				
				int overlap = (int)(Math.round( ratio * tabbed.getTabComponent( i ).getOverlap()));
				int width = (int)Math.round( ratio * preferreds[i].width );
				
				x = Math.max( 0, x-overlap );
				child.setBounds( x, 0, width, parentHeight );
				x += width;
			}
		}
	}

	public Dimension minimumLayoutSize( Container parent ){
		synchronized( parent.getTreeLock() ){
			int width = 0;
			int height = 0;
			
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				Component child = parent.getComponent( i );
				Dimension size = child.getMinimumSize();
				int overlap = tabbed.getTabComponent( i ).getOverlap();
				width = Math.max( 0, width-overlap );
				width += size.width;
				height = Math.max( height, size.height );
			}
			return new Dimension( width, height );
		}
	}

	public Dimension preferredLayoutSize( Container parent ){
		synchronized( parent.getTreeLock() ){
			int width = 0;
			int height = 0;
			
			for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
				Component child = parent.getComponent( i );
				Dimension size = child.getPreferredSize();
				int overlap = tabbed.getTabComponent( i ).getOverlap();
                width = Math.max( 0, width-overlap );
				width += size.width;
				height = Math.max( height, size.height );
			}
			return new Dimension( width, height );
		}
	}

	public void removeLayoutComponent( Component comp ){
		// ignore
	}
}
