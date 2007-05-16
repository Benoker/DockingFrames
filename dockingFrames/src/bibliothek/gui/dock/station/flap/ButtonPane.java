/**
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

package bibliothek.gui.dock.station.flap;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JPanel;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.FlapDockStation.Direction;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This panel is used by the {@link FlapDockStation} to display some button-titles.
 */
public class ButtonPane extends JPanel{
    /** The owner of this panel */
    private FlapDockStation station;
    
    /** Information where currently a {@link Dockable} is dropped */
    private FlapDropInfo dropInfo;
    
    /** A mapping which {@link Dockable} has which {@link DockTitle} */
    private Map<Dockable, DockTitle> buttonTitles;
    
    /**
     * Constructs a new panel.
     * @param station The owner
     * @param titles The titles (this map is modified by the station)
     */
    public ButtonPane( FlapDockStation station, Map<Dockable, DockTitle> titles ){
        super( null );
        this.station = station;
        this.buttonTitles = titles;
    }
    
    /**
     * Tells where a {@link Dockable} is to be put onto the station
     * @param dropInfo the target or <code>null</code>
     */
    public void setDropInfo( FlapDropInfo dropInfo ) {
        this.dropInfo = dropInfo;
        repaint();
    }
    
    /**
     * Tells whether the given point is inside a button.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return <code>true</code> if the point is inside a child of this panel
     */
    public boolean titleContains( int x, int y ){
        if( getComponentCount() == 0 ){
            int w = getWidth() / 3;
            int h = getHeight() / 3;
            
            return contains( x - w, y - h ) && contains( x + w, y + h );
        }
        
        for( int i = 0, n = getComponentCount(); i<n; i++ ){
            Component c = getComponent( i );
            if( c.contains( x - c.getX(), y - c.getY() ))
                return true;
        }
        
        return false;
    }
    
    @Override
    public void doLayout() {
        Insets insets = getInsets();
        
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        if( station.getDirection() == Direction.NORTH || station.getDirection() == Direction.SOUTH ){
            int count = getComponentCount();
            int[] widths = new int[ count ];
            int preferredHeight = 0;
            int sum = 0;
            
            for( int i = 0; i < count; i++ ){
                Dimension size = getComponent( i ).getPreferredSize();
                widths[i] = size.width;
                preferredHeight = Math.max( preferredHeight, size.height );
                sum += widths[i];
            }
            
            if( station.isSmallButtons() && preferredHeight < height ){
                int delta = height - preferredHeight;
                y += delta/2;
                height = preferredHeight;
            }
            
            if( sum > width ){
                double ratio = ((double)width) / sum;
                for( int i = 0; i < count; i++ ){
                    int temp = (int)(widths[i]*ratio);
                    getComponent( i ).setBounds( x, y, temp, height );
                    x += temp;
                }
            }
            else{
                for( int i = 0; i < count; i++ ){
                    getComponent( i ).setBounds( x, y, widths[i], height );
                    x += widths[i];
                }
            }
        }
        else{
            int count = getComponentCount();
            int[] heights = new int[ count ];
            int preferredWidth = 0;
            int sum = 0;
            
            for( int i = 0; i < count; i++ ){
                Dimension size = getComponent( i ).getPreferredSize();
                heights[i] = size.height;
                preferredWidth = Math.max( preferredWidth, size.width );
                sum += heights[i];
            }
            
            if( station.isSmallButtons() && preferredWidth < width ){
                int delta = width - preferredWidth;
                x += delta/2;
                width = preferredWidth;
            }                
            
            if( sum > height ){
                double ratio = ((double)height) / sum;
                for( int i = 0; i < count; i++ ){
                    int temp = (int)(heights[i]*ratio);
                    getComponent( i ).setBounds( x, y, width, temp );
                    y += temp;
                }
            }
            else{
                for( int i = 0; i < count; i++ ){
                    getComponent( i ).setBounds( x, y, width, heights[i] );
                    y += heights[i];
                }
            }
        }
        
        repaint();
    }
    
    /**
     * Ensures that all titles of the title-map, which was given to the
     * constructor, are shown on this panel.
     */
    public void resetTitles(){
        removeAll();
        for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
            Dockable dockable = station.getDockable( i );
            DockTitle title = buttonTitles.get( dockable );
            if( title != null ){
                add( title.getComponent() );
            }
        }
        revalidate();
    }
    
    @Override
    public void paint( Graphics g ) {
        super.paint(g);
        if( dropInfo != null && dropInfo.isDraw() && !dropInfo.isCombine() ){
            int left = dropInfo.getIndex()-1;
            int right = left+1;
            
            Dockable leftDockable = left < 0 ? null : station.getDockable( left );
            Dockable rightDockable = right >= station.getDockableCount() ? null : station.getDockable( right );
            
            DockTitle leftTitle = leftDockable == null ? null : buttonTitles.get( leftDockable );
            DockTitle rightTitle = rightDockable == null ? null : buttonTitles.get( rightDockable );
            
            boolean horizontal = station.getDirection() == Direction.SOUTH || station.getDirection() == Direction.NORTH;
            
            int x1, y1, x2, y2;
            
            if( leftTitle == null && rightTitle == null ){
                if( horizontal ){
                    x1 = getX() + getWidth()/2;
                    y1 = 0;
                    x2 = x1;
                    y2 = getHeight();
                }
                else{
                    x1 = 0;
                    y1 = getY() + getHeight()/2;
                    x2 = getWidth();
                    y2 = y1;
                }
            }
            else if( leftTitle == null ){
                if( horizontal ){
                    x1 = rightTitle.getComponent().getX();
                    y1 = 0;
                    x2 = x1;
                    y2 = getHeight();
                }
                else{
                    x1 = 0;
                    y1 = rightTitle.getComponent().getY();
                    x2 = getWidth();
                    y2 = y1;
                }
            }
            else if( rightTitle == null ){
                if( horizontal ){
                    x1 = leftTitle.getComponent().getX() + leftTitle.getComponent().getWidth();
                    y1 = 0;
                    x2 = x1;
                    y2 = getHeight();
                }
                else{
                    x1 = 0;
                    y1 = leftTitle.getComponent().getY() + leftTitle.getComponent().getHeight();
                    x2 = getWidth();
                    y2 = y1;
                }
            }
            else{
                if( horizontal ){
                    x1 = (rightTitle.getComponent().getX() + leftTitle.getComponent().getX() + leftTitle.getComponent().getWidth()) / 2;
                    y1 = 0;
                    x2 = x1;
                    y2 = getHeight();
                }
                else{
                    x1 = 0;
                    y1 = (rightTitle.getComponent().getY() + leftTitle.getComponent().getY() + leftTitle.getComponent().getHeight() ) / 2;
                    x2 = getWidth();
                    y2 = y1;
                }
            }
            
            station.getPaint().drawInsertionLine( g, station, x1, y1, x2, y2 );
        }
    }
    
    /**
     * Searches an location where a {@link Dockable} could be inserted.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @return a location
     */
    public int indexAt( int x, int y ){
        if( station.getDirection() == Direction.SOUTH || station.getDirection() == Direction.NORTH ){
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                DockTitle title = buttonTitles.get( station.getDockable( i ) );
                if( title != null ){
                    int tx = title.getComponent().getX();
                    int tw = title.getComponent().getWidth();
                    
                    if( x <= tx )
                        return i;
                    
                    if( x <= tx + tw ){
                        if( x < tx + tw/2 )
                            return i;
                        else
                            return i+1;
                    }
                }
            }
        }
        else{
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                DockTitle title = buttonTitles.get( station.getDockable( i ) );
                if( title != null ){
                    int ty = title.getComponent().getY();
                    int th = title.getComponent().getHeight();
                    
                    if( y <= ty )
                        return i;
                    
                    if( y <= ty + th ){
                        if( y < ty + th/2 )
                            return i;
                        else
                            return i+1;
                    }
                }
            }
        }
        
        return station.getDockableCount();
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    @Override
    public Dimension getPreferredSize() {
        int width = 0;
        int height = 0;
        
        if( station.getDirection() == Direction.NORTH || station.getDirection() == Direction.SOUTH ){
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                Dockable dockable = station.getDockable( i );
                DockTitle title = buttonTitles.get( dockable );
                if( title != null ){
                    Dimension size = title.getComponent().getPreferredSize();
                    width += size.width;
                    height = Math.max( height, size.height );
                }
            }
        }
        else{
            for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                Dockable dockable = station.getDockable( i );
                DockTitle title = buttonTitles.get( dockable );
                if( title != null ){
                    Dimension size = title.getComponent().getPreferredSize();
                    height += size.height;
                    width = Math.max( width, size.width );
                }
            }
        }
        
        return new Dimension( Math.max( 10, width ), Math.max( 10, height ));
    }
}