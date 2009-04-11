/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

import java.awt.Dimension;
import java.awt.Rectangle;

import bibliothek.gui.Dockable;

/**
 * A layout that puts all tabs at the bottom of the panel, in one or more rows.
 * @author Benjamin Sigg
 */
public class BottomFlowLayout implements TabLayoutManager{
	public Dimension getMinimumSize( TabPane pane ){
		return new Dimension( 10, 10 );
	}

	public Dimension getPreferredSize( TabPane pane ){
		return new Dimension( 10, 10 );
	}

	public void layout( TabPane pane ){
		Rectangle available = pane.getAvailableArea();
		
        int maxwidth = available.width;
        
        int x = 0;
        int y = 0;
        
        int maxRowHeight = 0;
        int rowCount = 0;
        
        Dockable[] dockables = pane.getDockables();
        Tab[] tabs = new Tab[ dockables.length ];
        for( int i = 0; i < tabs.length; i++ ){
        	tabs[i] = pane.putOnTab( dockables[i] );
        }
        
        // calculate the required size for the tabs
        Dimension required = getPreferredSize( tabs, pane );
        
        // put the selection area at the top
        if( required.height < available.height ){
        	pane.setSelectedBounds( new Rectangle( available.x, available.y, available.width, available.height - required.height ) );
        }
        else{
        	pane.setSelectedBounds( new Rectangle( available.x, available.y, available.width, 0 ) );
        }
        
        // put the tabs at the bottom
        int dx = available.x;
        int dy = available.y = available.height - required.height;
        
        for( Tab tab : tabs ){
            Dimension size = tab.getPreferredSize();
            
            if( x + size.width > maxwidth && rowCount > 0 ){
                rowCount = 0;
                y += maxRowHeight;
                x = 0;
                maxRowHeight = 0;
            }
            
            tab.setBounds( new Rectangle( x+dx, y+dy, size.width, size.height ));
            x += size.width;
            maxRowHeight = Math.max( maxRowHeight, size.height );
            rowCount++;
        }
	}
	
	private Dimension getPreferredSize( Tab[] tabs, TabPane pane ){
        int width;
        
        if( pane == null )
            width = Integer.MAX_VALUE;
        else
            width = pane.getAvailableArea().width;
        
        int maxWidth = 0;
        int currentWidth = 0;
        int currentHeight = 0;
        int left = 0;
        int height = 0;
        
        for( Tab tab : tabs ){
            Dimension preferred = tab.getPreferredSize();
            
            if( left == 0 || currentWidth + preferred.width <= width ){
                currentWidth += preferred.width;
                currentHeight = Math.max( currentHeight, preferred.height );
                left++;
            }
            else{
                height += currentHeight;
                maxWidth = Math.max( maxWidth, currentWidth );
                left = 0;
                
                currentWidth = preferred.width;    
                currentHeight = preferred.height;
                left++;
            }
        }
        
        
        height += currentHeight;
        maxWidth = Math.max( maxWidth, currentWidth );
    
        return new Dimension( maxWidth, height );
	}
	
	public void install( TabPane pane ){
		// nothing to do
	}


	public void uninstall( TabPane pane ){
		// nothing to do
	}

}
