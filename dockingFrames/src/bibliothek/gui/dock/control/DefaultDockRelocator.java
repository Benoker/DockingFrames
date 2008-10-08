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
package bibliothek.gui.dock.control;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.event.DockControllerRepresentativeListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * Default implementation of a handler that performs the drag & drop operations
 * for a {@link DockController}.
 * @author Benjamin Sigg
 */
public class DefaultDockRelocator extends DockRelocator{
	/** <code>true</code> as long as the user drags a title or a Dockable */
    private boolean onMove = false;
    /** <code>true</code> while a drag and drop-operation is performed */
    private boolean onPut = false;
    
	/** the current destination of a dragged dockable */
    private DockStation dragStation;
    /** a window painting a title onto the screen */
    private ImageWindow movingImageWindow;
    /** the point where the mouse was pressed on the currently dragged title */
    private Point pressPointScreen;
    /** the point where the mouse was pressed on the currently dragged title */
    private Point pressPointLocal;
    /** the location of the last mouse event */
    private Point lastPoint;
    
	/**
	 * Creates a new manager.
	 * @param controller the controller whose dockables are moved
	 * @param setup observable informing this object when <code>controller</code>
	 * is set up.
	 */
	public DefaultDockRelocator( DockController controller, ControllerSetupCollection setup ){
		super( controller );
		setup.add( new ControllerSetupListener(){
		    public void done( DockController controller ) {
		        controller.addRepresentativeListener( new Listener() );
		    }
		});
	}
	
	@Override
	public boolean isOnMove(){
        return onMove;
    }
    
	@Override
    public boolean isOnPut() {
        return onPut;
    }    
    
    @Override
    public DirectRemoteRelocator createDirectRemote( Dockable dockable ){
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        return new DefaultRemoteRelocator( dockable );        
    }
    
    @Override
    public RemoteRelocator createRemote( Dockable dockable ) {
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        return new DefaultRemoteRelocator( dockable );
    }
    
    /**
     * Executes a drag and drop event. <code>dockable</code> is removed
     * from its parent (if the parent is not <code>station</code>) and
     * dropped to <code>station</code>. The new location of
     * <code>dockable</code> has to be precomputed by <code>station</code>.
     * @param dockable a {@link Dockable} which is moved
     * @param station the new parent of <code>dockable</code>
     */
    protected void executePut( Dockable dockable, DockStation station ){
        onPut = true;
        DockController controller = getController();
        controller.getRegister().setStalled( true );
        disableAllModes();
        
        try{
            if( station == null )
                throw new IllegalStateException( "There is no station to put the dockable." );
            
            DockStation parent = dockable.getDockParent();
            if( parent != station || parent == null ){
                fireDrag( dockable, station );
                if( parent != null )
                    parent.drag( dockable );
                station.drop();
                fireDrop( dockable, station );
            }
            else{
                fireDrag( dockable, parent );
                parent.move();
                fireDrop( dockable, parent );
            }
        }
        finally{
            onPut = false;
            controller.getRegister().setStalled( false );
        }
    }    
    
    /**
     * Searches a station which can become the parent of <code>dockable</code> 
     * if the mouse is released at <code>mouseX/mouseY</code>.
     * @param mouseX x-coordinate of the mouse on the screen
     * @param mouseY y-coordinate of the mouse on the screen
     * @param titleX x-coordinate of the dragged title or mouseX
     * @param titleY y-coordinate of the dragged title or mouseY
     * @param dockable a Dockable which is dragged
     * @return the new parent of <code>dockable</code> or <code>null</code>
     */
    protected DockStation preparePut( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
        List<DockStation> list = listStationsOrdered( mouseX, mouseY, dockable );
        
        for( int i = 0; i < 2; i++ ){
            boolean checkOverrideZone = i == 0;
            
            for( DockStation station : list ){   
                if( dockable.getDockParent() == station ){
                    // just a move
                    if( station.prepareMove( mouseX, mouseY, titleX, titleY, checkOverrideZone, dockable ) ){
                        return station;
                    }
                }
                else{
                    // perhaps a drop
                    if( station.prepareDrop( mouseX, mouseY, titleX, titleY, checkOverrideZone, dockable )){
                        return station;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Makes a list of all stations which are visible and contain the point
     * <code>x/y</code>. The stations are ordered by their visibility.
     * @param x x-coordinate on the screen
     * @param y y-coordinate on the screen
     * @param moved a Dockable which is dragged. If this is a 
     * station, then no child of the station will be in the resulting list.
     * @return a list of stations
     */
    protected List<DockStation> listStationsOrdered( int x, int y, Dockable moved ){
        List<DockStation> result = new LinkedList<DockStation>();
        DockStation movedStation = moved.asDockStation();
        DockController controller = getController();
                
        for( DockStation station : controller.getRegister().listDockStations() ){   
            if( movedStation == null || (!DockUtilities.isAncestor( movedStation, station ) && movedStation != station )){
                if( station.isStationVisible() ){
                    Rectangle bounds = station.getStationBounds();
                    if( bounds == null || bounds.contains( x, y )){
                        int index = 0;
                        
                        // insertion sort
                        for( DockStation resultStation : result ){
                            int compare = compare( resultStation, station );
                            if( compare < 0 )
                                break;
                            else
                                index++;
                        }
                        
                        result.add( index, station );
                    }
                }
            }
        }        
        return result;
    }    
    
    /**
     * Tries to decide which station is over the other stations.
     * @param a the first station
     * @param b the second station
     * @return a number less/equal/greater than zero if
     * a is less/equal/more visible than b. 
     */
    protected int compare( DockStation a, DockStation b ){
        if( DockUtilities.isAncestor( a, b ))
            return -1;
        
        if( DockUtilities.isAncestor( b, a ))
            return 1;
        
        if( a.canCompare( b ))
            return a.compare( b );
        
        if( b.canCompare( a ))
            return -b.compare( a );
        
        Dockable dockA = a.asDockable();
        Dockable dockB = b.asDockable();
        
        if( dockA != null && dockB != null ){
            Component compA = dockA.getComponent();
            Component compB = dockB.getComponent();
            
            Window windowA = SwingUtilities.getWindowAncestor( compA );
            Window windowB = SwingUtilities.getWindowAncestor( compB );
            
            if( windowA != null && windowB != null ){
                if( isParent( windowA, windowB ))
                    return -1;
                
                if( isParent( windowB, windowA ))
                    return 1;
            }
        }
        return 0;
    }
        
    /**
     * Tells whether <code>parent</code> is really a parent of <code>child</code>
     * or not.
     * @param parent a window which may be an ancestor of <code>child</code>
     * @param child a window which may be child of <code>parent</code>
     * @return <code>true</code> if <code>parent</code> is an
     * ancestor of <code>child</code>
     */
    private boolean isParent( Window parent, Window child ){
        Window temp = child.getOwner();
        while( temp != null ){
            if( temp == parent )
                return true;
            
            temp = temp.getOwner();
        }
        
        return false;
    }
    
    /**
     * Invoked by the listeners of a title to start a drag and drop operation.
     * @param e the initializing event
     * @param title the grabbed title, can be <code>null</code> if
     * <code>dockable</code> is not <code>null</code>
     * @param dockable the grabbed Dockable, can be <code>null</code>
     * if <code>title</code> is not <code>null</code>
     */
    protected void dragMousePressed( MouseEvent e, DockTitle title, Dockable dockable ) {
        if( dockable == null )
            dockable = title.getDockable();
        
        Point point = e.getPoint();
        SwingUtilities.convertPointToScreen( point, e.getComponent() );
        Reaction reaction = dragMousePressed( point.x, point.y, e.getX(), e.getY(), e.getModifiersEx(), dockable );
        if( reaction == Reaction.BREAK_CONSUMED || reaction == Reaction.CONTINUE_CONSUMED )
            e.consume();
    }
    
    /**
     * Handles a mouse-pressed event.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @param dx the x-coordinate of the mouse on its component
     * @param dy the y-coordinate of the mouse on its component
     * @param modifiers the state of the mouse, see {@link MouseEvent#getModifiersEx()}
     * @param dockable the dockable which is moved around
     * @return how this relocator reacts on the event
     */
    protected Reaction dragMousePressed( int x, int y, int dx, int dy, int modifiers, Dockable dockable ){
        if( dockable.getDockParent() == null )
            return Reaction.BREAK;
        
        lastPoint = new Point( x, y );
        
        int onmask = InputEvent.BUTTON1_DOWN_MASK;
        int offmask = InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;
        
        if( ((modifiers & (onmask | offmask)) == onmask ) ){
            // cancel all pending operations
            // (should not be necessary, but there is no guarantee that no event gets lost)
            titleDragCancel();
            onMove = false;
            
            // initiate new operation
            pressPointScreen = new Point( x, y );
            pressPointLocal = new Point( dx, dy );
            checkModes( modifiers );
            return Reaction.CONTINUE;
        }
        else if( pressPointScreen != null ){
            titleDragCancel();
            disableAllModes();
            fireCancel( dockable );
            return Reaction.BREAK_CONSUMED;
        }
        return Reaction.BREAK;
    }
    
    /**
     * Invoked while the user drags a title or Dockable.
     * @param e the initializing event
     * @param title the grabbed title, can be <code>null</code> if
     * <code>dockable</code> is not <code>null</code>
     * @param dockable the grabbed Dockable, can be <code>null</code>
     * if <code>title</code> is not <code>null</code>
     */
    protected void dragMouseDragged( MouseEvent e, DockTitle title, Dockable dockable ) {
        Point point = e.getPoint();
        SwingUtilities.convertPointToScreen( point, e.getComponent() );
        Reaction reaction = dragMouseDragged( point.x, point.y, e.getModifiersEx(), title, dockable, false );
        if( reaction == Reaction.BREAK_CONSUMED || reaction == Reaction.CONTINUE_CONSUMED )
            e.consume();
    }
    
    /**
     * Handles a mouse-pressed event.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @param modifiers the state of the mouse, see {@link MouseEvent#getModifiersEx()}
     * @param title the title which might be grabbed by the mouse
     * @param dockable the dockable which is moved around
     * @param always <code>true</code> if the drag event should be executed and
     * restrictions to this relocator ignored.
     * @return how this relocator reacts on the event
     */
    protected Reaction dragMouseDragged( int x, int y, int modifiers, DockTitle title, Dockable dockable, boolean always ){
        if( pressPointScreen == null )
            return Reaction.BREAK;
        
        checkModes( modifiers );
        
        if( dockable == null )
            dockable = title.getDockable();
        
        Point mouse = new Point( x, y );
        lastPoint = new Point( x, y );
        
        if( !onMove ){
            // not yet free
            if( !dockable.getDockParent().canDrag( dockable )){
                titleDragCancel();
                disableAllModes();
                return Reaction.BREAK_CONSUMED;
            }
            
            int distance = Math.abs( x - pressPointScreen.x ) + Math.abs( y - pressPointScreen.y );
            if( always || distance >= getDragDistance() ){
                if( movingImageWindow != null ){
                    // That means, that an old window was not closed correctly
                    movingImageWindow.close();
                    movingImageWindow = null;
                }
                
                movingImageWindow = getTitleWindow( dockable, title );
                if( movingImageWindow != null ){
                    updateTitleWindowPosition( mouse );
                    movingImageWindow.setVisible( true );
                }
                
                onMove = true;
                fireInit( dockable );
            }
        }
        if( onMove ){
            if( movingImageWindow != null )
                updateTitleWindowPosition( mouse );
            
            DockStation next = preparePut( 
                    mouse.x, mouse.y,
                    mouse.x - pressPointLocal.x, mouse.y - pressPointLocal.y,
                    dockable );
            
            if( next != null ){
                next.draw();
            }
            
            if( next != dragStation ){
                if( dragStation != null ){
                    dragStation.forget();
                }
                dragStation = next;
            }
        }
        
        return Reaction.CONTINUE_CONSUMED;
    }
    
    /**
     * Updates the location of the {@link #movingImageWindow} according
     * to the current location of the mouse.
     * @param mouse the location of the mouse
     */
    private void updateTitleWindowPosition( Point mouse ){
        int width = Math.min( 25, movingImageWindow.getWidth());
        int height = Math.min( 25, movingImageWindow.getHeight());
        
        int delta = Math.min( width, height ) + 1;
        
        int dx = Math.min( width, pressPointLocal.x );
        int dy = Math.min( height, pressPointLocal.y );
        
        movingImageWindow.setLocation( mouse.x - dx + delta, mouse.y - dy + delta );
    }
    
    /**
     * Invoked while the user drags a title or Dockable and releases a mouse
     * button.
     * @param e the initializing event
     * @param title the grabbed title, can be <code>null</code> if
     * <code>dockable</code> is not <code>null</code>
     * @param dockable the grabbed Dockable, can be <code>null</code>
     * if <code>title</code> is not <code>null</code> 
     */
    protected void dragMouseReleased( MouseEvent e, DockTitle title, Dockable dockable ) {
        if( dockable == null )
            dockable = title.getDockable();
        
        Point point = e.getPoint();
        SwingUtilities.convertPointToScreen( point, e.getComponent() );
        Reaction reaction = dragMouseReleased( point.x, point.y, e.getModifiersEx(), dockable );
        if( reaction == Reaction.BREAK_CONSUMED || reaction == Reaction.CONTINUE_CONSUMED )
            e.consume();
    }
    
    /**
     * Handles a mouse-released event.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @param modifiers the state of the mouse, see {@link MouseEvent#getModifiersEx()}
     * @param dockable the dockable which is moved around
     * @return how this relocator reacts on the event
     */
    protected Reaction dragMouseReleased( int x, int y, int modifiers, Dockable dockable ){
        checkModes( modifiers );
        int offmask = InputEvent.BUTTON1_DOWN_MASK |
            InputEvent.BUTTON2_DOWN_MASK |
            InputEvent.BUTTON3_DOWN_MASK;
        
        boolean stop = !onMove || ((modifiers & offmask) == 0);
        
        if( stop && onMove ){
            EventQueue.invokeLater( new Runnable(){
                public void run() {
                    onMove = false;
                }
            });
        }
        
        if( !onMove ){
            boolean wasDragging = pressPointScreen != null;
            titleDragCancel();
            disableAllModes();
            
            if( stop ){
                fireCancel( dockable );
                
                if( wasDragging )
                    return Reaction.BREAK_CONSUMED;
                else
                    return Reaction.BREAK;
            }
            else
                return Reaction.CONTINUE_CONSUMED;
        }
        boolean consume = false;

        if( stop ){
            if( pressPointScreen != null ){
                // local copy, some objects using the remote are invoking cancel
                // after the put has finished
                DockStation dragStation = this.dragStation;

                if( x != lastPoint.x || y != lastPoint.y ){
                    DockStation next = preparePut( 
                            x, y,
                            x - pressPointLocal.x, y - pressPointLocal.y,
                            dockable );

                    if( next != dragStation ){
                        if( dragStation != null ){
                            dragStation.forget();
                        }
                        dragStation = next;
                    }
                }

                if( dragStation != null ){
                    consume = true;
                    executePut( dockable, dragStation );
                    dragStation.forget();
                    this.dragStation = null;
                }
            }

            if( movingImageWindow != null )
                movingImageWindow.close();

            movingImageWindow = null;
            pressPointScreen = null;
            pressPointLocal = null;
        }
        
        if( stop ){
            disableAllModes();
            return consume ? Reaction.BREAK_CONSUMED : Reaction.BREAK;
        }
        else
            return consume ? Reaction.CONTINUE_CONSUMED : Reaction.CONTINUE;
    }
    
    /**
     * Cancels a drag and drop operation.
     */
    private void titleDragCancel(){
    	if( !isOnPut() ){
    		// if it is on put, than it is too late to stop
	        if( dragStation != null ){
	            dragStation.forget();
	            dragStation = null;
	        }
	        
	        if( movingImageWindow != null )
	            movingImageWindow.close();
	        
	        movingImageWindow = null;
	        pressPointScreen = null;
	        pressPointLocal = null;
    	}
    }
    
    
    /**
     * Gets a window which shows a title of <code>dockable</code>. The
     * title on the window will be bound to <code>dockable</code>.
     * @param dockable the Dockable for which a title should be shown
     * @param title a title which is grabbed by the mouse, can be <code>null</code>
     * @return a window or <code>null</code>
     */
    private ImageWindow getTitleWindow( Dockable dockable, DockTitle title ){
    	DockController controller = getController();
        DockableMovingImageFactory factory = controller.getTheme().getMovingImageFactory( controller );
        MovingImage image;
        
        if( title == null )
            image = factory.create( controller, dockable );
        else
            image = factory.create( controller, title );
        
        if( image == null )
            return null;
        
    	Window parent;
        if( title == null )
            parent = SwingUtilities.getWindowAncestor( dockable.getComponent() );
        else
            parent = SwingUtilities.getWindowAncestor( title.getComponent() );
        
        ImageWindow window = new ImageWindow( parent, image );
        window.pack();
        return window;
    }
    
    /**
     * An implementation connecting a {@link RemoteRelocator} to the
     * enclosing {@link DefaultDockRelocator}.
     * @author Benjamin Sigg
     */
    private class DefaultRemoteRelocator implements RemoteRelocator, DirectRemoteRelocator{
        /** the Dockable which might be moved by this relocator */
        private Dockable dockable;
        
        /**
         * Creates a new remote
         * @param dockable the dockable which might be moved
         */
        public DefaultRemoteRelocator( Dockable dockable ){
            this.dockable = dockable;
        }
        
        public void cancel() {
            titleDragCancel();
            onMove = false;
        }

        public void drag( int x, int y, boolean always ) {
            dragMouseDragged( x, y, InputEvent.BUTTON1_DOWN_MASK, null, dockable, always );
        }
        
        public Reaction drag( int x, int y, int modifiers ) {
            return dragMouseDragged( x, y, modifiers, null, dockable, false );
        }

        public void drop( int x, int y ) {
            drop( x, y, 0 );
        }
        
        public Reaction drop( int x, int y, int modifiers ) {
            return dragMouseReleased( x, y, modifiers, dockable );
        }

        public void init( int x, int y, int dx, int dy ) {
            init( x, y, dx, dy, InputEvent.BUTTON1_DOWN_MASK );
        }
        
        public Reaction init( int x, int y, int dx, int dy, int modifiers ) {
            return dragMousePressed( x, y, dx, dy, modifiers, dockable );
        }
    }
    
    /**
     * A listener observing the {@link DockController} for new {@link DockElementRepresentative}s
     * and adding the new representatives a {@link MouseRepresentativeListener}.
     * @author Benjamin Sigg
     */
    private class Listener implements DockControllerRepresentativeListener{
        private Map<DockElementRepresentative, MouseRepresentativeListener> listeners =
            new HashMap<DockElementRepresentative, MouseRepresentativeListener>();
        
        public void representativeAdded( DockController controller,
                DockElementRepresentative representative ) {
         
            if( representative.getElement().asDockable() != null ){
                MouseRepresentativeListener listener = new MouseRepresentativeListener( representative );
                listeners.put( representative, listener );
                representative.addMouseInputListener( listener );
            }
        }
        public void representativeRemoved( DockController controller,
                DockElementRepresentative representative ) {
            
            if( representative.getElement().asDockable() != null ){
                MouseRepresentativeListener listener = listeners.remove( representative );
                if( listener != null ){
                    representative.removeMouseInputListener( listener );
                }
            }
        }
    }
    
    /**
     * A listener that can be added to a {@link DockElementRepresentative}. Will
     * forward {@link MouseEvent}s to the appropriate method of this relocator.
     * @author Benjamin Sigg
     */
    private class MouseRepresentativeListener extends MouseInputAdapter{
        /** the title which is observed by this listener */
        private DockTitle title;
        /** the dockable which might be moved around by this listener */
        private Dockable dockable;
        
        /** the current element that is observed */
        private DockElementRepresentative representative;
        
        /**
         * Creates a new listener
         * @param representative the element which will be observed
         */
        public MouseRepresentativeListener( DockElementRepresentative representative ){
            this.representative = representative;
            
            if( representative instanceof DockTitle )
                title = (DockTitle)representative;
            
            dockable = representative.getElement().asDockable();
        }
        
        @Override
        public void mousePressed( MouseEvent e ){
            if( e.isConsumed() )
                return;
            if( !representative.isUsedAsTitle() && isDragOnlyTitel() )
                return;
            dragMousePressed( e, title, dockable );
        }
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( e.isConsumed() )
                return;
            if( !representative.isUsedAsTitle() && isDragOnlyTitel() )
                return;
            dragMouseReleased( e, title, dockable );
        }
        @Override
        public void mouseDragged( MouseEvent e ) {
            if( e.isConsumed() )
                return;
            if( !representative.isUsedAsTitle() && isDragOnlyTitel() )
                return;
            dragMouseDragged( e, title, dockable );
        }
    }
    
    /**
     * A window which shows a single {@link DockTitle}.
     * @author Benjamin Sigg
     */
    private class ImageWindow extends JWindow{
        /** the image to display */
        private MovingImage image;
        
        /**
         * Constructs a new window
         * @param parent the parent of the window
         * @param image the image to display
         */
        public ImageWindow( Window parent, MovingImage image ){
            super( parent );
            
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
            		if( ImageWindow.this != movingImageWindow ){
            			// that should really not be possible...
            			SwingUtilities.invokeLater( new Runnable(){
            				public void run(){
            					close();
            				}
            			});
            		}					
				}
            	
            });
            
            Container content = getContentPane();
            content.setLayout( new GridLayout( 1, 1 ));
            setFocusableWindowState( false );
            
            try{
                setAlwaysOnTop( true );
            }
            catch( SecurityException ex ){
                // ignore
            }
            
            image.bind();
            content.add( image.getComponent() );
            this.image = image;
        }
        
        /**
         * Closes this window and ensures that the title has the same
         * binding-state as it had at the time when this window was
         * constructed. 
         */
        public void close(){
            dispose();
            if( image != null ){
                image.unbind();
                image = null;
            }
            getContentPane().removeAll();
        }
    }
}
