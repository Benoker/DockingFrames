package bibliothek.gui.dock.control;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * Adds a {@link MouseListener} to all {@link Dockable}s and {@link DockTitle}s
 * and informs the registered {@link DoubleClickObserver}s whenever the user
 * clicks twice on such an element.
 * @author Benjamin Sigg
 */
public class DoubleClickController {
    /** the list of all observers */
    private List<DoubleClickObserver> observers = new ArrayList<DoubleClickObserver>();
    
    /** A map that tells which listener was added to which {@link Dockable} */
    private Map<Dockable, DoubleClickListener> listeners = 
        new HashMap<Dockable, DoubleClickListener>();
    
    /**
     * Creates a new <code>DoubleClickController</code>.
     * @param controller the source of all {@link Dockable}s which have
     * to be observed.
     */
    public DoubleClickController( DockController controller ){
        controller.addDockControllerListener( new DockControllerAdapter(){
            @Override
            public void dockableRegistered( DockController controller, Dockable dockable ) {
                DoubleClickListener listener = new DoubleClickListener( dockable );
                dockable.addMouseInputListener( listener );
                listeners.put( dockable, listener );
                for( DockTitle title : dockable.listBoundTitles() )
                    title.addMouseInputListener( listener );
            }
            
            @Override
            public void dockableUnregistered( DockController controller, Dockable dockable ) {
                DoubleClickListener listener = listeners.remove( dockable );
                dockable.removeMouseInputListener( listener );
                for( DockTitle title : dockable.listBoundTitles() )
                    title.removeMouseInputListener( listener );
            }
            
            @Override
            public void titleBound( DockController controller, DockTitle title, Dockable dockable ) {
                title.addMouseInputListener( listeners.get( dockable ) );
            }
            
            @Override
            public void titleUnbound( DockController controller, DockTitle title, Dockable dockable ) {
                title.removeMouseInputListener( listeners.get( dockable ) );
            }
        });
    }
    
    /**
     * Adds an observer to this controller.
     * @param observer the new observer
     */
    public void addObserver( DoubleClickObserver observer ){
        observers.add( observer );
    }
    
    /**
     * Removes an observer from this controller.
     * @param observer the controller to remove
     */
    public void removeObserver( DoubleClickObserver observer ){
        observers.remove( observer );
    }
    
    /**
     * Fires an event to the {@link DoubleClickObserver}s whose location in the
     * tree is equal or below <code>dockable</code>. The order in which the
     * observers receive the event depends on their distance to the <code>dockable</code>.
     * @param dockable the dockable which was selected by the user
     * @param event the cause of the invocation, its click count must be 2.
     */
    public void send( Dockable dockable, MouseEvent event ){
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );
        
        if( event == null )
            throw new NullPointerException( "event must not be null" );
        
        if( event.getClickCount() != 2 )
            throw new IllegalArgumentException( "click count must be equal to 2" );
        
        List<DoubleClickObserver> list = affected( dockable );
        for( DoubleClickObserver observer : list ){
            if( observer.process( dockable, event )){
                event.consume();
                break;
            }
        }
    }
    
    /**
     * Creates a list of all {@link DoubleClickObserver} which are affected
     * by an event which occurs on <code>dockable</code>. The list is ordered
     * by the distance of the observers to <code>dockable</code>.
     * @param dockable the element which is the source of an event
     * @return the ordered list of observers
     */
    protected List<DoubleClickObserver> affected( Dockable dockable ){
        List<DoubleClickObserver> list = new LinkedList<DoubleClickObserver>();
        for( DoubleClickObserver observer : observers ){
            DockElement element = observer.getTreeLocation();
            if( element == null )
                list.add( observer );
            else if( DockUtilities.isAnchestor( element, dockable ))
                list.add( observer );
        }
        
        Collections.sort( list, new Comparator<DoubleClickObserver>(){
            public int compare( DoubleClickObserver o1, DoubleClickObserver o2 ) {
                DockElement a = o1.getTreeLocation();
                DockElement b = o2.getTreeLocation();
                
                if( a == b )
                    return 0;
                
                if( a == null )
                    return -1;
                
                if( b == null )
                    return 1;
                
                if( DockUtilities.isAnchestor( a, b ))
                    return -1;
                
                return 1;
            }
        });
        
        return list;
    }
    
    /**
     * A listener which waits for a double-click-event.
     * @author Benjamin Sigg
     */
    protected class DoubleClickListener extends MouseInputAdapter{
        /** The Dockable for which this listener is waiting */
        private Dockable dockable;
        
        /**
         * Constructs a new listener.
         * @param dockable the element that will become the source
         * of the forwarded event
         */
        public DoubleClickListener( Dockable dockable ){
            this.dockable = dockable;
        }
        
        @Override
        public void mousePressed( MouseEvent event ) {
            if( event.getClickCount() == 2 ){
                send( dockable, event );
            }
        }
    }
}
