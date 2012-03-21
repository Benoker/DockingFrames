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
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.util.MouseOverListener;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.util.Path;

/**
 * A title that has the ability to paint a round rect as background. It also can
 * apply animations when changing the state.
 * @see BubbleColorAnimation
 * @author Benjamin Sigg
 */
public abstract class AbstractBubbleDockTitle extends AbstractDockTitle{
    /** key for the foreground color used by the animation */
    protected static final String ANIMATION_KEY_TEXT = "text";
    /** key for the background color at the top used by the animation */
    protected static final String ANIMATION_KEY_BACKGROUND_TOP = "top";
    /** key for the background color at the bottom used by the animation */
    protected static final String ANIMATION_KEY_BACKGROUND_BOTTOM = "bottom";

    /** An animation which can change a set of colors smoothly */
    private BubbleColorAnimation animation;

    /** Tells whether the mouse is over this title or not */
    private MouseOverListener mouseover;
    /** The size of the round edges */
    private int arc = 16;

    /** the colors used on this title */
    private List<BubbleTitleColor> colors = new ArrayList<BubbleTitleColor>();

    /**
     * Creates a new title.
     * @param dockable the {@link Dockable} for which this title is shown
     * @param origin the creator of this title
     */
    public AbstractBubbleDockTitle( Dockable dockable, DockTitleVersion origin ) {
        this( dockable, origin, true );
    }

    /**
     * Creates a new title.
     * @param dockable the {@link Dockable} for which this title is shown
     * @param origin the creator of this title
     * @param showMiniButtons whether this title should show the {@link bibliothek.gui.dock.action.DockAction actions} or not
     */
    public AbstractBubbleDockTitle( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
        init( dockable, origin, showMiniButtons );
    }

    /**
     * A constructor that does not do anything, subclasses should later call
     * {@link #init(Dockable, DockTitleVersion, boolean)}.
     */
    protected AbstractBubbleDockTitle(){

    }

    /**
     * Initializes this title, this method should be called only once.
     * @param dockable the {@link Dockable} for which this title is shown
     * @param origin the creator of this title
     * @param showMiniButtons whether this title should show the {@link bibliothek.gui.dock.action.DockAction actions} or not
     */
    @Override
    protected void init( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
        super.init( dockable, origin, showMiniButtons );
        setOpaque( false );
        initAnimation();

        mouseover = new MouseOverListener( getComponent() ){
            @Override
            protected void changed() {
                updateAnimation();
            }
        };
    }

    /**
     * Tells whether the mouse is currently over this title or not.
     * @return <code>true</code> if the mouse is within the borders of this title
     */
    public boolean isMouseOver(){
        return !isDisabled() && mouseover == null ? false : mouseover.isMouseOver();
    }

    /**
     * Registers a {@link TitleColor} width identifier <code>id</code> at this
     * title.
     * @param id the id of the color
     * @param kind what kind of color it is (should be derived from {@link TitleColor#KIND_TITLE_COLOR}
     * @param backup the standard color if nothing else is set
     */
    protected void addColor( String id, Path kind, Color backup ){
        BubbleTitleColor color = new BubbleTitleColor( id, kind, backup );
        colors.add( color );
        addColor( color );
    }

    /**
     * Sets up the animation such that it can be started at any time.
     */
    private void initAnimation(){
    	setTransparency( Transparency.DEFAULT );
    	
        animation = new BubbleColorAnimation();

        updateAnimation();

        animation.addTask( new Runnable(){
            public void run() {
                pulse();
            }
        });

        setForeground( animation.getColor( "text" ));
    }

    @Override
    public void bind() {
        super.bind();
        animation.kick();
    }

    @Override
    public void setActive( boolean active ) {
        if( isActive() != active ){
            super.setActive( active );
            updateAnimation();
        }
    }
    
    @Override
    protected void setDisabled( boolean disabled ){
    	if( isDisabled() != disabled ){
    		super.setDisabled( disabled );
    		updateAnimation();
    	}
    }

    /**
     * Called when the mouse entered or left this title, or when the active
     * state changed. This method has to update the animation, it should call
     * {@link #updateAnimation(String, String)} for all animation-keys using
     * the currently best fitting identifiers. Subclasses might want to call this
     * method when some additional states changed which imply a change of the
     * look of this title.
     */
    protected abstract void updateAnimation();

    /**
     * Starts an animation for changing the color of <code>animationKey</code>
     * to <code>colorId</code>.
     * @param animationKey One of {@link #ANIMATION_KEY_TEXT}, {@link #ANIMATION_KEY_BACKGROUND_TOP},
     * {@link #ANIMATION_KEY_BACKGROUND_BOTTOM} or if this subclasses has its
     * own painting algorithm some other keys can be used.
     * @param colorId One of the identifiers used on {@link #addColor(String, Path, Color)}
     */
    protected void updateAnimation( String animationKey, String colorId ){
        for( BubbleTitleColor color : colors ){
            if( colorId.equals( color.getId() )){
                animation.putColor( animationKey, color.color() );
                break;
            }
        }
    }

    /**
     * Gets a color for an animation that was stared with {@link #updateAnimation()}.
     * @param animationKey the key for the animation
     * @return the current color or <code>null</code> if not present
     */
    protected Color getColor( String animationKey ){
    	return animation.getColor( animationKey );
    }
    
    /**
     * Called every time when the colors of the animation have been changed.
     */
    protected void pulse(){
        setForeground( animation.getColor( ANIMATION_KEY_TEXT ));
        repaint();
    }

    @Override
    protected Insets getInnerInsets() {
        int edge = arc / 4;

        switch( getOrientation() ){
            case EAST_SIDED: return new Insets( edge, edge/2, edge, edge );
            case FREE_HORIZONTAL: return new Insets( edge, edge, edge, edge );
            case FREE_VERTICAL: return new Insets( edge, edge, edge, edge );
            case NORTH_SIDED: return new Insets( edge, edge, edge/2, edge );
            case SOUTH_SIDED: return new Insets( edge/2, edge, edge, edge );
            case WEST_SIDED: return new Insets( edge, edge, edge, edge/2 );
            default: return super.getInnerInsets();
        }
    }
    
    @Override
    protected void paintBackground( Graphics g, JComponent component ) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        doPaintBackground( g2, component );
        g2.dispose();
    }
    
    /**
     * Actually paints the background with a graphics context that has special settings.
     * @param g the graphics context to use
     * @param component the component that is painted
     */
    protected void doPaintBackground( Graphics g, JComponent component ){
    	Graphics2D g2 = (Graphics2D)g;
    	
        Insets insets = getInsets();
        int x = 0, y = 0;
        int w = component.getWidth();
        int h = component.getHeight();
        if( insets != null ){
            x = insets.left;
            y = insets.top;
            w -= insets.left + insets.right;
            h -= insets.top + insets.bottom;
        }

        // set color
        Color top = animation.getColor( ANIMATION_KEY_BACKGROUND_TOP );
        Color bottom = animation.getColor( ANIMATION_KEY_BACKGROUND_BOTTOM );

        if( top != null && bottom != null ){
            if( getOrientation().isHorizontal() )
                g2.setPaint( new GradientPaint( 0, 0, top, 0, h, bottom ));
            else
                g2.setPaint( new GradientPaint( 0, 0, top, w, 0, bottom ));

            // draw
            drawRoundRect( g2, x, y, w, h );
        }    	
    }
    
    @Override
    public void paintOverlay( Graphics g ){
    	 // draw horizon
        Graphics2D g2 = (Graphics2D)g.create();

        Insets insets = getInsets();
        int x = 0, y = 0;
        int w = getWidth();
        int h = getHeight();
        if( insets != null ){
            x = insets.left;
            y = insets.top;
            w -= insets.left + insets.right;
            h -= insets.top + insets.bottom;
        }

        Rectangle clip = g.getClipBounds();
        if( clip == null ){
            clip = new Rectangle( x, y, w, h );
        }

        // set clipping area and colors
        if( getOrientation().isHorizontal() ){
            g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), 0, h/2, Color.WHITE ));
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );

            g2.setClip( 0, 0, w, h/2 );
            g2.clipRect( clip.x, clip.y, clip.width, clip.height );
        }
        else{
            g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), w/2, 0, Color.WHITE ));
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );

            g2.setClip( 0, 0, w/2, h );
            g2.clipRect( clip.x, clip.y, clip.width, clip.height );
        }

        drawRoundRect( g2, x, y, w, h );
        g2.dispose();
    }

    /**
     * Draws a rectangle which has some round edges within the bounds
     * x,y,w,h.
     * @param g2 the graphics to paint with
     * @param x the x-coordinate of the bounds
     * @param y the y-coordinate of the bounds
     * @param w the width of the bounds
     * @param h the height of the bounds
     */
    private void drawRoundRect( Graphics2D g2, int x, int y, int w, int h ){
        switch( getOrientation() ){
            case FREE_HORIZONTAL:
            case FREE_VERTICAL:
                g2.fillRoundRect( x, y, w, h, arc, arc );
                break;
            case EAST_SIDED:
                g2.fillRoundRect( x-arc, y, w+arc, h, arc, arc );
                break;
            case NORTH_SIDED:
                g2.fillRoundRect( x, y, w, h+arc, arc, arc );
                break;
            case SOUTH_SIDED:
                g2.fillRoundRect( x, y-arc, w, h+arc, arc, arc );
                break;
            case WEST_SIDED:
                g2.fillRoundRect( x, y, w+arc, h, arc, arc );
                break;
        }
    }

    /**
     * A color used on a {@link BubbleDockTitle}.
     * @author Benjamin Sigg
     */
    private class BubbleTitleColor extends TitleColor{
        public BubbleTitleColor( String id, Path kind, Color backup ){
            super( id, kind, AbstractBubbleDockTitle.this, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateAnimation();
        }
    }
}
