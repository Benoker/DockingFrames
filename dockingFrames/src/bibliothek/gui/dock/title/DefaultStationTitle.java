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

package bibliothek.gui.dock.title;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.util.Colors;

/**
 * This title is used for {@link DockStation DockStations} which are also
 * {@link Dockable Dockables}
 * @author Benjamin Sigg
 *
 */
public class DefaultStationTitle extends AbstractDockTitle {
    /** The minimal preferred width and height of this title */
    private int preferredDimension = 20;
    
    /** The background if the title is selected */
    private Color activeColor; // = SystemColor.activeCaption;
    /** The foreground if the title is selected */
    private Color activeTextColor; // = SystemColor.activeCaptionText;
    
    /** The background if the title is not selected */
    private Color inactiveColor; // = SystemColor.inactiveCaption;
    /** The foreground if the title is not selected */
    private Color inactiveTextColor; // = SystemColor.inactiveCaptionText;
    
    /**
     * Creates a new instance
     * @param dockable the owner of this title
     * @param origin the version which was used to create this title
     */
    public DefaultStationTitle( Dockable dockable, DockTitleVersion origin ) {
        super(dockable, origin);
        setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ));
        setActive( false );
    }
    
    @Override
    public void updateUI() {
    	super.updateUI();
    	
        activeColor = UIManager.getColor( "MenuItem.selectionBackground");
        activeTextColor = UIManager.getColor( "MenuItem.selectionForeground");
        
        inactiveColor = UIManager.getColor( "MenuItem.background");
        inactiveTextColor = UIManager.getColor( "MenuItem.foreground");
    }

    @Override
    protected void paintBackground( Graphics g, JComponent component ) {
    	Color background = component.getBackground();
    
    	Insets insets = component.getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = component.getWidth() - insets.left - insets.right;
        int height = component.getHeight() - insets.top - insets.bottom;
        
    	g.setColor( background );
        g.fillRect( 0, 0, getWidth(), getHeight() );
        
        Color bright = Colors.brighter( background, 0.1 );
        Color dark = Colors.darker( background, 0.1 );
        
        if( width > height ){
            if( height >= 6 ){
                for( int i = 0; i < 3; i++ ){
                   drawLineHorizontal( g, x, height * (i+1)/4, width, bright, dark );
                }
            }
            else if( height >= 4 ){
                drawLineHorizontal( g, x, y, width, bright, dark );
                drawLineHorizontal( g, x, y+height-2, width, bright, dark );
            }
            else if( height >= 2 ){
                drawLineHorizontal( g, x, y, width, bright, dark );
            }
        }
        else{
            if( width >= 6 ){
                for( int i = 0; i < 3; i++ ){
                   drawLineVertical( g, width * (i+1)/4, y, height, bright, dark );
                }
            }
            else if( width >= 4 ){
                drawLineVertical( g, x, y, height, bright, dark );
                drawLineVertical( g, x+width-2, y, height, bright, dark );
            }
            else if( width >= 2 ){
                drawLineVertical( g, x, y, height, bright, dark );
            }            
        }
    }
    
    /**
     * Gets the minimum of the preferred width and height.
     * @return the minimum
     * @see #setPreferredDimension(int)
     */
    public int getPreferredDimension() {
        return preferredDimension;
    }
    
    /**
     * Sets the minimum of the preferred size. The width and the height
     * of the result of <code>getPreferredSize</code> will always be equal
     * or greater than <code>preferredDimension</code>.
     * @param preferredDimension the smallest preferred dimension
     */
    public void setPreferredDimension( int preferredDimension ) {
        this.preferredDimension = preferredDimension;
    }
    
    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        updateColors();
    }

    /**
     * Changes the background and the foreground color of this title. If the
     * title is {@link #isActive() active}, the foreground is set to
     * {@link #getActiveTextColor() activeTextColor} and the background is
     * set to {@link #getActiveColor() activeColor}. Otherwise the
     * foreground is {@link #getInactiveTextColor() inactiveTextColor} and
     * the background is {@link #getInactiveColor() inacticeColor}. 
     */
    protected void updateColors(){
        if( isActive() ){
            if( activeTextColor != null && activeColor != null ){
                setBackground( activeColor );
                setForeground( activeTextColor );
            }
        }
        else{
            if( inactiveColor != null && inactiveTextColor != null ){
                setBackground( inactiveColor );
                setForeground( inactiveTextColor );
            }
        }
        
        repaint();
    }
    
    /**
     * Gets the background-color which is used if this title is selected.
     * @return the background
     */
    public Color getActiveColor() {
        return activeColor;
    }
    /**
     * Gets the background-color which is used if this title is not selected. 
     * @return the background
     */
    public Color getInactiveColor() {
        return inactiveColor;
    }
    
    /**
     * Gets the foreground-color which is used if this title is selected.
     * @return the foreground
     */
    public Color getActiveTextColor() {
        return activeTextColor;
    }
    
    /**
     * Gets the foreground-color which is used if this title is not selected.
     * @return the foreground
     */
    public Color getInactiveTextColor() {
        return inactiveTextColor;
    }
    
    /**
     * Sets the background-color which is used if this title is selected.
     * @param activeColor the background
     */
    public void setActiveColor( Color activeColor ) {
        this.activeColor = activeColor;
        updateColors();
    }
    
    /**
     * Sets the foreground-color which is used if this title is selected.
     * @param activeTextColor the foreground
     */
    public void setActiveTextColor( Color activeTextColor ) {
        this.activeTextColor = activeTextColor;
        updateColors();
    }
    
    /**
     * Sets the background-color which is used if this title is not selected.
     * @param inactiveColor the background
     */
    public void setInactiveColor( Color inactiveColor ) {
        this.inactiveColor = inactiveColor;
        updateColors();
    }
    
    /**
     * Sets the foreground-color which is used if this title is not selected.
     * @param inactiveTextColor the background
     */
    public void setInactiveTextColor( Color inactiveTextColor ) {
        this.inactiveTextColor = inactiveTextColor;
        updateColors();
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension( 
                Math.max( preferredDimension, size.width ),
                Math.max( preferredDimension, size.height ));
    }
    
    /**
     * Draws a horizontal line beginning at <code>x, y</code>.
     * @param g the graphics context used to paint
     * @param x the x-coordinate of the left point
     * @param y the y-coordinate of the line
     * @param width the length of the line
     * @param up the color used to draw the upper half
     * @param down the color used to draw the lower half
     */
    private void drawLineHorizontal( Graphics g, int x, int y, int width, Color up, Color down ){
        g.setColor( up );
        g.drawLine( x+2, y, x+width-4, y );
        
        g.setColor( down );
        g.drawLine( x+2, y+1, x+width-4, y+1 );
    }
    
    /**
     * Draws a vertical line beginning at <code>x, y</code>.
     * @param g the graphics context used to paint
     * @param x the x-coordinate of the line
     * @param y the y-coordinate of the top point
     * @param height the length of the line
     * @param up the color used to draw the left half
     * @param down the color used to draw the right half
     */
    private void drawLineVertical( Graphics g, int x, int y, int height, Color up, Color down ){
        g.setColor( up );
        g.drawLine( x, y+2, x, y+height-4 );
        
        g.setColor( down );
        g.drawLine( x+1, y+2, x+1, y+height-4 );
    }
}
