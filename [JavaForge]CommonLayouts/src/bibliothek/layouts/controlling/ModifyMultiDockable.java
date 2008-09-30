package bibliothek.layouts.controlling;

import bibliothek.extension.gui.dock.preference.AbstractPreference;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.layouts.Core;
import bibliothek.layouts.Icons;

public class ModifyMultiDockable extends DefaultSingleCDockable{
    private static final PreferenceOperation ADD_MULTI_FACTORY = new PreferenceOperation( "add factory", Icons.get( "add factory" ), "CControl.add( String, MultipleCDockableFactory" );
    private static final PreferenceOperation REMOVE_MULTI_FACTORY = new PreferenceOperation( "remove factory", Icons.get( "remove factory" ), "CControl.removeSingleBackupFactory" );
    
    private static final PreferenceOperation ADD_MULTI_DOCKABLE = new PreferenceOperation( "add", Icons.get( "add dockable" ), "CControl.add( MultipleCDockable )" );
    
    private Core core;
    
    public ModifyMultiDockable( Core core ){
        super( "modify multi" );
        this.core = core;
        
        
    }
    
    private class Entry extends AbstractPreference<String>{
        private String id;
        
        public String getDescription() {
            return "Represents a MultipleCDockableFactory";
        }

        @Override
        public PreferenceOperation[] getOperations() {
            return new PreferenceOperation[]{
                    ADD_MULTI_DOCKABLE,
                    REMOVE_MULTI_FACTORY,
                    ADD_MULTI_FACTORY
            };
        }
        
        @Override
        public boolean isEnabled( PreferenceOperation operation ) {
            boolean present = core.getEnvironment().getEnvironmentControl().getMultipleDockableFactory( id ) != null;
            if( operation == ADD_MULTI_DOCKABLE )
                return present;
            if( operation == ADD_MULTI_FACTORY )
                return !present;
            if( operation == REMOVE_MULTI_FACTORY )
                return present;
            return false;
        }
        
        @Override
        public void doOperation( PreferenceOperation operation ) {
            not implemented yet
        }
        
        public String getLabel() {
            return "Factory: ";
        }

        public Path getPath() {
            return new Path( "factory." + id );
        }

        public Path getTypePath() {
            return Path.TYPE_LABEL;
        }

        public String getValue() {
            return id;
        }

        public Object getValueInfo() {
            return null;
        }

        public boolean isNatural() {
            return true;
        }

        public void setValue( String value ) {
            // ignore
        }
    }
}
