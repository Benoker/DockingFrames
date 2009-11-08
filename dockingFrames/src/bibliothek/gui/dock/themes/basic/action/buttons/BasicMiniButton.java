package bibliothek.gui.dock.themes.basic.action.buttons;

import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;

/**
 * A {@link MiniButton} using a {@link BasicButtonModel}.
 * @author Benjamin Sigg
 */
public class BasicMiniButton extends MiniButton<BasicButtonModel> {
    /**
     * Creates the new button.
     * @param trigger the callback that is invoked when the user clicks onto this button
     */
    public BasicMiniButton( BasicTrigger trigger ) {
        super( null );
        
        BasicButtonModel model = new BasicButtonModel( this, trigger );
        
        setModel( model );
    }
}
