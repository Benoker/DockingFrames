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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This window pops up if the user presses one of the buttons of a 
 * {@link FlapDockStation}. The window shows one {@link Dockable}
 */
public class FlapWindow extends JDialog implements MouseListener, MouseMotionListener{
	/** the element that is shown on this window */
    private StationChildHandle dockable;
	
    /** a listener for the current {@link DockableDisplayer} */
    private DockableDisplayerListener displayerListener = new DockableDisplayerListener(){
    	public void discard( DockableDisplayer displayer ){
	    	discardDisplayer();	
    	}
    };
    
    /** <code>true</code> if the mouse is currently pressed */
    private boolean pressed;
    
    /** The owner of this window */
    private FlapDockStation station;
    /** The buttons on the station */
    private ButtonPane buttonPane;
    /** Information where the user will drop or move a {@link Dockable} */
    private FlapDropInfo dropInfo;
    
    /** the panel onto which {@link #dockable} is put */
    private JComponent contentPane;

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
        OverpaintablePanel content = new OverpaintablePanel(){
            @Override
            protected void paintOverlay( Graphics g ) {
                if( dropInfo != null && dropInfo.getCombine() != null && dropInfo.isDraw() ){
                    Rectangle bounds = new Rectangle( 0, 0, getWidth(), getHeight() );
                    FlapWindow.this.station.getPaint().drawInsertion( g, FlapWindow.this.station, bounds, bounds );
                }
            }
        };
        
        setContentPane( content );
        contentPane = content.getContentPane();
        
        contentPane.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ));
        setUndecorated( true );
        getRootPane().setWindowDecorationStyle( JRootPane.NONE );
        contentPane.addMouseListener( this );
        contentPane.addMouseMotionListener( this );
        
        contentPane.setLayout( new LayoutManager(){
            public void addLayoutComponent( String name, Component comp ) {
            	// do nothing
            }

            public void removeLayoutComponent( Component comp ) {
            	// do nothing
            }

            public Dimension preferredLayoutSize( Container parent ) {
            	DockableDisplayer displayer = getDisplayer();
            	
                if( displayer == null )
                    return new Dimension( 100, 100 );
                
                return displayer.getComponent().getPreferredSize();
            }

            public Dimension minimumLayoutSize( Container parent ) {
            	DockableDisplayer displayer = getDisplayer();
            	
                if( displayer == null )
                    return new Dimension( 100, 100 );
                
                return displayer.getComponent().getMinimumSize();
            }

            public void layoutContainer( Container parent ) {
            	DockableDisplayer displayer = getDisplayer();
            	
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
    
    @Override
    public void setVisible( boolean flag ){    	   
    	if( flag ){
    		// Actually this should not be necessary and only
    		// prevents some strange bug where the size gets wrong,
    		// the origin of the bug remains a mystery.
    		updateBounds();
    	}

    	super.setVisible( flag );
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
    public void setDockTitle( DockTitleVersion title ){
    	if( dockable != null ){
    		dockable.setTitleRequest( title );
    	}
    }
    
    /**
     * Gets the title which is currently displayed
     * @return the title or <code>null</code>
     */
    public DockTitle getDockTitle(){
    	if( dockable == null )
    		return null;
    	return dockable.getTitle();
    }
    
    /**
     * Gets the {@link Dockable} which is shown on this window.
     * @return The {@link Dockable} or <code>null</code>
     */
    public Dockable getDockable(){
    	if( dockable == null )
    		return null;
    	return dockable.getDockable();
    }
    
    /**
     * Gets the displayer used to show a {@link Dockable}.
     * @return the displayer, might be <code>null</code>
     */
    public DockableDisplayer getDisplayer() {
        if( dockable == null )
        	return null;
        return dockable.getDisplayer();
    }
    
    /**
     * Sets the {@link Dockable} which will be shown on this window.
     * @param dockable The <code>Dockable</code> or <code>null</code>
     */
    public void setDockable( Dockable dockable ){
        Container content = getDisplayerParent();
        
        if( this.dockable != null ){
        	DockableDisplayer displayer = getDisplayer();
        	displayer.removeDockableDisplayerListener( displayerListener );
        	content.remove( displayer.getComponent() );
        	this.dockable.destroy();
        	this.dockable = null;
        }
        
        if( dockable != null ){
        	this.dockable = new StationChildHandle( station, station.getDisplayers(), dockable, station.getTitleVersion() );
        	this.dockable.updateDisplayer();
        	
            DockableDisplayer displayer = getDisplayer();
            displayer.addDockableDisplayerListener( displayerListener );
            content.add( displayer.getComponent() );
        }
    }
    
    /**
     * Replaces the current {@link DockableDisplayer} with a new instance.
     */
    protected void discardDisplayer(){
    	if( dockable != null ){
    		DockableDisplayer displayer = dockable.getDisplayer();
    		displayer.removeDockableDisplayerListener( displayerListener );
    		contentPane.remove( displayer.getComponent() );
    		
    		dockable.updateDisplayer();
    		
    		displayer = dockable.getDisplayer();
    		displayer.addDockableDisplayerListener( displayerListener );
    		contentPane.add( displayer.getComponent() );

        	updateBounds();
    	}
    }
    
    /**
     * Gets the container that will become the parent of a {@link DockableDisplayer}.
     * @return the parent
     */
    protected Container getDisplayerParent(){
        return contentPane;
    }
    
    /**
     * Makes a guess how big the insets around the current {@link Dockable}
     * of this window are.
     * @return a guess of the insets
     */
    public Insets getDockableInsets(){
    	DockableDisplayer displayer = dockable.getDisplayer();
    	
        Insets insets = displayer.getDockableInsets();
        displayer.getComponent().getBounds();
        
        Point zero = new Point( 0, 0 );
        zero = SwingUtilities.convertPoint( displayer.getComponent(), zero, this );
        
        int deltaX = zero.x;
        int deltaY = zero.y;
        int deltaW = getWidth() - displayer.getComponent().getWidth();
        int deltaH = getHeight() - displayer.getComponent().getHeight();
        
        insets.left += deltaX;
        insets.top += deltaY;
        insets.right += deltaW - deltaX;
        insets.bottom += deltaH - deltaY;

        return insets;
    }
    
    /**
     * Recalculates the size and the location of this window.
     */
    public void updateBounds(){
    	DockableDisplayer displayer = getDisplayer();
    	Dockable dockable = displayer == null ? null : displayer.getDockable();
        if( dockable != null ){
        	validate();
            Point location;
            Dimension size;
            FlapDockStation.Direction direction = station.getDirection();
            int windowSize = station.getWindowSize( dockable );
            Rectangle bounds = station.getExpansionBounds();
            Insets insets = getDockableInsets();
            
            if( direction == Direction.SOUTH ){
                windowSize += insets.top + insets.bottom;
                location = new Point( bounds.x, bounds.height );
                size = new Dimension( bounds.width, windowSize );
            }
            else if( direction == Direction.NORTH ){
                windowSize += insets.top + insets.bottom;
                location = new Point( bounds.x, -windowSize );
                size = new Dimension( bounds.width, windowSize );
            }
            else if( direction == Direction.WEST ){
                windowSize += insets.left + insets.right;
                location = new Point( -windowSize, bounds.y );
                size = new Dimension( windowSize, bounds.height );
            }
            else{
                windowSize += insets.left + insets.right;
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
        	DockableDisplayer displayer = getDisplayer();
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
                Insets insets = getDockableInsets();
                if( station.getDirection() == Direction.NORTH || station.getDirection() == Direction.SOUTH )
                    size -= insets.top + insets.bottom;
                else
                    size -= insets.left + insets.right;
                
                if( size > 0 ){
                    station.setWindowSize( dockable, size );
                }
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
