package bibliothek.layouts.testing;

import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableBackupFactory;

public class SingleTestFactory implements SingleCDockableBackupFactory{
    public SingleCDockable createBackup( String id ) {
        return new SingleTestDockable( id, true );
    }
}
