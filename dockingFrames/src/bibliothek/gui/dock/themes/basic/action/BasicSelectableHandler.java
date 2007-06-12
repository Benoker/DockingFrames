package bibliothek.gui.dock.themes.basic.action;

import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A handler connecting a {@link SelectableDockAction} with a 
 * {@link BasicButtonModel}.
 * @author Benjamin Sigg
 *
 */
public abstract class BasicSelectableHandler extends BasicHandler<SelectableDockAction>{
    /**
     * An implementation used to connect a {@link SelectableDockAction} of
     * type {@link ActionType#CHECK} with a {@link BasicButtonModel}
     * @author Benjamin Sigg
     */
    public static class Check extends BasicSelectableHandler{
        /**
         * Creates a new handler
         * @param action the action which is observed by this handler
         * @param dockable the dockable for which the action is shown
         */
        public Check( SelectableDockAction action, Dockable dockable ) {
            super( action, dockable );
        }

        @Override
        public void triggered() {
            SelectableDockAction action = getAction();
            Dockable dockable = getDockable();
            action.setSelected( dockable, !action.isSelected( dockable ) );
        }
    }
    
    /**
     * An implementation used to connect a {@link SelectableDockAction} of
     * type {@link ActionType#RADIO} with a {@link BasicButtonModel}
     * @author Benjamin Sigg
     */
    public static class Radio extends BasicSelectableHandler{
        /**
         * Creates a new handler
         * @param action the action which is observed by this handler
         * @param dockable the dockable for which the action is shown
         */
        public Radio( SelectableDockAction action, Dockable dockable ) {
            super( action, dockable );
        }

        @Override
        public void triggered() {
            SelectableDockAction action = getAction();
            Dockable dockable = getDockable();
            action.setSelected( dockable, true );
        }
    }
    
    /** a listener added to the action to be informed when the <code>selected</code> property changes */
    private Listener listener = new Listener();
    
    /**
     * Creates a new handler
     * @param action the action which is observed by this handler
     * @param dockable the dockable for which the action is shown
     */
    public BasicSelectableHandler( SelectableDockAction action, Dockable dockable ) {
        super( action, dockable );
    }
    
    @Override
    public void bind() {
        super.bind();
        getAction().addSelectableListener( listener );
        getModel().setSelected( getAction().isSelected( getDockable() ) );
    }
    
    @Override
    public void unbind() {
        super.unbind();
        getAction().removeSelectableListener( listener );
    }
    
    /**
     * A listener to a {@link SelectableDockAction}, informing the model
     * whenever the <code>selected</code> state of the action changes.
     * @author Benjamin Sigg
     */
    private class Listener implements SelectableDockActionListener{
        public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( getDockable() ))
                getModel().setSelected( action.isSelected( getDockable() ) );
        }
    }
}
