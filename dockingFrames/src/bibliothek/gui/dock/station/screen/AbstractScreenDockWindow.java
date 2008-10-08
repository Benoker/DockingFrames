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
package bibliothek.gui.dock.station.screen;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationPaint;

/**
 * The default implementation of {@link ScreenDockWindow} uses a {@link DockableDisplayer}
 * to show the {@link Dockable}. It can operate on any 
 * @author Benjamin Sigg
 */
public abstract class AbstractScreenDockWindow extends DisplayerScreenDockWindow{
    /** the component which represents the window */
    private Component window;

    /** the elements to display */
    private DockableDisplayer displayer;

    /** the parent of {@link #displayer}, used to paint */
    private OverpaintablePanel content;

    /** whether to paint combination marks or not */
    private boolean painting = false;

    /** the explicit set icon */
    private Icon titleIcon = null;
    
    /** the explicit set text */
    private String titleText = null;
    
    /** a listener added to the <code>Dockable</code> of this window, updates icon and title text */
    private DockableListener listener = new DockableAdapter(){
        @Override
        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            updateTitleIcon();
        }
        
        @Override
        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            updateTitleText();
        }
    };

    /**
     * Creates a new window. Subclasses must call {@link #init(Component, Container, boolean)}
     * when using this constructor.
     * @param station the owner of this window
     */
    protected AbstractScreenDockWindow( ScreenDockStation station ){
        super( station );
    }
    
    /**
     * Creates a new window. 
     * @param station the owner of this window
     * @param window the root component of this window
     * @param contentParent the container onto which the contents of this window will be put
     * @param resizeable whether this window should create its own resizing system or not
     * @see #init(Component, Container, boolean)
     */
    public AbstractScreenDockWindow( ScreenDockStation station, Component window, Container contentParent, boolean resizeable ){
        super( station );
        init( window, contentParent, resizeable );
    }

    /**
     * Initializes this window.
     * @param window the component which represents the window. This component
     * will be used when calling methods like {@link #setWindowBounds(Rectangle)}. It
     * is the root of this whole window.
     * @param contentParent the container which will be used as parent for the
     * contents of this window. This method will change the {@link LayoutManager}
     * and add a child to <code>contentParent</code>. This component can be
     * the same as <code>window</code>.
     * @param resizeable If <code>true</code>, then a new border is installed
     * for the {@link #getDisplayerParent() displayer parent}, and some {@link MouseListener}s
     * are installed. When the mouse is over the border it will change the cursor
     * and the user can resize or move the window. If <code>false</code>
     * nothing happens and the resizing system has to be implemented by the
     * subclass.
     */
    protected void init( Component window, Container contentParent, boolean resizeable ){
        if( window == null )
            throw new IllegalArgumentException( "window must not be null" );
        
        if( contentParent == null )
            throw new IllegalArgumentException( "contentParent must not be null" );
        
        this.window = window;
        
        content = createContent();
        contentParent.setLayout( new GridLayout( 1, 1 ) );
        contentParent.add( content );

        Container parent = getDisplayerParent();
        parent.setLayout( new GridLayout( 1, 1 ));

        if( resizeable ){
            if( parent instanceof JComponent ){
                ((JComponent)parent).setBorder( BorderFactory.createCompoundBorder( 
                        BorderFactory.createBevelBorder( BevelBorder.RAISED ),
                        BorderFactory.createEmptyBorder( 1, 1, 1, 1 )));
            }

            Listener listener = new Listener();
            parent.addMouseListener( listener );
            parent.addMouseMotionListener( listener );
        }
    }

    @Override
    protected Component getWindowComponent() {
        return window;
    }

    @Override
    public void setDockable( Dockable dockable ) {
        Dockable old = getDockable();
        if( old != null ){
            old.removeDockableListener( listener );
        }
        
        super.setDockable( dockable );
        
        if( dockable != null ){
            dockable.addDockableListener( listener );
        }
        
        updateTitleIcon();
        updateTitleIcon();
    }
    
    @Override
    protected void showDisplayer( DockableDisplayer displayer ) {
        if( this.displayer != displayer ){
            if( this.displayer != null ){
                getDisplayerParent().remove( this.displayer.getComponent() );
            }

            this.displayer = displayer;

            if( displayer != null ){
                getDisplayerParent().add( displayer.getComponent() );
            }
        }

        validate();
    }

    /**
     * Explicitly sets the icon of the title.
     * @param titleIcon the new icon or <code>null</code> to use the
     * {@link Dockable}s icon.
     */
    public void setTitleIcon( Icon titleIcon ) {
        this.titleIcon = titleIcon;
        updateTitleIcon();
    }
    
    /**
     * Gets the icon which should be used in the title.
     * @return the icon
     */
    protected Icon getTitleIcon(){
        if( titleIcon != null )
            return titleIcon;
        
        Dockable dockable = getDockable();
        if( dockable == null )
            return null;
        
        return dockable.getTitleIcon();
    }
    
    /**
     * Called when the icon of the title should be updated.
     * @see #getTitleIcon()
     */
    protected void updateTitleIcon(){
        // nothing to do
    }
    
    /**
     * Explicitly sets the text of the title.
     * @param titleText the new text or <code>null</code> to use
     * the {@link Dockable}s title text.
     */
    public void setTitleText( String titleText ) {
        this.titleText = titleText;
        updateTitleText();
    }
    
    /**
     * Gets the text which should be used in the title.
     * @return the text, might be <code>null</code>
     */
    protected String getTitleText(){
        if( titleText != null )
            return titleText;
        
        Dockable dockable = getDockable();
        if( dockable == null )
            return null;
        
        return dockable.getTitleText();
    }
    
    /**
     * Called when the text of the title should be updated.
     * @see #getTitleText()
     */    
    protected void updateTitleText(){
        // nothing to do
    }
    
    public Rectangle getWindowBounds() {
        return window.getBounds();
    }

    public void setPaintCombining( boolean paint ) {
        painting = paint;
        window.repaint();
    }

    public void setVisible( boolean visible ) {
        window.setVisible( visible );
    }

    public void setWindowBounds( Rectangle bounds ) {
        Rectangle valid = getStation().getBoundaryRestriction().check( this, bounds );

        if( valid != null )
            window.setBounds( valid );
        else
            window.setBounds( bounds );

        invalidate();
        validate();
    }

    public void checkWindowBounds() {
        Rectangle valid = getStation().getBoundaryRestriction().check( this );
        if( valid != null ){
            window.setBounds( valid );
        }
    }

    /**
     * Invalidates the layout of this window.
     * @see Component#invalidate()
     */
    public void invalidate(){
        window.invalidate();
    }

    public void validate() {
        window.validate();
    }

    /**
     * Sets the current cursor of this window
     * @param cursor the cursor
     * @see Component#setCursor(Cursor)
     */
    protected void setCursor( Cursor cursor ){
        window.setCursor( cursor );
    }

    /**
     * Makes a guess how big the insets of the {@link Dockable} compared to
     * the whole dialog are.
     * @return the insets, only a guess
     */
    public Insets getDockableInsets(){
        Container parent = getDisplayerParent();
        Insets parentInsets = parent.getInsets();
        if( parentInsets == null )
            parentInsets = new Insets( 0, 0, 0, 0 );

        Point zero = new Point( 0, 0 );
        zero = SwingUtilities.convertPoint( parent, zero, window );

        parentInsets.left += zero.x;
        parentInsets.top += zero.y;
        parentInsets.right += window.getWidth() - parent.getWidth() - zero.x;
        parentInsets.bottom += window.getHeight() - parent.getHeight() - zero.y;

        if( displayer == null )
            return parentInsets;

        Insets insets = displayer.getDockableInsets();
        parentInsets.top += insets.top;
        parentInsets.bottom += insets.bottom;
        parentInsets.left += insets.left;
        parentInsets.right += insets.right;

        return parentInsets;
    }

    /**
     * Creates the component that will be used as 
     * {@link JDialog#setContentPane(Container) content-pane}.
     * This method is invoked by the constructor.
     * @return the new content pane
     */
    protected OverpaintablePanel createContent(){
        OverpaintablePanel panel = new OverpaintablePanel(){
            @Override
            protected void paintOverlay( Graphics g ) {
                if( painting ){
                    ScreenDockStation station = getStation();
                    StationPaint paint = station.getPaint();
                    Insets insets = getInsets();

                    Rectangle bounds = new Rectangle( 0, 0, getWidth(), getHeight() );
                    Rectangle insert = new Rectangle( 2*insets.left, 2*insets.top, 
                            getWidth() - 2*(insets.left+insets.right),
                            getHeight() - 2*(insets.top+insets.bottom ));


                    paint.drawInsertion( g, station, bounds, insert );
                }
            }
        };

        return panel;
    }

    /**
     * Gets the container on which the displayer is shown.
     * @return the parent of the displayer
     */
    protected Container getDisplayerParent(){
        return content.getContentPane();
    }

    /**
     * Gets the displayer which is shown on this dialog.
     * @return The displayer, may be <code>null</code>
     * @see #showDisplayer(DockableDisplayer)
     */
    public DockableDisplayer getDisplayer() {
        return displayer;
    }

    private enum Position{ N, E, S, W, NE, SW, NW, SE, MOVE, NOTHING };

    private class Listener implements MouseListener, MouseMotionListener{
        private boolean pressed = false;
        private Position position = Position.NOTHING;

        private Point start;
        private Rectangle bounds;

        public void mouseClicked( MouseEvent e ) {
            // do nothing
        }

        public void mousePressed( MouseEvent e ) {
            updateCursor( e );
            if( !pressed ){
                if( e.getButton() == MouseEvent.BUTTON1 ){
                    pressed = true;
                    start = e.getPoint();
                    SwingUtilities.convertPointToScreen( start, e.getComponent() );
                    bounds = getWindowBounds();
                }
            }
        }

        public void mouseReleased( MouseEvent e ) {
            if( pressed && e.getButton() == MouseEvent.BUTTON1 ){
                pressed = false;
                updateCursor( e );
                checkWindowBounds();
                invalidate();
                validate();
            }
        }

        public void mouseEntered( MouseEvent e ) {
            if( !pressed && e.getButton() == MouseEvent.NOBUTTON ){
                updateCursor( e );
                position = Position.NOTHING;
            }
        }

        public void mouseExited( MouseEvent e ) {
            if( !pressed && e.getButton() == MouseEvent.NOBUTTON ){
                setCursor( Cursor.getDefaultCursor() );
                position = Position.NOTHING;
            }
        }

        public void mouseDragged( MouseEvent e ) {
            if( pressed ){
                Point point = e.getPoint();
                SwingUtilities.convertPointToScreen( point, e.getComponent() );

                int dx = point.x - start.x;
                int dy = point.y - start.y;

                Rectangle bounds = new Rectangle( this.bounds );

                int min = 25;

                if( position == Position.N || position == Position.NE || position == Position.NW ){
                    bounds.height -= dy;
                    bounds.y += dy;

                    if( bounds.height < min ){
                        bounds.y -= min - bounds.height;
                        bounds.height = min;
                    }
                }
                if( position == Position.E || position == Position.NE || position == Position.SE ){
                    bounds.width += dx;
                    if( bounds.width < min )
                        bounds.width = min;
                }
                if( position == Position.S || position == Position.SE || position == Position.SW ){
                    bounds.height += dy;
                    if( bounds.height < min )
                        bounds.height = min;
                }
                if( position == Position.W || position == Position.SW || position == Position.NW ){
                    bounds.width -= dx;
                    bounds.x += dx;

                    if( bounds.width < min ){
                        bounds.x -= min - bounds.width;
                        bounds.width = min;
                    }
                }
                if( position == Position.MOVE ){
                    bounds.x += dx;
                    bounds.y += dy;
                }

                setWindowBounds( bounds );
                invalidate();
                validate();
            }
        }

        public void mouseMoved( MouseEvent e ) {
            if( !pressed && e.getButton() == MouseEvent.NOBUTTON )
                updateCursor( e );
        }


        private void updateCursor( MouseEvent e ){
            Container component = getDisplayerParent();
            Insets insets = component.getInsets();

            boolean valid = e.getComponent() == component && e.getY() <= insets.top || e.getY() >= component.getHeight() - insets.bottom ||
            e.getX() <= insets.left || e.getX() >= component.getWidth() - insets.right;

            if( valid ){
                boolean top = e.getY() <= insets.top * 5;
                boolean left = e.getX() <= insets.left * 5;
                boolean bottom = e.getY() >= component.getHeight() - insets.bottom * 5;
                boolean right = e.getX() >= component.getWidth() - insets.right * 5;

                if( top && left ){
                    setCursor( Cursor.getPredefinedCursor( Cursor.NW_RESIZE_CURSOR ) );
                    position = Position.NW;
                }
                else if( top && right ){
                    setCursor( Cursor.getPredefinedCursor( Cursor.NE_RESIZE_CURSOR ) );
                    position = Position.NE;
                }
                else if( bottom && right ){
                    setCursor( Cursor.getPredefinedCursor( Cursor.SE_RESIZE_CURSOR ) );
                    position = Position.SE;
                }
                else if( bottom && left ){
                    setCursor( Cursor.getPredefinedCursor( Cursor.SW_RESIZE_CURSOR ) );
                    position = Position.SW;
                }
                else if( top ){
                    int width = component.getWidth();
                    if( e.getX() > width / 3 && e.getX() < width / 3 * 2 ){
                        setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ));
                        position = Position.MOVE;
                    }
                    else{
                        setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
                        position = Position.N;
                    }
                }
                else if( bottom ){
                    setCursor( Cursor.getPredefinedCursor( Cursor.S_RESIZE_CURSOR ) );
                    position = Position.S;
                }
                else if( left ){
                    setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
                    position = Position.W;
                }
                else if( right ){
                    setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
                    position = Position.E;
                }
                else{
                    setCursor( Cursor.getDefaultCursor() );
                    position = Position.NOTHING;
                }
            }
            else{
                setCursor( Cursor.getDefaultCursor() );
                position = Position.NOTHING;
            }
        }
    }

}