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

package bibliothek.gui.dock.themes.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Colors;

/**
 * This title is used for {@link DockStation DockStations} which are also
 * {@link Dockable Dockables}
 * @author Benjamin Sigg
 *
 */
@ColorCodes( {"title.station.active", "title.station.active.text",
    "title.station.inactive", "title.station.inactive.text",
    "title.station.disabled" })
public class BasicStationTitle extends AbstractDockTitle {
    /** The minimal preferred width and height of this title */
    private int preferredDimension = 20;
    
    /** The background if the title is selected */
    private TitleColor activeColor = new BasicStationTitleColor( "title.station.active", Color.WHITE );
    /** The foreground if the title is selected */
    private TitleColor activeTextColor = new BasicStationTitleColor( "title.station.active.text", Color.BLACK );
    
    /** The background if the title is not selected */
    private TitleColor inactiveColor = new BasicStationTitleColor( "title.station.inactive", Color.WHITE );
    /** The foreground if the title is not selected */
    private TitleColor inactiveTextColor = new BasicStationTitleColor( "title.station.inactive.text", Color.DARK_GRAY );
    
    /** The background if the title disabled */
    private TitleColor disabledColor = new BasicStationTitleColor( "title.station.disabled", Color.WHITE );
    
    /**
     * Creates a new instance
     * @param dockable the owner of this title
     * @param origin the version which was used to create this title
     */
    public BasicStationTitle( Dockable dockable, DockTitleVersion origin ) {
        super(dockable, origin);
        setBorder( ThemeManager.BORDER_MODIFIER + ".title.station.basic", BorderFactory.createBevelBorder( BevelBorder.RAISED ));
        setActive( false );
        
        addColor( activeColor );
        addColor( activeTextColor );
        addColor( inactiveColor );
        addColor( inactiveTextColor );
        addColor( disabledColor );
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
    
    @Override
    protected void setDisabled( boolean disabled ){
    	super.setDisabled( disabled );
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
    	if( isDisabled() ){
    		if( inactiveTextColor != null && disabledColor != null ){
    			setBackground( disabledColor.color() );
    			setForeground( inactiveTextColor.color() );
    		}
    	}
    	else if( isActive() ){
            if( activeTextColor != null && activeColor != null ){
                setBackground( activeColor.color() );
                setForeground( activeTextColor.color() );
            }
        }
        else{
            if( inactiveColor != null && inactiveTextColor != null ){
                setBackground( inactiveColor.color() );
                setForeground( inactiveTextColor.color() );
            }
        }
        
        repaint();
    }
    
    /**
     * Gets the background-color which is used if this title is selected.
     * @return the background
     */
    public Color getActiveColor() {
        return activeColor.value();
    }
    /**
     * Gets the background-color which is used if this title is not selected. 
     * @return the background
     */
    public Color getInactiveColor() {
        return inactiveColor.value();
    }
    
    /**
     * Gets the background-color which is used if this title is disabled.
     * @return the background
     */
    public Color getDisabledColor(){
    	return disabledColor.value();
    }
    
    /**
     * Gets the foreground-color which is used if this title is selected.
     * @return the foreground
     */
    public Color getActiveTextColor() {
        return activeTextColor.value();
    }
    
    /**
     * Gets the foreground-color which is used if this title is not selected.
     * @return the foreground
     */
    public Color getInactiveTextColor() {
        return inactiveTextColor.value();
    }
    
    /**
     * Sets the background-color which is used if this title is selected.
     * @param activeColor the background
     */
    public void setActiveColor( Color activeColor ) {
        this.activeColor.setValue( activeColor );
        updateColors();
    }
    
    /**
     * Sets the foreground-color which is used if this title is selected.
     * @param activeTextColor the foreground
     */
    public void setActiveTextColor( Color activeTextColor ) {
        this.activeTextColor.setValue( activeTextColor );
        updateColors();
    }
    
    /**
     * Sets the background-color which is used if this title is not selected.
     * @param inactiveColor the background
     */
    public void setInactiveColor( Color inactiveColor ) {
        this.inactiveColor.setValue( inactiveColor );
        updateColors();
    }

    /**
     * Sets the background-color which is used if this title is disabled. 
     * @param disabledColor the background
     */
    public void setDisabledColor( Color disabledColor ){
    	this.disabledColor.setValue( disabledColor );
    	updateColors();
    }
    
    /**
     * Sets the foreground-color which is used if this title is not selected.
     * @param inactiveTextColor the background
     */
    public void setInactiveTextColor( Color inactiveTextColor ) {
        this.inactiveTextColor.setValue( inactiveTextColor );
        updateColors();
    }
    
    /**
     * Gets the {@link TitleColor} which represents the background of an active title.
     * @return the active background
     */
    public TitleColor getActiveTitleColor(){
        return activeColor;
    }
    
    /**
     * Gets the {@link TitleColor} which represents the background of a disabled title.
     * @return the disabled background
     */
    public TitleColor getDisabledTitleColor(){
    	return disabledColor;
    }
    
    /**
     * Gets the {@link TitleColor} which represents the foreground of an active title.
     * @return the active foreground
     */    
    public TitleColor getActiveTextTitleColor(){
        return activeTextColor;
    }
    
    /**
     * Gets the {@link TitleColor} which represents the background of an inactive title.
     * @return the inactive background
     */
    public TitleColor getInactiveTitleColor(){
        return inactiveColor;
    }
    
    /**
     * Gets the {@link TitleColor} which represents the foreground of an inactive title.
     * @return the inactive foreground
     */
    public TitleColor getInactiveTextTitleColor(){
        return inactiveTextColor;
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
    
    /**
     * A {@link DockColor} representing a color of {@link BasicStationTitle}.
     * @author Benjamin Sigg
     */
    private class BasicStationTitleColor extends TitleColor{
        /**
         * Creates a new color
         * @param id the unique identifier of the color
         * @param backup the default value
         */
        public BasicStationTitleColor( String id, Color backup ){
            super( id, BasicStationTitle.this, backup );
        }
        
        @Override
        protected void changed( Color oldValue, Color newValue ) {
            updateColors();
        }
    }
}
