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
import java.awt.Point;
import java.awt.Rectangle;

import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * This layout puts all tabs in one, or if there is not enough space, in many,
 * rows. This {@link TabLayoutManager} does never create menus and ignores the 
 * info panel.<br>
 * Please do note that this manager is no longer used by any of the default
 * {@link DockTheme}s. It remains however in the framework for future uses.
 * @author Benjamin Sigg
 */
public class RowLayout implements TabLayoutManager{
	public Dimension getMinimumSize( TabPane pane ){
		return new Dimension( 10, 10 );
	}

	public Dimension getPreferredSize( TabPane pane ){
		return new Dimension( 10, 10 );
	}
	
	/**
	 * Creates a conversion for converting a layout that is
	 * {@link TabPlacement#TOP_OF_DOCKABLE} to the current {@link TabPane#getDockTabPlacement() orientation}.
	 * @param pane the pane for which the conversion is required
	 * @return the conversion
	 */
	private AxisConversion getConversion( TabPane pane ){
		return new DefaultAxisConversion( pane.getAvailableArea(), pane.getDockTabPlacement() );
	}
	
	public int getIndexOfTabAt( TabPane pane, Point mouseLocation ){
		int index = 0;
		for( Tab tab : pane.getTabs() ){
			if( tab.getBounds().contains( mouseLocation )){
				return index;
			}
			index++;
		}
		return -1;
	}

	public void layout( TabPane pane ){
		AxisConversion conversion = getConversion( pane );
		
		TabPlacement orientation = pane.getDockTabPlacement();
		Rectangle available = conversion.viewToModel( pane.getAvailableArea() );
		
        int maxwidth = available.width;
        
        Dockable[] dockables = pane.getDockables();
        Tab[] tabs = new Tab[ dockables.length ];
        for( int i = 0; i < tabs.length; i++ ){
        	tabs[i] = pane.putOnTab( dockables[i] );
        	tabs[i].setOrientation( orientation );
        }
        
        // calculate the required size for the tabs
        Dimension required = conversion.viewToModel( getPreferredSize( tabs, pane ) );
        
        // put the selection area at the bottom
        if( required.height < available.height ){
        	pane.setSelectedBounds( conversion.modelToView( new Rectangle( 0, required.height, available.width, available.height - required.height ) ) );
        }
        else{
        	pane.setSelectedBounds( conversion.modelToView( new Rectangle( 0, 0, available.width, 0 ) ) );
        }
        
        // put the tabs at the top
        int dx = 0;
        int dy = 0;

        int x = 0;
        int y = 0;
        
        int maxRowHeight = -1;
        int rowCount = 0;
        
        for( int i = 0; i < tabs.length; i++ ){
        	Dimension size = conversion.viewToModel( tabs[i].getPreferredSize() );
            
            if( x + size.width > maxwidth && rowCount > 0 ){
                rowCount = 0;
                y += maxRowHeight;
                x = 0;
                maxRowHeight = -1;
            }            
            
        	// find row height
        	if( maxRowHeight == -1 ){
        		maxRowHeight = 0;
	        	int tx = x;
	        	
	        	for( int j = i; j < tabs.length; j++ ){
	        		Dimension tsize = conversion.viewToModel( tabs[j].getPreferredSize() );
	            
	        		if( tx + tsize.width > maxwidth && rowCount > 0 ){
	        			rowCount = 0;
	        			break;
	        		}
	            
	        		tx += tsize.width;
	        		maxRowHeight = Math.max( maxRowHeight, tsize.height );
	        		rowCount++;
	        	}
        	}
            
            tabs[i].setBounds( conversion.modelToView( new Rectangle( x+dx, y+dy-size.height+maxRowHeight, size.width, size.height )) );
            x += size.width;
            rowCount++;
        }
	}
	
	private Dimension getPreferredSize( Tab[] tabs, TabPane pane ){
		AxisConversion conversion = getConversion( pane );
		
        int width;
        
        if( pane == null )
            width = Integer.MAX_VALUE;
        else
            width = conversion.viewToModel( pane.getAvailableArea() ).width;
        
        int maxWidth = 0;
        int currentWidth = 0;
        int currentHeight = 0;
        int left = 0;
        int height = 0;
        
        for( Tab tab : tabs ){
            Dimension preferred = conversion.viewToModel( tab.getPreferredSize() );
            
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
    
        return conversion.modelToView( new Dimension( maxWidth, height ) );
	}
	
	public void install( TabPane pane ){
		// nothing to do
	}


	public void uninstall( TabPane pane ){
		// nothing to do
	}

}
