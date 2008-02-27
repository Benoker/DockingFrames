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

package bibliothek.gui.dock.themes.basic;

import java.awt.*;

import javax.swing.JComponent;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * The default-title that is used most times in the framework. This title
 * shows an icon, a text, some small buttons and a gradient as background.
 * @author Benjamin Sigg
 */
@ColorCodes({ "title.active.left", "title.inactive.left", 
    "title.active.right", "title.inactive.right", 
    "title.active.text", "title.inactive.text" })
public class BasicDockTitle extends AbstractDockTitle {
    /**
     * A factory for the {@link BasicDockTitle}.
     */
    public static final DockTitleFactory FACTORY = new DockTitleFactory(){
        public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
            return new BasicDockTitle( dockable, version );
        }

        public <D extends Dockable & DockStation> DockTitle createStationTitle( 
                D dockable, DockTitleVersion version ) {
            return new BasicDockTitle( dockable, version );
        }
    };
    
    /** The left color of the gradient if the title is active */
    private TitleColor activeLeftColor = new BasicTitleColor( "title.active.left", Color.BLACK );
    /** The left color of the gradient if the title is not active */
    private TitleColor inactiveLeftColor = new BasicTitleColor( "title.inactive.left", Color.DARK_GRAY );
    
    /** The right color of the gradient if the title is active */
    private TitleColor activeRightColor = new BasicTitleColor( "title.active.right", Color.DARK_GRAY );
    /** The right color of the gradient if the title is not active */
    private TitleColor inactiveRightColor = new BasicTitleColor( "title.inactive.right", Color.LIGHT_GRAY );
    
    /** The color of the text if the title is active */
    private TitleColor activeTextColor = new BasicTitleColor( "title.active.text", Color.WHITE );
    /** The color of the text if the title is not active */
    private TitleColor inactiveTextColor = new BasicTitleColor( "title.inactive.text", Color.BLACK );
    
    /** The gradient used to paint this title */
    private GradientPaint gradient;
    
    /**
     * Creates a new title
     * @param dockable the owner of this title
     * @param origin the version which was used to create this title
     */
    public BasicDockTitle( Dockable dockable, DockTitleVersion origin ){
        super( dockable, origin );
        setActive( false );
        
        addColor( activeLeftColor );
        addColor( inactiveLeftColor );
        addColor( activeRightColor );
        addColor( inactiveRightColor );
        addColor( activeTextColor );
        addColor( inactiveTextColor );
    }

    @Override
    @Deprecated
    public void reshape( int x, int y, int w, int h ){
        super.reshape( x, y, w, h );
        gradient = null;
    }
    
    @Override
    public void validate() {
        gradient = null;
        super.validate();
    }
    
    @Override
    public void setOrientation( Orientation orientation ) {
        gradient = null;
        super.setOrientation(orientation);
    }
    
    @Override
    protected void paintBackground( Graphics g, JComponent component ) {
        Graphics2D g2 = (Graphics2D)g;

        if( gradient == null ){
            if ( isActive() ){
                gradient = getGradient( activeLeftColor.color(), activeRightColor.color(), component );
            }
            else{
                gradient = getGradient( inactiveLeftColor.color(), inactiveRightColor.color(), component );
            }
        }
        
        g2.setPaint( gradient );
        g.fillRect( 0, 0, component.getWidth(), component.getHeight() );
    }
    
    /**
     * Gets the gradient which is used to fill the background of <code>component</code>.
     * @param left the first color of the gradient
     * @param right the second color of the gradient
     * @param component the component on which the gradient will be used
     * @return the new gradient
     */
    protected GradientPaint getGradient( Color left, Color right, Component component ){
        GradientPaint gradient;
        
        if( getOrientation().isHorizontal() ){
            float h = component.getHeight() / 2.0f;
            gradient = new GradientPaint( 0, h, 
                left, component.getWidth(), h,
                right, false );
        }
        else{
            float w = component.getWidth() / 2.0f;
            gradient = new GradientPaint( w, 0, 
                    left, w, component.getHeight(),
                    right, false );                    
        }
        
        return gradient;
    }
    
    /**
     * Gets the color that is used on the left side if this title
     * is active.
     * @return the color
     * @see #setActiveLeftColor(Color)
     */
    public Color getActiveLeftColor() {
        return activeLeftColor.color();
    }

    /**
     * Sets the color that is used on the left side if this
     * title is active.
     * @param activeLeftColor the color
     */
    public void setActiveLeftColor( Color activeLeftColor ) {
        this.activeLeftColor.setValue( activeLeftColor );
        updateColors();
    }
    
    /**
     * Gets the color that is used on the right side in the
     * gradient of this title.
     * @return the color
     */
    public Color getActiveRightColor() {
        return activeRightColor.color();
    }
    
    /**
     * Sets the color which is used on the right side in the
     * gradient of this title.
     * @param activeRightColor the color
     */
    public void setActiveRightColor( Color activeRightColor ) {
        this.activeRightColor.setValue( activeRightColor );
        updateColors();
    }

    /**
     * Gets the color that is used for text if this title is active.
     * @return the color
     */
    public Color getActiveTextColor() {
        return activeTextColor.color();
    }

    /**
     * Sets the color which is used to paint the text if this title
     * is active.
     * @param activeTextColor the color
     */
    public void setActiveTextColor( Color activeTextColor ) {
        this.activeTextColor.set( activeTextColor );
        updateColors();
    }

    /**
     * Gets the color which is used on the left side of the
     * gradient if this title is not active.
     * @return the color
     */
    public Color getInactiveLeftColor() {
        return inactiveLeftColor.color();
    }

    /**
     * Sets the color which will be used on the left side of
     * the gradient if this title is not active.
     * @param inactiveLeftColor the color
     */
    public void setInactiveLeftColor( Color inactiveLeftColor ) {
        this.inactiveLeftColor.setValue( inactiveLeftColor );
        updateColors();
    }
    
    /**
     * Gets the color which is used on the right side of the
     * gradient if this title is not active.
     * @return the color on the right side
     */
    public Color getInactiveRightColor() {
        return inactiveRightColor.color();
    }
    
    /**
     * Sets the color of the right side of the gradient. The color
     * will only be used if this title is not active.
     * @param inactiveRightColor the color
     */
    public void setInactiveRightColor( Color inactiveRightColor ) {
        this.inactiveRightColor.setValue( inactiveRightColor );
        updateColors();
    }

    /**
     * Gets the color of the text. This color is used if this title is not active.
     * @return the color
     */
    public Color getInactiveTextColor() {
        return inactiveTextColor.color();
    }

    /**
     * Sets the color of the text. The color will only be used if this title
     * is not active.
     * @param inactiveTextColor the color
     */
    public void setInactiveTextColor( Color inactiveTextColor ) {
        this.inactiveTextColor.setValue( inactiveTextColor );
        updateColors();
    }

    @Override
    public void setActive( boolean active ) {
        super.setActive( active );
        updateColors();
    }
    
    /**
     * Invoked after a color has changed. This method ensures that the
     * gradient is recreated.
     */
    protected void updateColors(){
        gradient = null;
        
        if( isActive() ){
            if( activeTextColor != null ){
                setForeground( activeTextColor.color() );
            }
        }
        else{
            if( inactiveTextColor != null ){
                setForeground( inactiveTextColor.color() );
            }
        }
        
        repaint();
    }
    
    /**
     * A implementation of {@link TitleColor} that calls <code>repaint</code>
     * when the color changes.
     * @author Benjamin Sigg
     */
    private class BasicTitleColor extends TitleColor{
        /**
         * Creates a new color
         * @param id the id of the color
         * @param backup a backup color
         */
        public BasicTitleColor( String id, Color backup ){
            super( id, TitleColor.class, BasicDockTitle.this, backup );
        }
        
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            gradient = null;
            repaint();
        }
    }
}
