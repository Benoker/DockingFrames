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
package bibliothek.gui.dock.station.stack;

import java.awt.*;

/**
 * {@link LayoutManager} that works like {@link FlowLayout}, but the components
 * are not centered in their cell but stick to the top of the cell.
 * @author Benjamin Sigg
 * @deprecated this class is no longer used anywhere and will be removed
 */
@Deprecated
public class StackTabListLayout implements LayoutManager{
    public void addLayoutComponent( String name, Component comp ) {
        // ignore
    }
    
    public void removeLayoutComponent( Component comp ) {
        // ignore
    }
    
    public Dimension minimumLayoutSize( Container target ){
        return preferredLayoutSize( target );
    }
    
    public Dimension preferredLayoutSize( Container target ){
        int width;
        
        if( target.getParent() == null )
            width = Integer.MAX_VALUE;
        else
            width = target.getParent().getWidth();
        
        int maxWidth = 0;
        int currentWidth = 0;
        int currentHeight = 0;
        int left = 0;
        int height = 0;
        
        for( int i = 0, n = target.getComponentCount(); i<n; i++ ){
            Dimension preferred = target.getComponent(i).getPreferredSize();
            
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
    
    public void layoutContainer( Container parent ) {
        int maxwidth = parent.getWidth();
        
        int x = 0;
        int y = 0;
        
        int maxRowHeight = 0;
        int rowCount = 0;
        
        for( int i = 0, n = parent.getComponentCount(); i<n; i++ ){
            Component next = parent.getComponent( i );
            Dimension size = next.getPreferredSize();
            
            if( x + size.width > maxwidth && rowCount > 0 ){
                rowCount = 0;
                y += maxRowHeight;
                x = 0;
                maxRowHeight = 0;
            }
            
            next.setBounds( x, y, size.width, size.height );
            x += size.width;
            maxRowHeight = Math.max( maxRowHeight, size.height );
            rowCount++;
        }
    }
}
