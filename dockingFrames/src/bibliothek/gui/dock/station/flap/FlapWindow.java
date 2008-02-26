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

package bibliothek.gui.dock.station.flap;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This window pops up if the user presses one of the buttons of a 
 * {@link FlapDockStation}. The window shows one {@link Dockable}
 */
public class FlapWindow extends JDialog implements MouseListener, MouseMotionListener{
    /** The displayer which is the direct parent of the {@link Dockable} and its title */
    private DockableDisplayer displayer;
    
    /** <code>true</code> if the mouse is currently pressed */
    private boolean pressed;
    
    /** The owner of this window */
    private FlapDockStation station;
    /** The buttons on the station */
    private ButtonPane buttonPane;
    /** Information where the user will drop or move a {@link Dockable} */
    private FlapDropInfo dropInfo;
    
    /**
     * Constructs a new window.
     * @param station the station which manages this window
     * @param buttonPane the buttons on the station
     * @param frame the owner of the window
     */
    public FlapWindow( FlapDockStation station, ButtonPane buttonPane, Frame frame ){
        super( frame, false );
        this.station = station;
        this.buttonPane = buttonPane;
    }
    
    /**
     * Constructs a new window.
     * @param station the station which manages this window
     * @param buttonPane the buttons on the station
     * @param dialog the owner of this window
     */
    public FlapWindow( FlapDockStation station, ButtonPane buttonPane, Dialog dialog ){
        super( dialog, false );
        this.station = station;
        this.buttonPane = buttonPane;
    }
    
    {
        JPanel content = new JPanel(){
            @Override
            public void paint( Graphics g ) {
                super.paint(g);
                if( dropInfo != null && dropInfo.getCombine() != null && dropInfo.isDraw() ){
                    Rectangle bounds = new Rectangle( 0, 0, getWidth(), getHeight() );
                    FlapWindow.this.station.getPaint().drawInsertion( g, FlapWindow.this.station, bounds, bounds );
                }
            }
        };
        setContentPane( content );
        
        content.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ));
        setUndecorated( true );
        content.addMouseListener( this );
        content.addMouseMotionListener( this );
        
        content.setLayout( new LayoutManager(){
            public void addLayoutComponent( String name, Component comp ) {
            	// do nothing
            }

            public void removeLayoutComponent( Component comp ) {
            	// do nothing
            }

            public Dimension preferredLayoutSize( Container parent ) {
                if( displayer == null )
                    return new Dimension( 100, 100 );
                
                return displayer.getComponent().getPreferredSize();
            }

            public Dimension minimumLayoutSize( Container parent ) {
                if( displayer == null )
                    return new Dimension( 100, 100 );
                
                return displayer.getComponent().getMinimumSize();
            }

            public void layoutContainer( Container parent ) {
                if( displayer != null ){
                    Insets insets = parent.getInsets();
                    insets = new Insets( insets.top, insets.left, insets.bottom, insets.right );
                    
                    if( station.getDirection() == Direction.SOUTH )
                        insets.bottom += station.getWindowBorder();
                    else if( station.getDirection() == Direction.NORTH )
                        insets.top += station.getWindowBorder();
                    else if( station.getDirection() == Direction.EAST )
                        insets.right += station.getWindowBorder();
                    else
                        insets.left += station.getWindowBorder();
                    
                    displayer.getComponent().setBounds( insets.left, insets.top, 
                            parent.getWidth()-insets.left-insets.right, 
                            parent.getHeight()-insets.top-insets.bottom );
                }
            }
        });
        
        addComponentListener( new ComponentListener(){
			public void componentHidden( ComponentEvent e ){
				// ignore
			}

			public void componentMoved( ComponentEvent e ){
				// ignore				
			}

			public void componentResized( ComponentEvent e ){
				// ignore
			}

			public void componentShown( ComponentEvent e ){
				if( !station.isFlapWindow( FlapWindow.this ) || getDockable() == null ){
					// This window should not be visible if it is not used
					// by its former owner
					SwingUtilities.invokeLater( new Runnable(){
						public void run(){
							dispose();
						}
					});
				}
			}
        });
    }
    
    /**
     * Gets the station for which this window is shown.
     * @return the owner of this window
     */
    public FlapDockStation getStation() {
        return station;
    }
    
    /**
     * Sets information where a {@link Dockable} will be dropped.
     * @param dropInfo the information or <code>null</code>
     */
    public void setDropInfo( FlapDropInfo dropInfo ) {
        this.dropInfo = dropInfo;
        repaint();
    }
    
    /**
     * Sets the title which should be displayed.
     * @param title the title or <code>null</code>
     */
    public void setDockTitle( DockTitle title ){
        displayer.setTitle( title );
    }
    
    /**
     * Gets the currently displayed title.
     * @return the title or <code>null</code>
     */
    public DockTitle getDockTitle(){
        if( displayer == null )
            return null;
        else
            return displayer.getTitle();
    }
    
    /**
     * Gets the {@link Dockable} which is shown on this window.
     * @return The {@link Dockable} or <code>null</code>
     */
    public Dockable getDockable(){
        if( displayer == null )
            return null;
        else
            return displayer.getDockable();
    }
    
    /**
     * Gets the displayer used to show a {@link Dockable}.
     * @return the displayer, might be <code>null</code>
     */
    public DockableDisplayer getDisplayer() {
        return displayer;
    }
    
    /**
     * Sets the {@link Dockable} which will be shown on this window.
     * @param dockable The <code>Dockable</code> or <code>null</code>
     */
    public void setDockable( Dockable dockable ){
        Container content = getDisplayerParent();
        
        if( displayer != null ){
            Dockable old = displayer.getDockable();
            DockTitle oldTitle = displayer.getTitle();
        
            station.getDisplayers().release( displayer );
            
            content.remove( displayer.getComponent() );
            
            displayer = null;
            
            if( oldTitle != null && old != null )
                old.unbind( oldTitle );
        }
        
        if( dockable != null ){
            DockTitle title = null;
            DockTitleVersion titleVersion = station.getTitleVersion();
            if( titleVersion != null ){
                title = dockable.getDockTitle( titleVersion );
                if( title != null )
                    dockable.bind( title );
            }
            
            displayer = station.getDisplayers().fetch( dockable, title );
            content.add( displayer.getComponent() );
        }
    }
    
    /**
     * Gets the container that will become the parent of a {@link DockableDisplayer}.
     * @return the parent
     */
    protected Container getDisplayerParent(){
        return getContentPane();
    }
    
    /**
     * Recalculates the size and the location of this window.
     */
    public void updateBounds(){
        Dockable dockable = displayer == null ? null : displayer.getDockable();
        if( dockable != null ){
            Point location;
            Dimension size;
            FlapDockStation.Direction direction = station.getDirection();
            int windowSize = station.getWindowSize( dockable );
            Rectangle bounds = station.getExpansionBounds();
            
            if( direction == Direction.SOUTH ){
                location = new Point( bounds.x, bounds.height );
                size = new Dimension( bounds.width, windowSize );
            }
            else if( direction == Direction.NORTH ){
                location = new Point( bounds.x, -windowSize );
                size = new Dimension( bounds.width, windowSize );
            }
            else if( direction == Direction.WEST ){
                location = new Point( -windowSize, bounds.y );
                size = new Dimension( windowSize, bounds.height );
            }
            else{
                location = new Point( bounds.width, bounds.y );
                size = new Dimension( windowSize, bounds.height );
            }
            
            SwingUtilities.convertPointToScreen( location, buttonPane );
            setLocation( location );
            setSize( size );
            validate();
        }
    }
    
    public void mouseExited( MouseEvent e ) {
        if( !pressed )
            setCursor( Cursor.getDefaultCursor() );
    }
    
    public void mouseEntered( MouseEvent e ) {
        if( station.getDirection() == Direction.SOUTH )
            setCursor( Cursor.getPredefinedCursor( Cursor.S_RESIZE_CURSOR ) );
        else if( station.getDirection() == Direction.NORTH )
            setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
        else if( station.getDirection() == Direction.EAST )
            setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
        else
            setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
    }
    
    public void mousePressed( MouseEvent e ) {
        pressed = true;
    }
    
    public void mouseReleased( MouseEvent e ) {
        pressed = false;
    }
    
    public void mouseDragged( MouseEvent e ) {
        if( pressed ){
            Dockable dockable = displayer == null ? null : displayer.getDockable();
            if( dockable != null ){
                Point mouse = new Point( e.getX(), e.getY() );
                SwingUtilities.convertPointToScreen( mouse, e.getComponent() );
                
                Component flap = station.getComponent();
                
                Point zero = new Point( 0, 0 );
                SwingUtilities.convertPointToScreen( zero, flap );
                
                int size = 0;
                
                if( station.getDirection() == Direction.SOUTH )
                    size = mouse.y - zero.y - flap.getHeight();
                else if( station.getDirection() == Direction.NORTH )
                    size = zero.y - mouse.y;
                else if( station.getDirection() == Direction.EAST )
                    size = mouse.x - zero.x - flap.getWidth();
                else
                    size = zero.x - mouse.x;
                
                size = Math.max( size, station.getWindowMinSize() );
                station.setWindowSize( dockable, size );
            }
        }
    }
    
    public void mouseClicked( MouseEvent e ) {
        // do nothing
    }
    public void mouseMoved( MouseEvent e ) {
        // do nothing
    }
}
