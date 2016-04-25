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

package bibliothek.gui.dock.themes.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.util.Condition;

/**
 * The default-title that is used most times in the framework. This title
 * shows an icon, a text, some small buttons and a gradient as background.
 * @author Benjamin Sigg
 */
@ColorCodes({ "title.active.left", "title.inactive.left", "title.disabled.left", 
    "title.active.right", "title.inactive.right", "title.disabled.right", 
    "title.active.text", "title.inactive.text" })
public class BasicDockTitle extends AbstractDockTitle {
    /**
     * A factory for the {@link BasicDockTitle}.
     */
    public static final DockTitleFactory FACTORY = new DockTitleFactory(){
    	public void install( DockTitleRequest request ){
    		// ignore
    	}
    	
    	public void uninstall( DockTitleRequest request ){
    		// ignore
    	}
    	
    	public void request( DockTitleRequest request ){
    		request.answer( new BasicDockTitle( request.getTarget(), request.getVersion() ) );
    	}
    };
    
    /** The left color of the gradient if the title is active */
    private TitleColor activeLeftColor = new BasicTitleColor( "title.active.left", Color.BLACK );
    /** The left color of the gradient if the title is not active */
    private TitleColor inactiveLeftColor = new BasicTitleColor( "title.inactive.left", Color.DARK_GRAY );
    /** The left color of the gradient if the title is disabled */
    private TitleColor disabledLeftColor = new BasicTitleColor( "title.disabled.left", Color.LIGHT_GRAY );
    
    /** The right color of the gradient if the title is active */
    private TitleColor activeRightColor = new BasicTitleColor( "title.active.right", Color.DARK_GRAY );
    /** The right color of the gradient if the title is not active */
    private TitleColor inactiveRightColor = new BasicTitleColor( "title.inactive.right", Color.LIGHT_GRAY );
    /** The right color of the gradient if the title is disabled */
    private TitleColor disabledRightColor = new BasicTitleColor( "title.disabled.right", Color.LIGHT_GRAY );
    
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
        this( dockable, origin, true );
    }

    /**
     * Creates a new title
     * @param dockable the owner of this title
     * @param origin the version which was used to create this title
     * @param setDefaultConditionalFonts whether to set the default set of
     * conditional fonts for {@link DockFont#ID_TITLE_ACTIVE} and
     * {@link DockFont#ID_TITLE_INACTIVE}
     */
    protected BasicDockTitle( Dockable dockable, DockTitleVersion origin, boolean setDefaultConditionalFonts ){
        super( dockable, origin );
        setActive( false );
        
        addColor( activeLeftColor );
        addColor( inactiveLeftColor );
        addColor( disabledLeftColor );
        addColor( activeRightColor );
        addColor( inactiveRightColor );
        addColor( disabledRightColor );
        addColor( activeTextColor );
        addColor( inactiveTextColor );
        
        if( setDefaultConditionalFonts ){
            addConditionalFont( DockFont.ID_TITLE_ACTIVE, TitleFont.KIND_TITLE_FONT, new Condition(){
                public boolean getState() {
                    return isActive();
                }
            }, null );

            addConditionalFont( DockFont.ID_TITLE_INACTIVE, TitleFont.KIND_TITLE_FONT, new Condition(){
                public boolean getState() {
                    return !isActive();
                }
            }, null );
        }
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
        	if( isDisabled() ){
        		gradient = getGradient( disabledLeftColor.value(), disabledRightColor.value(), component );
        	}
        	else if ( isActive() ){
                gradient = getGradient( activeLeftColor.value(), activeRightColor.value(), component );
            }
            else{
                gradient = getGradient( inactiveLeftColor.value(), inactiveRightColor.value(), component );
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
     * is active. This method does the same as
     * <code>getActiveLeftTitleColor().color();</code>.
     * @return the color
     * @see #setActiveLeftColor(Color)
     * @see #getActiveLeftTitleColor()
     */
    public Color getActiveLeftColor() {
        return activeLeftColor.value();
    }
    
    /**
     * Gets the handle for the left active color.
     * @return the handle, can be used to change the color of this title
     */
    public TitleColor getActiveLeftTitleColor(){
        return activeLeftColor;
    }

    /**
     * Sets the color that is used on the left side if this
     * title is active. This method does the same as
     * <code>getActiveLeftTitleColor().setValue( activeLeftColor );</code>.
     * @param activeLeftColor the color
     * @see #getActiveLeftTitleColor()
     */
    public void setActiveLeftColor( Color activeLeftColor ) {
        this.activeLeftColor.setValue( activeLeftColor );
    }
    
    /**
     * Changes the identifier that is used for the active left color.
     * @param id the new identifier, not <code>null</code>
     */
    public void setActiveLeftColorId( String id ){ 
        activeLeftColor.setId( id );
    }
        
    /**
     * Gets the color that is used on the right side in the
     * gradient of this title. This method does the same
     * as <code>getActiveRightTitleColor().color();</code>.
     * @return the color
     */
    public Color getActiveRightColor() {
        return activeRightColor.value();
    }

    /**
     * Gets the handle for the left active color.
     * @return the handle, can be used to change the color of this title
     */
    public TitleColor getActiveRightTitleColor(){
        return activeRightColor;
    }
    
    /**
     * Sets the color which is used on the right side in the
     * gradient of this title. This method does the same as
     * <code>getActiveRightTitleColor().setValue( activeRightColor );</code>.
     * @param activeRightColor the color
     */
    public void setActiveRightColor( Color activeRightColor ) {
        this.activeRightColor.setValue( activeRightColor );
    }
    
    /**
     * Changes the identifier that is used for the active right color.
     * @param id the new identifier, not <code>null</code>
     */
    public void setActiveRightColorId( String id ){ 
        activeRightColor.setId( id );
    }

    /**
     * Gets the color that is used for text if this title is active. This
     * method does the same as <code>getActiveTextTitleColor().color();</code>.
     * @return the color
     */
    public Color getActiveTextColor() {
        return activeTextColor.value();
    }

    /**
     * Gets a handle for the foreground color of an active title.
     * @return the handle, can be used to change the color of this title
     */
    public TitleColor getActiveTextTitleColor(){
        return activeTextColor;
    }
    
    /**
     * Sets the color which is used to paint the text if this title
     * is active. This method does the same as 
     * <code>getActiveTextTitleColor().setValue( activeTextColor );</code>.
     * @param activeTextColor the color
     */
    public void setActiveTextColor( Color activeTextColor ) {
        this.activeTextColor.setValue( activeTextColor );
    }
    
    /**
     * Changes the identifier that is used for the active text color.
     * @param id the new identifier, not <code>null</code>
     */
    public void setActiveTextColorId( String id ){ 
        activeTextColor.setId( id );
    }
    
    /**
     * Gets the color that is used on the left side if this title is disabled. This method
     * does the same as <code>getDisabledLeftTitleColor().color();</code>.
     * @return the color
     * @see #setDisabledLeftColor(Color)
     * @see #getDisabledLeftTitleColor()
     */
    public Color getDisabledLeftColor(){
    	return disabledLeftColor.value();
    }
    
    /**
     * Gets the handle for the left disabled color.
     * @return the handle, can be used to change the color of this title
     */
    public TitleColor getDisabledLeftTitleColor(){
    	return disabledLeftColor;
    }

    /**
     * Sets the color that is used on the left side if this
     * title is disabled. This method does the same as
     * <code>getDisabledLeftTitleColor().setValue( activeLeftColor );</code>.
     * @param disabledLeftColor the color
     * @see #getDisabledLeftTitleColor()
     */
    public void setDisabledLeftColor( Color disabledLeftColor ){
		this.disabledLeftColor.setValue( disabledLeftColor );
	}
    
    /**
     * Changes the identifier that is used for the disabled left color.
     * @param id the new identifier, not <code>null</code>
     */
    public void setDisabledLeftColorId( String id ){
    	disabledLeftColor.setId( id );
    }
    
    /**
     * Gets the color that is used on the right side if this title is disabled. This method
     * does the same as <code>getDisabledRightTitleColor().color();</code>.
     * @return the color
     * @see #setDisabledRightColor(Color)
     * @see #getDisabledRightTitleColor()
     */
    public Color getDisabledRightColor(){
    	return disabledRightColor.value();
    }
    
    /**
     * Gets the handle for the right disabled color.
     * @return the handle, can be used to change the color of this title
     */
    public TitleColor getDisabledRightTitleColor(){
    	return disabledRightColor;
    }

    /**
     * Sets the color that is used on the right side if this
     * title is disabled. This method does the same as
     * <code>getDisabledRightTitleColor().setValue( activeLeftColor );</code>.
     * @param disabledRightColor the color
     * @see #getDisabledRightTitleColor()
     */
    public void setDisabledRightColor( Color disabledRightColor ){
		this.disabledRightColor.setValue( disabledRightColor );
	}
    
    /**
     * Changes the identifier that is used for the disabled right color.
     * @param id the new identifier, not <code>null</code>
     */
    public void setDisabledRightColorId( String id ){
    	disabledRightColor.setId( id );
    }

    /**
     * Gets the color which is used on the left side of the
     * gradient if this title is not active. This method does the
     * same as <code>getInactiveLeftTitleColor().color();</code>.
     * @return the color
     */
    public Color getInactiveLeftColor() {
        return inactiveLeftColor.value();
    }
    
    /**
     * Gets a handle for the left inactive color.
     * @return the handle, can be used to change the color of this title
     */
    public TitleColor getInactiveLeftTitleColor(){
        return inactiveLeftColor;
    }

    /**
     * Sets the color which will be used on the left side of
     * the gradient if this title is not active. This method does the
     * same as <code>getInactiveLeftTitleColor().setValue( inactiveLeftColor );</code>.
     * @param inactiveLeftColor the color
     */
    public void setInactiveLeftColor( Color inactiveLeftColor ) {
        this.inactiveLeftColor.setValue( inactiveLeftColor );
    }
    
    /**
     * Changes the identifier that is used for the inactive left color.
     * @param id the new identifier, not <code>null</code>
     */
    public void setInactiveLeftColorId( String id ){ 
        inactiveLeftColor.setId( id );
    }
    
    /**
     * Gets the color which is used on the right side of the
     * gradient if this title is not active. This method does
     * the same as <code>getInactiveRightTitleColor().color();</code>.
     * @return the color on the right side
     */
    public Color getInactiveRightColor() {
        return inactiveRightColor.value();
    }
    
    /**
     * Gets a handle for the inactive right background color.
     * @return a handle, can be used to change the colors of this title
     */
    public TitleColor getInactiveRightTitleColor(){
        return inactiveRightColor;
    }
    
    /**
     * Sets the color of the right side of the gradient. The color
     * will only be used if this title is not active. This method does
     * the same as <code>getInactiveRightTitleColor().setValue( inactiveRightColor );</code>.
     * @param inactiveRightColor the color
     */
    public void setInactiveRightColor( Color inactiveRightColor ) {
        this.inactiveRightColor.setValue( inactiveRightColor );
    }
    
    /**
     * Changes the identifier that is used for the inactive right color.
     * @param id the new identifier, not <code>null</code>
     */
    public void setInactiveRightColorId( String id ){ 
        activeRightColor.setId( id );
    }

    /**
     * Gets the color of the text. This color is used if this title is not active.
     * This method does the same as <code>getInactiveTextTitleColor().color();</code>.
     * @return the color
     */
    public Color getInactiveTextColor() {
        return inactiveTextColor.value();
    }
    
    /**
     * Gets a handle for the inactive foreground color.
     * @return a handle, can be used to change the color of this title.
     */
    public TitleColor getInactiveTextTitleColor(){
        return inactiveTextColor;
    }

    /**
     * Sets the color of the text. The color will only be used if this title
     * is not active. This method does the same as
     * <code>getInactiveTextTitleColor().setValue( inactiveTextColor );</code>.
     * @param inactiveTextColor the color
     */
    public void setInactiveTextColor( Color inactiveTextColor ) {
        this.inactiveTextColor.setValue( inactiveTextColor );
    }

    /**
     * Changes the identifier that is used for the inactive text color.
     * @param id the new identifier, not <code>null</code>
     */
    public void setInactiveTextColorId( String id ){ 
        inactiveTextColor.setId( id );
    }
    
    @Override
    public void setActive( boolean active ) {
        super.setActive( active );
        updateColors();
        updateFonts();
    }
    
    @Override
    protected void setDisabled( boolean disabled ){
    	super.setDisabled( disabled );
    	updateColors();
    	updateFonts();
    }
    
    /**
     * Invoked after a color has changed. This method ensures that the
     * gradient is recreated.
     */
    protected void updateColors(){
        gradient = null;
        
        if( isActive() ){
            if( activeTextColor != null ){
                setForeground( activeTextColor.value() );
            }
        }
        else{
            if( inactiveTextColor != null ){
                setForeground( inactiveTextColor.value() );
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
            super( id, BasicDockTitle.this, backup );
        }
        
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            gradient = null;
            updateColors();
        }
    }
}
