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

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A <code>BubbleDockTitle</code> is a title which has two or four round edges.
 * The title can smoothly change its colors when touched by the mouse.
 * @author Benjamin Sigg
 */

@ColorCodes({ 
    "title.background.top.active.mouse",
    "title.background.top.active",
    "title.background.top.inactive.mouse",
    "title.background.top.inactive",
    
    "title.background.bottom.active.mouse",
    "title.background.bottom.active",
    "title.background.bottom.inactive.mouse",
    "title.background.bottom.inactive",
    
    "title.foreground.active.mouse",
    "title.foreground.active",
    "title.foreground.inactive.mouse",
    "title.foreground.inactive" })
public class BubbleDockTitle extends AbstractDockTitle {
	/** An animation which can change a set of colors smoothly */
	private BubbleColorAnimation animation;
    
	/** Tells whether the mouse is over this title or not */
    private boolean mouse = false;
    /** The size of the round edges */
    private int arc = 16;
    
    /** the set of colors used on this title */
    private BubbleTitleColor[] colors;
    
    /**
     * Creates a new title.
     * @param dockable the {@link Dockable} for which this title is shown
     * @param origin the creator of this title
     */
    public BubbleDockTitle( Dockable dockable, DockTitleVersion origin ) {
        this( dockable, origin, true );
    }
    
    /**
     * Creates a new title.
     * @param dockable the {@link Dockable} for which this title is shown
     * @param origin the creator of this title
     * @param showMiniButtons whether this title should show the {@link bibliothek.gui.dock.action.DockAction actions} or not
     */
    public BubbleDockTitle( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
        init( dockable, origin, showMiniButtons );
    }
    
    /**
     * A constructor that does not do anything, subclasses should later call
     * {@link #init(Dockable, DockTitleVersion, boolean)}.
     */
    protected BubbleDockTitle(){
        
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
    }
    
    /**
     * Sets up the animation such that it can be started at any time.
     */
    private void initAnimation(){
        animation = new BubbleColorAnimation();
        
        colors = new BubbleTitleColor[]{
                new BubbleTitleColor( "title.background.top.active.mouse", Color.RED ),
                new BubbleTitleColor( "title.background.top.active", Color.LIGHT_GRAY ),
                new BubbleTitleColor( "title.background.top.inactive.mouse", Color.BLUE ),
                new BubbleTitleColor( "title.background.top.inactive", Color.DARK_GRAY ),
        
                new BubbleTitleColor( "title.background.bottom.active.mouse", Color.LIGHT_GRAY ),
                new BubbleTitleColor( "title.background.bottom.active", Color.WHITE ),
                new BubbleTitleColor( "title.background.bottom.inactive.mouse", Color.DARK_GRAY ),
                new BubbleTitleColor( "title.background.bottom.inactive", Color.BLACK ),
        
                new BubbleTitleColor( "title.foreground.active.mouse", Color.BLACK ),
                new BubbleTitleColor( "title.foreground.active", Color.BLACK ),
                new BubbleTitleColor( "title.foreground.inactive.mouse", Color.WHITE ),
                new BubbleTitleColor( "title.foreground.inactive", Color.WHITE )
        };
        
        for( BubbleTitleColor color : colors )
            addColor( color );
        
        updateAnimation( false );
        
        animation.addTask( new Runnable(){
            public void run() {
                pulse();
            }
        });
        
        setForeground( animation.getColor( "text" ));
        
        addMouseInputListener( new MouseInputAdapter(){
            @Override
            public void mouseEntered( MouseEvent e ) {
                updateAnimation( true );
            }
            
            @Override
            public void mouseExited( MouseEvent e ) {
                updateAnimation( false );
            }
        });
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
            updateAnimation( mouse );
        }
    }
    
    /**
     * Updates the setting of the animation to reflect new properties of
     * this title.
     * @param mouse whether the mouse is pressed or not
     */
    protected void updateAnimation( boolean mouse ){
        this.mouse = mouse;
        
        String postfix = "";
        if( isActive() ){
            if( mouse )
                postfix = "active.mouse";
            else
                postfix = "active";
        }
        else{
            if( mouse )
                postfix = "inactive.mouse";
            else
                postfix = "inactive";            
        }
        
        String top = "title.background.top." + postfix;
        String bottom = "title.background.bottom." + postfix;
        String text = "title.foreground." + postfix;
        
        for( BubbleTitleColor color : colors ){
            if( top.equals( color.getId() ))
                animation.putColor( "top", color.value() );
            else if( bottom.equals( color.getId() ))
                animation.putColor( "bottom", color.value() );
            else if( text.equals( color.getId() ))
                animation.putColor( "text", color.value() );
        }
    }
    
    /**
     * Called every time when the colors of the animation have been changed.
     */
    protected void pulse(){
        setForeground( animation.getColor( "text" ));
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
    public void paint( Graphics g ) {
        super.paint( g );
        
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

    @Override
    protected void paintBackground( Graphics g, JComponent component ) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
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
        Color top = animation.getColor( "top" );
        Color bottom = animation.getColor( "bottom" );
        
        if( getOrientation().isHorizontal() )
            g2.setPaint( new GradientPaint( 0, 0, top, 0, h, bottom ));
        else
            g2.setPaint( new GradientPaint( 0, 0, top, w, 0, bottom ));
        
        // draw
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
        public BubbleTitleColor( String id, Color backup ){
            super( id, TitleColor.class, BubbleDockTitle.this, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateAnimation( mouse );
        }
    }
}
