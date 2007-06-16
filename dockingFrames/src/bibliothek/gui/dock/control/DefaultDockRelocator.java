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
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.event.DockAdapter;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.MovingTitleGetter;
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
    private TitleWindow movingTitleWindow;
    /** the point where the mouse was pressed on the currently dragged title */
    private Point pressPointScreen;
    /** the point where the mouse was pressed on the currently dragged title */
    private Point pressPointLocal;
    
	/**
	 * Creates a new manager.
	 * @param controller the controller whose dockables are moved
	 */
	public DefaultDockRelocator( DockController controller ){
		super( controller );
		
		controller.getRegister().addDockRegisterListener( new MouseDockableListener() );
		controller.getRegister().addDockRegisterListener( new TitleListener() );
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
        final Set<DockTitle> oldTitles = new HashSet<DockTitle>();
        
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            private DockTitle exclude = movingTitleWindow == null ? null : movingTitleWindow.title;
            
            @Override
            public void handleDockable( Dockable dockable ) {
                for( DockTitle title : dockable.listBindedTitles() ){
                    if( title != exclude )
                        oldTitles.add( title );
                }
            }
        });
        
        DockController controller = getController();
        controller.getRegister().setStalled( true );
        try{
            if( station == null )
                throw new IllegalStateException( "There is no station to put the dockable." );
            
            DockStation parent = dockable.getDockParent();
            if( parent != station || parent == null ){
                fireDockableDrag( dockable, station );
                if( parent != null )
                    parent.drag( dockable );
                station.drop();
                controller.rebindTitles( dockable, oldTitles );
                fireDockablePut( dockable, station );
            }
            else{
                fireDockableDrag( dockable, parent );
                parent.move();
                fireDockablePut( dockable, parent );
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
        DockController controller = getController();
        
        for( DockStation station : list ){   
            if( dockable.getDockParent() == station ){
                // just a move
                if( station.prepareMove( mouseX, mouseY, titleX, titleY, dockable ) ){
                    return station;
                }
            }
            else{
                // perhaps a drop
                if( controller.getAcceptance().accept( station, dockable )){
                    if( station.accept( dockable ) && dockable.accept( station ) ){
                        if( station.prepareDrop( mouseX, mouseY, titleX, titleY, dockable )){
                            return station;
                        }
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
            if( movedStation == null || (!DockUtilities.isAnchestor( movedStation, station ) && movedStation != station )){
                if( station.isStationVisible() && station.getStationBounds().contains( x, y )){
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
        if( DockUtilities.isAnchestor( a, b ))
            return -1;
        
        if( DockUtilities.isAnchestor( b, a ))
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
            return Reaction.CONTINUE_CONSUMED;
        }
        else if( pressPointScreen != null ){
            titleDragCancel();
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
        
        if( dockable == null )
            dockable = title.getDockable();
        
        Point mouse = new Point( x, y );
        
        if( !onMove ){
            // not yet free
            if( !dockable.getDockParent().canDrag( dockable )){
                titleDragCancel();
                return Reaction.BREAK_CONSUMED;
            }
            
            int distance = Math.abs( x - pressPointScreen.x ) + Math.abs( y - pressPointScreen.y );
            if( always || distance >= getDragDistance() ){
                if( movingTitleWindow != null ){
                    // That means, that an old window was not closed correctly
                    movingTitleWindow.close();
                    movingTitleWindow = null;
                }
                
                movingTitleWindow = getTitleWindow( dockable, title );
                if( movingTitleWindow != null ){
                    updateTitleWindowPosition( mouse );
                    movingTitleWindow.setVisible( true );
                }
                
                onMove = true;
            }
        }
        if( onMove ){
            if( movingTitleWindow != null )
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
     * Updates the location of the {@link #movingTitleWindow} according
     * to the current location of the mouse.
     * @param mouse the location of the mouse
     */
    private void updateTitleWindowPosition( Point mouse ){
        int width = movingTitleWindow.getWidth();
        int height = movingTitleWindow.getHeight();
        
        int delta = Math.min( width, height ) + 1;
        
        int dx = Math.min( width, pressPointLocal.x );
        int dy = Math.min( height, pressPointLocal.y );
        
        movingTitleWindow.setLocation( mouse.x - dx + delta, mouse.y - dy + delta );
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
        int offmask = InputEvent.BUTTON1_DOWN_MASK |
            InputEvent.BUTTON2_DOWN_MASK |
            InputEvent.BUTTON3_DOWN_MASK;
        
        boolean stop = !onMove || ((modifiers & offmask) == 0);
        
        if( stop ){
            EventQueue.invokeLater( new Runnable(){
                public void run() {
                    onMove = false;
                }
            });
        }
        
        if( !onMove ){
            titleDragCancel();
            if( stop )
                return Reaction.BREAK_CONSUMED;
            else
                return Reaction.CONTINUE_CONSUMED;
        }
        boolean consume = false;

        if( pressPointScreen != null ){
            // local copy, some objects using the remote are invoking cancel
            // after the put has finished
            DockStation dragStation = this.dragStation;
            
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
            
            if( dragStation != null ){
                consume = true;
                executePut( dockable, dragStation );
                dragStation.forget();
                this.dragStation = null;
            }
        }
        
        if( movingTitleWindow != null )
            movingTitleWindow.close();
        
        movingTitleWindow = null;
        pressPointScreen = null;
        pressPointLocal = null;
        
        if( stop )
            return consume ? Reaction.BREAK_CONSUMED : Reaction.BREAK;
        else
            return consume ? Reaction.CONTINUE_CONSUMED : Reaction.CONTINUE;
    }
    
    /**
     * Cancels a drag and drop operation.
     */
    private void titleDragCancel(){
    	if( !isOnPut() ){
    		// if it is on but, than it is too late to stop
	        if( dragStation != null ){
	            dragStation.forget();
	            dragStation = null;
	        }
	        
	        if( movingTitleWindow != null )
	            movingTitleWindow.close();
	        
	        movingTitleWindow = null;
	        pressPointScreen = null;
	        pressPointLocal = null;
    	}
    }
    
    
    /**
     * Gets a window which shows a title of <code>dockable</code>. The
     * title on the window will be binded to <code>dockable</code>.
     * @param dockable the Dockable for which a title should be shown
     * @param title a title which is grabbed by the mouse, can be <code>null</code>
     * @return a window or <code>null</code>
     */
    private TitleWindow getTitleWindow( Dockable dockable, DockTitle title ){
    	DockController controller = getController();
        MovingTitleGetter movingTitleGetter = controller.getTheme().getMovingTitleGetter( controller );
        DockTitle windowTitle = title;
        
        if( windowTitle == null )
            windowTitle = movingTitleGetter.get( controller, dockable );
        else
            windowTitle = movingTitleGetter.get( controller, title );
        
        if( windowTitle == null )
            return null;
        
    	Window parent;
        if( title == null )
            parent = SwingUtilities.getWindowAncestor( dockable.getComponent() );
        else
            parent = SwingUtilities.getWindowAncestor( title.getComponent() );
        
        TitleWindow window = new TitleWindow( parent, windowTitle );
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
     * A listener to the set of known {@link Dockable Dockables}. 
     * Adds a {@link MouseListener} to all Dockables. This second listener allows
     * a popup-menu and connects the Dockables to the drag and drop mechanism.
     */
    private class MouseDockableListener extends DockControllerAdapter{
        /** tells which Dockable has which listener */
        private Map<Dockable, SingleMouseDockableListener> listeners =
            new HashMap<Dockable, SingleMouseDockableListener>();
        
        @Override
        public void dockableRegistered( DockController controller, Dockable dockable ) {
            if( !listeners.containsKey( dockable )){
                SingleMouseDockableListener listener = new SingleMouseDockableListener( dockable );
                dockable.addMouseInputListener( listener );
                listeners.put( dockable, listener );
            }
        }
        
        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
            SingleMouseDockableListener listener = listeners.remove( dockable );
            if( listener != null ){
                dockable.removeMouseInputListener( listener );
            }
        }
        
        
        /**
         * A listener to a Dockable, lets the user
         * drag and drop a Dockable.
         * @author Benjamin Sigg
         */
        private class SingleMouseDockableListener extends MouseInputAdapter{
            /** the observed element */
            private Dockable dockable;
            
            /**
             * Constructs a new listener
             * @param dockable the Dockable to observe
             */
            public SingleMouseDockableListener( Dockable dockable ){
                this.dockable = dockable;
            }
            
            @Override
            public void mousePressed( MouseEvent e ) {
                if( !e.isConsumed() ){
                	if( !isDragOnlyTitel() )
                		dragMousePressed( e, null, dockable );
                }
            }
            @Override
            public void mouseDragged( MouseEvent e ) {
                if( !e.isConsumed() ){
                	if( !isDragOnlyTitel() )
                		dragMouseDragged( e, null, dockable );
                }
            }
            @Override
            public void mouseReleased( MouseEvent e ) {
                if( !e.isConsumed() ){
                	if( !isDragOnlyTitel() )
                		dragMouseReleased( e, null, dockable );
                }
            }
        }
    }    
    
    /**
     * Observers this controller and registers listeners to all new titles.
     */
    private class TitleListener extends DockAdapter{
        /** a map telling which listener was added to which title */
        private Map<DockTitle, MouseTitleListener> listeners =
            new HashMap<DockTitle, MouseTitleListener>();

        @Override
        public void titleBinded( Dockable dockable, DockTitle title ) {
            if( !listeners.containsKey( title )){
                MouseTitleListener listener = new MouseTitleListener( title );
                listeners.put( title, listener );
            
                title.addMouseInputListener( listener );
            }
        }
        
        @Override
        public void titleUnbinded( Dockable dockable, DockTitle title ) {
            MouseTitleListener listener = listeners.remove( title );
            if( listener != null ){
                title.removeMouseInputListener( listener );
            }
        }

        @Override
        public void dockableRegistering( DockController controller, Dockable dockable ){
        	dockable.addDockableListener( this );
        }
        
        @Override
        public void dockableRegistered( DockController controller, Dockable dockable ) {
            DockTitle[] titles = dockable.listBindedTitles();
            for( DockTitle title : titles ){
                if( !listeners.containsKey( title )){
                    MouseTitleListener listener = new MouseTitleListener( title );
                    listeners.put( title, listener );
                    title.addMouseInputListener( listener );
                }
            }
        }

        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
            dockable.removeDockableListener( this );
        	
            DockTitle[] titles = dockable.listBindedTitles();
            for( DockTitle title : titles ){
                if( listeners.containsKey( title )){
                    MouseInputListener listener = listeners.remove( title );
                    title.removeMouseInputListener( listener );
                }
            }
        }
        
        /**
         * A {@link MouseListener} which is added to a {@link DockTitle}. This
         * listener informs a controller as soon as the mouse grabs the
         * title.
         * @author Benjamin Sigg
         */
        private class MouseTitleListener extends MouseInputAdapter{
            /** the observed title */
            private DockTitle title;
            
            /**
             * Creates a new listener
             * @param title the title to observe
             */
            public MouseTitleListener( DockTitle title ){
                this.title = title;
            }
            
            @Override
            public void mousePressed( MouseEvent e ){
                if( e.isConsumed() )
                    return;
                dragMousePressed( e, title, null );
            }
            @Override
            public void mouseReleased( MouseEvent e ) {
                if( e.isConsumed() )
                    return;
                dragMouseReleased( e, title, null );
            }
            @Override
            public void mouseDragged( MouseEvent e ) {
                if( e.isConsumed() )
                    return;
                dragMouseDragged( e, title, null );
            }
        }
    }    
    
    /**
     * A window which shows a single {@link DockTitle}.
     * @author Benjamin Sigg
     */
    private class TitleWindow extends JWindow{
        /** the title to display */
        private DockTitle title;
        /** whether the title was already binded when this window was constructed */
        private boolean binded;
        
        /**
         * Constructs a new window
         * @param parent the parent of the window
         * @param title the title to show, may be binded
         */
        public TitleWindow( Window parent, DockTitle title ){
            super( parent );
            
            Container content = getContentPane();
            content.setLayout( new GridLayout( 1, 1 ));
            setFocusableWindowState( false );
            
            try{
                setAlwaysOnTop( true );
            }
            catch( SecurityException ex ){
                // ignore
            }
            
            binded = getController().isBinded( title );

            if( binded && title.getOrigin() != null ){
                DockTitleVersion origin = title.getOrigin();
                DockTitle replacement = title.getDockable().getDockTitle( origin );
                if( replacement != null ){
                    replacement.setOrientation( title.getOrientation() );
                    title = replacement;
                    binded = false;
                }
            }
            
            if( !binded ){
                title.getDockable().bind( title );
                title.changed( new DockTitleEvent( title.getDockable(), true ));
                content.add( title.getComponent() );
            }
            else{
                /* TODO find a way to use the preferred size */
                Component c = title.getComponent();
                final Dimension size = c.getSize();
                final Image image = new BufferedImage(
                		Math.max( 1, size.width),
                		Math.max( 1, size.height),
                		BufferedImage.TYPE_INT_ARGB );
                Graphics graphics = image.getGraphics();
                c.paint( graphics );
                graphics.dispose();
                
                JComponent ground = new JComponent(){
                    @Override
                    public void paint( Graphics g ){
                        g.drawImage( image, 0, 0, this );
                        /*Component c = TitleWindow.this.title.getComponent();
                        Dimension size = c.getSize();
                        c.setSize( getWidth(), getHeight() );
                        c.validate();
                        c.paint( g );
                        c.setSize( size );
                        c.validate();*/
                    }
                    
                    @Override
                    public Dimension getPreferredSize() {
                        return size;
                        //return TitleWindow.this.title.getComponent().getPreferredSize();
                    }
                };
                
                content.add( ground );
            }
            
            this.title = title;
        }
        
        /**
         * Gets the title which is painted on this window
         * @return the title
         */
        public DockTitle getTitle() {
            return title;
        }
        
        /**
         * Closes this window and ensures that the title has the same
         * binding-state as it had at the time when this window was
         * constructed. 
         */
        public void close(){
            dispose();
            
            if( !binded ){
                Dockable dockable = title.getDockable();
                dockable.unbind(title);
            }
        }
    }
}
