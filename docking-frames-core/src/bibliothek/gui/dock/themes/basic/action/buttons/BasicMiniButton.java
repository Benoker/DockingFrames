package bibliothek.gui.dock.themes.basic.action.buttons;

import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicResourceInitializer;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;

/**
 * A {@link MiniButton} using a {@link BasicButtonModel}.
 * @author Benjamin Sigg
 */
public class BasicMiniButton extends MiniButton<BasicButtonModel> {
    /**
     * Creates the new button.
     * @param trigger the callback that is invoked when the user clicks onto this button
     * @param initializer a strategy to lazily initialize resources
     */
    public BasicMiniButton( BasicTrigger trigger, BasicResourceInitializer initializer ) {
        super( null );
        
        BasicButtonModel model = new BasicButtonModel( this, trigger, initializer );
        
        setModel( model );
    }
}
