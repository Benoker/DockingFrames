/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.title;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.UIManager;

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;

/**
 * The default-title that is used most times in the framework. This title
 * shows an icon, a text, some small buttons and a gradient as background.
 * @author Benjamin Sigg
 */
public class DefaultDockTitle extends AbstractDockTitle {
    /**
     * A factory for the {@link DefaultDockTitle}.
     */
    public static final DockTitleFactory FACTORY = new DockTitleFactory(){
        public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
            return new DefaultDockTitle( dockable, version );
        }

        public <D extends Dockable & DockStation> DockTitle createStationTitle( 
                D dockable, DockTitleVersion version ) {
            return new DefaultDockTitle( dockable, version );
        }
    };
    
    /** The left color of the gradient if the title is active */
    private Color activeLeftColor;// = UIManager.getColor( "MenuItem.selectionBackground");// SystemColor.activeCaption;
    /** The left color of the gradient if the title is not active */
    private Color inactiveLeftColor;// = SystemColor.inactiveCaption;
    
    /** The right color of the gradient if the title is active */
    private Color activeRightColor;// = inactiveLeftColor;
    /** The right color of the gradient if the title is not active */
    private Color inactiveRightColor;// = inactiveLeftColor;
    
    /** The color of the text if the title is active */
    private Color activeTextColor;// = SystemColor.activeCaptionText;
    /** The color of the text if the title is not active */
    private Color inactiveTextColor;// = SystemColor.inactiveCaptionText;
    
    /** The gradient used to paint this title */
    private GradientPaint gradient;
    
    /**
     * Creates a new title
     * @param dockable the owner of this title
     * @param origin the version which was used to create this title
     */
    public DefaultDockTitle( Dockable dockable, DockTitleVersion origin ){
        super( dockable, origin );
        setActive( false );
    }
    
    @Override
    public void updateUI() {
    	super.updateUI();
    	
        activeLeftColor = UIManager.getColor( "MenuItem.selectionBackground");
        inactiveLeftColor = UIManager.getColor( "MenuItem.background");
        
        activeRightColor = UIManager.getColor( "MenuItem.selectionBackground");
        inactiveRightColor = UIManager.getColor( "MenuItem.background");
        
        activeTextColor = UIManager.getColor( "MenuItem.selectionForeground");
        inactiveTextColor = UIManager.getColor( "MenuItem.foreground");
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
                gradient = getGradient( activeLeftColor, activeRightColor, component );
            }
            else{
                gradient = getGradient( inactiveLeftColor, inactiveRightColor, component );
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
        return activeLeftColor;
    }

    /**
     * Sets the color that is used on the left side if this
     * title is active.
     * @param activeLeftColor the color
     */
    public void setActiveLeftColor( Color activeLeftColor ) {
        this.activeLeftColor = activeLeftColor;
        updateColors();
    }
    
    /**
     * Gets the color that is used on the right side in the
     * gradient of this title.
     * @return the color
     */
    public Color getActiveRightColor() {
        return activeRightColor;
    }
    
    /**
     * Sets the color which is used on the right side in the
     * gradient of this title.
     * @param activeRightColor the color
     */
    public void setActiveRightColor( Color activeRightColor ) {
        this.activeRightColor = activeRightColor;
        updateColors();
    }

    /**
     * Gets the color that is used for text if this title is active.
     * @return the color
     */
    public Color getActiveTextColor() {
        return activeTextColor;
    }

    /**
     * Sets the color which is used to paint the text if this title
     * is active.
     * @param activeTextColor the color
     */
    public void setActiveTextColor( Color activeTextColor ) {
        this.activeTextColor = activeTextColor;
        updateColors();
    }

    /**
     * Gets the color which is used on the left side of the
     * gradient if this title is not active.
     * @return the color
     */
    public Color getInactiveLeftColor() {
        return inactiveLeftColor;
    }

    /**
     * Sets the color which will be used on the left side of
     * the gradient if this title is not active.
     * @param inactiveLeftColor the color
     */
    public void setInactiveLeftColor( Color inactiveLeftColor ) {
        this.inactiveLeftColor = inactiveLeftColor;
        updateColors();
    }
    
    /**
     * Gets the color which is used on the right side of the
     * gradient if this title is not active.
     * @return the color on the right side
     */
    public Color getInactiveRightColor() {
        return inactiveRightColor;
    }
    
    /**
     * Sets the color of the right side of the gradient. The color
     * will only be used if this title is not active.
     * @param inactiveRightColor the color
     */
    public void setInactiveRightColor( Color inactiveRightColor ) {
        this.inactiveRightColor = inactiveRightColor;
        updateColors();
    }

    /**
     * Gets the color of the text. This color is used if this title is not active.
     * @return the color
     */
    public Color getInactiveTextColor() {
        return inactiveTextColor;
    }

    /**
     * Sets the color of the text. The color will only be used if this title
     * is not active.
     * @param inactiveTextColor the color
     */
    public void setInactiveTextColor( Color inactiveTextColor ) {
        this.inactiveTextColor = inactiveTextColor;
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
                setForeground( activeTextColor );
            }
        }
        else{
            if( inactiveTextColor != null ){
                setForeground( inactiveTextColor );
            }
        }
        
        repaint();
    }
}
