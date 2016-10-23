package bibliothek.chess.view;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.ControllerSetupCollection;
import bibliothek.gui.dock.control.DefaultDockControllerFactory;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.control.relocator.DefaultDockRelocator;

/**
 * A controller which replaces its {@link DockRelocator} in order to start
 * the drag &amp; drop operation as soon as the mouse is pressed. The mouse
 * does not have to be dragged like the in the original controller.
 * @author Benjamin Sigg
 *
 */
public class ChessDockController extends DockController {
    /**
     * Creates a new controller
     */
    public ChessDockController() {
        super( null );
        initiate( new DefaultDockControllerFactory(){
            @Override
            public DockRelocator createRelocator( DockController controller,  ControllerSetupCollection setup ) {
                return createChessRelocator( controller, setup );
            }
        }, null );
        setRestrictedEnvironment( true );
    }
    
    /**
     * Creates the relocator that should be used by this controller.
     * @param controller <code>this</code>
     * @param setup a collection for listeners that get informed when the setup is complete
     * @return the new relocator
     */
    private DockRelocator createChessRelocator( DockController controller, ControllerSetupCollection setup ) {
        return new DefaultDockRelocator( controller, setup ){
            {
                setDragDistance( 0 );
            }
            
            @Override
            protected Reaction dragMousePressed( int x, int y, int dx, int dy, int modifiers, Dockable dockable, boolean forceDrag ) {
                Reaction reaction = super.dragMousePressed( x, y, dx, dy, modifiers, dockable, forceDrag );
                if( reaction == Reaction.CONTINUE || reaction == Reaction.CONTINUE_CONSUMED ){
                    return createRemote( dockable ).drag( x, y, modifiers );
                }
                
                return reaction;
            }
        };             
    }
}

