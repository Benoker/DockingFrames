package bibliothek.gui.dock.themes.basic.action;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MenuDockAction;

/**
 * A handler connecting a {@link MenuDockAction} with a {@link BasicButtonModel}.
 * @author Benjamin Sigg
 *
 */
public class BasicMenuHandler extends BasicHandler<MenuDockAction> {
    /**
     * Creates a new handler
     * @param action the action which is observed by this handler
     * @param dockable the dockable for which the action is shown
     */
    public BasicMenuHandler( MenuDockAction action, Dockable dockable ) {
        super( action, dockable );
    }

    @Override
    public void triggered(){
        final DockActionSource source = getAction().getMenu( getDockable() );
        if( source != null && source.getDockActionCount() > 0 ){
            ActionPopup popup = new ActionPopup( false ){
                @Override
                protected Dockable getDockable() {
                    return BasicMenuHandler.this.getDockable();
                }

                @Override
                protected DockActionSource getSource() {
                    return source;
                }

                @Override
                protected boolean isEnabled() {
                    return true;
                }
            };
            
            JComponent component = getModel().getOwner();
            if( getModel().getOrientation().isHorizontal() )
                popup.popup( component, 0, component.getHeight() );
            else
                popup.popup( component, component.getWidth(), 0 );
        }
    }
}
