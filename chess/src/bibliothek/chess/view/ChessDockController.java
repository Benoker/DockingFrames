package bibliothek.chess.view;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DefaultDockRelocator;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.gui.dock.security.SecureDockControllerFactory;

/**
 * A controller which replaces its {@link DockRelocator} in order to start
 * the drag &amp; drop operation as soon as the mouse is pressed. The mouse
 * does not have to be dragged like the in the original controller.
 * @author Benjamin Sigg
 *
 */
public class ChessDockController extends SecureDockController {
    /**
     * Creates a new controller
     */
    public ChessDockController() {
        super( null );
        initiate( call("new SecureDockControllerFactory(){\n  @Override public DockRelocator createRelocator(  DockController controller){\n    return new DefaultDockRelocator(controller){\n{\n        setDragDistance(0);\n      }\n      @Override protected Reaction dragMousePressed(      int x,      int y,      int dx,      int dy,      int modifiers,      Dockable dockable){\n        Reaction reaction=super.dragMousePressed(x,y,dx,dy,modifiers,dockable);\n        if (reaction == Reaction.CONTINUE || reaction == Reaction.CONTINUE_CONSUMED) {\n          return createRemote(dockable).drag(x,y,modifiers);\n        }\n        return reaction;\n      }\n    }\n;\n  }\n}\n"));
    }
}
