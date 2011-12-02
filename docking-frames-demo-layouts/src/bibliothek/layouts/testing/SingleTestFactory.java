package bibliothek.layouts.testing;

import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;

public class SingleTestFactory implements SingleCDockableFactory{
    public SingleCDockable createBackup( String id ) {
        return new SingleTestDockable( id, true );
    }
}
