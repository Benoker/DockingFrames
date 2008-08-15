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

package bibliothek.gui.dock.station.screen;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.util.PropertyValue;


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
    
    /** the restrictions of the boundaries of this dialog*/
    private PropertyValue<BoundaryRestriction> restriction =
        new PropertyValue<BoundaryRestriction>( ScreenDockStation.BOUNDARY_RESTRICTION ){
            @Override
            protected void valueChanged( BoundaryRestriction oldValue, BoundaryRestriction newValue ) {
                checkRestrictedBounds();
            }
    };

    
    /**
     * Creates a new dialog. Note that the constructors with
     * an owner window are preferred.
     * @param station the station to which this dialog is responsible
     */
    public ScreenDockDialog( ScreenDockStation station ){
        if( station == null )
            throw new IllegalArgumentException( "Station must not be null" );
        
        this.station = station;
    }

    
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
        getRootPane().setWindowDecorationStyle( JRootPane.NONE );
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
     * Sets the bounds of this dialog, uses the {@link #getRestriction() restrictions}
     * to check the validity of the bounds.
     * @param bounds the new bounds of this dialog
     */
    public void setRestrictedBounds( Rectangle bounds ) {
        Rectangle valid = restriction.getValue().check( this, bounds );
                
        if( valid != null )
            setBounds( valid );
        else
            setBounds( bounds );
        
        invalidate();
        validate();
    }
    
    /**
     * Sets the bounds of this dialog, uses the {@link #getRestriction() restrictions}
     * to check the validity of the bounds.
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     * @param width the new height
     * @param height the new height
     */
    public void setRestrictedBounds( int x, int y, int width, int height ) {
        setRestrictedBounds( new Rectangle( x, y, width, height ));
    }

    /**
     * Checks the validity of the bounds of this dialog.
     */
    public void checkRestrictedBounds(){
        Rectangle bounds = restriction.getValue().check( this );
        if( bounds != null ){
            setBounds( bounds );
        }
    }
    
    /**
     * Gets the restrictions of the boundaries of this dialog.
     * @return the restrictions
     */
    public PropertyValue<BoundaryRestriction> getRestriction() {
        return restriction;
    }
    
    /**
     * Gets the station for which this dialog is shown
     * @return the station
     */
    public ScreenDockStation getStation() {
        return station;
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
        zero = SwingUtilities.convertPoint( parent, zero, this );
        
        parentInsets.left += zero.x;
        parentInsets.top += zero.y;
        parentInsets.right += getWidth() - parent.getWidth() - zero.x;
        parentInsets.bottom += getHeight() - parent.getHeight() - zero.y;
        
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
     * Sets the {@link DockController} that is needed to read properties.
     * @param controller the new controller, can be <code>null</code>
     */
    public void setController( DockController controller ){
        restriction.setProperties( controller );
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
                checkRestrictedBounds();
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
                
                setRestrictedBounds( bounds );
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
