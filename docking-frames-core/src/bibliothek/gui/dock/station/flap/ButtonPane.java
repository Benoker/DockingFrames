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

package bibliothek.gui.dock.station.flap;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.security.SecureContainer;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.Transparency;

/**
 * This panel is used by the {@link FlapDockStation} to display some button-titles.
 */
public class ButtonPane extends SecureContainer{
    /** The owner of this panel */
    private FlapDockStation station;
    
    /** Information where currently a {@link Dockable} is dropped */
    private FlapDropInfo dropInfo;
    
    /** whether {@link #resetTitles()} has been called but not yet executed */
    private boolean resetStarted = false;
    
    /** the content pane */
    private Content content;
    
    /** handles the {@link Span}s used on this panel */
    private FlapSpanStrategy span;
    
    /**
     * Constructs a new panel.
     * @param station The owner
     */
    public ButtonPane( FlapDockStation station ){
    	span = new FlapSpanStrategy( station, this );
    	content = new Content();
        setBasePane( content );
        this.station = station;
    }
    
    /**
     * Sets the background algorithm of this panel.
     * @param background the algorithm
     */
    public void setBackground( BackgroundAlgorithm background ){
    	content.setBackground( background );
    }
    
    /**
     * Tells where a {@link Dockable} is to be put onto the station
     * @param dropInfo the target or <code>null</code>
     */
    public void setDropInfo( FlapDropInfo dropInfo ) {
        if( dropInfo == null ){
        	span.untease();
        }
        else if( dropInfo.getCombineTarget() != null ){
        	span.tease( -1 );
        }
        else{
        	int index = dropInfo.getIndex();
        	if( index < station.getDockableCount() && station.getDockable( index ) == dropInfo.getDockable()){
        		dropInfo = null;
        		span.tease( -1 );
        	}
        	else if( index-1 >= 0 && index-1 < station.getDockableCount() && station.getDockable( index-1 ) == dropInfo.getDockable()){
        		dropInfo = null;
        		span.tease( -1 );
        	}
        	else{
        		span.tease( dropInfo.getIndex() );
        	}
        }
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
    
    /**
     * Called by the owning {@link FlapDockStation} if the {@link DockController} changes.
     * @param controller the new controller, can be <code>null</code>
     */
    public void setProperties( DockController controller ){
    	span.setController( controller );
    }
    
    /**
     * Ensures that all titles of the title-map, which was given to the
     * constructor, are shown on this panel. This method works asynchronous.
     */
    public void resetTitles(){
    	if( !resetStarted ){
    		Runnable code = new Runnable() {
				public void run(){
					resetStarted = false;
			        getContentPane().removeAll();
			        for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
			            DockTitle title = station.getButton( i );
			            if( title != null ){
			                getContentPane().add( title.getComponent() );
			            }
			        }
			        span.reset();
			        revalidate();
				}
			};
    		
    		resetStarted = true;
    		if( EventQueue.isDispatchThread() ){
    			EventQueue.invokeLater( code );	
    		}
    		else{
    			code.run();
    		}
    	}
    }
    
    @Override
    protected void paintOverlay( Graphics g ) {
       if( dropInfo != null && dropInfo.getCombineTarget() == null ){
            int left = dropInfo.getIndex()-1;
            int right = left+1;
            
            DockTitle leftTitle = left < 0 ? null : station.getButton( left );
            DockTitle rightTitle = right >= station.getDockableCount() ? null : station.getButton( right );
            
            boolean horizontal = station.getDirection() == Direction.SOUTH || station.getDirection() == Direction.NORTH;
            
            int x1, y1, x2, y2;
            
            if( leftTitle == null && rightTitle == null ){
                if( horizontal ){
                    x1 = 0;
                    y1 = 0;
                    x2 = getWidth();
                    y2 = getHeight();
                }
                else{
                    x1 = 0;
                    y1 = 0;
                    x2 = getWidth();
                    y2 = getHeight();
                }
            }
            else if( leftTitle == null ){
                if( horizontal ){
                    x1 = 0;
                    y1 = 0;
                    x2 = rightTitle.getComponent().getX();
                    y2 = getHeight();
                }
                else{
                    x1 = 0;
                    y1 = 0;
                    x2 = getWidth();
                    y2 = rightTitle.getComponent().getY();
                }
            }
            else if( rightTitle == null ){
            	int last = getNumberOfButtons();
                if( horizontal ){
                    x1 = leftTitle.getComponent().getX() + leftTitle.getComponent().getWidth();
                    y1 = 0;
                    x2 = x1 + span.getGap( last );
                    y2 = getHeight();
                }
                else{
                    x1 = 0;
                    y1 = leftTitle.getComponent().getY() + leftTitle.getComponent().getHeight();
                    x2 = getWidth();
                    y2 = y1 + span.getGap( last );
                }
            }
            else{
                if( horizontal ){
                    x1 = leftTitle.getComponent().getX() + leftTitle.getComponent().getWidth();
                    y1 = 0;
                    x2 = rightTitle.getComponent().getX();
                    y2 = getHeight();
                }
                else{
                    x1 = 0;
                    y1 = leftTitle.getComponent().getY() + leftTitle.getComponent().getHeight();
                    x2 = getWidth();
                    y2 = rightTitle.getComponent().getY();
                }
            }
            
            x1++;
            y1++;
            x2--;
            y2--;
            
            if( x1 >= x2 || y1 >= y2 ){
            	if( horizontal ){
            		x1 = (x1 + x2) / 2;
            		x2 = x1;
            	}
            	else{
            		y1 = (y1 + y2) / 2;
            		y2 = y1;
            	}
            	
            	station.getPaint().drawInsertionLine( g, x1, y1, x2, y2 );
            }
            else{
            	station.getPaint().drawInsertion( g, new Rectangle( 0, 0, getWidth(), getHeight()), new Rectangle( x1, y1, x2-x1, y2-y1 ));
            }
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
                DockTitle title = station.getButton( i );
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
                DockTitle title = station.getButton( i );
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
        int count = station.getDockableCount();

        if( station.getDirection() == Direction.NORTH || station.getDirection() == Direction.SOUTH ){
        	height = span.getTeasing();
            for( int i = 0; i<count; i++ ){
                DockTitle title = station.getButton( i );
                if( title != null ){
                    Dimension size = title.getComponent().getPreferredSize();
                    width += size.width;
                    height = Math.max( height, size.height );
                }
                width += span.getGap( i );
            }
            if( count > 0 ){
            	width += span.getGap( count );
            }
        }
        else{
        	width = span.getTeasing();
            for( int i = 0; i<count; i++ ){
                DockTitle title = station.getButton( i );
                if( title != null ){
                    Dimension size = title.getComponent().getPreferredSize();
                    height += size.height;
                    width = Math.max( width, size.width );
                }
                height += span.getGap( i );
            }
            if( count > 0 ){
            	height += span.getGap( count );
            }
        }
        
        Dimension empty = station.getMinimumSize();
        return new Dimension( Math.max( empty.width, width ), Math.max( empty.height, height ));
    }
    
    /**
     * Gets the number of buttons that are actually shown.
     * @return the number of buttons
     */
    public int getNumberOfButtons(){
    	return getContentPane().getComponentCount();
    }
    
    /**
     * Called if the {@link Span}s used by this {@link ButtonPane} changed their size.
     */
    public void spanResized(){
    	getContentPane().revalidate();
    	revalidate();
    }
    
    /**
     * The direct parent of the buttons-titles. 
     * @author Benjamin Sigg
     */
    private class Content extends ConfiguredBackgroundPanel{
        /**
         * Creates a new panel
         */
        public Content(){
        	super( null, Transparency.SOLID );
        }
        
        @Override
        public void setTransparency( Transparency transparency ){
        	super.setTransparency( transparency );
        	ButtonPane.this.setSolid( transparency == Transparency.SOLID );
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
                    sum += span.getGap( i );
                }
                if( count > 0 ){
                	sum += span.getGap( count );
                }
                
                if( station.isSmallButtons() && preferredHeight < height ){
                    int delta = height - preferredHeight;
                    y += delta/2;
                    height = preferredHeight;
                }
                
                if( sum > width ){
                    double ratio = ((double)width) / sum;
                    for( int i = 0; i < count; i++ ){
                    	x += span.getGap( i );
                        int temp = (int)(widths[i]*ratio);
                        getComponent( i ).setBounds( x, y, temp, height );
                        x += temp;
                    }
                }
                else{
                    for( int i = 0; i < count; i++ ){
                    	x += span.getGap( i );
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
                    sum += span.getGap( i );
                }
                if( count > 0 ){
                	sum += span.getGap( count );
                }
                
                if( station.isSmallButtons() && preferredWidth < width ){
                    int delta = width - preferredWidth;
                    x += delta/2;
                    width = preferredWidth;
                }                
                
                if( sum > height ){
                    double ratio = ((double)height) / sum;
                    for( int i = 0; i < count; i++ ){
                    	y += span.getGap( i );
                        int temp = (int)(heights[i]*ratio);
                        getComponent( i ).setBounds( x, y, width, temp );
                        y += temp;
                    }
                }
                else{
                    for( int i = 0; i < count; i++ ){
                    	y += span.getGap( i );
                        getComponent( i ).setBounds( x, y, width, heights[i] );
                        y += heights[i];
                    }
                }
            }
            
            repaint();
        }
    }
}
