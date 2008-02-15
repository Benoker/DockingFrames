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
import java.awt.event.*;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A panel containing two children: a "content pane" and a "glass pane". The
 * "content pane" can be replaced by the client and can be any {@link JComponent}.
 * The "glassed pane" is an invisible panel above the "content pane". It will
 * catch all {@link MouseEvent}s, inform a {@link SecureMouseFocusObserver} about
 * them, and then forward the events to the "content pane".
 * <b>Note:</b> clients can use {@link SecureDockController#wrap(JComponent)} to
 * create and register a {@link GlassedPane} in one step.
 * @author Benjamin Sigg
 */
public class GlassedPane extends JPanel{
    /** An arbitrary component */
    private JComponent contentPane = new JPanel();
    /** A component lying over all other components. Catches every MouseEvent */
    private JComponent glassPane = new GlassPane();
    /** A controller which will be informed about every click of the mouse */
    private SecureMouseFocusObserver focusController;
    
    /**
     * Creates a new pane
     */
    public GlassedPane(){
        setLayout( null );
        add( glassPane );
        add( contentPane );
        setFocusCycleRoot( true );
    }
    
    /**
     * Creates a new pane and registers <code>this</code> at <code>observer</code>.
     * @param observer the observer <code>this</code> has to be registered at
     */
    public GlassedPane( SecureMouseFocusObserver observer ){
    	this();
    	observer.addGlassPane( this );
    }
    
    /**
     * Sets the focus-observer which has to be informed when the mouse is clicked
     * or the mouse wheel is moved.
     * @param focusController the controller, may be <code>null</code>
     */
    public void setFocusController( SecureMouseFocusObserver focusController ) {
        this.focusController = focusController;
    }

    @Override
    public void doLayout() {
        int width = getWidth();
        int height = getHeight();
        contentPane.setBounds( 0, 0, width, height );
        glassPane.setBounds( 0, 0, width, height );
    }

    @Override
    public Dimension getPreferredSize() {
        return contentPane.getPreferredSize();
    }
    @Override
    public Dimension getMaximumSize() {
        return contentPane.getMinimumSize();
    }
    @Override
    public Dimension getMinimumSize() {
        return contentPane.getMinimumSize();
    }

    /**
     * Sets the center panel of this GlassedPane.
     * @param contentPane the content of this pane, not <code>null</code>
     */
    public void setContentPane( JComponent contentPane ) {
        if( contentPane == null )
            throw new IllegalArgumentException( "Content Pane must not be null" );
        this.contentPane = contentPane;

        removeAll();

        add( glassPane );
        add( contentPane );
    }

    /**
     * Gets the content of this pane.
     * @return the content
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
     * This panel catches all MouseEvent, and informs the {@link GlassedPane#focusController focusController}
     * about the events.
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
        }

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
         * of the ContentPane. Also informs the FocusManager about the event.
         * @param e the event to handle
         * @param id the type of the event
         */
        private void send( MouseEvent e, int id ){
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

            if( press )
                downCount++;

            if( downCount > 0 && dragged != null )
                component = dragged;
            else if( downCount > 0 && dragged == null )
                dragged = component;
            else if( downCount == 0 )
                dragged = null;

            if( release ){
                downCount--;
                if( downCount < 0 )
                    downCount = 0;
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
                
                if( focusController != null )
                    focusController.check( forward );
                
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
            Point mouse = e.getPoint();
            Component component = SwingUtilities.getDeepestComponentAt( contentPane, mouse.x, mouse.y );
            if( component != null ){
                mouse = SwingUtilities.convertPoint( this, mouse, component );
                MouseWheelEvent forward = new MouseWheelEvent( 
                        component, e.getID(), e.getWhen(), e.getModifiers(), 
                        mouse.x, mouse.y, e.getClickCount(), e.isPopupTrigger(), 
                        e.getScrollType(), e.getScrollAmount(), e.getWheelRotation() );
                
                if( focusController != null )
                    focusController.check( forward );
                
                component.dispatchEvent( forward );
            }
        }
    }
}