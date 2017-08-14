/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of 
import bibliothek.util.Path;
the GNU Lesser General Public
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
package bibliothek.gui.dock.control.relocator;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.control.ControllerSetupCollection;
import bibliothek.gui.dock.control.DirectRemoteRelocator;
import bibliothek.gui.dock.control.GlobalMouseDispatcher;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.event.ControllerSetupListener;
import bibliothek.gui.dock.event.DockControllerRepresentativeListener;
import bibliothek.gui.dock.station.StationDragOperation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.layer.DockStationDropLayerFactory;
import bibliothek.gui.dock.station.layer.OrderedLayerCollection;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.ClientOnly;
import bibliothek.util.Path;
import bibliothek.util.Workarounds;

/**
 * Default implementation of a handler that performs the {@literal drag & drop} operations
 * for a {@link DockController}.
 * @author Benjamin Sigg
 */
public class DefaultDockRelocator extends AbstractDockRelocator{
	/**
	 * If <code>true</code>, then any mouse-released event may stop a drag-and-drop operation.<br> 
	 * If <code>false</code>, then only mouse-released events that originate from the same {@link Component}
	 * as the mouse-pressed event that started the operation may cancel it.<br>
	 * The default value is <code>true</code>.
	 */
	public static final PropertyKey<Boolean> AUTO_DROP_ON_ANY_MOUSE_RELEASED_EVENT = 
			new PropertyKey<Boolean>( "dock.default.relocator.autodrop", 
					new ConstantPropertyFactory<Boolean>( true ), true );
	
	/** Path of an {@link ExtensionName} that adds new {@link Merger}s */
	public static final Path MERGE_EXTENSION = new Path( "dock.merger" );
	
	/** Path of an {@link ExtensionName} that adds new {@link Inserter}s */
	public static final Path INSERTER_EXTENSION = new Path( "dock.inserter" );
	
	/** Name of a parameter of an {@link ExtensionName} pointing to <code>this</code> */
	public static final String EXTENSION_PARAM = "relocator";
	
	/** <code>true</code> as long as the user drags a title or a Dockable */
    private boolean onMove = false;
    /** <code>true</code> while a drag and drop-operation is performed */
    private boolean onPut = false;
    
	/** the current destination of a dragged dockable */
    private RelocateOperation operation;
    
    /** the current parent of a dragged dockable */
    private StationDragOperation dragOperation;
    
    /** a window painting a title onto the screen */
    private ImageWindow movingImageWindow;
    /** the point where the mouse was pressed on the currently dragged title */
    private Point pressPointScreen;
    /** the point where the mouse was pressed on the currently dragged title */
    private Point pressPointLocal;
    /** the location of the last mouse event */
    private Point lastPoint;
    
    /** information about the last dragged dockable */
    private StationDropItem lastItem;
    
    /** The {@link DockControllerRepresentativeListener} that creates all the {@link MouseListener}s */
    private Listener listeners;
    
	/**
	 * Creates a new manager.
	 * @param controller the controller whose dockables are moved
	 * @param setup observable informing this object when <code>controller</code>
	 * is set up.
	 */
	public DefaultDockRelocator( DockController controller, ControllerSetupCollection setup ){
		super( controller );
		
		final MultiMerger merger = new MultiMerger();
		merger.add( new StackMerger() );
		merger.add( new TabMerger() );
		
		final MultiInserter inserter = new MultiInserter();
		
		listeners = new Listener();
		
		setup.add( new ControllerSetupListener(){
		    public void done( DockController controller ) {
		        controller.addRepresentativeListener( listeners );
		        
		        List<Merger> mergers = controller.getExtensions().load( new ExtensionName<Merger>( MERGE_EXTENSION, Merger.class, EXTENSION_PARAM, DefaultDockRelocator.this ));
				for( Merger next : mergers ){
					merger.add( next );
				}
				
				List<Inserter> inserters = controller.getExtensions().load( new ExtensionName<Inserter>( INSERTER_EXTENSION, Inserter.class, EXTENSION_PARAM, DefaultDockRelocator.this ));
				for( Inserter next : inserters ){
					inserter.add( next );
				}
				
				GlobalMouseReleaseListener globalMouseReleaseListener = new GlobalMouseReleaseListener();
				globalMouseReleaseListener.link();
		    }
		});
		
		setMerger( merger );
		setInserter( inserter );
	}
	
	public boolean isOnMove(){
        return onMove;
    }
    
    public boolean isOnPut() {
        return onPut;
    }    
    
    public boolean hasTarget(){
    	return operation != null;
    }
    
    public DirectRemoteRelocator createDirectRemote( Dockable dockable ){
    	return createDirectRemote( dockable, false );
    }
    
    public DirectRemoteRelocator createDirectRemote( Dockable dockable, boolean forceDrag ){
    	if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        return new DefaultRemoteRelocator( dockable, forceDrag );
    }
    
    public RemoteRelocator createRemote( Dockable dockable ) {
        return createRemote( dockable, false );
    }
    
    public RemoteRelocator createRemote( Dockable dockable, boolean forceDrag ){
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        return new DefaultRemoteRelocator( dockable, forceDrag );
    }
    
    /**
     * Executes the drag and drop event <code>operation</code>.
     * @param dockable a {@link Dockable} which is moved
     * @param operation the operation to execute
     * @return <code>true</code> if the operation was a success, <code>false</code> if the operation was canceled
     */
    protected boolean executeOperation( Dockable dockable, RelocateOperation operation ){
        onPut = true;
        DockController controller = getController();
        controller.getRegister().setStalled( true );
        disableAllModes();
        
        try{
        	boolean success = operation.execute( dockable, new VetoableDockRelocatorListener(){
				public void searched( DockRelocatorEvent event ){
					throw new IllegalStateException( "this event must not be called from an operation" );
				}
				
				public void grabbing( DockRelocatorEvent event ){
					throw new IllegalStateException( "this event must not be called from an operation" );
				}
				
				public void grabbed( DockRelocatorEvent event ){
					throw new IllegalStateException( "this event must not be called from an operation" );
				}
				
				public void dropping( DockRelocatorEvent event ){
					throw new IllegalStateException( "this event must not be called from an operation" );
				}
				
				public void dropped( DockRelocatorEvent event ){
					throw new IllegalStateException( "this event must not be called from an operation" );
				}
				
				public void dragging( DockRelocatorEvent event ){
					fireDragging( event );	
				}
				
				public void dragged( DockRelocatorEvent event ){
					fireDragged( event );
				}
				
				public void canceled( DockRelocatorEvent event ){
					throw new IllegalStateException( "this event must not be called from an operation" );
				}
			});
        	if( dragOperation != null ){
	        	if( success ){
	        		dragOperation.succeeded();
	        	}
	        	else{
	        		dragOperation.canceled();
	        	}
	        	dragOperation = null;
        	}
        	return success;
        }
        finally{
        	operation.destroy( null );
        	operation = null;
            onPut = false;
            controller.getRegister().setStalled( false );
        }
    }    
    
    private StationDropItem createStationDropItem( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
    	if( lastItem == null || lastItem.getDockable() != dockable ){
    		lastItem = new StationDropItem( mouseX, mouseY, titleX, titleY, dockable );
    	}
    	else{
    		lastItem = new StationDropItem( mouseX, mouseY, titleX, titleY, dockable, lastItem.getOriginalSize(), lastItem.getMinimumSize() );
    	}
    	return lastItem;
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
    protected RelocateOperation preparePut( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
        List<DockStation> list = listStationsOrdered( mouseX, mouseY, dockable );

        Inserter inserter = getInserter();
        StationDropItem item = createStationDropItem( mouseX, mouseY, titleX, titleY, dockable );

        for( DockStation station : list ){
        	StationDropOperation operation = null;
        	DefaultInserterSource inserterSource = new DefaultInserterSource( station, item );
        	
        	if( inserter != null ){
        		operation = inserter.before( inserterSource );
        	}
        	if( operation == null ){
        		operation = station.prepareDrop( item );
        		if( inserter != null ){
        			inserterSource.setOperation( operation );
        			operation = inserter.after( inserterSource );
        			if( operation == null ){
        				operation = inserterSource.getOperation();
        			}
        		}
        	}
        	
        	RelocateOperation result = null;
        	
        	boolean merge = canMerge( operation, station, dockable );

        	if( operation != null ){
        		if( merge ){
        			result = new MergeOperation( getController(), getMerger(), station, operation, item );
        		}
        		else{
        			result = new DropOperation( getController(), station, operation, item );
        		}
        	}
        	
        	if( result != null ){
        		boolean move = result.getOperation().isMove();;
	        	DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, result.getImplicit( dockable ), station, new Point( mouseX, mouseY ), move );
	        	fireSearched( event );
	        	
	        	if( event.isForbidden() ){
	        		result = null;
	        	}
	        	else if( event.isCanceled() ){
	        		cancel();
	        		return null;
	        	}
	        	if( result != null ){
	        		return result;
	        	}
        	}
        }
        
        return null;
    }
    
    /**
     * Checks whether the current {@link #getMerger() Merger} can merge <code>parent</code>
     * with <code>child</code>.
     * @param operation the operation that would be expected
     * @param parent the new parent for the children of <code>child</code>
     * @param selection the element whose children are to be removed
     * @return <code>true</code> if a merge is possible
     */
    protected boolean canMerge( StationDropOperation operation, DockStation parent, Dockable selection ){
    	Merger merger = getMerger();
    	if( merger == null ){
    		return false;
    	}
    	
    	DockStation child = selection.asDockStation();
    	if( child == null ){
    		return false;
    	}
    	if( DockUtilities.isAncestor( child, parent )){
    		return false;
    	}
    	
    	if( selection.getDockParent() != null ){
    		if( !selection.getDockParent().canDrag( selection )){
    			return false;
    		}
    	}
    	
    	DockAcceptance acceptance = getController().getAcceptance();
    	
    	for( int i = 0, n = child.getDockableCount(); i<n; i++ ){
    		Dockable dockable = child.getDockable( i );
    		if( !child.canDrag( dockable )){
    			return false;
    		}
    		if( !parent.accept( dockable ) || !dockable.accept( parent )){
    			return false;
    		}
    		if( !acceptance.accept( parent, dockable )){
    			return false;
    		}
    	}
    	
    	return merger.canMerge( operation, parent, child );
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
    	DockController controller = getController();
    	DisablingStrategy disabling = controller.getProperties().get( DisablingStrategy.STRATEGY );
    	DockStationDropLayerFactory dropLayerFactory = controller.getProperties().get( DockStationDropLayerFactory.DROP_LAYER_FACTORY );
    	
    	OrderedLayerCollection collection = new OrderedLayerCollection( dropLayerFactory );
    	
    	if( disabling == null || !disabling.isDisabled( moved )){
	        DockStation movedStation = moved.asDockStation();
	        if( !isCancelLocation( x, y, moved )){
	        	for( DockStation station : controller.getRegister().listDockStations() ){
	        		if( disabling == null || !disabling.isDisabled( station )){
		        		if( movedStation == null || (!DockUtilities.isAncestor( movedStation, station ) && movedStation != station )){
		        			if( station.isStationShowing() && isStationValid( station ) ){
		        				collection.add( station );
			                }
			            }
	        		}
		        }
	        }
    	}
	    return collection.sort( x, y );
    }
    
    /**
     * Checks whether the mouse is at a location that cancels a drag and drop operation. This method just calls
     * {@link #isCancelLocation(int, int, DockElementRepresentative)} with all the {@link DockElementRepresentative}
     * that can be found for <code>moved</code>.
     * @param x x-coordinate on the screen
     * @param y y-coordinate on the screen
     * @param moved the item that was moved around
     * @return <code>true</code> if the current location can never result in a valid drop operation
     */
    protected boolean isCancelLocation( int x, int y, Dockable moved ){
    	if( isCancelLocation( x, y, (DockElementRepresentative)moved )){
    		return true;
    	}
    	DockController controller = moved.getController();
    	for( DockElementRepresentative item : controller.getRepresentatives( moved ) ){
    		if( isCancelLocation( x, y, item )){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Checks whether the mouse is at a location that cancels a drag and drop operation. The default implementation
     * of this method always returns <code>false</code>.
     * @param x x-coordinate on the screen
     * @param y y-coordinate on the screen
     * @param item the item that was moved around
     * @return <code>true</code> if the current location can never result in a valid drop operation
     */
    protected boolean isCancelLocation( int x, int y, DockElementRepresentative item ){
    	return false;
    }
    
    /**
     * Only stations passing this test are considered during drag and drop operation as new parent. Subclasses 
     * may override this method.
     * @param station the station to check
     * @return <code>true</code> if <code>station</code> should be considered as new parent
     */
    @ClientOnly
    protected boolean isStationValid( DockStation station ){
    	return true;
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
        Reaction reaction = dragMousePressed( point.x, point.y, e.getX(), e.getY(), e.getModifiersEx(), dockable, false );
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
     * @param forceDrag if this flag is set to <code>true</code>, then dragging will always start even
     * if one of the usual conditions is not met. I.e. dragging will start even if <code>dockable</code>
     * does not have a parent of even if the parent does not allow dragging. This flag should be used
     * with caution.
     * @return how this relocator reacts on the event
     */
    protected Reaction dragMousePressed( int x, int y, int dx, int dy, int modifiers, Dockable dockable, boolean forceDrag ){
    	listeners.unsetLastActiveListener();
    	
        if( !forceDrag && dockable.getDockParent() == null )
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
            
            Dockable[] implicit = operation == null ? new Dockable[]{} : operation.getImplicit( dockable );
            boolean move = operation != null && operation.getOperation().isMove();
            DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, implicit, operation == null ? null : operation.getStation(), new Point( x, y ), move );
            event.cancel();
            fireCanceled( event );
            
            return onBreak( event );
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
        Reaction reaction = dragMouseDragged( point.x, point.y, e.getModifiersEx(), title, dockable, false, false );
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
     * @param forceDrag if this flag is set to <code>true</code>, then dragging will always start even
     * if one of the usual conditions is not met. I.e. dragging will start even if <code>dockable</code>
     * does not have a parent of even if the parent does not allow dragging. This flag should be used
     * with caution.
     * @return how this relocator reacts on the event
     */
    protected Reaction dragMouseDragged( int x, int y, int modifiers, DockTitle title, Dockable dockable, boolean always, boolean forceDrag ){
    	return dragMouseDragged( x, y, modifiers, title, dockable, always, forceDrag, true );
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
     * @param forceDrag if this flag is set to <code>true</code>, then dragging will always start even
     * if one of the usual conditions is not met. I.e. dragging will start even if <code>dockable</code>
     * does not have a parent of even if the parent does not allow dragging. This flag should be used
     * with caution.
     * @param showMovingImage whether to show a {@link MovingImage}
     * @return how this relocator reacts on the event
     */
    protected Reaction dragMouseDragged( int x, int y, int modifiers, DockTitle title, Dockable dockable, boolean always, boolean forceDrag, boolean showMovingImage ){
    	listeners.unsetLastActiveListener();
    	
        if( pressPointScreen == null )
            return Reaction.BREAK;
        
        checkModes( modifiers );
        
        if( dockable == null )
            dockable = title.getDockable();
        
        if( dockable == null ){
        	return Reaction.BREAK;
        }
        
        Point mouse = new Point( x, y );
        lastPoint = new Point( x, y );
        
        if( !onMove ){
            // cannot start, dockable is cannot be dragged
        	DockStation parent = dockable.getDockParent();
            if( !forceDrag && parent != null && !parent.canDrag( dockable )){
                titleDragCancel();
                disableAllModes();
                return Reaction.BREAK;
            }
            
            int distance = Math.abs( x - pressPointScreen.x ) + Math.abs( y - pressPointScreen.y );
            if( always || distance >= getDragDistance() ){
            	Reaction result = initiateOperation( dockable, title, mouse, showMovingImage );
            	if( !onMove && result != null ){
            		return result;
            	}
            }
        }
        if( onMove ){
            Reaction result = selectNextTarget( dockable, title, mouse );
            if( result != null ){
            	return result;
            }
        }
        
        return Reaction.CONTINUE;
    }
    
    private Reaction initiateOperation( Dockable dockable, DockTitle title, Point mouse, boolean showMovingImage ){
    	if( movingImageWindow != null ){
            // That means, that an old window was not closed correctly
            movingImageWindow.close();
            movingImageWindow = null;
        }
    	lastItem = null;
    	
    	Dockable[] implicit = new Dockable[]{};
    	DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, implicit, null, mouse, false );
    	fireGrabbing( event );
    	if( event.isIgnored() ){
    		return Reaction.CONTINUE;
    	}
    	if( event.isCanceled() ){
    		event = new DefaultDockRelocatorEvent( getController(), dockable, implicit, null, mouse, false );
    		event.cancel();
    		fireCanceled( event );
    		return onBreak( event );
    	}
    	if( !event.isForbidden() ){
    		if( showMovingImage ){
	        	movingImageWindow = getTitleWindow( dockable, title );
	            if( movingImageWindow != null ){
	                updateTitleWindowPosition( mouse );
	                movingImageWindow.setVisible( true );
	            }
    		}
            
            onMove = true;
            
            DockStation parent = dockable.getDockParent();
            if( dragOperation != null ){
        		dragOperation.canceled();
        		dragOperation = null;
        	}
            if( parent != null ){
            	dragOperation = parent.prepareDrag( dockable );
            }
            
            event = new DefaultDockRelocatorEvent( getController(), dockable, implicit, null, mouse, false );
            fireGrabbed( event );
            if( event.isCanceled() || event.isForbidden() ){
            	cancel( dockable );
            	return Reaction.BREAK_CONSUMED;
            }
            return onContinue( event );
    	}
    	return null;
    }
    
    private Reaction selectNextTarget( Dockable dockable, DockTitle title, Point mouse ){
    	if( movingImageWindow != null )
            updateTitleWindowPosition( mouse );
        
        RelocateOperation next = preparePut( 
                mouse.x, mouse.y,
                mouse.x - pressPointLocal.x, mouse.y - pressPointLocal.y,
                dockable );
        
        boolean drop = false;

        Dockable[] implicit = next == null ? new Dockable[]{} : next.getImplicit( dockable );
        boolean move = next != null && next.getOperation().isMove();
    	DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, implicit, next == null ? null : next.getStation(), mouse, move );
    	fireDragged( event );
    	if( event.isCanceled() ){
    		cancel( dockable );
    		return onBreak( event );
    	}
    	else if( event.isForbidden() ){
    		next = null;
    	}
    	else if( event.isIgnored() ){
    		return Reaction.CONTINUE;
    	}
        
        if( next != null ){
            drop = next != null && event.isDropping();
        }
        
        if( drop ){
        	return dragMouseReleased( mouse.x, mouse.y, 0, dockable );
        }
        else{
            if( operation != null ){
	            operation.destroy( next );
            }
            
            this.operation = next;
            
            if( next != null ){
            	next.getOperation().draw();
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
    	MovingImage image = movingImageWindow.getImage();
    	Point offset = null;
    	if( image != null ){
    		offset = image.getOffset( new Point( pressPointLocal ) );
    	}
    	
    	if( offset == null ){
	    	int width = Math.min( 25, movingImageWindow.getWidth());
	        int height = Math.min( 25, movingImageWindow.getHeight());
	        
	        int delta = Math.min( width, height ) + 1;
	        
	        int dx = Math.min( width, pressPointLocal.x );
	        int dy = Math.min( height, pressPointLocal.y );
	        
	        movingImageWindow.setLocation( mouse.x - dx + delta, mouse.y - dy + delta );
    	}
    	else{
    		movingImageWindow.setLocation( mouse.x + offset.x, mouse.y + offset.y );
    	}
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
    	listeners.unsetLastActiveListener();
    	
        checkModes( modifiers );
        int offmask = InputEvent.BUTTON1_DOWN_MASK |
            InputEvent.BUTTON2_DOWN_MASK |
            InputEvent.BUTTON3_DOWN_MASK;
        
        boolean stop = !onMove || ((modifiers & offmask) == 0);
        Point mouse = new Point( x, y );
        
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
            	DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, new Dockable[]{}, null, mouse, false );
            	event.cancel();
            	fireCanceled( event );
                
                if( wasDragging )
                    return Reaction.BREAK_CONSUMED;
                else
                    return Reaction.BREAK;
            }
            else
                return Reaction.CONTINUE_CONSUMED;
        }
        boolean consume = false;

        DefaultDockRelocatorEvent dropped = null;
        
        if( stop ){
            if( pressPointScreen != null ){
                // local copy, some objects using the remote are invoking cancel
                // after the put has finished
                RelocateOperation operation = this.operation;
                
                if( x != lastPoint.x || y != lastPoint.y ){
                	RelocateOperation next = preparePut( x, y, x - pressPointLocal.x, y - pressPointLocal.y, dockable );
                	
                    if( next != null ){
                    	Dockable[] implicit = next.getImplicit( dockable );
                    	boolean move = next.getOperation().isMove();
                    	DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, implicit, next.getStation(), mouse, move );
                    	fireCanceled( event );
                    	if( event.isCanceled() || event.isForbidden() ){
                    		next = null;
                    	}
                    	else if( event.isIgnored() ){
                    		return Reaction.CONTINUE;
                    	}
                    }

                    if( operation != null && (next == null || operation.getStation() != next.getStation() )){
                        operation.destroy( next );
                    }
                    
                    operation = next;
                }

                if( operation != null ){
                	Dockable[] implicit = operation.getImplicit( dockable );
                	boolean move = operation.getOperation().isMove();
                	DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, implicit, operation.getStation(), mouse, move );
                	event.drop();
                	fireDropping( event );
                	if( event.isCanceled() || event.isForbidden() ){
                		operation.destroy( null );
                		operation = null;
                	}
                	else if( event.isIgnored() ){
                		return Reaction.CONTINUE;
                	}
                }
                
                if( operation != null ){
                	Dockable[] implicit = operation.getImplicit( dockable );
                    boolean canceled = !executeOperation( dockable, operation );
                    operation.destroy( null );
                    this.operation = null;
                    
                    dropped = new DefaultDockRelocatorEvent( getController(), dockable, implicit, operation.getStation(), mouse, operation.getOperation().isMove() );
                    if( canceled ){
                    	dropped.cancel();
                    	fireCanceled( dropped );
                    	dropped = null;
                    }
                    consume = true;
                }
                else{
                	DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, new Dockable[]{}, null, mouse, false );
                	event.cancel();
                	fireCanceled( event );
                }
            }

            if( movingImageWindow != null )
                movingImageWindow.close();
            
            lastItem = null;
            
            if( dragOperation != null ){
            	dragOperation.canceled();
            	dragOperation = null;
            }

            movingImageWindow = null;
            pressPointScreen = null;
            pressPointLocal = null;
        }
        
        if( dropped != null ){
        	fireDropped( dropped );
        }
        
        if( stop ){
            disableAllModes();
            return consume ? Reaction.BREAK_CONSUMED : Reaction.BREAK;
        }
        else
            return consume ? Reaction.CONTINUE_CONSUMED : Reaction.CONTINUE;
    }
    
    private Reaction onBreak( DefaultDockRelocatorEvent event ){
    	if( event.isIgnored() ){
    		return Reaction.BREAK;
    	}
    	return Reaction.BREAK_CONSUMED;
    }
    
    private Reaction onContinue( DefaultDockRelocatorEvent event ){
    	if( event.isIgnored() ){
    		return Reaction.CONTINUE;
    	}
    	return Reaction.CONTINUE_CONSUMED;
    }
    
    public void cancel(){
    	if( operation != null ){
    		cancel( operation.getOperation().getItem() );
    	}
    }
    
    /**
     * Cancels the current drag and drop operation and fires events
     * @param dockable the element whose operation is canceled
     */
    private void cancel(Dockable dockable){
    	titleDragCancel();
    	onMove = false;
    	DefaultDockRelocatorEvent event = new DefaultDockRelocatorEvent( getController(), dockable, new Dockable[]{}, null, null, false );
		event.cancel();
		fireCanceled( event );
    }
    
    /**
     * Cancels a drag and drop operation.
     */
    private void titleDragCancel(){
    	if( !isOnPut() ){
    		// if it is on put, than it is too late to stop
	        if( operation != null ){
	            operation.destroy( null );
	            operation = null;
	        }
	        
	        if( dragOperation != null ){
	        	dragOperation.canceled();
	        	dragOperation = null;
	        }
	        
	        if( movingImageWindow != null )
	            movingImageWindow.close();
	        
	        lastItem = null;
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
        DockableMovingImageFactory factory = controller.getProperties().get( DockTheme.DOCKABLE_MOVING_IMAGE_FACTORY );
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
        
        /** the dragged title (optional) */
        private DockTitle title;
        
        /** whether to force dragging anyway */
        private boolean forceDrag;
        
        /** whether a {@link MovingImage} can be shown */
        private boolean showMovingImage = true;
        
        /**
         * Creates a new remote
         * @param dockable the dockable which might be moved
	     * @param forceDrag if this flag is set to <code>true</code>, then dragging will always start even
	     * if one of the usual conditions is not met. I.e. dragging will start even if <code>dockable</code>
	     * does not have a parent of even if the parent does not allow dragging. This flag should be used
	     * with caution.
         */
        public DefaultRemoteRelocator( Dockable dockable, boolean forceDrag ){
            this.dockable = dockable;
            this.forceDrag = forceDrag;
        }
        
        public void setTitle( DockTitle title ){
			this.title = title;
		}
        
        public void setShowImageWindow( boolean imageWindow ){
        	showMovingImage = imageWindow;
        }
        
        public void cancel() {
            titleDragCancel();
            onMove = false;
        }

        public void drag( int x, int y, boolean always ) {
            dragMouseDragged( x, y, InputEvent.BUTTON1_DOWN_MASK, title, dockable, always, forceDrag, showMovingImage );
        }
        
        public Reaction drag( int x, int y, int modifiers ) {
            return dragMouseDragged( x, y, modifiers, title, dockable, false, forceDrag, showMovingImage );
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
            return dragMousePressed( x, y, dx, dy, modifiers, dockable, forceDrag );
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
        
        /** the last {@link MouseRepresentativeListener} that is registered in {@link #listeners}, and that has received a {@link MouseEvent} */
        private MouseRepresentativeListener lastActiveListener = null;
        
        public void representativeAdded( DockController controller,
                DockElementRepresentative representative ) {
         
            if( representative.getElement().asDockable() != null ){
                MouseRepresentativeListener listener = new MouseRepresentativeListener( this, representative );
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
                if( listener == lastActiveListener ){
                	lastActiveListener = null;
                }
            }
        }
        
        public void unsetLastActiveListener(){
        	lastActiveListener = null;
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
        
        /** the {@link Listener} that created <code>this</code> object */
        private Listener parent;
        
        /**
         * Creates a new listener
         * @param representative the element which will be observed
         */
        public MouseRepresentativeListener( Listener parent, DockElementRepresentative representative ){
        	this.parent = parent;
            this.representative = representative;
            
            if( representative instanceof DockTitle )
                title = (DockTitle)representative;
            
            dockable = representative.getElement().asDockable();
        }
        
        @Override
        public void mousePressed( MouseEvent e ){
            if( e.isConsumed() )
                return;
            if( !representative.isUsedAsTitle() && isDragOnlyTitle() )
                return;
            dragMousePressed( e, title, dockable );
            setLastActiveToThis();
        }
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( e.isConsumed() )
                return;
            if( !representative.isUsedAsTitle() && isDragOnlyTitle() )
                return;
            dragMouseReleased( e, title, dockable );
            setLastActiveToThis();
        }
        @Override
        public void mouseDragged( MouseEvent e ) {
            if( e.isConsumed() )
                return;
            if( !representative.isUsedAsTitle() && isDragOnlyTitle() )
                return;
            dragMouseDragged( e, title, dockable );
            setLastActiveToThis();
        }
        
        private void setLastActiveToThis(){
        	parent.lastActiveListener = this;
        }
    }
    
    /**
     * A global {@link MouseListener} that forwards the {@link #mouseReleased(MouseEvent)} to the last
     * active {@link MouseRepresentativeListener}, thus making sure that all drag and drop operations finish.
     * @author Benjamin Sigg
     */
    private class GlobalMouseReleaseListener extends MouseInputAdapter{
    	private boolean enabled = false;
    	private PropertyValue<Boolean> autoCancel = new PropertyValue<Boolean>( AUTO_DROP_ON_ANY_MOUSE_RELEASED_EVENT ) {
			@Override
			protected void valueChanged( Boolean oldValue, Boolean newValue ) {
				setEnabled( newValue );
			}
		};
    	
    	@Override
    	public void mouseReleased( MouseEvent e ) {
    		MouseRepresentativeListener lastActiveListener = listeners.lastActiveListener;
    		
    		if( isOnMove() && !isOnPut() && !e.isConsumed() && lastActiveListener != null ){
    			lastActiveListener.mouseReleased( e );
    		}
    	}
    	
    	public void link(){
    		autoCancel.setProperties( getController() );
    		setEnabled( autoCancel.getValue() );
    	}
    	
    	public void setEnabled( boolean enabled ){
    		if( this.enabled != enabled ){
    			this.enabled = enabled;

    			GlobalMouseDispatcher mouseDispatcher = getController().getGlobalMouseDispatcher();
    			if( enabled ){
    				mouseDispatcher.addMouseListener( this );
    			}
    			else{
    				mouseDispatcher.removeMouseListener( this );
    			}
    		}
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
            
            boolean transparency = Workarounds.getDefault().setTranslucent( this );
            
            image.bind( transparency );
            content.add( image.getComponent() );
            this.image = image;
        }
        
        /**
         * Gets the image that is shown on this window.
         * @return the image, may be <code>null</code>
         */
        public MovingImage getImage(){
			return image;
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
