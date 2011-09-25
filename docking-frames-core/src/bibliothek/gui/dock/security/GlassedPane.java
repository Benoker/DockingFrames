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

package bibliothek.gui.dock.security;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.control.focus.FocusController;
import bibliothek.gui.dock.control.focus.MouseFocusObserver;
import bibliothek.util.JavaVersionWorkaround;

/**
 * A panel containing two children: a "content pane" and a "glass pane". The
 * "content pane" can be replaced by the client and can be any {@link JComponent}.
 * The "glassed pane" is an invisible panel above the "content pane". It will
 * catch all {@link MouseEvent}s, inform the {@link FocusController} about
 * them, and then forward the events to the "content pane".
 * @author Benjamin Sigg
 */
public class GlassedPane extends JPanel{
    /** An arbitrary component */
    private JComponent contentPane = new JPanel();
    /** A component lying over all other components. Catches every MouseEvent */
    private JComponent glassPane = new GlassPane();
    /** A controller which will be informed about every click of the mouse */
    private DockController controller;
    
    /**
     * Creates a new pane
     */
    public GlassedPane(){
        setLayout( null );
        contentPane.setOpaque( false );
        setOpaque( false );
        
        add( glassPane );
        add( contentPane );
        setFocusCycleRoot( true );
    }
    
    /**
     * Sets the controller to inform about {@link KeyEvent}s.
     * @param controller the controller to inform
     */
    public void setController( DockController controller ){
		this.controller = controller;
	}

    @Override
    public void doLayout() {
        int width = getWidth();
        int height = getHeight();
        if( contentPane != null ){
        	contentPane.setBounds( 0, 0, width, height );
        }
        glassPane.setBounds( 0, 0, width, height );
    }

    @Override
    public Dimension getPreferredSize() {
    	if( contentPane == null ){
    		return super.getPreferredSize();
    	}
        return contentPane.getPreferredSize();
    }
    @Override
    public Dimension getMaximumSize() {
    	if( contentPane == null ){
    		return super.getMaximumSize();
    	}
        return contentPane.getMaximumSize();
    }
    @Override
    public Dimension getMinimumSize() {
    	if( contentPane == null ){
    		return super.getMinimumSize();
    	}
    	return contentPane.getMinimumSize();
    }

    /**
     * Sets the center panel of this GlassedPane.
     * @param contentPane the content of this pane, a value of <code>null</code> will
     * just remove the current content pane
     */
    public void setContentPane( JComponent contentPane ) {
        this.contentPane = contentPane;

        removeAll();

        add( glassPane );
        if( contentPane != null ){
        	add( contentPane );
        }
    }

    /**
     * Gets the content of this pane.
     * @return the content, may be <code>null</code>
     */
    public JComponent getContentPane(){
        return contentPane;
    }

    /**
     * Gets the transparent panel that is lying over the ContentPane.
     * @return the GlassPane
     */
    public JComponent getGlassPane(){
        return glassPane;
    }
    
    /**
     * A panel that lies over all other components of the enclosing GlassedPane.
     * This panel catches all MouseEvent, and informs the {@link MouseFocusObserver}.
     * @author Benjamin Sigg
     */
    private class GlassPane extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener{
        /** the component where a drag-event started */
        private Component dragged;
        /** the component currently under the mouse */
        private Component over;
        /** the number of pressed buttons */
        private int downCount = 0;

        /**
         * Creates a new GlassPane.
         */
        public GlassPane(){
            addMouseListener( this );
            addMouseMotionListener( this );
            addMouseWheelListener( this );

            setOpaque( false );
            
            setFocusable( false );
            
            JavaVersionWorkaround.markAsGlassPane( this );
        }

//        @Override
//        protected void paintComponent( Graphics g ){
//	        g.setColor( Color.BLUE );
//	        int w = getWidth();
//	        int h = getHeight();
//	        g.drawLine( 0, 0, w, h );
//	        g.drawLine( w, 0, 0, h );
//        }
        
        public void mouseClicked( MouseEvent e ) {
            if( !e.isConsumed() )
                send( e );
        }

        public void mousePressed( MouseEvent e ) {
            if( !e.isConsumed() )
                send( e );
        }

        public void mouseReleased( MouseEvent e ) {
            if( !e.isConsumed() )
                send( e );
        }

        public void mouseEntered( MouseEvent e ) {
            if( !e.isConsumed() )
                send( e );
        }

        public void mouseExited( MouseEvent e ) {
            if( !e.isConsumed() && isVisible() )
                send( e );

            if( !isVisible() ){
                downCount = 0;
            }
        }

        public void mouseDragged( MouseEvent e ) {
            if( !e.isConsumed() )
                send( e );
        }

        public void mouseMoved( MouseEvent e ) {
            if( !e.isConsumed() )
                send( e );
        }

        public void mouseWheelMoved( MouseWheelEvent e ) {
            if( !e.isConsumed() )
                send( e );
        }

        /**
         * Shorthand for <code>send( e, e.getID() );</code>.
         * @param e the event to send
         */
        private void send( MouseEvent e ){
            send( e, e.getID() );
        }

        /**
         * Dispatches the event <code>e</code> to the ContentPane or a child
         * of the ContentPane. Also informs the {@link MouseFocusObserver} about the event.
         * @param e the event to handle
         * @param id the type of the event
         */
        private void send( MouseEvent e, int id ){
        	if( contentPane == null ){
        		return;
        	}

            Point mouse = e.getPoint();
            Component component = SwingUtilities.getDeepestComponentAt( contentPane, mouse.x, mouse.y );

            boolean drag = id == MouseEvent.MOUSE_DRAGGED;
            boolean press = id == MouseEvent.MOUSE_PRESSED;
            boolean release = id == MouseEvent.MOUSE_RELEASED;
            boolean moved = id == MouseEvent.MOUSE_MOVED;
            boolean entered = id == MouseEvent.MOUSE_ENTERED;
            boolean exited = id == MouseEvent.MOUSE_EXITED;
            
            if( drag && dragged == null )
                dragged = component;
            else if( drag )
                component = dragged;

            if( press ){
            	downCount |= 1 << e.getButton();
            }

            if( downCount > 0 && dragged != null )
                component = dragged;
            else if( downCount > 0 && dragged == null )
                dragged = component;
            else if( downCount == 0 )
                dragged = null;

            if( release ){
            	downCount &= ~(1 << e.getButton());
            }
            if( (e.getModifiersEx() & (MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)) == 0 ){
            	// no button is pressed currently, reset dragging
            	downCount = 0;
            	dragged = null;
            }

            if( moved || entered || exited ){
                if( over != component ){
                    if( over != null ){
                        over.dispatchEvent( new MouseEvent( 
                                over, MouseEvent.MOUSE_EXITED, e.getWhen(), e.getModifiers(), 
                                mouse.x, mouse.y, e.getClickCount(), e.isPopupTrigger(), 
                                e.getButton() ));
                    }

                    over = component;

                    if( over != null ){
                        over.dispatchEvent( new MouseEvent( 
                                over, MouseEvent.MOUSE_ENTERED, e.getWhen(), e.getModifiers(), 
                                mouse.x, mouse.y, e.getClickCount(), e.isPopupTrigger(), 
                                e.getButton() ));
                    }
                }
            }
            
            if( component == null ){
                setCursor( null );
                setToolTipText( null );
            }
            else{
                mouse = SwingUtilities.convertPoint( this, mouse, component );
                MouseEvent forward = new MouseEvent( 
                        component, id, e.getWhen(), e.getModifiers(), 
                        mouse.x, mouse.y, e.getClickCount(), e.isPopupTrigger(), 
                        e.getButton() );
                
                if( controller != null ){
                	controller.getGlobalMouseDispatcher().dispatch( forward );
                }
                
                component.dispatchEvent( forward );
                
                Cursor cursor = component.getCursor();
                if( getCursor() != cursor )
                    setCursor( cursor );

                if( component instanceof JComponent ){
                    JComponent jcomp = (JComponent)component;
                    String tooltip = jcomp.getToolTipText( forward );
                    String thistip = getToolTipText();

                    if( tooltip != thistip ){
                        if( tooltip == null || thistip == null || !tooltip.equals( thistip ))
                            setToolTipText( tooltip );
                    }
                }
                else
                    setToolTipText( null );
            }
        }

        /**
         * Dispatches the event <code>e</code> to the ContentPane or one
         * of the children of ContentPane. Also informs the focusController about
         * the event.
         * @param e the event to dispatch
         */
        private void send( MouseWheelEvent e ){
        	if( contentPane == null ){
        		return;
        	}
        	
            Point mouse = e.getPoint();
            Component component = SwingUtilities.getDeepestComponentAt( contentPane, mouse.x, mouse.y );
            if( component != null ){
                mouse = SwingUtilities.convertPoint( this, mouse, component );
                MouseWheelEvent forward = new MouseWheelEvent( 
                        component, e.getID(), e.getWhen(), e.getModifiers(), 
                        mouse.x, mouse.y, e.getClickCount(), e.isPopupTrigger(), 
                        e.getScrollType(), e.getScrollAmount(), e.getWheelRotation() );
                
                if( controller != null ){
                	controller.getGlobalMouseDispatcher().dispatch( forward );
                }
                
                component.dispatchEvent( forward );
            }
        }
    }
}