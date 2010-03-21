package bibliothek.layouts;

import bibliothek.gui.dock.common.CControl;
import bibliothek.test.Inspection;

public class Application {
    public static void main( String[] args ) {
        Core core = new Core();
        core.show( null );
        
        CControl environment = core.getEnvironment().getEnvironmentControl();
        Inspection.open( environment );
    }
}
