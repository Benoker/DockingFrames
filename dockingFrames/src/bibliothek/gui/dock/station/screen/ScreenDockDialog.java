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

package bibliothek.gui.dock.station.screen;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationPaint;


/**
 * A {@link Dialog} on which exactly one {@link DockableDisplayer} can be placed.
 * This dialog has {@link Dialog#setModal(boolean) modal} and has no
 * {@link Dialog#setUndecorated(boolean) decorations}. However, the user
 * can grab the border of the dialog to resize it.
 * @author Benjamin Sigg
 */
public class ScreenDockDialog extends JDialog {
    private ScreenDockStation station;
    private DockableDisplayer displayer;
    
    private OverpaintablePanel content;
    
    /**
     * Creates a new dialog.
     * @param station the station to which this dialog is responsible
     * @param frame the owner of the dialog
     */
    public ScreenDockDialog( ScreenDockStation station, Frame frame ){
        super( frame );
        
        if( station == null )
            throw new IllegalArgumentException( "Station must not be null" );
        
        this.station = station;
    }

    /**
     * Creates a new dialog.
     * @param station the station to which this dialog is responsible
     * @param dialog the owner of this dialog
     */
    public ScreenDockDialog( ScreenDockStation station, Dialog dialog ){
        super( dialog );
        
        if( station == null )
            throw new IllegalArgumentException( "Station must not be null" );
        
        this.station = station;
    }
    
    {
        setUndecorated( true );
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        
        content = createContent();
        setContentPane( content );
        
        Container parent = getDisplayerParent();
        if( parent instanceof JComponent ){
            ((JComponent)parent).setBorder( BorderFactory.createCompoundBorder( 
                BorderFactory.createBevelBorder( BevelBorder.RAISED ),
                BorderFactory.createEmptyBorder( 1, 1, 1, 1 )));
        }
        
        Listener listener = new Listener();
        parent.addMouseListener( listener );
        parent.addMouseMotionListener( listener );
        parent.setLayout( new GridLayout( 1, 1 ));
        
        setModal( false );
    }


    /**
     * Sets the bounds of this dialog such that the title is still visible
     * in the screen.
     * @param bounds the new bounds of this dialog
     */
    public void setBoundsInScreen( Rectangle bounds ) {
        setBoundsInScreen( bounds.x, bounds.y, bounds.width, bounds.height );
    }
    
    /**
     * Sets the bounds of this dialog such that the title is still visible
     * in the screen.
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     * @param width the new height
     * @param height the new height
     */
    public void setBoundsInScreen( int x, int y, int width, int height ) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        
        double fit = -1.0;
        GraphicsDevice best = null;
        
        for( GraphicsDevice screen : screens ){
            double check = checkBounds( x, y, width, height, screen );
            if( check > fit ){
                fit = check;
                best = screen;
            }
        }
        
        if( best == null )
            setBounds( x, y, width, height );
        else
            setBoundsInScreen( x, y, width, height, best );
    }
    
    /**
     * Checks how good this dialog fits into the screen <code>device</code>
     * @param x the desired x-coordinate
     * @param y the desired y-coordinate
     * @param width the desired width
     * @param height the desired height
     * @param device the targeted screen
     * @return a value between 0 and 1, where 0 means "does not fit" and
     * 1 means "perfect".
     */
    protected double checkBounds( int x, int y, int width, int height, GraphicsDevice device ){
        GraphicsConfiguration config = getGraphicsConfiguration();
        if( config == null )
            return 0.0;
        
        Rectangle screen = config.getBounds();
        Rectangle dialog = new Rectangle( x, y, width, height );
        
        Rectangle intersection = screen.intersection( dialog );
        
        if( intersection.width <= 0 || intersection.height <= 0 )
            return 0.0;
        
        return (dialog.width * dialog.height) / (intersection.width * intersection.height);
    }
    
    /**
     * Sets the location and size of this dialog such that it is visible within
     * the screen <code>device</code>. 
     * @param x the desired x-coordinate
     * @param y the desired y-coordinate
     * @param width the desired width
     * @param height the desired height
     * @param device the screen in which to show this dialog
     */
    protected void setBoundsInScreen( int x, int y, int width, int height, GraphicsDevice device ){
        GraphicsConfiguration config = getGraphicsConfiguration();
        if( config != null ){
            Rectangle size = config.getBounds();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets( config );
            if( insets == null )
                insets = new Insets( 0,0,0,0 );
            
            width = Math.min( size.width-insets.left-insets.right, width );
            height = Math.min( size.height-insets.top-insets.bottom, height );
            
            x = Math.max( x, size.x+insets.left );
            y = Math.max( y, size.y+insets.right );
            
            x = Math.min( x, size.width - insets.left - insets.right - width + size.x );
            y = Math.min( y, size.height - insets.top - insets.bottom - height + size.y );
        }
        
        setBounds( x, y, width, height );
    }
    
    /**
     * Gets the station for which this dialog is shown
     * @return the station
     */
    public ScreenDockStation getStation() {
        return station;
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
                if( station != null && station.shouldDraw( ScreenDockDialog.this )){
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
     * Sets the displayer which will be shown on this dialog.
     * @param displayer The displayer. A value of <code>null</code> just
     * removes the old displayer.
     */
    public void setDisplayer( DockableDisplayer displayer ) {
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
     * Gets the container on which the displayer is shown.
     * @return the parent of the displayer
     */
    protected Container getDisplayerParent(){
        return content.getContentPane();
    }
    
    /**
     * Gets the displayer which is shown on this dialog.
     * @return The displayer, may be <code>null</code>
     * @see #setDisplayer(DockableDisplayer)
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
                    bounds = getBounds();
                }
            }
        }

        public void mouseReleased( MouseEvent e ) {
            if( pressed && e.getButton() == MouseEvent.BUTTON1 ){
                pressed = false;
                updateCursor( e );
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
                
                setBoundsInScreen( bounds );
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
